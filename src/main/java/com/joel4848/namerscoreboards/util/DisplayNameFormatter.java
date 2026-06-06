package com.joel4848.namerscoreboards.util;

import com.joel4848.namerscoreboards.NamerScoreboards;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class DisplayNameFormatter {

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

    @Nullable
    public static Text combineNickAndPronounsConditional(@Nullable Text nick, @Nullable String pronouns, boolean includePronouns) {
        boolean shouldIncludePronouns = includePronouns || NamerScoreboards.CONFIG.usePronounsEverywhere();
        if (!shouldIncludePronouns) {
            // Pronouns suppressed — just return the nickname alone
            if (nick == null) return null;
            return nick;
        }
        return combineNickAndPronouns(nick, pronouns);
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    @Nullable
    public static Text combineNickAndPronounsConditionalClient(@Nullable Text nick, @Nullable String pronouns, boolean includePronouns) {
        boolean usePronounsEverywhere = com.joel4848.namerscoreboards.client.ClientConfigHolder.getServerUsePronounsEverywhere();
        boolean shouldIncludePronouns = includePronouns || usePronounsEverywhere;
        if (!shouldIncludePronouns) {
            if (nick == null) return null;
            return nick;
        }
        return combineNickAndPronouns(nick, pronouns);
    }
}