package net.minecraft.client.gui;

import com.alan.clients.Client;
import com.alan.clients.newevent.impl.render.LimitedRender2DEvent;
import com.alan.clients.newevent.impl.render.RenderHungerEvent;
import com.alan.clients.util.font.impl.minecraft.FontRenderer;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
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
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.src.Config;
import net.minecraft.util.*;
import net.minecraft.world.border.WorldBorder;
import net.optifine.CustomColors;
import org.lwjgl.opengl.GL11;

import java.util.Random;

import static com.alan.clients.util.interfaces.InstanceAccess.LIMITED_POST_RENDER_RUNNABLES;
import static com.alan.clients.util.interfaces.InstanceAccess.LIMITED_PRE_RENDER_RUNNABLES;

public class GuiIngame extends Gui {
    private static final ResourceLocation vignetteTexPath = new ResourceLocation("textures/misc/vignette.png");
    private static final ResourceLocation widgetsTexPath = new ResourceLocation("textures/gui/widgets.png");
    private static final ResourceLocation pumpkinBlurTexPath = new ResourceLocation("textures/misc/pumpkinblur.png");
    public final Random rand = new Random();
    private final Minecraft mc;
    private final RenderItem itemRenderer;

    private float rPosX;

    /**
     * ChatGUI instance that retains all previous chat data
     */
    private final GuiNewChat persistantChatGUI;
    public int updateCounter;

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
     * Previous frame vignette brightness (slowly changes by 1% each frame)
     */
    public float prevVignetteBrightness = 1.0F;

    /**
     * Remaining ticks the item highlight should be visible
     */
    private int remainingHighlightTicks;

    /**
     * The ItemStack that is currently being highlighted
     */
    private ItemStack highlightingItemStack;
    private final GuiOverlayDebug overlayDebug;

    /**
     * The spectator GUI for this in-game GUI instance
     */
    private final GuiSpectator spectatorGui;
    public final GuiPlayerTabOverlay overlayPlayerList;
    private int field_175195_w;
    private String field_175201_x = "";
    private String field_175200_y = "";
    private int field_175199_z;
    private int field_175192_A;
    private int field_175193_B;
    private int playerHealth = 0;
    private int lastPlayerHealth = 0;

    /**
     * The last recorded system time
     */
    private long lastSystemTime = 0L;
    public static long a = 34L;

    /**
     * Used with updateCounter to make the heart bar flash
     */
    public long healthUpdateCounter = 0L;
    public int frame;

    public GuiIngame(final Minecraft mcIn) {
        this.mc = mcIn;
        this.itemRenderer = mcIn.getRenderItem();
        this.overlayDebug = new GuiOverlayDebug(mcIn);
        this.spectatorGui = new GuiSpectator(mcIn);
        this.persistantChatGUI = new GuiNewChat(mcIn);
        this.overlayPlayerList = new GuiPlayerTabOverlay(mcIn, this);
        this.func_175177_a();
    }

    public void func_175177_a() {
        this.field_175199_z = 10;
        this.field_175192_A = 70;
        this.field_175193_B = 20;
    }

