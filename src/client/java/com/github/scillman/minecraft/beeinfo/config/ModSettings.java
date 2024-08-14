package com.github.scillman.minecraft.beeinfo.config;

import com.github.scillman.minecraft.beeinfo.registry.ModConstants;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public class ModSettings
{
    private static ModSettingsHandler handler;

    private ModSettings()
    {
        super();
    }

    private static ModSettingsHandler getHandler()
    {
        if (handler == null)
        {
            handler = new ModSettingsHandler();
        }
        return handler;
    }

    public static void init(String modId)
    {
        getHandler().init(modId);
    }

    public static void load()
    {
        getHandler().load();
    }

    public static void save()
    {
        getHandler().save();
    }

    public static Boolean getEnableMenu()
    {
        return getHandler().get(ModConstants.SETTING_ENABLE_MENU);
    }

    public static void setEnableMenu(Boolean enable)
    {
        getHandler().set(ModConstants.SETTING_ENABLE_MENU, enable);
    }

    public static void resetEnableMenu()
    {
        getHandler().reset(ModConstants.SETTING_ENABLE_MENU);
    }

    public static Boolean getEnableHud()
    {
        return getHandler().get(ModConstants.SETTING_ENABLE_HUD);
    }

    public static void setEnableHud(Boolean enable)
    {
        getHandler().set(ModConstants.SETTING_ENABLE_HUD, enable);
    }

    public static void resetEnableHud()
    {
        getHandler().reset(ModConstants.SETTING_ENABLE_HUD);
    }

    public static Float getHudAxisXMax()
    {
        return getHandler().max(ModConstants.SETTING_HUD_AXIS_X);
    }

    public static Float getHudAxisXMin()
    {
        return getHandler().min(ModConstants.SETTING_HUD_AXIS_X);
    }

    public static Float getHudAxisX()
    {
        return getHandler().get(ModConstants.SETTING_HUD_AXIS_X);
    }

    public static void setHudAxisX(Float value)
    {
        getHandler().set(ModConstants.SETTING_HUD_AXIS_X, value);
    }

    public static void resetHudAxisX()
    {
        getHandler().reset(ModConstants.SETTING_HUD_AXIS_X);
    }

    public static Float getHudAxisYMax()
    {
        return getHandler().max(ModConstants.SETTING_HUD_AXIS_Y);
    }

    public static Float getHudAxisYMin()
    {
        return getHandler().min(ModConstants.SETTING_HUD_AXIS_Y);
    }

    public static Float getHudAxisY()
    {
        return getHandler().get(ModConstants.SETTING_HUD_AXIS_Y);
    }

    public static void setHudAxisY(Float value)
    {
        getHandler().set(ModConstants.SETTING_HUD_AXIS_Y, value);
    }

    public static void resetHudAxisY()
    {
        getHandler().reset(ModConstants.SETTING_HUD_AXIS_Y);
    }
}
