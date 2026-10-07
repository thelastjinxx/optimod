package com.example.optimod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/** All the LOD logic lives here so the mixins stay tiny. */
public final class EntityLod {
    private record Cached(EntityRenderState state, long timeNs) {}

    private static final Map<Integer, Cached> CACHE = new HashMap<>();
    private static int farItemsThisFrame = 0;
    private static int storesSincePurge = 0;

    private EntityLod() {}

    /** Called once per frame (from Minecraft.runTick). */
    public static void newFrame() {
        farItemsThisFrame = 0;
    }

    // ---------- Items: distance cull + per-frame cap ----------
    /** Returns true if this entity should NOT be rendered. */
    public static boolean shouldHide(Entity entity, double camX, double camY, double camZ) {
        if (!(entity instanceof ItemEntity)) return false;
        OptiModConfig c = OptiModConfig.get();
        double distSq = entity.distanceToSqr(camX, camY, camZ);

        if (c.cullFarItems) {
            double max = c.itemRenderDistance;
            if (distSq > max * max) return true;
        }
        if (c.itemCapEnabled) {
            double near = c.itemNearRadius;
            if (distSq > near * near) {
                if (++farItemsThisFrame > c.itemCap) return true;
            }
        }
        return false;
    }

    // ---------- Animation throttling: reuse a recent render state ----------
    public static EntityRenderState reuse(Entity entity) {
        OptiModConfig c = OptiModConfig.get();
        if (!c.throttle || !isThrottleCandidate(entity)) return null;
        double dist = distanceToCamera(entity);
        if (dist < c.throttleDistance) return null;

        Cached cached = CACHE.get(entity.getId());
        if (cached == null) return null;

        double scale = Math.min(3.0, dist / c.throttleDistance);
        long intervalNs = (long) (c.throttleMs * scale * 1_000_000L);
        if (System.nanoTime() - cached.timeNs() < intervalNs) return cached.state();
        return null;
    }

    // ---------- After extraction: apply player LOD, then cache ----------
    public static void afterExtract(Entity entity, EntityRenderState state) {
        if (state == null) return;
        OptiModConfig c = OptiModConfig.get();
        double dist = distanceToCamera(entity);

        if (c.playerLod && entity instanceof Player && dist > c.playerLodDistance) {
            applyPlayerLod(state);
        }

        if (c.throttle && isThrottleCandidate(entity) && dist >= c.throttleDistance) {
            CACHE.put(entity.getId(), new Cached(state, System.nanoTime()));
            if (++storesSincePurge > 300) purge();
        }
    }

    private static void applyPlayerLod(EntityRenderState state) {
        state.nameTag = null; // FRAGILE: field name/type may differ in 1.21.11
        if (state instanceof HumanoidRenderState h) {
            // FRAGILE: equipment field names
            h.headEquipment = ItemStack.EMPTY;
            h.chestEquipment = ItemStack.EMPTY;
            h.legsEquipment = ItemStack.EMPTY;
            h.feetEquipment = ItemStack.EMPTY;
        }
        if (state instanceof ArmedEntityRenderState a) {
            // FRAGILE: held item fields
            a.rightHandItemState.clear();
            a.leftHandItemState.clear();
        }
    }

    private static boolean isThrottleCandidate(Entity entity) {
        Minecraft mc = Minecraft.getInstance();
        return entity instanceof LivingEntity && entity != mc.getCameraEntity() && entity != mc.player;
    }

    private static double distanceToCamera(Entity entity) {
        Entity cam = Minecraft.getInstance().getCameraEntity();
        return cam == null ? 0 : Math.sqrt(entity.distanceToSqr(cam));
    }

    private static void purge() {
        storesSincePurge = 0;
        long cutoff = System.nanoTime() - 1_000_000_000L;
        Iterator<Cached> it = CACHE.values().iterator();
        while (it.hasNext()) {
            if (it.next().timeNs() < cutoff) it.remove();
        }
    }
}
