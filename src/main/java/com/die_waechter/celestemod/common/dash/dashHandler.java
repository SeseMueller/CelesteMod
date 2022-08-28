package com.die_waechter.celestemod.common.dash;

import java.util.HashMap;
import java.util.UUID;

import com.die_waechter.celestemod.celestemod;
import com.die_waechter.celestemod.common.packets.CelestePacketHandler;
import com.die_waechter.celestemod.common.packets.DashNumberPacket;
import com.die_waechter.celestemod.common.packets.DashPacket;
import com.die_waechter.celestemod.server.SaveAndLoadDashMap;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.util.thread.SidedThreadGroups;
import net.minecraftforge.server.ServerLifecycleHooks;

public abstract class dashHandler {

    public HashMap<UUID, dashDescriptor> dashDescriptors = new HashMap<UUID, dashDescriptor>();

    public Vec3 velocityNextFrame = Vec3.ZERO;
    public Vec3 velocityNextFrame1 = Vec3.ZERO;

    public void initDashDescriptors(ServerLevel level) {
        celestemod.LOGGER.debug("Initializing dash descriptors");
        //Clear dash Descriptors. If this wasn't done, dashes from one server will carry over because the data wouldn't be refreshed.
        dashDescriptors.clear();
        SaveAndLoadDashMap dashMapLoader = new SaveAndLoadDashMap();
        dashMapLoader.getHashMap(level).forEach((uuid, dashDescriptor) -> {
            celestemod.LOGGER.info("Found a dash descriptor for " + uuid.toString());
            dashDescriptors.put(uuid, dashDescriptor);
        });
    }
    
    public int getDashes(UUID uuid) {

        if (!dashDescriptors.containsKey(uuid)){
            dashDescriptors.put(uuid, new dashDescriptor());
        }

        return dashDescriptors.get(uuid).numberOfDashes;
    }

    public int getMaxDashes(UUID uuid) {

        if (!dashDescriptors.containsKey(uuid)){
            dashDescriptors.put(uuid, new dashDescriptor());
        }

        return dashDescriptors.get(uuid).maxNumberOfDashes;
    }

    //Client side only
    public void clientSetMaxDashes(UUID uuid, int maxNumberOfDashes) {
        //This is called when a loginDashPacket is received from the server.
        //The server is not notified by this, because he sent the packet to the client.

        if (!dashDescriptors.containsKey(uuid)){
            dashDescriptors.put(uuid, new dashDescriptor());
        }

        dashDescriptors.get(uuid).maxNumberOfDashes = maxNumberOfDashes;
    }

    //strictly both sides. 
    public void setMaxDashes(UUID uuid, int maxNumberOfDashes) {

        if (!dashDescriptors.containsKey(uuid)){
            dashDescriptors.put(uuid, new dashDescriptor());
        }

        dashDescriptors.get(uuid).maxNumberOfDashes = maxNumberOfDashes;

        //Send packet to server, if this is client side.
        if (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER) { 
            // celestemod.LOGGER.debug("Sending to server...");
            CelestePacketHandler.INSTANCE.sendToServer(new DashNumberPacket(maxNumberOfDashes, uuid));
        }
    }

    //This is called every tick on both sides.
    public void update(UUID uuid){
        //Updates the dashdescriptor of the entity with the given UUID
        // celestemod.LOGGER.debug("Updating dashHandler.");

        
        
        if (!dashDescriptors.containsKey(uuid)){
            dashDescriptors.put(uuid, new dashDescriptor());
        }

        // if (dashDescriptors.get(uuid).isInDash) {
        //     celestemod.LOGGER.debug("Updating dashHandler. isInDash is true.");
        // }

        dashDescriptors.get(uuid).update();
        setVelocity(uuid);
        updateGroundedness(uuid);

        //Update number of dahes. 
        dashDescriptor d = dashDescriptors.get(uuid);

        Player currentPlayer = getPlayer(uuid);

        if (( (!d.isInDash ) && d.ticksNotOnGround == 0)
        || ( d.isInDash && d.ticksNotOnGround < 2 && d.activeDashTicks > 6) 
        || ( d.isInDash && currentPlayer.isInWater())
        ){
            if (d.numberOfDashes < d.maxNumberOfDashes){
                d.numberOfDashes = d.maxNumberOfDashes;
            }
        }
        
        //Debug:
        // celestemod.LOGGER.debug(currentPlayer.getDeltaMovement().toString());

    }

    public void setVelocity(UUID uuid){
        // celestemod.LOGGER.debug("Setting velocity.");
        // Sets the velocities for a player with the given UUID.
        if (dashDescriptors.get(uuid).isInDash){
            // celestemod.LOGGER.debug("Setting velocity");
            Vec3 direction = dashDescriptors.get(uuid).direction;
            
            Player currentPlayer = getPlayer(uuid);

            // If the player is null, it is assumed that he is offline.
            if (currentPlayer != null){
                
                direction.add(currentPlayer.getDeltaMovement());
                currentPlayer.setDeltaMovement(direction);
                //FIXME: this seems wrong.
            }
        } else if (velocityNextFrame != Vec3.ZERO){
            celestemod.LOGGER.debug("Setting velocity: velocityNextFrame is: " + velocityNextFrame.toString());
            Player currentPlayer = getPlayer(uuid);
            if (currentPlayer != null){
                currentPlayer.hurtMarked = true;
                currentPlayer.setDeltaMovement(velocityNextFrame);
            }
            velocityNextFrame = Vec3.ZERO;
        }
        velocityNextFrame = velocityNextFrame1;
        velocityNextFrame1 = Vec3.ZERO;
    }

