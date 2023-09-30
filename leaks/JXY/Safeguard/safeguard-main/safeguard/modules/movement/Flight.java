package intentions.modules.movement;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import intentions.Client;
import intentions.events.Event;
import intentions.events.listeners.EventMotion;
import intentions.events.listeners.EventPacket;
import intentions.events.listeners.EventUpdate;
import intentions.modules.Module;
import intentions.modules.player.NoFall;
import intentions.settings.ModeSetting;
import intentions.settings.NumberSetting;
import intentions.util.PacketUtils;
import intentions.util.PlayerUtil;
import intentions.util.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

public class Flight extends Module {

	public static NumberSetting flightSpeed = new NumberSetting("Speed", 1, 0.2, 10, 0.1);
	public static ModeSetting type = new ModeSetting("Type", "Vanilla", new String[] { "Glide", "Redesky", "Vanilla",
			"Bounce", "ACD", "AGC", "AAC3", "Verus", "Flappy", "Hypixel", "Watcher" });

	public Flight() {
		super("Flight", Keyboard.KEY_G, Category.MOVEMENT, "Allows you to fly like a bird", true);
		this.addSettings(flightSpeed, type);
	}

	public static Minecraft mc = Minecraft.getMinecraft();
	public static boolean flight;
	private boolean Bounce, above = false;
	private Timer timer = new Timer();
	private double[] loc;

