package com.reclizer.csgobox.utils.random_pickers;

import com.reclizer.csgobox.CsgoBox;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashSet;

@SuppressWarnings("deprecation")
public final class RandomCurioPicker {
    private static volatile Item[] CACHED_CURIOS = new Item[0];

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

    public static ItemStack randomCurioStack(RandomSource random) {
        Item[] cache = CACHED_CURIOS;
        if (cache.length == 0) {
            CsgoBox.LOGGER.error("curios is not picked properly, give a default curios");
            return Items.TOTEM_OF_UNDYING.getDefaultInstance();
        }

        Item item = cache[random.nextInt(cache.length)];

        return new ItemStack(item, 1);
    }
}
