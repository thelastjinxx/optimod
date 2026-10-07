package com.example.optimod;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class OptiModConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("optimod.json");
    private static OptiModConfig instance = new OptiModConfig();

    // 1. Item culling + render cap
    public boolean cullFarItems = true;
    public int itemRenderDistance = 32;       // blocks
    public boolean itemCapEnabled = true;
    public int itemNearRadius = 8;            // items inside this radius never count toward the cap
    public int itemCap = 64;                  // max far items rendered per frame

    // 2. Player LOD
    public boolean playerLod = true;
    public int playerLodDistance = 48;        // blocks

    // 3. Animation throttling
    public boolean throttle = true;
    public int throttleDistance = 40;         // blocks
    public int throttleMs = 50;               // base refresh interval for far entities

    public static OptiModConfig get() { return instance; }

    public static void load() {
        try {
            if (Files.exists(FILE)) {
                OptiModConfig c = GSON.fromJson(Files.readString(FILE), OptiModConfig.class);
                if (c != null) instance = c;
            }
            save();
        } catch (Exception e) {
            System.err.println("[OptiMod] Failed to load config: " + e);
        }
    }

    public static void save() {
        try {
            Files.writeString(FILE, GSON.toJson(instance));
        } catch (IOException e) {
            System.err.println("[OptiMod] Failed to save config: " + e);
        }
    }
}
