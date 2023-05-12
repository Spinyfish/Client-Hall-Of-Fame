package markgg.modules.combat;

import java.awt.Color;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import markgg.Client;
import markgg.events.Event;
import markgg.events.listeners.EventMotion;
import markgg.events.listeners.EventRenderGUI;
import markgg.modules.Module;
import markgg.settings.BooleanSetting;
import markgg.settings.KeybindSetting;
import markgg.settings.ModeSetting;
import markgg.settings.NumberSetting;
import markgg.ui.HUD;
import markgg.ui.notifs.Notification;
import markgg.ui.notifs.NotificationManager;
import markgg.ui.notifs.NotificationType;
import markgg.util.MathUtil;
import markgg.util.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemSword;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.potion.Potion;
import net.minecraft.world.World;

public class KillAura extends Module{

	public Timer timer = new Timer();
	public static List<EntityLivingBase> targets;
	public NumberSetting range = new NumberSetting("Range", this, 4, 1, 6, 0.1);
	public NumberSetting aps = new NumberSetting("APS", this, 10, 1, 20, 1);
	public BooleanSetting noSwing = new BooleanSetting("No Swing", this, false);
	public BooleanSetting autoBlock = new BooleanSetting("Auto Block", this, true);
	public BooleanSetting silentRotate = new BooleanSetting("Silent Rotate", this, true);
	public BooleanSetting attackMobs = new BooleanSetting("Attack Mobs", this, false);
	public ModeSetting blockMode = new ModeSetting("Block Mode", this, "Legit", "Legit", "Fake");

	public KillAura() {
		super("KillAura", "Hits people for you", 0, Category.COMBAT);
		addSettings(blockMode,range, aps, noSwing, silentRotate, autoBlock, attackMobs);
	}
	
	public static int getRainbow(float seconds, float saturation, float brightness) {
		float hue = (System.currentTimeMillis() % (int)(seconds * 1000)) / (float)(seconds * 1000);
		int color = Color.HSBtoRGB(hue, saturation, brightness);
		return color;
	}

