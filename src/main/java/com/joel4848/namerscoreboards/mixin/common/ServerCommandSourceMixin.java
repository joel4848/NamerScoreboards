package com.joel4848.namerscoreboards.mixin.common;

import com.joel4848.namerscoreboards.impl.NickSuggestionData;
import com.joel4848.namerscoreboards.pond.CommandSourceDuck;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(ServerCommandSource.class)
public abstract class ServerCommandSourceMixin implements CommandSourceDuck {
    @Shadow @Final private MinecraftServer server;

    @Override
    public List<NickSuggestionData> namerscoreboards$getNickSuggestionData() {
        return server.getPlayerManager().getPlayerList().stream()
                .map(serverPlayerEntity -> new NickSuggestionData(serverPlayerEntity.getName(), serverPlayerEntity.getDisplayName()))
                .toList();
    }
}