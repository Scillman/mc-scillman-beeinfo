package com.github.scillman.minecraft.beeinfo.gui;

import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class PercentageSliderWidget extends SliderWidget
{
    private UpdateCallback callback;
    private Text text;

    public PercentageSliderWidget(int x, int y, int width, int height, Text text, double value, UpdateCallback callback)
    {
        super(x, y, width, height, Text.literal(""), value);
        this.callback = callback;
        this.text = text;

        updateMessage();
    }

    @Override
    protected void updateMessage()
    {
        int value = (int) Math.round(this.value * 100.0d);
        String percentage = I18n.translate(": %d%%", value);
        MutableText text = this.text.copy();
        setMessage(text.append(percentage));
    }

    @Override
    protected void applyValue()
    {
        callback.onValueChange(this, value);
    }

    @Environment(value=EnvType.CLIENT)
    public static interface UpdateCallback {
        public void onValueChange(PercentageSliderWidget sender, double value);
    }
}
