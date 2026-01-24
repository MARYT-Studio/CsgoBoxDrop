package com.reclizer.csgobox.packet;


import com.reclizer.csgobox.item.ItemCsgoBox;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketCsgoProgress {
    private final int buttonID;

    public PacketCsgoProgress(FriendlyByteBuf buffer) {
        this.buttonID = buffer.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(buttonID);
    }


    public PacketCsgoProgress(int buttonID) {
        this.buttonID = buttonID;
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();

        ctx.get().enqueueWork(() -> {
            Player entity = context.getSender();
            int buttonID = this.buttonID;

            if (buttonID == 2) {
                if (entity != null && entity.getMainHandItem().getItem() instanceof ItemCsgoBox) {
                    entity.getMainHandItem().shrink(1);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
