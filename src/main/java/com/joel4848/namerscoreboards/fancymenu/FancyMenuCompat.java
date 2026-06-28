package com.joel4848.namerscoreboards.fancymenu;

import de.keksuccino.fancymenu.customization.placeholder.PlaceholderRegistry;

public class FancyMenuCompat {

    public static void registerFancyMenuIntegration() {
        PlaceholderRegistry.register(new NamerScoreboardsNicknamePlaceholder());
    }
}