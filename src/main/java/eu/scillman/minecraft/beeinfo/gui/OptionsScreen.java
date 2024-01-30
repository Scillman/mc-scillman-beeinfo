package eu.scillman.minecraft.beeinfo.gui;

import net.fabricmc.api.Environment;
import eu.scillman.minecraft.beeinfo.BeeInfo;
import net.fabricmc.api.EnvType;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class OptionsScreen extends Screen
{
    private ButtonWidget buttonDone;
    private CyclingButtonWidget<Boolean> buttonToggleHUD;
    private CyclingButtonWidget<Boolean> buttonToggleMenu;

    public OptionsScreen(Text title)
    {
        super(title);
    }

    private void onPressedButtonDone(ButtonWidget sender)
    {
        assert(sender == buttonDone);
        close();
    }

    private void onToggleHUD(CyclingButtonWidget<Boolean> sender, Boolean value)
    {
        assert(sender == buttonToggleHUD);
        BeeInfo.LOGGER.info("Enable HUD = " + value);
    }

    private void onToggleMenu(CyclingButtonWidget<Boolean> sender, Boolean value)
    {
        assert(sender == buttonToggleMenu);
        BeeInfo.LOGGER.info("Enable Menu = " + value);
    }

    @Override
    protected void init()
    {
        final int SPACING = 5;

        buttonToggleHUD = createToggleButton((width/6)-SPACING, 35, (width/3), 20, Text.literal("Enable HUD"), this::onToggleHUD);
        buttonToggleMenu = createToggleButton((width/2)+SPACING, 35, (width/3), 20, Text.literal("Enable In-Game Menu"), this::onToggleMenu);

        buttonDone = createButton((width/4), (35+20+SPACING), (width/2), 20, Text.literal("Done"), this::onPressedButtonDone);

        addDrawableChild(buttonToggleHUD);
        addDrawableChild(buttonToggleMenu);
        addDrawableChild(buttonDone);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
    {
        assert(client != null);

        renderBackground(matrices);

        drawCenteredText(
            matrices,       // MatrixStack
            textRenderer,   // TextRenderer
            title,          // Text
            (width / 2),    // centerX
            20,             // y
            0xffffff        // color
        );

        super.render(matrices, mouseX, mouseY, delta);
    }

    private static ButtonWidget createButton(int x, int y, int width, int height, Text text, ButtonWidget.PressAction pressAction)
    {
        return ButtonWidget.builder(text, pressAction).dimensions(x, y, width, height).build();
    }

    private static CyclingButtonWidget<Boolean> createToggleButton(int x, int y, int width, int height, Text text, CyclingButtonWidget.UpdateCallback<Boolean> callback)
    {
        return CyclingButtonWidget.onOffBuilder().build(x, y, width, height, text, callback);
    }
}
