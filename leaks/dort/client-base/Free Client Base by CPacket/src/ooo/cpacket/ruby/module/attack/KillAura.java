package ooo.cpacket.ruby.module.attack;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C02PacketUseEntity;
import ooo.cpacket.ruby.ClientBase;
import ooo.cpacket.ruby.api.event.EventImpl;
import ooo.cpacket.ruby.api.event.events.player.EventMotionUpdate;
import ooo.cpacket.ruby.api.event.events.player.motion.State;
import ooo.cpacket.ruby.manager.FriendManager;
import ooo.cpacket.ruby.module.Module;
import ooo.cpacket.ruby.util.EntityHelper;
import ooo.cpacket.ruby.util.RayCast;
import ooo.cpacket.ruby.util.Timer;

public class KillAura extends Module {

	public Timer timer = new Timer();

	public static EntityLivingBase target = null;

	public KillAura(String name, int key, Category category) {
		super(name, key, category);
		this.addNumberOption("APS", 6, 1, 20, true);
		this.addNumberOption("Reach", 3.4, 1, 6, false);
		this.addBool("Villagers", false);
		this.addBool("Players", true);
		// this.addModes("Single", "Multi");
	}

	@EventImpl
	public void onPreMotionUpdate(EventMotionUpdate event) {
		/* if (this.isMode("Single")) */ {
			EntityLivingBase e = target = (EntityLivingBase) RayCast.raycast(mc, this.getDouble("Reach"), this.getTarget());
//			boolean ab = this.getBool("AutoBock");
			if (e == null)
				return;
			long APS = (long) this.getDouble("APS");
			float f[] = EntityHelper.getEntityRotations(mc.thePlayer, e);
			event.setYaw(f[0]);
			event.setPitch(f[1]);
			if (event.getState() == State.POST) {
				if (this.timer.hasReached(1000L / APS)) {
					mc.thePlayer.swingItem();
					mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(e, C02PacketUseEntity.Action.ATTACK));
					this.timer.reset();
				}
			}
		}
	}

	@Override
	public void onEnable() {
		long APS = (long) this.getDouble("APS");
	}

	@Override
	public void onDisable() {
		target = null;
	}
	// TODO improve stuff for aura
	public EntityLivingBase getTarget() {
		for (Entity o : (List<Entity>) mc.theWorld.loadedEntityList) {
			if (o instanceof EntityPlayer && !o.isDead && o != mc.thePlayer && (mc.thePlayer
					.getDistanceToEntity(o) <= (mc.thePlayer.canEntityBeSeen(o) ? this.getDouble("Reach") : 3.1))) {
				if (ClientBase.INSTANCE.getModuleManager().getModule(AntiBot.class).isEnabled()
						&& !AntiBot.isEntityValidTarget(o)) {
					continue;
				}
				if (FriendManager.isFriend(o.getName())) {
					continue;
				}
				return (EntityPlayer) o;
			}
			if (o instanceof EntityVillager && this.getBool("Villagers") && !o.isDead
					&& (mc.thePlayer.getDistanceToEntity(o) <= this.getDouble("Reach")))
				return (EntityVillager) o;
		}
		return null;
	}

}
