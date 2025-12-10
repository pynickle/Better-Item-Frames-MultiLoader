package com.euphony.better_item_frames.config;

import com.electronwill.nightconfig.core.file.FileConfig;
import net.minecraft.client.Minecraft;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Better Item Frames 模组配置管理器
 * 使用 Minecraft 自带的 night-config 库处理 TOML 格式配置文件
 */
public class BIFConfig {
    private static BIFConfig instance;
    private static final String CONFIG_FILE_NAME = "better_item_frames-config.toml";

    private FileConfig config;

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
        Path configPath = getConfigFile();

        // 创建默认配置文件（如果不存在）
        if (!Files.exists(configPath)) {
            createDefaultConfig(configPath);
        }

        // 使用night-config加载配置文件
        config = FileConfig.builder(configPath).build();
        config.load();

        // 确保所有必需的配置项都存在
        ensureDefaults();
        config.save();
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
        // 检查并设置默认的喷溅药水范围
        if (!config.contains("gameplay.splash_potion_range")) {
            config.set("gameplay.splash_potion_range", SplashPotionRange.VANILLA.getValue());
        }
    }

    /**
     * 获取喷溅药水范围
     */
    public SplashPotionRange getSplashPotionRange() {
        String value = config.get("gameplay.splash_potion_range");
        return SplashPotionRange.fromString(value != null ? value : SplashPotionRange.VANILLA.getValue());
    }

    /**
     * 设置喷溅药水范围
     */
    public void setSplashPotionRange(SplashPotionRange range) {
        config.set("gameplay.splash_potion_range", range.getValue());
        config.save();
    }

    /**
     * 重新加载配置文件
     */
    public void reload() {
        if (config != null) {
            config.load();
        }
    }

    /**
     * 手动保存配置文件
     */
    public void save() {
        if (config != null) {
            config.save();
        }
    }

    /**
     * 关闭配置文件
     */
    public void close() {
        if (config != null) {
            config.close();
        }
    }
}
