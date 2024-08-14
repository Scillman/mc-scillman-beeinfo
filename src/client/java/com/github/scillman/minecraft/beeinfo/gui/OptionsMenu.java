package com.github.scillman.minecraft.beeinfo.gui;

import com.github.scillman.minecraft.beeinfo.registry.ModConstants;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class OptionsMenu implements ModMenuApi
{
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory()
    {
        return screen -> new com.github.scillman.minecraft.beeinfo.gui.OptionsScreen(
            Text.translatable(ModConstants.MENU_OPTIONS_MENU.toTranslationKey(ModConstants.KEY_MENU))
        );
    }
}
