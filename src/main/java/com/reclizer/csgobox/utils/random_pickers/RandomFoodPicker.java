package com.reclizer.csgobox.utils.random_pickers;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public final class RandomFoodPicker {
    private RandomFoodPicker() {}
    private static volatile Item[] FOOD_ITEMS = null;
    public static void initFoodCache() {
        if (FOOD_ITEMS != null) return;

        List<Item> foods = new ArrayList<>(1024);
        for (Item item : BuiltInRegistries.ITEM) {
            if (item != null && item.isEdible()) {
                foods.add(item);
            }
        }

        FOOD_ITEMS = foods.toArray(Item[]::new);
    }
    public static @NotNull String randomFoodData(RandomSource random) {
        Item[] cache = FOOD_ITEMS;
        String defaultData = Items.GOLDEN_APPLE.getDefaultInstance().save(new CompoundTag()).getAsString();

        if (cache == null || cache.length == 0) {
            initFoodCache();
            cache = FOOD_ITEMS;
            if (cache == null || cache.length == 0) {
                // 返回一个默认的食物
                return defaultData;
            }
        }

        Item item = cache[random.nextInt(cache.length)];

        int range = item.getMaxStackSize();
        int count = (int) Math.ceil((range / 4.0d) * random.nextGaussian() + (range / 3.0d));

        String result = (new ItemStack(item, count)).save(new CompoundTag()).toString();
        return result.contains("minecraft:air") ? defaultData: result;
    }
}
