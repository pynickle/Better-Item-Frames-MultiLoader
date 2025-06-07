package com.euphony.better_item_frames.utils;

import com.euphony.better_item_frames.api.ICustomItemFrame;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.projectile.AbstractThrownPotion;
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
        AABB checkBox = thrownPotion.getBoundingBox().inflate(2.0D, 1.0D, 2.0D);
        List<ItemFrame> itemFrames = thrownPotion.level().getEntitiesOfClass(ItemFrame.class, checkBox);
        for (ItemFrame frame : itemFrames) {
            ICustomItemFrame itemFrame = (ICustomItemFrame) frame;
            if (invisibility != itemFrame.better_item_frames$getIsInvisible())
                itemFrame.better_item_frames$setIsInvisible(invisibility);
        }
    }
}
