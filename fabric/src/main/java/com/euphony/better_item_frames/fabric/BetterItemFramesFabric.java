package com.euphony.better_item_frames.fabric;

import com.euphony.better_item_frames.BetterItemFrames;
import net.fabricmc.api.ModInitializer;

public final class BetterItemFramesFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        BetterItemFrames.init();
    }
}
