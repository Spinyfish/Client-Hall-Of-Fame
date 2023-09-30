package felix.module.impl.visuals;

import java.awt.Color;

import felix.module.Module;
import felix.value.Value;
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

public class Chams extends Module {
	
	public ColorValue hiddenColor = new ColorValue("Hidden Color", new Color(160, 0, 160).getRGB());
	public ColorValue visibleColor = new ColorValue("Visible Color", new Color(255, 0, 0).getRGB());
    
    public NumberValue<Integer> alpha = new NumberValue<>("Alpha", 255, 1, 255);

    public BooleanValue players = new BooleanValue("Players", true);
    public BooleanValue animals = new BooleanValue("Animals", true);
    public BooleanValue mobs = new BooleanValue("Monsters", false);
    
    public BooleanValue invisibles = new BooleanValue("Invisibles", false);
    
    public BooleanValue passives = new BooleanValue("Passives", true);
    
    public BooleanValue colored = new BooleanValue("Colored", false);
    public BooleanValue hands = new BooleanValue("Hands", false);
    public BooleanValue rainbow = new BooleanValue("Rainbow", false);
    public BooleanValue shadows = new BooleanValue("Cast Shadows", true);

    public Chams() {
        super("Chams", 0, ModuleCategory.VISUALS);
        addValues(players, animals, mobs, passives, colored, rainbow, shadows, alpha, visibleColor, hiddenColor);
    }

    public boolean isValid(EntityLivingBase entity) {
        return isValidType(entity) && entity.isEntityAlive() && (!entity.isInvisible() || invisibles.isEnabled());
    }

    private boolean isValidType(EntityLivingBase entity) {
        return (players.isEnabled() && entity instanceof EntityPlayer) || (mobs.isEnabled() && (entity instanceof EntityMob || entity instanceof EntitySlime) || (passives.isEnabled() && (entity instanceof EntityVillager || entity instanceof EntityGolem)) || (animals.isEnabled() && entity instanceof EntityAnimal));
    }
}
