package com.example.optimod.mixin;

import com.example.optimod.EntityLod;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {
    @Inject(method = "extractEntity", at = @At("HEAD"), cancellable = true)
    private <E extends Entity> void optimod$reuse(E entity, float partialTick,
                                                  CallbackInfoReturnable<EntityRenderState> cir) {
        EntityRenderState cached = EntityLod.reuse(entity);
        if (cached != null) cir.setReturnValue(cached);
    }

    @Inject(method = "extractEntity", at = @At("RETURN"))
    private <E extends Entity> void optimod$after(E entity, float partialTick,
                                                  CallbackInfoReturnable<EntityRenderState> cir) {
        EntityLod.afterExtract(entity, cir.getReturnValue());
    }
}
