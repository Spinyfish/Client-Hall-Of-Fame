package felix.module.impl.visuals;

import java.awt.Color;

import felix.module.Module;
import felix.value.impl.BooleanValue;
import felix.value.impl.ColorValue;
import felix.value.impl.NumberValue;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;

public class OutlineESP extends Module {
	
	public ColorValue outlineColor = new ColorValue("Outline Color", new Color(0, 130, 255).getRGB());
	
    public NumberValue<Integer> alpha = new NumberValue<>("Alpha", 255, 1, 255);
    
    public NumberValue<Float> width = new NumberValue<>("Width", 3.0F, 1.0F, 10.0F, 0.01F);
    
    public BooleanValue players = new BooleanValue("Players", true);
    public BooleanValue animals = new BooleanValue("Animals", true);
    public BooleanValue mobs = new BooleanValue("Mobs", false);
    public BooleanValue invisibles = new BooleanValue("Invisibles", false);
    public BooleanValue passives = new BooleanValue("Passives", true);
    public BooleanValue rainbow = new BooleanValue("Raindow", false);

    public OutlineESP() {
        super("OutlineESP", 0, ModuleCategory.VISUALS);
        addValues(outlineColor, alpha, width, players, animals, mobs, invisibles, passives, rainbow);
    }

    public boolean isValid(EntityLivingBase entity) {
        return isValidType(entity) && entity.isEntityAlive() && (!entity.isInvisible() || invisibles.isEnabled());
    }

    private boolean isValidType(EntityLivingBase entity) {
        return (players.isEnabled() && entity instanceof EntityPlayer) || (mobs.isEnabled() && (entity instanceof EntityMob || entity instanceof EntitySlime) || (passives.isEnabled() && (entity instanceof EntityVillager || entity instanceof EntityGolem)) || (animals.isEnabled() && entity instanceof EntityAnimal));
    }
}
