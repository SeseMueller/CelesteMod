package com.die_waechter.celestemod.server;

import java.util.HashMap;
import java.util.UUID;

import com.die_waechter.celestemod.celestemod;
import com.die_waechter.celestemod.common.dash.dashDescriptor;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;

public class SaveAndLoadDashMap {
    
    public PersistantDashMap create(){
        return new PersistantDashMap();
    }

    public PersistantDashMap load(CompoundTag tag) {
        //A new empty PersistantDashMap is created.
        PersistantDashMap persistantDashMap = this.create();

        //Load all the max dashes from the tag.
        tag.getAllKeys().forEach(key -> {
            dashDescriptor localDashDescriptor = new dashDescriptor();
            localDashDescriptor.maxNumberOfDashes = tag.getInt(key);
            persistantDashMap.pDashMap.put(UUID.fromString(key), localDashDescriptor);
        });

        return persistantDashMap;
    }

    public HashMap<UUID, dashDescriptor> getHashMap (ServerLevel level) {
        // Get the PersistantDashMap from the level.
        PersistantDashMap pMap = level.getDataStorage().computeIfAbsent(this::load, this::create, celestemod.MODID + ".dashMap");
        return pMap.pDashMap;
    }

    public void save (ServerLevel level, HashMap<UUID, dashDescriptor> pDashMap) {
        // Get the PersistantDashMap from the level.
        PersistantDashMap pMap = level.getDataStorage().computeIfAbsent(this::load, this::create, celestemod.MODID + ".dashMap");
        pMap.pDashMap.clear();
        pDashMap.forEach((key, value) -> pMap.pDashMap.put(key, value));
        pMap.update();
    }
    
}
