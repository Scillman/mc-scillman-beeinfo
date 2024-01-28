package eu.scillman.minecraft.beenfo;

import eu.scillman.minecraft.beenfo.network.BeenfoPacketHUD;
import eu.scillman.minecraft.beenfo.network.BeenfoPacketMenu;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

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
        HUD_TEXTURE = new Identifier(Beenfo.MOD_ID, "textures/gui/hud.png");

        ClientPlayNetworking.registerGlobalReceiver(BeenfoServer.S2CPacketIdentifierMenu, this::onReceiveHiveInfoMenu);
        ClientPlayNetworking.registerGlobalReceiver(BeenfoServer.S2CPacketIdentifierHud, this::onReceiveHiveInfoHud);
    }

    @Environment(EnvType.CLIENT)
    private void onReceiveHiveInfoMenu(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buffer, PacketSender responseSender)
    {
        BeenfoPacketMenu packet = BeenfoPacketMenu.decode(buffer);
        client.execute(() -> {
            client.setScreen(new BeenfoScreen(null, packet.honeyLevel, packet.beeNames));
        });
    }

    @Environment(EnvType.CLIENT)
    private void onReceiveHiveInfoHud(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buffer, PacketSender responseSender)
    {
        BeenfoPacketHUD packet = BeenfoPacketHUD.decode(buffer);

        lastHiveResponseHoneyLevel = packet.honeyLevel;
        lastHiveResponseBeeCount = packet.beeCount;
        lastHiveResponseBlockPos = packet.blockPos;
    }
}
