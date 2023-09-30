package axolotl.cheats.modules.impl.movement;

import axolotl.cheats.events.*;
import axolotl.util.*;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import org.lwjgl.input.Keyboard;

import axolotl.Axolotl;
import axolotl.cheats.modules.Module;
import axolotl.cheats.modules.impl.player.NoFall;
import axolotl.cheats.settings.ModeSetting;
import axolotl.cheats.settings.NumberSetting;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Flight extends Module {

	
	public String[] modes = new String[] 
			{
					"Smooth",
					"NCP",
					"Minemora",
					"Funcraft",
					"Watchdog",
					"RedeSky",
					"TP",
					"Verus",
					"VerusJump",
					"RedeDark",
					"Watcher",
					"ACD",
					"AntiBedless",
					"Larkus",
					"Spartan",
					"Taka",
					"Vulcan",
					"Soaroma",
					"Flappy",
					"Astro",
					"AAC4",
					"AxoWatcher",
					"Ninja",
			},
	
	larkusModes = new String[] 
			{
					"Larkus"
			},
			
	spartanModes = new String[]
			{
					"Default",
					"Long",
					"Dev"
			};
	
	public ModeSetting mode = new ModeSetting("Mode", "Smooth", modes), slow = new ModeSetting("Slowdown", "Entity", "Entity", "Smart", "None");
	public NumberSetting flightSpeed = new NumberSetting("Speed", 1f, 0.1f, 12f, 0.1f), timer = new NumberSetting("Timer", 1f, 0.1f, 2f, 0.1f), packets = new NumberSetting("Packets", 15, 5, 45, 5);
	
	public ModeSetting larkusMode = new ModeSetting("Mode", "Larkus", larkusModes);
	public ModeSetting spartanMode = new ModeSetting("Mode", "Default", spartanModes);

	public Flight() {
		super("Flight", Category.MOVEMENT, true);
		mode.getSettingCluster("Smooth").addSettings(flightSpeed);
		mode.getSettingCluster("Vulcan").addSettings(flightSpeed);
		mode.getSettingCluster("Larkus").addSettings(larkusMode);
		mode.getSettingCluster("Spartan").addSettings(spartanMode);
		mode.getSettingCluster("Watcher").addSettings(packets, slow);
		this.addSettings(mode);
		this.setSpecialSetting(mode);
	}
	
	public boolean allowFlyingBefore = false;
	
	public void damage() {
		NoFall NF = ((NoFall)Axolotl.INSTANCE.moduleManager.getModule("NoFall"));
		NF.work = false;
        mc.thePlayer.sendQueue.addToSendQueue(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 3.0001, mc.thePlayer.posZ, false));
        mc.thePlayer.sendQueue.addToSendQueue(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
        mc.thePlayer.sendQueue.addToSendQueue(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
        NF.work = true;
	}
	
	public void onEnable() {
		doFly = false;
		hasClipped = false;
		switch(mode.getMode()) {

			case "Minemora":
				if(mc.thePlayer.onGround)mc.thePlayer.jump();
				break;

			case "Funcraft":
			case "Larkus":
				value0 = (float)mc.thePlayer.posY;
				break;

			case "Taka":
				
				vClip(-0.001);
				
				break;

			case "Watchdog":
				value1 = (float)mc.thePlayer.posY;
				break;

			case "Ninja":
				mc.thePlayer.setSpeed(4f);
				mc.thePlayer.motionY = 0.3f;
				break;

			case "Flappy":
			case "Watcher":
			case "RedeDark":
				vClip(0.2);
				break;
				
			case "Smooth":
				mc.thePlayer.capabilities.isFlying = true;
				break;

			case "TP":
				vClip(30);
				break;
			//damage();
			case "AntiBedless":
			case "Vulcan":
				damage();
				break;
			case "Verus":
				damage();
				value0 = (float)mc.thePlayer.posY;
				break;

			case "VerusJump":
				Module velo = Axolotl.INSTANCE.moduleManager.getModule("Velocity");
				value2 = velo.toggled ? 1 : 0;
				velo.toggled = false;
				damage();
				break;
			case "ACD":
				value2 = (float)mc.thePlayer.posY;
				vClip(0.2f);
				break;
			default:
				mc.thePlayer.capabilities.allowFlying = false;
				break;
		}
	}

	public CopyOnWriteArrayList<Packet> packetList = new CopyOnWriteArrayList();
	
	public void onDisable() {
		mc.thePlayer.capabilities.allowFlying = allowFlyingBefore;
		mc.thePlayer.capabilities.isFlying = false;
		value0 = 0;
		value1 = 0;
		value2 = 0;
		mc.thePlayer.motionX = 0;
		mc.thePlayer.motionZ = 0;
		mc.thePlayer.motionY = mc.thePlayer.motionY > 0 ? 0 : mc.thePlayer.motionY;
		((NoFall)Axolotl.INSTANCE.moduleManager.getModule("NoFall")).work = true;
		switch(mode.getMode()) {

			case "VerusJump":
				Axolotl.INSTANCE.moduleManager.getModule("Velocity").toggled = value2 == 1;
				break;

			case "Minemora":
				mc.thePlayer.motionX = mc.thePlayer.motionZ = 0;
				PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY-0.0625, mc.thePlayer.posZ, false));
				for (int i = 10; i > 0; --i) {
					PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.onGround));
				}
				for (int i = 40; i > 0; --i) {
					mc.thePlayer.posY -= 0.25;
					PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.onGround));
				}
				PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.onGround));
				break;

			case "Verus":
				value2 = System.currentTimeMillis();
				break;
			default:
				break;
		}
	}

	private boolean hasClipped, doFly;
	public float value0 = 0, value1 = 0, value2 = 0;
	
	public void onEvent(Event e) {
		
		if(e instanceof EventUpdate && e.eventType == EventType.PRE) {

			switch (mode.getMode()) {

				case "Ninja":
					value0++;
					if(value0 < 5) {
						mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
					} else {
						mc.thePlayer.jump();
						if(value0 >= 20)value0 = 0;
					}
					break;

				case "TP":
					mc.thePlayer.motionY = 0;
					break;

				case "AAC4":
					if (!mc.thePlayer.onGround) {
						value0++;
						mc.thePlayer.speedInAir = 0.02035f * (1 / mc.timer.timerSpeed);
						mc.thePlayer.motionY *= mc.thePlayer.motionY > 0 ? 1.01 : 0.98;
						mc.timer.timerSpeed = 0.15f + value0 % 50 / 100;
					}
					break;

				case "Watcher":
					value0++;
					mc.thePlayer.motionY = 0;
					boolean slowdown = false;
					switch (slow.getMode()) {
						case "Smart":
							boolean G = PlayerUtil.getPlayerHeight() <= 1;
							if (G) {
								value1 = 1;
								value2++;
							} else {
								value2 = 0;
							}
							slowdown = G && value1 > 0 && value2 > 2;
						case "Entity":
							List<EntityLivingBase> entities = EntityUtil.getEntitiesAroundPlayer(4);
							if (!slowdown && !entities.isEmpty())
								slowdown = true;
							break;
						default:
							break;
					}
					if (value0 < 2) {
						mc.timer.timerSpeed = 0.1f;
					} else {
						if (isMoving())
							MotionUtil.setSpeed(2f);
						mc.timer.timerSpeed = slowdown ? 0.1f : 3f;
					}
					break;

				case "RedeDark":
					mc.thePlayer.motionY = 0;
					mc.thePlayer.onGround = true;
					break;

				case "Flappy":
					mc.thePlayer.motionY = 0;
					vClip(0.0000000001);
					mc.thePlayer.onGround = true;
					break;

				case "Soaroma":

					if (mc.thePlayer.onGround) {
						mc.thePlayer.jump();
						mc.thePlayer.motionY = 0.3f;
						mc.timer.timerSpeed = 1.3f;
						value0++;
					}
					if (value0 == 1) value1++;
					if (value1 > 1 && value1 < 13) {
						mc.thePlayer.motionY = 0;
						mc.thePlayer.setSpeed(0.4f);
						mc.timer.timerSpeed = 1.2f;
					} else if (value1 == 13) {
						hClip(7);
						vClip(3);
					} else if (value1 > 13) {
						mc.timer.timerSpeed = 0.2f;
						mc.thePlayer.motionY = mc.thePlayer.ticksExisted % 6 == 0 ? 0 : mc.thePlayer.motionY;
						if (mc.thePlayer.onGround) this.toggle();
						mc.thePlayer.setSpeed(0.4f);
					}

					break;

				case "Taka":

					value0++;

					if (value0 >= 6 && value0 <= 8) {
						hClip(7);
						mc.thePlayer.motionY = 0;
					} else if (value0 > 8) {
						this.toggle();
					}
					break;

				case "ACD":
					mc.thePlayer.setSpeed(0.25f);
					mc.thePlayer.motionY = 0.08 - (mc.thePlayer.ticksExisted % 2 == 0 ? 0.08 : 0);
					break;

				case "Spartan":
					mc.thePlayer.onGround = true;
					mc.thePlayer.motionY = 0;
					return;

				case "Astro":

					if (mc.thePlayer.hurtTime == mc.thePlayer.maxHurtTime - 1) {
						value0 = 1;
						hClip(2);
					}

					if (value0 >= 1) {
						value0++;
						hClip(3);
						if (value0 > 2) this.toggle();
					}

					break;

				case "AxoWatcher":
					if(mc.thePlayer.ticksExisted % 10 == 0) {
						mc.thePlayer.sendQueue.addToSendQueue(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 2.9999, mc.thePlayer.posZ, false));
						mc.thePlayer.sendQueue.addToSendQueue(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
						mc.thePlayer.sendQueue.addToSendQueue(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
					}
				case "Verus":
					mc.timer.timerSpeed = 0.1f;
				case "Minemora":
				case "Vulcan":
				case "Smooth":
					MotionUtil.setSpeed(isMoving() ? flightSpeed.getNumberValue() / 2.5 : 0);
					double speed = (flightSpeed.getNumberValue() / 2.5) / 2;
					mc.thePlayer.motionY = mc.gameSettings.keyBindJump.pressed && !mc.gameSettings.keyBindSneak.pressed ? speed : mc.gameSettings.keyBindSneak.pressed && !mc.gameSettings.keyBindJump.pressed ? -speed : 0;
					break;

				case "Watchdog":

					if(!hasClipped)mc.thePlayer.motionY = 0;

					if(doFly) {
						hasClipped = true;
						value0++;
						if(value0 % 5 == 0)
							mc.thePlayer.motionY = 0;
					}

					break;

				case "Permission":
					flightSpeed.maxValue = 10f;
					mc.thePlayer.capabilities.allowFlying = true;
					mc.thePlayer.capabilities.setFlySpeed((float) flightSpeed.getNumberValue() / 20f);
					break;

				case "AntiBedless":
					if (mc.thePlayer.hurtTime == mc.thePlayer.maxHurtTime - 2) {
						mc.thePlayer.jump();
						mc.thePlayer.motionY = 0.65f;
						mc.thePlayer.setSpeed(0.65f);
						value0 = 1;
					}
					if (value0 == 1) {
						mc.thePlayer.speedInAir = 0.0207f;
					}
					if (value0 == 1 && mc.thePlayer.onGround) this.toggle();
					break;

				case "VerusJump":

					if (mc.thePlayer.hurtTime == mc.thePlayer.maxHurtTime - 2) {
						value0 = 1;
						mc.thePlayer.motionY = 0.6f;
						mc.thePlayer.setSpeed(4f);
					} else if (value0 >= 1) {
						if (mc.thePlayer.onGround) this.toggle();
					} else {
						mc.thePlayer.motionX = 0;
						mc.thePlayer.motionZ = 0;
					}
					break;


				case "Larkus":

					if ("Larkus".equals(larkusMode.getMode())) {
						mc.thePlayer.onGround = true;
						if (mc.thePlayer.ticksExisted % 10 == 0) mc.thePlayer.jump();
					}
					break;


				default:
					mc.thePlayer.capabilities.allowFlying = allowFlyingBefore;
					break;

			}

		} else if (e instanceof MoveEvent) {
			switch(mode.getMode()) {
				case "RedeSky":
					mc.thePlayer.setSprinting(true);

					if(mc.thePlayer.onGround) {
						mc.thePlayer.speedInAir = 0.02F;
						if(!mc.gameSettings.keyBindJump.pressed) {
							mc.thePlayer.jump();
						}
						mc.thePlayer.motionY = 1;
						value1 = 0.135f;
						mc.timer.timerSpeed = 1F;
					} else {
						if(value0 != 1) {
							if(mc.thePlayer.motionY < 0.9) {
								mc.thePlayer.jumpMovementFactor = 0.2F;
							}
						} else {
							mc.thePlayer.jumpMovementFactor = value1;
						}
					}

					if(isMoving()) {
						mc.timer.timerSpeed = 1F;
					} else {
						mc.timer.timerSpeed = 0.25F;
					}
					if(mc.thePlayer.ticksExisted % 10 == 0) {
						((MoveEvent) e).setOnGround(true);
					}
					break;
				case "NCP":
					((MoveEvent) e).setOnGround(true);
					mc.thePlayer.motionY = 0;
					mc.thePlayer.onGround = true;
					break;
				case "Flappy":
					((MoveEvent) e).setYaw(0);
					((MoveEvent) e).setPitch(0);

					mc.thePlayer.motionX = 0;
					mc.thePlayer.motionZ = 0;

					if (isMoving())
						hClip(0.23);

					value0++;

					mc.gameSettings.keyBindForward.pressed = false;
					mc.gameSettings.keyBindBack.pressed = false;
					mc.gameSettings.keyBindLeft.pressed = false;
					mc.gameSettings.keyBindRight.pressed = false;
				case "Funcraft":
					value1++;
					mc.timer.timerSpeed = 0.5f + ((value1 % 20) / 10);
					((MoveEvent) e).setOnGround(true);
					mc.thePlayer.onGround = true;
					((MoveEvent) e).setY(value0);
					mc.thePlayer.motionY = -0.02;
					if(value1 > 100)this.toggle();
					if(value1 > 41) {
						if(value1 % 3 == 0) {
							for(int i=0;i<5;i++) {
								mc.thePlayer.sendQueue.addToSendQueue(new C0CPacketInput(Float.MAX_VALUE, Float.MAX_VALUE, true, true));
								mc.thePlayer.sendQueue.addToSendQueue(new C0CPacketInput(Float.MAX_VALUE, Float.MAX_VALUE, false, false));
							}
						}
					}
					mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
					if (value1 % 4 == 0) {
						mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK,
								new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ),
								EnumFacing.DOWN));
					} else {
						mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK,
								new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ),
								EnumFacing.DOWN));
					}
					break;
				default:
					break;
			}
			
		} else if (e instanceof EventPacket) {
			
			EventPacket event = (EventPacket)e;
			Packet p = event.getPacket();
			
			switch(mode.getMode()) {

				case "RedeSky":

					if(event.packet instanceof S12PacketEntityVelocity) {
						S12PacketEntityVelocity packet = (S12PacketEntityVelocity) event.packet;

						if(packet.func_149412_c() == mc.thePlayer.getEntityId()) {
							event.cancelled = true;
						}
					} else if(event.packet instanceof S27PacketExplosion) {
						event.cancelled = true;
					} else if(event.packet instanceof S08PacketPlayerPosLook) {
						value0 = 1;
					}
					break;

				case "Watchdog":
					if(event.getPacket() instanceof S08PacketPlayerPosLook) {
						PacketUtils.sendPacketNoEvent(new C0CPacketInput(Float.MIN_VALUE, Float.MIN_VALUE, false, false));
						doFly = true;
					}
					break;

				case "Watcher":
					if(!(event.getPacket() instanceof C03PacketPlayer))return;
					for(int i=0;i<Math.round(packets.getNumberValue() + (value0 / 100));i++)
						PacketUtils.sendPacketNoEvent(event.getPacket());
					break;

				case "Minemora":
					if(((EventPacket) e).getPacket() instanceof C03PacketPlayer || ((EventPacket) e).getPacket() instanceof C00PacketKeepAlive) {
						((EventPacket) e).setCancelled(true);
						packetList.add(((EventPacket) e).getPacket());
					}
					break;

				case "Spartan":
					switch(spartanMode.getMode()) {
						case "Long":
							if(mc.thePlayer.ticksExisted % 35 == 0) {
								mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.1, mc.thePlayer.posZ);
								return;
							}
							if(p instanceof C03PacketPlayer) {
								event.setCancelled(true);
							}
							break;
						case "Dev":
							mc.timer.timerSpeed = 0.56f;
							if(mc.thePlayer.ticksExisted % 10 == 0) {
								PacketUtils.sendPacketNoEvent(new C0CPacketInput(-60, -60,true, true));
								PacketUtils.sendPacketNoEvent(new C14PacketTabComplete("~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+~!@#$%^&*()_+"));
							}
							((EventPacket) e).setCancelled(mc.thePlayer.ticksExisted % 6 != 0 && (((EventPacket) e).getPacket() instanceof C0FPacketConfirmTransaction || ((EventPacket) e).getPacket() instanceof C00PacketKeepAlive));
							break;
						default:
							break;
					}
					
					break;
					
				case "Flappy":
					
					if(Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode())) {
						if(value1 == 0) {
							value1 = (float)mc.thePlayer.posX;
							value2 = (float)mc.thePlayer.posZ;
						}
						value0 = 0;
						mc.thePlayer.setPosition(value1, mc.thePlayer.posY, value2);
						mc.thePlayer.motionX = 0;
						mc.thePlayer.motionZ = 0;
						vClip(0.1);
					} else value1 = 0;
					
					break;
					
				default:
					break;
			
			}
			
		}
			
	}
	
}
