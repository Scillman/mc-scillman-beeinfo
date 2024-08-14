package com.github.scillman.minecraft.beeinfo.registry;

import static com.github.scillman.minecraft.beeinfo.ModMain.MOD_ID;

import net.minecraft.util.Identifier;

public final class ModConstants
{
    public static final Identifier PACKET_ID_LOOKAT = Identifier.of(MOD_ID, "client/lookat");
    public static final Identifier PACKET_ID_MENU   = Identifier.of(MOD_ID, "server/menu");
    public static final Identifier PACKET_ID_HUD    = Identifier.of(MOD_ID, "server/hud");

    public static final String KEY_SETTING = "setting";
    public static final Identifier KEY_SETTING_ENABLE_MENU = Identifier.of(MOD_ID, "enable_menu");
    public static final Identifier KEY_SETTING_ENABLE_HUD  = Identifier.of(MOD_ID, "enable_hud");
    public static final Identifier KEY_SETTING_HUD_AXIS_X  = Identifier.of(MOD_ID, "hud_axis_x");
    public static final Identifier KEY_SETTING_HUD_AXIS_Y  = Identifier.of(MOD_ID, "hud_axis_y");

    public static final String KEY_TOOLTIP = "tooltip";
    public static final Identifier TOOLTIP_BEES_BABY    = Identifier.of(MOD_ID, "bees_baby");
    public static final Identifier TOOLTIP_BEES_ADULT   = Identifier.of(MOD_ID, "bees_adult");
    public static final Identifier TOOLTIP_BEES         = Identifier.of(MOD_ID, "bees");
    public static final Identifier TOOLTIP_HONEY        = Identifier.of(MOD_ID, "honey");

    public static final String KEY_MENU = "menu";
    public static final Identifier MENU_OPTIONS_MENU    = Identifier.of(MOD_ID, "options_menu");
    public static final Identifier MENU_INGAME_MENU     = Identifier.of(MOD_ID, "ingame_menu");
    public static final Identifier MENU_BUTTON_DONE     = Identifier.of(MOD_ID, "button_done");
}
