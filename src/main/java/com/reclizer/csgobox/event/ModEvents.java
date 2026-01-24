package com.reclizer.csgobox.event;

import com.reclizer.csgobox.CsgoBoxDrop;


import com.reclizer.csgobox.item.ItemCsgoBox;
import com.reclizer.csgobox.item.ModItems;
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
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collection;
import java.util.Objects;
import java.util.Random;

import static com.reclizer.csgobox.utils.random_pickers.RandomBladePicker.randomBladeData;
import static com.reclizer.csgobox.utils.random_pickers.RandomCurioPicker.randomCurioData;
import static com.reclizer.csgobox.utils.random_pickers.RandomFoodPicker.randomFoodData;

@Mod.EventBusSubscriber(modid = CsgoBoxDrop.MOD_ID)
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
            ItemCsgoBox.BoxInfo boxInfo = generateBoxInfo(bossFlag, drops, probability, player.getRandom(), player.level());
            if (checkIfBoxInvalid(boxInfo, player)) {
                // 箱子信息无效，不会掉落箱子，按照“没有掉落箱子”的情况给予一定量概率补偿
                player.getPersistentData().putDouble("probability", bossFlag ? probability * 1.2D : probability * 1.05D);
            } else {
                ItemCsgoBox.setBoxInfo(boxInfo, box);
                entity.spawnAtLocation(box);
                // 掉落了箱子会导致掉落率下降
                player.getPersistentData().putDouble("probability", bossFlag ? probability * 0.9D : probability * 0.5D);
            }
        } else {
            // 如果打怪没有掉落箱子，给予一定量概率补偿
            player.getPersistentData().putDouble("probability", bossFlag ? probability * 1.2D : probability * 1.05D);
        }
    }


    private static ItemCsgoBox.BoxInfo generateBoxInfo(boolean bossFlag, Collection<ItemEntity> drops, double probability, RandomSource random, Level level) {
        return ItemCsgoBox.BoxInfo.deserializeNBT(itemToTag(bossFlag, drops, probability, random, level));
    }

    private static CompoundTag itemToTag(boolean bossFlag, Collection<ItemEntity> drops, double probability, RandomSource random, Level level) {
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

        // 将怪物的掉落物成倍放入grade1
        for (ItemEntity itemEntity: drops.stream().toList()) {
            ItemStack item = itemEntity.getItem();
            int count = item.getCount();
            if (bossFlag && count > 5) item.setCount((int) Math.floor(1 + new Random().nextFloat(4f)));
            String itemData = item.save(new CompoundTag()).toString();
            grade1Tag.add(StringTag.valueOf(itemData));
        }

        int rewardCount = random.nextIntBetweenInclusive(1, 3);
        int base = bossFlag ? 2: 1;

        // 击杀Boss的保底会变为2个，否则保底1个结束后都需要概率roll
        // 食物固定放grade1Tag，避免怪物无掉落物时出现空位
        for(int i = 0; i < rewardCount; i++) {
            if (i < base) {
                grade1Tag.add(StringTag.valueOf(randomFoodData(random)));
            } else {
                if (random.nextFloat() < probability) {
                    grade1Tag.add(StringTag.valueOf(randomFoodData(random)));
                }
            }

        }

        // grade2：如果打了boss放的就是饰品，否则放的是食物
        for(int i = 0; i < rewardCount; i++) {
            if (i < base) {
                grade2Tag.add(StringTag.valueOf(bossFlag? randomCurioData(random) : randomFoodData(random)));
            } else {
                if (random.nextFloat() < probability) {
                    grade2Tag.add(StringTag.valueOf(bossFlag? randomCurioData(random) : randomFoodData(random)));
                }
            }
        }

        // grade3：固定是饰品
        for(int i = 0; i < rewardCount; i++) {
            if (i < base) {
                grade3Tag.add(StringTag.valueOf(randomCurioData(random)));
            } else {
                if (random.nextFloat() < probability) {
                    grade3Tag.add(StringTag.valueOf(randomCurioData(random)));
                }
            }
        }

        // grade4：固定是拔刀剑
        for(int i = 0; i < rewardCount; i++) {
            if (i < base) {
                grade4Tag.add(StringTag.valueOf(randomBladeData(random, level)));
            } else {
                if (random.nextFloat() < probability) {
                    grade4Tag.add(StringTag.valueOf(randomBladeData(random, level)));
                }
            }
        }

        // grade5：该物品是不可见的传说物品，如果打了boss就是拔刀剑，否则是饰品
        for(int i = 0; i < rewardCount; i++) {
            if (i < base) {
                grade5Tag.add(StringTag.valueOf(bossFlag? randomBladeData(random, level) : randomCurioData(random)));
            } else {
                if (random.nextFloat() < probability) {
                    grade5Tag.add(StringTag.valueOf(bossFlag? randomBladeData(random, level) : randomCurioData(random)));
                }
            }
        }

        tag.put("grade1", grade1Tag);
        tag.put("grade2", grade2Tag);
        tag.put("grade3", grade3Tag);
        tag.put("grade4", grade4Tag);
        tag.put("grade5", grade5Tag);
        if (CsgoBoxDrop.DEBUG) CsgoBoxDrop.LOGGER.debug(tag.toString());
        return tag;
    }

    public static boolean checkIfBoxInvalid(ItemCsgoBox.BoxInfo boxInfo, Player player) {
        int result = 0;
        if (boxInfo.grade1.isEmpty()) result += 1;
        if (boxInfo.grade2.isEmpty()) result += 2;
        if (boxInfo.grade3.isEmpty()) result += 4;
        if (boxInfo.grade4.isEmpty()) result += 8;
        if (boxInfo.grade5.isEmpty()) result += 16;
        if (result > 0 && CsgoBoxDrop.DEBUG) CsgoBoxDrop.LOGGER.error("Player {} got an invalid box, empty grades are: {}",player.getName().getString(), Integer.toBinaryString(result));
        return result > 0;
    }

}