	public void onDisable() {
		if (type.getMode().equalsIgnoreCase("Verus")) {
			PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
					mc.thePlayer.posY + 1, mc.thePlayer.posZ, true));
			PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
					mc.thePlayer.posY - 1, mc.thePlayer.posZ, true));
		} else if (type.getMode().equalsIgnoreCase("Watcher")) {
			mc.thePlayer.motionY = -(cacFly / 100);
		}
		mc.timer.timerSpeed = 1f;
		mc.thePlayer.capabilities.isFlying = false;
		mc.thePlayer.capabilities.setFlySpeed(0.05f);
		mc.thePlayer.motionX = 0;
		mc.thePlayer.motionY = 0;
		mc.thePlayer.motionZ = 0;
		mc.thePlayer.jumpMovementFactor = 0.02f;

		flight = false;

		NoFall.shouldWork = true;
		pitch = 0f;
		yaw = 0f;
		cacFly = 0f;
		value0 = 0f;
		value1 = 0f;
		value2 = 0f;
		mc.gameSettings.keyBindJump.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode());

	}

	private final ArrayList<Packet> packets = new ArrayList<>();

	public void onEnable() {
		flight = true;
		timer.reset();
		loc = new double[] { mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ };
		if (type.getMode().equalsIgnoreCase("Verus")) {
			mc.thePlayer.motionY = 0.01f;
		} else if (type.getMode().equalsIgnoreCase("Hypixel")) {
		} else if (type.getMode().equalsIgnoreCase("Watcher")) {
			mc.thePlayer.motionY = 0.03f;
			// TeleportUtils.pathFinderTeleportTo(new Vec3(mc.thePlayer.posX,
			// mc.thePlayer.posY, mc.thePlayer.posZ), new Vec3(mc.thePlayer.posX, 256,
			// mc.thePlayer.posZ));
		} else if (type.getMode().equalsIgnoreCase("ACD")) {
			//mc.thePlayer.motionY += 0.85f;
		} else if (type.getMode().equalsIgnoreCase("Flappy")) {
			mc.thePlayer.motionY = 0.2f;
		}
	}

	EventPacket lastC04 = null;
	boolean aac5nextFlag;
	ArrayList<C03PacketPlayer> aac5C03List = new ArrayList<C03PacketPlayer>();

	public void onSendPacket(EventPacket e) {
		if (mc.theWorld == null || mc.thePlayer == null || mc.thePlayer.onGround || !this.toggled)
			return;
		if (e.getPacket() instanceof C04PacketPlayerPosition) {
			lastC04 = e;
		}
		//Client.addChatMessage(e.getPacket().toString().replace("net.minecraft.network.play.client.", "").substring(0, 3));

		if (type.getMode().equalsIgnoreCase("Watcher")) {
			if (e.getPacket() instanceof C04PacketPlayerPosition) {
				e.setCancelled(true);
				C04PacketPlayerPosition p = (C04PacketPlayerPosition) e.getPacket();
				PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(p.x, p.y, p.z, p.yaw, p.pitch,
						mc.thePlayer.onGround));
			}
		} else if (type.getMode().equalsIgnoreCase("Redesky")) {
			if (e.getPacket() instanceof S08PacketPlayerPosLook) {

				C04PacketPlayerPosition l = (C04PacketPlayerPosition) lastC04.getPacket();
			} else if (e.getPacket() instanceof C03PacketPlayer) {
				double f = mc.thePlayer.width / 2.0;
				final C03PacketPlayer packetPlayer = (C03PacketPlayer) e.getPacket();
				// need to no collide else will flag
				if (aac5nextFlag || !mc.theWorld.checkBlockCollision(new AxisAlignedBB(packetPlayer.getPositionX() - f,
						packetPlayer.getPositionY(), packetPlayer.getPositionZ() - f, packetPlayer.getPositionX() + f,
						packetPlayer.getPositionY() + mc.thePlayer.height, packetPlayer.getPositionZ() + f))) {
					aac5C03List.add(packetPlayer);
					aac5nextFlag = false;
					e.setCancelled(true);
					if (!timer.hasTimeElapsed(1000, true) && aac5C03List.size() > 7) {
						sendAAC5Packets();
					}
				}
			}
		} else if (type.getMode().equalsIgnoreCase("Verus")) {
			//e.setCancelled(mc.thePlayer.ticksExisted % 2 == 0);
		} else if (type.getMode().equalsIgnoreCase("Flappy")) {
			if(e.getPacket() instanceof C04PacketPlayerPosition) {
				
				C04PacketPlayerPosition C04 = (C04PacketPlayerPosition)e.getPacket();
				
				e.setCancelled(true);
				
				PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(C04.x, C04.y, C04.z, C04.yaw, C04.pitch, mc.thePlayer.onGround));
				
			}
		}
	}

	private void sendAAC5Packets() {
		float yaw = mc.thePlayer.rotationYaw;
		float pitch = mc.thePlayer.rotationPitch;
		for (C03PacketPlayer packet : aac5C03List) {
			PacketUtils.sendPacketNoEvent(packet);
			if (packet.getRotating()) {
				yaw = packet.getYaw();
				pitch = packet.getPitch();
			}
			PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(packet.x, 1e+305, packet.z, true));
			PacketUtils
					.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(packet.x, packet.y, packet.z, true));
		}

		aac5C03List.clear();
	}

	float pitch = 0, yaw = 0, cacFly = 0, value0, value1, value2;

	public void onEvent(Event e) {
		if (e instanceof EventUpdate) {
			if (e.isPre() && this.toggled) {

				if (type.getMode().equalsIgnoreCase("ACD")) {
					/*mc.thePlayer.setSpeed(1.3f);
					mc.thePlayer.motionY = 0;
					pitch++;
					if(Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode())) {
						mc.thePlayer.motionY += 0.5f;
					}
					if(Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode())) {
						mc.thePlayer.motionY -= 0.5f;
					}
					mc.timer.timerSpeed = 2.3f;
					if(pitch % 70 == 0)
						PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, 1e+305, mc.thePlayer.posZ, true));*/
					value0++;
					if(value0 % 8 == 0) {
						mc.thePlayer.motionY *= 0.95f;
						mc.thePlayer.motionX *= 1.05f;
						mc.thePlayer.motionZ *= 1.05f;
					}
					return;

				} else if (type.getMode().equalsIgnoreCase("Watcher")) {

					// for(int i=0;i<30;i++) {
					// mc.thePlayer.sendQueue.addToSendQueue(new
					// C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY,
					// mc.thePlayer.posZ, mc.thePlayer.onGround));
					// }
				} else if (type.getMode().equalsIgnoreCase("Hypixel")) {
					if(mc.thePlayer.onGround)
					mc.thePlayer.motionY = 0.1f;
					return;
				} else if (type.getMode().equalsIgnoreCase("Flappy")) {
					double playerYaw = Math.toRadians(mc.thePlayer.rotationYaw);
					double speed = 0.1f;
					double x = speed * -Math.sin(playerYaw);
					double z = speed * Math.cos(playerYaw);
					double y = 0.1f;
			        mc.thePlayer.setPosition(mc.thePlayer.posX + x, mc.thePlayer.posY + y, mc.thePlayer.posZ + z);
					/*if (mc.thePlayer.onGround || PlayerUtil.getPlayerHeightDouble() < 0.2)
						return;
					mc.thePlayer.motionY = 0;
					mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.000001f, mc.thePlayer.posZ);*/
					return;
				} else if (type.getMode().equalsIgnoreCase("AAC3")) {
					mc.timer.timerSpeed = 1.3f;
				} else if (type.getMode().equalsIgnoreCase("Redesky")) {
				} else if (type.getMode().equalsIgnoreCase("Bounce") || type.getMode().equalsIgnoreCase("Flappy")) {
					if (mc.thePlayer.onGround || PlayerUtil.getPlayerHeightDouble() <= 0.1)
						return;
					if (!Bounce) {
						Bounce = true;
						mc.thePlayer.motionY += 0.201;
					} else {
						Bounce = false;
						mc.thePlayer.motionY -= 0.2;
					}
				} else if (type.getMode().equalsIgnoreCase("Verus")) {
					pitch++;
			        if (pitch == 1) { mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 4, mc.thePlayer.posZ); }
			        if (pitch == 2) { mc.thePlayer.motionY = -3.3;}

			        double playerYaw = Math.toRadians(mc.thePlayer.rotationYaw);
			        double x = -Math.sin(playerYaw);
			        double z = Math.cos(playerYaw);
			        
			        if (pitch > 5) {
			          if (pitch < 3) {
			            mc.thePlayer.motionY = 0.001;
			            mc.thePlayer.setPosition(mc.thePlayer.posX + (x * 5), mc.thePlayer.posY, mc.thePlayer.posZ + (z * 5));
			            mc.timer.timerSpeed = 1.2f;
			          } else {
			            mc.timer.timerSpeed = 1;
			            mc.thePlayer.setPosition(mc.thePlayer.posX + (x * 0.28), mc.thePlayer.posY, mc.thePlayer.posZ + (z * 0.28));
			            mc.thePlayer.motionY = 0;
			          }
			        }
					return;
				} else if (type.getMode().equalsIgnoreCase("AGC")) {
					mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C03PacketPlayer(true));
					mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(0, -1, 0), 0,
							mc.thePlayer.inventory.getCurrentItem(), 0, 0, 0));

					mc.thePlayer.motionY = 0;
					return;
				}
				mc.thePlayer.capabilities.isFlying = true;
				mc.thePlayer.fallDistance = 0f;
				mc.thePlayer.capabilities.setFlySpeed((float) flightSpeed.getValue() / 20f);
			}
		} else if (e instanceof EventMotion) {
			if (type.getMode().equalsIgnoreCase("ACD")) {
			} else if (type.getMode().equalsIgnoreCase("Watcher")) {
				if (!isMovingKB()) {
					mc.thePlayer.motionX = 0;
					mc.thePlayer.motionZ = 0;
				}
			} else if (type.getMode().equalsIgnoreCase("AGC")) {
			}
		}
	}

	@Override
	public void onBB(AxisAlignedBB e) {
		if (type.getMode().equalsIgnoreCase("AGC"))
			e.setBoundingBox(
					AxisAlignedBB.fromBounds(e.minX, e.minY, e.minZ, e.minX + 1, mc.thePlayer.posY, e.minZ + 1));
		
	}

	public void onTick() {
		if (mc.thePlayer != null && mc.theWorld != null && (Flight.type.getMode().equalsIgnoreCase("Glide"))) {
			mc.thePlayer.motionY = -0.05f;
		}
	}

}
