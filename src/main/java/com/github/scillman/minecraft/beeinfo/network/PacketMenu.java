package com.github.scillman.minecraft.beeinfo.network;

import java.util.ArrayList;
import java.util.List;

import com.github.scillman.minecraft.beeinfo.BeeInfo;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

/**
 * The packet containing the payload for the in-game menu.
 */
public record PacketMenu(int honeyLevel, int beeCount, String beeName1, String beeName2, String beeName3) implements CustomPayload
{
    public static final CustomPayload.Id<PacketMenu> ID = new CustomPayload.Id<>(BeeInfo.PACKET_ID_MENU);
    public static final PacketCodec<RegistryByteBuf, PacketMenu> CODEC = PacketCodec.tuple(
        PacketCodecs.INTEGER, PacketMenu::honeyLevel,
        PacketCodecs.INTEGER, PacketMenu::beeCount,
        PacketCodecs.STRING, PacketMenu::beeName1,
        PacketCodecs.STRING, PacketMenu::beeName2,
        PacketCodecs.STRING, PacketMenu::beeName3,
        PacketMenu::new
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId()
    {
        return ID;
    }

    /**
     * Get the honey level of the beehive.
     * @return The honey level of the beehive.
     */
    public int getHoneyLevel()
    {
        return this.honeyLevel;
    }

    /**
     * Get the number of bees inside the beehive.
     * @return The number of bees inside the beehive.
     */
    public int getBeeCount()
    {
        return this.beeCount;
    }

    /**
     * Get the names of the bees inside the beehive.
     * @return The names of the bees inside the beehive.
     */
    public List<String> getBeeNames()
    {
        ArrayList<String> list = new ArrayList<String>();

        list.add(beeName1);
        list.add(beeName2);
        list.add(beeName3);

        return list;
    }
}
