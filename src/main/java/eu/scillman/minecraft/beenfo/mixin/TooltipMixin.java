package eu.scillman.minecraft.beenfo.mixin;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import eu.scillman.minecraft.beenfo.Beenfo;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import java.util.List;

@Mixin(ItemStack.class)
public abstract class TooltipMixin
{
    /**
     * @brief A reference to ItemStack.isEmpty()
     * @return True if there are no items in the stack; otherwise, false.
     */
    @Shadow
    public abstract boolean isEmpty();
    
    /**
     * @brief A reference to the ItemStack.getItem()
     * @return Items.AIR when empty; otherwise, the item inside the stack.
     */
    @Shadow
    public abstract Item getItem();

    /**
     * @brief A reference to the ItemStack.getNbt()
     * @return The contained data. (TODO: update this text)
     */
    @Shadow
    public abstract @Nullable NbtCompound getNbt();

    /**
     * @brief Called before ItemStack.getToolTip returns to the caller.
     * @param player The player to draw the tooltip for.
     * @param context The context of the tooltip.
     * @param list A reference to local variable @e list of ItemStack.getTooltip
     */
    @Inject(method="getTooltip", at=@At("RETURN"), locals=LocalCapture.CAPTURE_FAILHARD)
    private void onGetTooltip(@Nullable PlayerEntity player, TooltipContext context, CallbackInfoReturnable<?> ci, List<Text> list)
    {
        // Must be a block capable of containing honey
        if (!isHoneyContainer())
        {
            return;
        }

        // Must have data attached to the block.
        NbtCompound nbt = getNbt();
        if (nbt == null)
        {
            return;
        }

        // TODO:
        //   The original code states MC 1.15 may return this as a string
        int honeyLevel = nbt.getCompound("BlockStateTag").getInt("honey_level");

        NbtList bees = nbt.getCompound("BlockEntityTag").getList("Bees", Beenfo.NBT_TYPE_COMPOUND);
        int beeCount = bees.size();

        for (int i = 0; i < beeCount; i++)
        {
            nbt = bees.getCompound(i).getCompound("EntityData");
            
            //ASSERT(nbt != null);
            if (nbt == null)
            {
                continue;
            }

            if (nbt.contains("CustomName", Beenfo.NBT_TYPE_STRING))
            {
                String beeName = nbt.getString("CustomName");
                list.add(Math.min(1, list.size()), Text.literal(I18n.translate("tooltip.name", Text.Serializer.fromJson(beeName).getString())));
            }
        }

        list.add(Math.min(1, list.size()), Text.literal(I18n.translate("tooltip.bees", beeCount)));
        list.add(Math.min(1, list.size()), Text.literal(I18n.translate("tooltip.honey", honeyLevel)));
    }

    /**
     * @brief Get an indicator whether the item is capable of containing honey.
     * @return True if the item is capable of containing honey; otherwise, false.
     */
    private boolean isHoneyContainer()
    {
        return !isEmpty() && (getItem() == Items.BEEHIVE || getItem() == Items.BEE_NEST);
    }
}
