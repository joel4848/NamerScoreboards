package com.joel4848.nicknameseverywhere.mixin.client;

import com.joel4848.nicknameseverywhere.util.DisplayNameFormatter;
import com.joel4848.nicknameseverywhere.util.NickFormatter;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.mojang.authlib.GameProfile;

import static com.joel4848.nicknameseverywhere.registry.CardinalComponentsRegistry.NICK_STORAGE;

@Mixin(PlayerListEntry.class)
public abstract class PlayerListEntryMixin {

    @Shadow public abstract GameProfile getProfile();

    @ModifyReturnValue(method = "getDisplayName", at = @At("RETURN"))
    private Text useNicknameInChat(Text original) {
        if (original != null) return original;

        net.minecraft.client.MinecraftClient client = net.minecraft.client.MinecraftClient.getInstance();
        if (client.world == null) return original;

        var scoreboard = client.world.getScoreboard();
        if (scoreboard == null) return original;

        var storage = NICK_STORAGE.getNullable(scoreboard);
        if (storage == null) return original;

        String rawNick = storage.getRawNick(getProfile().getId());
        String rawPronouns = storage.getRawPronouns(getProfile().getId());

        if (rawNick == null && (rawPronouns == null || rawPronouns.isBlank())) return original;

        Text parsedNick = rawNick != null
                ? NickFormatter.parseNick(rawNick)
                : Text.literal(getProfile().getName());

        return DisplayNameFormatter.combineNickAndPronouns(parsedNick, rawPronouns);
    }
}