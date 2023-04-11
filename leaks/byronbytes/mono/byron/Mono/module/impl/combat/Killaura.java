package byron.Mono.module.impl.combat;

import byron.Mono.Mono;
import byron.Mono.clickgui.setting.Setting;
import byron.Mono.event.impl.Event2D;
import byron.Mono.event.impl.EventPreUpdate;
import byron.Mono.event.impl.EventUpdate;
import byron.Mono.font.FontUtil;
import byron.Mono.interfaces.ModuleInterface;
import byron.Mono.module.Category;
import byron.Mono.module.Module;
import byron.Mono.utils.AuraUtil;
import byron.Mono.utils.ColorUtils;
import byron.Mono.utils.RenderUtil;
import byron.Mono.utils.RotationUtil;
import byron.Mono.utils.TimeUtil;
import com.google.common.eventbus.Subscribe;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;

import java.awt.Color;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.lwjgl.opengl.GL11;

@ModuleInterface(name = "Killaura", description = "Lol.", category = Category.Combat)
public class Killaura extends Module {

	public static boolean toggled;
	public static float yaw, pitch;
	public static EntityLivingBase target;
	private final TimeUtil timeUtil = new TimeUtil();

	@Override
	public void setup() {
		super.setup();
		ArrayList<String> options = new ArrayList<>();
		rSetting(new Setting("Range", this, 4, 3, 6, false));
		rSetting(new Setting("APS", this, 13, 5, 20, true));
		rSetting(new Setting("TargetHUD", this, true));
		rSetting(new Setting("Autoblock", this, false));
		rSetting(new Setting("NoSwing", this, false));
		options.add("Verus");
		options.add("Default");
		rSetting(new Setting("Autoblock Mode", this, "Verus", options));
	}

