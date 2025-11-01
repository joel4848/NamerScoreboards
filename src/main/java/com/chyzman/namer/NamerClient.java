package com.chyzman.namer;

import com.chyzman.namer.fancymenu.NamerNicknamePlaceholder;
import de.keksuccino.fancymenu.customization.placeholder.PlaceholderRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class NamerClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Only register FancyMenu integration if FancyMenu is loaded
        if (FabricLoader.getInstance().isModLoaded("fancymenu")) {
            registerFancyMenuIntegration();
        }
    }

    private void registerFancyMenuIntegration() {
        PlaceholderRegistry.register(new NamerNicknamePlaceholder());
    }
}