    public void renderGameOverlay(final float partialTicks) {
        final ScaledResolution scaledResolution = mc.scaledResolution;
        final int i = scaledResolution.getScaledWidth();
        final int j = scaledResolution.getScaledHeight();
//        this.mc.entityRenderer.setupOverlayRendering();
        GlStateManager.enableBlend();

        if (a == 34L && a != 33L) {
            GL11.glScaled(1.5, 1.5, 1.5);
        }

//        if (Config.isVignetteEnabled()) {
//            this.renderVignette(this.mc.thePlayer.getBrightness(partialTicks), scaledResolution);
//        } else {
//        }

//        if (this.mc.gameSettings.thirdPersonView == 0 && itemstack != null && itemstack.getItem() == Item.getItemFromBlock(Blocks.pumpkin)) {
//            this.renderPumpkinOverlay(scaledResolution);
//        }

//        if (!this.mc.thePlayer.isPotionActive(Potion.confusion)) {
//            final float f = this.mc.thePlayer.prevTimeInPortal + (this.mc.thePlayer.timeInPortal - this.mc.thePlayer.prevTimeInPortal) * partialTicks;
//
//            if (f > 0.0F) {
//                this.func_180474_b(f, scaledResolution);
//            }
//        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(Gui.icons);
        GlStateManager.enableBlend();

        if (this.mc.playerController.isSpectator()) {
            this.spectatorGui.renderTooltip(scaledResolution, partialTicks);
        }

        GlStateManager.enableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        this.mc.mcProfiler.startSection("bossHealth");
//        this.renderBossHealth();
        this.mc.mcProfiler.endSection();

        if (this.mc.playerController.shouldDrawHUD()) {
            this.renderPlayerStats(scaledResolution);
        }

        GlStateManager.disableBlend();

//        if (this.mc.thePlayer.getSleepTimer() > 0) {
//            this.mc.mcProfiler.startSection("sleep");
//            GlStateManager.disableDepth();
//            GlStateManager.disableAlpha();
//            final int j1 = this.mc.thePlayer.getSleepTimer();
//            float f1 = (float) j1 / 100.0F;
//
//            if (f1 > 1.0F) {
//                f1 = 1.0F - (float) (j1 - 100) / 10.0F;
//            }
//
//            final int k = (int) (220.0F * f1) << 24 | 1052704;
//            drawRect(0, 0, i, j, k);
//            GlStateManager.enableAlpha();
//            GlStateManager.enableDepth();
//            this.mc.mcProfiler.endSection();
//        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        final int k1 = i / 2 - 91;

//        if (this.mc.playerController.gameIsSurvivalOrAdventure()) {
//            this.renderExpBar(scaledResolution, k1);
//        }

        if (this.mc.gameSettings.heldItemTooltips && !this.mc.playerController.isSpectator()) {
            this.func_181551_a(scaledResolution);
        } else if (this.mc.thePlayer.isSpectator()) {
            this.spectatorGui.func_175263_a(scaledResolution);
        }

        if (this.mc.gameSettings.showDebugInfo) {
            this.overlayDebug.renderDebugInfo(scaledResolution);
        }

//        if (this.recordPlayingUpFor > 0) {
//            this.mc.mcProfiler.startSection("overlayMessage");
//            final float f2 = (float) this.recordPlayingUpFor - partialTicks;
//            int l1 = (int) (f2 * 255.0F / 20.0F);
//
//            if (l1 > 255) {
//                l1 = 255;
//            }
//
//            if (l1 > 8) {
//                GlStateManager.pushMatrix();
//                GlStateManager.translate((float) (i / 2), (float) (j - 68), 0.0F);
//                GlStateManager.enableBlend();
//                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
//                int l = 16777215;
//
//                if (this.recordIsPlaying) {
//                    l = MathHelper.func_181758_c(f2 / 50.0F, 0.7F, 0.6F) & 16777215;
//                }
//
//                this.getFontRenderer().drawString(this.recordPlaying, -this.getFontRenderer().getStringWidth(this.recordPlaying) / 2, -4, l + (l1 << 24 & -16777216));
//                GlStateManager.disableBlend();
//                GlStateManager.popMatrix();
//            }
//
//            this.mc.mcProfiler.endSection();
//        }

        if (this.field_175195_w > 0) {
            this.mc.mcProfiler.startSection("titleAndSubtitle");
            final float f3 = (float) this.field_175195_w - partialTicks;
            int i2 = 255;

            if (this.field_175195_w > this.field_175193_B + this.field_175192_A) {
                final float f4 = (float) (this.field_175199_z + this.field_175192_A + this.field_175193_B) - f3;
                i2 = (int) (f4 * 255.0F / (float) this.field_175199_z);
            }

            if (this.field_175195_w <= this.field_175193_B) {
                i2 = (int) (f3 * 255.0F / (float) this.field_175193_B);
            }

            i2 = MathHelper.clamp_int(i2, 0, 255);

            if (i2 > 8) {
                GlStateManager.pushMatrix();
                GlStateManager.translate((float) (i / 2), (float) (j / 2), 0.0F);
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                GlStateManager.pushMatrix();
                GlStateManager.scale(4.0F, 4.0F, 4.0F);
                final int j2 = i2 << 24 & -16777216;
                this.getFontRenderer().drawString(this.field_175201_x, (float) (-this.getFontRenderer().width(this.field_175201_x) / 2), -10.0F, 16777215 | j2, true);
                GlStateManager.popMatrix();
                GlStateManager.pushMatrix();
                GlStateManager.scale(2.0F, 2.0F, 2.0F);
                this.getFontRenderer().drawString(this.field_175200_y, (float) (-this.getFontRenderer().width(this.field_175200_y) / 2), 5.0F, 16777215 | j2, true);
                GlStateManager.popMatrix();
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }

            this.mc.mcProfiler.endSection();
        }

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.disableAlpha();
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, (float) (j - 48), 0.0F);
        this.mc.mcProfiler.startSection("chat");
        this.persistantChatGUI.drawChat(this.updateCounter);
        this.persistantChatGUI.drawChatLimitedFrameRate(this.updateCounter);
        this.mc.mcProfiler.endSection();
        GlStateManager.popMatrix();

