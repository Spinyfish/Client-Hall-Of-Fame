package haptic.module.impl.movement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.input.Keyboard;

import com.mojang.realmsclient.gui.ChatFormatting;

import haptic.Haptic;
import haptic.event.Event;
import haptic.event.impl.EventMotionUpdate;
import haptic.event.impl.EventPostMotionUpdate;
import haptic.event.impl.EventReceivePacket;
import haptic.event.impl.EventRender;
import haptic.event.impl.EventSendPacket;
import haptic.event.impl.EventUpdate;
import haptic.event.impl.MovementEvent;
import haptic.module.Category;
import haptic.module.ListeningType;
import haptic.module.Module;
import haptic.module.impl.combat.Killaura;
import haptic.setting.impl.ModeSetting;
import haptic.setting.impl.NumberSetting;
import haptic.util.misc.MathUtils;
import haptic.util.network.PacketUtils;
import haptic.util.network.ServerUtils;
import haptic.util.player.DamageUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0BPacketEntityAction.Action;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.network.play.server.S22PacketMultiBlockChange;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.network.play.server.S24PacketBlockAction;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

public class Fly extends Module {
	
	private ModeSetting mode = new ModeSetting("Mode", "Vanilla", "Vanilla", "Airwalk", "Airjump", "PulsiveHVH");
	
	private ModeSetting vanillaMode = new ModeSetting("Vanilla Mode", "Motion", "Motion", "Creative");
	private NumberSetting vanillaSpeed = new NumberSetting("Vanilla Speed", 1, 0.2, 9, 0.2, this);
	private NumberSetting vanillaVerticalSpeed = new NumberSetting("Vanilla Vertical Speed", 1, 0.2, 9, 0.2, this);
	
	private NumberSetting timer = new NumberSetting("Timer", 1, 0.1, 4, 0.1, this);
	
	
	//These are some variables I used for other fly modes in vestige
	private BlockPos originalPos;
	private double x, y, z;
	
	private ArrayList<BlockPos> blocks = new ArrayList<>();
	
	private double speed;
	private int counter, ticks;
	private boolean lagbacked;
	private boolean started;
	
	private double motionY;
	private double lastDist;
	
	public Fly() {
		super("Fly", Category.MOVEMENT, ListeningType.ALWAYS, Keyboard.KEY_G);
		this.addSettings(mode, vanillaMode, vanillaSpeed, vanillaVerticalSpeed, timer);
	}
	
	public void onEnable() {
		originalPos = new BlockPos(x = mc.thePlayer.posX, y = mc.thePlayer.posY, z = mc.thePlayer.posZ);
		ticks = counter = 0;
		lagbacked = false;
		
		started = false;
		
		if(mode.is("PulsiveHVH")) {
			speed = 0.28;
			mc.thePlayer.jump();
			move.setSpeed(0.4);
		}
	}
	
	public void onDisable() {
		mc.thePlayer.capabilities.isFlying = false;
		mc.timer.timerSpeed = 1F;
		if(mode.is("Vanilla")) {
			mc.thePlayer.motionX *= 0.1;
			mc.thePlayer.motionY *= 0.1;
			mc.thePlayer.motionZ *= 0.1;
		}
		
		if(mode.is("PulsiveHVH")) {
			move.setSpeed(Math.min(speed, move.getBaseMoveSpeed()));
		}
		
		//If your fly is an airjump fly that requires you to place barrier blocks under you client-side
		if(!blocks.isEmpty()) {
			for(BlockPos pos : blocks) {
				mc.theWorld.setBlockToAir(pos);	
			}
			blocks.clear();
		}
	}
	
	public void onEvent(Event e) {
		if(e instanceof EventRender) {
			vanillaMode.setShowed(mode.is("Vanilla"));
			vanillaSpeed.setShowed(mode.is("Vanilla") && vanillaMode.is("Motion"));
			vanillaVerticalSpeed.setShowed(mode.is("Vanilla") && vanillaMode.is("Motion"));
			timer.setShowed(false);
			
			this.setDisplayName(this.getName() + " " + ChatFormatting.GRAY + mode.getMode());
		}
		
		if(!isEnabled()) {
			return;
		}
		
		switch(mode.getMode()) {
		case "Vanilla":
			Vanilla(e);
			break;
		case "Airwalk":
			Airwalk(e);
			break;
		case "Airjump":
			Airjump(e);
			break;
		case "PulsiveHVH":
			PulsiveHVH(e);
			break;
		}
	}

	private void Vanilla(Event e) {
		if(e instanceof MovementEvent) {
			mc.timer.timerSpeed = 1F;
			MovementEvent event = (MovementEvent) e;
			if(vanillaMode.is("Motion")) {
				move.setSpeed(event, vanillaSpeed.getValue());
				
				if(mc.gameSettings.keyBindJump.isKeyDown()) {
					event.setMotionY(vanillaVerticalSpeed.getValue());
				} else if(mc.gameSettings.keyBindSneak.isKeyDown()) {
					event.setMotionY(-vanillaVerticalSpeed.getValue());
				} else {
					event.setMotionY(0);
				}
				mc.thePlayer.motionY = 0;
			} else if(vanillaMode.is("Creative")) {
				mc.thePlayer.capabilities.isFlying = true;
			}
		}
	}
	
	private void Airwalk(Event event) {
		if(event instanceof EventUpdate) {
			mc.thePlayer.motionY = 0;
			mc.thePlayer.onGround = true;
		} else if(event instanceof MovementEvent) {
			MovementEvent e = (MovementEvent) event;
			e.setMotionY(0);
			mc.gameSettings.keyBindJump.pressed = false;
		} else if(event instanceof EventMotionUpdate) {
			EventMotionUpdate e = (EventMotionUpdate) event;
			e.setOnGround(true);
		}
	}
	
	private void Airjump(Event event) {
		if(event instanceof EventUpdate) {
			if(mc.thePlayer.fallDistance > (mc.gameSettings.keyBindJump.isKeyDown() ? 0 : 0.6)) {
				BlockPos pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ);
				if(mc.theWorld.getBlockState(pos).getBlock() instanceof BlockAir) {
					if(!blocks.isEmpty()) {
						for(BlockPos pos1 : blocks) {
							mc.theWorld.setBlockToAir(pos1);
						}
						blocks.clear();
					}
					
					mc.theWorld.setBlockState(pos, Blocks.barrier.getDefaultState());
					blocks.add(pos);
				}
			}
			if(mc.thePlayer.onGround) {
				move.jump();
			}
		}
	}
	
	private void PulsiveHVH(Event event) {
		if(event instanceof EventMotionUpdate) {
			EventMotionUpdate e = (EventMotionUpdate) event;
			
			mc.timer.timerSpeed = 1F;
			
			mc.thePlayer.motionY = 0;
			
			/*
			If you have a boost :
			speed -= speed / 159;
			speed = Math.max(speed, move.getBaseMoveSpeed());
			
			if(!move.isWalking()) {
				speed = move.getBaseMoveSpeed();
			}
			*/
			
			move.setSpeed(speed);
			ticks++;
		}
	}
	
}