package eu.scillman.minecraft.beenfo.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import eu.scillman.minecraft.beenfo.BeenfoClient;
import eu.scillman.minecraft.beenfo.BeenfoServer;
import eu.scillman.minecraft.beenfo.network.BeenfoPacketLookAt;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.OrderedText;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import static net.minecraft.block.BeehiveBlock.HONEY_LEVEL;

@Mixin(InGameHud.class)
public class HiveInfoRenderMixin extends DrawableHelper
{
    /**
     * TODO:
     *   Ensure this matches the default Minecraft interaction distance.
     */
    private static final double BLOCK_INTERACTION_DISTANCE = 20.0d;

    /**
     * @brief A reference to InGameHud.MinecraftClient client
     */
    @Shadow
    @Final
    private MinecraftClient client;

    /**
     * @brief The last time a notification/request has been send to the server.
     * @remarks Format is milliseconds in local machine time.
     */
    private long lastHiveRequestTime = 0;
    
    /**
     * @brief The block that was last interacted with.
     */
    private @Nullable BlockPos lastHiveRequestBlockPos = null;

    /**
     * @brief Called just before InGameHud.renderStatusEffectOverlay returns to the caller.
     * @param matrices
     */
    @Inject(method="renderStatusEffectOverlay", at=@At("RETURN"))
    private void onRenderStatusEffectsOverlay(MatrixStack matrices, CallbackInfo ci)
    {
        Entity entity = client.getCameraEntity();
        HitResult blockHit = entity.raycast(BLOCK_INTERACTION_DISTANCE, 0, false);
        
        // NOTE: The raycast should never return NULL, double check before removing
        //ASSERT(blockHit != null);
        if (blockHit == null) 
        {
            return;
        }

        if (blockHit.getType() != HitResult.Type.BLOCK)
        {
            return;
        }

        BlockPos blockPos = ((BlockHitResult)(blockHit)).getBlockPos();
        BlockState blockState = client.world.getBlockState(blockPos);

        // This allows both Beehive and Bee Nest, as well as potential future additions.
        if (!blockState.contains(HONEY_LEVEL))
        {
            return;
        }

        notifyServer(blockPos);
        drawHud(matrices, blockState);
    }

    /**
     * @brief Notifies the server that the client interacting with a beehive/beenest
     * @param blockPos The position of the block that the player is interacting with.
     */
    private void notifyServer(BlockPos blockPos)
    {
        long now = System.currentTimeMillis();
        boolean sameBlock = blockPos.equals(lastHiveRequestBlockPos);

        if (now > (lastHiveRequestTime + 100) || !sameBlock)
        {
            if (!sameBlock)
            {
                BeenfoClient.lastHiveResponseBeeCount = 0;
            }
    
            lastHiveRequestBlockPos = blockPos;
            lastHiveRequestTime = now;
    
            BeenfoPacketLookAt packet = BeenfoPacketLookAt.encode(blockPos);
            ClientPlayNetworking.send(BeenfoServer.C2SPacketIdentifierLookAt, packet);
        }
    }

    /**
     * Draws the HUD on the client side.
     * @param matrices
     * @param blockState
     * @see resources/assets/beenfo/textures/gui/hud.png
     */
    private void drawHud(MatrixStack matrices, BlockState blockState)
    {
        final int HUD_WIDTH = 82;
        final int HUD_HEIGHT = 59;

        // The texture to use for rendering.
        RenderSystem.setShaderTexture(0, BeenfoClient.HUD_TEXTURE);

        // Draw the background texture
        int x = (client.getWindow().getScaledWidth() - HUD_WIDTH) * 35 / 100; // TODO: update this value
        int y = (client.getWindow().getScaledHeight() - HUD_HEIGHT) * 35 / 100; // TODO: update this value
        drawTexture(matrices, x, y, 0, 0, HUD_WIDTH, HUD_HEIGHT);

        // Fills the honey slots with honey if the respective level is met
        int honey = blockState.get(HONEY_LEVEL);
        if (honey >= 1) drawTexture(matrices, x+17, y+16, 84, 17, 6, 7);
        if (honey >= 2) drawTexture(matrices, x+24, y+22, 84, 17, 6, 7);
        if (honey >= 3) drawTexture(matrices, x+31, y+16, 84, 17, 6, 7);
        if (honey >= 4) drawTexture(matrices, x+38, y+22, 84, 17, 6, 7);
        if (honey >= 5) drawTexture(matrices, x+51, y+16, 83, 34, 14, 13);

        // Draws the bees inside the hive based on the count
        if (BeenfoClient.lastHiveResponseBeeCount >= 1) drawTexture(matrices, x+14, y+37, 83, 2, 13, 12);
        if (BeenfoClient.lastHiveResponseBeeCount >= 2) drawTexture(matrices, x+34, y+37, 83, 2, 13, 12);
        if (BeenfoClient.lastHiveResponseBeeCount >= 3) drawTexture(matrices, x+54, y+37, 83, 2, 13, 12);
    
        // Draws the name of the block (TODO: check if this supports custom names)
        OrderedText orderedText = blockState.getBlock().getName().asOrderedText();
        client.textRenderer.draw(matrices, orderedText, (float)(
            x + 41 - (client.textRenderer.getWidth(orderedText) / 2)
        ), y+5, 0x404040);
    }
}
