package eu.scillman.minecraft.beeinfo.gui;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.minecraft.text.Text;

public class OptionsMenu implements ModMenuApi
{
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory()
    {
        return screen -> new eu.scillman.minecraft.beeinfo.gui.OptionsScreen(Text.literal("Bee Information Tweaks"));
    }
}
