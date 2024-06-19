package com.github.scillman.minecraft.beeinfo.mixin.client;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.github.scillman.minecraft.beeinfo.BeeInfoClient;
import com.github.scillman.minecraft.beeinfo.config.ModSettings;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.OrderedText;
import net.minecraft.util.math.BlockPos;

import static net.minecraft.block.BeehiveBlock.HONEY_LEVEL;

@Mixin(InGameHud.class)
public class HudRenderMixin
{
    @Shadow
    @Final
    @Nullable
    private MinecraftClient client;

    /**
     * @brief Called just before InGameHud.renderStatusEffectOverlay returns to the caller.
     * @param matrices
     */
    @Inject(method="renderStatusEffectOverlay", at=@At("RETURN"))
    private void onRenderStatusEffectsOverlay(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci)
    {
        if (!ModSettings.getEnableHud())
        {
            return;
        }

        if (client == null || client.player == null || client.world == null)
        {
            return;
        }

        if (client.player.isSpectator())
        {
            return;
        }

        BlockPos blockPos = BeeInfoClient.lastHiveResponseBlockPos;

        // It is possible for a block to have been destroyed locally but not
        // yet having received new lookAt data. Therefor it is imperative
        // to check if the block is a honey bee container.
        if (blockPos == null || !isHoneyBeeContainer(blockPos))
        {
            return;
        }

        drawHud(context, tickCounter, client.world.getBlockState(blockPos));
    }

    /**
     * @brief Determines if the block is a honey bee container block.
     * @param blockPos The position of the block.
     * @return True if the block is a honey bee container; otherwise, false.
     */
    private boolean isHoneyBeeContainer(BlockPos blockPos)
    {
        BlockState blockState = client.world.getBlockState(blockPos);
        return blockState.contains(HONEY_LEVEL);
    }

    /**
     * Draws the HUD on the client side.
     * @param matrices
     * @see resources/assets/beeinfo/textures/gui/hud.png
     */
    private void drawHud(DrawContext context, RenderTickCounter tickCounter, BlockState blockState)
    {
        final int HUD_WIDTH = 82;
        final int HUD_HEIGHT = 59;

        // Offset on both axis to the top-left corner of the popup
        int x = (int) ((client.getWindow().getScaledWidth() - HUD_WIDTH) * ModSettings.getHudAxisX());
        int y = (int) ((client.getWindow().getScaledHeight() - HUD_HEIGHT) * ModSettings.getHudAxisY());

        // The texture to use for rendering.
        RenderSystem.setShaderTexture(0, BeeInfoClient.HUD_TEXTURE);

        // Draw the background texture
        context.drawTexture(BeeInfoClient.HUD_TEXTURE, x, y, 0, 0, HUD_WIDTH, HUD_HEIGHT);

        // Fills the honey slots with honey if the respective level is met
        int honey = BeeInfoClient.lastHiveResponseHoneyLevel; //blockState.get(HONEY_LEVEL);
        if (honey >= 1) context.drawTexture(BeeInfoClient.HUD_TEXTURE, x+17, y+16, 84, 17, 6, 7);
        if (honey >= 2) context.drawTexture(BeeInfoClient.HUD_TEXTURE, x+24, y+22, 84, 17, 6, 7);
        if (honey >= 3) context.drawTexture(BeeInfoClient.HUD_TEXTURE, x+31, y+16, 84, 17, 6, 7);
        if (honey >= 4) context.drawTexture(BeeInfoClient.HUD_TEXTURE, x+38, y+22, 84, 17, 6, 7);
        if (honey >= 5) context.drawTexture(BeeInfoClient.HUD_TEXTURE, x+51, y+16, 83, 34, 14, 13);

        // Draws the bees inside the hive based on the count
        drawBeeTexture(context, 0, x+14, y+37);
        drawBeeTexture(context, 1, x+34, y+37);
        drawBeeTexture(context, 2, x+54, y+37);

        // Draws the name of the block
        OrderedText orderedText = blockState.getBlock().getName().asOrderedText();
        context.drawText(client.textRenderer, orderedText, x + 41 - (client.textRenderer.getWidth(orderedText) / 2), y+5, 0x404040, false);
    }

    private void drawBeeTexture(DrawContext context, int index, int x, int y)
    {
        if (BeeInfoClient.lastHiveResponseBeeCount > index)
        {
            if (BeeInfoClient.lastHiveResponseChildCount > index)
            {
                context.drawTexture(BeeInfoClient.HUD_TEXTURE, x+1, y+1, 101, 3, 10, 10);
            }
            else
            {
                context.drawTexture(BeeInfoClient.HUD_TEXTURE, x, y, 83, 2, 13, 12);
            }
        }
    }
}
