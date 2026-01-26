package com.reclizer.csgobox.packet;


import net.minecraft.network.FriendlyByteBuf;
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
        ctx.get().enqueueWork(() -> {
        });
        ctx.get().setPacketHandled(true);
    }
}
