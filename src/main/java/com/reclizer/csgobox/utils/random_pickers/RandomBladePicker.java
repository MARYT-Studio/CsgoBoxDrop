package com.reclizer.csgobox.utils.random_pickers;

import com.reclizer.csgobox.CsgoBox;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.registry.slashblade.SlashBladeDefinition;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public final class RandomBladePicker {
    private RandomBladePicker() {}

    private static volatile ItemStack[] SLASHBLADES = null;
    public static void initBladeCache(Level level) {
        if (SLASHBLADES != null && SLASHBLADES.length != 0) return;
        SLASHBLADES = null;
        List<ItemStack> blades = new ArrayList<>(1024);
        for (SlashBladeDefinition blade : SlashBlade.getSlashBladeDefinitionRegistry(level)) {
            blades.add(blade.getBlade());
        }

        SLASHBLADES = blades.toArray(ItemStack[]::new);
    }
    public static ItemStack fetchBladeStack(int keyId) {
        ItemStack[] cache = SLASHBLADES;
        if (cache == null || cache.length == 0) {
            CsgoBox.LOGGER.error("SlashBlade cache is empty");
            return ItemStack.EMPTY;
        }
        return cache[keyId];
    }
    public static ItemStack randomBladeStack(RandomSource random, Level level) {
        ItemStack[] cache = SLASHBLADES;
        if (cache == null || cache.length == 0) {
            initBladeCache(level);
            cache = SLASHBLADES;
            if (cache == null || cache.length == 0) {
                CsgoBox.LOGGER.error("SlashBlade cache is empty");
                return ItemStack.EMPTY;
            }
        }

        int keyId = random.nextInt(cache.length);
        ItemStack result = cache[keyId];
        result.getOrCreateTag().putInt("keyIdForFetchBladeFromCache", keyId);
        return result;
    }
}
