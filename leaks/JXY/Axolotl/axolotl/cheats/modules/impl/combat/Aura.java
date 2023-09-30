package axolotl.cheats.modules.impl.combat;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import axolotl.Axolotl;
import axolotl.cheats.events.EventType;
import axolotl.cheats.settings.*;
import axolotl.util.*;

import axolotl.cheats.events.Event;
import axolotl.cheats.events.MoveEvent;
import axolotl.cheats.modules.Module;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.*;
import net.minecraft.util.BlockPos;
import org.lwjgl.input.Keyboard;

public class Aura extends Module {

	public NumberSetting fov = new NumberSetting("FOV", 180, 5, 360, 15), range = new NumberSetting("Range", 3.5f, 1f, 6f, 0.1f), aps = new NumberSetting("APS", 10f, 1f, 20f, 1f), hitChance = new NumberSetting("HitChance", 100f, 5f, 100f, 5f);
	public BooleanSetting randomization = new BooleanSetting("Randomization", false),
		autoblock = new BooleanSetting("AutoBlock", false), noSwing = new BooleanSetting("NoSwing", false),
		mobs = new BooleanSetting("Mobs", true), players = new BooleanSetting("Players", true),
		animals = new BooleanSetting("Animals", true),
		teams = new BooleanSetting("Teams", false);
	public ModeSetting rotation = new ModeSetting("Rotation", "Server", "Server", "Client", "None"), look = new ModeSetting("Look", "Normal", "Normal", "Derp");
	public ModeSetting mode = new ModeSetting("Mode", "Single", "Single", "Switch", "Watchdog");
	
	public Aura() {
		super("Aura", Category.COMBAT, true);
		if(Math.random() < 0.069) {
			this.name = "NefKilla";
		}
		this.addSettings(range, aps, fov, randomization, teams, autoblock, noSwing, hitChance, rotation, mode, look, mobs, players, animals);
		this.setSpecialSetting(new SpecialSettings(new SpecialSetting(range, "R"), new SpecialSetting(aps, "APS")));
	}
	
	public String ip;
	public static EntityLivingBase target;
	public float yaw, pitch, timeRotating;
	public Timer timer = new Timer();

	public void onDisable() {
		mc.gameSettings.keyBindUseItem.pressed = false;
	}
	
	public void onEvent(Event event) {

    	if(mc.thePlayer == null || !(event instanceof MoveEvent)) return;
		if (event.eventType == EventType.PRE) {
			MoveEvent e = (MoveEvent) event;

			if(super.ipToggle())return;

			if (mc.thePlayer.deathTime > 1) {
				if (this.toggled) {
					this.toggle();
				}
				return;
			}

			if (target != null && target.getDistanceToEntity(mc.thePlayer) > range.getNumberValue() + 0.3) {
				target = null;
			}
			if (yaw != -6999 && pitch != -6999 && target != null && look.getMode().equalsIgnoreCase("Always")) {
				if (rotation.getMode().equalsIgnoreCase("Server")) {
					e.setYaw(yaw);
					e.setPitch(pitch);
				} else {
					mc.thePlayer.rotationYaw = yaw;
					mc.thePlayer.rotationPitch = pitch;
				}
			}
			List<EntityLivingBase> targets;
			if(teams.isEnabled()) {
				targets = EntityUtil.getEntitiesAroundPlayerTeams(range.getNumberValue(), mobs.getBValue(), players.getBValue(), animals.getBValue());
			} else {
				targets = EntityUtil.getEntitiesAroundPlayer(range.getNumberValue(), mobs.getBValue(), players.getBValue(), animals.getBValue());
			}
			if (!targets.isEmpty()) {
				int current = 0;
				int max = targets.size() - 1;
				EntityLivingBase target = targets.get(0);

				if (target.isInvisibleToPlayer(mc.thePlayer)) return;

				if (mode.getMode().equalsIgnoreCase("Switch") || (this.target != null && this.target.getDistanceToEntity(mc.thePlayer) >= range.getNumberValue() + 0.3))
					this.target = target;

				if (target instanceof EntityPlayer) {
					EntityPlayer target2 = (EntityPlayer) target;
					if (mode.getMode().equalsIgnoreCase("Watchdog")) {
						if (target2.posY > mc.thePlayer.posY + 1) {
							if (Material.air == mc.theWorld.getBlockState(new BlockPos(target2.posX, target2.posY, target2.posZ)).getBlock().getMaterial()) {
								target2.botValue++;
							}
						}
					}
					if (target2.botValue > 10) {
						target2.botValue--;
						if (current >= max) return;
						current++;
						target = targets.get(current);
					}
				}

				float[] rotations = getRotations(target);
				this.yaw = (float) (Math.round(rotations[0]) + (Math.random() * 10)) % 360;
				this.pitch = (float) (Math.round(rotations[1]) + (Math.random() * 10));

				yaw -= yaw % ((mc.gameSettings.mouseSensitivity / 2) + 0.1);
				pitch -= pitch % ((mc.gameSettings.mouseSensitivity / 2) + 0.1);
				if(mc.objectMouseOver.entityHit != target) {
					switch (rotation.getMode()) {
						case "Server":
							e.setYaw(yaw);
							if(look.getMode().equalsIgnoreCase("Normal"))e.setPitch(pitch);
							else e.setPitch(90);
							break;
						case "Client":
							mc.thePlayer.rotationYaw = yaw;
							mc.thePlayer.rotationPitch = pitch;
							break;
						default:
							break;
					}
				}
				//this.timeRotating++;
				this.target = target;
			}
		} else {
			if(this.target == null || this.target.isDead/* || this.timeRotating < 4*/) {
				mc.gameSettings.keyBindUseItem.pressed = false;
				return;
			}
			long time = (long) (1000.0D / (aps.getNumberValue() + (randomization.isEnabled() ? ((Math.random() * 3) - 1.5) : 0)));
			if (this.timer.hasTimeElapsed(time, Math.random() < (hitChance.getNumberValue() / 100))) {
				if(!noSwing.isEnabled()) {
					this.mc.thePlayer.swingItem();
				} else {
					this.mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
				}
				//timeRotating = 0;
				this.mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK));
				if(autoblock.isEnabled()) {
					if(mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
						mc.gameSettings.keyBindUseItem.pressed = mc.thePlayer.ticksExisted % 3 == 0;
					} else {
						mc.gameSettings.keyBindUseItem.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindUseItem.getKeyCode());
					}
				}
				mc.thePlayer.setSprinting(Keyboard.isKeyDown(mc.gameSettings.keyBindSprint.getKeyCode()));
			}
		}
	}
	
	  public float[] getRotations(Entity e) {
		    return RotationUtils.getRotationsToEntity(e);
	  }
	
}
