package com.reclizer.csgobox.capability.csbox;


import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class CsboxCap implements ICsboxCap {


    private long playSeed;

    private int mode;

    private ItemStack item = ItemStack.EMPTY;

    private int grade;
    public CsboxCap(@Nullable final LivingEntity ignored) {
    }

    @Override
    public long playerSeed() {
        return this.playSeed;
    }

    @Override
    public void setSeed(long seed) {
        this.playSeed = seed;
    }

    @Override
    public int mode() {
        return this.mode;
    }

    @Override
    public void setMode(int mode) {
        if (mode > -2 && mode < 2) {
            this.mode = mode;
        }
        mode();
    }

    @Override
    public void setItem(ItemStack item) {
        this.item = item;
    }

    @Override
    public ItemStack getItem() {
        return this.item;
    }

    @Override
    public int getGrade() {
        return this.grade;
    }

    @Override
    public void setGrade(int grade) {
        if (grade > 0 && grade < 6) {
            this.grade = grade;
        }
    }


    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putLong("csrandom", playerSeed());
        tag.putInt("csmode", mode());
        tag.put("csitem", getItem().serializeNBT());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        setSeed(nbt.getLong("csrandom"));
        setMode(nbt.getInt("csmode"));
        setItem(ItemStack.of(nbt.getCompound("csitem")));
    }


}
