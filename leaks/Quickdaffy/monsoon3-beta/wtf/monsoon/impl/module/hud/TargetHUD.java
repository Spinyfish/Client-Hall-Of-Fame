/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.surge.animation.Animation
 *  me.surge.animation.Easing
 *  org.lwjgl.BufferUtils
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.util.glu.GLU
 *  org.lwjgl.util.vector.Vector3f
 *  org.lwjgl.util.vector.Vector4f
 */
package wtf.monsoon.impl.module.hud;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.awt.Color;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import me.surge.animation.Animation;
import me.surge.animation.Easing;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.HUDModule;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.misc.Timer;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.DrawUtil;
import wtf.monsoon.api.util.render.RenderUtil;
import wtf.monsoon.api.util.render.RoundedUtils;
import wtf.monsoon.impl.event.EventRender3D;
import wtf.monsoon.impl.module.combat.Aura;
import wtf.monsoon.impl.module.hud.HUD;

public class TargetHUD
extends HUDModule {
    AbstractClientPlayer target;
    AbstractClientPlayer oldTarget;
    float health = 20.0f;
    float absorption = 0.0f;
    Setting<TargetHUDTheme> theme = new Setting<TargetHUDTheme>("Theme", TargetHUDTheme.NEW).describedBy("Them of the TargetHUD");
    Setting<Boolean> followPlayer = new Setting<Boolean>("Follow Player", false).describedBy("Whether to follow the target or not");
    Animation damageAnim = new Animation(() -> Float.valueOf(450.0f), false, () -> Easing.CUBIC_IN);
    Animation absorptionAnim = new Animation(() -> Float.valueOf(400.0f), false, () -> Easing.CUBIC_IN);
    Animation hasAbsorbtion = new Animation(() -> Float.valueOf(200.0f), false, () -> Easing.CUBIC_IN_OUT);
    Animation targetRenderAnimation = new Animation(() -> Float.valueOf(150.0f), false, () -> Easing.CUBIC_IN_OUT);
    private boolean shouldDraw;
    private Timer animTimer = new Timer();
    private final FloatBuffer windowPosition = BufferUtils.createFloatBuffer((int)4);
    private final IntBuffer viewport = GLAllocation.createDirectIntBuffer(16);
    private final FloatBuffer modelMatrix = GLAllocation.createDirectFloatBuffer(16);
    private final FloatBuffer projectionMatrix = GLAllocation.createDirectFloatBuffer(16);
    float[] coords = null;
    float coordX = 0.0f;
    float coordY = 0.0f;
    @EventLink
    public final Listener<EventRender3D> eventRender3D = e -> {
        if (this.target == null) {
            this.coords = null;
            return;
        }
        ScaledResolution sr = new ScaledResolution(this.mc);
        int scaleFactor = sr.getScaleFactor();
        Vec3 vec3 = this.getVec3(this.target);
        float posX = (float)(vec3.x - this.mc.getRenderManager().viewerPosX);
        float posY = (float)(vec3.y - this.mc.getRenderManager().viewerPosY);
        float posZ = (float)(vec3.z - this.mc.getRenderManager().viewerPosZ);
        double halfWidth = (double)this.target.width / 2.0 + (double)0.18f;
        AxisAlignedBB bb = new AxisAlignedBB((double)posX - halfWidth, posY, (double)posZ - halfWidth, (double)posX + halfWidth, (double)(posY + this.target.height) + 0.18, (double)posZ + halfWidth);
        double[][] vectors = new double[][]{{bb.minX, bb.minY, bb.minZ}, {bb.minX, bb.maxY, bb.minZ}, {bb.minX, bb.maxY, bb.maxZ}, {bb.minX, bb.minY, bb.maxZ}, {bb.maxX, bb.minY, bb.minZ}, {bb.maxX, bb.maxY, bb.minZ}, {bb.maxX, bb.maxY, bb.maxZ}, {bb.maxX, bb.minY, bb.maxZ}};
        Vector4f position = new Vector4f(Float.MAX_VALUE, Float.MAX_VALUE, -1.0f, -1.0f);
        for (double[] vec : vectors) {
            Vector3f projection = this.project2D((float)vec[0], (float)vec[1], (float)vec[2], scaleFactor);
            if (projection == null || !(projection.z >= 0.0f) || !(projection.z < 1.0f)) continue;
            position.x = Math.min(position.x, projection.x);
            position.y = Math.min(position.y, projection.y);
            position.z = Math.max(position.z, projection.x);
            position.w = Math.max(position.w, projection.y);
        }
        this.coords = new float[]{position.x, position.z, position.y, position.w};
    };

    public TargetHUD() {
        super("Target HUD", "Displays information about the current target", 400.0f, 400.0f);
    }

    private Vec3 getVec3(EntityLivingBase var0) {
        float timer = this.mc.getTimer().renderPartialTicks;
        double x = var0.lastTickPosX + (var0.posX - var0.lastTickPosX) * (double)timer;
        double y = var0.lastTickPosY + (var0.posY - var0.lastTickPosY) * (double)timer;
        double z = var0.lastTickPosZ + (var0.posZ - var0.lastTickPosZ) * (double)timer;
        return new Vec3(x, y, z);
    }

    private Vector3f project2D(float x, float y, float z, int scaleFactor) {
        GL11.glGetFloat((int)2982, (FloatBuffer)this.modelMatrix);
        GL11.glGetFloat((int)2983, (FloatBuffer)this.projectionMatrix);
        GL11.glGetInteger((int)2978, (IntBuffer)this.viewport);
        if (GLU.gluProject((float)x, (float)y, (float)z, (FloatBuffer)this.modelMatrix, (FloatBuffer)this.projectionMatrix, (IntBuffer)this.viewport, (FloatBuffer)this.windowPosition)) {
            return new Vector3f(this.windowPosition.get(0) / (float)scaleFactor, ((float)this.mc.displayHeight - this.windowPosition.get(1)) / (float)scaleFactor, this.windowPosition.get(2));
        }
        return null;
    }

    void drawSolidOutline() {
        Color cc4;
        Color cc3;
        Color cc2;
        Color cc1;
        Color c4;
        Color c1 = ColorUtil.getClientAccentTheme()[0];
        Color c2 = ColorUtil.getClientAccentTheme()[1];
        Color c3 = ColorUtil.getClientAccentTheme().length > 2 ? ColorUtil.getClientAccentTheme()[2] : ColorUtil.getClientAccentTheme()[0];
        Color color = c4 = ColorUtil.getClientAccentTheme().length > 3 ? ColorUtil.getClientAccentTheme()[3] : ColorUtil.getClientAccentTheme()[1];
        if (ColorUtil.getClientAccentTheme().length > 3) {
            cc1 = c1;
            cc2 = c2;
            cc3 = c3;
            cc4 = c4;
        } else {
            cc1 = ColorUtil.fadeBetween(10, 270, c1, c2);
            cc2 = ColorUtil.fadeBetween(10, 0, c1, c2);
            cc3 = ColorUtil.fadeBetween(10, 180, c1, c2);
            cc4 = ColorUtil.fadeBetween(10, 90, c1, c2);
        }
        RoundedUtils.outline(this.coordX, this.coordY, this.getWidth(), this.getHeight(), 10.0f, 1.0f, 2.0f, cc1, cc2, cc3, cc4);
    }

    void drawShadowOutline() {
        Color cc4;
        Color cc3;
        Color cc2;
        Color cc1;
        Color c4;
        Color c1 = ColorUtil.getClientAccentTheme()[0];
        Color c2 = ColorUtil.getClientAccentTheme()[1];
        Color c3 = ColorUtil.getClientAccentTheme().length > 2 ? ColorUtil.getClientAccentTheme()[2] : ColorUtil.getClientAccentTheme()[0];
        Color color = c4 = ColorUtil.getClientAccentTheme().length > 3 ? ColorUtil.getClientAccentTheme()[3] : ColorUtil.getClientAccentTheme()[1];
        if (ColorUtil.getClientAccentTheme().length > 3) {
            cc1 = c1;
            cc2 = c2;
            cc3 = c3;
            cc4 = c4;
        } else {
            cc1 = ColorUtil.fadeBetween(10, 270, c1, c2);
            cc2 = ColorUtil.fadeBetween(10, 0, c1, c2);
            cc3 = ColorUtil.fadeBetween(10, 180, c1, c2);
            cc4 = ColorUtil.fadeBetween(10, 90, c1, c2);
        }
        RoundedUtils.shadowGradient(this.coordX + 1.5f, this.coordY + 1.5f, this.getWidth() - 3.0f, this.getHeight() - 3.0f, 10.0f, 10.0f, 2.0f, cc1, cc2, cc3, cc4, false);
    }

    void drawNewTargetHUD(AbstractClientPlayer target) {
        RenderUtil.getDefaultHudRenderer(this.coordX, this.coordY, this.getWidth(), this.getHeight());
        float distance = (float)Math.round(this.mc.thePlayer.getDistanceToEntity(target) * 100.0f) / 100.0f;
        Wrapper.getFont().drawString(target.getCommandSenderName(), this.coordX + 28.0f, this.coordY + 4.0f, Color.WHITE, false);
        Wrapper.getFont().drawString(distance + "", this.coordX + 28.0f, this.coordY + 16.0f, Color.WHITE, false);
        GL11.glEnable((int)3042);
        this.mc.getTextureManager().bindTexture(target.getLocationSkin());
        Gui.drawScaledCustomSizeModalRect(this.coordX + 6.0f, this.coordY + 6.0f, 3.0f, 3.0f, 3.0f, 3.0f, 20.0f, 20.0f, 24.0f, 24.0f);
        this.mc.getTextureManager().bindTexture(target.getLocationSkin());
        Gui.drawScaledCustomSizeModalRect(this.coordX + 6.0f, this.coordY + 6.0f, 15.0f, 3.0f, 3.0f, 3.0f, 20.0f, 20.0f, 24.0f, 24.0f);
        GL11.glDisable((int)3042);
        RoundedUtils.round(this.coordX + 6.0f, this.coordY + 6.0f + 20.0f + 4.0f + 7.0f, this.absorption / 20.0f * (this.getWidth() - 12.0f), 4.0f, 1.0f, new Color(250, 218, 82, 255));
        RoundedUtils.gradient(this.coordX + 6.0f, this.coordY + 6.0f + 20.0f + 4.0f, this.health / 20.0f * (this.getWidth() - 12.0f), 4.0f, 1.5f, 1.0f, ColorUtil.fadeBetween(10, 0, ColorUtil.getClientAccentTheme()[0], ColorUtil.getClientAccentTheme()[1]), ColorUtil.fadeBetween(10, 0, ColorUtil.getClientAccentTheme()[0], ColorUtil.getClientAccentTheme()[1]), ColorUtil.fadeBetween(10, 180, ColorUtil.getClientAccentTheme()[0], ColorUtil.getClientAccentTheme()[1]), ColorUtil.fadeBetween(10, 180, ColorUtil.getClientAccentTheme()[0], ColorUtil.getClientAccentTheme()[1]));
    }

    void drawOldTargetHUD(AbstractClientPlayer target) {
        wtf.monsoon.api.util.font.impl.FontRenderer small = Wrapper.getFontUtil().productSansSmaller;
        Gui.drawRect(this.coordX, this.coordY, this.coordX + this.getWidth(), this.coordY + this.getHeight(), 0x70000000);
        int i = 0;
        while ((float)i < this.getWidth() - 2.0f) {
            RenderUtil.drawRect(this.coordX + (float)i + 1.0f, this.coordY + 1.0f, 1.0, 1.0, Wrapper.getModule(HUD.class).getColorForArray(i, i * 2));
            ++i;
        }
        i = 0;
        while ((float)i < this.health / 20.0f * (this.getWidth() - 4.0f - 20.0f - 8.0f)) {
            RenderUtil.drawRect(this.coordX + 4.0f + 20.0f + 4.0f + (float)i, this.coordY + 4.0f + 10.0f + 1.0f, 1.0, 5.0, Wrapper.getModule(HUD.class).getColorForArray(i, i * 2));
            ++i;
        }
        RenderUtil.drawRect(this.coordX + 4.0f + 20.0f + 4.0f, this.coordY + 4.0f + 10.0f + 1.0f + 2.5f, this.absorption / 20.0f * (this.getWidth() - 4.0f - 20.0f - 8.0f), 2.5, new Color(250, 218, 82, 255).getRGB());
        Wrapper.getFont().drawString((float)Math.round(target.getHealth()) / 2.0f + "", this.coordX + 28.0f, this.coordY + 3.0f, Color.WHITE, false);
        small.drawString(target.getNameClear().toLowerCase(), this.coordX + 28.0f, this.coordY + this.getHeight() - 8.0f, Color.WHITE, false);
        small.drawString("distance: " + Math.round(target.getDistanceToEntity(this.mc.thePlayer)), this.coordX + 28.0f + 20.0f, this.coordY + 3.0f, Color.WHITE, false);
        small.drawString("armor: " + target.getTotalArmorValue(), this.coordX + 28.0f + 20.0f, this.coordY + 3.0f + (float)small.getHeight() - 1.0f, Color.WHITE, false);
        GlStateManager.resetColor();
        this.mc.getTextureManager().bindTexture(target.getLocationSkin());
        Gui.drawScaledCustomSizeModalRect(this.coordX + 4.0f, this.coordY + 4.0f, 3.0f, 3.0f, 3.0f, 3.0f, 20.0f, 20.0f, 24.0f, 24.0f);
        this.mc.getTextureManager().bindTexture(target.getLocationSkin());
        Gui.drawScaledCustomSizeModalRect(this.coordX + 4.0f, this.coordY + 4.0f, 15.0f, 3.0f, 3.0f, 3.0f, 20.0f, 20.0f, 24.0f, 24.0f);
    }

    void drawExhiTargetHUD(AbstractClientPlayer target) {
        if (target.ticksExisted < 40 && this.mc.thePlayer.ticksExisted < 40) {
            return;
        }
        FontRenderer fr = this.mc.fontRendererObj;
        Gui.drawRect(this.coordX, this.coordY, this.coordX + this.getWidth(), this.coordY + this.getHeight(), new Color(0, 0, 0, 170).getRGB());
        GuiInventory.drawEntityOnScreen((int)(this.coordX + 16.0f), (int)(this.coordY + 28.0f), 13, -target.rotationYaw, target.rotationPitch, target);
        GlStateManager.pushMatrix();
        GlStateManager.translate(this.coordX + 33.0f, this.coordY + 2.5f, 0.0f);
        GlStateManager.scale(0.85f, 0.85f, 1.0f);
        fr.drawStringWithShadow(target.getNameClear(), 0.0f, 0.0f, -1);
        GlStateManager.popMatrix();
        float width = this.getWidth() - 33.0f - 10.0f;
        float healthRect = width * MathHelper.clamp_float(target.getHealth() / target.getMaxHealth(), 0.0f, 1.0f);
        float percentage = target.getHealth() / target.getMaxHealth() / 3.0f;
        Gui.drawRect(this.coordX + 33.0f, this.coordY + 12.0f, this.coordX + 33.0f + healthRect, this.coordY + 15.0f, Color.HSBtoRGB(percentage, 1.0f, 1.0f));
        DrawUtil.drawHollowRect(this.coordX + 33.0f, this.coordY + 12.0f, this.coordX + 33.0f + width, this.coordY + 15.0f, 0.6f, Color.BLACK.getRGB());
        float spacing = (this.getWidth() - 33.0f - 10.0f) / 9.0f;
        for (int i = 1; i < 9; ++i) {
            DrawUtil.drawRect((double)this.coordX + 32.75 + (double)((float)i * spacing), this.coordY + 12.0f, (double)this.coordX + 33.25 + (double)((float)i * spacing), this.coordY + 15.0f, Color.BLACK.getRGB());
        }
        StringBuilder line1 = new StringBuilder();
        StringBuilder line2 = new StringBuilder();
        StringBuilder line3 = new StringBuilder();
        line1.append("HP: ").append((int)target.getHealth());
        line1.append(" | ");
        line1.append("Dist: ").append((int)this.mc.thePlayer.getDistanceToEntity(target));
        line2.append("G: ").append(target.onGround ? "true" : "false");
        line2.append(" ");
        line2.append("CV: ").append(target.onGround ? "true" : "false");
        line3.append("TCG: ").append(target.ticksExisted);
        line3.append(" ");
        line3.append("HURT: ").append(target.hurtTime);
        GlStateManager.pushMatrix();
        GlStateManager.translate(this.coordX + 33.0f, this.coordY + 17.5f, 0.0f);
        GlStateManager.scale(0.5f, 0.5f, 1.0f);
        fr.drawStringWithShadow(line1.toString(), 0.0f, 0.0f, -1);
        fr.drawStringWithShadow(line2.toString(), 0.0f, 8.0f, -1);
        fr.drawStringWithShadow(line3.toString(), 0.0f, 16.0f, -1);
        GlStateManager.popMatrix();
    }

    @Override
    public void render() {
        float diff;
        if (this.followPlayer.getValue().booleanValue()) {
            this.coordX = this.coords == null ? this.getX() : this.coords[0] + (this.coords[1] - this.coords[0]) / 2.0f - this.getWidth() / 2.0f;
            this.coordY = this.coords == null ? this.getY() : this.coords[2] + (this.coords[3] - this.coords[2]) / 2.0f - this.getHeight() / 2.0f;
        } else {
            this.coordX = this.getX();
            this.coordY = this.getY();
        }
        if (this.target == this.mc.thePlayer) {
            this.coordX = this.getX();
            this.coordY = this.getY();
        }
        if (Wrapper.getModule(Aura.class).isEnabled()) {
            this.target = Wrapper.getModule(Aura.class).getTarget() instanceof AbstractClientPlayer ? (AbstractClientPlayer)Wrapper.getModule(Aura.class).getTarget() : null;
        } else {
            AbstractClientPlayer abstractClientPlayer = this.target = this.mc.pointedEntity instanceof AbstractClientPlayer ? (AbstractClientPlayer)this.mc.pointedEntity : null;
        }
        if (this.mc.currentScreen instanceof GuiChat) {
            this.target = this.mc.thePlayer;
        }
        if (this.target == null) {
            this.health = 0.0f;
            this.absorption = 0.0f;
            if (this.targetRenderAnimation.getState()) {
                this.animTimer.reset();
            }
            this.targetRenderAnimation.setState(false);
        } else {
            this.oldTarget = this.target;
            this.targetRenderAnimation.setState(true);
            this.shouldDraw = true;
        }
        if (this.target == null && this.oldTarget == null || !this.shouldDraw) {
            return;
        }
        if (this.target == null && this.oldTarget != null) {
            this.target = this.oldTarget;
        }
        this.damageAnim.setState(this.target.getHealth() != this.health);
        this.absorptionAnim.setState(this.target.getAbsorptionAmount() != this.absorption);
        this.hasAbsorbtion.setState(this.target.getAbsorptionAmount() != 0.0f);
        if (this.target.getHealth() < this.health) {
            diff = this.health - this.target.getHealth();
            this.health -= diff * (float)this.damageAnim.getAnimationFactor();
        }
        if (this.target.getHealth() > this.health) {
            diff = this.target.getHealth() - this.health;
            this.health += diff * (float)this.damageAnim.getAnimationFactor();
        }
        if (this.target.getAbsorptionAmount() < this.absorption) {
            diff = this.absorption - this.target.getAbsorptionAmount();
            this.absorption -= diff * (float)this.absorptionAnim.getAnimationFactor();
        }
        if (this.target.getAbsorptionAmount() > this.absorption) {
            diff = this.target.getAbsorptionAmount() - this.absorption;
            this.absorption += diff * (float)this.absorptionAnim.getAnimationFactor();
        }
        RenderUtil.scaleXY(this.getX() + this.getWidth() / 2.0f, this.getY() + this.getHeight() / 2.0f, this.targetRenderAnimation, () -> {
            switch (this.theme.getValue()) {
                case NEW: {
                    this.drawNewTargetHUD(this.target != null ? this.target : this.oldTarget);
                    break;
                }
                case OLD: {
                    this.drawOldTargetHUD(this.target != null ? this.target : this.oldTarget);
                    break;
                }
                case EXHIBITION: {
                    this.drawExhiTargetHUD(this.target != null ? this.target : this.oldTarget);
                }
            }
        });
    }

    @Override
    public void blur() {
        if (this.target == null && this.oldTarget == null || !this.shouldDraw) {
            return;
        }
        RenderUtil.scaleXY(this.getX() + this.getWidth() / 2.0f, this.getY() + this.getHeight() / 2.0f, this.targetRenderAnimation, () -> {
            switch (this.theme.getValue()) {
                case NEW: {
                    RoundedUtils.glRound(this.coordX, this.coordY, this.getWidth(), this.getHeight(), 10.0f, Wrapper.getPallet().getBackground().getRGB());
                    break;
                }
                case OLD: {
                    RenderUtil.drawRect(this.coordX, this.coordY, this.getWidth(), this.getHeight(), 0x50000000);
                }
            }
        });
    }

    @Override
    public float getWidth() {
        switch (this.theme.getValue()) {
            case NEW: 
            case OLD: {
                if (this.target != null) {
                    float absorption = (float)this.absorptionAnim.getAnimationFactor() * this.target.getAbsorptionAmount();
                    return Math.max(Wrapper.getFont().getStringWidth(this.target.getCommandSenderName()), 110);
                }
                return 110.0f;
            }
            case EXHIBITION: {
                if (this.target != null) {
                    return Math.max(33 + this.mc.fontRendererObj.getStringWidth(this.target.getNameClear()) + 5, 100);
                }
                return 100.0f;
            }
        }
        return 0.0f;
    }

    @Override
    public float getHeight() {
        switch (this.theme.getValue()) {
            case NEW: {
                return 40.0f + (float)this.hasAbsorbtion.getAnimationFactor() * 7.0f;
            }
            case OLD: {
                return 28.0f;
            }
            case EXHIBITION: {
                return 32.0f;
            }
        }
        return 0.0f;
    }

    public static enum TargetHUDTheme {
        NEW,
        OLD,
        EXHIBITION;

    }
}

