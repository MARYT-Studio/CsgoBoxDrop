package com.reclizer.csgobox.utils.random_pickers;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.tags.TagKey;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashSet;

@Mod.EventBusSubscriber(modid = "yourmodid")
public final class RandomCurioPicker {

    // 运行期只读数组：O(1) 随机访问
    private static volatile Item[] CACHED_CURIOS = new Item[0];

    private RandomCurioPicker() {}

    /**
     * 在标签加载/数据包重载后重建缓存。
     * Curios 的“哪些物品算饰品”主要由标签决定，监听这个事件最稳妥。:contentReference[oaicite:2]{index=2}
     */
    @SubscribeEvent
    public static void onTagsUpdated(TagsUpdatedEvent event) {
        System.out.println("tag reloaded");
        var set = new HashSet<Item>(1024);

        // 遍历物品注册表的所有标签，挑出 namespace == "curios" 的那些
        BuiltInRegistries.ITEM.getTags().forEach(pair -> {
            TagKey<Item> tagKey = pair.getFirst();
            ResourceLocation id = tagKey.location();

            if (!"curios".equals(id.getNamespace())) return;

            HolderSet.Named<Item> holders = pair.getSecond();
            for (Holder<Item> holder : holders) {
                set.add(holder.value());
            }
        });

        CACHED_CURIOS = set.toArray(Item[]::new);
    }

    /**
     * 运行期随机获取一个 Curios 饰品 ItemStack：
     * 1) 必须是 Curios（来自 curios:* 标签）
     * 2) 数量固定为 1
     * 3) 运行期开销极低（O(1)）
     */
    public static ItemStack randomCurioStack(RandomSource random) {
        Item[] cache = CACHED_CURIOS;
        if (cache.length == 0) {
            return ItemStack.EMPTY; // 说明标签尚未加载或当前包内没有 curios 标签物品
        }

        Item item = cache[random.nextInt(cache.length)];

        // “与创造模式直接取出同一物品结果相同”的最接近实现：
        // 返回默认实例（无额外 NBT/Components），数量为 1。
        return new ItemStack(item, 1);
    }
}
