package com.joel4848.namerscoreboards.client;

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents.AllowChat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import com.mojang.authlib.GameProfile;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.Collection;

import static com.joel4848.namerscoreboards.registry.CardinalComponentsRegistry.NICK_STORAGE;

/**
 * Client-side chat listener that replaces displayed sender names with nicknames (if present),
 * and preserves team color formatting.
 *
 * Format:
 *   - No nick: username: message  (vanilla behavior)
 *   - With nick: nickname: message   (nickname + colon; color matches team color if any)
 */
public final class ChatNickClient {

    private ChatNickClient() {}

    public static void register() {
        ClientReceiveMessageEvents.ALLOW_CHAT.register((message, signedMessage, sender, params, receptionTimestamp) -> {
            // If there's no sender (system message) or client/world missing, let vanilla handle it.
            if (sender == null) return true;
            MinecraftClient client = MinecraftClient.getInstance();
            if (client == null || client.world == null || client.getNetworkHandler() == null) return true;

            Scoreboard scoreboard = client.world.getScoreboard();
            if (scoreboard == null) return true;

            var storage = NICK_STORAGE.getNullable(scoreboard);
            if (storage == null) return true;

            Text nick = storage.getNick(sender.getId());
            if (nick == null) {
                // No nickname — let vanilla display username: message
                return true;
            }

            // Find the player's team by scanning scoreboard teams (avoids reliance on a possibly different mapping)
            Collection<Team> teams = scoreboard.getTeams();
            Team foundTeam = null;
            String playerName = sender.getName();
            for (Team t : teams) {
                if (t.getPlayerList().contains(playerName)) {
                    foundTeam = t;
                    break;
                }
            }

            Formatting teamColor = (foundTeam != null && foundTeam.getColor() != null) ? foundTeam.getColor() : Formatting.WHITE;

            // Apply the team color to the nickname and the colon/separator
            MutableText displayName = nick.copy().setStyle(Style.EMPTY.withColor(teamColor));
            Text separator = Text.literal(": ").setStyle(Style.EMPTY.withColor(teamColor));

            // Message body (preserve signed message content when present)
            Text content = (signedMessage == null) ? message : signedMessage.getContent();

            // Combine: nickname: message
            MutableText combined = Text.empty().append(displayName).append(separator).append(content);

            // Show combined message in chat HUD and block vanilla display
            if (client.inGameHud != null && client.inGameHud.getChatHud() != null) {
                client.inGameHud.getChatHud().addMessage(combined);
            } else if (client.player != null) {
                client.player.sendMessage(combined);
            }

            return false;
        });
    }
}
