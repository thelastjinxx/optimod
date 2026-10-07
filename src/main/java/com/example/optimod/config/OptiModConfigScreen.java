package com.example.optimod.config;

import com.example.optimod.OptiModConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class OptiModConfigScreen {
    private OptiModConfigScreen() {}

    public static Screen create(Screen parent) {
        OptiModConfig c = OptiModConfig.get();
        ConfigBuilder b = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.literal("OptiMod"))
                .setSavingRunnable(OptiModConfig::save);
        ConfigEntryBuilder e = b.entryBuilder();

        ConfigCategory items = b.getOrCreateCategory(Component.literal("Items"));
        items.addEntry(e.startBooleanToggle(Component.literal("Cull far items"), c.cullFarItems)
                .setDefaultValue(true).setSaveConsumer(v -> c.cullFarItems = v).build());
        items.addEntry(e.startIntSlider(Component.literal("Item render distance"), c.itemRenderDistance, 8, 128)
                .setDefaultValue(32).setSaveConsumer(v -> c.itemRenderDistance = v).build());
        items.addEntry(e.startBooleanToggle(Component.literal("Limit items per frame"), c.itemCapEnabled)
                .setDefaultValue(true).setSaveConsumer(v -> c.itemCapEnabled = v).build());
        items.addEntry(e.startIntSlider(Component.literal("Always-render radius"), c.itemNearRadius, 2, 32)
                .setDefaultValue(8).setSaveConsumer(v -> c.itemNearRadius = v).build());
        items.addEntry(e.startIntSlider(Component.literal("Max far items per frame"), c.itemCap, 8, 512)
                .setDefaultValue(64).setSaveConsumer(v -> c.itemCap = v).build());

        ConfigCategory players = b.getOrCreateCategory(Component.literal("Players"));
        players.addEntry(e.startBooleanToggle(Component.literal("Player LOD"), c.playerLod)
                .setDefaultValue(true).setSaveConsumer(v -> c.playerLod = v).build());
        players.addEntry(e.startIntSlider(Component.literal("Player LOD distance"), c.playerLodDistance, 16, 128)
                .setDefaultValue(48).setSaveConsumer(v -> c.playerLodDistance = v).build());

        ConfigCategory anim = b.getOrCreateCategory(Component.literal("Animation"));
        anim.addEntry(e.startBooleanToggle(Component.literal("Throttle far entities"), c.throttle)
                .setDefaultValue(true).setSaveConsumer(v -> c.throttle = v).build());
        anim.addEntry(e.startIntSlider(Component.literal("Throttle distance"), c.throttleDistance, 16, 128)
                .setDefaultValue(40).setSaveConsumer(v -> c.throttleDistance = v).build());
        anim.addEntry(e.startIntSlider(Component.literal("Refresh interval (ms)"), c.throttleMs, 16, 200)
                .setDefaultValue(50).setSaveConsumer(v -> c.throttleMs = v).build());

        return b.build();
    }
}
