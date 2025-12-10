package com.euphony.better_item_frames.utils;

import com.euphony.better_item_frames.api.ICustomItemFrame;
import com.euphony.better_item_frames.config.BIFConfig;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.projectile.throwableitemprojectile.AbstractThrownPotion;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class Utils {
    public static void handleInvisibility(AbstractThrownPotion thrownPotion, Iterable<MobEffectInstance> effects) {
        effects.forEach(
        effect -> {
            if (effect.is(MobEffects.INVISIBILITY)) {
                setInvisibility(thrownPotion, true);
            }
        });
    }

    public static void setInvisibility(AbstractThrownPotion thrownPotion, boolean invisibility) {
        AABB checkBox = getBoundingBox(thrownPotion);
        List<ItemFrame> itemFrames = thrownPotion.level().getEntitiesOfClass(ItemFrame.class, checkBox);
        for (ItemFrame frame : itemFrames) {
            ICustomItemFrame itemFrame = (ICustomItemFrame) frame;
            if (invisibility != itemFrame.better_item_frames$getIsInvisible())
                itemFrame.better_item_frames$setIsInvisible(invisibility);
        }
    }

    private static AABB getBoundingBox(AbstractThrownPotion thrownPotion) {
        if(BIFConfig.getInstance().getSplashPotionRange() == BIFConfig.SplashPotionRange.HALF) {
            return thrownPotion.getBoundingBox().inflate(2.0D, 1.0D, 2.0D);
        } else {
            return thrownPotion.getBoundingBox().inflate(4.0D, 2.0D, 4.0D);
        }
    }
}
