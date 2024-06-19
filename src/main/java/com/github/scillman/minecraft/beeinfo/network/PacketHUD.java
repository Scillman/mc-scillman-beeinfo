package com.github.scillman.minecraft.beeinfo.network;

import com.github.scillman.minecraft.beeinfo.BeeInfo;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;

/**
 * Packet with the payload for the HUD.
 */
public record PacketHUD(int honeyLevel, int beeCount, int childCount, BlockPos blockPos) implements CustomPayload
{
    public static final CustomPayload.Id<PacketHUD> ID = new CustomPayload.Id<>(BeeInfo.PACKET_ID_HUD);
    public static final PacketCodec<RegistryByteBuf, PacketHUD> CODEC = PacketCodec.tuple(
        PacketCodecs.INTEGER, PacketHUD::honeyLevel,
        PacketCodecs.INTEGER, PacketHUD::beeCount,
        PacketCodecs.INTEGER, PacketHUD::childCount,
        BlockPos.PACKET_CODEC, PacketHUD::blockPos,
        PacketHUD::new
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId()
    {
        return ID;
    }

    /**
     * Get the honey level of the beehive that is being looked at.
     * @return The honey level of the beehive that is being looked at.
     */
    public int getHoneyLevel()
    {
        return this.honeyLevel;
    }

    /**
     * Get the number of bees inside the beehive that is being looked at.
     * @return The number of bees inside the beehive that is being looked at.
     */
    public int getBeeCount()
    {
        return this.beeCount;
    }

    /**
     * Get the number of child bees inside the beehive that is being looked at.
     * @return The number of child bees inside the beehive that is being looked at.
     */
    public int getChildCount()
    {
        return this.childCount;
    }

    /**
     * Get the BlockPos of the beehive that is being looked at.
     * @return The BlockPos of the beehive that is being looked at.
     */
    public BlockPos getBlockPos()
    {
        return this.blockPos;
    }
}
