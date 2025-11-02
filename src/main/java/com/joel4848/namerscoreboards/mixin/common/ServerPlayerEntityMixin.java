package com.joel4848.namerscoreboards.mixin.common;

import com.joel4848.namerscoreboards.pond.PlayerEntityDuck;
import com.joel4848.namerscoreboards.util.DisplayNameFormatter;
import com.joel4848.namerscoreboards.util.NickFormatter;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
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

        // If no nickname or pronouns, use default behavior
        if (rawNick == null && (rawPronouns == null || rawPronouns.isBlank())) {
            return;
        }

        // Parse the nickname on-demand, or use username if only pronouns exist
        Text parsedNick;
        if (rawNick != null) {
            parsedNick = NickFormatter.parseNick(rawNick);
        } else {
            parsedNick = Text.literal(self.getNameForScoreboard());
        }

        // Combine nickname and pronouns
        Text combined = DisplayNameFormatter.combineNickAndPronouns(parsedNick, rawPronouns);

        if (combined == null) return;

        cir.setReturnValue(NickFormatter.nickAndName(combined, ((PlayerEntityDuck)self).namerscoreboards$getActualDisplayName()));
    }
}