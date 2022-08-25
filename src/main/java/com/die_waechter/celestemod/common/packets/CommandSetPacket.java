package com.die_waechter.celestemod.common.packets;

import java.util.UUID;
import java.util.function.Supplier;

import com.die_waechter.celestemod.celestemod;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

public class CommandSetPacket {
    
    //Holds info abou a change in the numebr of dashes a player has, by command
    public final int numDashes;
    public final UUID playerUUID;

    public CommandSetPacket(int numDashes, UUID playerUUID) {
        this.numDashes = numDashes;
        this.playerUUID = playerUUID;
    }

    public static CommandSetPacket decode(FriendlyByteBuf buf) {
        int numDashes = buf.readInt();
        UUID playerUUID = buf.readUUID();
        return new CommandSetPacket(numDashes, playerUUID);
    }

    public static void encode(CommandSetPacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.numDashes);
        buf.writeUUID(packet.playerUUID);
    }

    public static void handle(CommandSetPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(()-> {
            celestemod.LOGGER.debug("Got CommandSetPacket...");

            //First makes sure that the number of dashes is either 1 or 2.
            int safeNumberOfDashes = packet.numDashes;
            if (safeNumberOfDashes != 1 && safeNumberOfDashes != 2) {
                safeNumberOfDashes = 1;
            }

            //First makes sure that the player has the permission to set the number of dashes.
            Player player = ctx.get().getSender();
            CommandSourceStack source = player.createCommandSourceStack();
            MinecraftServer server = player.getServer();
            if (server == null){
                celestemod.LOGGER.warn("Player" + player.getName() + " tried to set the number of dashes to " + safeNumberOfDashes + " but he is not on a server."); 
            }
            //gets the OP level from the server.
            int opLevel = server.getOperatorUserPermissionLevel();
            if (source.hasPermission(opLevel)){
                celestemod.ServerDH.setMaxDashes(player.getUUID(), safeNumberOfDashes);
            } else{
                celestemod.LOGGER.warn("Player" + player.getName() + " tried to set the number of dashes to " + safeNumberOfDashes + " but he is not an OP.");
            }
            


        });
        ctx.get().setPacketHandled(true);
    }

}
