package markgg.modules.movement;

import markgg.modules.Module.Category;
import markgg.settings.BooleanSetting;
import markgg.settings.ModeSetting;
import markgg.settings.NumberSetting;
import markgg.ui.HUD;
import markgg.ui.notifs.Notification;
import markgg.ui.notifs.NotificationManager;
import markgg.ui.notifs.NotificationType;
import markgg.util.MoveUtil;
import markgg.util.Timer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBarrier;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

import org.lwjgl.input.Keyboard;

import markgg.Client;
import markgg.events.Event;
import markgg.events.listeners.EventMotion;
import markgg.events.listeners.EventPacket;
import markgg.events.listeners.EventUpdate;
import markgg.modules.Module;

public class Fly extends Module{
	public ModeSetting flyMode = new ModeSetting("Mode", this, "Vanilla", "Vanilla", "Spartan", "Motion", "Verus", "Vulcan", "Glide Up", "Glide", "Spartan2", "Float", "Airjump", "Packet", "SpoofGround", "VClip", "Dev");
	public NumberSetting flySpeed = new NumberSetting("Fly Speed", this, 1, 0.1, 9.5, 0.1);
	public BooleanSetting startBoost = new BooleanSetting("Boost", this, false);
	public BooleanSetting debugMode = new BooleanSetting("Debug Mode", this, false);
	public Timer timer = new Timer();

	boolean upOrDown;
	boolean sprintBefore;
	int timeFly;
	int tick = 0;

	public Fly() {
		super("Fly", "Fly like a bird", 0, Category.MOVEMENT);
		addSettings(flyMode, flySpeed, startBoost, debugMode);
	}

