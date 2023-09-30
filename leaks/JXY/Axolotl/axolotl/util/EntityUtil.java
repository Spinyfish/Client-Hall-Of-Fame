package axolotl.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;

public class EntityUtil {

	public static Minecraft mc = Minecraft.getMinecraft();
	
	public static List<EntityLivingBase> getEntitiesAroundPlayer(double range) {
		  return getEntitiesAroundPlayer(range, true, true, true);
	}

	public static List<EntityLivingBase> getEntitiesAroundPlayer(double range, boolean mobs, boolean players, boolean animals) {

		List<EntityLivingBase> targetsAround = (List<EntityLivingBase>)mc.theWorld.loadedEntityList.stream().filter(EntityLivingBase.class::isInstance).collect(Collectors.toList());
		targetsAround = targetsAround.stream().filter(entity -> (entity.getDistanceToEntity(mc.thePlayer) < range && entity != mc.thePlayer && !entity.isDead && entity.getHealth() > 0.0F)).collect(Collectors.toList());

		List<EntityLivingBase> targets = new ArrayList<>();

		for(EntityLivingBase entity : targetsAround) {
			if((mobs && entity instanceof EntityMob) || (animals && entity instanceof EntityAnimal) || (players && entity instanceof EntityPlayer)) {
				targets.add(entity);
			}
		}
		targets.sort(Comparator.comparingDouble(entity -> entity.getDistanceToEntity(mc.thePlayer)));

		return targets;

	}

	public static List<EntityLivingBase> getEntitiesAroundPlayerTeams(double range, boolean mobs, boolean players, boolean animals) {

		List<EntityLivingBase> targetsAround = getEntitiesAroundPlayer(range, mobs, players, animals);
		List<EntityLivingBase> targets = new ArrayList<>();

		for(EntityLivingBase entity : targetsAround) {
			if(entity.getTeam() != mc.thePlayer.getTeam()) {
				targets.add(entity);
			}
		}

		return targets;

	}
	
	public static Entity getHoveredEntity() {
		return mc.objectMouseOver.entityHit;
    }
	
}
