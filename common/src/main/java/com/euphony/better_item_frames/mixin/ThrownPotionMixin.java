package com.euphony.better_item_frames.mixin;

import com.euphony.better_item_frames.api.ICustomItemFrame;
import com.euphony.better_item_frames.config.BIFConfig;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ThrownPotion.class)
public class ThrownPotionMixin {
    @Inject(method = "applySplash(Ljava/lang/Iterable;Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"))
    public void applySplash(Iterable<MobEffectInstance> effectInstanceList, @Nullable Entity entity, CallbackInfo ci) {
        ThrownPotion potion = (ThrownPotion) (Object) this;
        better_item_frames$handleSplash(effectInstanceList, potion);
    }

    @Inject(method = "applyWater()V", at = @At("HEAD"))
    private void applyWater(CallbackInfo ci) {
        ThrownPotion potion = (ThrownPotion) (Object) this;
        better_item_frames$handleWater(potion);
    }

    @Unique
    private static void better_item_frames$handleSplash(Iterable<MobEffectInstance> effectInstanceList, ThrownPotion thrownPotion) {
        AABB checkBox = better_item_frames$getBoundingBox(thrownPotion);
        effectInstanceList.forEach(instance -> {
            if (instance.getEffect() == MobEffects.INVISIBILITY) {
                List<ItemFrame> itemFrames = thrownPotion.level().getEntitiesOfClass(ItemFrame.class, checkBox);
                for (ItemFrame frame : itemFrames) {
                    ICustomItemFrame itemFrame = (ICustomItemFrame) frame;
                    if (!itemFrame.better_item_frames$getIsInvisible())
                        itemFrame.better_item_frames$setIsInvisible(true);
                }
            }
        });
    }

    @Unique
    private static void better_item_frames$handleWater(ThrownPotion thrownPotion) {
        AABB checkBox = better_item_frames$getBoundingBox(thrownPotion);
        List<ItemFrame> itemFrames = thrownPotion.level().getEntitiesOfClass(ItemFrame.class, checkBox);
        for (ItemFrame frame : itemFrames) {
            ICustomItemFrame itemFrame = (ICustomItemFrame) frame;
            if (itemFrame.better_item_frames$getIsInvisible())
                itemFrame.better_item_frames$setIsInvisible(false);
        }
    }

    @Unique
    private static AABB better_item_frames$getBoundingBox(ThrownPotion thrownPotion) {
        if(BIFConfig.getInstance().getSplashPotionRange() == BIFConfig.SplashPotionRange.HALF) {
            return thrownPotion.getBoundingBox().inflate(2.0D, 1.0D, 2.0D);
        } else {
            return thrownPotion.getBoundingBox().inflate(4.0D, 2.0D, 4.0D);
        }
    }
}