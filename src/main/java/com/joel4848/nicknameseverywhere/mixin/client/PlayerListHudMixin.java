package com.joel4848.nicknameseverywhere.mixin.client;

import com.joel4848.nicknameseverywhere.util.DisplayNameFormatter;
import com.joel4848.nicknameseverywhere.util.NickFormatter;
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

import static com.joel4848.nicknameseverywhere.registry.CardinalComponentsRegistry.NICK_STORAGE;

@Mixin(PlayerListHud.class)
public abstract class PlayerListHudMixin {

    @Shadow @Final private MinecraftClient client;

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

        if (rawNick == null && (rawPronouns == null || rawPronouns.isBlank())) {
            return original.call(instance, entry);
        }

        Text parsedNick = rawNick != null
                ? NickFormatter.parseNick(rawNick)
                : Text.literal(entry.getProfile().getName());

        Text combined = DisplayNameFormatter.combineNickAndPronouns(parsedNick, rawPronouns);
        if (combined == null) return original.call(instance, entry);

        Team team = scoreboard.getScoreHolderTeam(entry.getProfile().getName());
        if (team != null) {
            return Team.decorateName(team, combined);
        }

        return combined;
    }
}