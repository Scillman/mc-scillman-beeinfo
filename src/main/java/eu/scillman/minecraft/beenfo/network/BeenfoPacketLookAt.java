package eu.scillman.minecraft.beenfo.network;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

/**
 * @brief A packet send from the client to the server.
 * 
 * A packet send from the client to the server, contains information about the block the player is looking at.
 */
public class BeenfoPacketLookAt extends PacketByteBuf
{
    private int version = 1;

    public BlockPos blockPos;

    private BeenfoPacketLookAt()
    {
        super(Unpooled.buffer());
    }

    private BeenfoPacketLookAt(PacketByteBuf parent)
    {
        super(parent);
    }

    private BeenfoPacketLookAt encode()
    {
        writeInt(version);
        writeBlockPos(blockPos);

        return this;
    }

    public static BeenfoPacketLookAt encode(BlockPos blockPos)
    {
        BeenfoPacketLookAt packet = new BeenfoPacketLookAt();

        packet.blockPos = blockPos;

        return packet.encode();
    }

    private BeenfoPacketLookAt decode()
    {
        // TODO: check version
        version  = readInt();
        blockPos = readBlockPos();

        return this;
    }

    public static BeenfoPacketLookAt decode(PacketByteBuf buffer)
    {
        BeenfoPacketLookAt packet = new BeenfoPacketLookAt(buffer);
        return packet.decode();
    }
}
