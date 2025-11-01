package com.joel4848.namerscoreboards.mixin.client;

import com.joel4848.namerscoreboards.util.NickFormatter;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.scoreboard.ScoreboardEntry;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import static com.joel4848.namerscoreboards.registry.CardinalComponentsRegistry.NICK_STORAGE;

@Mixin(ScoreboardEntry.class)
public abstract class ScoreboardPlayerListEntryMixin {

    /**
     * Replace the scoreboard display name with the player's nickname, if available.
     * This hooks into the `name()` method in ScoreboardEntry (Yarn 1.11-SNAPSHOT).
     */
    @ModifyReturnValue(method = "name", at = @At("RETURN"))
    private Text useNicknameInScoreboard(Text original) {
        ScoreboardEntry entry = (ScoreboardEntry) (Object) this;
        String ownerName = entry.owner();

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) return original;

        var scoreboard = client.world.getScoreboard();
        if (scoreboard == null) return original;

        var storage = NICK_STORAGE.getNullable(scoreboard);
        if (storage == null) return original;

        if (client.getNetworkHandler() != null) {
            for (PlayerListEntry playerListEntry : client.getNetworkHandler().getPlayerList()) {
                if (playerListEntry.getProfile().getName().equals(ownerName)) {
                    String rawNick = storage.getRawNick(playerListEntry.getProfile().getId());
                    if (rawNick != null) {
                        // Parse on client side (respects server's formatting setting)
                        return NickFormatter.parseNick(rawNick);
                    }
                    break;
                }
            }
        }

        return original;
    }
}