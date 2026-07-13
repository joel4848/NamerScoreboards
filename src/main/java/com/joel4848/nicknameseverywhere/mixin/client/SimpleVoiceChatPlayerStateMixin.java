package com.joel4848.nicknameseverywhere.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.UUID;

import static com.joel4848.nicknameseverywhere.registry.CardinalComponentsRegistry.NICK_STORAGE;

@Mixin(targets = "de.maxhenkel.voicechat.voice.common.PlayerState", remap = false)
public abstract class SimpleVoiceChatPlayerStateMixin {

    @ModifyReturnValue(method = "getName", at = @At("RETURN"))
    private String modifyVoiceChatDisplayName(String originalUsername) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.world == null || client.getNetworkHandler() == null) {
            return originalUsername;
        }

        var scoreboard = client.world.getScoreboard();
        if (scoreboard == null) {
            return originalUsername;
        }

        var storage = NICK_STORAGE.getNullable(scoreboard);
        if (storage == null) {
            return originalUsername;
        }

        UUID playerUuid = nicknamesEverywhere$extractUuid(this);
        if (playerUuid == null) {
            return originalUsername;
        }

        String rawNick = storage.getRawNick(playerUuid);
        String rawPronouns = storage.getRawPronouns(playerUuid);

        if (rawNick == null && (rawPronouns == null || rawPronouns.isBlank())) {
            return originalUsername;
        }

        String cleanNick = originalUsername;
        if (rawNick != null) {
            cleanNick = com.joel4848.nicknameseverywhere.util.NickFormatter.parseNick(rawNick).getString();
        }

        String cleanPronouns = "";
        if (rawPronouns != null) {
            cleanPronouns = com.joel4848.nicknameseverywhere.util.NickFormatter.parsePronouns(rawPronouns).getString();
        }

        if (rawPronouns != null && !rawPronouns.isBlank()) {
            return cleanNick + " " + cleanPronouns;
        }

        return cleanNick;
    }

    @Unique
    private static UUID nicknamesEverywhere$extractUuid(Object instance) {
        try {
            java.lang.reflect.Method method = instance.getClass().getMethod("getUuid");
            return (UUID) method.invoke(instance);
        } catch (Exception e1) {
            try {
                java.lang.reflect.Method method = instance.getClass().getMethod("getUuid");
                return (UUID) method.invoke(instance);
            } catch (Exception e2) {
                return null;
            }
        }
    }
}