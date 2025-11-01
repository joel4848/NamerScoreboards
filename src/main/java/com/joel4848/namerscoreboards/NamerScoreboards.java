package com.joel4848.namerscoreboards;

import com.joel4848.namerscoreboards.command.NamerScoreboardsCommand;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class NamerScoreboards implements ModInitializer {
    public static final String MODID = "namerscoreboards";

    public static final NamerScoreboardsConfig CONFIG = NamerScoreboardsConfig.load();

    @Override
    public void onInitialize() {
        NamerScoreboardsCommand.register();
    }

    public static Identifier id(String path) {
        return Identifier.of(MODID, path);
    }
}
