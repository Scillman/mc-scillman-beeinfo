package com.github.scillman.minecraft.beeinfo.config;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import static com.github.scillman.minecraft.beeinfo.BeeInfo.KEY_SETTING_ENABLE_MENU;
import static com.github.scillman.minecraft.beeinfo.BeeInfo.KEY_SETTING_ENABLE_HUD;
import static com.github.scillman.minecraft.beeinfo.BeeInfo.KEY_SETTING_HUD_AXIS_X;
import static com.github.scillman.minecraft.beeinfo.BeeInfo.KEY_SETTING_HUD_AXIS_Y;

import static com.github.scillman.minecraft.beeinfo.BeeInfo.HINT_SETTING_ENABLE_MENU;
import static com.github.scillman.minecraft.beeinfo.BeeInfo.HINT_SETTING_ENABLE_HUD;
import static com.github.scillman.minecraft.beeinfo.BeeInfo.HINT_SETTING_HUD_AXIS_X;
import static com.github.scillman.minecraft.beeinfo.BeeInfo.HINT_SETTING_HUD_AXIS_Y;

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

        register(KEY_SETTING_ENABLE_MENU, HINT_SETTING_ENABLE_MENU, null, false);
        register(KEY_SETTING_ENABLE_HUD,  HINT_SETTING_ENABLE_HUD,  null, true);
        register(KEY_SETTING_HUD_AXIS_X,  HINT_SETTING_HUD_AXIS_X,  null, 0.63f, 0.0f, 1.0f);
        register(KEY_SETTING_HUD_AXIS_Y,  HINT_SETTING_HUD_AXIS_Y,  null, 0.63f, 0.0f, 1.0f);
    }
}
