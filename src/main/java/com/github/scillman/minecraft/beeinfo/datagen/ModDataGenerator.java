package com.github.scillman.minecraft.beeinfo.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator.Pack;

/**
 * The mod's (Json) data generator.
 */
public class ModDataGenerator implements DataGeneratorEntrypoint
{
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator)
    {
        Pack pack = fabricDataGenerator.createPack();

        pack.addProvider(ModLanguageProvider::new);
    }
}
