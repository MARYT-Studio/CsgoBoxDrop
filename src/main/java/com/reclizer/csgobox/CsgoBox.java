package com.reclizer.csgobox;

import com.mojang.logging.LogUtils;
import com.reclizer.csgobox.gui.RecModScreens;
import com.reclizer.csgobox.sounds.ModSounds;
import com.reclizer.csgobox.config.CsgoBoxManage;
import com.reclizer.csgobox.gui.RecModMenus;
import com.reclizer.csgobox.item.ModItems;
import com.reclizer.csgobox.packet.Networking;

import com.reclizer.csgobox.utils.random_pickers.RandomBladePicker;
import com.reclizer.csgobox.utils.random_pickers.RandomCurioPicker;
import com.reclizer.csgobox.utils.random_pickers.RandomFoodPicker;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.slf4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(CsgoBox.MOD_ID)
public class CsgoBox {

    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "csgobox";
    public static final Logger LOGGER = LogUtils.getLogger();
    // 仅在开发环境中为 true，构建发布前须改为 false
    public static final boolean DEBUG = true;

    public CsgoBox() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        ModSounds.SOUNDS.register(modEventBus);
        RecModMenus.register(modEventBus);
        ModItems.register(modEventBus);
        ModItems.registerTab(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(RandomCurioPicker.class);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // 生成默认配置文件
        Path configPath = FMLPaths.CONFIGDIR.get(); // 获取Minecraft配置目录
        Path folderPath = configPath.resolve("csbox");
        try {
            // 创建文件夹（如果不存在）
            if (!Files.exists(folderPath)) {
                Files.createDirectories(folderPath);
            }
            String content = """
                    [
                      {
                        "name": "Weapons Supply Box",
                        "key": "csgobox:csgo_key0",
                        "drop": 0,
                        "random": [
                          2,
                          5,
                          6,
                          20,
                          625
                        ],
                        "entity": [
                          "minecraft:zombie",
                          "minecraft:skeleton"
                        ],
                        "grade1": [
                          "minecraft:stone_sword.withTags{Damage:0}",
                          "minecraft:iron_axe.withTags{Damage:0}",
                          "minecraft:iron_shovel.withTags{Damage:0}",
                          "minecraft:iron_pickaxe.withTags{Damage:0}",
                          "minecraft:iron_axe.withTags{Damage:0}",
                          "minecraft:iron_hoe.withTags{Damage:0}",
                          "minecraft:iron_sword.withTags{Damage:0}"
                        ],
                        "grade2": [
                          "minecraft:golden_sword.withTags{Damage:0}",
                          "minecraft:golden_axe.withTags{Damage:0}",
                          "minecraft:golden_axe.withTags{Damage:0}",
                          "minecraft:golden_pickaxe.withTags{Damage:0}",
                          "minecraft:golden_shovel.withTags{Damage:0}"
                        ],
                        "grade3": [
                          "minecraft:diamond_shovel.withTags{Damage:0}",
                          "minecraft:diamond_pickaxe.withTags{Damage:0}",
                          "minecraft:diamond_hoe.withTags{Damage:0}"
                        ],
                        "grade4": [
                          "minecraft:diamond_axe.withTags{Damage:0}",
                          "minecraft:diamond_sword.withTags{Damage:0}"
                        ],
                        "grade5": [
                          "minecraft:netherite_sword.withTags{Damage:0,Enchantments:[{id:\\"minecraft:sharpness\\",lvl:4s},{id:\\"minecraft:fire_aspect\\",lvl:1s},{id:\\"minecraft:sweeping\\",lvl:2s}],RepairCost:7}",
                          "minecraft:netherite_axe.withTags{Damage:0}",
                          "minecraft:netherite_pickaxe.withTags{Damage:0}",
                          "minecraft:netherite_shovel.withTags{Damage:0}",
                          "minecraft:netherite_hoe.withTags{Damage:0}"
                        ]
                      }
                    ]""";

            if (!Files.exists(folderPath.resolve("box.json"))) {
                Path filePath = folderPath.resolve("box.json");
                // 创建文件并写入内容new FileWriter(filePath.toFile())
                try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath.toFile()), StandardCharsets.UTF_8))) {

                    writer.write(content);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 随机查找表缓存
        event.enqueueWork(RandomFoodPicker::initFoodCache);

        event.enqueueWork(() -> {
            try {
                CsgoBoxManage.loadConfigBox();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        Networking.registerMessages();
    }

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        RandomBladePicker.initBladeCache(event.getServer().overworld());
    }

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        RandomBladePicker.initBladeCache(event.getServer().overworld());
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            event.enqueueWork(RecModScreens::clientLoad);
        }
    }
}