	@Subscribe
	public void onUpdate(EventUpdate e) {
		
	   	 if (mc.thePlayer.getHealth() <= 0) { 
			 this.setToggled(false);
			 Mono.INSTANCE.sendAlert("Killaura was disabled due to death.");
	     }
	   	 
	   	 
		target = AuraUtil.getTarget(getSetting("Range").getValDouble());
		if (target == null) {
			yaw = mc.thePlayer.rotationYaw;
			pitch = mc.thePlayer.rotationYaw;
			target = null;
			return;
		}

		float[] rotations = RotationUtil.getRotations(target);
		yaw = rotations[0];
		pitch = rotations[1];

		if (timeUtil.hasTimePassed((long) (1000 / getSetting("APS").getValDouble()))) {
			if (getSetting("Autoblock").getValBoolean()) {
				mc.thePlayer.setItemInUse(mc.thePlayer.getHeldItem(), 20);
			}

			if (getSetting("Autoblock").getValBoolean() && getSetting("Autoblock Mode").getValString() == "Verus"
					&& mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemSword
					&& mc.thePlayer.getHeldItem() != null) {
				mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem());
			}

			AuraUtil.attack(target);
			timeUtil.reset();
		}
	}

	@Subscribe
	public void onPreUpdate(EventPreUpdate e) {
		if (target == null)
			return;

		mc.thePlayer.rotationYawHead = yaw;
		mc.thePlayer.renderYawOffset = yaw;
		e.setRotation(new float[] { yaw, pitch });
	}

	public static int astolfoColors(int yOffset, int yTotal) {
		float speed = 2900.0F;

		float hue;
		for (hue = (float) (System.currentTimeMillis() % (long) ((int) speed))
				+ (float) ((yTotal - yOffset) * 9); hue > speed; hue -= speed) {
		}

		hue /= speed;
		if ((double) hue > 1.5D) {
			hue = 0.5F - (hue - 0.5F);
		}

		hue += 0.5F;
		return Color.HSBtoRGB(hue, 0.4F, 1.0F);
	}

	double posX;
	double posY;
	double width;
	double height;
	int count = 0;
	float count2 = 0.0F;

	@Subscribe
	public void onRender2D(Event2D e) {
		if (target != null) {
			if (getSetting("TargetHUD").getValBoolean()) {

				int offset = 0;
				posX = e.getScaledResolution().getScaledWidth() / 2;
				posY = e.getScaledResolution().getScaledHeight() / 4;
				width = posX + 275;
				height = posY + 250;
				drawSmoothRect((float) posX + 25, (float) posY + 98, (float) width - 70, (float) height - 150, ColorUtils.astolfoColors((int) offset * 10, 1000));
				offset += 20.5;
				drawSmoothRect((float) posX + 25, (float) posY + 100, (float) width - 70, (float) height - 80, new Color(14,14,14,100).getRGB());
				String targetHealth = String.valueOf(getRoundedHealth(target.getHealth()));
				String Test = "\u2764";
				FontUtil.normal2.drawString(target.getName(), posX + 90, (float) (posY + 105), ColorUtils.astolfoColors(offset * 10, 1000));
				FontUtil.litenormal.drawString("HP:§c " + targetHealth, posX + 90, (float) (posY + 120), new Color(255, 0, 0).getRGB());

			//	 mc.fontRendererObj.drawString("\u2764", (int) posX + 90, (int) posY + 120, ColorUtils.astolfoColors((int)offset * 10, 1000));
				  final float width = (float) FontUtil.normal2.getStringWidth(target.toString());
                  final float progress = Math.min((target).getHealth(), (target).getMaxHealth()) / (target).getMaxHealth();

                  final Color color = new Color(255,255,255);

               //   drawSmoothRect((float) posX + 25, (float) posY + 98, (float) width - 70, (float) height - 150, 0x40000000);
                //  drawSmoothRect((float) posX + 25, (float) posY + 98, (-width / 2.0F - 5.0F + (width / 2.0F + 5.0F - -width / 2.0F + 5.0F) * progress), 8, new Color(250, 250, 250).getRGB());
				FontUtil.normal.drawString("Distance: " + Math.round(target.getDistanceToEntity(mc.thePlayer)) + " blocks", posX + 90, (float) (posY + 135), new Color(255, 255, 255).getRGB());
				GuiInventory.drawEntityOnScreen((int) posX + 50, (int) (posY + 167), 30, 5.0F, 5.0F, target);

			}

		}
	}

	public double getRoundedHealth(double number) {
		BigDecimal bigDecimal = new BigDecimal(number);
		bigDecimal = bigDecimal.setScale(1, RoundingMode.HALF_EVEN);
		return bigDecimal.doubleValue();

	}

	@Override
	public void onEnable() {
		super.onEnable();
		toggled = true;
		yaw = mc.thePlayer.rotationYaw;
		pitch = mc.thePlayer.rotationYaw;
		target = null;
	}

	@Override
	public void onDisable() {
		super.onDisable();
		toggled = false;
	}

	public static final void drawSmoothRect(float left, float top, float right, float bottom, int color) {
		GL11.glEnable(3042);
		GL11.glEnable(2848);
		Gui.drawRect(left, top, right, bottom, color);
		GL11.glScalef(0.5f, 0.5f, 0.5f);
		Gui.drawRect(left * 2.0f - 1.0f, top * 2.0f, left * 2.0f, bottom * 2.0f - 1.0f, color);
		Gui.drawRect(left * 2.0f, top * 2.0f - 1.0f, right * 2.0f, top * 2.0f, color);
		Gui.drawRect(right * 2.0f, top * 2.0f, right * 2.0f + 1.0f, bottom * 2.0f - 1.0f, color);
		Gui.drawRect(left * 2.0f, bottom * 2.0f - 1.0f, right * 2.0f, bottom * 2.0f, color);
		GL11.glDisable(3042);
		GL11.glScalef(2.0f, 2.0f, 2.0f);
	}

	private int getHealthColorHEX(EntityPlayer e) {
		int health = Math.round(20.0F * (e.getHealth() / e.getMaxHealth()));
		int color = -1;
		if (health >= 20) {
			color = 5030935;
		} else if (health >= 18) {
			color = 9108247;
		} else if (health >= 16) {
			color = 10026904;
		} else if (health >= 14) {
			color = 12844472;
		} else if (health >= 12) {
			color = 16633879;
		} else if (health >= 10) {
			color = 15313687;
		} else if (health >= 8) {
			color = 16285719;
		} else if (health >= 6) {
			color = 16286040;
		} else if (health >= 4) {
			color = 15031100;
		} else if (health >= 2) {
			color = 16711680;
		} else if (health >= 0) {
			color = 16190746;
		}

		return color;
	}

}
