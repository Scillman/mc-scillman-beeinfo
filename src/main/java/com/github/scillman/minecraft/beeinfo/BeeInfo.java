package com.github.scillman.minecraft.beeinfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.util.Identifier;

public class BeeInfo
{
	public static final String MOD_ID = "beeinfo";
	public static final String MOD_NAME = "BeeInfo";

	public static final Identifier PACKET_ID_LOOKAT = Identifier.of(MOD_ID, "client/lookat");
	public static final Identifier PACKET_ID_MENU   = Identifier.of(MOD_ID, "server/menu");
    public static final Identifier PACKET_ID_HUD    = Identifier.of(MOD_ID, "server/hud");

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static final String KEY_SETTING_ENABLE_MENU = (MOD_ID + ".setting.enable_menu");
    public static final String KEY_SETTING_ENABLE_HUD  = (MOD_ID + ".setting.enable_hud");
    public static final String KEY_SETTING_HUD_AXIS_X  = (MOD_ID + ".setting.hud_axis_x");
    public static final String KEY_SETTING_HUD_AXIS_Y  = (MOD_ID + ".setting.hud_axis_y");

    public static final String HINT_SETTING_ENABLE_MENU  = (MOD_ID + ".hint.enable_menu");
    public static final String HINT_SETTING_ENABLE_HUD   = (MOD_ID + ".hint.enable_hud");
    public static final String HINT_SETTING_HUD_AXIS_X   = (MOD_ID + ".hint.hud_axis_x");
    public static final String HINT_SETTING_HUD_AXIS_Y   = (MOD_ID + ".hint.hud_axis_y");
}
