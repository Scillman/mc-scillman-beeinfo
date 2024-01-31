package eu.scillman.minecraft.beeinfo;

import org.jetbrains.annotations.Nullable;

import eu.scillman.minecraft.beeinfo.config.ModSettings;
import eu.scillman.minecraft.beeinfo.gui.InGameMenu;
import eu.scillman.minecraft.beeinfo.network.PacketHUD;
import eu.scillman.minecraft.beeinfo.network.PacketMenu;
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

public class BeeInfoClient implements ClientModInitializer
{
    @Nullable
    public static BlockPos lastHiveResponseBlockPos = null;
    public static int lastHiveResponseHoneyLevel = 0;
    public static int lastHiveResponseBeeCount = 0;

    /**
     * @brief The texture used for rendering the HUD.
     */
    public static Identifier HUD_TEXTURE;

    /**
     * @brief Called when initializing the client-side of the mod.
     * @remarks Not called for the server.jar
     */
    @Environment(EnvType.CLIENT)
    @Override
    public void onInitializeClient()
    {
        HUD_TEXTURE = new Identifier(BeeInfo.MOD_ID, "textures/gui/hud.png");

        ClientPlayNetworking.registerGlobalReceiver(BeeInfo.PACKET_ID_MENU, BeeInfoClient::onReceiveContainerInfoMenu);
        ClientPlayNetworking.registerGlobalReceiver(BeeInfo.PACKET_ID_HUD, BeeInfoClient::onReceiveContainerInfoHud);

        ModSettings.init(BeeInfo.MOD_ID);
        ModSettings.load();
    }

    /**
     * @brief Called when a packet was received containing the information to display inside the menu.
     * @param client The client that has received the packet.
     * @param handler The client's networking handler.
     * @param buffer The data that has been received from the server.
     * @param responseSender The socket to use to send information back to the server.
     */
    @Environment(EnvType.CLIENT)
    private static void onReceiveContainerInfoMenu(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buffer, PacketSender responseSender)
    {
        PacketMenu packet = PacketMenu.decode(buffer);
        client.execute(() -> {
            client.setScreen(new InGameMenu(packet.honeyLevel, packet.beeNames));
        });
    }

    /**
     * @brief Called when a packet was received containing information to display inside the HUD.
     * @param client  The client that has received the packet.
     * @param handler The client's networking handler.
     * @param buffer The data thas has been received from the server.
     * @param responseSender The socket to use to send information back to the server.
     */
    @Environment(EnvType.CLIENT)
    private static void onReceiveContainerInfoHud(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buffer, PacketSender responseSender)
    {
        PacketHUD packet = PacketHUD.decode(buffer);

        lastHiveResponseHoneyLevel = packet.honeyLevel;
        lastHiveResponseBeeCount = packet.beeCount;
        lastHiveResponseBlockPos = packet.blockPos;
    }

    /**
     * @brief Call to reset the lookat block of the client.
     */
    public static void resetLookAtBlock()
    {
        lastHiveResponseBlockPos = null;
    }
}
