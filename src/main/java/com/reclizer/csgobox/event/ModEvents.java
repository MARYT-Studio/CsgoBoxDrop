package com.reclizer.csgobox.event;

import com.reclizer.csgobox.CsgoBox;


import com.reclizer.csgobox.item.ItemCsgoBox;
import com.reclizer.csgobox.item.ModItems;
import com.reclizer.csgobox.utils.random_pickers.RandomCurioPicker;
import com.reclizer.csgobox.utils.random_pickers.RandomFoodPicker;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collection;
import java.util.Objects;
import java.util.Random;

import static com.reclizer.csgobox.utils.ItemNBT.getStacksData;

@Mod.EventBusSubscriber(modid = CsgoBox.MODID)
public class ModEvents {

    @SubscribeEvent
    public static void LivingDropEvents(LivingDropsEvent event) {
        Entity killer = event.getSource().getEntity();
        if(!(killer instanceof Player)) return;

        Player player;
        player = (Player) killer;
        if (player.level().isClientSide()) return;

        Collection<ItemEntity> drops = event.getDrops();

        LivingEntity entity = event.getEntity();
        EntityType<?> entityType = entity.getType();
        TagKey<EntityType<?>> bossTag = TagKey.create(Registries.ENTITY_TYPE, Objects.requireNonNull(ResourceLocation.tryParse("forge:bosses")));
        boolean bossFlag = entityType.is(bossTag);

        // Init
        if (!(player.getPersistentData().contains("probability"))) {
            player.getPersistentData().putDouble("probability", 0.4D);
        }

        // 不让掉落率太高
        if (player.getPersistentData().getDouble("probability") > 0.7D) {
            player.getPersistentData().putDouble("probability", 0.7D);
        }


        // Time refreshing
        // 80000 secs makes it merely one day but not precisely, creating a fake random
        long time = Math.floorMod(System.currentTimeMillis(), 80000000);
        if (!(player.getPersistentData().contains("timestamp"))) {
            player.getPersistentData().putLong("timestamp", time);
        } else if (time < player.getPersistentData().getLong("timeStamp")) {
            // 按时重置
            player.getPersistentData().putDouble("probability", 0.4D);
            player.getPersistentData().putLong("timestamp", time);
        }

        double probability = player.getPersistentData().getDouble("probability");

        if (player.getRandom().nextDouble() < (bossFlag ? 0.6D: probability)) {
            ItemStack box = new ItemStack(ModItems.ITEM_CSGOBOX.get());
            ItemCsgoBox.setBoxInfo(generateBoxInfo(bossFlag, drops, probability, player.getRandom()), box);
            entity.spawnAtLocation(box);

            // 掉落了箱子会导致掉落率下降
            player.getPersistentData().putDouble("probability", bossFlag ? probability * 0.9D : probability * 0.5D);
        } else {
            // 如果打怪没有掉落箱子，给予一定量概率补偿
            player.getPersistentData().putDouble("probability", bossFlag ? probability * 1.2D : probability * 1.05D);
        }
    }


    private static ItemCsgoBox.BoxInfo generateBoxInfo(boolean bossFlag, Collection<ItemEntity> drops, double probability, RandomSource random) {
        return ItemCsgoBox.BoxInfo.deserializeNBT(itemToTag(bossFlag, drops, probability, random));
    }

    private static CompoundTag itemToTag(boolean bossFlag, Collection<ItemEntity> drops, double probability, RandomSource random) {
        CompoundTag tag = new CompoundTag();
        tag.putString("name", "Newbie Chest");
        tag.putString("key", "newbie_chest");
        tag.putFloat("drop",114514);
        tag.putIntArray("random", new int[]{1,9, 19, 8, 10});

        ListTag grade1Tag = new ListTag();
        ListTag grade2Tag = new ListTag();
        ListTag grade3Tag = new ListTag();
        ListTag grade4Tag = new ListTag();
        ListTag grade5Tag = new ListTag();
        for (ItemEntity itemEntity: drops.stream().toList()) {
            ItemStack item = itemEntity.getItem();
            int count = item.getCount();
            if (bossFlag && count > 5) item.setCount((int) Math.floor(1 + new Random().nextFloat(4f)));
            String itemData = getStacksData(item);
            if (itemData == null) continue;
            switch (item.getRarity().ordinal()) {
                case 0: grade1Tag.add(StringTag.valueOf(itemData));
                case 1: grade2Tag.add(StringTag.valueOf(itemData));
                case 2: grade3Tag.add(StringTag.valueOf(itemData));
                case 3: grade4Tag.add(StringTag.valueOf(itemData));
                case 4: grade5Tag.add(StringTag.valueOf(itemData));
                default: grade1Tag.add(StringTag.valueOf(itemData));
            }
        }

        // 额外的奖励，玩家在箱子概率越低的时候获得的箱子开出好东西的几率越高
        // 这个概率会随着额外奖励的出货情况改变
        double extraProbability = 1 - probability;

        for(int i = 0; i < 5; i++) {
            if (random.nextFloat() < extraProbability) {
                ItemStack food = RandomFoodPicker.randomFoodStack(random);
                if (food == null) continue;
                String data = getStacksData(food);
                if (data == null) continue;
                grade1Tag.add(StringTag.valueOf(data));
                extraProbability *= 0.8;

                // 食物出一个就够了
                break;
            } else extraProbability += 0.05;
        }
        extraProbability = Math.max(extraProbability, 0.9d);


        for(int i = 0; i < 5; i++) {
            if (random.nextFloat() < extraProbability) {
                ItemStack curios = RandomCurioPicker.randomCurioStack(random);
                if (curios == null) continue;
                String data = getStacksData(curios);
                if (data == null) continue;
                grade4Tag.add(StringTag.valueOf(data));
                extraProbability *= 0.8;
            } else extraProbability += 0.05;
        }
        extraProbability = Math.max(extraProbability, 0.9d);

        for(int i = 0; i < 5; i++) {
            if (random.nextFloat() < extraProbability) {
                ItemStack curios = RandomCurioPicker.randomCurioStack(random);
                if (curios == null) continue;
                String data = getStacksData(curios);
                if (data == null) continue;
                grade5Tag.add(StringTag.valueOf(data));
                extraProbability *= 0.8;
            } else extraProbability += 0.05;
        }

        // Grade 5 分组至少有一个饰品
        ItemStack curios = RandomCurioPicker.randomCurioStack(random);
        if (curios != null) {
            String data = getStacksData(curios);
            if (data != null) {
                grade5Tag.add(StringTag.valueOf(data));
            }
        }

        tag.put("grade1", grade1Tag);
        tag.put("grade2", grade2Tag);
        tag.put("grade3", grade3Tag);
        tag.put("grade4", grade4Tag);
        tag.put("grade5", grade5Tag);
        return tag;
    }

}
