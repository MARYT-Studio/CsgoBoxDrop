package com.reclizer.csgobox.packet;


import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

public class PacketLookItem {
    private final int buttonID;

    public PacketLookItem(FriendlyByteBuf buffer) {
        this.buttonID = buffer.readInt();

    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(buttonID);

    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().setPacketHandled(true);
    }
}