    public void updateGroundedness(UUID uuid){
        //Updates all dashDescriptors considering how long the player has been on the ground for.

        Player currentPlayer = getPlayer(uuid);
        
        // If the player is null, it is assumed that he is offline.
        if (currentPlayer == null){
            return;
        }

        //This ensures that the player cannot dash one too many times from a single block.
        if (currentPlayer.isOnGround() || currentPlayer.isInWater()){
            dashDescriptors.get(uuid).ticksNotOnGround = 0;
        } else {
            dashDescriptors.get(uuid).ticksNotOnGround++;
        }

        
    }

    public void potentiallyRegister(UUID uuid){
        //Registers the player with the dashDescriptor if he is not already registered.
        if (!dashDescriptors.containsKey(uuid)){
            celestemod.LOGGER.debug("Player " + uuid + " registered");
            dashDescriptors.put(uuid, new dashDescriptor());
        }
    }

    //Implied Client-side
    public void dash(ClientTickEvent event, int dashDirection){
        //Called when the player presses the dash key.
        //Only the rising edge is sent.

        //Debug:
        // celestemod.LOGGER.debug("Dash event received");
        
        // UUID uuid = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayerByUUID(event.player.getUUID()).getUUID();
        Player player = getPlayer();
        UUID uuid = player.getUUID(); //TODO: Also possible ressource leak?
        float yaw = player.yRotO;

        //If the player is already in a dash and not in the last three ticks, he cannot dash again.
        if (dashDescriptors.get(uuid).isInDash && dashDescriptors.get(uuid).activeDashTicks < 7){
            return;
        }

        dashDescriptors.get(uuid).dash(dashDirection, yaw, player.isCreative());

        //Sends a packet to the server.
        CelestePacketHandler.INSTANCE.sendToServer( new DashPacket(dashDirection)); 
    }

    //Implied Server-side
    public void dash(UUID uuid, int dashDirection){

        // celestemod.LOGGER.debug("Received dash event from client:"+dashDirection);

        if (!dashDescriptors.containsKey(uuid)){
            dashDescriptors.put(uuid, new dashDescriptor());
        }

        //If the player doesn't have at least one dash, return.
        if (dashDescriptors.get(uuid).numberOfDashes == 0){
            return;
        }
        
        //Get Player Yaw
        ServerPlayer player = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(uuid);

        if (player == null){
            celestemod.LOGGER.debug("Player " + uuid + " is offline. Dash canceled.");
            return;
        }

        float yaw = player.yRotO;
        dashDescriptors.get(uuid).dash(dashDirection, yaw, player.isCreative());

    }

    // strict Server-side
    public void waveDash(UUID uuid, int dashDirectionAsInt) {
        // celestemod.LOGGER.debug("Received wavedash event from client:"+dashDirectionAsInt);

        if (!dashDescriptors.containsKey(uuid)){
            dashDescriptors.put(uuid, new dashDescriptor());
        }

        //Checks if the player could have wavedashed.
        if (!(dashDescriptors.get(uuid).isInDash)){
            celestemod.LOGGER.debug("Player is not in dash, cancelling waveDash.");
            return;
        }
        if (dashDescriptors.get(uuid).activeDashTicks < 3){
            return;
        }
        if (!dashDirections.isValidHyperDirection(dashDirectionAsInt)){
            return;
        }
        
        ServerPlayer player = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(uuid);
        
        if (player == null){
            celestemod.LOGGER.debug("Player " + uuid + " is offline. wavedash canceled.");
            return;
        }
        
        if(!player.isOnGround()){
            return;
        }
         
        //The player can wavedash.
        //End the current dash.
        dashDescriptors.get(uuid).isInDash = false;

        //Apply the wavedash. The Hyper and Wave Directions are the same.
        Vec3 direction = dashDirections.getHyperDashDirection(dashDescriptors.get(uuid).direction);

        direction.add(player.getDeltaMovement());
        // celestemod.LOGGER.debug("wavedashing in direction:"+direction.toString()); 
        // player.setDeltaMovement(direction);
        velocityNextFrame = direction; //This needs to be done this way because otherwise the jump function would interfere.
        
    }

    //Strict Server-side
    public void superDash(UUID uuid, int dashDirectionAsInt) {
        // celestemod.LOGGER.debug("Received superdash event from client:"+dashDirectionAsInt);
        
        if (!dashDescriptors.containsKey(uuid)){
            dashDescriptors.put(uuid, new dashDescriptor());
        }
        
        //Checks if the player could have superdashed.
        if (!(dashDescriptors.get(uuid).isInDash)){
            celestemod.LOGGER.debug("Player is not in dash, cancelling superdash.");
            return;
        }
        if (dashDescriptors.get(uuid).activeDashTicks < 3){
            return;
        }
        if (!dashDirections.isValidSuperDirection(dashDirectionAsInt)){
            return;
        }
        
        ServerPlayer player = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(uuid);
        
        if (player == null){
            celestemod.LOGGER.debug("Player " + uuid + " is offline. superdash canceled.");
            return;
        }
        
        if(!player.isOnGround()){
            return;
        }
         
        //The player can superdash.
        //End the current dash.
        dashDescriptors.get(uuid).isInDash = false;
        
        //Apply the superdash.
        Vec3 direction = dashDirections.getSuperDashDirection(dashDescriptors.get(uuid).direction);
        
        direction.add(player.getDeltaMovement());
        celestemod.LOGGER.debug("superdashing in direction:"+direction.toString()); 
        // player.setDeltaMovement(direction);
        velocityNextFrame = direction; //This needs to be done this way because otherwise the jump function would interfere.
        
    }

    //this function is supposed to be implemented on both sides.
    public abstract Player getPlayer(UUID uuid);

    //This function is supposed to only be implemented on client. The server will throw an error if it is called.
    public abstract Player getPlayer();

}
