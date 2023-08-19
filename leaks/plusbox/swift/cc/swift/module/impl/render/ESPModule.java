/**
 * @project hakarware
 * @author CodeMan
 * @at 25.07.23, 19:42
 */

package cc.swift.module.impl.render;

import cc.swift.events.BlurEvent;
import cc.swift.events.RenderOverlayEvent;
import cc.swift.events.RenderWorldEvent;
import cc.swift.module.Module;
import cc.swift.util.render.RenderUtil;
import cc.swift.util.render.StencilUtil;
import cc.swift.util.render.font.CFontRenderer;
import cc.swift.util.render.font.FontRenderer;
import cc.swift.util.shader.ShaderProgram;
import cc.swift.value.impl.BooleanValue;
import cc.swift.value.impl.DoubleValue;
import cc.swift.value.impl.ModeValue;
import com.github.javafaker.Bool;
import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL20;

import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;

public final class ESPModule extends Module {
    public final BooleanValue players = new BooleanValue("Players", true);
    public final BooleanValue items = new BooleanValue("Items", true);
    public final BooleanValue other = new BooleanValue("Other", false);

    public final BooleanValue rectangle = new BooleanValue("Rectangle", true);
    public final DoubleValue rectOpacity = new DoubleValue("Rectangle Opacity", 120d, 1, 255, 1).setDependency(rectangle::getValue);
    public final ModeValue<Mode> boxMode = new ModeValue<>("Box Type", Mode.values());
    public final DoubleValue linewidth = new DoubleValue("Line Width", 1d, 0.5, 5, 0.5f);
    public final BooleanValue outline = new BooleanValue("Outline", true);
    public final DoubleValue outlineWidth = new DoubleValue("Outline Width", 1d, 0.5, 3, 0.5f).setDependency(outline::getValue);
    public final BooleanValue hbar = new BooleanValue("Health Bar", true);
    public final BooleanValue healthText = new BooleanValue("Health Text", true).setDependency(hbar::getValue);
    public final BooleanValue heldItem = new BooleanValue("Held Item", false);

    public final BooleanValue blur = new BooleanValue("Blur", false);

    private final Map<Entity, float[]> entityPositionMap = new HashMap<>();

    public ESPModule() {
        super("ESP", Category.RENDER);
        this.registerValues(this.players, this.items, this.other, this.boxMode, this.hbar, this.healthText, this.heldItem, this.linewidth, this.outline, this.outlineWidth, this.rectangle, this.rectOpacity, this.blur);
    }

    @Handler
    public final Listener<BlurEvent> blurEventListener = event -> {
        if(blur.getValue()) {
            for (Map.Entry<Entity, float[]> entry : this.entityPositionMap.entrySet()) {

                if (!RenderUtil.isInViewFrustrum(entry.getKey().getEntityBoundingBox()))
                    continue;

                float[] position = entry.getValue();

                float x = position[0];
                float y = position[2];
                float x1 = position[1];
                float y1 = position[3];

                Gui.drawRect(x, y, x1, y1, -1);

            }
        }
    };

