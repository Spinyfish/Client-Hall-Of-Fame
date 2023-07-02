package me.eldodebug.soar.management.mods.impl;

import me.eldodebug.soar.management.mods.Mod;
import me.eldodebug.soar.management.mods.ModCategory;
import me.eldodebug.soar.utils.ClientUtils;
import net.minecraft.client.entity.EntityOtherPlayerMP;

public class SpawnNPCMod extends Mod {

	private EntityOtherPlayerMP npc;
	
	public SpawnNPCMod() {
		super("Spawn NPC", "Spawn you as an NPC in the world", ModCategory.RENDER);
	}

	@Override
	public void onEnable() {
		super.onEnable();
		
		if(mc.thePlayer == null && mc.theWorld == null) {
			this.setToggled(false);
			return;
		}
		
		if(!mc.isSingleplayer()) {
			ClientUtils.showNotification("Mod", "This mod can only be used in single player");
			this.setToggled(false);
			return;
		}
		
		npc = new EntityOtherPlayerMP(mc.theWorld, mc.thePlayer.getGameProfile());
		npc.copyLocationAndAnglesFrom(mc.thePlayer);
		npc.setRotationYawHead(mc.thePlayer.rotationYawHead);
		
		mc.theWorld.addEntityToWorld(npc.getEntityId(), npc);
	}
	
	@Override
	public void onDisable() {
		super.onDisable();
		
		if(mc.thePlayer == null || mc.theWorld == null) {
			return;
		}
		
		if(npc != null) {
			mc.theWorld.removeEntityFromWorld(npc.getEntityId());
			npc = null;
		}
	}
}
