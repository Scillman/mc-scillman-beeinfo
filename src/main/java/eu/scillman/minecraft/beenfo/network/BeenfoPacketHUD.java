package eu.scillman.minecraft.beenfo.network;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

/**
 * @brief Packet send from the server to the client with the beehive/nest HUD information.
 */
public class BeenfoPacketHUD extends PacketByteBuf
{
    private int version = 1;

    public int honeyLevel;
    public int beeCount;
    public BlockPos blockPos;

    private BeenfoPacketHUD()
    {
        super(Unpooled.buffer());
    }

    private BeenfoPacketHUD(PacketByteBuf parent)
    {
        super(parent);
    }

    private BeenfoPacketHUD encode()
    {
        writeInt(version);
        writeInt(honeyLevel);
        writeInt(beeCount);
        writeBlockPos(blockPos);

        return this;
    }

    public static BeenfoPacketHUD encode(int honeyLevel, int beeCount, BlockPos blockPos)
    {
        BeenfoPacketHUD packet = new BeenfoPacketHUD();

        packet.honeyLevel = honeyLevel;
        packet.beeCount = beeCount;
        packet.blockPos = blockPos;

        return packet.encode();
    }

    private BeenfoPacketHUD decode()
    {
        // TODO: check version
        version     = readInt();
        honeyLevel  = readInt();
        beeCount    = readInt();
        blockPos    = readBlockPos();

        return this;
    }

    public static BeenfoPacketHUD decode(PacketByteBuf buffer)
    {
        BeenfoPacketHUD packet = new BeenfoPacketHUD(buffer);
        return packet.decode();
    }
}
