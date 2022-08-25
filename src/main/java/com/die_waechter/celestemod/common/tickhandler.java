package com.die_waechter.celestemod.common;

import java.util.UUID;

import com.die_waechter.celestemod.celestemod;
import com.die_waechter.celestemod.common.dash.dHHelper;
import com.die_waechter.celestemod.common.packets.CelestePacketHandler;
import com.die_waechter.celestemod.common.packets.LoginDashPacket;
import com.die_waechter.celestemod.server.SaveAndLoadDashMap;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.util.thread.SidedThreadGroups;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;



public class tickhandler { 

    //This class handles tick events and "distributes" the execution to the correct classes.
    
    
    @SubscribeEvent
    public static void onEntityUpdate(LivingUpdateEvent event){

        //Debug:
        // celestemod.LOGGER.debug("Tick event received");

        if (event.getEntityLiving() instanceof Player) {

            UUID uuid = event.getEntityLiving().getUUID();
            dHHelper.update(uuid);
            if (dHHelper.getDashDescriptors().get(uuid).isInDash){
                Vec3 direction = dHHelper.getDashDescriptors().get(uuid).direction;
                // celestemod.LOGGER.debug("Setting Direction:" + direction.x + "," + direction.y + "," + direction.z);
                event.getEntityLiving().setDeltaMovement(direction);
                //Also cancel all fall distance.
                event.getEntityLiving().fallDistance = 0;
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerLoggedInEvent event){
        // Debug:
        // celestemod.LOGGER.debug("Player login event received");
        
        UUID uuid = event.getPlayer().getUUID();
        dHHelper.update(uuid);

        //If this is on server, send the max number of dashes to the client.
        if (Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER) {
            celestemod.LOGGER.debug("Player login! Sending to client...: Dashes: " + celestemod.ServerDH.getMaxDashes(uuid));
            ServerPlayer player = (ServerPlayer) event.getPlayer();

            CelestePacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player),new LoginDashPacket(celestemod.ServerDH.dashDescriptors.get(uuid).maxNumberOfDashes));
        }
    }


    @SubscribeEvent
    public static void onLivingFall(LivingFallEvent event){
        if (event.getEntity() instanceof Player){
            //If that player is in a dash, cancel the fall.
            UUID uuid = event.getEntity().getUUID();
            if (dHHelper.getDashDescriptors().get(uuid).isInDash){
                event.setCanceled(true);
            }
        }
    }


    //Config reload event.
    @SubscribeEvent
    public static void onConfigLoad(ModConfigEvent.Reloading event){
        celestemod.LOGGER.info("Config reloading...");
        Config.register(); //Reload config.
    }


    @SubscribeEvent
    public static void onServerStart(ServerStartedEvent event) {
        celestemod.LOGGER.debug("Server started event received; initializing dash map");
        //Initialize the server's dash map.
        celestemod.ServerDH.initDashDescriptors(event.getServer().overworld());
    }

    @SubscribeEvent
    public static void onServerStop(ServerStoppingEvent event) {
        celestemod.LOGGER.debug("Server stopping event received; saving dash map");
        //Save the server's dash map.
        //TODO: this needs to be reworked probably.
        SaveAndLoadDashMap saveMap = new SaveAndLoadDashMap();
        saveMap.save(event.getServer().overworld(), saveMap.create().pDashMap);
    }


}
