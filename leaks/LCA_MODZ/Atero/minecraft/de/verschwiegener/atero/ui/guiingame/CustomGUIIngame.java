package de.verschwiegener.atero.ui.guiingame;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.events.callables.EventRenderShader;
import de.verschwiegener.atero.Management;
import de.verschwiegener.atero.audio.Stream;
import de.verschwiegener.atero.design.font.Font;
import de.verschwiegener.atero.design.font.Fontrenderer;
import de.verschwiegener.atero.font.FontManager;
import de.verschwiegener.atero.module.Module;
import de.verschwiegener.atero.module.ModuleManager;
import de.verschwiegener.atero.module.modules.combat.Antibots;
import de.verschwiegener.atero.module.modules.combat.Killaura;
import de.verschwiegener.atero.module.modules.combat.Target;
import de.verschwiegener.atero.module.modules.movement.Speed;
import de.verschwiegener.atero.module.modules.movement.TargetStrafe;
import de.verschwiegener.atero.module.modules.render.Design;
import de.verschwiegener.atero.settings.Setting;
import de.verschwiegener.atero.util.Util;
import de.verschwiegener.atero.util.render.RenderUtil;
import de.verschwiegener.atero.util.render.ShaderRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;

public class CustomGUIIngame {
    public static ShaderRenderer shader = new ShaderRenderer("tabGuiBlur.frag");
    private static Minecraft mc = Minecraft.getMinecraft();

    // TODO: mit event machen ohne static!
    public static int mouseX;
    public static int mouseY;
    public static int lastMouseX;
    public static int lastMouseY;
    public static boolean dragging;
    public static Setting setting;
    public static int posX = 2;
    public static int posY = 23;
    public static double width = 75;
    public static double height = 75;
	private static double Alpha = 0;
    public static void drawArrayList() {
	Fontrenderer fontRenderer = Management.instance.fontmgr.getFontByName("ArrayListFont").getFontrenderer();
	if (Management.instance.modulechange) {
	    Management.instance.modulechange = false;
	   // Util.sortModuleList(Management.instance.modulemgr.modules,
		//    Management.instance.fontmgr.getFontByName("ArrayListFont"));
		final List<Module> moduleList = ModuleManager.modules;
		int moduleIndex = 0;
		List<Module> activeList = new ArrayList<>();
		for (Module module : moduleList) {
			if (module.isEnabled())
				activeList.add(module);
		}
		Collections.sort(ModuleManager.getModules(), new Comparator<Module>() {
			public int compare(Module mod1, Module mod2) {
				if (FontManager.VistolSans_Light.getStringWidth(mod1.getExtraTag()) > FontManager.VistolSans_Light.getStringWidth(mod2.getExtraTag()))
					return -1;
				if (FontManager.VistolSans_Light.getStringWidth(mod1.getExtraTag()) < FontManager.VistolSans_Light.getStringWidth(mod2.getExtraTag()))
					return 1;
				return 0;
			}

		});
	    System.out.println("HEY");
	}
	int yoffset = 0;
	int xoffset = 3;
	int rainbow = 0;
	int index = 0;
	ModuleManager mm = Management.instance.modulemgr;
	ScaledResolution sr = new ScaledResolution(mc);
	for (int i = 0; i < Management.instance.modulemgr.modules.size(); i++) {
	    if (Management.instance.modulemgr.modules.get(i).isEnabled()) {

			Alpha = Management.instance.settingsmgr.getSettingByName("Design").getItemByName("Alpha").getCurrentValue();
		RenderUtil.fillRect(
			((sr.getScaledWidth()) - FontManager.VistolSans_Light.getStringWidth(mm.modules.get(i).getExtraTag()))
				- xoffset - 2,
			yoffset, xoffset + FontManager.VistolSans_Light.getStringWidth(mm.modules.get(i).getExtraTag()),
			FontManager.VistolSans_Light.getFontHeight() + 4, new Color(0,0,0, (int) Alpha));
		// FontManager.ROBOTOTHIN_20.drawString(mm.modules.get(i).getName(),
		// ((sr.getScaledWidth()) -
		// FontManager.ROBOTOTHIN_20.getStringWidth(mm.modules.get(i).getName())) -
		// xoffset +1, yoffset + 2, rainbow2( 500 ,rainbow *2 ), true);
		//FontManager.VistolSans_Light.drawString(mm.modules.get(i).getExtraTag(),
		//	((sr.getScaledWidth()) - FontManager.VistolSans_Light.getStringWidth(mm.modules.get(i).getExtraTag())) - xoffset - 1, yoffset + 2, getGradientOffset(new Color(31, 191, 186),new Color(221, 7, 232), index / 12.4).getRGB(), true);
			FontManager.VistolSans_Light.drawString(mm.modules.get(i).getExtraTag(),
					((sr.getScaledWidth()) - FontManager.VistolSans_Light.getStringWidth(mm.modules.get(i).getExtraTag())) - xoffset - 1, yoffset + 2, getGradientOffset(new Color(Management.instance.colorBlue.getRGB()),new Color(Management.instance.colorBlue2.getRGB()), index / 12.4).getRGB(), true);
		index++;
		//RenderUtil.fillRect(sr.getScaledWidth() - 2, yoffset, 2, FontManager.ROBOTOTHIN_20.getFontHeight() + 3,
		//		getGradientOffset(new Color(31, 191, 186),new Color(221, 7, 232), index / 12.4));
			RenderUtil.fillRect(sr.getScaledWidth() - 2, yoffset, 2, FontManager.ROBOTOTHIN_20.getFontHeight() + 3,
					getGradientOffset(new Color(Management.instance.colorBlue.getRGB()),new Color(Management.instance.colorBlue2.getRGB()), index / 12.4));
		// RenderUtil.fillRect(((sr.getScaledWidth()) -
		// FontManager.ROBOTOTHIN_20.getStringWidth(mm.modules.get(i).getName())) -
		// xoffset - 3, yoffset, xoffset +
		// FontManager.ROBOTOTHIN_20.getStringWidth(mm.modules.get(i).getName()),
		// FontManager.ROBOTOTHIN_20.getFontHeight() + 4, new Color(0, 0, 0, 50));
		// fontRenderer.drawString(mm.modules.get(i).getName(),
		// ((sr.getScaledWidth() * 2) -
		// fontRenderer.getStringWidth(mm.modules.get(i).getName()))
		// - xoffset,
		// yoffset,
		// Management.instance.settingsmgr.getSettingByName("ClickGui").getItemByName("TEST").getColor().getRGB());
		yoffset += FontManager.VistolSans_Light.getFontHeight() + 4;

	    }
	}
    }

