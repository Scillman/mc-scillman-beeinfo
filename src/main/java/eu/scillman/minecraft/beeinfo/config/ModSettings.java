package eu.scillman.minecraft.beeinfo.config;

public class ModSettings extends Configuration
{
    private static ModSettings instance;

    private ModSettings()
    {

    }

    public static ModSettings getInstance()
    {
        if (instance == null)
        {
            instance = new ModSettings();
        }

        return instance;
    }

    @Override
    public void init(String modId)
    {
        super.init(modId);

        register("beeinfo.setting.enable_hud",  "beeinfo.hint.enable_hud",  null, false);
        register("beeinfo.setting.enable_menu", "beeinfo.hint.enable_menu", null, true);
        register("beeinfo.setting.hud_axis_x",  "beeinfo.hint.hud_axis_x",  null, 0.5f, 0.0f, 1.0f);
        register("beeinfo.setting.hud_axis_y",  "beeinfo.hint.hud_axis_y",  null, 0.5f, 0.0f, 1.0f);
    }
}
