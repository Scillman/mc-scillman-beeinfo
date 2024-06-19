package com.github.scillman.minecraft.beeinfo;

import org.jetbrains.annotations.Nullable;

import com.github.scillman.minecraft.beeinfo.config.ModSettings;
import com.github.scillman.minecraft.beeinfo.gui.InGameMenu;
import com.github.scillman.minecraft.beeinfo.network.PacketHUD;
import com.github.scillman.minecraft.beeinfo.network.PacketMenu;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class BeeInfoClient implements ClientModInitializer
{
   @Nullable
    public static BlockPos lastHiveResponseBlockPos = null;
    public static int lastHiveResponseHoneyLevel = 0;
    public static int lastHiveResponseBeeCount = 0;
    public static int lastHiveResponseBabyBeeCount = 0;

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
        HUD_TEXTURE = Identifier.of(BeeInfo.MOD_ID, "textures/gui/hud.png");

        ModSettings.init(BeeInfo.MOD_ID);
        ModSettings.load();

        ClientPlayNetworking.registerGlobalReceiver(PacketHUD.ID, BeeInfoClient::new_onReceiveContainerInfoHud);
        ClientPlayNetworking.registerGlobalReceiver(PacketMenu.ID, BeeInfoClient::new_onReceiveContainerInfoMenu);
    }

    /**
     * @brief Called when a payload was received containing the information to display inside the menu.
     * @param payload The received payload.
     * @param context The networking context.
     */
    @Environment(EnvType.CLIENT)
    private static void new_onReceiveContainerInfoMenu(PacketMenu payload, ClientPlayNetworking.Context context)
    {
        context.client().execute(() -> {
            if (ModSettings.getEnableMenu())
            {
                context.client().setScreen(new InGameMenu(payload.getHoneyLevel(), payload.getBeeCount(), payload.getBeeNames()));
            }
        });
    }

    /**
     * @brief Called when a payload was received containing information to display inside the HUD.
     * @param payload The received payload.
     * @param context The networking context.
     */
    @Environment(EnvType.CLIENT)
    private static void new_onReceiveContainerInfoHud(PacketHUD payload, ClientPlayNetworking.Context context)
    {
        context.client().execute(() -> {
            lastHiveResponseHoneyLevel = payload.getHoneyLevel();
            lastHiveResponseBeeCount = payload.getBeeCount();
            lastHiveResponseBabyBeeCount = payload.getBabyBeeCount();
            lastHiveResponseBlockPos = payload.getBlockPos();
        });
    }

    /**
     * @brief Call to reset the lookat block of the client.
     */
    public static void resetLookAtBlock()
    {
        lastHiveResponseBlockPos = null;
    }
}