    public static void renderRadar(int mouseX, int mouseY) {
		int index = 0;
	if (dragging) {
	    CustomGUIIngame.mouseX = mouseX - lastMouseX;
	    CustomGUIIngame.mouseY = mouseY - lastMouseY;
	}

	int posX = CustomGUIIngame.posX + CustomGUIIngame.mouseX;
	int posY = CustomGUIIngame.posY + CustomGUIIngame.mouseY;

	// RenderUtil.drawRect(posX, posY, posX + width + 2, posY + height, new
	// Color(100,100,255,160).getRGB());
	// drawBorderedRect(posX + .5, posY + .5, posX + width + 1.5, posY + height -
	// .5, 0.5, new Color(100,100,255,160).getRGB(), new
	// Color(100,100,255,160).getRGB(), true);
	drawBorderedRect(posX, posY + 2, posX + width, posY + height - 2, 0.3, new Color(22, 22, 22, 255).getRGB(),
			getGradientOffset(new Color(Management.instance.colorBlue.getRGB()),new Color(Management.instance.colorBlue2.getRGB()), index / 12.4).getRGB(), true);
	index++;
	// RenderUtil.drawRect(posX + 2.5, posY + 2.5, posX + width - .5, posY + 4.5,
	// new Color(100,100,255,160).getRGB());

	// for (int i = posX + 4; i < width + posX - 2; i++) {
	// Color rainbow = new Color(rainbow(3, 1f, 1f, 10L * i));
	// RenderUtil.drawRect(-1 + i, posY + 3, i + 2, posY + 4, rainbow.getRGB());
	// }

	double halfWidth = width / 2 + 0.5;
	double halfHeight = height / 2 - 0.5;

	RenderUtil.drawRect(posX + halfWidth, posY + halfHeight, posX + halfWidth + 1, posY + halfHeight + 1,
		new Color(205, 205, 205, 255).getRGB());
		//FontManager.VistolSans_Light.drawString("BPS " +  Math.round(getSpeed() * 24 * mc.timer.timerSpeed), 2, 15, Management.instance.colorBlue.getRGB(),true);
	for (EntityPlayer player : mc.theWorld.playerEntities ) {
	    if (player != mc.thePlayer) {
		double playerX = player.posX;
		double playerZ = player.posZ;

		double diffX = playerX - mc.thePlayer.posX;
		double diffZ = playerZ - mc.thePlayer.posZ;

		if (MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ) < 50) {
		    double clampedX = MathHelper.clamp_double(diffX, -halfWidth + 3, halfWidth - 3);
		    double clampedY = MathHelper.clamp_double(diffZ, -halfHeight + 5, halfHeight - 3);

		    RenderUtil.drawRect(posX + halfWidth + clampedX, posY + halfHeight + clampedY,
			    posX + halfWidth + clampedX + 1, posY + halfHeight + clampedY + 1,
			    Management.instance.colorBlue.getRGB());
		}
	    }
	}
    }

    @EventTarget
    public static void renderShader(EventRenderShader event) {
	if (mc.currentScreen == null) {
	    // Render Tabgui Blur shader
	    try {
		// ScaledResolution sr = new ScaledResolution(mc);
		// shader.prepareRender();
		// ARBShaderObjects.glUniform2fARB(shader.getUniformLocation("u_size"), 200.0F,
		// 200.0F);
		// shader.renderShader(sr);
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
    }

    public static void drawMusikTab() {
	Stream stream = Management.instance.currentStream;
	if (stream != null) {
	    ScaledResolution sr = new ScaledResolution(mc);
	    final int scaleFactor = sr.getScaleFactor();
	    Font fontBold = Management.instance.fontBold;
	    drawImage(sr.getScaledWidth() - 54, sr.getScaledHeight() - 54, 50, 50, stream);
	    System.out.println("Width: " + fontBold.getStringWidth2(stream.getFulltitle()) * 2);
	    fontBold.drawString(stream.getFulltitle(),
		    (sr.getScaledWidth() - 54) - (fontBold.getStringWidth2(stream.getFulltitle()) * 2) - 5,
		    (sr.getScaledHeight() - 53), Color.BLACK.getRGB());
	    fontBold.drawString(stream.getArtist(),
		    (sr.getScaledWidth() - 54) - (fontBold.getStringWidth2(stream.getArtist()) * 2) - 5,
		    (sr.getScaledHeight() - 53) + (fontBold.getBaseStringHeight()), Color.BLACK.getRGB());
	}
    }

    private static void drawImage(final int xPos, final int yPos, final int width, final int height, Stream stream) {
	mc.getTextureManager().deleteTexture(stream.getLocation());
	stream.setTexture(new DynamicTexture(stream.getImage()));
	stream.setLocation(
		mc.getTextureManager().getDynamicTextureLocation(stream.getChannelName(), stream.getTexture()));
	stream.getTexture().updateDynamicTexture();
	mc.getTextureManager().bindTexture(stream.getLocation());
	GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	Gui.drawModalRectWithCustomSizedTexture(xPos, yPos, 0.0F, 0.0F, width, height, width, height);
    }

    public static void renderTargetHud(ScaledResolution scaledResolution) {

	// Font font = Management.instance.font;
	// EntityLivingBase target = Killaura.instance.getTarget();

	// if (target != null && target instanceof EntityPlayer || target instanceof
	// EntityAnimal || target instanceof EntityVillager || target instanceof
	// EntityMob &&
	// Management.instance.modulemgr.getModuleByName("Killaura").isEnabled()) {
	// GlStateManager.pushMatrix();
	// GlStateManager.translate(scaledResolution.getScaledWidth() / 2F,
	// scaledResolution.getScaledHeight() / 1.8F, 0);
	// RenderUtil.drawRectRound(0, 0, 150, 60, 5, Management.instance.colorGray);
	// RenderUtil.fillRect(0, 0, 150, 60, Management.instance.colorGray);
	// RenderUtil.fillRect(0, 59, 150, 1, Management.instance.colorBlue);

	// font.drawString(target.getName(), 20, 3F,
	// Management.instance.colorBlue.getRGB());

	// renderPlayer(25, 55, 23, target);

	// float healthProcent = target.getHealth() / target.getMaxHealth();
	// RenderUtil.drawRect(55, 15, 55 + (90 * healthProcent), 25,
	// Color.HSBtoRGB(Math.min(-healthProcent + 0.3F, 0), 1, 1));
	// font.drawString(String.valueOf(Math.round(target.getHealth())), 100, 6F,
	// Color.HSBtoRGB(Math.min(-healthProcent + 0.3F, 0), 1, 1));

	// double winChance = 0;

	// double TargetStrength = getWeaponStrength(target.getHeldItem());
	// winChance = getWeaponStrength(mc.thePlayer.getHeldItem()) - TargetStrength;
	// winChance += getProtection(mc.thePlayer) - getProtection(target);
	// winChance += mc.thePlayer.getHealth() - (target).getHealth();
//
	// String message = winChance == 0 ? "You could win"
	// : winChance < 0 ? "You could lose" : "You are going to win";
	// font.drawString(message, 50.5F - font.getStringWidth(message) +
	// font.getStringWidth(message) / 1F, 30F,
	// Util.getColor(255, 240, 0, 255));
	// GlStateManager.popMatrix();
	// }
    }

    private static double getProtection(EntityLivingBase target) {
	double protection = 0;

	for (int i = 0; i <= 3; i++) {
	    ItemStack stack = target.getCurrentArmor(i);

	    if (stack != null) {
		if (stack.getItem() instanceof ItemArmor) {
		    ItemArmor armor = (ItemArmor) stack.getItem();
		    protection += armor.damageReduceAmount;
		}

		protection += EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack) * 0.25;
	    }
	}

	return protection;
    }

    // schon mal an nen event dafï¿½r gedacht - wo renderst du das was rendern?
    // die aary?

    private static double getWeaponStrength(ItemStack stack) {
	double damage = 0;

	if (stack != null) {
	    if (stack.getItem() instanceof ItemSword) {
		ItemSword sword = (ItemSword) stack.getItem();
		damage += sword.getDamageVsEntity();
	    }

	    if (stack.getItem() instanceof ItemTool) {
		ItemTool tool = (ItemTool) stack.getItem();
		damage += tool.getToolMaterial().getDamageVsEntity();
	    }

	    damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack) * 1.25;
	}

	return damage;
    }

    private static void renderPlayer(int posX, int posY, int scale, EntityLivingBase player) {
	GlStateManager.enableColorMaterial();
	GlStateManager.pushMatrix();
	GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	GlStateManager.translate((float) posX, (float) posY, 50.0F);
	GlStateManager.scale((float) (-scale), (float) scale, (float) scale);
	GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
	RenderHelper.enableStandardItemLighting();
	player.rotationYawHead = player.rotationYaw;
	player.prevRotationYawHead = player.rotationYaw;
	GlStateManager.translate(0.0F, 0.0F, 0.0F);
	RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
	rendermanager.setPlayerViewY(180.0F);
	rendermanager.setRenderShadow(false);
	rendermanager.renderEntityWithPosYaw(player, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
	rendermanager.setRenderShadow(true);
	player.renderYawOffset = player.rotationYaw;
	player.prevRotationYawHead = player.rotationYaw;
	player.rotationYawHead = player.rotationYaw;
	GlStateManager.popMatrix();
	RenderHelper.disableStandardItemLighting();
	GlStateManager.disableRescaleNormal();
	GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
	GlStateManager.disableTexture2D();
	GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    public static void drawWatermark(ScaledResolution sr) {
	final Fontrenderer fontRenderer = Management.instance.fontmgr.getFontByName("WaterMarkFont").getFontrenderer();
	int xP = 15;
	int yP = 5;
	int widthP = (int) ((int) fontRenderer.getStringWidth("atero"));
	int heightP = (int) fontRenderer.getStringHeight("atero") / 2;
	LocalDateTime now = LocalDateTime.now();

	double seconds = now.getSecond();
	double minutes = now.getMinute();
	double hours = now.getHour();

	int width = sr.getScaledWidth() / 10;
	int height = sr.getScaledHeight() / 45;

	int clockW = 30;
	int clockH = 30;

	if (mc.gameSettings.showDebugInfo)
	    return;
	GlStateManager.pushMatrix();
	GlStateManager.disableBlend();
	final String server = mc.isSingleplayer() ? "local" : mc.getCurrentServerData().serverIP.toLowerCase();
	// mc.thePlayer.getUniqueID();
	//final String text = "Atero";
	// final String text = "Atero.cc | " + server + " | " + Minecraft.getDebugFPS()
	// + " fps | " + "0" + " ms" + " | " + "Build " +
	// Management.instance.CLIENT_VERSION;

	//final float width1 = FontManager.ROBOTOTHIN_20.getStringWidth(text) + 8;
	final int height1 = 18;
	final int posX = 2;
	final int posY = 2;

	// RenderUtil.drawRect(posX, posY, posX + width1 + 2, posY + height1,
	// Management.instance.colorBlue.getRGB());
	//drawBorderedRect(posX + .5, posY + .5, posX + width1 + 1.5, posY + height1 - .5, 0.5,
	//	new Color(40, 40, 40, 255).getRGB(), new Color(255, 255, 255, 30).getRGB(), true);
	//drawBorderedRect(posX + 2, posY + 2, posX + width1, posY + height1 - 2, 0.5,
		//new Color(22, 22, 22, 255).getRGB(), new Color(22, 22, 22, 255).getRGB(), true);
	// RenderUtil.drawRect(posX + 2.5, posY + 2.5, posX + width1 - .5, posY + 4.5,
	// new Color(9, 9, 9, 255).getRGB());

	// for (int i = 6; i < width1; i++) {
	// Color rainbow = new Color(rainbow(3, 1f, 1f, 10L * i));
	// RenderUtil.drawRect(-1 + i, posY + 3, i + 2, posY + 4, rainbow.getRGB());
	// }

//	FontManager.ROBOTOTHIN_20.drawString(text, 4 + posX, posY + FontManager.ROBOTOTHIN_20.getFontHeight() - 9, -1,
	//	true);

	GlStateManager.enableBlend();
	GlStateManager.popMatrix();

	// fontRenderer.drawString("Atero", xP, yP,
	// Management.instance.colorBlue.getRGB());
	// mc.fontRendererObj.drawStringWithShadow("[" + now.getHour() + ":" +
	// insertNulls(now.getMinute(), 2) + ":" + insertNulls(now.getSecond(), 2) +
	// "]",
	// width, height, Management.instance.colorBlue.getRGB());
	// drawImage((int) (xP -10), (int) (yP + fontRenderer.getStringHeight("") -10),
	// widthP - 20, heightP + 40,
	// new ResourceLocation("atero/assets/arrow.png"));
    }

    private static void drawImage(int x, int y, int width, int height, ResourceLocation resourceLocation) {
	mc.getTextureManager().bindTexture(resourceLocation);
	GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, width, height);
	GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static String insertNulls(int val, int min_digits) {
	String s = String.valueOf(val);
	while (s.length() < min_digits)
	    s = "0" + s;
	return s;
    }

    public static void drawBorderedRect(double left, double top, double right, double bottom, double borderWidth,
	    int insideColor, int borderColor, boolean borderIncludedInBounds) {
	Gui.drawRect(left - (!borderIncludedInBounds ? borderWidth : 0),
		top - (!borderIncludedInBounds ? borderWidth : 0), right + (!borderIncludedInBounds ? borderWidth : 0),
		bottom + (!borderIncludedInBounds ? borderWidth : 0), borderColor);
	Gui.drawRect(left + (borderIncludedInBounds ? borderWidth : 0),
		top + (borderIncludedInBounds ? borderWidth : 0), right - ((borderIncludedInBounds ? borderWidth : 0)),
		bottom - ((borderIncludedInBounds ? borderWidth : 0)), insideColor);
    }

    public static int rainbow(float seconds, float saturation, float brightness, long index) {
	float hue = ((System.currentTimeMillis() + index) % (int) (seconds * 1000)) / (seconds * 1000);
	return Color.HSBtoRGB(hue, saturation, brightness);
    }

    public static int rainbow2(long delay, double speed) {
	double rainBowstate = Math.ceil((System.currentTimeMillis() + delay) / speed);
	rainBowstate %= 360;
	return Color.getHSBColor((float) rainBowstate / 360f, 0.8f, 0.7f).getRGB();
    }

    public static void renderTargetHUD2() {
	Entity target = Killaura.instance.getTarget();

	if (target instanceof EntityPlayer) {
	    ScaledResolution s = new ScaledResolution(mc);
	    Color backgroundColor = new Color(0, 0, 0, 120);
	    Color emptyBarColor = new Color(59, 59, 59, 160);
	    Color healthBarColor = new Color(92, 228, 128);
	    Color distBarColor = new Color(20, 81, 208);
	    int left = s.getScaledWidth() / 2 + 5;
	    int right2 = 120;
	    int right = s.getScaledWidth() / 2 + right2;
	    int right3 = 120 + FontManager.ROBOTOTHIN_20.getStringWidth(target.getName()) / 2 - 15;
	    int top = s.getScaledHeight() / 2 - 25;
	    int bottom = s.getScaledHeight() / 2 + 25;
	    float curTargetHealth = ((EntityPlayer) target).getHealth();

	    float maxTargetHealth = ((EntityPlayer) target).getMaxHealth();
	    float calculatedHealth = curTargetHealth / maxTargetHealth;
	    int rectRight = right + FontManager.ROBOTOTHIN_20.getStringWidth(target.getName()) / 2 - 5;
	    float healthPos = calculatedHealth * right3;
	    // WHITEMODE
	    if (Management.instance.modulemgr.getModuleByName("Design").isEnabled()) {
		Util.drawRect(left, top, rectRight, bottom, new Color(0, 0, 0, 120));
	    } else {
		Util.drawRect(left, top, rectRight, bottom, Management.instance.colorGray);
	    }

	    // Util.drawRect(left, top, rectRight, bottom, new Color(255,255,255,255));

	    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	    List NetworkMoment = GuiPlayerTabOverlay.field_175252_a
		    .sortedCopy(mc.thePlayer.sendQueue.getPlayerInfoMap());
	    Iterator itarlor = NetworkMoment.iterator();
	    while (itarlor.hasNext()) {
		Object nextObject = itarlor.next();
		NetworkPlayerInfo networkPlayerInfo = (NetworkPlayerInfo) nextObject;
		if (mc.theWorld.getPlayerEntityByUUID(networkPlayerInfo.getGameProfile().getId()) == target) {
		    GlStateManager.enableCull();
		    mc.getTextureManager().bindTexture(networkPlayerInfo.getLocationSkin());
		    Gui.drawScaledCustomSizeModalRect(s.getScaledWidth() / 2 + 10, s.getScaledHeight() / 2 - 22, 8.0F,
			    8.0F, 8, 8, 28, 28, 64.0F, 66.0F);

		}
	    }

	    // Util.drawRect(left + 5, bottom - 14, left + right3, bottom - 12, Color.CYAN);
	    Util.drawRect(left + 5, bottom - 14, left + healthPos, bottom - 12, healthBarColor);
	    Util.drawRect(left + 5, bottom - 8, left + right3, bottom - 6, Color.BLUE);

	    renderPlayer(260, 260, 0, Killaura.instance.getTarget());
	    // WhiteMODE
	    if (Management.instance.modulemgr.getModuleByName("Design").isEnabled()) {
		FontManager.ROBOTOTHIN_20.drawString(target.getName(), left + 50, top, Color.WHITE.getRGB(), false);
		FontManager.ROBOTOTHIN_20.drawString("Health: " + Math.round(curTargetHealth), left + 50, top + 15,
			Color.WHITE.getRGB(), false);
	    } else {
		FontManager.ROBOTOTHIN_20.drawString(target.getName(), left + 50, top, Color.WHITE.getRGB(), false);
		FontManager.ROBOTOTHIN_20.drawString("Health: " + Math.round(curTargetHealth), left + 50, top + 15,
			Color.WHITE.getRGB(), false);
	    }
	}
    }
	public static Color getGradientOffset(Color color1, Color color2, double index) {
		double offs = (Math.abs(((System.currentTimeMillis()) / 13)) / 60D) + index;
		if(offs >1)

		{
			double left = offs % 1;
			int off = (int) offs;
			offs = off % 2 == 0 ? left : 1 - left;
		}

		double inverse_percent = 1 - offs;
		int redPart = (int) (color1.getRed() * inverse_percent + color2.getRed() * offs);
		int greenPart = (int) (color1.getGreen() * inverse_percent + color2.getGreen() * offs);
		int bluePart = (int) (color1.getBlue() * inverse_percent + color2.getBlue() * offs);
		return new Color(redPart, greenPart, bluePart);
	}
	public static float getSpeed() {
		return (float) Math.sqrt(Minecraft.getMinecraft().thePlayer.motionX * Minecraft.getMinecraft().thePlayer.motionX + Minecraft.getMinecraft().thePlayer.motionZ * Minecraft.getMinecraft().thePlayer.motionZ);
	}
}