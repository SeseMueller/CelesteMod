package com.die_waechter.celestemod.common.packets;

import java.util.UUID;

import com.die_waechter.celestemod.celestemod;
import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public class SuperDashPacket {
    //Holds a SuperDashPacket. This Packet is sent from the client to the server.

    public final int dashDirectionAsInt; //the INPUTTED dash direction (not the calculated one)

    public SuperDashPacket(int dashDirection) {
        this.dashDirectionAsInt = dashDirection;
    }

    public static SuperDashPacket decode(FriendlyByteBuf buf){
        int dashDirection = buf.readInt();
        return new SuperDashPacket(dashDirection);
    }

    public static void encode(SuperDashPacket packet, FriendlyByteBuf buf){
        buf.writeInt(packet.dashDirectionAsInt);
    }

    public static void handle(SuperDashPacket packet, Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(() -> {
            //  celestemod.LOGGER.debug("Handling SuperDashPacket...");
            ServerPlayer sender = ctx.get().getSender();
            UUID uuid = sender.getUUID();
            celestemod.ServerDH.superDash(uuid, packet.dashDirectionAsInt);

        });
        ctx.get().setPacketHandled(true); 
    }
}
