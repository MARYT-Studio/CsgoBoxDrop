package com.reclizer.csgobox.packet;

import com.reclizer.csgobox.CsgoBox;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class Networking {
    public static SimpleChannel INSTANCE;

    private static int ID = 0;

    public static int nextID() {
        return ID++;
    }

    public static void registerMessages() {
        INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(CsgoBox.MODID, "network"), () -> "1.0", s -> true, s -> true);




        INSTANCE.registerMessage(nextID(),
                PacketCsgoProgress.class,
                PacketCsgoProgress::toBytes,
                PacketCsgoProgress::new,
                PacketCsgoProgress::handle);

        INSTANCE.registerMessage(nextID(),
                PacketUpdateMode.class,
                PacketUpdateMode::toBytes,
                PacketUpdateMode::new,
                PacketUpdateMode::handle);



        INSTANCE.registerMessage(nextID(),
                PacketGiveItem.class,
                PacketGiveItem::toBytes,
                PacketGiveItem::new,
                PacketGiveItem::handle);

        INSTANCE.registerMessage(nextID(),
                PacketLookItem.class,
                PacketLookItem::toBytes,
                PacketLookItem::new,
                PacketLookItem::handle);



    }


}
