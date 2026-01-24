package com.reclizer.csgobox.utils.random_pickers;

import com.reclizer.csgobox.CsgoBoxDrop;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
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

    public static String randomCurioData(RandomSource random) {
        Item[] cache = CACHED_CURIOS;
        String defaultData = Items.TOTEM_OF_UNDYING.getDefaultInstance().save(new CompoundTag()).getAsString();

        if (cache.length == 0) {
            CsgoBoxDrop.LOGGER.error("curios is not picked properly, give a default curios");
            return defaultData;
        }

        Item item = cache[random.nextInt(cache.length)];

        String result = (new ItemStack(item, 1)).save(new CompoundTag()).toString();
        return result.contains("minecraft:air") ? defaultData: result;
    }
}
