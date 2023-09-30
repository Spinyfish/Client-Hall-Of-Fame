package haptic.module.impl.combat;

import java.util.ArrayList;

import haptic.Haptic;
import haptic.event.Event;
import haptic.event.impl.EventReceivePacket;
import haptic.event.impl.EventUpdate;
import haptic.module.Category;
import haptic.module.ListeningType;
import haptic.module.Module;
import haptic.setting.impl.BooleanSetting;
import haptic.setting.impl.NumberSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.world.WorldSettings.GameType;

public class Antibot extends Module {
	
	private NumberSetting ticksExisted = new NumberSetting("Ticks Existed", 40, 0, 200, 5, this);
	private BooleanSetting invisibleOnSpawn = new BooleanSetting("Invisible on spawn", true, this);
	private BooleanSetting hycraft = new BooleanSetting("Hycraft", false, this);
	
	private ArrayList<EntityLivingBase> blacklistedEntities = new ArrayList<>();
	
	public Antibot() {
		super("Antibot", Category.COMBAT, ListeningType.ALWAYS);
		this.addSettings(ticksExisted, invisibleOnSpawn, hycraft);
	}
	
	public void onEvent(Event event) {
		if(mc.thePlayer.ticksExisted >= 5 && !this.isEnabled()) {
			return;
		}
		
		if(event instanceof EventUpdate) {
			if(mc.thePlayer.ticksExisted < 5) {
				blacklistedEntities.clear();
			}
			if(mc.thePlayer.ticksExisted % 5 == 0) {
				for(Entity e : mc.theWorld.loadedEntityList) {
					if(e.ticksExisted < 10 && e.isInvisible() && e instanceof EntityLivingBase) {
						blacklistedEntities.add((EntityLivingBase) e);
						//Vestige.addChatMessage("Detected a bot : invisible on spawn antibot");
					}
				}
			}
		} else if(event instanceof EventReceivePacket) {
			EventReceivePacket e = (EventReceivePacket) event;
			if(mc.thePlayer.ticksExisted > 300 && mc.playerController.getCurrentGameType() == GameType.SURVIVAL) {
				if(hycraft.isEnabled()) {
					if(e.getPacket() instanceof S0CPacketSpawnPlayer) {
						S0CPacketSpawnPlayer packet = (S0CPacketSpawnPlayer) e.getPacket();
						blacklistedEntities.add(packet.getEntityPlayer());
						Haptic.addChatMessage("Detected a bot : hycraft antibot");
					}
				}
			}
		}
	}
	
	public boolean canAttack(EntityLivingBase e) {
		if(mc.thePlayer.ticksExisted < ticksExisted.getValue()) {
			return false;
		}
		
		if(blacklistedEntities.contains(e)) {
			return false;
		}
		
		return true;
	}

}