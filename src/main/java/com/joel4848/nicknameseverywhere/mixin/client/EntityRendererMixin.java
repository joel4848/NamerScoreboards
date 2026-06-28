package com.joel4848.nicknameseverywhere.mixin.client;

import com.joel4848.nicknameseverywhere.util.DisplayNameFormatter;
import com.joel4848.nicknameseverywhere.util.NickFormatter;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import static com.joel4848.nicknameseverywhere.registry.CardinalComponentsRegistry.NICK_STORAGE;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity> {

    @WrapOperation(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;getDisplayName()Lnet/minecraft/text/Text;"
            )
    )
    private Text alwaysShowPronounsInNametag(Entity entity, Operation<Text> original) {
        Text originalText = original.call(entity);

        if (!(entity instanceof PlayerEntity player)) return originalText;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) return originalText;

        var scoreboard = client.world.getScoreboard();
        if (scoreboard == null) return originalText;

        var storage = NICK_STORAGE.getNullable(scoreboard);
        if (storage == null) return originalText;

        String rawNick = storage.getRawNick(player.getUuid());
        String rawPronouns = storage.getRawPronouns(player.getUuid());

        if (rawNick == null && (rawPronouns == null || rawPronouns.isBlank())) return originalText;

        Text parsedNick = rawNick != null
                ? NickFormatter.parseNick(rawNick)
                : originalText;

        Text combined = DisplayNameFormatter.combineNickAndPronouns(parsedNick, rawPronouns);
        return combined != null ? combined : originalText;
    }
}