        Scoreboard scoreboard = this.mc.theWorld.getScoreboard();
        ScoreObjective scoreobjective1 = scoreboard.getObjectiveInDisplaySlot(0);

        if (this.mc.gameSettings.keyBindPlayerList.isKeyDown() && (!this.mc.isIntegratedServerRunning() || this.mc.thePlayer.sendQueue.getPlayerInfoMap().size() > 1 || scoreobjective1 != null)) {
            this.overlayPlayerList.updatePlayerList(true);
            this.overlayPlayerList.renderPlayerlist(i, scoreboard, scoreobjective1);
        } else {
            this.overlayPlayerList.updatePlayerList(false);
        }

        Client.INSTANCE.getEventBus().handle(new LimitedRender2DEvent(scaledResolution, partialTicks));

        LIMITED_PRE_RENDER_RUNNABLES.forEach(Runnable::run);
        LIMITED_POST_RENDER_RUNNABLES.forEach(Runnable::run);
    }

    /* protected void renderTooltip(final ScaledResolution sr, final float partialTicks) {
        if (this.mc.getRenderViewEntity() instanceof EntityPlayer) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(widgetsTexPath);
            final EntityPlayer entityplayer = (EntityPlayer) this.mc.getRenderViewEntity();
            final int i = sr.getScaledWidth() / 2;
            final float f = zLevel;
            zLevel = -90.0F;

            final Interface module = Rise.INSTANCE.getModuleManager().get(Interface.class);

            if (module.smoothHotbar.getValue()) {
                for (int time = 0; time < stopwatch.getElapsedTime(); ++time) {
                    rPosX = MathUtil.lerp(rPosX, i - 91 - 1 + entityplayer.inventory.currentItem * 20, 0.055f);
                }

                stopwatch.reset();
            } else {
                rPosX = i - 91 - 1 + entityplayer.inventory.currentItem * 20;
            }

            if (module.customHotbar.getValue()) {
                Gui.drawRect(i - 91, sr.getScaledHeight() - 22, (sr.getScaledWidth() / 2F) + 91, sr.getScaledHeight(), 1342177280);
                Gui.drawRect(rPosX + 1, sr.getScaledHeight() - 22, rPosX + 23, sr.getScaledHeight(), 1342177280);
                Gui.drawRect(rPosX + 1, sr.getScaledHeight() - 21, rPosX + 23, sr.getScaledHeight() - 22, Rise.INSTANCE.getThemeManager().getTheme().getColor().getRGB());
            } else {
                this.drawTexturedModalRect(i - 91, sr.getScaledHeight() - 22, 0, 0, 182, 22);
                this.drawTexturedModalRect(rPosX, sr.getScaledHeight() - 22 - 1, 0, 22, 24, 22);
            }


            zLevel = f;
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            RenderHelper.enableGUIStandardItemLighting();

            for (int j = 0; j < 9; ++j) {
                final int k = sr.getScaledWidth() / 2 - 90 + j * 20 + 2;
                final int l = sr.getScaledHeight() - 16 - 3;
                this.renderHotbarItem(j, k, l, partialTicks, entityplayer);
            }

            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableRescaleNormal();
            GlStateManager.disableBlend();
        }
    } */

    public void renderHorseJumpBar(final ScaledResolution p_175186_1_, final int p_175186_2_) {
        this.mc.mcProfiler.startSection("jumpBar");
        this.mc.getTextureManager().bindTexture(Gui.icons);
        final float f = this.mc.thePlayer.getHorseJumpPower();
        final int i = 182;
        final int j = (int) (f * (float) (i + 1));
        final int k = p_175186_1_.getScaledHeight() - 32 + 3;
        this.drawTexturedModalRect(p_175186_2_, k, 0, 84, i, 5);

        if (j > 0) {
            this.drawTexturedModalRect(p_175186_2_, k, 0, 89, j, 5);
        }

        this.mc.mcProfiler.endSection();
    }

    public void renderExpBar(final ScaledResolution p_175176_1_, final int p_175176_2_) {
        this.mc.mcProfiler.startSection("expBar");
        this.mc.getTextureManager().bindTexture(Gui.icons);
        final int i = this.mc.thePlayer.xpBarCap();

        if (i > 0) {
            final int j = 182;
            final int k = (int) (this.mc.thePlayer.experience * (float) (j + 1));
            final int l = p_175176_1_.getScaledHeight() - 32 + 3;
            this.drawTexturedModalRect(p_175176_2_, l, 0, 64, j, 5);

            if (k > 0) {
                this.drawTexturedModalRect(p_175176_2_, l, 0, 69, k, 5);
            }
        }

        this.mc.mcProfiler.endSection();

        if (this.mc.thePlayer.experienceLevel > 0) {
            this.mc.mcProfiler.startSection("expLevel");
            int k1 = 8453920;

            if (Config.isCustomColors()) {
                k1 = CustomColors.getExpBarTextColor(k1);
            }

            final String s = "" + this.mc.thePlayer.experienceLevel;
            final int l1 = (p_175176_1_.getScaledWidth() - this.getFontRenderer().width(s)) / 2;
            final int i1 = p_175176_1_.getScaledHeight() - 31 - 4;
            this.getFontRenderer().drawString(s, l1 + 1, i1 - 7, 0);
            this.getFontRenderer().drawString(s, l1 - 1, i1 - 7, 0);
            this.getFontRenderer().drawString(s, l1, i1 + 1 - 7, 0);
            this.getFontRenderer().drawString(s, l1, i1 - 1 - 7, 0);
            this.getFontRenderer().drawString(s, l1, i1 - 7, k1);
            this.mc.mcProfiler.endSection();
        }
    }

    public void func_181551_a(final ScaledResolution p_181551_1_) {
        this.mc.mcProfiler.startSection("selectedItemName");

        if (this.remainingHighlightTicks > 0 && this.highlightingItemStack != null) {
            String s = this.highlightingItemStack.getDisplayName();

            if (this.highlightingItemStack.hasDisplayName()) {
                s = EnumChatFormatting.ITALIC + s;
            }

            final int i = (p_181551_1_.getScaledWidth() - this.getFontRenderer().width(s)) / 2;
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
                this.getFontRenderer().drawStringWithShadow(s, (float) i, (float) j, 16777215 + (k << 24));
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }
        }

        this.mc.mcProfiler.endSection();
    }

    public void renderDemo(final ScaledResolution p_175185_1_) {
        this.mc.mcProfiler.startSection("demo");
        final String s;

        if (this.mc.theWorld.getTotalWorldTime() >= 120500L) {
            s = I18n.format("demo.demoExpired");
        } else {
            s = I18n.format("demo.remainingTime", StringUtils.ticksToElapsedTime((int) (120500L - this.mc.theWorld.getTotalWorldTime())));
        }

        final int i = this.getFontRenderer().width(s);
        this.getFontRenderer().drawStringWithShadow(s, (float) (p_175185_1_.getScaledWidth() - i - 10), 5.0F, 16777215);
        this.mc.mcProfiler.endSection();
    }

    public boolean showCrosshair() {
        if (this.mc.gameSettings.showDebugInfo && !this.mc.thePlayer.hasReducedDebug() && !this.mc.gameSettings.reducedDebugInfo) {
            return false;
        } else if (this.mc.playerController.isSpectator()) {
            if (this.mc.pointedEntity != null) {
                return true;
            } else {
                if (this.mc.objectMouseOver != null && this.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                    final BlockPos blockpos = this.mc.objectMouseOver.getBlockPos();

                    return this.mc.theWorld.getTileEntity(blockpos) instanceof IInventory;
                }

                return false;
            }
        } else {
            return true;
        }
    }

    private void renderPlayerStats(final ScaledResolution p_180477_1_) {
        if (this.mc.getRenderViewEntity() instanceof EntityPlayer) {
            final EntityPlayer entityplayer = (EntityPlayer) this.mc.getRenderViewEntity();
            final int i = MathHelper.ceiling_float_int(entityplayer.getHealth());
            final boolean flag = this.healthUpdateCounter > (long) this.updateCounter && (this.healthUpdateCounter - (long) this.updateCounter) / 3L % 2L == 1L;

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
            final int j = this.lastPlayerHealth;
            this.rand.setSeed(this.updateCounter * 312871L);
            final boolean flag1 = false;
            final FoodStats foodstats = entityplayer.getFoodStats();
            final int k = foodstats.getFoodLevel();
            final IAttributeInstance iattributeinstance = entityplayer.getEntityAttribute(SharedMonsterAttributes.maxHealth);
            final int i1 = p_180477_1_.getScaledWidth() / 2 - 91;
            final int j1 = p_180477_1_.getScaledWidth() / 2 + 91;
            final int k1 = p_180477_1_.getScaledHeight() - 39;
            final float f = (float) iattributeinstance.getAttributeValue();
            final float f1 = entityplayer.getAbsorptionAmount();
            final int l1 = MathHelper.ceiling_float_int((f + f1) / 2.0F / 10.0F);
            final int i2 = Math.max(10 - (l1 - 2), 3);
            final int j2 = k1 - (l1 - 1) * i2 - 10;
            float f2 = f1;
            final int k2 = entityplayer.getTotalArmorValue();
            int l2 = -1;

            if (entityplayer.isPotionActive(Potion.regeneration)) {
                l2 = this.updateCounter % MathHelper.ceiling_float_int(f + 5.0F);
            }

            this.mc.mcProfiler.startSection("armor");

            for (int i3 = 0; i3 < 10; ++i3) {
                if (k2 > 0) {
                    final int j3 = i1 + i3 * 8;

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

            for (int i6 = MathHelper.ceiling_float_int((f + f1) / 2.0F) - 1; i6 >= 0; --i6) {
                int j6 = 16;

                if (entityplayer.isPotionActive(Potion.poison)) {
                    j6 += 36;
                } else if (entityplayer.isPotionActive(Potion.wither)) {
                    j6 += 72;
                }

                int k3 = 0;

                if (flag) {
                    k3 = 1;
                }

                final int l3 = MathHelper.ceiling_float_int((float) (i6 + 1) / 10.0F) - 1;
                final int i4 = i1 + i6 % 10 * 8;
                int j4 = k1 - l3 * i2;

                if (i <= 4) {
                    j4 += this.rand.nextInt(2);
                }

                if (i6 == l2) {
                    j4 -= 2;
                }

                int k4 = 0;

                if (entityplayer.worldObj.getWorldInfo().isHardcoreModeEnabled()) {
                    k4 = 5;
                }

                this.drawTexturedModalRect(i4, j4, 16 + k3 * 9, 9 * k4, 9, 9);

                if (flag) {
                    if (i6 * 2 + 1 < j) {
                        this.drawTexturedModalRect(i4, j4, j6 + 54, 9 * k4, 9, 9);
                    }

                    if (i6 * 2 + 1 == j) {
                        this.drawTexturedModalRect(i4, j4, j6 + 63, 9 * k4, 9, 9);
                    }
                }

                if (f2 <= 0.0F) {
                    if (i6 * 2 + 1 < i) {
                        this.drawTexturedModalRect(i4, j4, j6 + 36, 9 * k4, 9, 9);
                    }

                    if (i6 * 2 + 1 == i) {
                        this.drawTexturedModalRect(i4, j4, j6 + 45, 9 * k4, 9, 9);
                    }
                } else {
                    if (f2 == f1 && f1 % 2.0F == 1.0F) {
                        this.drawTexturedModalRect(i4, j4, j6 + 153, 9 * k4, 9, 9);
                    } else {
                        this.drawTexturedModalRect(i4, j4, j6 + 144, 9 * k4, 9, 9);
                    }

                    f2 -= 2.0F;
                }
            }

            final Entity entity = entityplayer.ridingEntity;

            if (entity == null) {
                this.mc.mcProfiler.endStartSection("food");

                for (int k6 = 0; k6 < 10; ++k6) {
                    int j7 = k1;
                    int l7 = 16;
                    int k8 = 0;

                    if (entityplayer.isPotionActive(Potion.hunger)) {
                        l7 += 36;
                        k8 = 13;
                    }

                    if (entityplayer.getFoodStats().getSaturationLevel() <= 0.0F && this.updateCounter % (k * 3 + 1) == 0) {
                        j7 = k1 + (this.rand.nextInt(3) - 1);
                    }

                    final int j9 = j1 - k6 * 8 - 9;
                    this.drawTexturedModalRect(j9, j7, 16 + k8 * 9, 27, 9, 9);

                    if (k6 * 2 + 1 < k) {
                        this.drawTexturedModalRect(j9, j7, l7 + 36, 27, 9, 9);
                    }

                    if (k6 * 2 + 1 == k) {
                        this.drawTexturedModalRect(j9, j7, l7 + 45, 27, 9, 9);
                    }
                }

                Client.INSTANCE.getEventBus().handle(new RenderHungerEvent(p_180477_1_));
            } else if (entity instanceof EntityLivingBase) {
                this.mc.mcProfiler.endStartSection("mountHealth");
                final EntityLivingBase entitylivingbase = (EntityLivingBase) entity;
                final int i7 = (int) Math.ceil(entitylivingbase.getHealth());
                final float f3 = entitylivingbase.getMaxHealth();
                int j8 = (int) (f3 + 0.5F) / 2;

                if (j8 > 30) {
                    j8 = 30;
                }

                int i9 = k1;

                for (int k9 = 0; j8 > 0; k9 += 20) {
                    final int l4 = Math.min(j8, 10);
                    j8 -= l4;

                    for (int i5 = 0; i5 < l4; ++i5) {
                        final int j5 = 52;

                        final int l5 = j1 - i5 * 8 - 9;
                        this.drawTexturedModalRect(l5, i9, j5, 9, 9, 9);

                        if (i5 * 2 + 1 + k9 < i7) {
                            this.drawTexturedModalRect(l5, i9, j5 + 36, 9, 9, 9);
                        }

                        if (i5 * 2 + 1 + k9 == i7) {
                            this.drawTexturedModalRect(l5, i9, j5 + 45, 9, 9, 9);
                        }
                    }

                    i9 -= 10;
                }
            }

            this.mc.mcProfiler.endStartSection("air");

            if (entityplayer.isInsideOfMaterial(Material.water)) {
                final int l6 = this.mc.thePlayer.getAir();
                final int k7 = MathHelper.ceiling_double_int((double) (l6 - 2) * 10.0D / 300.0D);
                final int i8 = MathHelper.ceiling_double_int((double) l6 * 10.0D / 300.0D) - k7;

                for (int l8 = 0; l8 < k7 + i8; ++l8) {
                    if (l8 < k7) {
                        this.drawTexturedModalRect(j1 - l8 * 8 - 9, j2, 16, 18, 9, 9);
                    } else {
                        this.drawTexturedModalRect(j1 - l8 * 8 - 9, j2, 25, 18, 9, 9);
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
            final ScaledResolution scaledresolution = mc.scaledResolution;
            final int i = scaledresolution.getScaledWidth();
            final int j = 182;
            final int k = i / 2 - j / 2;
            final int l = (int) (BossStatus.healthScale * (float) (j + 1));
            final int i1 = 12;
            this.drawTexturedModalRect(k, i1, 0, 74, j, 5);
            this.drawTexturedModalRect(k, i1, 0, 74, j, 5);

            if (l > 0) {
                this.drawTexturedModalRect(k, i1, 0, 79, l, 5);
            }

            final String s = BossStatus.bossName;
            this.getFontRenderer().drawStringWithShadow(s, (float) (i / 2 - this.getFontRenderer().width(s) / 2), (float) (i1 - 10), 16777215);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(icons);
        }
    }

    private void renderPumpkinOverlay(final ScaledResolution p_180476_1_) {
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableAlpha();
        this.mc.getTextureManager().bindTexture(pumpkinBlurTexPath);
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(0.0D, p_180476_1_.getScaledHeight(), -90.0D).tex(0.0D, 1.0D).endVertex();
        worldrenderer.pos(p_180476_1_.getScaledWidth(), p_180476_1_.getScaledHeight(), -90.0D).tex(1.0D, 1.0D).endVertex();
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
    private void renderVignette(float p_180480_1_, final ScaledResolution p_180480_2_) {
        if (!Config.isVignetteEnabled()) {
            GlStateManager.enableDepth();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        } else {
            p_180480_1_ = 1.0F - p_180480_1_;
            p_180480_1_ = MathHelper.clamp_float(p_180480_1_, 0.0F, 1.0F);
            final WorldBorder worldborder = this.mc.theWorld.getWorldBorder();
            float f = (float) worldborder.getClosestDistance(this.mc.thePlayer);
            final double d0 = Math.min(worldborder.getResizeSpeed() * (double) worldborder.getWarningTime() * 1000.0D, Math.abs(worldborder.getTargetSize() - worldborder.getDiameter()));
            final double d1 = Math.max(worldborder.getWarningDistance(), d0);

            if ((double) f < d1) {
                f = 1.0F - (float) ((double) f / d1);
            } else {
                f = 0.0F;
            }

            this.prevVignetteBrightness = (float) ((double) this.prevVignetteBrightness + (double) (p_180480_1_ - this.prevVignetteBrightness) * 0.01D);
            GlStateManager.disableDepth();
            GlStateManager.depthMask(false);
            GlStateManager.tryBlendFuncSeparate(0, 769, 1, 0);

            if (f > 0.0F) {
                GlStateManager.color(0.0F, f, f, 1.0F);
            } else {
                GlStateManager.color(this.prevVignetteBrightness, this.prevVignetteBrightness, this.prevVignetteBrightness, 1.0F);
            }

            this.mc.getTextureManager().bindTexture(vignetteTexPath);
            final Tessellator tessellator = Tessellator.getInstance();
            final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
            worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
            worldrenderer.pos(0.0D, p_180480_2_.getScaledHeight(), -90.0D).tex(0.0D, 1.0D).endVertex();
            worldrenderer.pos(p_180480_2_.getScaledWidth(), p_180480_2_.getScaledHeight(), -90.0D).tex(1.0D, 1.0D).endVertex();
            worldrenderer.pos(p_180480_2_.getScaledWidth(), 0.0D, -90.0D).tex(1.0D, 0.0D).endVertex();
            worldrenderer.pos(0.0D, 0.0D, -90.0D).tex(0.0D, 0.0D).endVertex();
            tessellator.draw();
            GlStateManager.depthMask(true);
            GlStateManager.enableDepth();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        }
    }

    private void func_180474_b(float p_180474_1_, final ScaledResolution p_180474_2_) {
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
        final TextureAtlasSprite textureatlassprite = this.mc.getBlockRendererDispatcher().getBlockModelShapes().getTexture(Blocks.portal.getDefaultState());
        final float f = textureatlassprite.getMinU();
        final float f1 = textureatlassprite.getMinV();
        final float f2 = textureatlassprite.getMaxU();
        final float f3 = textureatlassprite.getMaxV();
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
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

    private void renderHotbarItem(final int index, final int xPos, final int yPos, final float partialTicks, final EntityPlayer p_175184_5_) {
        final ItemStack itemstack = p_175184_5_.inventory.mainInventory[index];

        if (itemstack != null) {
            final float f = (float) itemstack.animationsToGo - partialTicks;

            if (f > 0.0F) {
                GlStateManager.pushMatrix();
                final float f1 = 1.0F + f / 5.0F;
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

        if (this.field_175195_w > 0) {
            --this.field_175195_w;

            if (this.field_175195_w <= 0) {
                this.field_175201_x = "";
                this.field_175200_y = "";
            }
        }

        ++this.updateCounter;

        if (this.mc.thePlayer != null) {
            final ItemStack itemstack = this.mc.thePlayer.inventory.getCurrentItem();

            if (itemstack == null) {
                this.remainingHighlightTicks = 0;
            } else if (this.highlightingItemStack != null && itemstack.getItem() == this.highlightingItemStack.getItem() && ItemStack.areItemStackTagsEqual(itemstack, this.highlightingItemStack) && (itemstack.isItemStackDamageable() || itemstack.getMetadata() == this.highlightingItemStack.getMetadata())) {
                if (this.remainingHighlightTicks > 0) {
                    --this.remainingHighlightTicks;
                }
            } else {
                this.remainingHighlightTicks = 40;
            }

            this.highlightingItemStack = itemstack;
        }
    }

    public void setRecordPlayingMessage(final String p_73833_1_) {
        this.setRecordPlaying(I18n.format("record.nowPlaying", p_73833_1_), true);
    }

    public void setRecordPlaying(final String p_110326_1_, final boolean p_110326_2_) {
        this.recordPlaying = p_110326_1_;
        this.recordPlayingUpFor = 60;
        this.recordIsPlaying = p_110326_2_;
    }

    public void displayTitle(final String p_175178_1_, final String p_175178_2_, final int p_175178_3_, final int p_175178_4_, final int p_175178_5_) {
        if (p_175178_1_ == null && p_175178_2_ == null && p_175178_3_ < 0 && p_175178_4_ < 0 && p_175178_5_ < 0) {
            this.field_175201_x = "";
            this.field_175200_y = "";
            this.field_175195_w = 0;
        } else if (p_175178_1_ != null) {
            this.field_175201_x = p_175178_1_;
            this.field_175195_w = this.field_175199_z + this.field_175192_A + this.field_175193_B;
        } else if (p_175178_2_ != null) {
            this.field_175200_y = p_175178_2_;
        } else {
            if (p_175178_3_ >= 0) {
                this.field_175199_z = p_175178_3_;
            }

            if (p_175178_4_ >= 0) {
                this.field_175192_A = p_175178_4_;
            }

            if (p_175178_5_ >= 0) {
                this.field_175193_B = p_175178_5_;
            }

            if (this.field_175195_w > 0) {
                this.field_175195_w = this.field_175199_z + this.field_175192_A + this.field_175193_B;
            }
        }
    }

    public void setRecordPlaying(final IChatComponent p_175188_1_, final boolean p_175188_2_) {
        this.setRecordPlaying(p_175188_1_.getUnformattedText(), p_175188_2_);
    }

    /**
     * returns a pointer to the persistant Chat GUI, containing all previous chat messages and such
     */
    public GuiNewChat getChatGUI() {
        return this.persistantChatGUI;
    }

    public int getUpdateCounter() {
        return this.updateCounter;
    }

    public FontRenderer getFontRenderer() {
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
}
