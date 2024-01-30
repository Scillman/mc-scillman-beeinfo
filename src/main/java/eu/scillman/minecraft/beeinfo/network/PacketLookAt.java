package eu.scillman.minecraft.beeinfo.network;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

/**
 * @brief A packet send from the client to the server.
 *
 * A packet send from the client to the server, contains information about the block the player is looking at.
 */
public class PacketLookAt extends PacketByteBuf
{
    private int version = 1;

    public BlockPos blockPos;

    private PacketLookAt()
    {
        super(Unpooled.buffer());
    }

    private PacketLookAt(PacketByteBuf parent)
    {
        super(parent);
    }

    private PacketLookAt encode()
    {
        writeInt(version);
        writeBlockPos(blockPos);

        return this;
    }

    public static PacketLookAt encode(BlockPos blockPos)
    {
        PacketLookAt packet = new PacketLookAt();

        packet.blockPos = blockPos;

        return packet.encode();
    }

    private PacketLookAt decode()
    {
        // TODO: check version
        version  = readInt();
        blockPos = readBlockPos();

        return this;
    }

    public static PacketLookAt decode(PacketByteBuf buffer)
    {
        PacketLookAt packet = new PacketLookAt(buffer);
        return packet.decode();
    }
}
