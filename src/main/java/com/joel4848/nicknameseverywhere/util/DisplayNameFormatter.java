package com.joel4848.nicknameseverywhere.util;

import com.joel4848.nicknameseverywhere.NicknamesEverywhere;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class DisplayNameFormatter {

    @Nullable
    public static Text combineNickAndPronouns(@Nullable Text nick, @Nullable String pronouns) {
        if (nick == null && (pronouns == null || pronouns.isBlank())) {
            return null;
        }

        // Safely parse the pronouns using NickFormatter to process style & gradient tags
        Text parsedPronouns = (pronouns != null && !pronouns.isBlank())
                ? NickFormatter.parsePronouns(pronouns)
                : Text.empty();

        if (nick == null) {
            return parsedPronouns;
        }
        if (pronouns == null || pronouns.isBlank()) {
            return nick;
        }

        // Append the pre-parsed pronoun text component instead of the raw string
        return Text.empty().append(nick).append(" ").append(parsedPronouns);
    }

    @Nullable
    public static Text combineNickAndPronounsConditional(@Nullable Text nick, @Nullable String pronouns, boolean includePronouns) {
        boolean shouldIncludePronouns = includePronouns || NicknamesEverywhere.CONFIG.usePronounsEverywhere();
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
        boolean usePronounsEverywhere = com.joel4848.nicknameseverywhere.client.ClientConfigHolder.getServerUsePronounsEverywhere();
        boolean shouldIncludePronouns = includePronouns || usePronounsEverywhere;
        if (!shouldIncludePronouns) {
            if (nick == null) return null;
            return nick;
        }
        return combineNickAndPronouns(nick, pronouns);
    }
}