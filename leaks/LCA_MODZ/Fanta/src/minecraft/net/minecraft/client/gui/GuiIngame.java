package net.minecraft.client.gui;

import java.awt.Color;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import org.lwjgl.opengl.GL11;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import de.fanta.Client;
import de.fanta.events.EventType;
import de.fanta.events.listeners.EventRender2D;
import de.fanta.gui.font.BasicFontRenderer;
import de.fanta.module.impl.visual.Themes;
import de.fanta.module.impl.world.Scaffold;
import de.fanta.notifications.NotificationManager;
import de.fanta.setting.settings.DropdownBox;
import de.fanta.utils.RenderUtil;
import de.fanta.utils.TimeUtil;
import fr.lavache.anime.Animate;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.FoodStats;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.world.border.WorldBorder;
import optifine.Config;
import optifine.CustomColors;

public class GuiIngame extends Gui {
	public static int count;
	Animate anim3 = new Animate();
	Animate anim4 = new Animate();
	Animate anim5 = new Animate();
	Animate anim6 = new Animate();
	Animate anim7 = new Animate();
	Animate anim8 = new Animate();
	Animate anim9 = new Animate();
	Animate anim10 = new Animate();
    public static int mouseX;
    public static int mouseY;
    public static int lastMouseX;
    public static int lastMouseY;
    public static boolean dragging;
    //public static Setting setting;
    public static int posX = 2;
    public static int posY = 23;
    public static double width = 75;
    public static double height = 75;
	private static double Alpha = 0;
	private static final ResourceLocation vignetteTexPath = new ResourceLocation("textures/misc/vignette.png");
	private static final ResourceLocation widgetsTexPath = new ResourceLocation("textures/gui/widgets.png");
	private static final ResourceLocation pumpkinBlurTexPath = new ResourceLocation("textures/misc/pumpkinblur.png");
	private static final String __OBFID = "CL_00000661";
	private final Random rand = new Random();
	private final Minecraft mc;
	private final RenderItem itemRenderer;
	private String title;
	private String messsage;
	private long start;

	private long fadedIn;
	private long fadeOut;
	private long end;
	/**
	 * ChatGUI instance that retains all previous chat data
	 */
	private final GuiNewChat persistantChatGUI;
	private final GuiStreamIndicator streamIndicator;
	private final GuiOverlayDebug overlayDebug;
	/**
	 * The spectator GUI for this in-game GUI instance
	 */
	private final GuiSpectator spectatorGui;
	private final GuiPlayerTabOverlay overlayPlayerList;
	/**
	 * Previous frame vignette brightness (slowly changes by 1% each frame)
	 */
	public float prevVignetteBrightness = 1.0F;
	private int updateCounter;
	/**
	 * The string specifying which record music is playing
	 */
	private String recordPlaying = "";
	/**
	 * How many ticks the record playing message will be displayed
	 */
	private int recordPlayingUpFor;
	private boolean recordIsPlaying;
	/**
	 * Remaining ticks the item highlight should be visible
	 */
	private int remainingHighlightTicks;
	/**
	 * The ItemStack that is currently being highlighted
	 */
	private ItemStack highlightingItemStack;
	private int titlesTimer;
	private String field_175201_x = "";
	private String field_175200_y = "";
	private int titleFadeIn;
	private int titleDisplayTime;
	private int titleFadeOut;
	private int playerHealth = 0;
	private int lastPlayerHealth = 0;
	/**
	 * The last recorded system time
	 */
	TimeUtil time = new TimeUtil();
	private long startTime;
	private long lastSystemTime = 0L;
	/**
	 * Used with updateCounter to make the heart bar flash
	 */
	private long healthUpdateCounter = 0L;

	public GuiIngame(Minecraft mcIn) {
		this.mc = mcIn;
		this.itemRenderer = mcIn.getRenderItem();
		this.overlayDebug = new GuiOverlayDebug(mcIn);
		this.spectatorGui = new GuiSpectator(mcIn);
		this.persistantChatGUI = new GuiNewChat(mcIn);
		this.streamIndicator = new GuiStreamIndicator(mcIn);
		this.overlayPlayerList = new GuiPlayerTabOverlay(mcIn, this);
		this.func_175177_a();
	}

	public void func_175177_a() {
		this.titleFadeIn = 10;
		this.titleDisplayTime = 70;
		this.titleFadeOut = 20;
	}

