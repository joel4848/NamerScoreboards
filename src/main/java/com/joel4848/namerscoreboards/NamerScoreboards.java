package com.joel4848.namerscoreboards;

import com.joel4848.namerscoreboards.command.NamerScoreboardsCommand;
import com.joel4848.namerscoreboards.network.ConfigSyncPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public class NamerScoreboards implements ModInitializer {
    public static final String MODID = "namerscoreboards";

    public static final NamerScoreboardsConfig CONFIG = NamerScoreboardsConfig.load();

    @Override
    public void onInitialize() {
        // Register payload type for server->client communication
        PayloadTypeRegistry.playS2C().register(ConfigSyncPayload.ID, ConfigSyncPayload.CODEC);

        NamerScoreboardsCommand.register();

        // Send config to clients when they join - using INIT phase which happens after handshake
        ServerPlayConnectionEvents.INIT.register((handler, server) -> {
            // Schedule for next tick to ensure connection is fully ready
            server.execute(() -> {
                if (handler.player != null) {
                    sendConfigToClient(handler.player);
                }
            });
        });
    }

    public static void sendConfigToClient(net.minecraft.server.network.ServerPlayerEntity player) {
        if (ServerPlayNetworking.canSend(player, ConfigSyncPayload.ID)) {
            ServerPlayNetworking.send(player, new ConfigSyncPayload(CONFIG.allowNickFormatting()));
        }
    }

    public static void broadcastConfigToAllClients(net.minecraft.server.MinecraftServer server) {
        ConfigSyncPayload payload = new ConfigSyncPayload(CONFIG.allowNickFormatting());
        for (var player : server.getPlayerManager().getPlayerList()) {
            if (ServerPlayNetworking.canSend(player, ConfigSyncPayload.ID)) {
                ServerPlayNetworking.send(player, payload);
            }
        }
    }

    public static Identifier id(String path) {
        return Identifier.of(MODID, path);
    }
}