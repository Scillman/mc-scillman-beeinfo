package eu.scillman.minecraft.beeinfo.mixin;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import eu.scillman.minecraft.beeinfo.BeeInfo;
import eu.scillman.minecraft.beeinfo.BeeInfoClient;
import eu.scillman.minecraft.beeinfo.network.PacketLookAt;

import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import static net.minecraft.block.BeehiveBlock.HONEY_LEVEL;

@Mixin(MinecraftClient.class)
public class LookAtMixin
{
    /**
     * @brief The target the player is looking at.
     */
    @Shadow
    @Nullable
    public HitResult crosshairTarget;

    /**
     * @brief The player.
     */
    @Shadow
    @Nullable
    public ClientPlayerEntity player;

    /**
     * @brief The world the player is in.
     * @remarks E.g. Overworld, Nether, The End
     */
    @Shadow
    @Nullable
    public ClientWorld world;

    /**
     * @brief The last time a notification has been send to the server.
     * @remarks Local machine time in ms when last send.
     */
    private long lastUpdateTime = 0;

    /**
     * @brief The position of the block the server was last notified of.
     */
    @Nullable
    private BlockPos lastUpdateBlockPos = null;

    /**
     * @brief Called when the render has ended.
     * @param tick N/A
     * @param ci Mixin callback information.
     */
    @Inject(method="render", at=@At("RETURN"))
    private void onRender(boolean tick, CallbackInfo ci)
    {
        if (crosshairTarget == null || player == null || world == null)
        {
            return;
        }

        if (isHoneyBeeContainer(crosshairTarget))
        {
            // Notifies the server about the honey bee container
            // the player is looking at; in turn the server will
            // send the information about the block and its
            // contents back to the client.
            notifyLookAtHoneyBeeContainer(crosshairTarget);
        }
        else
        {
            // The player does not look at a honey bee container.
            // Instead of sending a request to update the value,
            // reset the local variables instead.
            BeeInfoClient.resetLookAtBlock();
        }
    }

    /**
     * @brief Determines if the player is looking at a honey bee container.
     * @param crosshairTarget The crosshair target.
     * @return True if the player is looking at a honey bee container; otherwise, false.
     */
    private boolean isHoneyBeeContainer(HitResult crosshairTarget)
    {
        if (crosshairTarget.getType() != HitResult.Type.BLOCK)
        {
            return false;
        }

        BlockHitResult targetBlock = ((BlockHitResult)(crosshairTarget));
        BlockPos blockPos = targetBlock.getBlockPos();
        BlockState blockState = world.getBlockState(blockPos);

        return blockState.contains(HONEY_LEVEL);
    }

    /**
     * @brief Notifies the server about the honey bee block the player is looking at.
     * @param crosshairTarget The target the player is looking at.
     * @remarks When called the player is confirmed to be looking at a honey bee container.
     */
    private void notifyLookAtHoneyBeeContainer(HitResult crosshairTarget)
    {
        BlockPos blockPos = ((BlockHitResult)(crosshairTarget)).getBlockPos();

        if (!shouldSendUpdate(blockPos))
        {
            return;
        }

        lastUpdateTime = System.currentTimeMillis();
        lastUpdateBlockPos = blockPos;

        PacketLookAt packet = PacketLookAt.encode(blockPos);
        ClientPlayNetworking.send(BeeInfo.PACKET_ID_LOOKAT, packet);
    }

    /**
     * @brief Determines whether an update has to be send to the server.
     * @param blockPos The position of the block the player is looking at.
     * @return True if an update has to be send; otherwise, false.
     */
    private boolean shouldSendUpdate(BlockPos blockPos)
    {
        long now = System.currentTimeMillis();
        if (now > (lastUpdateTime + 100)) // 0.1 second = 2 game ticks
        {
            return true;
        }

        return !blockPos.equals(lastUpdateBlockPos);
    }

}
