package com.die_waechter.celestemod.server;

import java.util.HashMap;
import java.util.UUID;

import com.die_waechter.celestemod.celestemod;
import com.die_waechter.celestemod.common.dash.dashDescriptor;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

//Server only
public class PersistantDashMap extends SavedData {
    //This is a SavedData class that stores the dash data for each player.
    //Dashes that are performed during or before a world unloads are IGNORED. 
    //Only the number of max dashes is stored.
    //This works because I assume that the player is on the ground when the world is loaded again.


    public final HashMap<UUID, dashDescriptor> pDashMap = new HashMap<UUID, dashDescriptor>();

    @Override
    public CompoundTag save(CompoundTag tag) {
        celestemod.LOGGER.info("Saving dash map...");
        //For each UUID in the dashDescriptors HashMap, save the max number of dashes.
        for (UUID uuid : celestemod.ServerDH.dashDescriptors.keySet()) {
            tag.putInt(uuid.toString(), celestemod.ServerDH.dashDescriptors.get(uuid).maxNumberOfDashes);
        }
        return tag;
    }

    public void update() {
        setDirty();
        return;
    }
    
}
