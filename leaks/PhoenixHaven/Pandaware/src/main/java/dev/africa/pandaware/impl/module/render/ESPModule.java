package dev.africa.pandaware.impl.module.render;

import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.module.interfaces.Category;
import dev.africa.pandaware.api.module.interfaces.ModuleInfo;
import dev.africa.pandaware.impl.event.render.NameTagsEvent;
import dev.africa.pandaware.impl.event.render.RenderEvent;
import dev.africa.pandaware.impl.font.Fonts;
import dev.africa.pandaware.impl.module.combat.KillAuraModule;
import dev.africa.pandaware.impl.setting.BooleanSetting;
import dev.africa.pandaware.impl.setting.EnumSetting;
import dev.africa.pandaware.impl.setting.NumberSetting;
import dev.africa.pandaware.impl.ui.UISettings;
import dev.africa.pandaware.impl.ui.shader.impl.GlowShader;
import dev.africa.pandaware.utils.math.apache.ApacheMath;
import dev.africa.pandaware.utils.render.RenderUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.src.Config;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;
import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.List;

@Getter
@ModuleInfo(name = "ESP", category = Category.VISUAL)
public class ESPModule extends Module {
    private final EnumSetting<Mode> mode = new EnumSetting<>("Mode", Mode.SHADER);
    private final BooleanSetting box = new BooleanSetting("Box", true,
            () -> this.mode.getValue() == Mode.CSGO);
    private final BooleanSetting healthBar = new BooleanSetting("Health bar", true,
            () -> this.mode.getValue() == Mode.CSGO);
    private final BooleanSetting armorBar = new BooleanSetting("Armor bar", true,
            () -> this.mode.getValue() == Mode.CSGO);
    private final BooleanSetting nametags = new BooleanSetting("Nametags", false,
            () -> this.mode.getValue() == Mode.CSGO);
    private final BooleanSetting invisible = new BooleanSetting("Invisible", true);
    private final EnumSetting<Femboy> femboyMode = new EnumSetting<>("Mode", Femboy.ASTOLFO,
            () -> mode.getValue() == Mode.FEMBOY);
    private final BooleanSetting shaderSeparateTextures = new BooleanSetting("Shader separate textures",
            false, () -> this.mode.getValue() == Mode.SHADER);
    private final NumberSetting shaderRadius = new NumberSetting("Shader radius", 10, 1, 3.5, 0.5,
            () -> this.mode.getValue() == Mode.SHADER);
    private final NumberSetting shaderExposure = new NumberSetting("Shader exposure", 5, 1, 2.5, 0.5,
            () -> this.mode.getValue() == Mode.SHADER);

    public ESPModule() {
        this.registerSettings(this.mode, this.box, this.healthBar, this.armorBar, this.nametags, this.femboyMode,
                this.invisible, this.shaderSeparateTextures, this.shaderRadius, this.shaderExposure);
    }

    private boolean cancelNameTags;
    private boolean cancelShadow;

    private final GlowShader glowShader = new GlowShader();

    private final ResourceLocation femboy = new ResourceLocation("pandaware/icons/esp.png");
    private final ResourceLocation femboy2 = new ResourceLocation("pandaware/icons/esp2.png");
    private final ResourceLocation mixednuts = new ResourceLocation("pandaware/icons/mixednuts.png");

    private double interp(final double newPos, final double oldPos) {
        return oldPos + (newPos - oldPos) * mc.timer.renderPartialTicks;
    }

