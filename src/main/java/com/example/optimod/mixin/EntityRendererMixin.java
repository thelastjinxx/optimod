package com.example.optimod.mixin;

import com.example.optimod.EntityLod;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity> {
    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    private void optimod$hide(T entity, Frustum frustum, double camX, double camY, double camZ,
                              CallbackInfoReturnable<Boolean> cir) {
        if (EntityLod.shouldHide(entity, camX, camY, camZ)) cir.setReturnValue(false);
    }
}
