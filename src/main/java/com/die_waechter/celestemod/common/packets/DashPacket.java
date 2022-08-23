package com.die_waechter.celestemod.common.packets;

import java.util.UUID;

import com.die_waechter.celestemod.celestemod;
import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public class DashPacket {
    
    //Holds a DashPacket. This packet is sent from the client to the server.

    public final int dashDirectionAsInt;

    public DashPacket(int dashDirection) {
        this.dashDirectionAsInt = dashDirection;
    }

    public static DashPacket decode(FriendlyByteBuf buf){
        int dashDirection = buf.readInt();
        return new DashPacket(dashDirection);
    }

    public static void encode(DashPacket packet, FriendlyByteBuf buf){
        buf.writeInt(packet.dashDirectionAsInt);
    }

    public static void handle(DashPacket packet, Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(() -> {
            // celestemod.LOGGER.debug("Handling DashPacket...");
            ServerPlayer sender = ctx.get().getSender();
            UUID uuid = sender.getUUID();
            celestemod.ServerDH.dash(uuid, packet.dashDirectionAsInt);

        });
        ctx.get().setPacketHandled(true); 
    }
}
