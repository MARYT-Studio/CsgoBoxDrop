package com.reclizer.csgobox.utils;


import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.reclizer.csgobox.CsgoBoxDrop;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.item.ItemStack;

public class ItemNBT {
    public static ItemStack getStacks(String itemData) {
        try {
            return ItemStack.of(TagParser.parseTag(itemData));
        } catch (CommandSyntaxException e) {
            CsgoBoxDrop.LOGGER.error("ItemStack parsing error.\nError item data:\n{}\n{}", itemData, e);
            return ItemStack.EMPTY;
        }
    }
}