    @Handler
    public final Listener<RenderOverlayEvent> renderOverlayEventListener = event -> {

        CFontRenderer vietnamFont = FontRenderer.getFont("vietnam.ttf", 9);


        for (Map.Entry<Entity, float[]> entry : this.entityPositionMap.entrySet()) {

            if (!RenderUtil.isInViewFrustrum(entry.getKey().getEntityBoundingBox()))
                continue;

            float[] position = entry.getValue();

            Entity ent = entry.getKey();

            float x = position[0];
            float y = position[2];
            float x1 = position[1];
            float y1 = position[3];

            if (rectangle.getValue()) {
                Gui.drawRect(x, y, x1, y1, new Color(0, 0, 0, rectOpacity.getValue().intValue()).getRGB());
            }

            // box
            GL11.glPushMatrix();
            GlStateManager.pushAttrib();
            {
                GlStateManager.disableAlpha();
                GlStateManager.enableBlend();
                glDisable(GL_TEXTURE_2D);
                glEnable(GL_BLEND);
                glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                glEnable(GL_LINE_SMOOTH);

                if (this.outline.getValue()) {
                    GL11.glLineWidth(this.linewidth.getValue().floatValue() + this.outlineWidth.getValue().floatValue());
                    RenderUtil.glColor(Color.BLACK.getRGB());
                    this.boxMode.getValue().draw(x, y, x1, y1);
                }

                GL11.glLineWidth(this.linewidth.getValue().floatValue());
                RenderUtil.glColor(Color.WHITE.getRGB());
                this.boxMode.getValue().draw(x, y, x1, y1);

                glDisable(GL_BLEND);
                glDisable(GL_LINE_SMOOTH);
                glEnable(GL_TEXTURE_2D);
                GlStateManager.enableTexture2D();
                GlStateManager.disableBlend();
                GlStateManager.color(1, 1, 1, 1);
            }

            if(hbar.getValue() && ent instanceof EntityLivingBase){


                float health = 0;
                float maxHealth = 0;


                GL11.glDisable(GL11.GL_TEXTURE_2D);
//                GlStateManager.enableAlpha();
                GL11.glEnable(GL11.GL_BLEND);
                GL14.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
                // Health bar
                GL11.glLineWidth(linewidth.getValue().floatValue() + 2f);
                GlStateManager.color(0, 0, 0, 1);
                glBegin(GL11.GL_LINE_STRIP);
                GL11.glVertex2f(x - 3f, y - 0.5f);
                GL11.glVertex2f(x - 3f, y1 + 0.5f);
                glEnd();



                GlStateManager.color(0, 255, 0, 1);
                GL11.glLineWidth(linewidth.getValue().floatValue());
                glBegin(GL11.GL_LINE_STRIP);
                GL11.glVertex2f(x - 3f, y + ((y1 - y) * (1 - (((EntityLivingBase) ent).getHealth() / ((EntityLivingBase) ent).getMaxHealth()))));
                GL11.glVertex2f(x - 3f, y1);
                glEnd();
            }

            GL11.glPopMatrix();
            GlStateManager.popAttrib();

            if(healthText.getValue() && hbar.getValue() && ent instanceof EntityLivingBase) {
                vietnamFont.drawCenteredStringWithShadow( Math.round(((EntityLivingBase) ent).getHealth() / ((EntityLivingBase) ent).getMaxHealth() * 100) + "%", x - 10.5f, y1 - ((y1 - y) * (((EntityLivingBase) ent).getHealth() / ((EntityLivingBase) ent).getMaxHealth())) + 1, -1);
            }
            if(heldItem.getValue() && ent instanceof EntityPlayer && ((EntityPlayer)ent).getHeldItem() != null) {
                vietnamFont.drawCenteredStringWithShadow(((EntityPlayer) ent).getHeldItem().getDisplayName() + "", (x + x1) / 2, y1 + 5, -1);
            }
        }
    };

