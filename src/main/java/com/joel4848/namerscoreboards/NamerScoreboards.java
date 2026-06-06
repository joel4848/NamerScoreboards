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
        PayloadTypeRegistry.playS2C().register(ConfigSyncPayload.ID, ConfigSyncPayload.CODEC);

        NamerScoreboardsCommand.register();

        ServerPlayConnectionEvents.INIT.register((handler, server) -> {
            server.execute(() -> {
                if (handler.player != null) {
                    sendConfigToClient(handler.player);
                }
            });
        });
    }

    public static void sendConfigToClient(net.minecraft.server.network.ServerPlayerEntity player) {
        if (ServerPlayNetworking.canSend(player, ConfigSyncPayload.ID)) {
            ServerPlayNetworking.send(player, new ConfigSyncPayload(
                    CONFIG.allowNickFormatting(),
                    CONFIG.usePronounsEverywhere()
            ));
        }
    }

    public static void broadcastConfigToAllClients(net.minecraft.server.MinecraftServer server) {
        ConfigSyncPayload payload = new ConfigSyncPayload(
                CONFIG.allowNickFormatting(),
                CONFIG.usePronounsEverywhere()
        );
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