package com.github.scillman.minecraft.beeinfo.mixin;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.github.scillman.minecraft.beeinfo.BeeInfoServer;
import com.github.scillman.minecraft.beeinfo.nbt.NbtBeehive;

import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.Mixin;
import static net.minecraft.block.BeehiveBlock.HONEY_LEVEL;

@Mixin(AbstractBlock.class)
public class BlockUseMixin
{
    /**
     * Called when the player presses the use button when facing a Beehive/Bee Nest block.
     * @param state The state of the block the player is facing.
     * @param world The world the player resides in. (Overworld, Nether, The End)
     * @param pos The position of the block inside the world.
     * @param player The player that pressed the use button.
     * @param hit Information about whether the player hit the block and where at the block the player hit.
     * @remarks Gets called before the return statement of the BeehiveBlock.onUse function.
     */
    @Inject(method="onUse", at=@At(value="RETURN"))
    public void onUseHoneyBeeContainer(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit, CallbackInfoReturnable<?> ci)
    {
        if (world.isClient())
        {
            return;
        }

        if (player instanceof ServerPlayerEntity serverPlayer)
        {
            if (isPlayerEmptyHanded(serverPlayer))
            {
                if (state.isIn(BlockTags.BEEHIVES, statex -> statex.contains(HONEY_LEVEL)))
                {
                    NbtBeehive nbtBeehive = NbtBeehive.create(world, pos, state);
                    BeeInfoServer.sendBlockInfoToClient(serverPlayer, nbtBeehive.getHoneyLevel(), nbtBeehive.getBeeNames());
                }
            }
        }
    }

    /**
     * Determines if a player is empty handed.
     * @param player The player to check for if he is empty handed.
     * @return True if the player is empty handed; otherwise, false.
     */
    private boolean isPlayerEmptyHanded(ServerPlayerEntity player)
    {
        Item item = player.getStackInHand(Hand.MAIN_HAND).getItem();
        Block block = Block.getBlockFromItem(item);

        // Any item that is not a block will return Blocks.AIR
        return block == Blocks.AIR;
    }
}
