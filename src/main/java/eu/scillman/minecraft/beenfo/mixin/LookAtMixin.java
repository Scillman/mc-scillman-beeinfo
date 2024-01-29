package eu.scillman.minecraft.beenfo.mixin;

import eu.scillman.minecraft.beenfo.BeenfoServer;
import eu.scillman.minecraft.beenfo.network.BeenfoPacketLookAt;
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
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import static net.minecraft.block.BeehiveBlock.HONEY_LEVEL;

@Mixin(MinecraftClient.class)
public class LookAtMixin
{
    @Shadow
    @Nullable
    public HitResult crosshairTarget;

    @Shadow
    @Nullable
    public ClientPlayerEntity player;

    @Shadow
    @Nullable
    public ClientWorld world;

    private long lastUpdateTime = 0;

    @Nullable
    private BlockPos lastUpdateBlockPos = null;

    /**
     * @brief Called when the render has ended.
     * @param tick Whether to
     * @param ci
     */
    @Inject(method="render", at=@At("RETURN"))
    private void onRender(boolean tick, CallbackInfo ci)
    {
        if (/*tick == false ||*/ crosshairTarget == null || player == null || world == null)
        {
            return;
        }

        if (crosshairTarget.getType() != HitResult.Type.BLOCK)
        {
            return;
        }

        BlockHitResult result = ((BlockHitResult)(crosshairTarget));
        BlockPos blockPos = result.getBlockPos();
        BlockState blockState = world.getBlockState(blockPos);

        if (!blockState.contains(HONEY_LEVEL))
        {
            return;
        }

        notifyServer(blockPos);
    }

    /**
     * @brief Notifies the server about the block the player is looking at.
     * @param blockPos The position of the block the player is looking at.
     */
    private void notifyServer(BlockPos blockPos)
    {
        if (!shouldSendUpdate(blockPos))
        {
            return;
        }

        lastUpdateTime = System.currentTimeMillis();
        lastUpdateBlockPos = blockPos;

        BeenfoPacketLookAt packet = BeenfoPacketLookAt.encode(blockPos);
        ClientPlayNetworking.send(BeenfoServer.C2SPacketIdentifierLookAt, packet);
    }

    /**
     * @brief Determines whether an update has to be send to the server.
     * @param blockPos The position of the block the player is looking at.
     * @return True if an update has to be send; otherwise, false.
     */
    private boolean shouldSendUpdate(BlockPos blockPos)
    {
        long now = System.currentTimeMillis();
        if (now > (lastUpdateTime + 100))
        {
            return true;
        }

        return !blockPos.equals(lastUpdateBlockPos);
    }

}
