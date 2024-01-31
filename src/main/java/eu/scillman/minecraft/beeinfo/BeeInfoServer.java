package eu.scillman.minecraft.beeinfo;

import eu.scillman.minecraft.beeinfo.network.PacketHUD;
import eu.scillman.minecraft.beeinfo.network.PacketLookAt;
import eu.scillman.minecraft.beeinfo.network.PacketMenu;
import java.util.ArrayList;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import static net.minecraft.block.BeehiveBlock.HONEY_LEVEL;

public class BeeInfoServer implements ModInitializer
{
    @Override
    public void onInitialize()
    {
        ServerPlayNetworking.registerGlobalReceiver(BeeInfo.PACKET_ID_LOOKAT, BeeInfoServer::onLookAtPacketReceived);
    }

    /**
     * Get the name of a single bee entity.
     * @param nbt The entity data.
     * @return The name of the bee if given any; otherwise, an empty string.
     */
    private static String getBeeName(@Nullable NbtCompound nbt)
    {
        if (nbt != null && nbt.contains("CustomName", BeeInfo.NBT_TYPE_STRING))
        {
            return nbt.getString("CustomName");
        }

        return "";
    }

    /**
     * Get a list of names of all the given bees.
     * @param bees The bees to generate a name list for.
     * @return A list with all the names of the bees.
     */
    private static ArrayList<String> getBeeNameList(@Nullable NbtList bees)
    {
        ArrayList<String> list = new ArrayList<String>();

        if (bees != null)
        {
            for (int i = 0; i < bees.size(); i++)
            {
                list.add(getBeeName(bees.getCompound(i).getCompound("EntityData")));
            }
        }

        return list;
    }

    /**
     * @brief Send the info about a beehive that has been clicked on to the client.
     * @param player
     * @param honeyLevel
     * @param bees
     */
    public static void sendBlockInfoToClient(ServerPlayerEntity player, int honeyLevel, @Nullable NbtList bees)
    {
        ArrayList<String> beeNames = getBeeNameList(bees);
        PacketMenu packet = PacketMenu.encode(honeyLevel, beeNames);
        ServerPlayNetworking.send(player, BeeInfo.PACKET_ID_MENU, packet);
    }

    /**
     * @brief Called when a lookAt pakket has been received from a client.
     * @param server The server that received the request.
     * @param player The player that send the request.
     * @param handler The player's networking handler.
     * @param attachedData The data that was send.
     * @param responseSender The socket to send the response to.
     */
    private static void onLookAtPacketReceived(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf attachedData, PacketSender responseSender)
    {
        PacketLookAt packet = PacketLookAt.decode(attachedData);
        server.execute(() -> {
            sendHudInfoToClient(player, packet.blockPos, responseSender);
        });
    }

    /**
     * @brief Sends the required HUD content to the client.
     * @param player The player that requested the data.
     * @param blockPos The position of the block.
     * @param responseSender The socket to send the response to.
     */
    private static void sendHudInfoToClient(ServerPlayerEntity player, BlockPos blockPos, PacketSender responseSender)
    {
        if (!isHoneyBeeContainer(player.world, blockPos))
        {
            return;
        }

        World world = player.world;
        BlockState blockState = world.getBlockState(blockPos);

        int honey = blockState.get(HONEY_LEVEL);
        int beeCount = getBeeCount(world, blockPos);

        PacketHUD packet = PacketHUD.encode(honey, beeCount, blockPos);
        responseSender.sendPacket(BeeInfo.PACKET_ID_HUD, packet);
    }

    /**
     * @brief Determines if the block is a honey bee container.
     * @param world The world the player is in. (e.g. Overworld, Nether, The End)
     * @param blockPos The position of the block.
     * @return True if the block is a honey bee container; otherwise, false.
     */
    private static boolean isHoneyBeeContainer(World world, BlockPos blockPos)
    {
        BlockState blockState = world.getBlockState(blockPos);
        return blockState.contains(HONEY_LEVEL);
    }

    /**
     * @brief Get the number of bees inside the honey bee container.
     * @param world The world the player is in. (e.g. Overworld, Nether, The End)
     * @param blockPos The position of the block.
     * @return The number of bees inside the honey bee container.
     */
    private static int getBeeCount(World world, BlockPos blockPos)
    {
        BlockEntity entity = world.getBlockEntity(blockPos);
        if (entity instanceof BeehiveBlockEntity bbe)
        {
            NbtList nbt = bbe.getBees();
            if (nbt != null)
            {
                return nbt.size();
            }
        }

        return 0;
    }
}
