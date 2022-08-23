package com.die_waechter.celestemod.common.packets;

import java.util.UUID;

import com.die_waechter.celestemod.celestemod;
import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;


public class WaveDashPacket {
     //Holds a WaveDashPacket. This packet is sent from the client to the server.

     public final int dashDirectionAsInt; //the INPUTTED dash direction (not the calculated one)

     public WaveDashPacket(int dashDirection) {
         this.dashDirectionAsInt = dashDirection;
     }
 
     public static WaveDashPacket decode(FriendlyByteBuf buf){
         int dashDirection = buf.readInt();
         return new WaveDashPacket(dashDirection);
     }
 
     public static void encode(WaveDashPacket packet, FriendlyByteBuf buf){
         buf.writeInt(packet.dashDirectionAsInt);
     }
 
     public static void handle(WaveDashPacket packet, Supplier<NetworkEvent.Context> ctx){
         ctx.get().enqueueWork(() -> {
            //  celestemod.LOGGER.debug("Handling WaveDashPacket...");
             ServerPlayer sender = ctx.get().getSender();
             UUID uuid = sender.getUUID();
             celestemod.ServerDH.waveDash(uuid, packet.dashDirectionAsInt);
 
         });
         ctx.get().setPacketHandled(true); 
     }
}