	public void onEvent(Event e) {
		if(e instanceof EventMotion && e.isPre()) {
			EventMotion event = (EventMotion)e;
			switch(flyMode.getMode()) {
			case "Motion":
				event.onGround = true;
				MoveUtil.setSpeed(0.5D);
				if (GameSettings.isKeyDown(mc.gameSettings.keyBindJump)) {
					mc.thePlayer.motionY = 0.5D;
				} else if (GameSettings.isKeyDown(mc.gameSettings.keyBindSneak)) {
					mc.thePlayer.motionY = -0.5D;
				} else {
					mc.thePlayer.motionY = 0.0D;
				} 
				mc.timer.timerSpeed = 1.0F;
				break;
			case "Spartan":
				if (mc.thePlayer.onGround)
					mc.thePlayer.jump(); 
				if (mc.thePlayer.fallDistance > 1.0F)
					mc.thePlayer.motionY = -(mc.thePlayer.posY - Math.floor(mc.thePlayer.posY)); 
				if (mc.thePlayer.motionY == 0.0D) {
					mc.thePlayer.jump();
					mc.thePlayer.onGround = true;
					event.setOnGround(true);
					mc.thePlayer.fallDistance = 0.0F;
				}
				break;
			case "Verus":
				MoveUtil.setSpeed(0.2D);
				break;
			case "Airjump":
				mc.thePlayer.onGround = true;
				break;
			case "Glide Up":
				if (mc.thePlayer.fallDistance > 1.0f && mc.thePlayer.fallDistance < 2.0f) {
					mc.thePlayer.motionY *= -0.25;
					if(debugMode.isEnabled())
						mc.thePlayer.motionY *= 0.25;
				}
				if (mc.thePlayer.fallDistance > 3.4) {
					event.setOnGround(true);
					mc.thePlayer.motionY += 0.6;
					mc.thePlayer.fallDistance = 0.0f;
				}
				break;
			case "Packet":
				double x = mc.thePlayer.posX, y = mc.thePlayer.posY, z = mc.thePlayer.posZ;

				mc.thePlayer.sendQueue.addToSendQueue(new C04PacketPlayerPosition(x, y, z, true));
				mc.thePlayer.sendQueue.addToSendQueue(new C04PacketPlayerPosition(x, y - mc.thePlayer.fallDistance, z, true));
				MoveUtil.setSpeed(1F);
				break;
			case "VClip":
				if(!mc.thePlayer.onGround && timer.hasTimeElapsed(105, false))
					mc.thePlayer.setPositionAndUpdate(mc.thePlayer.posX, mc.thePlayer.posY + 1, mc.thePlayer.posZ);

				if(!mc.thePlayer.onGround && timer.hasTimeElapsed(125, true))
					mc.thePlayer.setPositionAndUpdate(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ);

				mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer());
				mc.thePlayer.motionY *= 0.03;
				mc.thePlayer.motionX *= 0.84;
				mc.thePlayer.motionZ *= 0.84;
				mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer());
				break;
			}
		}
		if(e instanceof EventUpdate && e.isPre()) {
			switch(flyMode.getMode()) {
			case "Vanilla":
				mc.thePlayer.capabilities.allowFlying = true;
				mc.thePlayer.capabilities.isFlying = true;
				break;
			case "Verus":
				mc.thePlayer.capabilities.allowFlying = true;
				mc.thePlayer.capabilities.isFlying = true;
				break;
			case "Vulcan":
				if(upOrDown = true)
					timeFly = 500;

				if(upOrDown = false)
					timeFly = 630;

				if(!mc.gameSettings.keyBindJump.pressed && !mc.gameSettings.keyBindSneak.pressed)
					timeFly = 550;

				if(mc.gameSettings.keyBindJump.pressed)
					upOrDown = true;

				if(mc.gameSettings.keyBindSneak.pressed)
					upOrDown = false;

				if(timer.hasTimeElapsed(368, false)) {
					mc.thePlayer.motionX *= 0.66F;
					mc.thePlayer.motionZ *= 0.66F;
					mc.thePlayer.onGround = false;
				}

				if(timer.hasTimeElapsed(548, false)) {
					if(mc.thePlayer.isSprinting())
						sprintBefore = true;
					else
						sprintBefore = false;
				}

				if(timer.hasTimeElapsed(549, false))
					mc.thePlayer.setSprinting(false);

				if(timer.hasTimeElapsed(timeFly, false))
					mc.thePlayer.jump();

				if(timer.hasTimeElapsed(timeFly + 1, true))
					mc.thePlayer.setSprinting(sprintBefore);
				break;
			case "Glide":
				if (!mc.thePlayer.onGround) {
					if (tick >= 1 && tick <= 20 && tick % 2 == 1)
						mc.thePlayer.motionY = 0.07;

					if (tick == 20)
						tick = 0;

					tick++;
					mc.thePlayer.onGround = false;
				}
				break;
			case "Spartan2":
				double x = mc.thePlayer.posX, y = mc.thePlayer.posY, z = mc.thePlayer.posZ;
				mc.thePlayer.motionY = 0F;
				mc.thePlayer.onGround = true;
				mc.thePlayer.fallDistance = 0F;
				break;
			case "Float":
				double y2, y1;
				mc.thePlayer.motionY = 0;
				if(mc.thePlayer.ticksExisted % 3 == 0) {
					y2 = mc.thePlayer.posY - 1.0E-10D;
					mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, y2, mc.thePlayer.posZ, true));
				}
				y1 = mc.thePlayer.posY + 1.0E-10D;
				mc.thePlayer.setPosition(mc.thePlayer.posX, y1, mc.thePlayer.posZ);
				break;
			case "SpoofGround":
				double x3 = mc.thePlayer.posX, y3 = mc.thePlayer.posY, z3 = mc.thePlayer.posZ;
				mc.thePlayer.onGround = true;
				mc.thePlayer.sendQueue.addToSendQueue(new C04PacketPlayerPosition(x3, y3, z3, true));
				mc.thePlayer.motionY = 0;
				mc.thePlayer.fallDistance = 0;
				if(mc.gameSettings.viewBobbing) {
					mc.thePlayer.cameraYaw = 0.1F;
					mc.thePlayer.cameraPitch = 0.1F;			
				}
				break;			
			case "Dev":
				mc.thePlayer.motionY = 0;
				if(timer.hasTimeElapsed(70, true)) {
					MoveUtil.stop();
					MoveUtil.setSpeed(0F);
				} else if(timer.hasTimeElapsed(30, false))
					MoveUtil.setSpeed(1F);
				break;
			}
		}
	}

	public void onEnable() {
		NotificationManager.show(new Notification(NotificationType.ENABLE, getName() + " was enabled!", 1));
		switch (flyMode.getMode()) {
		case "Vanilla":
			mc.thePlayer.jump();
			break;
		case "Vulcan":
			float startY = (float)mc.thePlayer.posY;
			float y = (float)mc.thePlayer.lastTickPosY;
			float YoffSet = (float)(startY - mc.thePlayer.posY);
			if(debugMode.isEnabled())
				Client.addChatMessage("Vulcan §7- §fCollided, Y offset is: " + YoffSet);
			break;
		case "Dev":
			if(startBoost.isEnabled()) {
				mc.thePlayer.jump();
				mc.thePlayer.motionX *= 2;
				mc.thePlayer.motionZ *= 2;
			}
			break;
		}
	}

	public void onDisable() {
		NotificationManager.show(new Notification(NotificationType.DISABLE, getName() + " was disabled!", 1));
		if(mc.thePlayer.capabilities.isCreativeMode) {
			mc.thePlayer.capabilities.isFlying = false;
			mc.thePlayer.capabilities.allowFlying = true;
		}else {
			mc.thePlayer.capabilities.isFlying = false;
			mc.thePlayer.capabilities.allowFlying = false;
		}
		mc.timer.timerSpeed = 1;
	}
}