package com.joel4848.namerscoreboards.mixin.common;

import com.joel4848.namerscoreboards.util.NickFormatter;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.joel4848.namerscoreboards.registry.CardinalComponentsRegistry.NICK_STORAGE;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {

    @Inject(method = "getPlayerListName", at = @At("HEAD"), cancellable = true)
    private void nicksInPlayerList(CallbackInfoReturnable<Text> cir) {
        ServerPlayerEntity self = (ServerPlayerEntity) (Object) this;

        var scoreboard = self.getWorld().getScoreboard();
        if (scoreboard == null) return;

        var storage = NICK_STORAGE.getNullable(scoreboard);
        if (storage == null) return;

        var rawNick = storage.getRawNick(self.getUuid());
        var rawPronouns = storage.getRawPronouns(self.getUuid());
        String username = self.getNameForScoreboard();

        // Parse nickname if present
        Text parsedNick = rawNick != null ? NickFormatter.parseNick(rawNick) : null;

        // Format the player list name
        Text formatted = NickFormatter.formatPlayerListName(parsedNick, rawPronouns, username);

        // If null, use vanilla behavior
        if (formatted == null) return;

        // Apply team color to the nickname part only (if nickname exists)
        if (parsedNick != null) {
            Team team = scoreboard.getScoreHolderTeam(username);
            if (team != null) {
                // We need to rebuild with team color on the nickname
                boolean hasPronouns = rawPronouns != null && !rawPronouns.isBlank();

                Text teamColoredNick = Team.decorateName(team, parsedNick);
                Text result = Text.empty().append(teamColoredNick);

                if (hasPronouns) {
                    result = Text.empty().append(result).append(" ").append(Text.literal(rawPronouns).formatted(Formatting.WHITE));
                }

                result = Text.empty()
                        .append(result)
                        .append(" ")
                        .append(
                                Text.literal("(" + username + ")")
                                        .formatted(Formatting.GRAY)
                                        .formatted(Formatting.ITALIC)
                        );

                cir.setReturnValue(result);
                return;
            }
        } else if (rawPronouns != null && !rawPronouns.isBlank()) {
            // Pronouns only - apply team color to username
            Team team = scoreboard.getScoreHolderTeam(username);
            if (team != null) {
                Text teamColoredUsername = Team.decorateName(team, Text.literal(username));
                cir.setReturnValue(
                        Text.empty()
                                .append(teamColoredUsername)
                                .append(" ")
                                .append(Text.literal(rawPronouns).formatted(Formatting.WHITE))
                );
                return;
            }
        }

        cir.setReturnValue(formatted);
    }
}