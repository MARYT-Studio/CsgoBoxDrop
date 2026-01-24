package com.reclizer.csgobox.packet;


import com.reclizer.csgobox.item.ItemCsgoBox;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;
import java.util.function.Supplier;

public class PacketCsgoProgress {
    private final int buttonID;
    private final String item;
    public PacketCsgoProgress(FriendlyByteBuf buffer) {
        this.buttonID = buffer.readInt();
        this.item=buffer.readUtf();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(buttonID);
        buf.writeUtf(item);
    }


    public PacketCsgoProgress(int buttonID,String item) {
        this.buttonID = buttonID;
        this.item=item;
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();

        ctx.get().enqueueWork(() -> {
            Player entity = context.getSender();
            int buttonID = this.buttonID;

            if (buttonID == 2) {
                if (entity != null && entity.getMainHandItem().getItem() instanceof ItemCsgoBox) {
                    entity.getMainHandItem().shrink(1);
                    for (ItemStack stack : entity.getInventory().items) {
                        if (Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(stack.getItem())).toString().equals(item) ) {
                            stack.shrink(1);

                        }
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
