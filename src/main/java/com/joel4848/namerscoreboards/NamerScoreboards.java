package com.joel4848.namerscoreboards;

import com.joel4848.namerscoreboards.command.NamerScoreboardsCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.util.Identifier;

import static com.joel4848.namerscoreboards.registry.CardinalComponentsRegistry.NICK_STORAGE;

public class NamerScoreboards implements ModInitializer {
    public static final String MODID = "namerscoreboards";

    public static final NamerScoreboardsConfig CONFIG = NamerScoreboardsConfig.createAndLoad();

    @Override
    public void onInitialize() {
        NamerScoreboardsCommand.register();

        var allowNickFormatting = CONFIG.optionForKey(CONFIG.keys.allowNickFormatting);
        if (allowNickFormatting != null) {
            ServerLifecycleEvents.SERVER_STARTED.register(server -> allowNickFormatting.observe(o -> {
                var storage = NICK_STORAGE.getNullable(server.getScoreboard());
                if (storage == null) return;
                for (var player : server.getPlayerManager().getPlayerList()) storage.syncPlayerNick(player);
            }));
        }
    }

    public static Identifier id(String path) {
        return Identifier.of(MODID, path);
    }
}