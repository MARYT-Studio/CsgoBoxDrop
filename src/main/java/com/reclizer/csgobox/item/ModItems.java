package com.reclizer.csgobox.item;

import com.reclizer.csgobox.CsgoBox;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {

    private static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CsgoBox.MOD_ID);

    public static void registerTab(IEventBus eventBus) {
        TABS.register(eventBus);
    }


    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CsgoBox.MOD_ID);
    public static final RegistryObject<Item> ITEM_CSGOBOX=ITEMS.register("csgo_box",  ItemCsgoBox::new);


    public static  void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
