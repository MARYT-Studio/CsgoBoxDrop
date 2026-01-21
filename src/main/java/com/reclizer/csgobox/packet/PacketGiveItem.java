package com.reclizer.csgobox.packet;

import com.reclizer.csgobox.utils.ItemNBT;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

import static com.reclizer.csgobox.utils.random_pickers.RandomBladePicker.fetchBladeStack;

public class PacketGiveItem {

    private String item;
    public PacketGiveItem(FriendlyByteBuf buf) {
        item=buf.readUtf();
    }

    //Encoder
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(item);
    }

    public PacketGiveItem(String item) {
        this.item=item;
    }



    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getSender() != null) {

                if(ctx.get().getSender() != null){
                    ServerPlayer player = ctx.get().getSender();
                    if (player == null) return;
                    ItemStack giveItem = ItemNBT.getStacks(item);
                    if(giveItem == null) return;
                    Inventory inventory = player.getInventory();
                    int emptySlot = -1;
                    for (int i = 0; i < 36; i++) {
                        if (inventory.getItem(i).isEmpty()) {
                            emptySlot = i;
                            break;
                        }
                    }
                    if (giveItem.getOrCreateTag().contains("keyIdForFetchBladeFromCache")) {
                        giveItem = fetchBladeStack(giveItem.getOrCreateTag().getInt("keyIdForFetchBladeFromCache"));
                        giveItem.getOrCreateTag().remove("keyIdForFetchBladeFromCache");
                    }
                    if (emptySlot != -1) {
                        player.getInventory().add(giveItem);
                    }else {
                        player.drop(giveItem,true,false);
                    }
                }

            }
        });
        ctx.get().setPacketHandled(true);
    }
}
