package com.euphony.better_item_frames.fabric;

import com.euphony.better_item_frames.BetterItemFrames;
import net.fabricmc.api.ModInitializer;

public final class BetterItemFramesFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        BetterItemFrames.init();
    }
}
