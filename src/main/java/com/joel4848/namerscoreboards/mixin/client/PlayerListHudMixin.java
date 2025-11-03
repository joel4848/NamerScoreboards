package com.joel4848.namerscoreboards.mixin.client;

import com.joel4848.namerscoreboards.util.NickFormatter;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import static com.joel4848.namerscoreboards.registry.CardinalComponentsRegistry.NICK_STORAGE;

@Mixin(PlayerListHud.class)
public abstract class PlayerListHudMixin {

    @Shadow @Final private MinecraftClient client;

    /**
     * Intercept the getPlayerName call in the tab list to show nicknames + pronouns with team colors
     */
    @WrapOperation(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/hud/PlayerListHud;getPlayerName(Lnet/minecraft/client/network/PlayerListEntry;)Lnet/minecraft/text/Text;"
            )
    )
    private Text modifyPlayerListName(PlayerListHud instance, PlayerListEntry entry, Operation<Text> original) {
        if (client.world == null) return original.call(instance, entry);

        var scoreboard = client.world.getScoreboard();
        if (scoreboard == null) return original.call(instance, entry);

        var storage = NICK_STORAGE.getNullable(scoreboard);
        if (storage == null) return original.call(instance, entry);

        String rawNick = storage.getRawNick(entry.getProfile().getId());
        String rawPronouns = storage.getRawPronouns(entry.getProfile().getId());
        String username = entry.getProfile().getName();

        // Parse nickname if present
        Text parsedNick = rawNick != null ? NickFormatter.parseNick(rawNick) : null;

        // Format the player list name
        Text formatted = NickFormatter.formatPlayerListName(parsedNick, rawPronouns, username);

        // If null, use vanilla behavior
        if (formatted == null) {
            return original.call(instance, entry);
        }

        // Apply team color to the nickname part only (if nickname exists)
        if (parsedNick != null) {
            Team team = scoreboard.getScoreHolderTeam(username);
            if (team != null) {
                // We need to rebuild with team color on the nickname
                boolean hasPronouns = rawPronouns != null && !rawPronouns.isBlank();

                Text teamColoredNick = Team.decorateName(team, parsedNick);
                Text result = Text.empty().append(teamColoredNick);

                if (hasPronouns) {
                    result = Text.empty().append(result).append(" ").append(Text.literal(rawPronouns).styled(style -> style.withColor(0xFFFFFF)));
                }

                result = Text.empty()
                        .append(result)
                        .append(" ")
                        .append(
                                Text.literal("(" + username + ")")
                                        .styled(style -> style.withColor(0x808080).withItalic(true))
                        );

                return result;
            }
        } else if (rawPronouns != null && !rawPronouns.isBlank()) {
            // Pronouns only - apply team color to username
            Team team = scoreboard.getScoreHolderTeam(username);
            if (team != null) {
                Text teamColoredUsername = Team.decorateName(team, Text.literal(username));
                return Text.empty()
                        .append(teamColoredUsername)
                        .append(" ")
                        .append(Text.literal(rawPronouns).styled(style -> style.withColor(0xFFFFFF)));
            }
        }

        return formatted;
    }
}