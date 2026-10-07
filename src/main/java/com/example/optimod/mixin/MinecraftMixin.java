package com.example.optimod.mixin;

import com.example.optimod.EntityLod;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Inject(method = "runTick", at = @At("HEAD"))
    private void optimod$newFrame(boolean renderLevel, CallbackInfo ci) {
        EntityLod.newFrame();
    }
}
