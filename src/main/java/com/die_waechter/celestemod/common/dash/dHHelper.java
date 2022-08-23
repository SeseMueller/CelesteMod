package com.die_waechter.celestemod.common.dash;

import java.util.HashMap;
import java.util.UUID;

import com.die_waechter.celestemod.celestemod;

import net.minecraftforge.fml.util.thread.SidedThreadGroups;

public class dHHelper {
    //Helps the execution and use of the dashHandler class. 
    //Because there are two dashHandler instances, ofter for updates, one would need to manually test whether they are Client or Server.
    //This class helps with that.

    /**
     * @return the dashHandler instance for the current side.
     */
    public static dashHandler getCorrectDH(){
        if (Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER) {
            return celestemod.ServerDH;
        } else {
            return celestemod.ClientDH;
        }
    }

    public static int getDashes(UUID uuid){
        return getCorrectDH().getDashes(uuid);
    }

    public static int getMaxDashes(UUID uuid){
        return getCorrectDH().getMaxDashes(uuid);
    }

    public static void setMaxDashes(UUID uuid, int maxDashes){
        getCorrectDH().setMaxDashes(uuid, maxDashes);
    }

    public static void update(UUID uuid){
        getCorrectDH().update(uuid);
    }

    public static HashMap<UUID, dashDescriptor> getDashDescriptors(){
        return getCorrectDH().dashDescriptors;
    }
}
