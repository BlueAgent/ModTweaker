package com.blamejared.compat.tconstruct.actions;

import com.blamejared.compat.tconstruct.materials.ITICMaterial;
import crafttweaker.IAction;
import slimeknights.tconstruct.library.materials.*;

public class SetDrawSpeedAction implements IAction {
    
    private final ITICMaterial material;
    private final String stat;
    private final float newValue;
    
    public SetDrawSpeedAction(ITICMaterial material, String stat, float newValue) {
        this.material = material;
        this.stat = stat;
        this.newValue = newValue;
    }
    
    private static void set(Material material, String stat, float drawSpeed) {
        IMaterialStats oldStat = material.getStats(stat);
        if(oldStat instanceof BowMaterialStats) {
            BowMaterialStats bowStat = (BowMaterialStats) oldStat;
            BowMaterialStats newBow = new BowMaterialStats(drawSpeed, bowStat.range, bowStat.bonusDamage);
            material.addStats(newBow);
        }
        
    }
    
    @Override
    public void apply() {
        set((Material) material.getInternal(), stat, newValue);
    }
    
    
    @Override
    public String describe() {
        return "Setting DrawSpeed of " + material.getName() + " to " + newValue + " for " + stat;
    }
    
}