	public void onEvent(Event e) {
		ScaledResolution sr = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
		FontRenderer fr = mc.fontRendererObj;
		if(e instanceof EventMotion) {
			if(e.isPre()) {

				EventMotion event = (EventMotion)e;

				List<EntityLivingBase> targets = (List<EntityLivingBase>) mc.theWorld.loadedEntityList.stream().filter(EntityLivingBase.class::isInstance).collect(Collectors.toList());
				targets = targets.stream().filter(entity -> entity.getDistanceToEntity(mc.thePlayer) < range.getValue() && entity != mc.thePlayer  && !entity.isInvisible()).collect(Collectors.toList());
				targets.sort(Comparator.comparingDouble(entity -> ((EntityLivingBase)entity).getDistanceToEntity(mc.thePlayer)));

				if(!attackMobs.isEnabled()) {
					targets = targets.stream().filter(EntityPlayer.class::isInstance).collect(Collectors.toList());
				}else {
					//not filtered
				}
				
				if(!targets.isEmpty()) {
					EntityLivingBase target = targets.get(0);

					mc.thePlayer.rotationYawHead = getRotations(target)[0];
					mc.thePlayer.renderYawOffset = getRotations(target)[1];
					
					if(silentRotate.isEnabled()) {
						event.setYaw(getRotations(target)[0]);
						event.setPitch(getRotations(target)[1]);
					}else {
						if(!target.isDead) {
							mc.thePlayer.rotationYaw = getRotations(target)[0];
							mc.thePlayer.rotationPitch = getRotations(target)[1];
						}
					}

					if(timer.hasTimeElapsed((long) (1000 / aps.getValue()), true)) {
						if(noSwing.isEnabled()) {
							mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
						}else {
							mc.thePlayer.swingItem();
						}
						if(autoBlock.isEnabled()) {
							if(blockMode.is("Legit")) {
								if(holdingSword()) {
									mc.playerController.sendUseItem((EntityPlayer)mc.thePlayer, (World)mc.theWorld,mc.thePlayer.inventory.getCurrentItem());
								}
							}else {
								if(holdingSword()) {
									mc.thePlayer.setItemInUse(mc.thePlayer.getHeldItem(), 32767);
								}
							}
						}
						if(target.hurtTime < 8) {
							mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(target, Action.ATTACK));
						}	
					}
				}
			}
		}
		if(e instanceof EventRenderGUI) {
			if (Client.getModuleByName("TargetHUD").toggled == true) {

				int primaryColor = 0xFFE44964;
				if(Client.isModuleToggled("Colors")) {
					if(((ModeSetting)Client.getModuleByName("Colors").settings.get(0)).getMode() == "Rainbow") {
						primaryColor = getRainbow(4, 0.8f, 1);
					}else if(((ModeSetting)Client.getModuleByName("Colors").settings.get(0)).getMode() == "Custom") {
						int red1 = (int) ((NumberSetting)Client.getModuleByName("Colors").settings.get(1)).getValue();
						int green1 = (int) ((NumberSetting)Client.getModuleByName("Colors").settings.get(2)).getValue();
						int blue1 = (int) ((NumberSetting)Client.getModuleByName("Colors").settings.get(3)).getValue();
						primaryColor = new Color(red1,green1,blue1).getRGB();
					}
				}else {
					primaryColor = 0xFFE44964;
				}

				targets = (List<EntityLivingBase>)mc.theWorld.loadedEntityList.stream().filter(EntityLivingBase.class::isInstance).collect(Collectors.toList());
				targets = targets.stream().filter(entity -> entity.getDistanceToEntity(mc.thePlayer) < range.getValue()  && entity != mc.thePlayer && !entity.isDead && entity.getHealth() > 0).collect(Collectors.toList());
				targets.sort(Comparator.comparingDouble(entity -> ((EntityLivingBase)entity).getDistanceToEntity(mc.thePlayer)));

				Entity closestEntity = getClosestEntity();
				if (closestEntity instanceof EntityLivingBase) {
					EntityLivingBase entityLiving = (EntityLivingBase) closestEntity;
					if(!targets.isEmpty()) {
						int width = sr.getScaledWidth();
						int height = sr.getScaledHeight();
						int centerX = width / 2;
						int centerY = height / 2;
						int boxWidth = 120;
						int boxHeight = 40;
						int boxX = centerX - (boxWidth / 2) + 80;
						int boxY = centerY - (boxHeight / 2) + 30;
						Gui.drawRect(boxX, boxY, boxX + boxWidth, boxY + boxHeight, -1879048192);
						switch(((ModeSetting)Client.getModuleByName("HUD").settings.get(0)).getMode()) {
						case "Classic":
							Gui.drawRect(boxX + 5, boxY + 5, boxX + 4, boxY + boxHeight - 5, primaryColor);
							Gui.drawRect(boxX + 117, boxY + 5, boxX + 116, boxY + boxHeight - 5, primaryColor);
							break;
						case "Sense":
							Gui.drawRect(boxX + boxX / 2 - 50, boxY, boxX, boxY + 1, primaryColor);
							break;
						}
						

						String entName = entityLiving.getName();
						int messageWidth = fr.getStringWidth(entName);
						int messageX = boxX + (boxWidth / 2) - 50;
						int messageY = boxY + 5;
						fr.drawStringWithShadow(entName, messageX, messageY, 0xFFFFFF);

						double entHealth = entityLiving.getHealth();
						int roundHealth = (int) MathUtil.round(entHealth, 2.0D);

						double entDist = mc.thePlayer.getDistanceToEntity(closestEntity);
						int roundDist = (int) MathUtil.round(entDist, 2.0D);

						String healthMessage = "Health: " + roundHealth;
						int healthMessageX = boxX + (boxWidth / 2) - 50;
						int healthMessageY = boxY + 15;
						fr.drawStringWithShadow(healthMessage, healthMessageX, healthMessageY, 0xFFFFFF);

						String distMessage = "Distance: " + roundDist;
						int distMessageWidth = fr.getStringWidth(healthMessage);
						int distMessageX = boxX + (boxWidth / 2) - 50;
						int distMessageY = boxY + 25;
						fr.drawStringWithShadow(distMessage, distMessageX, distMessageY, 0xFFFFFF);

						if(closestEntity instanceof EntityCreeper) {
							mc.getTextureManager().bindTexture(new ResourceLocation("Raze/Head/creeperFace.png"));
							Gui.drawModalRectWithCustomSizedTexture(boxX + 80, boxY + 4, 0, 0, 32, 32, 32, 32);
						}else if(closestEntity instanceof EntitySkeleton) {
							if(closestEntity.isImmuneToFire()) {
								mc.getTextureManager().bindTexture(new ResourceLocation("Raze/Head/witherSkeletonFace.png"));
								Gui.drawModalRectWithCustomSizedTexture(boxX + 80, boxY + 4, 0, 0, 32, 32, 32, 32);
							}else {
								mc.getTextureManager().bindTexture(new ResourceLocation("Raze/Head/skeletonFace.png"));
								Gui.drawModalRectWithCustomSizedTexture(boxX + 80, boxY + 4, 0, 0, 32, 32, 32, 32);
							}
						}else if(closestEntity instanceof EntitySpider) {
							if((EntitySpider) closestEntity instanceof EntityCaveSpider) {
								mc.getTextureManager().bindTexture(new ResourceLocation("Raze/Head/caveSpiderFace.png"));
								Gui.drawModalRectWithCustomSizedTexture(boxX + 80, boxY + 4, 0, 0, 32, 32, 32, 32);
							}else {
								mc.getTextureManager().bindTexture(new ResourceLocation("Raze/Head/spiderFace.png"));
								Gui.drawModalRectWithCustomSizedTexture(boxX + 80, boxY + 4, 0, 0, 32, 32, 32, 32);
							}
						}else if(closestEntity instanceof EntityZombie) {
							if((EntityZombie) closestEntity instanceof EntityPigZombie) {
								mc.getTextureManager().bindTexture(new ResourceLocation("Raze/Head/pigmanFace.png"));
								Gui.drawModalRectWithCustomSizedTexture(boxX + 80, boxY + 4, 0, 0, 32, 32, 32, 32);
							}else {
								mc.getTextureManager().bindTexture(new ResourceLocation("Raze/Head/zombieFace.png"));
								Gui.drawModalRectWithCustomSizedTexture(boxX + 80, boxY + 4, 0, 0, 32, 32, 32, 32);
							}
						}else if(closestEntity instanceof EntityBat) {
							mc.getTextureManager().bindTexture(new ResourceLocation("Raze/Head/batFace.png"));
							Gui.drawModalRectWithCustomSizedTexture(boxX + 80, boxY + 4, 0, 0, 32, 32, 32, 32);
						}else if(closestEntity instanceof EntityBlaze) {
							mc.getTextureManager().bindTexture(new ResourceLocation("Raze/Head/blazeFace.png"));
							Gui.drawModalRectWithCustomSizedTexture(boxX + 80, boxY + 4, 0, 0, 32, 32, 32, 32);
						}else if(closestEntity instanceof EntityChicken) {
							mc.getTextureManager().bindTexture(new ResourceLocation("Raze/Head/chickenFace.png"));
							Gui.drawModalRectWithCustomSizedTexture(boxX + 80, boxY + 4, 0, 0, 32, 32, 32, 32);
						}else if(closestEntity instanceof EntityVillager) {
							mc.getTextureManager().bindTexture(new ResourceLocation("Raze/Head/villagerFace.png"));
							Gui.drawModalRectWithCustomSizedTexture(boxX + 80, boxY + 4, 0, 0, 32, 32, 32, 32);
						}else if(closestEntity instanceof EntitySquid) {
							mc.getTextureManager().bindTexture(new ResourceLocation("Raze/Head/squidFace.png"));
							Gui.drawModalRectWithCustomSizedTexture(boxX + 80, boxY + 4, 0, 0, 32, 32, 32, 32);
						}else if(closestEntity instanceof EntitySnowman) {
							mc.getTextureManager().bindTexture(new ResourceLocation("Raze/Head/snowGolemFace.png"));
							Gui.drawModalRectWithCustomSizedTexture(boxX + 80, boxY + 4, 0, 0, 32, 32, 32, 32);
						}else if(closestEntity instanceof EntityIronGolem) {
							mc.getTextureManager().bindTexture(new ResourceLocation("Raze/Head/ironGolemFace.png"));
							Gui.drawModalRectWithCustomSizedTexture(boxX + 80, boxY + 4, 0, 0, 32, 32, 32, 32);
						}else if(closestEntity instanceof EntityCow) {
							if((EntityCow) closestEntity instanceof EntityMooshroom) {
								mc.getTextureManager().bindTexture(new ResourceLocation("Raze/Head/mooshroomFace.png"));
								Gui.drawModalRectWithCustomSizedTexture(boxX + 80, boxY + 4, 0, 0, 32, 32, 32, 32);
							}else {
								mc.getTextureManager().bindTexture(new ResourceLocation("Raze/Head/cowFace.png"));
								Gui.drawModalRectWithCustomSizedTexture(boxX + 80, boxY + 4, 0, 0, 32, 32, 32, 32);
							}
						}else if(closestEntity instanceof EntitySheep) {
							mc.getTextureManager().bindTexture(new ResourceLocation("Raze/Head/sheepFace.png"));
							Gui.drawModalRectWithCustomSizedTexture(boxX + 80, boxY + 4, 0, 0, 32, 32, 32, 32);
						}else if(closestEntity instanceof EntityWitch) {
							mc.getTextureManager().bindTexture(new ResourceLocation("Raze/Head/witchFace.png"));
							Gui.drawModalRectWithCustomSizedTexture(boxX + 80, boxY + 4, 0, 0, 32, 32, 32, 32);
						}else if(closestEntity instanceof EntitySlime) {
							if((EntitySlime) closestEntity instanceof EntityMagmaCube) {
								mc.getTextureManager().bindTexture(new ResourceLocation("Raze/Head/magmaCubeFace.png"));
								Gui.drawModalRectWithCustomSizedTexture(boxX + 80, boxY + 4, 0, 0, 32, 32, 32, 32);
							}else {
								mc.getTextureManager().bindTexture(new ResourceLocation("Raze/Head/slimeFace.png"));
								Gui.drawModalRectWithCustomSizedTexture(boxX + 80, boxY + 4, 0, 0, 32, 32, 32, 32);
							}
						}else if(closestEntity instanceof EntityEnderman) {
							mc.getTextureManager().bindTexture(new ResourceLocation("Raze/Head/endermanFace.png"));
							Gui.drawModalRectWithCustomSizedTexture(boxX + 80, boxY + 4, 0, 0, 32, 32, 32, 32);
						}else if(closestEntity instanceof EntityGhast) {
							mc.getTextureManager().bindTexture(new ResourceLocation("Raze/Head/ghastFace.png"));
							Gui.drawModalRectWithCustomSizedTexture(boxX + 80, boxY + 4, 0, 0, 32, 32, 32, 32);
						}else if(closestEntity instanceof EntitySilverfish) {
							mc.getTextureManager().bindTexture(new ResourceLocation("Raze/Head/silverfishFace.png"));
							Gui.drawModalRectWithCustomSizedTexture(boxX + 80, boxY + 4, 0, 0, 32, 32, 32, 32);
						}else if(closestEntity instanceof EntityEndermite) {
							mc.getTextureManager().bindTexture(new ResourceLocation("Raze/Head/endermiteFace.png"));
							Gui.drawModalRectWithCustomSizedTexture(boxX + 80, boxY + 4, 0, 0, 32, 32, 32, 32);
						}else if(closestEntity instanceof EntityGuardian) {
							mc.getTextureManager().bindTexture(new ResourceLocation("Raze/Head/guardianFace.png"));
							Gui.drawModalRectWithCustomSizedTexture(boxX + 80, boxY + 4, 0, 0, 32, 32, 32, 32);
						}else if(closestEntity instanceof EntityPig) {
							mc.getTextureManager().bindTexture(new ResourceLocation("Raze/Head/pigFace.png"));
							Gui.drawModalRectWithCustomSizedTexture(boxX + 80, boxY + 4, 0, 0, 32, 32, 32, 32);
						}else if(closestEntity instanceof EntityOcelot) {
							mc.getTextureManager().bindTexture(new ResourceLocation("Raze/Head/ocelotFace.png"));
							Gui.drawModalRectWithCustomSizedTexture(boxX + 80, boxY + 4, 0, 0, 32, 32, 32, 32);
						}else if(closestEntity instanceof EntityWolf) {
							mc.getTextureManager().bindTexture(new ResourceLocation("Raze/Head/dogFace.png"));
							Gui.drawModalRectWithCustomSizedTexture(boxX + 80, boxY + 4, 0, 0, 32, 32, 32, 32);
						}else if(closestEntity instanceof EntityRabbit) {
							mc.getTextureManager().bindTexture(new ResourceLocation("Raze/Head/rabbitFace.png"));
							Gui.drawModalRectWithCustomSizedTexture(boxX + 80, boxY + 4, 0, 0, 32, 32, 32, 32);
						}else if(closestEntity instanceof EntityHorse) {
							mc.getTextureManager().bindTexture(new ResourceLocation("Raze/Head/horseFace.png"));
							Gui.drawModalRectWithCustomSizedTexture(boxX + 80, boxY + 4, 0, 0, 32, 32, 32, 32);
						}else if(closestEntity instanceof EntityWither) {
							mc.getTextureManager().bindTexture(new ResourceLocation("Raze/Head/witherFace.png"));
							Gui.drawModalRectWithCustomSizedTexture(boxX + 80, boxY + 4, 0, 0, 32, 32, 32, 32);
						}else {
							if(closestEntity instanceof EntityPlayer) {
								ResourceLocation skinLoc1 = ((AbstractClientPlayer) targets.get(0)).getLocationSkin();
								mc.getTextureManager().bindTexture(skinLoc1);
								Gui.drawModalRectWithCustomSizedTexture(boxX + 80, boxY + 4, 32, 32, 32, 32, 256, 256);
							}else {
								mc.getTextureManager().bindTexture(new ResourceLocation("Raze/Head/steveFace.png"));
								Gui.drawModalRectWithCustomSizedTexture(boxX + 80, boxY + 4, 0, 0, 32, 32, 32, 32);
							}
						}
					}
				}
			}
		}
	}

	public float[] getRotations(Entity e) {
		double deltaX = e.posX - (e.posX - e.lastTickPosX) - mc.thePlayer.posX;
		double deltaY = e.posY - 3.5 + e.getEyeHeight() - mc.thePlayer.posY + mc.thePlayer.getEyeHeight() + 0.2;
		double deltaZ = e.posZ + (e.posZ - e.lastTickPosZ) - mc.thePlayer.posZ;
		double distance = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2) + Math.pow(deltaZ, 2));

		float yaw = (float) Math.toDegrees(-Math.atan2(deltaX, deltaZ));
		float pitch = (float) -Math.toDegrees(Math.atan2(deltaY, Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaZ, 2))));
		float deltaYaw = yaw - mc.thePlayer.rotationYaw;
		float deltaPitch = pitch - mc.thePlayer.rotationPitch;
		deltaYaw = MathHelper.wrapAngleTo180_float(deltaYaw);
		deltaPitch = MathHelper.wrapAngleTo180_float(deltaPitch);

		float adjustedRotationSpeed = (float) (40 - Math.random() * 10 + Math.random() * 10);
		deltaYaw = Math.min(adjustedRotationSpeed, Math.max(-adjustedRotationSpeed, deltaYaw));
		deltaPitch = Math.min(adjustedRotationSpeed, Math.max(-adjustedRotationSpeed, deltaPitch));
		yaw = mc.thePlayer.rotationYaw + deltaYaw;
		pitch = mc.thePlayer.rotationPitch + deltaPitch;

		yaw = yaw + (float) (Math.random() - Math.random()) * 2;
		pitch = pitch + (float) (Math.random() - Math.random()) * 2;
		return new float[] {yaw, pitch };

	}

	private Entity getClosestEntity() {
		Entity closestEntity = null;
		double closestDistanceSq = Double.POSITIVE_INFINITY;
		EntityPlayerSP player = mc.thePlayer;

		for (Object obj : mc.theWorld.loadedEntityList) {
			if (obj instanceof Entity && obj != player) {
				Entity entity = (Entity) obj;
				double distanceSq = entity.getDistanceSq(player.posX, player.posY, player.posZ);
				if (distanceSq < closestDistanceSq) {
					closestEntity = entity;
					closestDistanceSq = distanceSq;
				}
			}
		}

		return closestEntity;
	}

	public boolean holdingSword() {
		if (mc.thePlayer.getCurrentEquippedItem() != null && mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemSword) {
			return true;
		}
		if (mc.thePlayer.getCurrentEquippedItem() != null && mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemAxe) {
			return true;
		}
		return false;
	}
	
	public void onDisable() {
		NotificationManager.show(new Notification(NotificationType.DISABLE, getName() + " was disabled!", 1));
		mc.gameSettings.keyBindUseItem.pressed = false;
	}

}
