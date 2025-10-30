package com.chyzman.namer.mixin.client;

import com.chyzman.namer.cca.NickStorage;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.scoreboard.ScoreboardEntry;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import static com.chyzman.namer.registry.CardinalComponentsRegistry.NICK_STORAGE;

@Mixin(ScoreboardEntry.class)
public abstract class ScoreboardPlayerListEntryMixin {

    @ModifyReturnValue(method = "getDisplayName", at = @At("RETURN"))
    private Text useNicknameInScoreboard(Text original) {
        ScoreboardEntry entry = (ScoreboardEntry) (Object) this;
        String ownerName = entry.owner();

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) return original;

        var scoreboard = client.world.getScoreboard();
        if (scoreboard == null) return original;

        var storage = NICK_STORAGE.getNullable(scoreboard);
        if (storage == null) return original;

        // Try to find the player by username
        if (client.getNetworkHandler() != null) {
            for (PlayerListEntry playerListEntry : client.getNetworkHandler().getPlayerList()) {
                if (playerListEntry.getProfile().getName().equals(ownerName)) {
                    Text nick = storage.getNick(playerListEntry.getProfile().getId());
                    if (nick != null) {
                        return nick;
                    }
                    break;
                }
            }
        }

        return original;
    }
}