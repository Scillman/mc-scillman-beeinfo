package com.github.scillman.minecraft.beeinfo.gui;

import com.github.scillman.minecraft.beeinfo.config.ModSettings;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.text.Text;

import static com.github.scillman.minecraft.beeinfo.BeeInfo.KEY_SETTING_ENABLE_MENU;
import static com.github.scillman.minecraft.beeinfo.BeeInfo.KEY_SETTING_ENABLE_HUD;
import static com.github.scillman.minecraft.beeinfo.BeeInfo.KEY_SETTING_HUD_AXIS_X;
import static com.github.scillman.minecraft.beeinfo.BeeInfo.KEY_SETTING_HUD_AXIS_Y;

// import static com.github.scillman.minecraft.beeinfo.BeeInfo.HINT_SETTING_ENABLE_MENU;
// import static com.github.scillman.minecraft.beeinfo.BeeInfo.HINT_SETTING_ENABLE_HUD;
// import static com.github.scillman.minecraft.beeinfo.BeeInfo.HINT_SETTING_HUD_AXIS_X;
// import static com.github.scillman.minecraft.beeinfo.BeeInfo.HINT_SETTING_HUD_AXIS_Y;

@Environment(value=EnvType.CLIENT)
public class OptionsScreen extends Screen
{
    private ButtonWidget buttonDone;
    private CyclingButtonWidget<Boolean> buttonToggleMenu;
    private CyclingButtonWidget<Boolean> buttonToggleHud;
    private PercentageSliderWidget sliderHudAxisX;
    private PercentageSliderWidget sliderHudAxisY;

    public OptionsScreen(Text title)
    {
        super(title);
    }

    @Override
    public void close()
    {
        ModSettings.save();
        super.close();
    }

    private void onPressedButtonDone(ButtonWidget sender)
    {
        assert(sender == buttonDone);
        close();
    }

    private void onToggleMenu(CyclingButtonWidget<Boolean> sender, Boolean value)
    {
        assert(sender == buttonToggleMenu);
        ModSettings.setEnableMenu(value);
    }

    private void onToggleHud(CyclingButtonWidget<Boolean> sender, Boolean value)
    {
        assert(sender == buttonToggleHud);
        ModSettings.setEnableHud(value);
    }

    private void onHudAxisXChanged(PercentageSliderWidget sender, double value)
    {
        assert(sender == sliderHudAxisX);
        float max = ModSettings.getHudAxisXMax();
        float min = ModSettings.getHudAxisXMin();
        float newValue = (((max - min) * (float)value) + min);
        ModSettings.setHudAxisX(newValue);
    }

    private void onHudAxisYChanged(PercentageSliderWidget sender, double value)
    {
        assert(sender == sliderHudAxisY);
        float max = ModSettings.getHudAxisYMax();
        float min = ModSettings.getHudAxisYMin();
        float newValue = (((max - min) * (float)value) + min);
        ModSettings.setHudAxisY(newValue);
    }

    @Override
    protected void init()
    {
        final int SPACING = 5;
        final int ITEM_WIDTH = (width/3);
        final int ITEM_HEIGHT = 20;
        final int ITEM_X_LEFT = ((width/6)-SPACING);
        final int ITEM_X_RIGHT = ((width/2)+SPACING);
        int nextY = 35; // 20+15 = title, see {@link #render}

        buttonToggleMenu = createToggleButton(
            ITEM_X_LEFT, nextY,
            ITEM_WIDTH, ITEM_HEIGHT,
            KEY_SETTING_ENABLE_MENU,
            ModSettings.getEnableMenu(),
            this::onToggleMenu
        );
        buttonToggleHud = createToggleButton(
            ITEM_X_RIGHT, nextY,
            ITEM_WIDTH, ITEM_HEIGHT,
            KEY_SETTING_ENABLE_HUD,
            ModSettings.getEnableHud(),
            this::onToggleHud
        );

        nextY += ITEM_HEIGHT + SPACING;

        sliderHudAxisX = createSlider(
            ITEM_X_LEFT, nextY,
            ITEM_WIDTH, ITEM_HEIGHT,
            KEY_SETTING_HUD_AXIS_X,
            ModSettings.getHudAxisX(),
            this::onHudAxisXChanged
        );
        sliderHudAxisY = createSlider(
            ITEM_X_RIGHT, nextY,
            ITEM_WIDTH, ITEM_HEIGHT,
            KEY_SETTING_HUD_AXIS_Y,
            ModSettings.getHudAxisY(),
            this::onHudAxisYChanged
        );

        nextY += ITEM_HEIGHT + SPACING;

        buttonDone = createButton(
            (width/4), nextY,
            (width/2), ITEM_HEIGHT,
            "Done",
            this::onPressedButtonDone
        );

        addDrawableChild(buttonToggleMenu);
        addDrawableChild(buttonToggleHud);
        addDrawableChild(sliderHudAxisX);
        addDrawableChild(sliderHudAxisY);
        addDrawableChild(buttonDone);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta)
    {
        assert(client != null);

        renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, title, (width / 2), 20, 0xffffff);

        super.render(context, mouseX, mouseY, delta);
    }

    private static ButtonWidget createButton(int x, int y, int width, int height, String text, ButtonWidget.PressAction pressAction)
    {
        return ButtonWidget.builder(Text.translatable(text), pressAction).dimensions(x, y, width, height).build();
    }

    private static CyclingButtonWidget<Boolean> createToggleButton(int x, int y, int width, int height, String text, boolean value, CyclingButtonWidget.UpdateCallback<Boolean> callback)
    {
        return CyclingButtonWidget.onOffBuilder(value).build(x, y, width, height, Text.translatable(text), callback);
    }

    private static PercentageSliderWidget createSlider(int x, int y, int width, int height, String text, double value, PercentageSliderWidget.UpdateCallback callback)
    {
        return new PercentageSliderWidget(x, y, width, height, Text.translatable(text), value, callback);
    }
}
