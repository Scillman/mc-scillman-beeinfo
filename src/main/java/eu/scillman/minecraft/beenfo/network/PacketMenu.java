package eu.scillman.minecraft.beenfo.network;

import io.netty.buffer.Unpooled;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.PacketByteBuf;

/**
 * @brief Packet send from the server to the client with the beehive/nest menu information.
 */
public class PacketMenu extends PacketByteBuf
{
    private int version = 1;

    public int honeyLevel;
    public int beeCount;
    public List<String> beeNames;

    public PacketMenu()
    {
        super(Unpooled.buffer());
    }

    PacketMenu(PacketByteBuf parent)
    {
        super(parent);
    }

    public PacketMenu encode()
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

    public static PacketMenu encode(int honeyLevel, List<String> beeNames)
    {
        PacketMenu packet = new PacketMenu();

        packet.honeyLevel = honeyLevel;
        packet.beeNames = beeNames;
        packet.beeCount = beeNames.size();

        return packet.encode();
    }

    private PacketMenu decode()
    {
        // TODO: check version
        version     = readInt();
        honeyLevel  = readInt();
        beeCount    = readInt();

        beeNames = new ArrayList<String>();
        for (int i = 0; i < beeCount; i++)
        {
            beeNames.add(readString());
        }

        return this;
    }

    public static PacketMenu decode(PacketByteBuf buffer)
    {
        PacketMenu packet = new PacketMenu(buffer);
        return packet.decode();
    }
}
