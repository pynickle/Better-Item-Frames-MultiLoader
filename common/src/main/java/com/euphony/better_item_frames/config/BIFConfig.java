package com.euphony.better_item_frames.config;

import net.minecraft.client.Minecraft;

import java.io.IOException;
import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BIFConfig {
    private static BIFConfig instance;
    private static final String CONFIG_FILE_NAME = "better_item_frames-config.toml";
    private static final String SPLASH_POTION_RANGE_KEY = "splash_potion_range";
    private static final Pattern SPLASH_POTION_RANGE_PATTERN = Pattern.compile("^\\s*" + SPLASH_POTION_RANGE_KEY + "\\s*=\\s*\"([^\"]*)\"\\s*(?:#.*)?$");
    private static final Pattern SECTION_PATTERN = Pattern.compile("^\\s*\\[([^\\]]+)]\\s*(?:#.*)?$");

    private Path configPath;
    private SplashPotionRange splashPotionRange = SplashPotionRange.VANILLA;

    // 配置选项枚举
    public enum SplashPotionRange {
        VANILLA("vanilla"),
        HALF("half");

        private final String value;

        SplashPotionRange(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static SplashPotionRange fromString(String value) {
            for (SplashPotionRange range : values()) {
                if (range.getValue().equals(value)) {
                    return range;
                }
            }
            return VANILLA; // 默认值
        }
    }

    private BIFConfig() {
        load();
    }

    public static BIFConfig getInstance() {
        if (instance == null) {
            instance = new BIFConfig();
            instance.load();
        }
        return instance;
    }

    /**
     * 获取配置文件路径
     */
    private Path getConfigFile() {
        Path gameDir = Minecraft.getInstance().gameDirectory.toPath();
        Path configDir = gameDir.resolve("config");
        try {
            Files.createDirectories(configDir);
        } catch (IOException e) {
            System.err.println("Failed to create config directory: " + e.getMessage());
        }
        return configDir.resolve(CONFIG_FILE_NAME);
    }

    /**
     * 加载配置文件
     */
    public void load() {
        configPath = getConfigFile();

        // 创建默认配置文件（如果不存在）
        if (!Files.exists(configPath)) {
            createDefaultConfig(configPath);
            splashPotionRange = SplashPotionRange.VANILLA;
            return;
        }

        LoadedConfigState loadedConfigState = readConfigState(configPath);
        splashPotionRange = loadedConfigState.splashPotionRange();
        ensureDefaults();
        if (loadedConfigState.requiresNormalization()) {
            save();
        }
    }

    /**
     * 创建默认配置文件
     */
    private void createDefaultConfig(Path configPath) {
        try {
            String defaultConfig = """
                # Better Item Frames Mod Configuration File
                # This file uses TOML format and supports comments
                
                [gameplay]
                # Splash potion range configuration
                # Controls the range of splash potions when thrown
                # Available options:
                #   "vanilla" - Vanilla range: Use the original Minecraft splash potion range
                #   "half"    - Half range: Reduce the splash potion range to half of vanilla
                # Default value: "vanilla"
                splash_potion_range = "vanilla"
                """;

            Files.writeString(configPath, defaultConfig);

        } catch (IOException e) {
            System.err.println("Failed to create default BetterItemFrames config: " + e.getMessage());
        }
    }

    /**
     * 确保默认配置值存在
     */
    private void ensureDefaults() {
        if (splashPotionRange == null) {
            splashPotionRange = SplashPotionRange.VANILLA;
        }
    }

    /**
     * 获取喷溅药水范围
     */
    public SplashPotionRange getSplashPotionRange() {
        return splashPotionRange;
    }

    /**
     * 设置喷溅药水范围
     */
    public void setSplashPotionRange(SplashPotionRange range) {
        splashPotionRange = range != null ? range : SplashPotionRange.VANILLA;
        save();
    }

    /**
     * 重新加载配置文件
     */
    public void reload() {
        load();
    }

    /**
     * 手动保存配置文件
     */
    public void save() {
        if (configPath == null) {
            configPath = getConfigFile();
        }

        try {
            if (!Files.exists(configPath)) {
                createDefaultConfig(configPath);
            }

            List<String> existingLines = Files.readAllLines(configPath);
            Files.writeString(configPath, buildConfigContent(existingLines));
        } catch (IOException e) {
            System.err.println("Failed to save BetterItemFrames config: " + e.getMessage());
        }
    }

    /**
     * 关闭配置文件
     */
    public void close() {
    }

    private LoadedConfigState readConfigState(Path path) {
        try {
            List<String> lines = Files.readAllLines(path);
            boolean inGameplaySection = false;
            boolean hasGameplaySection = false;
            boolean hasSplashPotionRange = false;

            for (String line : lines) {
                String trimmedLine = line.trim();
                if (trimmedLine.isEmpty() || trimmedLine.startsWith("#")) {
                    continue;
                }

                Matcher sectionMatcher = SECTION_PATTERN.matcher(trimmedLine);
                if (sectionMatcher.matches()) {
                    inGameplaySection = "gameplay".equals(sectionMatcher.group(1).trim());
                    hasGameplaySection = hasGameplaySection || inGameplaySection;
                    continue;
                }

                if (!inGameplaySection) {
                    continue;
                }

                Matcher matcher = SPLASH_POTION_RANGE_PATTERN.matcher(line);
                if (matcher.matches()) {
                    hasSplashPotionRange = true;
                    SplashPotionRange parsedRange = SplashPotionRange.fromString(matcher.group(1));
                    boolean hasValidSplashPotionRange = parsedRange.getValue().equals(matcher.group(1));
                    return new LoadedConfigState(parsedRange, !hasGameplaySection || !hasValidSplashPotionRange);
                }
            }

            return new LoadedConfigState(SplashPotionRange.VANILLA, !hasGameplaySection || !hasSplashPotionRange);
        } catch (IOException e) {
            System.err.println("Failed to load BetterItemFrames config: " + e.getMessage());
        }

        return new LoadedConfigState(SplashPotionRange.VANILLA, false);
    }

    private String buildConfigContent(List<String> existingLines) {
        if (existingLines.isEmpty()) {
            return """
                    # Better Item Frames Mod Configuration File
                    # This file uses TOML format and supports comments
                    
                    [gameplay]
                    # Splash potion range configuration
                    # Controls the range of splash potions when thrown
                    # Available options:
                    #   \"vanilla\" - Vanilla range: Use the original Minecraft splash potion range
                    #   \"half\"    - Half range: Reduce the splash potion range to half of vanilla
                    # Default value: \"vanilla\"
                    splash_potion_range = "%s"
                    """.formatted(splashPotionRange.getValue());
        }

        List<String> updatedLines = new ArrayList<>();
        boolean inGameplaySection = false;
        boolean hasGameplaySection = false;
        boolean hasSplashPotionRange = false;

        for (String line : existingLines) {
            String trimmedLine = line.trim();
            Matcher sectionMatcher = SECTION_PATTERN.matcher(trimmedLine);
            boolean isSectionHeader = sectionMatcher.matches();

            if (isSectionHeader) {
                if (inGameplaySection && !hasSplashPotionRange) {
                    updatedLines.add(buildSplashPotionRangeLine());
                    hasSplashPotionRange = true;
                }

                inGameplaySection = "gameplay".equals(sectionMatcher.group(1).trim());
                hasGameplaySection = hasGameplaySection || inGameplaySection;
                updatedLines.add(line);
                continue;
            }

            if (inGameplaySection) {
                Matcher matcher = SPLASH_POTION_RANGE_PATTERN.matcher(line);
                if (matcher.matches()) {
                    updatedLines.add(buildSplashPotionRangeLine());
                    hasSplashPotionRange = true;
                    continue;
                }
            }

            updatedLines.add(line);
        }

        if (inGameplaySection && !hasSplashPotionRange) {
            updatedLines.add(buildSplashPotionRangeLine());
            hasSplashPotionRange = true;
        }

        if (!hasGameplaySection) {
            if (!updatedLines.isEmpty() && !updatedLines.get(updatedLines.size() - 1).isBlank()) {
                updatedLines.add("");
            }
            updatedLines.add("[gameplay]");
            updatedLines.add(buildSplashPotionRangeLine());
        }

        return String.join(System.lineSeparator(), updatedLines) + System.lineSeparator();
    }

    private String buildSplashPotionRangeLine() {
        return SPLASH_POTION_RANGE_KEY + " = \"" + splashPotionRange.getValue() + "\"";
    }

    private record LoadedConfigState(SplashPotionRange splashPotionRange, boolean requiresNormalization) {
    }
}
