package com.euphony.better_item_frames.mixin;

import com.euphony.better_item_frames.utils.Utils;
import net.minecraft.world.entity.projectile.AbstractThrownPotion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractThrownPotion.class)
public class AbstractThrownPotionMixin {
    @Inject(method = "onHitAsWater", at = @At("HEAD"))
    private void applyWater(CallbackInfo ci) {
        Utils.setInvisibility((AbstractThrownPotion) (Object) this, false);
    }
}