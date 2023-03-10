/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.Subscribe
 *  org.lwjgl.input.Mouse
 *  org.lwjgl.opengl.GL11
 */
package lodomir.dev.modules.impl.render;

import com.google.common.eventbus.Subscribe;
import java.awt.Color;
import java.awt.Toolkit;
import java.text.DecimalFormat;
import java.util.ArrayList;
import lodomir.dev.November;
import lodomir.dev.event.impl.render.EventRender2D;
import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;
import lodomir.dev.modules.impl.combat.KillAura;
import lodomir.dev.modules.impl.render.Interface;
import lodomir.dev.settings.impl.ModeSetting;
import lodomir.dev.settings.impl.NumberSetting;
import lodomir.dev.ui.font.TTFFontRenderer;
import lodomir.dev.utils.math.MathUtils;
import lodomir.dev.utils.math.TimeUtils;
import lodomir.dev.utils.render.ColorUtils;
import lodomir.dev.utils.render.RenderUtils;
import lodomir.dev.utils.render.StencilUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class TargetHUD
extends Module {
    public ModeSetting mode = new ModeSetting("Mode", "November", "November", "Astolfo", "Novoline");
    public NumberSetting x;
    public NumberSetting y;
    private double animHealth;
    public boolean dragging;
    private int draggingX;
    private int draggingY;
    public TimeUtils timer;

    public TargetHUD() {
        super("TargetHUD", 0, Category.RENDER);
        this.x = new NumberSetting("Pos X", 0.0, Toolkit.getDefaultToolkit().getScreenSize().width / 2, 181.0, 1.0);
        this.y = new NumberSetting("Pos Y", 0.0, Toolkit.getDefaultToolkit().getScreenSize().width / 2, 32.0, 1.0);
        this.animHealth = Interface.getColorInt();
        this.timer = new TimeUtils();
        this.addSetting(this.mode);
        this.addSetting(this.x);
        this.addSetting(this.y);
    }

    public static void renderPlayerModelTexture(double x, double y, float u, float v, int uWidth, int vHeight, int width, int height, float tileWidth, float tileHeight, AbstractClientPlayer target) {
        ResourceLocation skin = target.getLocationSkin();
        Minecraft.getMinecraft().getTextureManager().bindTexture(skin);
        GL11.glEnable((int)3042);
        Gui.drawScaledCustomSizeModalRect((int)x, (int)y, u, v, uWidth, vHeight, width, height, tileWidth, tileHeight);
        GL11.glDisable((int)3042);
    }

    @Override
    @Subscribe
    public void on2D(EventRender2D event) {
        this.draw();
    }

    public void mouseClicked(double mouseX, double mouseY, double x, double y, double width, double height) {
        if (Mouse.isButtonDown((int)0) && RenderUtils.isHovered(mouseX, mouseY, x, y, width, height)) {
            this.dragging = true;
        }
    }

    public void mouseReleased(double mouseX, double mouseY, double x, double y, double width, double height) {
        if (Mouse.getEventButton() == Integer.parseInt(null) || !RenderUtils.isHovered(mouseX, mouseY, x, y, width, height)) {
            this.dragging = false;
        }
    }

    public void draw() {
        int darker = Interface.color.isMode("Rainbow") ? ColorUtils.rainbow(4, 1.0f, 1.0).getRGB() : (Interface.color.isMode("Blend") ? ColorUtils.getColorSwitch(new Color(Interface.red.getValueInt(), Interface.green.getValueInt(), Interface.blue.getValueInt()).darker(), new Color(Interface.red2.getValueInt(), Interface.green2.getValueInt(), Interface.blue2.getValueInt()).darker(), 3000.0f, 100, 50L, 4.0).getRGB() : (Interface.color.isMode("Dynamic") ? ColorUtils.fade(new Color(Interface.red.getValueInt(), Interface.green.getValueInt(), Interface.blue.getValueInt()), 100, 100).darker().getRGB() : new Color(Interface.red.getValueInt(), Interface.green.getValueInt(), Interface.blue.getValueInt()).darker().getRGB()));
        int color = Interface.getColorInt();
        EntityLivingBase target = (EntityLivingBase)KillAura.target;
        if (November.INSTANCE.getModuleManager().getModule("KillAura").isEnabled() && target == null || !(target instanceof EntityPlayer) || TargetHUD.mc.theWorld.getEntityByID(target.getEntityId()) == null || TargetHUD.mc.theWorld.getEntityByID(target.getEntityId()).getDistanceSqToEntity(TargetHUD.mc.thePlayer) > 100.0) {
            return;
        }
        switch (this.mode.getMode()) {
            case "November": {
                TTFFontRenderer cfr = November.INSTANCE.fm.getFont("SFUI BOLD 20");
                TTFFontRenderer font = November.INSTANCE.fm.getFont("SFUI BOLD 18");
                GlStateManager.pushMatrix();
                double posX = (float)TargetHUD.mc.displayWidth / (float)(TargetHUD.mc.gameSettings.guiScale * 2) + (float)this.x.getValueInt();
                double posY = (float)TargetHUD.mc.displayHeight / (float)(TargetHUD.mc.gameSettings.guiScale * 2) + (float)(this.y.getValueInt() / 2);
                GlStateManager.translate(posX - 660.0, posY - 160.0 - 90.0, 0.0);
                RenderUtils.drawRoundedRect(4.0, -4.0, 145.0, 40.5, 2.0, new Color(40, 40, 40, 40).getRGB());
                cfr.drawStringWithShadow(target.getName(), 45.0f, 0.3f, -1);
                float health = target.getHealth();
                float progress = health > 20.0f ? 20.0f : health / ((EntityPlayer)target).getMaxHealth();
                NetworkPlayerInfo networkPlayerInfo = mc.getNetHandler().getPlayerInfo(target.getUniqueID());
                DecimalFormat df = new DecimalFormat("#");
                double cockWidth = 0.0;
                cockWidth = MathUtils.round(cockWidth, 5);
                if (cockWidth < 50.0) {
                    cockWidth = 50.0;
                }
                double healthBarPos = cockWidth * (double)progress;
                RenderUtils.drawRoundedRect(46.0, 26.0, 100.0, 7.0, 0.0, new Color(0, 0, 0, 60).getRGB());
                RenderUtils.drawRoundedRect(46.0, 26.0, healthBarPos * 2.0, 7.0, 0.0, color);
                GL11.glScalef((float)1.0f, (float)1.0f, (float)1.0f);
                font.drawStringWithShadow(df.format(100.0f * target.getHealth() / target.getMaxHealth()) + "%", cockWidth * (target.getHealth() > 20.0f ? 1.7 : 1.65), 25.0, Color.WHITE.getRGB());
                GL11.glScalef((float)1.0f, (float)1.0f, (float)1.0f);
                if (target != null) {
                    this.drawEquipment(32, 9);
                }
                TargetHUD.renderPlayerModelTexture(7.0, -1.0, 3.0f, 3.0f, 3, 3, 35, 35, 24.0f, 24.0f, (AbstractClientPlayer)target);
                GlStateManager.scale(1.1, 1.1, 1.1);
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                GlStateManager.enableAlpha();
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                GlStateManager.disableAlpha();
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
                break;
            }
            case "Astolfo": {
                double posX = (float)TargetHUD.mc.displayWidth / (float)(TargetHUD.mc.gameSettings.guiScale * 2) + (float)this.x.getValueInt();
                double posY = (float)TargetHUD.mc.displayHeight / (float)(TargetHUD.mc.gameSettings.guiScale * 2) + (float)(this.y.getValueInt() / 2);
                GL11.glPushMatrix();
                GlStateManager.translate(posX - 660.0, posY - 160.0 - 90.0, 0.0);
                Gui.drawRect(-32.5, 0.0, 112.5, 50.0, new Color(0, 0, 0, 120).getRGB());
                GL11.glScalef((float)2.0f, (float)2.0f, (float)2.0f);
                DecimalFormat format = new DecimalFormat("#.#");
                String hp = format.format(target.getHealth() / 2.0f);
                TargetHUD.mc.fontRendererObj.drawStringWithShadow(hp + " \u2764", 0.0f, 8.35f, color);
                GL11.glScalef((float)0.5f, (float)0.5f, (float)0.5f);
                TargetHUD.mc.fontRendererObj.drawStringWithShadow(target.getName(), 0.0f, 4.0f, -1);
                GuiInventory.drawEntityOnScreen(-15, 45, 20, target.rotationYaw, target.rotationPitch, target);
                Gui.drawRect(0.0, 37.5, 107.5, 45.5, new Color(66, 66, 66, 255).getRGB());
                this.animHealth += ((double)target.getHealth() - this.animHealth) * 0.1;
                if (this.animHealth < 0.0 || this.animHealth > (double)target.getHealth()) {
                    this.animHealth = target.getHealth();
                } else {
                    double cockWidth = 0.0;
                    if ((cockWidth = MathUtils.round(cockWidth, 5)) < 50.0) {
                        cockWidth = 50.0;
                    }
                    double healthBarPos = cockWidth * (double)target.getHealth() / (double)target.getMaxHealth();
                    Gui.drawRect(0.0, 37.5, (int)(healthBarPos * 2.0 + 5.0 + 2.0), 45.5, darker);
                    Gui.drawRect(0.0, 37.5, (int)(healthBarPos * 2.0 + 5.0), 45.5, color);
                }
                GL11.glPopMatrix();
                break;
            }
            case "Novoline": {
                FontRenderer fr = TargetHUD.mc.fontRendererObj;
                GlStateManager.pushMatrix();
                double posX = (float)TargetHUD.mc.displayWidth / (float)(TargetHUD.mc.gameSettings.guiScale * 2) + (float)this.x.getValueInt();
                double posY = (float)TargetHUD.mc.displayHeight / (float)(TargetHUD.mc.gameSettings.guiScale * 2) + (float)(this.y.getValueInt() / 2);
                GlStateManager.translate(posX - 660.0, posY - 160.0 - 90.0, 0.0);
                RenderUtils.drawBorderedRect(6.0, -2.0, 145.0, 35.0, 1.0, new Color(50, 50, 50).getRGB(), new Color(40, 40, 40).getRGB());
                fr.drawStringWithShadow(target.getName(), 45.0f, 0.3f, -1);
                float health = target.getHealth();
                float progress = health / ((EntityPlayer)target).getMaxHealth();
                NetworkPlayerInfo networkPlayerInfo = mc.getNetHandler().getPlayerInfo(target.getUniqueID());
                DecimalFormat decimalFormat = new DecimalFormat("#.#");
                double cockWidth = 0.0;
                cockWidth = MathUtils.round(cockWidth, 5);
                if (cockWidth < 50.0) {
                    cockWidth = 50.0;
                }
                double healthBarPos = cockWidth * (double)progress;
                RenderUtils.drawRoundedRect(46.0, 15.0, 90.0, 7.0, 0.0, new Color(50, 50, 50).getRGB());
                RenderUtils.drawRoundedRect(46.0, 15.0, healthBarPos * 1.9, 7.0, 0.0, color);
                fr.drawStringWithShadow(decimalFormat.format(100.0f * target.getHealth() / target.getMaxHealth()) + "%", (float)(cockWidth * (target.getHealth() > 20.0f ? 1.7 : 1.65)), 15.0f, Color.WHITE.getRGB());
                TargetHUD.renderPlayerModelTexture(7.0, -1.0, 3.0f, 3.0f, 3, 3, 35, 35, 24.0f, 24.0f, (AbstractClientPlayer)target);
                GlStateManager.scale(1.1, 1.1, 1.1);
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                GlStateManager.enableAlpha();
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                GlStateManager.disableAlpha();
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
                break;
            }
            case "Rise": {
                GlStateManager.pushMatrix();
                TTFFontRenderer smallcfr = November.INSTANCE.fm.getFont("COMFORTAA 16");
                TTFFontRenderer cfr = November.INSTANCE.fm.getFont("COMFORTAA 32");
                double posX = (float)TargetHUD.mc.displayWidth / (float)(TargetHUD.mc.gameSettings.guiScale * 2) + (float)this.x.getValueInt() * 1.5f;
                double posY = (float)TargetHUD.mc.displayHeight / (float)(TargetHUD.mc.gameSettings.guiScale * 2) + (float)this.y.getValueInt() * 1.5f;
                GlStateManager.translate(posX - 660.0, posY - 160.0 - 90.0, 0.0);
                RenderUtils.drawRoundedRect(4.0, -4.0, 145.0, 40.5, 5.0, -1879048192);
                cfr.drawStringWithShadow(target.getName(), 45.0f, 0.3f, -1);
                float health = target.getHealth();
                float progress = health / ((EntityPlayer)target).getMaxHealth();
                NetworkPlayerInfo networkPlayerInfo = mc.getNetHandler().getPlayerInfo(target.getUniqueID());
                DecimalFormat decimalFormat = new DecimalFormat("#");
                double cockWidth = 0.0;
                cockWidth = MathUtils.round(cockWidth, 5);
                if (cockWidth < 50.0) {
                    cockWidth = 50.0;
                }
                double healthBarPos = cockWidth * (double)progress;
                RenderUtils.drawRoundedRect(46.0, 26.0, 95.0, 5.5, 0.0, new Color(24, 24, 24, 128).getRGB());
                RenderUtils.drawRoundedRect(46.0, 26.0, healthBarPos * 1.95, 5.5, 0.0, color);
                smallcfr.drawCenteredString(decimalFormat.format(target.getHealth()), (float)(healthBarPos * 2.8), 26.0f, Color.WHITE.getRGB());
                if (target instanceof AbstractClientPlayer) {
                    StencilUtils.write(false);
                    RenderUtils.drawCircle(posX + 38.0 + 3.0, health - 34.0f + 2.5f, 30.0f, Color.BLACK.getRGB());
                    StencilUtils.erase(true);
                    double offset = -(((AbstractClientPlayer)target).hurtTime * 23);
                    RenderUtils.color(new Color(255, (int)(255.0 + offset), (int)(255.0 + offset)));
                    TargetHUD.renderPlayerModelTexture(7.0, -1.0, 3.0f, 3.0f, 3, 3, 35, 35, 24.0f, 24.0f, (AbstractClientPlayer)target);
                    TargetHUD.renderPlayerModelTexture(7.0, -1.0, 3.0f, 3.0f, 3, 3, 35, 35, 24.0f, 24.0f, (AbstractClientPlayer)target);
                    RenderUtils.color(Color.WHITE);
                    StencilUtils.dispose();
                }
                GlStateManager.scale(1.1, 1.1, 1.1);
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                GlStateManager.enableAlpha();
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                GlStateManager.disableAlpha();
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
                break;
            }
        }
    }

    private void drawEquipment(int x, int y) {
        Entity target = KillAura.target;
        if (target == null || !(target instanceof EntityPlayer)) {
            return;
        }
        GL11.glPushMatrix();
        ArrayList<ItemStack> stuff = new ArrayList<ItemStack>();
        int cock = -2;
        for (int i = 3; i >= 0; --i) {
            ItemStack armor = ((EntityPlayer)target).getCurrentArmor(i);
            if (armor == null) continue;
            stuff.add(armor);
        }
        if (((EntityPlayer)target).getHeldItem() != null) {
            stuff.add(((EntityPlayer)target).getHeldItem());
        }
        for (ItemStack item : stuff) {
            if (Minecraft.getMinecraft().theWorld != null) {
                RenderHelper.enableGUIStandardItemLighting();
                cock += 16;
            }
            GlStateManager.pushMatrix();
            GlStateManager.disableAlpha();
            GlStateManager.clear(256);
            GlStateManager.enableBlend();
            Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(item, cock + x, y);
            Minecraft.getMinecraft().getRenderItem().renderItemOverlays(Minecraft.getMinecraft().fontRendererObj, item, cock + x, y);
            GlStateManager.disableBlend();
            GlStateManager.scale(0.5, 0.5, 0.5);
            GlStateManager.disableDepth();
            GlStateManager.disableLighting();
            GlStateManager.enableDepth();
            GlStateManager.scale(2.0f, 2.0f, 2.0f);
            GlStateManager.enableAlpha();
            GlStateManager.popMatrix();
            item.getEnchantmentTagList();
        }
        GL11.glPopMatrix();
    }
}

