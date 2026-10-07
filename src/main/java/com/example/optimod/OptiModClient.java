package com.example.optimod;

import net.fabricmc.api.ClientModInitializer;

public class OptiModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        OptiModConfig.load();
    }
}
