package com.joel4848.namerscoreboards.util;

import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class DisplayNameFormatter {

    /**
     * Combines nickname and pronouns for display.
     * If both exist: "nickname pronouns"
     * If only nickname: "nickname"
     * If only pronouns: "pronouns"
     * If neither: returns null
     */
    @Nullable
    public static Text combineNickAndPronouns(@Nullable Text nick, @Nullable String pronouns) {
        if (nick == null && (pronouns == null || pronouns.isBlank())) {
            return null;
        }

        if (nick == null) {
            return Text.literal(pronouns);
        }

        if (pronouns == null || pronouns.isBlank()) {
            return nick;
        }

        return Text.empty().append(nick).append(" ").append(pronouns);
    }
}