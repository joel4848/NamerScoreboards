package com.joel4848.namerscoreboards.mixin.client;

import com.joel4848.namerscoreboards.impl.NickSuggestionData;
import com.joel4848.namerscoreboards.pond.CommandSourceDuck;
import com.joel4848.namerscoreboards.util.NickFormatter;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static com.joel4848.namerscoreboards.registry.CardinalComponentsRegistry.NICK_STORAGE;

@Mixin(ClientCommandSource.class)
public abstract class ClientCommandSourceMixin implements CommandSourceDuck {
    @Shadow @Final private ClientPlayNetworkHandler networkHandler;

    @Override
    public List<NickSuggestionData> namerscoreboards$getNickSuggestionData() {
        var storage = NICK_STORAGE.getNullable(networkHandler.getWorld().getScoreboard());
        return networkHandler.getPlayerList().stream()
                .map(playerListEntry -> {
                    var profile = playerListEntry.getProfile();
                    var rawNick = storage == null ? null : storage.getRawNick(profile.getId());
                    var nick = rawNick == null ? null : NickFormatter.parseNick(rawNick);
                    return new NickSuggestionData(Text.literal(profile.getName()), nick);
                })
                .toList();
    }

    @WrapOperation(method = "getChatSuggestions", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientCommandSource;getPlayerNames()Ljava/util/Collection;"))
    private Collection<String> namerscoreboards$addNicksToChatSuggestions(ClientCommandSource instance, Operation<Collection<String>> original) {
        var originalResult = original.call(instance);
        var storage = NICK_STORAGE.getNullable(networkHandler.getWorld().getScoreboard());
        if (storage == null) return originalResult;
        originalResult.addAll(
                networkHandler.getPlayerList().stream()
                        .map(playerListEntry -> storage.getRawNick(playerListEntry.getProfile().getId()))
                        .filter(Objects::nonNull)
                        .map(rawNick -> NickFormatter.parseNick(rawNick).getString())
                        .toList()
        );
        return originalResult.stream().distinct().toList();
    }
}