package com.joel4848.nicknameseverywhere.mixin.common;

import com.joel4848.nicknameseverywhere.pond.PlayerEntityDuck;
import com.joel4848.nicknameseverywhere.util.DisplayNameFormatter;
import com.joel4848.nicknameseverywhere.util.NickFormatter;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.joel4848.nicknameseverywhere.registry.CardinalComponentsRegistry.NICK_STORAGE;

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

        if (rawNick == null && (rawPronouns == null || rawPronouns.isBlank())) return;

        Text parsedNick = rawNick != null
                ? NickFormatter.parseNick(rawNick)
                : Text.literal(self.getNameForScoreboard());

        Text combined = DisplayNameFormatter.combineNickAndPronouns(parsedNick, rawPronouns);
        if (combined == null) return;

        cir.setReturnValue(NickFormatter.nickAndName(combined, ((PlayerEntityDuck) self).nicknameseverywhere$getActualDisplayName()));
    }
}