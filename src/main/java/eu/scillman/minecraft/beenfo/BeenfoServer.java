package eu.scillman.minecraft.beenfo;

import static net.minecraft.block.BeehiveBlock.HONEY_LEVEL;

import org.jetbrains.annotations.Nullable;

import eu.scillman.minecraft.beenfo.Beenfo;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.BlockState;
import net.minecraft.world.World;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.block.entity.BlockEntity;

public class BeenfoServer implements ModInitializer
{
    public static final Identifier C2SPacketIdentifier      = new Identifier(Beenfo.MOD_ID, "c2s");         // Client -> Server
    public static final Identifier S2CPacketIdentifierOpen  = new Identifier(Beenfo.MOD_ID, "s2c_open");    // Server -> Client 
    public static final Identifier S2CPacketIdentifierHud   = new Identifier(Beenfo.MOD_ID, "s2c_hud");     // Server -> Client

    @Override
    public void onInitialize()
    {
        ServerPlayNetworking.registerGlobalReceiver(C2SPacketIdentifier, (server, player, handler, buf, responseSender) -> {
            processClientPacket(server, player, handler, buf, responseSender);
        });
    }

    /**
     * @brief Send the info about a beehive that has been clicked on to the client.
     * @param player
     * @param honeyLevel
     * @param bees
     */
    public static void sendBeehiveInfo(ServerPlayerEntity player, int honeyLevel, @Nullable NbtList bees)
    {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(honeyLevel);

        if (bees == null)
        {
            buf.writeInt(0);
        }
        else
        {
            int beeCount = bees.size();
            buf.writeInt(beeCount);

            for (int i = 0; i < beeCount; i++)
            {
                NbtCompound nbt = bees.getCompound(i).getCompound("EntityData");
                if (nbt != null && nbt.contains("CustomName", 8))
                {
                    String beeName = nbt.getString("CustomName");
                    buf.writeString(beeName);
                }
                else
                {
                    buf.writeString("");
                }
            }
        }

        ServerPlayNetworking.send(player, S2CPacketIdentifierOpen, buf);
    }

    /**
     * @brief Process the packet which is sent by the client when looking at a hive.
     * @param server
     * @param player
     * @param handler
     * @param attachedData
     * @param responseSender
     */
    private void processClientPacket(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf attachedData, PacketSender responseSender)
    {
        int packetVersion = attachedData.readInt();
        BlockPos blockPos = attachedData.readBlockPos();
        server.execute(() -> {
            sendHudContent(player, blockPos, responseSender);
        });
    }

    /**
     * @brief
     * @param player
     * @param pos
     * @param responseSender
     */
    private void sendHudContent(ServerPlayerEntity player, BlockPos blockPos, PacketSender responseSender)
    {
        World world = player.world;
        BlockState blockState = world.getBlockState(blockPos);

        // TODO: verify that the block contains the data

        int honey = blockState.get(HONEY_LEVEL);
    
        BlockEntity entity = world.getBlockEntity(blockPos);
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

        buf.writeInt(0); // packet version
        buf.writeInt(honey);

        if (entity instanceof BeehiveBlockEntity bbe)
        {
            NbtList nbt = bbe.getBees();

            if (nbt == null)
            {
                buf.writeInt(0);
            }
            else
            {
                buf.writeInt(nbt.size());
            }
        }
        else
        {
            buf.writeInt(0);
        }

        buf.writeBlockPos(blockPos);

        responseSender.sendPacket(S2CPacketIdentifierHud, buf);
    }

}
