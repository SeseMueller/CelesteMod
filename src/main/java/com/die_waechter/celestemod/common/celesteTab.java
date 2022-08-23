package com.die_waechter.celestemod.common;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class celesteTab extends CreativeModeTab {
    public static final CreativeModeTab celesteTab = new celesteTab("celesteTab");

    
    public celesteTab(String label) {
        super(label);
    }

    @Override
    public ItemStack makeIcon() {
        return new ItemStack(registerItems.DASH_ESSENCE_ITEM.get());
    }
}
    