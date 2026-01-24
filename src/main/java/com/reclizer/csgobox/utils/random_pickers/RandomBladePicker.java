package com.reclizer.csgobox.utils.random_pickers;

import com.reclizer.csgobox.CsgoBoxDrop;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.registry.slashblade.SlashBladeDefinition;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public final class RandomBladePicker {


    private static volatile ItemStack[] SLASHBLADES = null;

    public static void initBladeCache(Level level) {
        if (SLASHBLADES != null && SLASHBLADES.length != 0) return;
        SLASHBLADES = null;
        List<ItemStack> blades = new ArrayList<>(1024);
        for (SlashBladeDefinition blade : SlashBlade.getSlashBladeDefinitionRegistry(level)) {
            blades.add(blade.getBlade());
        }

        SLASHBLADES = blades.toArray(ItemStack[]::new);
        CsgoBoxDrop.LOGGER.info("SlashBlade list initialized. {} entries.", SLASHBLADES.length);
        if (CsgoBoxDrop.DEBUG) {
            CsgoBoxDrop.LOGGER.debug("SlashBlade list are below:");
            for (ItemStack blade: SLASHBLADES) {
                CsgoBoxDrop.LOGGER.debug("{}", blade.save(new CompoundTag()));
            }
        }
    }

    public static String randomBladeData(RandomSource random, Level level) {
        ItemStack[] cache = SLASHBLADES;
        String defaultData = ItemStack.EMPTY.save(new CompoundTag()).getAsString();

        if (cache == null || cache.length == 0) {
            initBladeCache(level);
            cache = SLASHBLADES;
            if (cache == null || cache.length == 0) {
                CsgoBoxDrop.LOGGER.error("SlashBlade cache is empty, return default SlashBlade");
                return defaultData;
            }
        }

//        String result = cache[random.nextInt(cache.length)].save(new CompoundTag()).getAsString();
//        return result.contains("minecraft:air") ? defaultData: result;
        return cache[random.nextInt(cache.length)].save(new CompoundTag()).getAsString();
    }
}
