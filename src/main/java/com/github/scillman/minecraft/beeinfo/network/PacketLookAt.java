package com.github.scillman.minecraft.beeinfo.network;

import com.github.scillman.minecraft.beeinfo.BeeInfo;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.BlockPos;

/**
 * Packet with the payload for the look at data.
 */
public record PacketLookAt(BlockPos blockPos) implements CustomPayload
{
    public static final CustomPayload.Id<PacketLookAt> ID = new CustomPayload.Id<>(BeeInfo.PACKET_ID_LOOKAT);
    public static final PacketCodec<RegistryByteBuf, PacketLookAt> CODEC = PacketCodec.tuple(
        BlockPos.PACKET_CODEC, PacketLookAt::blockPos,
        PacketLookAt::new
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId()
    {
        return ID;
    }

    /**
     * Get the BlockPos of the block that is being looked at.
     * @return The BlockPos of the block that is being looked at.
     */
    public BlockPos getBlockPos()
    {
        return this.blockPos;
    }
}
