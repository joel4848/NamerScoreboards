package com.joel4848.namerscoreboards.network;

import com.joel4848.namerscoreboards.NamerScoreboards;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record ConfigSyncPayload(boolean allowNickFormatting) implements CustomPayload {
    public static final CustomPayload.Id<ConfigSyncPayload> ID = new CustomPayload.Id<>(NamerScoreboards.id("config_sync"));

    public static final PacketCodec<RegistryByteBuf, ConfigSyncPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.BOOL, ConfigSyncPayload::allowNickFormatting,
            ConfigSyncPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}