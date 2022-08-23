package com.die_waechter.celestemod.common.packets;

import java.util.UUID;
import java.util.function.Supplier;

import com.die_waechter.celestemod.celestemod;
import com.die_waechter.celestemod.common.dashEssenceItem;
import com.die_waechter.celestemod.common.dash.dHHelper;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.network.NetworkEvent;

public class DashNumberPacket {
    //Holds info about a change in the number of dashes a player has
    public final int newNumberOfDashes;
    public final UUID playerUUID;

    public DashNumberPacket(int newNumberOfDashes, UUID playerUUID) {
        this.newNumberOfDashes = newNumberOfDashes;
        this.playerUUID = playerUUID;
    }

    public static DashNumberPacket decode(FriendlyByteBuf buf) {
        // The Packet has first the number of Dashes and then the UUID.
        int numDashes = buf.readInt();
        UUID uuid = buf.readUUID();
        return new DashNumberPacket(numDashes, uuid);
    }

    public static void encode(DashNumberPacket packet, FriendlyByteBuf buf) {
        // The Packet has first the number of Dashes and then the UUID.
        buf.writeInt(packet.newNumberOfDashes);
        buf.writeUUID(packet.playerUUID);
    }

    public static void handle(DashNumberPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            
            celestemod.LOGGER.debug("Got a DNPacket, updating number of dashes...");

            //First makes sure that the number of dashes is either 1 or 2. 
            int safeNumberOfDashes = packet.newNumberOfDashes;
            if (safeNumberOfDashes != 1 && safeNumberOfDashes != 2) {
                safeNumberOfDashes = 1;
            }

            try {
                //The server now also needs to remove the dash Essence from the player. 
                Player player = ctx.get().getSender();
                Item heldItem = player.getMainHandItem().getItem();

                //If the heldStack is a dash Essence, remove it.
                if (!(heldItem instanceof dashEssenceItem)){
                    celestemod.LOGGER.warn("Player " + player.getName() + " tried to set the number of dashes to " + safeNumberOfDashes + " but he is not holding a dash Essence.");
                } else {
                    //Remove the dash Essence from the player. 
                    player.getMainHandItem().setCount(0);
                }

                //Sets the new number of Dashes on the Server side. 
                dHHelper.setMaxDashes(packet.playerUUID, safeNumberOfDashes);
                // celestemod.LOGGER.debug("Successfully set max dashes.");
            }
            catch (Exception e){
                celestemod.LOGGER.warn("The client tried to set the number of dashes to " + safeNumberOfDashes + " but the server could not remove the dash essence from the player.");
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
