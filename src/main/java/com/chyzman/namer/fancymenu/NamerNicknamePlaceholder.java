package com.chyzman.namer.fancymenu;

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

import static com.chyzman.namer.registry.CardinalComponentsRegistry.NICK_STORAGE;

public class NamerNicknamePlaceholder extends Placeholder {

    public NamerNicknamePlaceholder() {
        super("namerscoreboards_nickname");
    }

    @Override
    public String getReplacementFor(DeserializedPlaceholderString dps) {
        // Get the username from the placeholder values
        String username = dps.values.get("username");
        if (username == null || username.isBlank()) {
            return "";
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

        // Find the player by username and get their nickname
        for (PlayerListEntry playerListEntry : networkHandler.getPlayerList()) {
            if (playerListEntry.getProfile().getName().equals(username)) {
                Text nick = storage.getNick(playerListEntry.getProfile().getId());
                if (nick != null) {
                    return nick.getString();
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
        return List.of("username");
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "Namer Nickname";
    }

    @Nullable
    @Override
    public List<String> getDescription() {
        return List.of("Returns the nickname of a player by their username.");
    }

    @Override
    public String getCategory() {
        return "Namer";
    }

    @NotNull
    @Override
    public DeserializedPlaceholderString getDefaultPlaceholderString() {
        LinkedHashMap<String, String> values = new LinkedHashMap<>();
        values.put("username", "Steve");
        return new DeserializedPlaceholderString(this.getIdentifier(), values, "");
    }
}