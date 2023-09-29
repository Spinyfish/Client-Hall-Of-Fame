package v4n1ty.module.render;

import java.awt.Color;
import java.util.Objects;

import javax.vecmath.Vector2d;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.MathHelper;
import v4n1ty.V4n1ty;
import v4n1ty.module.Category;
import v4n1ty.module.Module;
import v4n1ty.module.ModuleManager;
import v4n1ty.module.combat.KillAura;
import v4n1ty.utils.render.RoundedUtils;

public class TargetHud extends Module {
    private boolean show;
    private double healthBarWidth;
    private String enemyNickname;
    private double enemyHP;
    private double enemyDistance;
    private EntityPlayer entityPlayer;
    private Entity entity;


    public TargetHud() {
        super("TargetHud", 0, Category.RENDER);
    }

    public void onUpdate() {
        if (this.isToggled()) {
            KillAura killaura = (KillAura) V4n1ty.instance.moduleManager.getModule(KillAura.class);
            if (killaura.target != null) {
                if (killaura.target instanceof EntityPlayer) {
                    entityPlayer = (EntityPlayer) killaura.target;
                    enemyNickname = entityPlayer.getName();
                    enemyHP = entityPlayer.getHealth();
                    enemyDistance = entityPlayer.getDistanceToEntity(Minecraft.getMinecraft().thePlayer);
                    show = true;
                } else {
                    show = false;
                }
            } else {
                show = false;
            }
        }
    }

    public void onRender() {
        if (show && Minecraft.getMinecraft().theWorld != null && Minecraft.getMinecraft().thePlayer != null) {
            double posX = 200;
            double posY = -250;

            ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());

            final float scaledWidth = sr.getScaledWidth();

            final float x = (float) (scaledWidth / 2.0f - posX);
            final float y = (float) (scaledWidth / 2.0f + posY);

            final float health = Math.round(enemyHP);
            double hpPercentage = health / 20;

            hpPercentage = MathHelper.clamp_double(hpPercentage, 0, 1);
            final double hpWidth = 97.0 * hpPercentage;

            final String healthStr = String.valueOf(Math.round(enemyHP));

            float fadeOffset = 0;

            RoundedUtils.drawRoundedRect((float)(sr.getScaledWidth() / 2 - 78), (float)(sr.getScaledHeight() / 2 + 15), (float)(sr.getScaledWidth() / 2 + 68), 425.0f, 10.0f, new Color(35, 35, 35, 255).getRGB());
            Gui.drawRect((int) (x + 166.0f), (int) (y + 6.0f), (int) (x + 263.0f), (int) (y + 15.0f), new Color(40, 40, 40, 255).getRGB());
            Gui.drawRect((int) (x + 166.0f), (int) (y + 6.0f), (int) (x + 166.0f + this.healthBarWidth), (int) (y + 15.0f), V4n1ty.getHudColor(fadeOffset).getRGB());
            fadeOffset -= 3;
            Gui.drawRect((int) (x + 166.0f), (int) (y + 6.0f), (int) (x + 166.0f + hpWidth), (int) (y + 15.0f), V4n1ty.getHudColor(fadeOffset).getRGB());
            fadeOffset -= 3;
            Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(healthStr, x + 128.0f + 46.0f - Minecraft.getMinecraft().fontRendererObj.getStringWidth(healthStr) / 2.0f, y + 19.5f, -1);
            Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow("\u2764", x + 128.0f + 46.0f + Minecraft.getMinecraft().fontRendererObj.getStringWidth(healthStr), y + 19.5f, V4n1ty.getHudColor(fadeOffset).getRGB());
            fadeOffset -= 3;
            Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(entityPlayer.getName(), x + 167, y - 5.0f, -1);
            if(entityPlayer.isInvisible()) {
                Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow("Invisible", x + 200, y + 19.5f, -1);
            }
            try {;
                drawHead(Objects.requireNonNull(Minecraft.getMinecraft().getNetHandler()).getPlayerInfo(entityPlayer.getUniqueID()).getLocationSkin(), (int) (x + 127), (int) (y - 8));
            } catch (Exception ignored) {}
        }
    }

    public void onDisable() {
        show = false;
    }

    public void drawHead(ResourceLocation skin, int width, int height) {
        GL11.glColor4f(1, 1, 1, 1);
        Minecraft.getMinecraft().getTextureManager().bindTexture(skin);
        Gui.drawScaledCustomSizeModalRect(width, height, 8, 8, 8, 8, 36, 36, 64, 64);
    }
}