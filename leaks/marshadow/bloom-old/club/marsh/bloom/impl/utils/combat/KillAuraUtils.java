package club.marsh.bloom.impl.utils.combat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import club.marsh.bloom.impl.mods.combat.AntiBot;
import club.marsh.bloom.impl.mods.player.MiddleClickFriends;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

public class KillAuraUtils {
	public static Minecraft mc = Minecraft.getMinecraft();
	
	public static EntityLivingBase getClosest(double range)
	{
		List<EntityLivingBase> entities = new ArrayList<>();
		
		for (Entity entity : mc.theWorld.loadedEntityList)
		{
			if (entity instanceof EntityLivingBase && canAttack((EntityLivingBase) entity, range))
			{
				entities.add((EntityLivingBase) entity);
			}
		}
		
		entities.removeIf(entity -> entity.getHealth() == 0);
		entities.sort(Comparator.comparingInt(entity -> (int) -mc.thePlayer.getDistanceToEntity(entity)));
		Collections.reverse(entities);
		return entities.size() > 0 ? entities.get(0) : null;
	}
    
	public static boolean canAttack(EntityLivingBase entity, double reach)
	{
		if (entity == mc.thePlayer)
		{
			return false;
		}
		
		if (AntiBot.invalid.contains(entity))
		{
			return false;
		}
		
		if (entity.getHealth() == 0 || mc.thePlayer.isDead)
		{
			return false;
		}
		
		if (MiddleClickFriends.on && MiddleClickFriends.friends.contains(entity))
		{
			return false;
		}
		
		if (!mc.thePlayer.canEntityBeSeen(entity))
		{
			return false;
		}
		
		return mc.thePlayer.getDistanceToEntity(entity) <= reach;
	}
}
