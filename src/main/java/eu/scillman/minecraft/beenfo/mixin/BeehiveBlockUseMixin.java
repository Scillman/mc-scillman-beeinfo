package eu.scillman.minecraft.beenfo.mixin;

import eu.scillman.minecraft.beenfo.BeenfoServer;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.Mixin;
import static net.minecraft.block.BeehiveBlock.HONEY_LEVEL;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BeehiveBlock.class)
public class BeehiveBlockUseMixin
{
    /**
     * Called when the player presses the use button when facing a Beehive block.
     * @param state The state of the block the player is facing.
     * @param world The world the player resides in. (Overworld, Nether, The End)
     * @param pos The position of the block inside the world.
     * @param player The player that pressed the use button.
     * @param hand The hand of the player.
     * @param hit Information about whether the player hit the block and where at the block the player hit.
     * @remarks Gets called before the return statement of the BeehiveBlock.onUse function.
     */
    @Inject(method="onUse", at=@At(value="RETURN", ordinal=1))
    public void onUseStick(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<?> ci)
    {
        if (player instanceof ServerPlayerEntity serverPlayer)
        {
            if (isPlayerEmptyHanded(serverPlayer, hand))
            {
                int honey = state.get(HONEY_LEVEL);
                @Nullable NbtList tag = null;

                BlockEntity entity = world.getBlockEntity(pos);
                if (entity instanceof BeehiveBlockEntity bbe)
                {
                    tag = bbe.getBees();
                }

                BeenfoServer.sendBlockInfo(serverPlayer, honey, tag);
            }
        }
    }

    /**
     * Determines if a player is empty handed.
     * @param player The player to check for if he is empty handed.
     * @param hand The hand of the player.
     * @return True if the player is empty handed; otherwise, false.
     */
    private boolean isPlayerEmptyHanded(ServerPlayerEntity player, Hand hand)
    {
        Item item = player.getStackInHand(hand).getItem();
        Block block = Block.getBlockFromItem(item);

        // Any item that is not a block will return Blocks.AIR
        return block == Blocks.AIR;
    }
}
