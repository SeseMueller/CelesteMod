package com.die_waechter.celestemod.server;

import java.util.UUID;

import com.die_waechter.celestemod.celestemod;
import com.die_waechter.celestemod.common.dash.dashHandler;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.server.ServerLifecycleHooks;

public class ServerDashHandler extends dashHandler {

    @Override
    public Player getPlayer(UUID uuid) {
        return ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(uuid);
    }

    @Override
    public Player getPlayer() {
        //This method should never be called on the server.
        celestemod.LOGGER.error("The server tried to get the player from the client, but this should never happen.");
        return null;
    }
    
}