    @EventHandler
    EventCallback<RenderEvent> onRender = event -> {
        switch (this.mode.getValue()) {
            case CSGO:
                if (event.getType() == RenderEvent.Type.RENDER_3D) {
                    GlStateManager.pushAttribAndMatrix();
                    mc.theWorld.loadedEntityList.forEach(entity -> {
                        if (((entity != mc.thePlayer && mc.gameSettings.thirdPersonView == 0) &&
                                entity instanceof EntityPlayer ||
                                entity instanceof EntityPlayer &&
                                        mc.gameSettings.thirdPersonView != 0) &&
                                ((invisible.getValue() && entity.isInvisible()) ||
                                        (!invisible.getValue() && !entity.isInvisible()))) {
                            Color color = (((EntityPlayer) entity).hurtTime > 0 ? Color.RED : getColor(entity));

                            GL11.glPushMatrix();
                            GL11.glDisable(GL11.GL_DEPTH_TEST);
                            double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.timer.renderPartialTicks;
                            double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.timer.renderPartialTicks;
                            double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.timer.renderPartialTicks;
                            double width = entity.width / 1.5;
                            double height = entity.height + (entity.isSneaking() ? -0.3 : 0.2);
                            AxisAlignedBB aabb = new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width);
                            List<Vector3d> vectors = Arrays.asList(
                                    new Vector3d(aabb.minX, aabb.minY, aabb.minZ),
                                    new Vector3d(aabb.minX, aabb.maxY, aabb.minZ),
                                    new Vector3d(aabb.maxX, aabb.minY, aabb.minZ),
                                    new Vector3d(aabb.maxX, aabb.maxY, aabb.minZ),
                                    new Vector3d(aabb.minX, aabb.minY, aabb.maxZ),
                                    new Vector3d(aabb.minX, aabb.maxY, aabb.maxZ),
                                    new Vector3d(aabb.maxX, aabb.minY, aabb.maxZ),
                                    new Vector3d(aabb.maxX, aabb.maxY, aabb.maxZ)
                            );
                            Vector4d position = null;
                            for (Vector3d vector : vectors) {
                                vector = this.project(vector.x - mc.getRenderManager().viewerPosX, vector.y - mc.getRenderManager().viewerPosY, vector.z - mc.getRenderManager().viewerPosZ);
                                if (vector != null && vector.z >= 0.0 && vector.z < 1.0) {
                                    if (position == null) {
                                        position = new Vector4d(vector.x, vector.y, vector.z, 0.0);
                                    }
                                    position.x = ApacheMath.min(vector.x, position.x);
                                    position.y = ApacheMath.min(vector.y, position.y);
                                    position.z = ApacheMath.max(vector.x, position.z);
                                    position.w = ApacheMath.max(vector.y, position.w);
                                }
                            }
                            if (!Config.isShaders()) {
                                mc.entityRenderer.setupOverlayRendering();
                            }
                            if (position != null) {
                                GL11.glPushMatrix();

                                if (this.box.getValue()) {
                                    RenderUtils.drawBorderedRect(position.x - 1.5, position.y - 0.5, position.x - 1.5 + position.z - position.x + 4.0, position.y - 0.5 + position.w - position.y + 1.0, 1.5f, 0, Color.BLACK.getRGB());
                                    RenderUtils.drawBorderedRect(position.x - 1, position.y, position.x - 1 + position.z - position.x + 3, position.y + position.w - position.y, 0.5f, 0, color.getRGB());
                                }

                                if (this.healthBar.getValue() && ((EntityPlayer) entity).getHealth() > 0.0f) {
                                    double offset = position.w - position.y;
                                    double percentoffset = offset / ((EntityPlayer) entity).getMaxHealth();
                                    double finalnumber = percentoffset * ((EntityPlayer) entity).getHealth();
                                    RenderUtils.drawBorderedRect(position.x - 3.5, position.y - 0.5, position.x - 3.5 + 1.5, position.y - 0.5 + position.w - position.y + 1, 0.5, 0x60000000, 0xff000000);
                                    RenderUtils.drawRect(position.x - 3, position.y + offset, position.x - 3 + 0.5, MathHelper.clamp_double(position.y + offset + -finalnumber, position.y, position.y + offset), getHealthColor(((EntityPlayer) entity).getHealth(), ((EntityPlayer) entity).getMaxHealth()));
                                }

                                if (this.armorBar.getValue()) {
                                    double percentoffset = ((EntityPlayer) entity).getTotalArmorValue() / 20f;

                                    RenderUtils.drawBorderedRect(position.x - 1.5, position.y - 0.5 + position.w - position.y + 1.5, position.x - 1.5 + position.z - position.x + 4.0, position.y - 0.5 + position.w - position.y + 3, .5f, 0, Color.BLACK.getRGB());
                                    RenderUtils.drawRect(position.x - 1, position.y + position.w - position.y + 1.5, (position.x - 1) + ((position.z - position.x + 3.0) * MathHelper.clamp_double(percentoffset, 0, 1)), position.y + position.w - position.y + 2, Color.CYAN.getRGB());
                                }

                                if (this.nametags.getValue()) {
                                    double xPos = (position.x - 1) + ((position.z - position.x + 3.0) / 2f);
                                    double yPos = position.y - 8;

                                    GlStateManager.pushMatrix();
//                                    GlStateManager.scale(0.5, 0.5, 0.5);
                                    Fonts.getInstance().getProductSansSmall().drawCenteredString(entity.getName(),
                                            (int) xPos, (int) yPos, Color.RED.getRGB());
                                    GlStateManager.popMatrix();
                                }

                                GL11.glPopMatrix();
                            }
                            GlStateManager.enableDepth();
                            mc.entityRenderer.setupCameraTransform(event.getPartialTicks(), 0);
                            GL11.glPopMatrix();
                        }
                    });
                    GlStateManager.popAttribAndMatrix();
                }
                break;

            case SHADER:
                    if (event.getType() == RenderEvent.Type.RENDER_3D) {
                        boolean rendering = false;

                        for (int i = 0; i < mc.theWorld.playerEntities.size(); ++i) {
                            Entity entity = mc.theWorld.playerEntities.get(i);

                            if (entity != null && !(entity == mc.thePlayer && mc.gameSettings.thirdPersonView == 0)) {
                                rendering = true;
                                break;
                            }
                        }

                        if (rendering) {
                            this.cancelNameTags = true;
                            this.cancelShadow = true;

                            this.glowShader.applyGlow(() -> {
                                for (int i = 0; i < mc.theWorld.playerEntities.size(); ++i) {
                                    EntityPlayer entity = mc.theWorld.playerEntities.get(i);

                                    if (entity != null && !(entity == mc.thePlayer && mc.gameSettings.thirdPersonView == 0)) {
                                        mc.getRenderManager().renderEntityStatic(entity, event.getPartialTicks(), false);
                                    }
                                }
                            });

                            this.cancelNameTags = false;
                            this.cancelShadow = false;
                        } else {
                            this.glowShader.clearFrameBuffer();
                        }
                    }

                    if (event.getType() == RenderEvent.Type.RENDER_2D) {
                        this.glowShader.updateBuffer(
                                this.shaderRadius.getValue().floatValue(),
                                this.shaderExposure.getValue().floatValue(),
                                this.shaderSeparateTextures.getValue(),
                                UISettings.CURRENT_COLOR
                        );
                    }
                break;
            case FEMBOY:
                if (event.getType() == RenderEvent.Type.RENDER_3D) {
                    for (final EntityPlayer player : mc.theWorld.playerEntities) {
                        if (player.isEntityAlive() && player != mc.thePlayer && !player.isInvisible()) {
                            final double x = interp(player.posX, player.lastTickPosX) - RenderManager.renderPosX;
                            final double y = interp(player.posY, player.lastTickPosY) - RenderManager.renderPosY;
                            final double z = interp(player.posZ, player.lastTickPosZ) - RenderManager.renderPosZ;

                            GlStateManager.pushMatrix();
                            GL11.glColor4d(1.0, 1.0, 1.0, 1.0);
                            GL11.glDisable(2929);

                            final float distance = MathHelper.clamp_float(mc.thePlayer.getDistanceToEntity(player),
                                    20.0f, Float.MAX_VALUE);
                            final double scale = 0.005 * distance;

                            GlStateManager.translate(x, y, z);
                            GlStateManager.rotate(-Minecraft.getMinecraft().getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
                            GlStateManager.scale(-0.1, -0.1, 0.0);

                            switch (femboyMode.getValue()) {
                                case ASTOLFO:
                                    mc.getTextureManager().bindTexture(femboy);
                                    break;
                                case FELIX:
                                    mc.getTextureManager().bindTexture(femboy2);
                                    break;
                                case MIXED_NUTS:
                                    mc.getTextureManager().bindTexture(mixednuts);
                                    break;
                            }

                            Gui.drawScaledCustomSizeModalRect((int) (player.width / 2.0 - distance / 3.0),
                                    (int) (-player.height - distance), 0.0f, 0.0f, (int) 1.0, (int) 1.0,
                                    (int) (252.0 * (scale / 2.0)), (int) (476.0 * (scale / 2.0)), 1.0f, 1.0f);
                            GL11.glEnable(2929);

                            GlStateManager.popMatrix();
                        }
                    }
                }
        }
    };

