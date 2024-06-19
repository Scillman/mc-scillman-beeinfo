package com.github.scillman.minecraft.beeinfo.nbt;

import java.util.ArrayList;
import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static com.github.scillman.minecraft.beeinfo.BeeInfo.LOGGER;

/**
 * Represents a BeehiveBlockEntity in NBT data format.
 */
public class NbtBeehive
{
    private int honeyLevel;
    private ArrayList<NbtBee> bees;

    private NbtBeehive(int honeyLevel, ArrayList<NbtBee> bees)
    {
        this.honeyLevel = honeyLevel;
        this.bees = bees;
    }

    /**
     * Create a new NbtBeehive instance.
     * @param world The world in which the block resides.
     * @param blockPos The position of the block inside the world.
     * @param blockState The current block state of the target block.
     */
    public static NbtBeehive create(World world, BlockPos blockPos, BlockState blockState)
    {
        NbtBeehive beehive = new NbtBeehive(0, new ArrayList<NbtBee>());
        beehive.readNbt(world, blockPos, blockState);
        return beehive;
    }

    /**
     * Read the NBT data of a beehive tagged block.
     * @param world The world in which the block resides.
     * @param blockPos The position of the block inside the world.
     * @param blockState The current block state of the target block.
     */
    private void readNbt(World world, BlockPos blockPos, BlockState blockState)
    {
        this.honeyLevel = 0;
        this.bees.clear();

        if (blockState.isIn(BlockTags.BEEHIVES, statex -> statex.contains(BeehiveBlock.HONEY_LEVEL)))
        {
            this.honeyLevel = BeehiveBlockEntity.getHoneyLevel(blockState);
        }

        BlockEntity entity = world.getBlockEntity(blockPos);
        NbtCompound nbt = entity.createNbt(world.getRegistryManager());

        final String BEES_KEY = "bees";
        if (nbt.contains(BEES_KEY))
        {
            NbtBee.LIST_CODEC.parse(NbtOps.INSTANCE, nbt.get(BEES_KEY))
                .resultOrPartial(string -> LOGGER.error("Failed to parse bees: '{}'", string))
                .ifPresent(list -> list.forEach(this::addBee));
        }
    }

    /**
     * Get the honey level of the beehive.
     * @return The honey level of the beehive.
     */
    public int getHoneyLevel()
    {
        return this.honeyLevel;
    }

    /**
     * Add a bee to the beehive NBT data.
     * @param bee The bee to add to the beehive NBT data.
     */
    public void addBee(NbtBee bee)
    {
        this.bees.add(bee);
    }

    /**
     * Get the names of all the bees inside the beehive.
     * @return A list containing all the names of the bees inside the beehive.
     */
    public ArrayList<String> getBeeNames()
    {
        ArrayList<String> names = new ArrayList<>();

        for (NbtBee bee: this.bees)
        {
            names.add(bee.getName());
        }

        return names;
    }

    /**
     * Get the number of bees inside the beehive.
     * @return The number of bees inside the beehive.
     */
    public int getBeeCount()
    {
        return this.bees.size();
    }

    /**
     * Represents a BeeEntity in NBT data format.
     * @remarks The NBT data is in the format of when the BeeEntity is inside a Beehive tagged block.
     */
    public record NbtBee(NbtComponent entityData)
    {
        public static final Codec<NbtBee> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            NbtComponent.CODEC.optionalFieldOf("entity_data", NbtComponent.DEFAULT).forGetter(NbtBee::entityData)
        ).apply(instance, NbtBee::new));
        public static final Codec<List<NbtBee>> LIST_CODEC = CODEC.listOf();

        /**
         * Get the name of the bee.
         * @return The name of the bee.
         */
        public String getName()
        {
            final String CUSTOM_NAME = "CustomName";
            NbtCompound nbt = this.entityData.copyNbt();
            return nbt.contains(CUSTOM_NAME, NbtElement.STRING_TYPE) ? nbt.getString(CUSTOM_NAME) : "";
        }
    }
}
