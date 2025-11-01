package com.joel4848.namerscoreboards;

import com.joel4848.namerscoreboards.fancymenu.NamerScoreboardsNicknamePlaceholder;
import de.keksuccino.fancymenu.customization.placeholder.PlaceholderRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class NamerScoreboardsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Only register FancyMenu integration if FancyMenu is loaded
        if (FabricLoader.getInstance().isModLoaded("fancymenu")) {
            registerFancyMenuIntegration();
        }
    }

    private void registerFancyMenuIntegration() {
        PlaceholderRegistry.register(new NamerScoreboardsNicknamePlaceholder());
    }
}