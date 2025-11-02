package com.joel4848.namerscoreboards.mixin.client;

import com.joel4848.namerscoreboards.util.NickFormatter;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.mojang.authlib.GameProfile;

import static com.joel4848.namerscoreboards.registry.CardinalComponentsRegistry.NICK_STORAGE;

@Mixin(PlayerListEntry.class)
public abstract class PlayerListEntryMixin {

    @Shadow public abstract GameProfile getProfile();

    /**
     * Override the display name used in chat messages to show nicknames
     */
    @ModifyReturnValue(method = "getDisplayName", at = @At("RETURN"))
    private Text useNicknameInChat(Text original) {
        if (original != null) {
            // Player has a custom display name set, don't override
            return original;
        }

        PlayerListEntry entry = (PlayerListEntry) (Object) this;
        net.minecraft.client.MinecraftClient client = net.minecraft.client.MinecraftClient.getInstance();

        if (client.world == null) return original;

        var scoreboard = client.world.getScoreboard();
        if (scoreboard == null) return original;

        var storage = NICK_STORAGE.getNullable(scoreboard);
        if (storage == null) return original;

        String rawNick = storage.getRawNick(getProfile().getId());
        if (rawNick == null) return original;

        return NickFormatter.parseNick(rawNick);
    }
}