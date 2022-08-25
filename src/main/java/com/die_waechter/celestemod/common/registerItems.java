package com.die_waechter.celestemod.common;

import net.minecraft.world.item.Item;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import com.die_waechter.celestemod.celestemod;

public class registerItems {

    static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, celestemod.MODID);
    
    public static final RegistryObject<Item> DASH_ESSENCE_ITEM = ITEMS.register("dash_essence_item", () -> new dashEssenceItem()); // The internal name
    public static final RegistryObject<Item> DOUBLE_DASH_ESSENCE_ITEM = ITEMS.register("double_dash_essence_item", () -> new doubleDashEssenceItem()); // The internal name

    public static void registerAllItems() {
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        
    }

}
