package com.euphony.better_item_frames;

import com.euphony.better_item_frames.config.BIFConfig;

public final class BetterItemFrames {
    public static final String MOD_ID = "better_item_frames";

    public static void init() {
        BIFConfig.getInstance().load();
    }
}
