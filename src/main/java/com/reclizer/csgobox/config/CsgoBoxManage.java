package com.reclizer.csgobox.config;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.reclizer.csgobox.CsgoBoxDrop;
import com.reclizer.csgobox.item.ItemCsgoBox;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

public class CsgoBoxManage {

    private static final Gson GSON = new Gson();
    private static final Path CONFIG_DIR = Paths.get("config").resolve("csbox");
    private static final Path CONFIG_FILE = CONFIG_DIR.resolve("box.json");

    public static List<ItemCsgoBox.BoxInfo> BOX = Lists.newArrayList();

    public static void loadConfigBox() throws IOException {
        if (!Files.isDirectory(CONFIG_DIR)) {
            Files.createDirectories(CONFIG_DIR);
        }

        File file = CONFIG_FILE.toFile();
        InputStream stream = null;
        if (Files.exists(CONFIG_FILE)) {
            stream = Files.newInputStream(file.toPath());
        } else {
            ResourceLocation res = new ResourceLocation(CsgoBoxDrop.MOD_ID, "box.json");
            Optional<Resource> optional = Minecraft.getInstance().getResourceManager().getResource(res);
            if (optional.isPresent()) {
                stream = optional.get().open();
            }
        }
        if (stream != null) {
            BOX = GSON.fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8),
                    new TypeToken<List<ItemCsgoBox.BoxInfo>>() {
                    }.getType());
        }
    }


}
