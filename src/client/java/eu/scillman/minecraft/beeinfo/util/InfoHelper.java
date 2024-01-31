package eu.scillman.minecraft.beeinfo.util;

import static net.minecraft.block.BeehiveBlock.HONEY_LEVEL;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.block.BlockState;

public class InfoHelper
{
    // It is merely a helper class
    private InfoHelper() { }

    public static int getHoneyLevel(BlockState blockState)
    {
        if (blockState.contains(HONEY_LEVEL))
        {
            return blockState.get(HONEY_LEVEL);
        }

        return 0;
    }

    public static int getHoneyLevel(NbtCompound blockState)
    {
        if (blockState.contains("honey_level"))
        {
            // TODO:
            //   The original states MC 1.15 may return this as a string, double check to be sure
            return blockState.getInt("honey_level");
        }

        return 0;
    }
}
