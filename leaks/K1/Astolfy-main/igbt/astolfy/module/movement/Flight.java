package igbt.astolfy.module.movement;

import java.util.ArrayList;

import igbt.astolfy.ui.Notifications.NotificationManager;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import org.lwjgl.input.Keyboard;

import igbt.astolfy.Astolfy;
import igbt.astolfy.events.Event;
import igbt.astolfy.events.listeners.EventMotion;
import igbt.astolfy.events.listeners.EventPacket;
import igbt.astolfy.module.ModuleBase;
import igbt.astolfy.settings.settings.ModeSetting;
import igbt.astolfy.settings.settings.NumberSetting;
import igbt.astolfy.ui.Notifications.Notification;
import igbt.astolfy.ui.Notifications.NotificationType;
import igbt.astolfy.utils.TimerUtils;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public class Flight extends ModuleBase {

	public NumberSetting speed = new NumberSetting("Speed", 0.5, 0.2, 0.15, 4);
	public ModeSetting mode;
	public int st = 0;
	public int t = 0;
	public double realY = 0;
	public double startY = 0;
	public TimerUtils timer = new TimerUtils();
	public Flight() {
		super("Flight", Keyboard.KEY_V, Category.MOVEMENT);
		ArrayList<String> modes = new ArrayList<>();
		mode = new ModeSetting("Mode", "Smooth", "AGC", "Hypixel", "BlocksMC", "Vanilla");
		addSettings(mode,speed);
	}
	public static boolean hurt = false;
	public ArrayList<Packet> agcPackets = new ArrayList();

	public void onSkipEvent(Event event) {
		if(event instanceof EventPacket) {
			if(!isToggled())
			if(agcPackets.size() > 0) {
				mc.thePlayer.motionX = 0;
				mc.thePlayer.motionY = 0;
				mc.thePlayer.motionZ = 0;
				if(timer.hasReached(50)) {
					C03PacketPlayer c033 = (C03PacketPlayer)agcPackets.get(0);
					System.out.println(c033.x);
					mc.thePlayer.sendQueue.addToSendQueueNoEvent(agcPackets.get(0));
					agcPackets.remove(0);
					timer.reset();
				}
				EventPacket e = (EventPacket)event;
				if(e.getPacket() instanceof C03PacketPlayer) {
					C03PacketPlayer c03 = (C03PacketPlayer)e.getPacket();
					e.setCancelled(true);
				}
			}
		}
	}
	public void onEvent(Event e) {
		if(e instanceof EventMotion) {
			mc.thePlayer.capabilities.isFlying = false;
	        mc.thePlayer.cameraYaw = (float)(double) speed.getValue() * 50 / 1000;
			setSuffix(mode.getCurrentValue());
			switch(mode.getCurrentValue()) {
			case "Smooth":
					if(mc.thePlayer.moveForward != 0 || mc.thePlayer.moveStrafing != 0) {
						mc.thePlayer.setSpeed(speed.getValue());
					}else {
						mc.thePlayer.motionX /= 1.2;
						mc.thePlayer.motionZ /= 1.2;
					}
					if(mc.gameSettings.keyBindJump.isKeyDown())
						mc.thePlayer.motionY = 0.5;
					else if(mc.gameSettings.keyBindSneak.isKeyDown()) {
						mc.thePlayer.setSneaking(false);
						mc.thePlayer.motionY = -0.5;
					} else
						mc.thePlayer.motionY = 0.03 + mc.thePlayer.motionY/1.2;
				break;
			case "Hypixel":
				//System.out.println(t);
					if(t > 2) {
						mc.thePlayer.motionY = 0;
						mc.thePlayer.setSpeed(speed.getValue());
					}
				break;
			case "AGC":
				mc.timer.timerSpeed = 1.0f;
				if(realY < startY) {
					mc.thePlayer.motionY = 0.42f;
				}else {
					mc.thePlayer.posY = startY;
				}
				mc.thePlayer.setSpeed(speed.getValue());
				break;
			default:
					if(mc.thePlayer.hurtTime > 0.1)
						hurt = true;
					if(hurt) {
						mc.timer.timerSpeed = 0.8f;
						if(mc.thePlayer.moveForward != 0 || mc.thePlayer.moveStrafing != 0) {
							mc.thePlayer.setSpeed(speed.getValue() * 2);
						} else {
							mc.thePlayer.motionX = 0;
							mc.thePlayer.motionZ = 0;
						}
						if(mc.gameSettings.keyBindJump.isKeyDown())
							mc.thePlayer.motionY = 0.4;
						else if(mc.gameSettings.keyBindSneak.isKeyDown()) {
							mc.thePlayer.motionY = -0.4;
						} 
						else
							mc.thePlayer.motionY = 0;
					}
			break;	
			}
		}
		if(e instanceof EventPacket) {
			EventPacket p = (EventPacket)e;
			
			if(p.getPacket() instanceof C03PacketPlayer) {
				realY = ((C03PacketPlayer)p.getPacket()).getPositionY();
			}
			if(mode.getCurrentValue().equalsIgnoreCase("HYPIXEL") && p.getPacket() instanceof C03PacketPlayer){
				C03PacketPlayer c03 = (C03PacketPlayer) p.getPacket();
				if(t == 0){
					c03.y = startY - 1.5;
					t = 1;
				}else if(t == 1){
					t = 2;
				}else if(t == 2){
					mc.thePlayer.setPosition(mc.thePlayer.posX, startY + 0.1, mc.thePlayer.posZ);
					t = 3;
				}
			}
			if(mode.getCurrentValue().equalsIgnoreCase("AGC")) {
				if(p.getPacket() instanceof C03PacketPlayer) {
					C03PacketPlayer c03 = (C03PacketPlayer)p.getPacket();
					//agcPackets.add(c03);
					//e.setCancelled(true);
				}
			}
		}
	}
	
	public void onEnable() {
		realY = mc.thePlayer.posY;
		startY = mc.thePlayer.posY;
		st = (int)mc.thePlayer.posY;
		t = 0;
		if(Astolfy.moduleManager.getModuleByName("Speed").isToggled()){
			Astolfy.moduleManager.getModuleByName("Speed").setToggled(false);
			NotificationManager.showNotification(new Notification(NotificationType.WARNING, "Anti-Flag","Disabled Speed To Prevent Flags",3));
		}
		switch(mode.getCurrentValue()) {
			case "BlocksMC":
				Astolfy.notificationManager.showNotification(new Notification(NotificationType.INFORMATION, "Flight", "Trying to damage player", 2));
				mc.timer.timerSpeed = 0.175f;
				mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,mc.thePlayer.posY + 3.1005,mc.thePlayer.posZ,false));
				mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,mc.thePlayer.posY,mc.thePlayer.posZ,false));
				mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,mc.thePlayer.posY,mc.thePlayer.posZ,true));
				mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.25, mc.thePlayer.posZ);
				break;
			case "AGC":
				//mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - 2, mc.thePlayer.posZ, true));
				
				break;
			case "Vanilla":
				hurt = true;
				break;
				default: hurt = true; break;
				case "Hypixel":
					break;
		}
	}
	
	public void onDisable() {
		mc.thePlayer.setPosition(mc.thePlayer.posX, realY, mc.thePlayer.posZ);
		if(!mode.getCurrentValue().equalsIgnoreCase("Smooth")) {
			mc.thePlayer.motionZ = 0;
			mc.thePlayer.motionX = 0;
		}
		hurt = false;
		mc.timer.timerSpeed = 1;
	}

}
