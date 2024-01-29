package eu.scillman.minecraft.beenfo;

import java.util.List;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.text.Text;
import java.util.ArrayList;
import com.mojang.blaze3d.systems.RenderSystem;

public class BeenfoScreen extends Screen
{
    private static final Identifier MENU_TEXTURE = new Identifier(Beenfo.MOD_ID, "textures/gui/menu.png");

    private int honeyLevel;
    private List<Text> beeNames;
    private ItemStack honeyBottle;
    private int x;
    private int y;

    BeenfoScreen(Object object, int honeyLevel, List<String> beeNames)
    {
        super(Text.translatable("beenfo.screen.title"));

        this.honeyLevel = honeyLevel;
        this.beeNames = new ArrayList<Text>(beeNames.size());

        for (String beeName: beeNames)
        {
            this.beeNames.add(Text.Serializer.fromJson(beeName));
        }

        honeyBottle = new ItemStack(Items.HONEY_BOTTLE, 1);
    }

    @Override
    protected void init()
    {
        super.init();

        int minRows = Math.max(3, beeNames.size());
        int usedHeight = 30 + (minRows * 30) + 8;

        this.x = (this.width - 176) / 2;
        this.y = (this.height - usedHeight) / 2;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
    {
        if (client == null)
        {
            return;
        }

        renderBackground(matrices, 0);

        RenderSystem.setShaderTexture(0, MENU_TEXTURE);
        { // begin of TEXTURE usage
            drawTexture(matrices, x, y, 0, 0, 176, 30); // header - honey bottle slots

            int minRows = Math.max(3, beeNames.size());
            for (int i = 0; i < minRows; i++)
            {
                // Bee icon+name background
                drawTexture(matrices, x, y+30+(i*30), 0, 30, 176, 30);

                if (i < beeNames.size())
                {
                    // Bee icon
                    drawTexture(matrices, x+9, y+32+(i*30), 0, 166, 22, 22);
                }
            }
            drawTexture(matrices, x, y+30+(minRows*30), 0, 157, 176, 8); // footer

            for (int i = Math.max(5, honeyLevel); i < 9; i++)
            {
                // Honey bottle slots
                drawTexture(matrices, x+7+(i*18), y+7, 8, 64, 18, 18);
            }
        } // end of TEXTURE usage

        for (int i = 0; i < beeNames.size(); i++)
        {
            Text beeName = beeNames.get(i);
            if (beeName != null)
            {
                // Bee name
                textRenderer.draw(matrices, beeName.asOrderedText(), x+48, y+32+(i*30)+8, 0x000000);
            }
        }

        int oldZOffsetScreen = this.getZOffset();
        float oldZOffsetRenderer = itemRenderer.zOffset;
        {
            setZOffset(200);
            itemRenderer.zOffset = 200.0f;
            for (int i = 0; i < honeyLevel; i++)
            {
                itemRenderer.renderInGuiWithOverrides(honeyBottle, x+8+(i*18), y+8);
            }
        }
        setZOffset(oldZOffsetScreen);
        itemRenderer.zOffset = oldZOffsetRenderer;
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
 * 9,33   22
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
