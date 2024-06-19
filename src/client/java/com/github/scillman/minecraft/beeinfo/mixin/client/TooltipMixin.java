package com.github.scillman.minecraft.beeinfo.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import java.util.List;

import net.minecraft.block.entity.BeehiveBlockEntity.BeeData;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BlockStateComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

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
     * @brief Called before ItemStack.getToolTip returns to the caller.
     * @param player The player to draw the tooltip for.
     * @param context The context of the tooltip.
     * @param list A reference to local variable @e list of ItemStack.getTooltip
     */
    @ModifyReturnValue(method="getTooltip", at=@At("RETURN"))
    private List<Text> onGetTooltip(List<Text> list, Item.TooltipContext context, @Nullable PlayerEntity player, TooltipType type)
    {
        if (player == null)
        {
            return list;
        }

        World world = player.getWorld();
        assert(world.isClient());

        // Must be a block capable of containing honey
        if (!isHoneyBeeContainer())
        {
            return list;
        }

        ItemStack me = (ItemStack)(Object)this;

        int beeCount = 0, childCount = 0;
        int honeyLevel = 0;

        List<BeeData> bees = me.get(DataComponentTypes.BEES);
        if (bees != null)
        {
            beeCount = bees.size();

            for (BeeData bee: bees)
            {
                NbtComponent beeEntityData = bee.entityData();
                if (beeEntityData.contains("Age"))
                {
                    int age = beeEntityData.copyNbt().getInt("Age");
                    if (age < 0)
                    {
                        childCount++;
                    }
                }
            }
        }

        BlockStateComponent bsc = me.get(DataComponentTypes.BLOCK_STATE);
        if (bsc != null)
        {
            honeyLevel = bsc.getValue(Properties.HONEY_LEVEL);
        }

        if (beeCount > 0 || honeyLevel > 0)
        {
            if (childCount > 0)
            {
                list.add(Math.min(1, list.size()), Text.literal(I18n.translate("tooltip.bees_child", childCount)));
                list.add(Math.min(1, list.size()), Text.literal(I18n.translate("tooltip.bees_adult", (beeCount - childCount))));
            }
            else
            {
                list.add(Math.min(1, list.size()), Text.literal(I18n.translate("tooltip.bees", beeCount)));
            }

            list.add(Math.min(1, list.size()), Text.literal(I18n.translate("tooltip.honey", honeyLevel)));
        }

        return list;
    }

    /**
     * @brief Get an indicator whether the item is capable of containing honey.
     * @return True if the item is capable of containing honey; otherwise, false.
     */
    private boolean isHoneyBeeContainer()
    {
        // NOTE: Will likely be added by Mojang in the future
        // item.isIn(ItemTags.BEEHIVES);
        return !isEmpty() && (getItem() == Items.BEEHIVE || getItem() == Items.BEE_NEST);
    }
}
