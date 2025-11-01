package com.joel4848.namerscoreboards.cca;

import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.BuiltInEndecs;
import io.wispforest.endec.impl.KeyedEndec;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.ladysnake.cca.api.v3.component.Component;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

import static com.joel4848.namerscoreboards.registry.CardinalComponentsRegistry.NICK_STORAGE;

public class NickStorage implements Component, AutoSyncedComponent {
    private final Scoreboard holder;
    @Nullable
    public final MinecraftServer server;
    private HashMap<UUID, String> nicks = new HashMap<>();

    //region ENDEC STUFF

    private static final KeyedEndec<HashMap<UUID, String>> NICKS = Endec.map(BuiltInEndecs.UUID, Endec.STRING).xmap(HashMap::new, hashMap -> hashMap).keyed("nicks", new HashMap<>());

    @Override
    public void readFromNbt(NbtCompound tag, @NotNull RegistryWrapper.WrapperLookup registryLookup) {
        this.nicks = tag.get(NICKS);
    }

    @Override
    public void writeToNbt(NbtCompound tag, @NotNull RegistryWrapper.WrapperLookup registryLookup) {
        tag.put(NICKS, this.nicks);
    }

    //endregion

    public NickStorage(Scoreboard holder, @Nullable MinecraftServer server) {
        this.holder = holder;
        this.server = server;
    }

    public void setNick(ServerPlayerEntity player, String nick) {
        this.nicks.put(player.getUuid(), nick);
        syncPlayerNick(player);
    }

    public void clearNick(ServerPlayerEntity player) {
        this.nicks.remove(player.getUuid());
        syncPlayerNick(player);
    }

    public void syncPlayerNick(ServerPlayerEntity player) {
        NICK_STORAGE.sync(holder);
        if (server == null) return;
        Objects.requireNonNull(server).getPlayerManager().sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME, player));
    }

    @Nullable
    public String getRawNick(UUID uuid) {
        return this.nicks.get(uuid);
    }
}