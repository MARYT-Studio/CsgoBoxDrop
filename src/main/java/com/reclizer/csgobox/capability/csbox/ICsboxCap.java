package com.reclizer.csgobox.capability.csbox;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;

public interface ICsboxCap extends INBTSerializable<CompoundTag> {

    long playerSeed();

    void setSeed(final long seed);

    int mode();

    void setMode(final int mode);

    void setItem(final ItemStack item);

    ItemStack getItem();

    int getGrade();

    void setGrade(final int grade);


}