    @EventHandler
    EventCallback<NameTagsEvent> onNametags = event -> {
        if (this.mode.getValue() == Mode.SHADER && this.cancelNameTags) {
            event.cancel();
        }
    };

    private int getHealthColor(float health, float maxHealth) {
        return Color.HSBtoRGB(ApacheMath.max(0.0F, ApacheMath.min(health, maxHealth) / maxHealth) / 3.0F, 1.0F, 0.75F) | 0xFF000000;
    }

    private Vector3d project(double x, double y, double z) {
        IntBuffer viewport = GLAllocation.createDirectIntBuffer(16);
        FloatBuffer modelview = GLAllocation.createDirectFloatBuffer(16);
        FloatBuffer projection = GLAllocation.createDirectFloatBuffer(16);

        ScaledResolution scaledResolution = new ScaledResolution(mc);
        FloatBuffer vector = GLAllocation.createDirectFloatBuffer(4);
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelview);
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projection);
        GL11.glGetInteger(GL11.GL_VIEWPORT, viewport);
        if (GLU.gluProject((float) x, (float) y, (float) z, modelview, projection, viewport, vector)) {
            return new Vector3d(vector.get(0) / scaledResolution.getScaleFactor(),
                    (Display.getHeight() - vector.get(1)) / scaledResolution.getScaleFactor(), vector.get(2));
        }
        return null;
    }

    public Color getColor(Entity entity) {
        KillAuraModule killAuraModule = Client.getInstance().getModuleManager().getByClass(KillAuraModule.class);

        if (entity != null && killAuraModule.getData().isEnabled() &&
                killAuraModule.getTarget() != null && entity == killAuraModule.getTarget()) {
            return Color.BLUE;
        }

        if (entity instanceof EntityPlayer) {
            if (Client.getInstance().getIgnoreManager().isIgnore((EntityPlayer) entity, false)) {
                return Color.RED;
            } else if (Client.getInstance().getIgnoreManager().isIgnore((EntityPlayer) entity, true)) {
                return Color.GREEN;
            }
        }

        return UISettings.CURRENT_COLOR;
    }

    @AllArgsConstructor
    public enum Mode {
        CSGO("CSGO"),
        SHADER("Shader"),
        FEMBOY("Femboy");

        private final String label;

        @Override
        public String toString() {
            return label;
        }
    }

    @AllArgsConstructor
    public enum Femboy {
        ASTOLFO("Astolfo"),
        FELIX("Felix"),
        MIXED_NUTS("Mixed Nuts");

        private final String label;

        @Override
        public String toString() {
            return label;
        }
    }
}
