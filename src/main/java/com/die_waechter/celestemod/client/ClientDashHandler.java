package com.die_waechter.celestemod.client;

import java.util.UUID;

import com.die_waechter.celestemod.common.dash.dashHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public class ClientDashHandler extends dashHandler{

    @Override
    public Player getPlayer(UUID uuid) {
        return Minecraft.getInstance().player;
    }

    @Override
    public Player getPlayer() {
        return Minecraft.getInstance().player;
    }
    
}
