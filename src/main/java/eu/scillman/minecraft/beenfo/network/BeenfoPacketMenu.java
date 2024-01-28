package eu.scillman.minecraft.beenfo.network;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import java.util.List;

/**
 * @brief Packet send from the server to the client with the beehive/nest menu information.
 */
public class BeenfoPacketMenu extends PacketByteBuf
{
    private int version = 1;

    public int honeyLevel;
    public int beeCount;
    public List<String> beeNames;

    public BeenfoPacketMenu()
    {
        super(Unpooled.buffer());
    }

    BeenfoPacketMenu(PacketByteBuf parent)
    {
        super(parent);
    }

    public BeenfoPacketMenu encode()
    {
        writeInt(version);
        writeInt(honeyLevel);
        writeInt(beeCount);

        for (int i = 0; i < Math.max(beeCount, beeNames.size()); i++)
        {
            writeString(beeNames.get(i));
        }

        return this;
    }

    public static BeenfoPacketMenu encode(int honeyLevel, List<String> beeNames)
    {
        BeenfoPacketMenu packet = new BeenfoPacketMenu();

        packet.honeyLevel = honeyLevel;
        packet.beeNames = beeNames;
        packet.beeCount = beeNames.size();

        return packet.encode();
    }

    private BeenfoPacketMenu decode()
    {
        // TODO: check version
        version     = readInt();
        honeyLevel  = readInt();
        beeCount    = readInt();

        beeNames.clear();
        for (int i = 0; i < beeCount; i++)
        {
            beeNames.add(readString());
        }

        return this;
    }

    public static BeenfoPacketMenu decode(PacketByteBuf buffer)
    {
        BeenfoPacketMenu packet = new BeenfoPacketMenu(buffer);
        return packet.decode();
    }
}
