package com.reclizer.csgobox.utils.random_pickers;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public final class RandomFoodPicker {
    private RandomFoodPicker() {}

    // 缓存：启动后只构建一次；运行时 O(1) 取用
    private static volatile Item[] FOOD_ITEMS = null;

    /**
     * 在合适的初始化时机调用一次（例如 FMLCommonSetupEvent）。
     * 你也可以做成懒加载，但显式 init() 更可控。
     */
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

    /**
     * 运行期随机获取一个食物 ItemStack：
     * - 食物
     * - 数量为 [1, maxStackSize] 的随机合法整数
     * - 运行时不遍历注册表，速度最快
     */
    public static ItemStack randomFoodStack(RandomSource random) {
        Item[] cache = FOOD_ITEMS;
        if (cache == null || cache.length == 0) {
            initFoodCache();
            cache = FOOD_ITEMS;
            if (cache == null || cache.length == 0) {
                return ItemStack.EMPTY;
            }
        }

        Item item = cache[random.nextInt(cache.length)];

        int range = item.getMaxStackSize();
        int count = (int) Math.ceil((range / 4.0d) * random.nextGaussian() + (range / 3.0d));

        return new ItemStack(item, count);
    }
}
