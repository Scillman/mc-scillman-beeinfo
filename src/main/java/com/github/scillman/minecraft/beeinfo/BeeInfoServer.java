package com.github.scillman.minecraft.beeinfo;

import java.util.ArrayList;

import com.github.scillman.minecraft.beeinfo.nbt.NbtBeehive;
import com.github.scillman.minecraft.beeinfo.network.PacketHUD;
import com.github.scillman.minecraft.beeinfo.network.PacketLookAt;
import com.github.scillman.minecraft.beeinfo.network.PacketMenu;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static net.minecraft.block.BeehiveBlock.HONEY_LEVEL;

public class BeeInfoServer implements ModInitializer
{
    @Override
    public void onInitialize()
    {
        PayloadTypeRegistry.playS2C().register(PacketHUD.ID, PacketHUD.CODEC);
        PayloadTypeRegistry.playS2C().register(PacketMenu.ID, PacketMenu.CODEC);
        PayloadTypeRegistry.playC2S().register(PacketLookAt.ID, PacketLookAt.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(PacketLookAt.ID, BeeInfoServer::onLookAtPayloadReceived);
    }

    public static void onLookAtPayloadReceived(PacketLookAt payload, ServerPlayNetworking.Context context)
    {
        sendHudInfoToClient(context.player(), payload.getBlockPos(), context.responseSender());
    }

    public static void sendBlockInfoToClient(ServerPlayerEntity player, int honeyLevel, ArrayList<String> beeNames)
    {
        String bee1 = beeNames.size() > 0 ? beeNames.get(0) : "";
        String bee2 = beeNames.size() > 1 ? beeNames.get(1) : "";
        String bee3 = beeNames.size() > 2 ? beeNames.get(2) : "";
        ServerPlayNetworking.send(player, new PacketMenu(honeyLevel, beeNames.size(), bee1, bee2, bee3));
    }

    private static void sendHudInfoToClient(ServerPlayerEntity player, BlockPos blockPos, PacketSender responseSender)
    {
        World world = player.getWorld();
        assert(world != null);

        BlockState blockState = world.getBlockState(blockPos);
        if (blockState.contains(HONEY_LEVEL))
        {
            NbtBeehive beehive = NbtBeehive.create(world, blockPos, world.getBlockState(blockPos));
            responseSender.sendPacket(new PacketHUD(beehive.getHoneyLevel(), beehive.getBeeCount(), beehive.getBabyBeeCount(), blockPos));
        }
    }
}
