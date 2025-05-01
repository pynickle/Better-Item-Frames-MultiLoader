package com.euphony.better_item_frames.mixin;

import com.euphony.better_item_frames.api.ICustomItemFrame;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemFrame.class)
public abstract class ItemFrameMixin extends HangingEntity implements ICustomItemFrame {
    @Shadow public abstract ItemStack getItem();

    @Shadow public abstract void setItem(ItemStack stack);

    @Unique
    private boolean better_item_frames$isInvisible;

    protected ItemFrameMixin(EntityType<? extends HangingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "setItem(Lnet/minecraft/world/item/ItemStack;Z)V", at = @At("TAIL"))
    private void setHeldItem(ItemStack value, boolean update, CallbackInfo ci) {
        if (this.better_item_frames$isInvisible)
            ((ItemFrame) (Object) this).setInvisible(true);
        else
            ((ItemFrame) (Object) this).setInvisible(false);
    }

    @Inject(method = "removeFramedMap", at = @At("TAIL"))
    private void removeFromFrameMixin(ItemStack stack, CallbackInfo ci) {
        ((ItemFrame) (Object) this).setInvisible(false);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void addAdditionalSaveDataInject(CompoundTag nbt, CallbackInfo ci) {
        nbt.putBoolean("isInvisible", this.better_item_frames$isInvisible);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readAdditionalSaveDataInject(CompoundTag nbt, CallbackInfo ci) {
        if (nbt.contains("isInvisible")) {
            this.better_item_frames$isInvisible = nbt.getBoolean("isInvisible");
        }
    }

    @Override
    public boolean better_item_frames$getIsInvisible() {
        return better_item_frames$isInvisible;
    }

    @Override
    public void better_item_frames$setIsInvisible(boolean isInvisible) {
        this.better_item_frames$isInvisible = isInvisible;
        if(!this.getItem().isEmpty()) {
            setItem(this.getItem());
        }
    }
}