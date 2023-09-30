package axolotl.cheats.modules.impl.world;

import axolotl.cheats.events.EventType;
import axolotl.cheats.events.MoveEvent;
import axolotl.cheats.settings.ModeSetting;
import axolotl.cheats.settings.NumberSetting;
import axolotl.util.Timer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.*;

import axolotl.Axolotl;
import axolotl.cheats.events.Event;
import axolotl.cheats.events.EventPacket;
import axolotl.cheats.modules.Module;
import axolotl.util.PacketUtils;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Disabler extends Module {

	public ModeSetting mode = new ModeSetting("Mode", "BlocksMC", "BlocksMC", "Funcraft", "Vulcan", "AGC", "Watchdog", "C03", "C06", "PingSpoof", "AxoWatcher");
	public NumberSetting delay = new NumberSetting("Delay", 50, 20, 5000, 10);

	public Disabler() {
		super("Disabler", Category.WORLD, true);
		mode.getSettingCluster("BlocksMC").addSettings(delay);
		mode.getSettingCluster("PingSpoof").addSettings(delay);
		this.addSettings(mode);
		this.setSpecialSetting(mode);
	}

	private final Timer timer = new Timer(), timer2 = new Timer(), timer3 = new Timer();
	private final CopyOnWriteArrayList<Packet> packetList = new CopyOnWriteArrayList(),
			                                   packetList2 = new CopyOnWriteArrayList();
	private boolean cancel, send;

	public void onEvent(Event event) {

		if(event instanceof MoveEvent) {
			if (timer3.hasTimeElapsed(10000, true)) {
				cancel = true;
				timer2.reset();
			}
		}
		
		else if(event instanceof EventPacket && event.eventType == EventType.PRE) {

			EventPacket e = (EventPacket)event;

			switch(mode.getMode()) {

				case "Watchdog":

					// Strafe, Line 736 NetHandlerPlayClient.java
					if (e.getPacket() instanceof S08PacketPlayerPosLook) {
						PacketUtils.sendPacketNoEvent(new C14PacketTabComplete("9881"));
					}

					// Ping Spoof (Timer)
					if (e.getPacket() instanceof C03PacketPlayer) {

						C03PacketPlayer c03 = (C03PacketPlayer) e.getPacket();
						if (!c03.isMoving() && !mc.thePlayer.isUsingItem()) {
							e.setCancelled(true);
						}
						if (cancel) {
							if (!timer2.hasTimeElapsed(400, false)) {
								if (!Axolotl.INSTANCE.moduleManager.getModule("Scaffold").toggled) {
									e.setCancelled(true);
									packetList.add(e.getPacket());
								}
							} else {
								packetList.forEach(PacketUtils::sendPacketNoEvent);
								packetList.clear();
								cancel = false;
							}
						}
					}
					if (timer.hasTimeElapsed(150L, true)) {
						packetList.forEach(PacketUtils::sendPacketNoEvent);
						packetList.clear();
					}
					if (e.getPacket() instanceof C0FPacketConfirmTransaction || e.getPacket() instanceof C00PacketKeepAlive) {
						e.setCancelled(true);
						packetList.add(e.getPacket());
					}
					if(e.getPacket() instanceof C03PacketPlayer.C04PacketPlayerPosition) {
						PacketUtils.sendPacketNoEvent(new C10PacketCreativeInventoryAction(-1, mc.thePlayer.getHeldItem()));
						PacketUtils.sendPacketNoEvent(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
					}
					break;

				case "Vulcan":

					if (mc.thePlayer.isDead || e.sendType == 2) break;

					if (!isMoving()) {
						if (!packetList.isEmpty()) {
							packetList.forEach(PacketUtils::sendPacketNoEvent);
							packetList.clear();
						}
						break;
					}

					packetList.add(e.getPacket());
					e.setCancelled(true);

					if(!packetList.isEmpty() && mc.thePlayer.ticksExisted % 2 == 0) {
						packetList.forEach(PacketUtils::sendPacketNoEvent);
						packetList.clear();
					}

					break;

				case "Funcraft":
					if (mc.thePlayer.ticksExisted % 4 == 0) {
						PacketUtils.sendPacketNoEvent(new C0CPacketInput(Float.MAX_VALUE, Float.MAX_VALUE, false, false));
					}
					break;

				case "AxoWatcher":
					// Speed disabler
					PacketUtils.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));

					// Timer disabler
					if (e.getPacket() instanceof C03PacketPlayer) {
						C03PacketPlayer packetPlayer = (C03PacketPlayer) e.getPacket();
						double x = mc.thePlayer.posX;
						double y = mc.thePlayer.posY;
						double z = mc.thePlayer.posZ;
						float yaw = mc.thePlayer.rotationYaw;
						float pitch = mc.thePlayer.rotationPitch;
						boolean ground = packetPlayer.onGround;

						if (packetPlayer.isMoving()) {
							x = packetPlayer.getPositionX();
							y = packetPlayer.getPositionY();
							z = packetPlayer.getPositionZ();
						}

						if (packetPlayer.getRotating()) {
							yaw = packetPlayer.getYaw();
							pitch = packetPlayer.getPitch();
						}

						e.setPacket(new C03PacketPlayer.C06PacketPlayerPosLook(x, y, z, yaw, pitch, ground));
					}

					break;

				case "AGC":
					if (e.getPacket() instanceof C00PacketKeepAlive) {
						try {
							Thread.sleep(5000L);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
					break;

				case "BlocksMC":

					// Combat disabler
					if(e.getPacket() instanceof C0FPacketConfirmTransaction) {
						if(Axolotl.INSTANCE.moduleManager.getModule("Aura").toggled) {
							e.setCancelled(true);
						}
					}
					if(mc.thePlayer.ticksExisted % 70 == 0 && mc.thePlayer.motionX > 0 && mc.thePlayer.motionZ > 0) {
						PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition((mc.thePlayer.posX - Math.floor(mc.thePlayer.posX)) / 10, -mc.thePlayer.posY - 0.015625, (1 - (mc.thePlayer.posZ - (Math.floor(mc.thePlayer.posZ)))) / 10, mc.thePlayer.onGround));
					}
					if(mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword && mc.thePlayer.ticksExisted % 16 == 0)
						PacketUtils.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
					if(e.getPacket() instanceof C0FPacketConfirmTransaction || e.getPacket() instanceof C00PacketKeepAlive) {
						packetList.add(e.getPacket());
					}
					break;
					
				case "PingSpoof":
					if(timer.hasTimeElapsed((long)delay.getNumberValue() * 10, true)) {
						for(Packet p : packetList) {
							PacketUtils.sendPacketNoEvent(p);
						}
						packetList.clear();
					}
					if(e.getPacket() instanceof C0FPacketConfirmTransaction || e.getPacket() instanceof C00PacketKeepAlive) {
						e.setCancelled(true);
						packetList.add(e.getPacket());
					}
					break;
					
				case "C03":
					Packet p = e.getPacket();
					e.setCancelled(p instanceof C03PacketPlayer);
					break;
				
				case "C06":
					
					if (e.getPacket() instanceof C03PacketPlayer) {
	                    C03PacketPlayer packetPlayer = (C03PacketPlayer) e.getPacket();
	                    double x = mc.thePlayer.posX;
	                    double y = mc.thePlayer.posY;
	                    double z = mc.thePlayer.posZ;
	                    float yaw = mc.thePlayer.rotationYaw;
	                    float pitch = mc.thePlayer.rotationPitch;
	                    boolean ground = packetPlayer.onGround;

	                    if (packetPlayer.isMoving()) {
	                        x = packetPlayer.getPositionX();
	                        y = packetPlayer.getPositionY();
	                        z = packetPlayer.getPositionZ();
	                    }

	                    if (packetPlayer.getRotating()) {
	                        yaw = packetPlayer.getYaw();
	                        pitch = packetPlayer.getPitch();
	                    }

	                    e.setPacket(new C03PacketPlayer.C06PacketPlayerPosLook(x, y, z, yaw, pitch, ground));
	                }
					
					break;
					
				default:
					break;
			}
		}
	}

	public void onEnable() {
		timer.reset();
	}
	
}
