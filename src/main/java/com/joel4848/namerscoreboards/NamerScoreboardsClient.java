package com.joel4848.namerscoreboards;

import com.joel4848.namerscoreboards.client.ClientConfigHolder;
import com.joel4848.namerscoreboards.fancymenu.FancyMenuCompat; // Imported the new compat class
import com.joel4848.namerscoreboards.network.ConfigSyncPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;

public class NamerScoreboardsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(ConfigSyncPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                ClientConfigHolder.setServerConfig(
                        payload.allowNickFormatting(),
                        payload.usePronounsEverywhere()
                );
            });
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            ClientConfigHolder.reset();
        });

        if (FabricLoader.getInstance().isModLoaded("fancymenu")) {
            FancyMenuCompat.registerFancyMenuIntegration();
        }
    }
}