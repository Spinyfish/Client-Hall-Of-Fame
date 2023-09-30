package felix.module.impl.combat;

import java.util.ArrayList;

import felix.api.annotations.Handler;
import felix.events.packet.EventPacketReceive;
import felix.events.player.EventMotionUpdate;
import felix.module.Module;
import felix.util.other.PlayerUtil;
import felix.value.impl.BooleanValue;
import felix.value.impl.EnumValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.util.MathHelper;

public class AntiBot extends Module {

	public static ArrayList bots = new ArrayList();
	private EnumValue<Mode> mode = new EnumValue<>("Mode", Mode.Watchdog);
	public BooleanValue removeValue = new BooleanValue("Remove World", true);
	public BooleanValue botNotificationValue = new BooleanValue("Notification", true);

	public AntiBot() {
		super("Antibot", 0, ModuleCategory.COMBAT);
		addValues(mode);
	}

	public enum Mode {
		Watchdog, Mineplex;
	}

	@Override
	public void onEnable() {
		super.onEnable();
		bots.clear();
	}

	@Override
	public void onDisable() {
		super.onDisable();
		bots.clear();
	}

	private boolean isInTablist (EntityLivingBase player){
		if (mc.isSingleplayer()) {
			return true;
		}
		for (Object o : mc.getNetHandler().getPlayerInfoMap()) {
			NetworkPlayerInfo playerInfo = (NetworkPlayerInfo) o;
			if (playerInfo.getGameProfile().getName().equalsIgnoreCase(((EntityPlayer) player).getGameProfile().getName())) {
				return true;
			}
		}
		return false;
	}

	@Handler
	public void onPacketSend(final EventPacketReceive eventPacketReceive) {
		if (mode.getValue().equals(Mode.Watchdog)) {
			if (mc.thePlayer != null && PlayerUtil.isOnServer("hypixel")) {
				if (eventPacketReceive.getPacket() instanceof S18PacketEntityTeleport) {
					S18PacketEntityTeleport packet = (S18PacketEntityTeleport) eventPacketReceive.getPacket();
					Entity entity = mc.theWorld.getEntityByID(packet.getEntityId());
					if (entity != null && entity instanceof EntityPlayer) {
						if (entity.isInvisible()) {
							bots.add((EntityPlayer) entity);
						}
					}
				}
			}
		}
		if(mode.getValue() == Mode.Mineplex){
			if (eventPacketReceive.getPacket() instanceof S0CPacketSpawnPlayer) {
				S0CPacketSpawnPlayer packetSpawnPlayer = (S0CPacketSpawnPlayer) eventPacketReceive.getPacket();
				if (packetSpawnPlayer.func_148944_c().size() < 10) {
					bots.add(packetSpawnPlayer.getEntityID());
				}
			} else if (eventPacketReceive.getPacket() instanceof S01PacketJoinGame) {
				bots.clear();
			}
		}
	}

	@Handler
	public void onUpdate(final EventMotionUpdate event) {
		if (mode.getValue() == Mode.Watchdog) {
			for (Entity entity : mc.theWorld.loadedEntityList) {
				if ((entity instanceof EntityPlayer)) {
					EntityPlayer player = (EntityPlayer) entity;

					switch (mode.getValue()) {
						case Watchdog: {
							if ((player.getGameProfile().getName()).startsWith("§c") && !isInTablist(player) && player.isInvisible()) {
								if (!bots.contains(player)) {
									bots.add(player);
								}
							}
							if (player.isInvisible() && !bots.contains(player)) {
								float xDist = (float) (mc.thePlayer.posX - player.posX);
								float zDist = (float) (mc.thePlayer.posZ - player.posZ);
								double horizontalReaach = MathHelper.sqrt_float(xDist * xDist + zDist * zDist);
								if (horizontalReaach < .6) {
									double vert = mc.thePlayer.posY - player.posY;
									if (vert <= 5 && vert > 1) {
										if (mc.thePlayer.ticksExisted % 5 == 0) {
											bots.add(player);
										}
									}
								}
							}
							if (bots.contains(player) && player.hurtTime > 0 || player.fallDistance > 0) {
								bots.remove(player);
							}
							break;
						}
					}
				}
			}
			if (!bots.isEmpty() && mc.thePlayer.ticksExisted % 20 == 0) {
				for (int i = 0; i < bots.size(); i++) {
					if (bots.contains(bots.get(i))) {
						if (!mc.theWorld.playerEntities.contains(bots.get(i))) bots.remove(bots.get(i));
					}
				}
			}
		}
		if(mode.getValue() == Mode.Mineplex){
			mc.theWorld.playerEntities.stream()
					.filter(entity -> entity != mc.thePlayer)
					.filter(entity -> bots.contains(entity.getEntityId()))
					.forEach(entityPlayer -> bots.add(entityPlayer));
		}
	}
}