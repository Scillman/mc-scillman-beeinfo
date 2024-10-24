package com.github.scillman.minecraft.beeinfo.gui;

import com.github.scillman.minecraft.beeinfo.ModMain;
import com.github.scillman.minecraft.beeinfo.registry.ModConstants;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

/**
 * @brief The screen used for rendering the menu.
 */
@Environment(value=EnvType.CLIENT)
public class InGameMenu extends Screen
{
    private static final Identifier MENU_TEXTURE = Identifier.of(ModMain.MOD_ID, "textures/gui/menu.png");
    private static final int MENU_TEXTURE_WIDTH = 256;
    private static final int MENU_TEXTURE_HEIGHT = 256;

    private int honeyLevel;
    private List<Text> beeNames;
    private ItemStack honeyBottle;
    private int x;
    private int y;

    public InGameMenu(int honeyLevel, int beeCount, List<String> beeNames)
    {
        super(Text.translatable(ModConstants.MENU_INGAME_MENU.toTranslationKey(ModConstants.KEY_MENU)));

        this.honeyLevel = honeyLevel;
        this.beeNames = new ArrayList<Text>(beeNames.size());

        int max = Math.min(beeCount, beeNames.size());
        for (int i = 0; i < max; i++)
        {
            this.beeNames.add(Text.translatable(beeNames.get(i)));
        }

        honeyBottle = new ItemStack(Items.HONEY_BOTTLE, 1);
    }

    private int getMinBeeRows()
    {
        return Math.max(3, beeNames.size());
    }

    @Override
    protected void init()
    {
        super.init();

        int minRows = getMinBeeRows();
        int usedHeight = 30 + (minRows * 30) + 8;

        this.x = (this.width - 176) / 2;
        this.y = (this.height - usedHeight) / 2;
    }

    private void drawTexture(DrawContext context, int x, int y, float u, float v, int width, int height)
    {
        context.drawTexture(RenderLayer::getGuiTextured, MENU_TEXTURE, x, y, u, v, width, height, MENU_TEXTURE_WIDTH, MENU_TEXTURE_HEIGHT);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta)
    {
        if (client == null)
        {
            return;
        }

        renderBackground(context, mouseX, mouseY, delta);

        drawTexture(context, x, y, 0, 0, 176, 30); // header

        int minRows = getMinBeeRows();
        for (int i = 0; i < minRows; i++)
        {
            drawTexture(context, x, y+30+(i*30), 0, 30, 176, 30); // icon+name background

            if (i < beeNames.size())
            {
                drawTexture(context, x+9, y+32+(i*30), 0, 166, 22, 22); // icon
            }
        }
        drawTexture(context, x, y+30+(minRows*30), 0, 157, 176, 8); // footer

        for (int i = Math.max(5, honeyLevel); i < 9; i++)
        {
            drawTexture(context, x+7+(i*18), y+7, 8, 64, 18, 18); // bottle slots
        }

        for (int i = 0; i < beeNames.size(); i++)
        {
            Text beeName = beeNames.get(i);
            if (beeName != null)
            {
                context.drawText(textRenderer, beeName, x+48, y+32+(i*30)+8, 0x000000, false);
            }
        }

        for (int i = 0; i < honeyLevel; i++)
        {
            context.drawItem(honeyBottle, x+8+(i*18), y+8);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (super.keyPressed(keyCode, scanCode, modifiers))
        {
            return true;
        }

        if (keyCode == GLFW.GLFW_KEY_ESCAPE || client.options.inventoryKey.matchesKey(keyCode, scanCode))
        {
            client.player.closeScreen();
            return true;
        }

        return false;
    }

    @Override
    public boolean shouldPause()
    {
        return false;
    }
}

/**
 *
 * RENDERER ==> TEXTURE (2D)
 *
 * 0,0     176
 *    +-----------+
 * 30 | HEADER    |
 *    +-----------+
 * 30 | ICON+NAME |
 *    +-----------+
 * 8  | FOOTER    |
 *    +-----------+
 *
 * 9,32   22
 *     +------+
 *  22 | ICON |
 *     +------+
 *
 * 7,7   18
 *    +------------+
 * 18 | HONEY_SLOT |
 *    +------------+
 *
 * RENDERER ==> TEXT (2D)
 *
 * 48,40
 *      +------+
 *      | NAME |
 *      +------+
 *
 * RENDERER ==> ITEM (3D => 2D)
 *
 * 8,8
 *    +--------------+
 *    | HONEY_BOTTLE |
 *    +--------------+
 *
 */
