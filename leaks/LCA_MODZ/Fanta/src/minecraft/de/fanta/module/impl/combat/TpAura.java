package de.fanta.module.impl.combat;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



import de.fanta.Client;
import de.fanta.events.Event;
import de.fanta.events.listeners.EventPreMotion;
import de.fanta.events.listeners.EventTick;
import de.fanta.events.listeners.EventUpdate;
import de.fanta.module.Module;
import de.fanta.module.impl.combat.Killaura;
import de.fanta.module.impl.movement.Teleport;
import de.fanta.setting.Setting;
import de.fanta.setting.settings.CheckBox;
import de.fanta.setting.settings.DropdownBox;
import de.fanta.setting.settings.Slider;
import de.fanta.utils.ChatUtil;
import de.fanta.utils.FriendSystem;
import de.fanta.utils.PathFinder;
import de.fanta.utils.TimeUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.Vec3;
import tv.twitch.chat.Chat;

public class TpAura extends Module {
	public TpAura() {
		super("TpAura", 0, Type.Combat, Color.yellow);
		this.settings.add(new Setting("Delay", new Slider(0, 5000, 1, 50)));
	}

	TimeUtil time = new TimeUtil();
	public double delay;
	
	@Override
	public void onEvent(Event e) {
		
		

		if (e instanceof EventUpdate) {
			
			delay = ((Slider) this.getSetting("Delay").getSetting()).curValue;
			if(time.hasReached((long) delay)) {
				attack(modes());
				
				time.reset();
			}
		}
	}
	
	public Entity modes() {
		Entity target = null;
		
			for(EntityPlayer entity : mc.theWorld.playerEntities) {
				if(entity != mc.thePlayer)
				
				if(target == null || entity.getDistanceToEntity(mc.thePlayer) < target.getDistanceToEntity(mc.thePlayer)) {
					target = entity;
					System.out.println(entity);
				}
				
			}
		return target;
	}

	public void attack(Entity entity) {
		if(entity == null) {
		//	ChatUtil.sendChatMessage("is null");
			return;
		}

		final List<Vec3> path = calculatePath(mc.thePlayer.getPositionVector(), entity.getPositionVector());

		for(Vec3 pos : path) {
			if(FriendSystem.isFriendString(entity.getName()))return;
			mc.getNetHandler().addToSendQueue(
					new C03PacketPlayer.C04PacketPlayerPosition(pos.xCoord, pos.yCoord, pos.zCoord, false));
			
		}
		mc.thePlayer.swingItem();
		mc.playerController.attackEntity(mc.thePlayer, entity);
		mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(entity, Action.ATTACK));
		
		Collections.reverse(path);
		for(Vec3 pos : path) {
			mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(pos.xCoord, pos.yCoord, pos.zCoord, false));
		}
	}

	public static List<Vec3> calculatePath(Vec3 startPos, Vec3 endPos) {
		System.out.println("Test-1");
		final PathFinder pathfinder = new PathFinder(startPos, endPos);
		System.out.println("Test");
		pathfinder.calculatePath(5000);
		System.out.println("Test2");
		int i = 0;
		Vec3 lastLoc = null;
		Vec3 lastDashLoc = null;
		final List<Vec3> path = new ArrayList<Vec3>();
		final List<Vec3> pathFinderPath = pathfinder.getPath();
		for (final Vec3 pathElm : pathFinderPath) {
			if (i == 0 || i == pathFinderPath.size() - 1) {
				if (lastLoc != null) {
					path.add(lastLoc.addVector(0.5, 0, 0.5));
				}
				path.add(pathElm.addVector(0.5, 0, 0.5));
				lastDashLoc = pathElm;
			} else {
				boolean canContinue = true;
				if (pathElm.squareDistanceTo(lastDashLoc) > 30) {
					canContinue = false;
				} else {
					final double smallX = Math.min(lastDashLoc.xCoord, pathElm.xCoord);
					final double smallY = Math.min(lastDashLoc.yCoord, pathElm.yCoord);
					final double smallZ = Math.min(lastDashLoc.zCoord, pathElm.zCoord);
					final double bigX = Math.max(lastDashLoc.xCoord, pathElm.xCoord);
					final double bigY = Math.max(lastDashLoc.yCoord, pathElm.yCoord);
					final double bigZ = Math.max(lastDashLoc.zCoord, pathElm.zCoord);
					cordsLoop: for (int x = (int) smallX; x <= bigX; x++) {
						for (int y = (int) smallY; y <= bigY; y++) {
							for (int z = (int) smallZ; z <= bigZ; z++) {
								if (!PathFinder.checkPositionValidity(x, y, z, false)) {
									canContinue = false;
									break cordsLoop;
								}
							}
						}
					}
				}
				if (!canContinue) {
					path.add(lastLoc.addVector(0.5, 0, 0.5));
					lastDashLoc = lastLoc;
				}
			}
			lastLoc = pathElm;
			i++;
		}
		return path;
	}
}