package com.joel4848.namerscoreboards.util;

import com.joel4848.namerscoreboards.NamerScoreboards;
import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.parsers.NodeParser;
import eu.pb4.placeholders.api.parsers.TagParser;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class NickFormatter {
    private static final NodeParser NODE_PARSER = TagParser.QUICK_TEXT_WITH_STF_SAFE;

    public static Text parseNick(@Nullable String nick) {
        if (nick == null || nick.isBlank()) return Text.empty();

        // Check if we should allow formatting
        boolean allowFormatting = shouldAllowFormatting();

        if (!allowFormatting) return Text.literal(nick);
        return NODE_PARSER.parseText(nick, ParserContext.of());
    }

    private static boolean shouldAllowFormatting() {
        // Check which environment we're in
        EnvType env = FabricLoader.getInstance().getEnvironmentType();

        if (env == EnvType.CLIENT) {
            // On client, use the server's setting if we have it
            return shouldAllowFormattingClient();
        } else {
            // On dedicated server, use server config
            return NamerScoreboards.CONFIG.allowNickFormatting();
        }
    }

    @Environment(EnvType.CLIENT)
    private static boolean shouldAllowFormattingClient() {
        // Import here to avoid loading client classes on server
        com.joel4848.namerscoreboards.client.ClientConfigHolder holder;

        // Use server's setting if we've received it, otherwise fall back to local config
        if (com.joel4848.namerscoreboards.client.ClientConfigHolder.hasReceivedServerConfig()) {
            return com.joel4848.namerscoreboards.client.ClientConfigHolder.getServerAllowNickFormatting();
        }
        return NamerScoreboards.CONFIG.allowNickFormatting();
    }

    public static Text nickAndName(@Nullable Text nick, Text name) {
        if (nick == null) return name;
        return Text.empty().append(nick).append(" (").append(name).append(")");
    }
}