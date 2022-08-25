package com.die_waechter.celestemod.server;

import java.util.List;

import javax.annotation.Nullable;

import com.google.gson.JsonObject;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;

public class DoubleDashEssenceInEndCity extends LootModifier{

    private final Item addition;

    protected DoubleDashEssenceInEndCity(LootItemCondition[] conditionsIn, Item addition) {
        super(conditionsIn);
        this.addition = addition;
    }

    @Nullable
    @Override
    protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
        
        if(context.getRandom().nextFloat() < 0.2f) {
            generatedLoot.add(new ItemStack(addition, 1));
        }
        return generatedLoot;
    }
 
    
    public static class Serializer extends GlobalLootModifierSerializer<DoubleDashEssenceInEndCity> {
        
        @Override
        public DoubleDashEssenceInEndCity read(ResourceLocation location, JsonObject object, LootItemCondition[] conditions) {
            Item addition = ForgeRegistries.ITEMS.getValue(
                new ResourceLocation(GsonHelper.getAsString(object, "addition")));
            return new DoubleDashEssenceInEndCity(conditions, addition);
        }
 
        @Override
        public JsonObject write(DoubleDashEssenceInEndCity instance) {
            JsonObject json = makeConditions(instance.conditions);
            json.addProperty("addition", ForgeRegistries.ITEMS.getKey(instance.addition).toString());
            return json;
        }
    }

}