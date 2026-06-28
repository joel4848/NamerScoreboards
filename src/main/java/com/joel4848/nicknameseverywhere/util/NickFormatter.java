package com.joel4848.nicknameseverywhere.util;

import com.joel4848.nicknameseverywhere.NicknamesEverywhere;
import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.parsers.NodeParser;
import eu.pb4.placeholders.api.parsers.TagParser;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

public class NickFormatter {
    private static final NodeParser NODE_PARSER = TagParser.QUICK_TEXT_WITH_STF_SAFE;

    public static Text parseNick(@Nullable String nick) {
        if (nick == null || nick.isBlank()) return Text.empty();

        boolean allowFormatting = shouldAllowFormatting();

        if (!allowFormatting) return Text.literal(nick);
        return NODE_PARSER.parseText(nick, ParserContext.of());
    }

    /**
     * Parses the pronoun string to support gradient and style tags,
     * matching the behavior of nicknames.
     */
    public static Text parsePronouns(@Nullable String pronouns) {
        if (pronouns == null || pronouns.isBlank()) return Text.empty();

        boolean allowFormatting = shouldAllowFormatting();

        if (!allowFormatting) return Text.literal(pronouns);
        return NODE_PARSER.parseText(pronouns, ParserContext.of());
    }

    private static boolean shouldAllowFormatting() {
        EnvType env = FabricLoader.getInstance().getEnvironmentType();

        if (env == EnvType.CLIENT) {
            return shouldAllowFormattingClient();
        } else {
            return NicknamesEverywhere.CONFIG.allowNickFormatting();
        }
    }

    @Environment(EnvType.CLIENT)
    private static boolean shouldAllowFormattingClient() {
        if (com.joel4848.nicknameseverywhere.client.ClientConfigHolder.hasReceivedServerConfig()) {
            return com.joel4848.nicknameseverywhere.client.ClientConfigHolder.getServerAllowNickFormatting();
        }
        return NicknamesEverywhere.CONFIG.allowNickFormatting();
    }

    public static Text nickAndName(@Nullable Text nick, Text name) {
        if (nick == null) return name;
        return Text.empty().append(nick).append(" (").append(name).append(")");
    }

    /**
     * Formats a player's display for the player list (tab list)
     * @param nickname The player's parsed nickname component (can be null)
     * @param rawPronouns The player's raw pronouns string (can be null)
     * @param username The player's actual username
     * @return Formatted text for display
     */
    public static Text formatPlayerListName(@Nullable Text nickname, @Nullable String rawPronouns, String username) {
        boolean hasNickname = nickname != null;
        boolean hasPronouns = rawPronouns != null && !rawPronouns.isBlank();

        // Case 1: No nickname or pronouns - default vanilla layout
        if (!hasNickname && !hasPronouns) {
            return null;
        }

        // Parse the pronouns safely to preserve gradient tags if they exist
        Text parsedPronouns = hasPronouns ? parsePronouns(rawPronouns) : null;

        // Case 2: Pronouns only - username + pronouns (inherits natural coloring instead of forced white)
        if (!hasNickname && hasPronouns) {
            return Text.literal(username).append(" ").append(parsedPronouns);
        }

        // Case 3 & 4: Nickname present (with or without pronouns)
        Text result = Text.empty().append(nickname);

        if (hasPronouns) {
            result = Text.empty().append(result).append(" ").append(parsedPronouns);
        }

        // Append username explicitly wrapped in its own separate gray styling
        result = Text.empty()
                .append(result)
                .append(" ")
                .append(Text.literal("(" + username + ")").formatted(Formatting.GRAY));

        return result;
    }
}