    @Handler
    public final Listener<RenderWorldEvent> renderWorldEventListener = event -> {
        entityPositionMap.clear();

        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (entity == mc.thePlayer && mc.gameSettings.thirdPersonView == 0) continue;

            if (entity instanceof EntityPlayer) {
                if (entity.getName().startsWith("§")) continue;

                if (!players.getValue()) continue;
            } else if (entity instanceof EntityItem) {
                if (!items.getValue()) continue;
            } else if (entity instanceof EntityLivingBase) {
                if (!other.getValue()) continue;
            } else {
                continue;
            }

            double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * event.getPartialTicks() - mc.getRenderManager().getRenderPosX();
            double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * event.getPartialTicks() - mc.getRenderManager().getRenderPosY();
            double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * event.getPartialTicks() - mc.getRenderManager().getRenderPosZ();

            AxisAlignedBB bb = new AxisAlignedBB(x - entity.width / 1.5, y, z - entity.width / 1.5, x + entity.width / 1.5, y + entity.height + 0.1, z + entity.width / 1.5);
            double[][] vectors = {{bb.minX, bb.minY, bb.minZ}, {bb.minX, bb.maxY, bb.minZ}, {bb.minX, bb.maxY, bb.maxZ}, {bb.minX, bb.minY, bb.maxZ}, {bb.maxX, bb.minY, bb.minZ}, {bb.maxX, bb.maxY, bb.minZ}, {bb.maxX, bb.maxY, bb.maxZ}, {bb.maxX, bb.minY, bb.maxZ}};
            Vector4f position = new Vector4f(Float.MAX_VALUE, Float.MAX_VALUE, -1.0F, -1.0F);
            for (double[] vec : vectors) {
                Vector3f projection = RenderUtil.project((float) vec[0], (float) vec[1], (float) vec[2]);
                if (projection != null && projection.z > 0 && projection.z < 1) {
                    position.x = Math.min(position.x, projection.x);
                    position.y = Math.min(position.y, projection.y);
                    position.z = Math.max(position.z, projection.x);
                    position.w = Math.max(position.w, projection.y);
                }
            }
            entityPositionMap.put(entity, new float[]{position.x, position.z, position.y, position.w});
        }
    };

    enum Mode {

        SIDE {
            @Override
            public void draw(float x, float y, float x2, float y2) {
                double width = Math.abs(x2 - x);
                double height = Math.abs(y2 - y);
                double quarterWidth = width / 4;

                GL11.glBegin(GL_LINE_STRIP);
                GL11.glVertex2d(x + quarterWidth, y);
                GL11.glVertex2d(x, y);
                GL11.glEnd();

                GL11.glBegin(GL_LINE_STRIP);
                GL11.glVertex2d(x, y);
                GL11.glVertex2d(x, y + height);
                GL11.glVertex2d(x + quarterWidth, y + height);
                GL11.glEnd();

                GL11.glBegin(GL_LINE_STRIP);
                GL11.glVertex2d(x + width - quarterWidth, y + height);
                GL11.glVertex2d(x + width, y + height);
                GL11.glEnd();

                GL11.glBegin(GL_LINE_STRIP);
                GL11.glVertex2d(x + width, y2);
                GL11.glVertex2d(x + width, y);
                GL11.glVertex2d(x + width - quarterWidth, y);
                GL11.glEnd();
            }
        },
        CORNER {
            @Override
            public void draw(float x, float y, float x2, float y2) {
                double width = Math.abs(x2 - x);
                double height = Math.abs(y2 - y);
                double quarterWidth = width / 4;
                double quarterHeight = height / 4;

                GL11.glBegin(GL_LINE_STRIP);
                GL11.glVertex2d(x + quarterWidth, y);
                GL11.glVertex2d(x, y);
                GL11.glVertex2d(x, y + quarterHeight);
                GL11.glEnd();

                GL11.glBegin(GL_LINE_STRIP);
                GL11.glVertex2d(x, y + height - quarterHeight);
                GL11.glVertex2d(x, y + height);
                GL11.glVertex2d(x + quarterWidth, y + height);
                GL11.glEnd();

                GL11.glBegin(GL_LINE_STRIP);
                GL11.glVertex2d(x + width - quarterWidth, y + height);
                GL11.glVertex2d(x + width, y + height);
                GL11.glVertex2d(x + width, y + height - quarterHeight);
                GL11.glEnd();

                GL11.glBegin(GL_LINE_STRIP);
                GL11.glVertex2d(x + width, y + quarterHeight);
                GL11.glVertex2d(x + width, y);
                GL11.glVertex2d(x + width - quarterWidth, y);
                GL11.glEnd();

            }
        },
        FULL {
            @Override
            public void draw(float x, float y, float x2, float y2) {
                GL11.glBegin(GL_LINE_STRIP);
                GL11.glVertex2d(x, y);
                GL11.glVertex2d(x, y2);
                GL11.glEnd();

                GL11.glBegin(GL_LINE_STRIP);
                GL11.glVertex2d(x, y);
                GL11.glVertex2d(x2, y);
                GL11.glEnd();

                GL11.glBegin(GL_LINE_STRIP);
                GL11.glVertex2d(x2, y);
                GL11.glVertex2d(x2, y2);
                GL11.glEnd();

                GL11.glBegin(GL_LINE_STRIP);
                GL11.glVertex2d(x, y2);
                GL11.glVertex2d(x2, y2);
                GL11.glEnd();
            }
        },
        NONE {
            @Override
            public void draw(float x, float y, float x2, float y2) {
            }
        };

        public abstract void draw(float x, float y, float x2, float y2);
    }

}
