package eu.scillman.minecraft.beeinfo.network;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

/**
 * @brief Packet send from the server to the client with the beehive/nest HUD information.
 */
public class PacketHUD extends PacketByteBuf
{
    private int version = 1;

    public int honeyLevel;
    public int beeCount;
    public BlockPos blockPos;

    private PacketHUD()
    {
        super(Unpooled.buffer());
    }

    private PacketHUD(PacketByteBuf parent)
    {
        super(parent);
    }

    private PacketHUD encode()
    {
        writeInt(version);
        writeInt(honeyLevel);
        writeInt(beeCount);
        writeBlockPos(blockPos);

        return this;
    }

    public static PacketHUD encode(int honeyLevel, int beeCount, BlockPos blockPos)
    {
        PacketHUD packet = new PacketHUD();

        packet.honeyLevel = honeyLevel;
        packet.beeCount = beeCount;
        packet.blockPos = blockPos;

        return packet.encode();
    }

    private PacketHUD decode()
    {
        // TODO: check version
        version     = readInt();
        honeyLevel  = readInt();
        beeCount    = readInt();
        blockPos    = readBlockPos();

        return this;
    }

    public static PacketHUD decode(PacketByteBuf buffer)
    {
        PacketHUD packet = new PacketHUD(buffer);
        return packet.decode();
    }
}
