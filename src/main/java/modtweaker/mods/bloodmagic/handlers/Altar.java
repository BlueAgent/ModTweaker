package modtweaker.mods.bloodmagic.handlers;

import WayofTime.bloodmagic.api.ItemStackWrapper;
import WayofTime.bloodmagic.api.altar.EnumAltarTier;
import WayofTime.bloodmagic.api.registry.AltarRecipeRegistry;
import WayofTime.bloodmagic.api.registry.AltarRecipeRegistry.AltarRecipe;
import com.blamejared.mtlib.helpers.LogHelper;
import com.blamejared.mtlib.helpers.ReflectionHelper;
import com.blamejared.mtlib.utils.BaseMapAddition;
import com.blamejared.mtlib.utils.BaseMapRemoval;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IIngredient;
import minetweaker.api.item.IItemStack;
import modtweaker.mods.bloodmagic.BloodMagicHelper;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static com.blamejared.mtlib.helpers.InputHelper.*;
import static com.blamejared.mtlib.helpers.StackHelper.matches;

@ZenClass("mods.bloodmagic.Altar")
public class Altar
{
    protected static final String name = "Blood Magic Altar";

    private static final EnumAltarTier[] altarTiers = EnumAltarTier.values();

    @ZenMethod
    public static void addRecipe(IItemStack output, int minTier, int syphon, int consumeRate, int drainRate, IItemStack[] input)
    {
        if (output == null) {
            LogHelper.logError(String.format("Required parameters missing for %s Recipe.", name));
            return;
        }
        else if(minTier <= 0 || minTier > altarTiers.length)
        {
            LogHelper.logWarning(String.format("Invalid altar tier (%d) required for %s Recipe", minTier, Altar.name));
            return;
        }
        else if(syphon < 0)
        {
            LogHelper.logWarning(String.format("Syphon can't be below 0 (%d) for %s Recipe", syphon, Altar.name));
            return;
        }
        else if(consumeRate < 0)
        {
            LogHelper.logWarning(String.format("Consume rate can't be below 0 (%d) for %s Recipe", consumeRate, Altar.name));
            return;
        }
        else if(drainRate < 0)
        {
            LogHelper.logWarning(String.format("Drain rate can't be below 0 (%d) for %s Recipe", drainRate, Altar.name));
            return;
        }

        List<ItemStack> inputs = Arrays.asList(toStacks(input));
        AltarRecipeRegistry.AltarRecipe temp = new AltarRecipeRegistry.AltarRecipe(inputs, toStack(output), altarTiers[minTier-1], syphon, consumeRate, drainRate);
        MineTweakerAPI.apply(new Add(temp.getInput(), temp, BloodMagicHelper.altarBiMap));
    }

    private static class Add extends BaseMapAddition<List<ItemStackWrapper>, AltarRecipe>
    {
        public Add(List<ItemStackWrapper> inputs, AltarRecipe altarRecipe, Map<List<ItemStackWrapper>, AltarRecipe> map)
        {
            super(Altar.name, map);
            this.recipes.put(inputs, altarRecipe);
        }

        @Override
        public String getRecipeInfo(Entry<List<ItemStackWrapper>, AltarRecipe> recipe)
        {
            ItemStack output = ReflectionHelper.getFinalObject(recipe.getValue(), "output");
            return LogHelper.getStackDescription(output);
        }
    }

    @ZenMethod
    public static void removeRecipe(IIngredient output)
    {
        remove(output, BloodMagicHelper.altarBiMap);
    }

    public static void remove(IIngredient output, Map<List<ItemStackWrapper>, AltarRecipe> map)
    {
        if (output == null) {
            LogHelper.logError(String.format("Required parameters missing for %s Recipe.", name));
            return;
        }

        Map<List<ItemStackWrapper>, AltarRecipe> recipes = new HashMap<>();

        for(AltarRecipe altarRecipe : map.values())
        {
            ItemStack recipeOutput = ReflectionHelper.getFinalObject(altarRecipe, "output");
            if(matches(output, toIItemStack(recipeOutput))) {
                recipes.put(altarRecipe.getInput(), altarRecipe);
            }
        }

        if(!recipes.isEmpty())
        {
            MineTweakerAPI.apply(new Remove(map, recipes));
        }
        else
        {
            LogHelper.logWarning(String.format("No %s Recipe found for output %s. Command ignored!", Altar.name, output.toString()));
        }
    }

    private static class Remove extends BaseMapRemoval<List<ItemStackWrapper>, AltarRecipe>
    {
        public Remove(Map<List<ItemStackWrapper>, AltarRecipe> map, Map<List<ItemStackWrapper>, AltarRecipe> inputs)
        {
            super(Altar.name, map, inputs);
        }

        @Override
        public String getRecipeInfo(Entry<List<ItemStackWrapper>, AltarRecipe> recipe)
        {
            ItemStack output = ReflectionHelper.getFinalObject(recipe.getValue(), "output");
            return LogHelper.getStackDescription(output);
        }
    }
}
