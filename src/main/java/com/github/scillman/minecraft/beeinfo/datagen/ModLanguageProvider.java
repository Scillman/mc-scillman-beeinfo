package com.github.scillman.minecraft.beeinfo.datagen;

import java.util.concurrent.CompletableFuture;

import com.github.scillman.minecraft.beeinfo.registry.ModConstants;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.util.Identifier;

public class ModLanguageProvider extends FabricLanguageProvider
{
    protected ModLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<WrapperLookup> registryLookup)
    {
        super(dataOutput, "en_us", registryLookup);
    }

    @Override
    public void generateTranslations(WrapperLookup registryLookup, TranslationBuilder translationBuilder)
    {
        addSetting(translationBuilder, ModConstants.KEY_SETTING_ENABLE_MENU, "Enable Menu");
        addSetting(translationBuilder, ModConstants.KEY_SETTING_ENABLE_HUD, "Enable HUD");
        addSetting(translationBuilder, ModConstants.KEY_SETTING_HUD_AXIS_X, "HUD X-offset");
        addSetting(translationBuilder, ModConstants.KEY_SETTING_HUD_AXIS_Y, "HUD Y-offset");

        addTooltip(translationBuilder, ModConstants.TOOLTIP_BEES_BABY, "§6%d Baby Bees");
        addTooltip(translationBuilder, ModConstants.TOOLTIP_BEES_ADULT, "§6%d Adult Bees");
        addTooltip(translationBuilder, ModConstants.TOOLTIP_BEES, "§6%d Bees");
        addTooltip(translationBuilder, ModConstants.TOOLTIP_HONEY, "§6%d Honey");

        addMenu(translationBuilder, ModConstants.MENU_OPTIONS_MENU, ""); // Bee Information Tweaks
        addMenu(translationBuilder, ModConstants.MENU_INGAME_MENU, ""); // In-Game Menu
        addMenu(translationBuilder, ModConstants.MENU_BUTTON_DONE, "Done");
    }

    private void addSetting(TranslationBuilder builder, Identifier id, String value)
    {
        builder.add(id.toTranslationKey(ModConstants.KEY_SETTING), value);
    }

    private void addTooltip(TranslationBuilder builder, Identifier id, String value)
    {
        builder.add(id.toTranslationKey(ModConstants.KEY_TOOLTIP), value);
    }

    private void addMenu(TranslationBuilder builder, Identifier id, String value)
    {
        builder.add(id.toTranslationKey(ModConstants.KEY_MENU), value);
    }
}
