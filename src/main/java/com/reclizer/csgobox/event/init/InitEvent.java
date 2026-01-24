package com.reclizer.csgobox.event.init;

import com.reclizer.csgobox.utils.random_pickers.RandomBladePicker;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import static com.reclizer.csgobox.CsgoBoxDrop.*;

public class InitEvent {
    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        RandomBladePicker.initBladeCache(event.getServer().overworld());
        if (DEBUG) LOGGER.debug("SlashBlade initialization fired on server STARTING.");
    }
}
