package eu.scillman.minecraft.beenfo;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
//import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import java.util.List;
import java.util.ArrayList;

/**
 * TODO: create a buffer/packet class for reading/writing
 */
public class BeenfoClient implements ClientModInitializer
{
    
    public static BlockPos lastHiveResponseBlockPos = null;
    public static int lastHiveResponseHoneyLevel = 0;
    public static int lastHiveResponseBeeCount = 0;

    public static Identifier HUD_TEXTURE;

    @Environment(EnvType.CLIENT)
    @Override
    public void onInitializeClient()
    {
        HUD_TEXTURE = new Identifier(Beenfo.MOD_ID, "textures/gui/ingame.png");

        ClientPlayNetworking.registerGlobalReceiver(BeenfoServer.S2CPacketIdentifierOpen, this::onReceiveHiveInfoOpen);
        ClientPlayNetworking.registerGlobalReceiver(BeenfoServer.S2CPacketIdentifierHud, this::onReceiveHiveInfoHud);
    }

    @Environment(EnvType.CLIENT)
    private void onReceiveHiveInfoOpen(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buffer, PacketSender responseSender)
    {
        int honeyLevel;
        int beeCount;

        //ClientPlayerEntity player = client.player;
        List<String> beeNames = new ArrayList<String>();

        honeyLevel = buffer.readInt();
        beeCount = buffer.readInt();

        for (int i = 0; i < beeCount; i++)
        {
            String beeName = buffer.readString(); // buffer.readText();
            beeNames.add(beeName);
        }

        client.execute(() -> {
            client.setScreen(new BeenfoScreen(null, honeyLevel, beeNames));
        });
    }

    @Environment(EnvType.CLIENT)
    private void onReceiveHiveInfoHud(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buffer, PacketSender responseSender)
    {
        int packetVersion = buffer.readInt();
        if (packetVersion == 0)
        {
            lastHiveResponseHoneyLevel = buffer.readInt();
            lastHiveResponseBeeCount = buffer.readInt();
            lastHiveResponseBlockPos = buffer.readBlockPos();
        }
    }
}
