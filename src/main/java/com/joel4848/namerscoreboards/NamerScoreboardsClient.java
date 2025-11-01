package com.joel4848.namerscoreboards;

import com.joel4848.namerscoreboards.client.ChatNickClient;
import com.joel4848.namerscoreboards.client.ClientConfigHolder;
import com.joel4848.namerscoreboards.fancymenu.NamerScoreboardsNicknamePlaceholder;
import com.joel4848.namerscoreboards.network.ConfigSyncPayload;
import de.keksuccino.fancymenu.customization.placeholder.PlaceholderRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;

public class NamerScoreboardsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Don't register payload here - it's already registered in NamerScoreboards
        // Both client and server see that registration on integrated servers

        // Handle config sync packets from server
        ClientPlayNetworking.registerGlobalReceiver(ConfigSyncPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                ClientConfigHolder.setServerAllowNickFormatting(payload.allowNickFormatting());
            });
        });

        // Reset server config when disconnecting
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            ClientConfigHolder.reset();
        });

        // Register chat nickname replacement
        ChatNickClient.register();

        // Only register FancyMenu integration if FancyMenu is loaded
        if (FabricLoader.getInstance().isModLoaded("fancymenu")) {
            registerFancyMenuIntegration();
        }
    }

    private void registerFancyMenuIntegration() {
        PlaceholderRegistry.register(new NamerScoreboardsNicknamePlaceholder());
    }
}