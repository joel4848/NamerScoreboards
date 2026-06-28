package com.joel4848.nicknameseverywhere.network;

import com.joel4848.nicknameseverywhere.NicknamesEverywhere;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record ConfigSyncPayload(boolean allowNickFormatting, boolean usePronounsEverywhere) implements CustomPayload {
    public static final CustomPayload.Id<ConfigSyncPayload> ID = new CustomPayload.Id<>(NicknamesEverywhere.id("config_sync"));

    public static final PacketCodec<RegistryByteBuf, ConfigSyncPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.BOOL, ConfigSyncPayload::allowNickFormatting,
            PacketCodecs.BOOL, ConfigSyncPayload::usePronounsEverywhere,
            ConfigSyncPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}