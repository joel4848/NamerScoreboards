package com.joel4848.namerscoreboards.fancymenu;

import com.joel4848.namerscoreboards.util.DisplayNameFormatter;
import com.joel4848.namerscoreboards.util.NickFormatter;
import de.keksuccino.fancymenu.customization.placeholder.DeserializedPlaceholderString;
import de.keksuccino.fancymenu.customization.placeholder.Placeholder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;

import static com.joel4848.namerscoreboards.registry.CardinalComponentsRegistry.NICK_STORAGE;

public class NamerScoreboardsNicknamePlaceholder extends Placeholder {

    public NamerScoreboardsNicknamePlaceholder() {
        super("namerscoreboards_nickname");
    }

    @Override
    public String getReplacementFor(DeserializedPlaceholderString dps) {
        // Get the username from the placeholder values
        String username = dps.values.get("username");
        if (username == null || username.isBlank()) {
            return "";
        }

        // Check if we should include pronouns (default to true)
        boolean includePronouns = true;
        String includePronounsStr = dps.values.get("include_pronouns");
        if (includePronounsStr != null && !includePronounsStr.isBlank()) {
            includePronouns = Boolean.parseBoolean(includePronounsStr);
        }

        // Get Minecraft client instance
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) {
            return "";
        }

        // Get the client world
        ClientWorld world = client.world;
        if (world == null) {
            // Not in a world (e.g., on main menu)
            return "";
        }

        // Get the network handler
        ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();
        if (networkHandler == null) {
            return "";
        }

        // Get the scoreboard
        var scoreboard = world.getScoreboard();
        if (scoreboard == null) {
            return "";
        }

        // Get the nickname storage
        var storage = NICK_STORAGE.getNullable(scoreboard);
        if (storage == null) {
            return "";
        }

        // Find the player by username and get their nickname/pronouns
        for (PlayerListEntry playerListEntry : networkHandler.getPlayerList()) {
            if (playerListEntry.getProfile().getName().equals(username)) {
                String rawNick = storage.getRawNick(playerListEntry.getProfile().getId());
                String rawPronouns = storage.getRawPronouns(playerListEntry.getProfile().getId());

                if (rawNick != null || (includePronouns && rawPronouns != null)) {
                    // Parse nickname on client side (respects server's formatting setting)
                    Text parsedNick = rawNick != null ? NickFormatter.parseNick(rawNick) : null;

                    if (includePronouns) {
                        // Combine nickname and pronouns
                        Text combined = DisplayNameFormatter.combineNickAndPronouns(parsedNick, rawPronouns);
                        if (combined != null) {
                            return combined.getString();
                        }
                    } else if (parsedNick != null) {
                        // Only return nickname
                        return parsedNick.getString();
                    }
                }
                break;
            }
        }

        // No nickname found, return empty string
        return "";
    }

    @Nullable
    @Override
    public List<String> getValueNames() {
        return List.of("username", "include_pronouns");
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "NamerScoreboards Nickname";
    }

    @Nullable
    @Override
    public List<String> getDescription() {
        return List.of("Returns the nickname (and optionally pronouns) of a player by their username.", "Set include_pronouns to false to exclude pronouns.");
    }

    @Override
    public String getCategory() {
        return "NamerScoreboards";
    }

    @NotNull
    @Override
    public DeserializedPlaceholderString getDefaultPlaceholderString() {
        LinkedHashMap<String, String> values = new LinkedHashMap<>();
        values.put("username", "Steve");
        values.put("include_pronouns", "true");
        return new DeserializedPlaceholderString(this.getIdentifier(), values, "");
    }
}