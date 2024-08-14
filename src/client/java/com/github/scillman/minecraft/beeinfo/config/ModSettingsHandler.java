package com.github.scillman.minecraft.beeinfo.config;

import com.github.scillman.minecraft.beeinfo.registry.ModConstants;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public class ModSettingsHandler extends Configuration
{
    public ModSettingsHandler()
    {
        super();
    }

    @Override
    public void init(String modId)
    {
        super.init(modId);

        register(ModConstants.KEY_SETTING_ENABLE_MENU, null, false);
        register(ModConstants.KEY_SETTING_ENABLE_HUD,  null, true);
        register(ModConstants.KEY_SETTING_HUD_AXIS_X,  null, 0.63f, 0.0f, 1.0f);
        register(ModConstants.KEY_SETTING_HUD_AXIS_Y,  null, 0.63f, 0.0f, 1.0f);
    }
}