	public void renderGameOverlay(float partialTicks) {
		ScaledResolution scaledresolution = new ScaledResolution(this.mc);
		int i = scaledresolution.getScaledWidth();
		int j = scaledresolution.getScaledHeight();
		this.mc.entityRenderer.setupOverlayRendering();
		GlStateManager.enableBlend();

		EventRender2D eventRender2D = new EventRender2D();
		eventRender2D.setType(EventType.PRE);
		Client.INSTANCE.onEvent(eventRender2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		int index = 0;
		// Gui.drawRGBLineHorizontal(ScaledResolution.INSTANCE.getScaledWidth() / 2 +
		// 390,
		// ScaledResolution.INSTANCE.getScaledHeight() / 2 + 253,
		// Client.INSTANCE.unicodeBasicFontRenderer5
		// .getStringWidth("Fanta Beta - Build " + Client.INSTANCE.getVersion()),
		// 1, 2, true);
		// Client.INSTANCE.unicodeBasicFontRenderer5.drawStringWithShadow(
		// "Fanta Beta - Build " + Client.INSTANCE.getVersion(),
		// ScaledResolution.INSTANCE.getScaledWidth() / 2 + 390,
		// ScaledResolution.INSTANCE.getScaledHeight() / 2 + 243, -1);
		Client.blurHelper.blur2(0, 0, 0, 15, 25);
		// final Fontrenderer fontRenderer =
		// Management.instance.fontmgr.getFontByName("WaterMarkFont").getFontrenderer();
		int xP = 15;
		int yP = 5;
		int widthP = (int) ((int) Client.INSTANCE.fluxTabGuiFont.getStringWidth("atero"));
		int heightP = (int) Client.INSTANCE.fluxTabGuiFont.getHeight("atero") / 2;
		LocalDateTime now = LocalDateTime.now();

		double seconds = now.getSecond();
		double minutes = now.getMinute();
		double hours = now.getHour();

		int width = scaledresolution.getScaledWidth() / 10;
		int height = scaledresolution.getScaledHeight() / 45;

		int clockW = 30;
		int clockH = 30;

		// Client.INSTANCE.fluxTabGuiFont.drawString("Atero", xP, yP,
		// Color.white.getRGB());
		if (!Client.INSTANCE.moduleManager.getModule("Tabgui").isState()) {
		

			Client.INSTANCE.fluxTabGuiFont.drawStringWithShadow("" + now.getHour() + ":"
					+ insertNulls(now.getMinute(), 2) + ":" + insertNulls(now.getSecond(), 2) + "", width / 2,
					height - 4.5F, -1);

			Gui.drawRect(13, 5, width - 13, height + 10, new Color(0, 0, 0, 140).getRGB());
		}

		if (Client.INSTANCE.moduleManager.getModule("Tabgui").isState()) {
			if (((DropdownBox) Client.INSTANCE.moduleManager.getModule("Tabgui").getSetting("Mode")
					.getSetting()).curOption != "Violence") {
				// Client.INSTANCE.heroTabGui.drawStringWithShadow("Hero", 1.5F, -3.5F, new
				// Color(100,255,90,255).getRGB());

			}
			if (((DropdownBox) Client.INSTANCE.moduleManager.getModule("Tabgui").getSetting("Mode")
					.getSetting()).curOption == "Violence") {
//				mc.fontRendererObj.drawStringWithShadow("violence (B1.32)", 5, 7,
//						-1);	
				Client.INSTANCE.Violence2.drawStringWithShadow("V", 3, 1, new Color(60, 50, 255).getRGB());
				Client.INSTANCE.Violence.drawString("IOLENCE", 11, 6, -1);
			}
		}
		// Client.blurHelper.blur2((float) (GuiScreen.width - offset), GuiScreen.height
		// - 5 - height, GuiScreen.width, GuiScreen.height - 5, 100);
		if (!Client.INSTANCE.moduleManager.getModule("Tabgui").isState()) {
			switch (((DropdownBox) Themes.INSTANCE.getSetting("ArrayColor").getSetting()).curOption) {
			case "RED":
				Client.INSTANCE.unicodeBasicFontRenderer.drawString("F", 17, 7,
						Themes.getGradientOffset(new Color(255, 0, 0), new Color(64, 0, 0), index / 12.4).getRGB());
				break;
			case "BLUE":
				Client.INSTANCE.unicodeBasicFontRenderer.drawString("F", 17, 7,
						Themes.getGradientOffset(new Color(0, 37, 255), new Color(0, 16, 110), index / 12.4).getRGB());
				break;
			case "GREEN":
				Client.INSTANCE.unicodeBasicFontRenderer.drawString("F", 17, 7,
						Themes.getGradientOffset(new Color(0, 255, 4), new Color(0, 54, 0), index / 12.4).getRGB());
				break;
			case "YELLOW":
				Client.INSTANCE.unicodeBasicFontRenderer.drawString("F", 17, 7,
						Themes.getGradientOffset(new Color(230, 255, 0), new Color(98, 122, 1), index / 12.4).getRGB());
				break;
			case "Novoline":
				Client.INSTANCE.unicodeBasicFontRenderer.drawString("F", 17, 7,
						Themes.getGradientOffset(new Color(3, 81, 74, 255), new Color(5, 244, 222, 255), index / 4.4)
								.getRGB());
				break;
			}

			Client.INSTANCE.unicodeBasicFontRenderer.drawString("anta", 23, 7, -1);
		}
		// Client.INSTANCE.unicodeBasicFontRenderer.drawString("____", 6.5F, 7, -1);
		// Client.INSTANCE.unicodeBasicFontRenderer.drawString("anta", 13, 12, -1);
		// Client.INSTANCE.arial2.drawString("F", 0, 0, Arraylist
		// .getGradientOffset(new Color(Color.ORANGE.getRGB()), new
		// Color(Color.BLUE.getRGB()), index / 12.4)
		// .getRGB());

		// drawImage(scaledresolution.getScaledWidth() / 25 - 67,
		// scaledresolution.getScaledHeight() / 25 - 60, 170, 170, new
		// ResourceLocation("textures/Fanta.png"));
		GL11.glDisable(GL11.GL_BLEND);
		NotificationManager.render();

		if (Client.INSTANCE.moduleManager.getModule("Flight").isState()) {
//			anim3.setEase(Easing.LINEAR).setMin(0).setMax(scaledresolution.getScaledWidth() / 2f + 10).setSpeed(200)
//					.update();
//			anim4.setEase(Easing.LINEAR).setMin(0).setMax(scaledresolution.getScaledWidth() / 2f + 11).setSpeed(200)
//					.update();
//			anim5.setEase(Easing.LINEAR).setMin(0).setMax(scaledresolution.getScaledWidth() / 2f + 12).setSpeed(200)
//					.update();
//			anim6.setEase(Easing.LINEAR).setMin(0).setMax(scaledresolution.getScaledWidth() / 2f + 13).setSpeed(200)
//					.update();
//			anim7.setEase(Easing.LINEAR).setMin(0).setMax(scaledresolution.getScaledWidth() / 2f + 14).setSpeed(200)
//					.update();
//			anim8.setEase(Easing.LINEAR).setMin(0).setMax(scaledresolution.getScaledWidth() / 2f + 15).setSpeed(200)
//					.update();
//			anim9.setEase(Easing.LINEAR).setMin(0).setMax(scaledresolution.getScaledWidth() / 2f + 16).setSpeed(200)
//					.update();
//			anim10.setEase(Easing.LINEAR).setMin(0).setMax(scaledresolution.getScaledWidth() / 2f + 17).setSpeed(200)
//					.update();

//			if (!Flight.Start) {
//				drawRect2(scaledresolution.getScaledWidth() / 2F + 8, scaledresolution.getScaledHeight() / 2f - 7,
//						scaledresolution.getScaledWidth() / 2f + 82, scaledresolution.getScaledHeight() / 2f + 17,
//						Color.black.getRGB());
//				if (time.hasReached((long) 600)) {
//					time.reset();
//				}
//				
//				drawRect2(scaledresolution.getScaledWidth() / 2F + 10 * time.getCurrentMS(), scaledresolution.getScaledHeight() / 2f - 5,
//						scaledresolution.getScaledWidth() / 2f + 30, scaledresolution.getScaledHeight() / 2f + 15,
//						Color.green.getRGB());
//
//				
//				
//				if (count > 0) {
//			
//					drawRect2(scaledresolution.getScaledWidth() / 2F + 10, scaledresolution.getScaledHeight() / 2f - 5,
//							scaledresolution.getScaledWidth() / 2f + 20, scaledresolution.getScaledHeight() / 2f + 15,
//							Color.green.getRGB());
//
//				}
//
//				if (count > 1) {
//					
//					drawRect2(scaledresolution.getScaledWidth() / 2F + 20, scaledresolution.getScaledHeight() / 2f - 5,
//							scaledresolution.getScaledWidth() / 2f + 30, scaledresolution.getScaledHeight() / 2f + 15,
//							Color.green.getRGB());
//
//				}
//
//				if (count > 2) {
//				
//					drawRect2(scaledresolution.getScaledWidth() / 2F + 30, scaledresolution.getScaledHeight() / 2f - 5,
//							scaledresolution.getScaledWidth() / 2f + 40, scaledresolution.getScaledHeight() / 2f + 15,
//							Color.green.getRGB());
//
//				}
//
//				if (count > 3) {
//
//					drawRect2(scaledresolution.getScaledWidth() / 2F + 40, scaledresolution.getScaledHeight() / 2f - 5,
//							scaledresolution.getScaledWidth() / 2f + 50, scaledresolution.getScaledHeight() / 2f + 15,
//							Color.green.getRGB());
//
//				}
//
//				if (count > 4) {
//
//					drawRect2(scaledresolution.getScaledWidth() / 2F + 50, scaledresolution.getScaledHeight() / 2f - 5,
//							scaledresolution.getScaledWidth() / 2f + 60, scaledresolution.getScaledHeight() / 2f + 15,
//							Color.green.getRGB());
//
//				}
//
//				if (count > 5) {
//
//					drawRect2(scaledresolution.getScaledWidth() / 2F + 60, scaledresolution.getScaledHeight() / 2f - 5,
//							scaledresolution.getScaledWidth() / 2f + 30, scaledresolution.getScaledHeight() / 2f + 15,
//							Color.green.getRGB());
//
//				}
//
//				if (count > 6) {
//
//					drawRect2(scaledresolution.getScaledWidth() / 2F + 70, scaledresolution.getScaledHeight() / 2f - 5,
//							scaledresolution.getScaledWidth() / 2f + 30, scaledresolution.getScaledHeight() / 2f + 15,
//							Color.green.getRGB());
//
//				}
//
//				if (count > 7) {
//
//					drawRect2(scaledresolution.getScaledWidth() / 2F + 80, scaledresolution.getScaledHeight() / 2f - 5,
//							scaledresolution.getScaledWidth() / 2f + 30, scaledresolution.getScaledHeight() / 2f + 15,
//							Color.green.getRGB());
//
//				}
//
//			}
		}
		int top = scaledresolution.getScaledHeight() / 2;
		int bottom = scaledresolution.getScaledWidth() / 2;
		try {
			if (Client.INSTANCE.moduleManager.getModule("Scaffold").isState()) {

//				RenderUtil.drawTriangle(ScaledResolution.INSTANCE.getScaledWidth() / 2 + 10,
//						ScaledResolution.INSTANCE.getScaledHeight() / 2 + 20, 5, 5, new Color(0, 0, 0, 100).getRGB());

				drawRect2(scaledresolution.getScaledWidth() / 2F + 10, scaledresolution.getScaledHeight() / 2f - 5,
						scaledresolution.getScaledWidth() / 2f + 30, scaledresolution.getScaledHeight() / 2f + 15,
						new Color(23, 23, 23, 230).getRGB());

				drawRect2(scaledresolution.getScaledWidth() / 2F + 10, scaledresolution.getScaledHeight() / 2f - 5,
						scaledresolution.getScaledWidth() / 2f + 30, scaledresolution.getScaledHeight() / 2f - 4F,
						Color.green.getRGB());
				index++;
				Client.INSTANCE.unicodeBasicFontRenderer
						.drawString("" + Scaffold.getBlockCount(),
								scaledresolution.getScaledWidth() / 2f - Client.INSTANCE.unicodeBasicFontRenderer
										.getStringWidth("" + Scaffold.getBlockCount()) / 2f + 19.5F,
								top, Color.white.getRGB());

			}
		} catch (NullPointerException e) {

		}

		if (Config.isVignetteEnabled()) {
			this.renderVignette(this.mc.thePlayer.getBrightness(partialTicks), scaledresolution);
		} else {
			GlStateManager.enableDepth();
			GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		}

		ItemStack itemstack = this.mc.thePlayer.inventory.armorItemInSlot(3);

		if (this.mc.gameSettings.thirdPersonView == 0 && itemstack != null
				&& itemstack.getItem() == Item.getItemFromBlock(Blocks.pumpkin)) {
			this.renderPumpkinOverlay(scaledresolution);
		}

		if (!this.mc.thePlayer.isPotionActive(Potion.confusion)) {
			float f = this.mc.thePlayer.prevTimeInPortal
					+ (this.mc.thePlayer.timeInPortal - this.mc.thePlayer.prevTimeInPortal) * partialTicks;

			if (f > 0.0F) {
				this.func_180474_b(f, scaledresolution);
			}
		}

		if (this.mc.playerController.isSpectator()) {
			this.spectatorGui.renderTooltip(scaledresolution, partialTicks);
		} else {
			this.renderTooltip(scaledresolution, partialTicks);
		}

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(icons);
		GlStateManager.enableBlend();

		if (this.showCrosshair() && this.mc.gameSettings.thirdPersonView < 1) {
			GlStateManager.tryBlendFuncSeparate(775, 769, 1, 0);
			GlStateManager.enableAlpha();
			this.drawTexturedModalRect(i / 2 - 7, j / 2 - 7, 0, 0, 16, 16);
		}

		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		this.mc.mcProfiler.startSection("bossHealth");
		this.renderBossHealth();
		this.mc.mcProfiler.endSection();

		if (this.mc.playerController.shouldDrawHUD()) {
			this.renderPlayerStats(scaledresolution);
		}

		GlStateManager.disableBlend();

		if (this.mc.thePlayer.getSleepTimer() > 0) {
			this.mc.mcProfiler.startSection("sleep");
			GlStateManager.disableDepth();
			GlStateManager.disableAlpha();
			int l = this.mc.thePlayer.getSleepTimer();
			float f2 = (float) l / 100.0F;

			if (f2 > 1.0F) {
				f2 = 1.0F - (float) (l - 100) / 10.0F;
			}

			int k = (int) (220.0F * f2) << 24 | 1052704;
			drawRect(0, 0, i, j, k);
			GlStateManager.enableAlpha();
			GlStateManager.enableDepth();
			this.mc.mcProfiler.endSection();
		}

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		int i2 = i / 2 - 91;

		if (this.mc.thePlayer.isRidingHorse()) {
			this.renderHorseJumpBar(scaledresolution, i2);
		} else if (this.mc.playerController.gameIsSurvivalOrAdventure()) {
			this.renderExpBar(scaledresolution, i2);
		}

		if (this.mc.gameSettings.heldItemTooltips && !this.mc.playerController.isSpectator()) {
			this.func_181551_a(scaledresolution);
		} else if (this.mc.thePlayer.isSpectator()) {
			this.spectatorGui.func_175263_a(scaledresolution);
		}

		if (this.mc.isDemo()) {
			this.renderDemo(scaledresolution);
		}

		if (this.mc.gameSettings.showDebugInfo) {
			this.overlayDebug.renderDebugInfo(scaledresolution);
		}

		if (this.recordPlayingUpFor > 0) {
			this.mc.mcProfiler.startSection("overlayMessage");
			float f3 = (float) this.recordPlayingUpFor - partialTicks;
			int k1 = (int) (f3 * 255.0F / 20.0F);

			if (k1 > 255) {
				k1 = 255;
			}

			if (k1 > 8) {
				GlStateManager.pushMatrix();
				GlStateManager.translate((float) (i / 2), (float) (j - 68), 0.0F);
				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
				int i1 = 16777215;

				if (this.recordIsPlaying) {
					i1 = MathHelper.func_181758_c(f3 / 50.0F, 0.7F, 0.6F) & 16777215;
				}

				this.getBasicFontRenderer().drawString(this.recordPlaying,
						-this.getBasicFontRenderer().getStringWidth(this.recordPlaying) / 2, -4,
						i1 + (k1 << 24 & -16777216));
				GlStateManager.disableBlend();
				GlStateManager.popMatrix();
			}

			this.mc.mcProfiler.endSection();
		}

		if (this.titlesTimer > 0) {
			this.mc.mcProfiler.startSection("titleAndSubtitle");
			float f4 = (float) this.titlesTimer - partialTicks;
			int l1 = 255;

			if (this.titlesTimer > this.titleFadeOut + this.titleDisplayTime) {
				float f1 = (float) (this.titleFadeIn + this.titleDisplayTime + this.titleFadeOut) - f4;
				l1 = (int) (f1 * 255.0F / (float) this.titleFadeIn);
			}

			if (this.titlesTimer <= this.titleFadeOut) {
				l1 = (int) (f4 * 255.0F / (float) this.titleFadeOut);
			}

			l1 = MathHelper.clamp_int(l1, 0, 255);

			if (l1 > 8) {
				GlStateManager.pushMatrix();
				GlStateManager.translate((float) (i / 2), (float) (j / 2), 0.0F);
				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
				GlStateManager.pushMatrix();
				GlStateManager.scale(4.0F, 4.0F, 4.0F);
				int j2 = l1 << 24 & -16777216;
				this.getBasicFontRenderer().drawString(this.field_175201_x,
						(float) (-this.getBasicFontRenderer().getStringWidth(this.field_175201_x) / 2), -10.0F,
						16777215 | j2, true);
				GlStateManager.popMatrix();
				GlStateManager.pushMatrix();
				GlStateManager.scale(2.0F, 2.0F, 2.0F);
				this.getBasicFontRenderer().drawString(this.field_175200_y,
						(float) (-this.getBasicFontRenderer().getStringWidth(this.field_175200_y) / 2), 5.0F,
						16777215 | j2, true);
				GlStateManager.popMatrix();
				GlStateManager.disableBlend();
				GlStateManager.popMatrix();
			}

			this.mc.mcProfiler.endSection();
		}

		Scoreboard scoreboard = this.mc.theWorld.getScoreboard();
		ScoreObjective scoreobjective = null;
		ScorePlayerTeam scoreplayerteam = scoreboard.getPlayersTeam(this.mc.thePlayer.getName());

		if (scoreplayerteam != null) {
			try {
				int j1 = scoreplayerteam.getChatFormat().getColorIndex();

				if (j1 >= 0) {
					scoreobjective = scoreboard.getObjectiveInDisplaySlot(3 + j1);
				}
			} catch (NullPointerException e) {

			}
		}

		ScoreObjective scoreobjective1 = scoreobjective != null ? scoreobjective
				: scoreboard.getObjectiveInDisplaySlot(1);

		if (scoreobjective1 != null) {
			this.renderScoreboard(scoreobjective1, scaledresolution);
		}

		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		GlStateManager.disableAlpha();
		GlStateManager.pushMatrix();
		GlStateManager.translate(0.0F, (float) (j - 48), 0.0F);
		this.mc.mcProfiler.startSection("chat");
		this.persistantChatGUI.drawChat(this.updateCounter);
		this.mc.mcProfiler.endSection();
		GlStateManager.popMatrix();
		scoreobjective1 = scoreboard.getObjectiveInDisplaySlot(0);

		if (!this.mc.gameSettings.keyBindPlayerList.isKeyDown() || this.mc.isIntegratedServerRunning()
				&& this.mc.thePlayer.sendQueue.getPlayerInfoMap().size() <= 1 && scoreobjective1 == null) {
			this.overlayPlayerList.updatePlayerList(false);
		} else {
			this.overlayPlayerList.updatePlayerList(true);
			this.overlayPlayerList.renderPlayerlist(i, scoreboard, scoreobjective1);
		}

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableLighting();
		GlStateManager.enableAlpha();
	}

	protected void renderTooltip(ScaledResolution sr, float partialTicks) {
		if (this.mc.getRenderViewEntity() instanceof EntityPlayer) {
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			this.mc.getTextureManager().bindTexture(widgetsTexPath);
			EntityPlayer entityplayer = (EntityPlayer) this.mc.getRenderViewEntity();
			int i = sr.getScaledWidth() / 2;
			float f = this.zLevel;
			this.zLevel = -90.0F;
			if(!Client.INSTANCE.moduleManager.getModule("CustomHotbar").isState()) {
			this.drawTexturedModalRect(i - 91, sr.getScaledHeight() - 22, 0, 0, 182, 22);
			this.drawTexturedModalRect(i - 91 - 1 + entityplayer.inventory.currentItem * 20,
					sr.getScaledHeight() - 22 - 1, 0, 22, 24, 22);
			}
			this.zLevel = f;
			GlStateManager.enableRescaleNormal();
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
			RenderHelper.enableGUIStandardItemLighting();

			for (int j = 0; j < 9; ++j) {
				int k = sr.getScaledWidth() / 2 - 90 + j * 20 + 2;
				int l = sr.getScaledHeight() - 16 - 3;
				this.renderHotbarItem(j, k, l, partialTicks, entityplayer);
			}

			RenderHelper.disableStandardItemLighting();
			GlStateManager.disableRescaleNormal();
			GlStateManager.disableBlend();
		}
	}

	public void renderHorseJumpBar(ScaledResolution p_175186_1_, int p_175186_2_) {
		this.mc.mcProfiler.startSection("jumpBar");
		this.mc.getTextureManager().bindTexture(Gui.icons);
		float f = this.mc.thePlayer.getHorseJumpPower();
		short short1 = 182;
		int i = (int) (f * (float) (short1 + 1));
		int j = p_175186_1_.getScaledHeight() - 32 + 3;
		this.drawTexturedModalRect(p_175186_2_, j, 0, 84, short1, 5);

		if (i > 0) {
			this.drawTexturedModalRect(p_175186_2_, j, 0, 89, i, 5);
		}

		this.mc.mcProfiler.endSection();
	}

	public void renderExpBar(ScaledResolution p_175176_1_, int p_175176_2_) {
		this.mc.mcProfiler.startSection("expBar");
		this.mc.getTextureManager().bindTexture(Gui.icons);
		int i = this.mc.thePlayer.xpBarCap();

		if (i > 0) {
			short short1 = 182;
			int k = (int) (this.mc.thePlayer.experience * (float) (short1 + 1));
			int j = p_175176_1_.getScaledHeight() - 32 + 3;
			this.drawTexturedModalRect(p_175176_2_, j, 0, 64, short1, 5);

			if (k > 0) {
				this.drawTexturedModalRect(p_175176_2_, j, 0, 69, k, 5);
			}
		}

		this.mc.mcProfiler.endSection();

		if (this.mc.thePlayer.experienceLevel > 0) {
			this.mc.mcProfiler.startSection("expLevel");
			int j1 = 8453920;

			if (Config.isCustomColors()) {
				j1 = CustomColors.getExpBarTextColor(j1);
			}

			String s = "" + this.mc.thePlayer.experienceLevel;
			int i1 = (p_175176_1_.getScaledWidth() - this.getBasicFontRenderer().getStringWidth(s)) / 2;
			int l = p_175176_1_.getScaledHeight() - 31 - 4;
			boolean flag = false;
			this.getBasicFontRenderer().drawString(s, i1 + 1, l, 0);
			this.getBasicFontRenderer().drawString(s, i1 - 1, l, 0);
			this.getBasicFontRenderer().drawString(s, i1, l + 1, 0);
			this.getBasicFontRenderer().drawString(s, i1, l - 1, 0);
			this.getBasicFontRenderer().drawString(s, i1, l, j1);
			this.mc.mcProfiler.endSection();
		}
	}

	public void func_181551_a(ScaledResolution p_181551_1_) {
		this.mc.mcProfiler.startSection("selectedItemName");

		if (this.remainingHighlightTicks > 0 && this.highlightingItemStack != null) {
			String s = this.highlightingItemStack.getDisplayName();

			if (this.highlightingItemStack.hasDisplayName()) {
				s = EnumChatFormatting.ITALIC + s;
			}

			int i = (p_181551_1_.getScaledWidth() - this.getBasicFontRenderer().getStringWidth(s)) / 2;
			int j = p_181551_1_.getScaledHeight() - 59;

			if (!this.mc.playerController.shouldDrawHUD()) {
				j += 14;
			}

			int k = (int) ((float) this.remainingHighlightTicks * 256.0F / 10.0F);

			if (k > 255) {
				k = 255;
			}

			if (k > 0) {
				GlStateManager.pushMatrix();
				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
				this.getBasicFontRenderer().drawStringWithShadow(s, (float) i, (float) j, 16777215 + (k << 24));
				GlStateManager.disableBlend();
				GlStateManager.popMatrix();
			}
		}

		this.mc.mcProfiler.endSection();
	}

	public void renderDemo(ScaledResolution p_175185_1_) {
		this.mc.mcProfiler.startSection("demo");
		String s = "";

		if (this.mc.theWorld.getTotalWorldTime() >= 120500L) {
			s = I18n.format("demo.demoExpired");
		} else {
			s = I18n.format("demo.remainingTime",
					StringUtils.ticksToElapsedTime((int) (120500L - this.mc.theWorld.getTotalWorldTime())));
		}

		int i = this.getBasicFontRenderer().getStringWidth(s);
		this.getBasicFontRenderer().drawStringWithShadow(s, (float) (p_175185_1_.getScaledWidth() - i - 10), 5.0F,
				16777215);
		this.mc.mcProfiler.endSection();
	}

	protected boolean showCrosshair() {
		try {
			if (this.mc.gameSettings.showDebugInfo && !this.mc.thePlayer.hasReducedDebug()
					&& !this.mc.gameSettings.reducedDebugInfo
					|| Client.INSTANCE.moduleManager.getModule("Crosshair").state) {
				return false;
			} else if (this.mc.playerController.isSpectator()) {
				if (this.mc.pointedEntity != null) {
					return true;
				} else {
					if (this.mc.objectMouseOver != null
							&& this.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
						BlockPos blockpos = this.mc.objectMouseOver.getBlockPos();

						return this.mc.theWorld.getTileEntity(blockpos) instanceof IInventory;
					}

					return false;
				}
			} else {
				return true;
			}
		} catch (NullPointerException e) {
			return false;
		}
	}

	public void renderStreamIndicator(ScaledResolution p_180478_1_) {
		this.streamIndicator.render(p_180478_1_.getScaledWidth() - 10, 10);
	}

	private void renderScoreboard(ScoreObjective p_180475_1_, ScaledResolution p_180475_2_) {
		Scoreboard scoreboard = p_180475_1_.getScoreboard();
		Collection collection = scoreboard.getSortedScores(p_180475_1_);
		ArrayList arraylist = Lists.newArrayList(Iterables.filter(collection, new Predicate() {
			private static final String __OBFID = "CL_00001958";

			public boolean apply(Score p_apply_1_) {
				return p_apply_1_.getPlayerName() != null && !p_apply_1_.getPlayerName().startsWith("#");
			}

			public boolean apply(Object p_apply_1_) {
				return this.apply((Score) p_apply_1_);
			}
		}));
		ArrayList arraylist1;

		if (arraylist.size() > 15) {
			arraylist1 = Lists.newArrayList(Iterables.skip(arraylist, collection.size() - 15));
		} else {
			arraylist1 = arraylist;
		}

		int i =mc.fontRendererObj.getStringWidth(p_180475_1_.getDisplayName());

		for (Object score : arraylist1) {
			ScorePlayerTeam scoreplayerteam = scoreboard.getPlayersTeam(((Score) score).getPlayerName());
			String s = ScorePlayerTeam.formatPlayerName(scoreplayerteam, ((Score) score).getPlayerName()) + ": "
					+ EnumChatFormatting.RED + ((Score) score).getScorePoints();
			i = Math.max(i, Client.INSTANCE.unicodeBasicFontRenderer5.getStringWidth(s));
		}

		int j1 = arraylist1.size() *  mc.fontRendererObj.getFontHeight();
		int k1 = Client.INSTANCE.moduleManager.isToggled(Themes.class) || Client.INSTANCE.moduleManager.isToggled(de.fanta.module.impl.visual.ArrayList.class) ? Client.INSTANCE.moduleManager.getModule(Themes.class).getHeight() + 33 + j1 : p_180475_2_.getScaledHeight() / 2 + j1 + mc.fontRendererObj.getFontHeight() + 15;
		byte b0 = 3;
		int j = p_180475_2_.getScaledWidth() - i - b0;
		int k = 0;

		for (Object score1 : arraylist1) {
			++k;
			ScorePlayerTeam scoreplayerteam1 = scoreboard.getPlayersTeam(((Score) score1).getPlayerName());
			String s1 = ScorePlayerTeam.formatPlayerName(scoreplayerteam1, ((Score) score1).getPlayerName());
			String s2 = EnumChatFormatting.RED + "" + ((Score) score1).getScorePoints();
			int l = k1 - k * mc.fontRendererObj.getFontHeight();

			int i1 = p_180475_2_.getScaledWidth() - b0 + 2;
		//	Client.blurHelper.blur2(j - 2, l - Client.INSTANCE.unicodeBasicFontRenderer5.FONT_HEIGHT, i1, l - 1, (float) 10);
			drawRect(j - 2, l, i1, l +mc.fontRendererObj.getFontHeight(), new Color(0,0,0,180).getRGB());
			drawRect(j +200, l, i1, l + mc.fontRendererObj.getFontHeight(),Color.white.getRGB());
			// this.getBasicFontRenderer().drawString(s1, j, l, Color.white.getRGB());
			mc.fontRendererObj.drawString(s1.replaceAll(mc.getCurrentServerData().serverIP, "fanta.best"), j,
					l, Color.white.getRGB());
//			this.getBasicFontRenderer().drawString(s2, i1 - this.getBasicFontRenderer().getStringWidth(s2), l,
//					Color.white.getRGB());

			if (k == arraylist1.size()) {
				String s3 = p_180475_1_.getDisplayName();
			//	drawRect(j +80, l - this.getBasicFontRenderer().getFontHeight() - 1, i1, l - 1, Color.white.getRGB());
				drawRect(j - 2, l - this.getBasicFontRenderer().getFontHeight() - 1, i1, l - 1, new Color(0,0,0,180).getRGB());
				drawRect(j +200, l - this.getBasicFontRenderer().getFontHeight() - 1, i1, l - 1,Color.white.getRGB());
				drawRect(j - 2, l - 1, i1, l, new Color(0,0,0,180).getRGB());
				drawRect(j +200, l - 1, i1, l, Color.white.getRGB());

				mc.fontRendererObj.drawString(s3,
						j + i / 2 - mc.fontRendererObj.getStringWidth(s3) / 2,
						l - mc.fontRendererObj.getFontHeight(), Color.white.getRGB());

				// Gui.drawRect(ScaledResolution.INSTANCE.getScaledWidth()-2F, l + 100,
				// ScaledResolution.INSTANCE.getScaledWidth(), l - 10, Color.red.getRGB());

			}
		}
	}

	private void renderPlayerStats(ScaledResolution p_180477_1_) {
		if (this.mc.getRenderViewEntity() instanceof EntityPlayer) {
			EntityPlayer entityplayer = (EntityPlayer) this.mc.getRenderViewEntity();
			int i = MathHelper.ceiling_float_int(entityplayer.getHealth());
			boolean flag = this.healthUpdateCounter > (long) this.updateCounter
					&& (this.healthUpdateCounter - (long) this.updateCounter) / 3L % 2L == 1L;

			if (i < this.playerHealth && entityplayer.hurtResistantTime > 0) {
				this.lastSystemTime = Minecraft.getSystemTime();
				this.healthUpdateCounter = this.updateCounter + 20;
			} else if (i > this.playerHealth && entityplayer.hurtResistantTime > 0) {
				this.lastSystemTime = Minecraft.getSystemTime();
				this.healthUpdateCounter = this.updateCounter + 10;
			}

			if (Minecraft.getSystemTime() - this.lastSystemTime > 1000L) {
				this.playerHealth = i;
				this.lastPlayerHealth = i;
				this.lastSystemTime = Minecraft.getSystemTime();
			}

			this.playerHealth = i;
			int j = this.lastPlayerHealth;
			this.rand.setSeed(this.updateCounter * 312871);
			boolean flag1 = false;
			FoodStats foodstats = entityplayer.getFoodStats();
			int k = foodstats.getFoodLevel();
			int l = foodstats.getPrevFoodLevel();
			IAttributeInstance iattributeinstance = entityplayer.getEntityAttribute(SharedMonsterAttributes.maxHealth);
			int i1 = p_180477_1_.getScaledWidth() / 2 - 91;
			int j1 = p_180477_1_.getScaledWidth() / 2 + 91;
			int k1 = p_180477_1_.getScaledHeight() - 39;
			float f = (float) iattributeinstance.getAttributeValue();
			float f1 = entityplayer.getAbsorptionAmount();
			int l1 = MathHelper.ceiling_float_int((f + f1) / 2.0F / 10.0F);
			int i2 = Math.max(10 - (l1 - 2), 3);
			int j2 = k1 - (l1 - 1) * i2 - 10;
			float f2 = f1;
			int k2 = entityplayer.getTotalArmorValue();
			int l2 = -1;

			if (entityplayer.isPotionActive(Potion.regeneration)) {
				l2 = this.updateCounter % MathHelper.ceiling_float_int(f + 5.0F);
			}

			this.mc.mcProfiler.startSection("armor");

			for (int i3 = 0; i3 < 10; ++i3) {
				if (k2 > 0) {
					int j3 = i1 + i3 * 8;

					if (i3 * 2 + 1 < k2) {
						this.drawTexturedModalRect(j3, j2, 34, 9, 9, 9);
					}

					if (i3 * 2 + 1 == k2) {
						this.drawTexturedModalRect(j3, j2, 25, 9, 9, 9);
					}

					if (i3 * 2 + 1 > k2) {
						this.drawTexturedModalRect(j3, j2, 16, 9, 9, 9);
					}
				}
			}

			this.mc.mcProfiler.endStartSection("health");

			for (int j5 = MathHelper.ceiling_float_int((f + f1) / 2.0F) - 1; j5 >= 0; --j5) {
				int k5 = 16;

				if (entityplayer.isPotionActive(Potion.poison)) {
					k5 += 36;
				} else if (entityplayer.isPotionActive(Potion.wither)) {
					k5 += 72;
				}

				byte b0 = 0;

				if (flag) {
					b0 = 1;
				}

				int k3 = MathHelper.ceiling_float_int((float) (j5 + 1) / 10.0F) - 1;
				int l3 = i1 + j5 % 10 * 8;
				int i4 = k1 - k3 * i2;

				if (i <= 4) {
					i4 += this.rand.nextInt(2);
				}

				if (j5 == l2) {
					i4 -= 2;
				}

				byte b1 = 0;

				if (entityplayer.worldObj.getWorldInfo().isHardcoreModeEnabled()) {
					b1 = 5;
				}

				this.drawTexturedModalRect(l3, i4, 16 + b0 * 9, 9 * b1, 9, 9);

				if (flag) {
					if (j5 * 2 + 1 < j) {
						this.drawTexturedModalRect(l3, i4, k5 + 54, 9 * b1, 9, 9);
					}

					if (j5 * 2 + 1 == j) {
						this.drawTexturedModalRect(l3, i4, k5 + 63, 9 * b1, 9, 9);
					}
				}

				if (f2 <= 0.0F) {
					if (j5 * 2 + 1 < i) {
						this.drawTexturedModalRect(l3, i4, k5 + 36, 9 * b1, 9, 9);
					}

					if (j5 * 2 + 1 == i) {
						this.drawTexturedModalRect(l3, i4, k5 + 45, 9 * b1, 9, 9);
					}
				} else {
					if (f2 == f1 && f1 % 2.0F == 1.0F) {
						this.drawTexturedModalRect(l3, i4, k5 + 153, 9 * b1, 9, 9);
					} else {
						this.drawTexturedModalRect(l3, i4, k5 + 144, 9 * b1, 9, 9);
					}

					f2 -= 2.0F;
				}
			}

			Entity entity = entityplayer.ridingEntity;

			if (entity == null) {
				this.mc.mcProfiler.endStartSection("food");

				for (int l5 = 0; l5 < 10; ++l5) {
					int i8 = k1;
					int j6 = 16;
					byte b4 = 0;

					if (entityplayer.isPotionActive(Potion.hunger)) {
						j6 += 36;
						b4 = 13;
					}

					if (entityplayer.getFoodStats().getSaturationLevel() <= 0.0F
							&& this.updateCounter % (k * 3 + 1) == 0) {
						i8 = k1 + (this.rand.nextInt(3) - 1);
					}

					if (flag1) {
						b4 = 1;
					}

					int k7 = j1 - l5 * 8 - 9;
					this.drawTexturedModalRect(k7, i8, 16 + b4 * 9, 27, 9, 9);

					if (flag1) {
						if (l5 * 2 + 1 < l) {
							this.drawTexturedModalRect(k7, i8, j6 + 54, 27, 9, 9);
						}

						if (l5 * 2 + 1 == l) {
							this.drawTexturedModalRect(k7, i8, j6 + 63, 27, 9, 9);
						}
					}

					if (l5 * 2 + 1 < k) {
						this.drawTexturedModalRect(k7, i8, j6 + 36, 27, 9, 9);
					}

					if (l5 * 2 + 1 == k) {
						this.drawTexturedModalRect(k7, i8, j6 + 45, 27, 9, 9);
					}
				}
			} else if (entity instanceof EntityLivingBase) {
				this.mc.mcProfiler.endStartSection("mountHealth");
				EntityLivingBase entitylivingbase = (EntityLivingBase) entity;
				int l7 = (int) Math.ceil(entitylivingbase.getHealth());
				float f3 = entitylivingbase.getMaxHealth();
				int l6 = (int) (f3 + 0.5F) / 2;

				if (l6 > 30) {
					l6 = 30;
				}

				int j7 = k1;

				for (int j4 = 0; l6 > 0; j4 += 20) {
					int k4 = Math.min(l6, 10);
					l6 -= k4;

					for (int l4 = 0; l4 < k4; ++l4) {
						byte b2 = 52;
						byte b3 = 0;

						if (flag1) {
							b3 = 1;
						}

						int i5 = j1 - l4 * 8 - 9;
						this.drawTexturedModalRect(i5, j7, b2 + b3 * 9, 9, 9, 9);

						if (l4 * 2 + 1 + j4 < l7) {
							this.drawTexturedModalRect(i5, j7, b2 + 36, 9, 9, 9);
						}

						if (l4 * 2 + 1 + j4 == l7) {
							this.drawTexturedModalRect(i5, j7, b2 + 45, 9, 9, 9);
						}
					}

					j7 -= 10;
				}
			}

			this.mc.mcProfiler.endStartSection("air");

			if (entityplayer.isInsideOfMaterial(Material.water)) {
				int i6 = this.mc.thePlayer.getAir();
				int j8 = MathHelper.ceiling_double_int((double) (i6 - 2) * 10.0D / 300.0D);
				int k6 = MathHelper.ceiling_double_int((double) i6 * 10.0D / 300.0D) - j8;

				for (int i7 = 0; i7 < j8 + k6; ++i7) {
					if (i7 < j8) {
						this.drawTexturedModalRect(j1 - i7 * 8 - 9, j2, 16, 18, 9, 9);
					} else {
						this.drawTexturedModalRect(j1 - i7 * 8 - 9, j2, 25, 18, 9, 9);
					}
				}
			}

			this.mc.mcProfiler.endSection();
		}
	}

	/**
	 * Renders dragon's (boss) health on the HUD
	 */
	private void renderBossHealth() {
		if (BossStatus.bossName != null && BossStatus.statusBarTime > 0) {
			--BossStatus.statusBarTime;
			BasicFontRenderer fontrenderer = this.mc.fontRendererObj;
			ScaledResolution scaledresolution = new ScaledResolution(this.mc);
			int i = scaledresolution.getScaledWidth();
			short short1 = 182;
			int j = i / 2 - short1 / 2;
			int k = (int) (BossStatus.healthScale * (float) (short1 + 1));
			byte b0 = 12;
			this.drawTexturedModalRect(j, b0, 0, 74, short1, 5);
			this.drawTexturedModalRect(j, b0, 0, 74, short1, 5);

			if (k > 0) {
				this.drawTexturedModalRect(j, b0, 0, 79, k, 5);
			}

			String s = BossStatus.bossName;
			int l = 16777215;

			if (Config.isCustomColors()) {
				l = CustomColors.getBossTextColor(l);
			}

			this.getBasicFontRenderer().drawStringWithShadow(s,
					(float) (i / 2 - this.getBasicFontRenderer().getStringWidth(s) / 2), (float) (b0 - 10), l);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			this.mc.getTextureManager().bindTexture(icons);
		}
	}

	private void renderPumpkinOverlay(ScaledResolution p_180476_1_) {
		GlStateManager.disableDepth();
		GlStateManager.depthMask(false);
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableAlpha();
		this.mc.getTextureManager().bindTexture(pumpkinBlurTexPath);
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
		worldrenderer.pos(0.0D, p_180476_1_.getScaledHeight(), -90.0D).tex(0.0D, 1.0D).endVertex();
		worldrenderer.pos(p_180476_1_.getScaledWidth(), p_180476_1_.getScaledHeight(), -90.0D).tex(1.0D, 1.0D)
				.endVertex();
		worldrenderer.pos(p_180476_1_.getScaledWidth(), 0.0D, -90.0D).tex(1.0D, 0.0D).endVertex();
		worldrenderer.pos(0.0D, 0.0D, -90.0D).tex(0.0D, 0.0D).endVertex();
		tessellator.draw();
		GlStateManager.depthMask(true);
		GlStateManager.enableDepth();
		GlStateManager.enableAlpha();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	}

	/**
	 * Renders a Vignette arount the entire screen that changes with light level.
	 */
	private void renderVignette(float p_180480_1_, ScaledResolution p_180480_2_) {
		if (!Config.isVignetteEnabled()) {
			GlStateManager.enableDepth();
			GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		} else {
			p_180480_1_ = 1.0F - p_180480_1_;
			p_180480_1_ = MathHelper.clamp_float(p_180480_1_, 0.0F, 1.0F);
			WorldBorder worldborder = this.mc.theWorld.getWorldBorder();
			float f = (float) worldborder.getClosestDistance(this.mc.thePlayer);
			double d0 = Math.min(worldborder.getResizeSpeed() * (double) worldborder.getWarningTime() * 1000.0D,
					Math.abs(worldborder.getTargetSize() - worldborder.getDiameter()));
			double d1 = Math.max(worldborder.getWarningDistance(), d0);

			if ((double) f < d1) {
				f = 1.0F - (float) ((double) f / d1);
			} else {
				f = 0.0F;
			}

			this.prevVignetteBrightness = (float) ((double) this.prevVignetteBrightness
					+ (double) (p_180480_1_ - this.prevVignetteBrightness) * 0.01D);
			GlStateManager.disableDepth();
			GlStateManager.depthMask(false);
			GlStateManager.tryBlendFuncSeparate(0, 769, 1, 0);

			if (f > 0.0F) {
				GlStateManager.color(0.0F, f, f, 1.0F);
			} else {
				GlStateManager.color(this.prevVignetteBrightness, this.prevVignetteBrightness,
						this.prevVignetteBrightness, 1.0F);
			}

			this.mc.getTextureManager().bindTexture(vignetteTexPath);
			Tessellator tessellator = Tessellator.getInstance();
			WorldRenderer worldrenderer = tessellator.getWorldRenderer();
			worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
			worldrenderer.pos(0.0D, p_180480_2_.getScaledHeight(), -90.0D).tex(0.0D, 1.0D).endVertex();
			worldrenderer.pos(p_180480_2_.getScaledWidth(), p_180480_2_.getScaledHeight(), -90.0D).tex(1.0D, 1.0D)
					.endVertex();
			worldrenderer.pos(p_180480_2_.getScaledWidth(), 0.0D, -90.0D).tex(1.0D, 0.0D).endVertex();
			worldrenderer.pos(0.0D, 0.0D, -90.0D).tex(0.0D, 0.0D).endVertex();
			tessellator.draw();
			GlStateManager.depthMask(true);
			GlStateManager.enableDepth();
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		}
	}

	private void func_180474_b(float p_180474_1_, ScaledResolution p_180474_2_) {
		if (p_180474_1_ < 1.0F) {
			p_180474_1_ = p_180474_1_ * p_180474_1_;
			p_180474_1_ = p_180474_1_ * p_180474_1_;
			p_180474_1_ = p_180474_1_ * 0.8F + 0.2F;
		}

		GlStateManager.disableAlpha();
		GlStateManager.disableDepth();
		GlStateManager.depthMask(false);
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		GlStateManager.color(1.0F, 1.0F, 1.0F, p_180474_1_);
		this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
		TextureAtlasSprite textureatlassprite = this.mc.getBlockRendererDispatcher().getBlockModelShapes()
				.getTexture(Blocks.portal.getDefaultState());
		float f = textureatlassprite.getMinU();
		float f1 = textureatlassprite.getMinV();
		float f2 = textureatlassprite.getMaxU();
		float f3 = textureatlassprite.getMaxV();
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
		worldrenderer.pos(0.0D, p_180474_2_.getScaledHeight(), -90.0D).tex(f, f3).endVertex();
		worldrenderer.pos(p_180474_2_.getScaledWidth(), p_180474_2_.getScaledHeight(), -90.0D).tex(f2, f3).endVertex();
		worldrenderer.pos(p_180474_2_.getScaledWidth(), 0.0D, -90.0D).tex(f2, f1).endVertex();
		worldrenderer.pos(0.0D, 0.0D, -90.0D).tex(f, f1).endVertex();
		tessellator.draw();
		GlStateManager.depthMask(true);
		GlStateManager.enableDepth();
		GlStateManager.enableAlpha();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	}

	private void renderHotbarItem(int index, int xPos, int yPos, float partialTicks, EntityPlayer p_175184_5_) {
		ItemStack itemstack = p_175184_5_.inventory.mainInventory[index];

		if (itemstack != null) {
			float f = (float) itemstack.animationsToGo - partialTicks;

			if (f > 0.0F) {
				GlStateManager.pushMatrix();
				float f1 = 1.0F + f / 5.0F;
				GlStateManager.translate((float) (xPos + 8), (float) (yPos + 12), 0.0F);
				GlStateManager.scale(1.0F / f1, (f1 + 1.0F) / 2.0F, 1.0F);
				GlStateManager.translate((float) (-(xPos + 8)), (float) (-(yPos + 12)), 0.0F);
			}

			this.itemRenderer.renderItemAndEffectIntoGUI(itemstack, xPos, yPos);

			if (f > 0.0F) {
				GlStateManager.popMatrix();
			}

			this.itemRenderer.renderItemOverlays(this.mc.fontRendererObj, itemstack, xPos, yPos);
		}
	}

	/**
	 * The update tick for the ingame UI
	 */
	public void updateTick() {
		if (this.recordPlayingUpFor > 0) {
			--this.recordPlayingUpFor;
		}

		if (this.titlesTimer > 0) {
			--this.titlesTimer;

			if (this.titlesTimer <= 0) {
				this.field_175201_x = "";
				this.field_175200_y = "";
			}
		}

		++this.updateCounter;
		this.streamIndicator.func_152439_a();

		if (this.mc.thePlayer != null) {
			ItemStack itemstack = this.mc.thePlayer.inventory.getCurrentItem();

			if (itemstack == null) {
				this.remainingHighlightTicks = 0;
			} else if (this.highlightingItemStack != null && itemstack.getItem() == this.highlightingItemStack.getItem()
					&& ItemStack.areItemStackTagsEqual(itemstack, this.highlightingItemStack)
					&& (itemstack.isItemStackDamageable()
							|| itemstack.getMetadata() == this.highlightingItemStack.getMetadata())) {
				if (this.remainingHighlightTicks > 0) {
					--this.remainingHighlightTicks;
				}
			} else {
				this.remainingHighlightTicks = 40;
			}

			this.highlightingItemStack = itemstack;
		}
	}

	public void setRecordPlayingMessage(String p_73833_1_) {
		this.setRecordPlaying(I18n.format("record.nowPlaying", p_73833_1_), true);
	}

	public void setRecordPlaying(String p_110326_1_, boolean p_110326_2_) {
		this.recordPlaying = p_110326_1_;
		this.recordPlayingUpFor = 60;
		this.recordIsPlaying = p_110326_2_;
	}

	public void displayTitle(String p_175178_1_, String p_175178_2_, int p_175178_3_, int p_175178_4_,
			int p_175178_5_) {
		if (p_175178_1_ == null && p_175178_2_ == null && p_175178_3_ < 0 && p_175178_4_ < 0 && p_175178_5_ < 0) {
			this.field_175201_x = "";
			this.field_175200_y = "";
			this.titlesTimer = 0;
		} else if (p_175178_1_ != null) {
			this.field_175201_x = p_175178_1_;
			this.titlesTimer = this.titleFadeIn + this.titleDisplayTime + this.titleFadeOut;
		} else if (p_175178_2_ != null) {
			this.field_175200_y = p_175178_2_;
		} else {
			if (p_175178_3_ >= 0) {
				this.titleFadeIn = p_175178_3_;
			}

			if (p_175178_4_ >= 0) {
				this.titleDisplayTime = p_175178_4_;
			}

			if (p_175178_5_ >= 0) {
				this.titleFadeOut = p_175178_5_;
			}

			if (this.titlesTimer > 0) {
				this.titlesTimer = this.titleFadeIn + this.titleDisplayTime + this.titleFadeOut;
			}
		}
	}

	public void setRecordPlaying(IChatComponent p_175188_1_, boolean p_175188_2_) {
		this.setRecordPlaying(p_175188_1_.getUnformattedText(), p_175188_2_);
	}

	/**
	 * returns a pointer to the persistant Chat GUI, containing all previous chat
	 * messages and such
	 */
	public GuiNewChat getChatGUI() {
		return this.persistantChatGUI;
	}

	public int getUpdateCounter() {
		return this.updateCounter;
	}

	public BasicFontRenderer getBasicFontRenderer() {
		return this.mc.fontRendererObj;
	}

	public GuiSpectator getSpectatorGui() {
		return this.spectatorGui;
	}

	public GuiPlayerTabOverlay getTabList() {
		return this.overlayPlayerList;
	}

	public void func_181029_i() {
		this.overlayPlayerList.func_181030_a();
	}

	static Minecraft mc1 = Minecraft.getMinecraft();

	public static void drawImage(int x, int y, int width, int height, ResourceLocation resourceLocation) {
		mc1.getTextureManager().bindTexture(resourceLocation);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, width, height);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	public static int getStartReady() {
		for (int i = 0; i < 8; i++) {
			if (i == 1) {

			}
		}
		return 0;
	}

	public static String insertNulls(int val, int min_digits) {
		String s = String.valueOf(val);
		while (s.length() < min_digits)
			s = "0" + s;
		return s;
	}

	public static void drawBorderedRect(double left, double top, double right, double bottom, double borderWidth,
			int insideColor, int borderColor, boolean borderIncludedInBounds) {
		Gui.drawRect2(left - (!borderIncludedInBounds ? borderWidth : 0),
				top - (!borderIncludedInBounds ? borderWidth : 0), right + (!borderIncludedInBounds ? borderWidth : 0),
				bottom + (!borderIncludedInBounds ? borderWidth : 0), borderColor);
		Gui.drawRect2(left + (borderIncludedInBounds ? borderWidth : 0),
				top + (borderIncludedInBounds ? borderWidth : 0), right - ((borderIncludedInBounds ? borderWidth : 0)),
				bottom - ((borderIncludedInBounds ? borderWidth : 0)), insideColor);
	}

	public static void drawRect(double left, double top, double right, double bottom, int color) {
		if (left < right) {
			double i = left;
			left = right;
			right = i;
		}

		if (top < bottom) {
			double j = top;
			top = bottom;
			bottom = j;
		}

		float f3 = (float) (color >> 24 & 255) / 255.0F;
		float f = (float) (color >> 16 & 255) / 255.0F;
		float f1 = (float) (color >> 8 & 255) / 255.0F;
		float f2 = (float) (color & 255) / 255.0F;
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		GlStateManager.color(f, f1, f2, f3);
		worldrenderer.begin(7, DefaultVertexFormats.POSITION);
		worldrenderer.pos(left, bottom, 0.0D).endVertex();
		worldrenderer.pos(right, bottom, 0.0D).endVertex();
		worldrenderer.pos(right, top, 0.0D).endVertex();
		worldrenderer.pos(left, top, 0.0D).endVertex();
		tessellator.draw();
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
	}

}
