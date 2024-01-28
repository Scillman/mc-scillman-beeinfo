package eu.scillman.minecraft.beenfo;

import static net.minecraft.block.BeehiveBlock.HONEY_LEVEL;

import org.jetbrains.annotations.Nullable;

import eu.scillman.minecraft.beenfo.network.BeenfoPacketHUD;
import eu.scillman.minecraft.beenfo.network.BeenfoPacketLookAt;
import eu.scillman.minecraft.beenfo.network.BeenfoPacketMenu;
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

import java.util.ArrayList;;

public class BeenfoServer implements ModInitializer
{
    public static final Identifier C2SPacketIdentifierLookAt = new Identifier(Beenfo.MOD_ID, "c2s_lookat");
    public static final Identifier S2CPacketIdentifierMenu   = new Identifier(Beenfo.MOD_ID, "s2c_open");
    public static final Identifier S2CPacketIdentifierHud    = new Identifier(Beenfo.MOD_ID, "s2c_hud");

    @Override
    public void onInitialize()
    {
        ServerPlayNetworking.registerGlobalReceiver(C2SPacketIdentifierLookAt, this::onClientPacketReceived);
    }

    /**
     * Get the name of a single bee entity.
     * @param nbt The entity data.
     * @return The name of the bee if given any; otherwise, an empty string.
     */
    private static String getBeeName(@Nullable NbtCompound nbt)
    {
        if (nbt != null && nbt.contains("CustomName", Beenfo.NBT_TYPE_STRING))
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
    public static void sendBlockInfo(ServerPlayerEntity player, int honeyLevel, @Nullable NbtList bees)
    {
        ArrayList<String> beeNames = getBeeNameList(bees);
        BeenfoPacketMenu packet = BeenfoPacketMenu.encode(honeyLevel, beeNames);
        ServerPlayNetworking.send(player, S2CPacketIdentifierMenu, packet);
    }

    /**
     * @brief Process the packet which is sent by the client when looking at a hive.
     * @param server
     * @param player
     * @param handler
     * @param attachedData
     * @param responseSender
     */
    private void onClientPacketReceived(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf attachedData, PacketSender responseSender)
    {
        BeenfoPacketLookAt packet = BeenfoPacketLookAt.decode(attachedData);
        server.execute(() -> {
            sendHudContent(player, packet.blockPos, responseSender);
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
        int beeCount = 0;
        if (entity instanceof BeehiveBlockEntity bbe)
        {
            NbtList nbt = bbe.getBees();
            if (nbt != null)
            {
                beeCount = nbt.size();
            }
        }
        
        BeenfoPacketHUD packet = BeenfoPacketHUD.encode(honey, beeCount, blockPos);
        responseSender.sendPacket(S2CPacketIdentifierHud, packet);
    }

}
