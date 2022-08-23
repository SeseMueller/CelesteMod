package com.die_waechter.celestemod.common.packets;

import java.util.function.Supplier;

import com.die_waechter.celestemod.celestemod;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

public class LoginDashClientHandler {
    


    public static void handlePacket(LoginDashPacket LDG, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            int numDashes = LDG.numDashes;
            Player player = Minecraft.getInstance().player;
            celestemod.LOGGER.debug("Handling LDPacket... got Dashes: "+ numDashes);
            celestemod.ClientDH.clientSetMaxDashes(player.getUUID() ,numDashes);
        });
        ctx.get().setPacketHandled(true);
    }

    
}
