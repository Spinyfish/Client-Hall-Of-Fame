package club.pulsive.impl.module.impl.visual;

import club.pulsive.api.event.eventBus.handler.EventHandler;
import club.pulsive.api.event.eventBus.handler.Listener;
import club.pulsive.impl.event.render.Render2DEvent;
import club.pulsive.impl.event.render.RenderNametagEvent;
import club.pulsive.impl.module.Category;
import club.pulsive.impl.module.Module;
import club.pulsive.impl.module.ModuleInfo;
import club.pulsive.impl.property.Property;
import club.pulsive.impl.property.implementations.ColorProperty;
import club.pulsive.impl.property.implementations.DoubleProperty;
import club.pulsive.impl.property.implementations.EnumProperty;
import club.pulsive.impl.property.implementations.MultiSelectEnumProperty;
import club.pulsive.impl.util.player.PlayerUtil;
import club.pulsive.impl.util.render.RenderUtil;
import club.pulsive.impl.util.render.StencilUtil;
import club.pulsive.impl.util.render.shaders.Blur;
import club.pulsive.impl.util.world.WorldUtil;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;

import java.awt.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

@ModuleInfo(name = "ESP", renderName = "ESP", description = "See things through walls.", category = Category.VISUALS)
public class ESP extends Module {

    private final MultiSelectEnumProperty<PlayerUtil.TARGETS> targetsProperty = new MultiSelectEnumProperty<>("Targets", Lists.newArrayList(PlayerUtil.TARGETS.PLAYERS), PlayerUtil.TARGETS.values());
    private final MultiSelectEnumProperty<Element> elementsProperty = new MultiSelectEnumProperty<>("Elements", Lists.newArrayList(Element.BOX, Element.NAMETAGS, Element.HEALTH, Element.ARMOR), Element.values());
    private final DoubleProperty boxThicknessProperty = new DoubleProperty("Box Thickness", 1, 1, 10, 1, () -> elementsProperty.isSelected(Element.BOX));
    private final EnumProperty<BoxMode> boxModeProperty = new EnumProperty<>("Box Mode", BoxMode.BOX, () -> elementsProperty.isSelected(Element.BOX));
    private final Property<Boolean> oppositeCornersProperty = new Property<>("Opposite", false, () -> elementsProperty.isSelected(Element.BOX) && boxModeProperty.getValue() == BoxMode.HALF_CORNERS);
    private final EnumProperty<RenderUtil.ColorMode> boxColorModeProperty = new EnumProperty<>("Color Mode", RenderUtil.ColorMode.Sync, () -> elementsProperty.isSelected(Element.BOX));
    private final ColorProperty boxColorProperty = new ColorProperty("Color", new Color(209, 50, 50), () -> boxColorModeProperty.getValue() == RenderUtil.ColorMode.Custom);
      private final Property<Boolean> boxFadeProperty = new Property<>("Box Fade", true, () -> elementsProperty.isSelected(Element.BOX));

    public int getBoxColor(int index) {
        return boxColorModeProperty.getValue() == RenderUtil.ColorMode.Custom ? boxColorProperty.getValue().getRGB() : HUD.getColor();
    }

    @EventHandler
    private final Listener<RenderNametagEvent> renderNametagListener = event -> {
        if (event.getEntity() instanceof EntityPlayer && elementsProperty.isSelected(Element.NAMETAGS))
            event.setCancelled(true);
    };

    @EventHandler
    private final Listener<Render2DEvent> render2DListener = event -> {
        Gui.drawRect(0, 0, 0, 0,0);
        final List<EntityLivingBase> livingEntities = WorldUtil.getLivingEntities(Predicates.and(entity -> PlayerUtil.isValid(entity, targetsProperty)));
        for (EntityLivingBase entity : livingEntities) {
            if (!RenderUtil.isInViewFrustrum(entity)) continue;
            final double diffX = entity.posX - entity.lastTickPosX;
            final double diffY = entity.posY - entity.lastTickPosY;
            final double diffZ = entity.posZ - entity.lastTickPosZ;
            final double deltaX = mc.thePlayer.posX - entity.posX;
            final double deltaY = mc.thePlayer.posY - entity.posY;
            final double deltaZ = mc.thePlayer.posZ - entity.posZ;
            final float partialTicks = event.getPartialTicks();
            final AxisAlignedBB interpolatedBB = new AxisAlignedBB(
                    entity.lastTickPosX - entity.width / 2 + diffX * partialTicks,
                    entity.lastTickPosY + diffY * partialTicks,
                    entity.lastTickPosZ - entity.width / 2 + diffZ * partialTicks,
                    entity.lastTickPosX + entity.width / 2 + diffX * partialTicks,
                    entity.lastTickPosY + entity.height + diffY * partialTicks,
                    entity.lastTickPosZ + entity.width / 2 + diffZ * partialTicks);
            final double[][] vectors = new double[8][2];
            final float[] coords = new float[4];
            convertTo2D(interpolatedBB, vectors, coords);
            float minX = coords[0], minY = coords[1], maxX = coords[2], maxY = coords[3];
            float opacity = 255 - MathHelper.clamp_float(MathHelper.sqrt_double(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) * 4, 0, 255);
            Color color = boxFadeProperty.getValue() ? RenderUtil.toColorRGB(getBoxColor(0), opacity) : new Color(getBoxColor(0));
            for (Element element : elementsProperty.getValues()) {
                if (elementsProperty.isSelected(element)) {
                    switch (element) {
                        case BOX: {
                            switch (boxModeProperty.getValue()) {
                                case BOX: {
                                    RenderUtil.pre3D();
                                    glLineWidth(boxThicknessProperty.getValue().floatValue() * 4f);
                                    glBegin(GL_LINE_LOOP);
                                    glColor4f(0, 0, 0, opacity / 255f);
                                    glVertex2f(minX, minY);
                                    glVertex2f(maxX, minY);
                                    glVertex2f(maxX, maxY);
                                    glVertex2f(minX, maxY);
                                    glEnd();

                                    glLineWidth(boxThicknessProperty.getValue().floatValue());
                                    RenderUtil.color(color.getRGB());
                                    glBegin(GL_LINE_LOOP);
                                    glVertex2f(minX, minY);
                                    glVertex2f(maxX, minY);
                                    glVertex2f(maxX, maxY);
                                    glVertex2f(minX, maxY);
                                    glEnd();
                                    RenderUtil.post3D();
                                    break;
                                }
                                case FILL: {
                                    RenderUtil.drawRect(minX, minY, maxX, maxY, color.getRGB());
                                    break;
                                }
                                case BLUR_FILL: {
                                    StencilUtil.initStencilToWrite();
                                    RenderUtil.drawRect(minX, minY, maxX, maxY, color.getRGB());
                                    StencilUtil.readStencilBuffer(1);
                                    Blur.renderBlur(Shaders.blurRadius.getValue().floatValue());
                                    StencilUtil.uninitStencilBuffer();
                                    break;
                                }
                                case HORIZ_SIDES: {
                                    RenderUtil.pre3D();
                                    float lineLength = (maxX - minX) / 3;
                                    glLineWidth(boxThicknessProperty.getValue().floatValue() * 4f);
                                    glColor4f(0, 0, 0, opacity / 255f);
                                    glBegin(GL_LINES);
                                    glVertex2f(minX, minY);
                                    glVertex2f(minX + lineLength, minY);
                                    glVertex2f(minX, minY);
                                    glVertex2f(minX, maxY);
                                    glVertex2f(minX, maxY);
                                    glVertex2f(minX + lineLength, maxY);
                                    glVertex2f(maxX, minY);
                                    glVertex2f(maxX - lineLength, minY);
                                    glVertex2f(maxX, minY);
                                    glVertex2f(maxX, maxY);
                                    glVertex2f(maxX, maxY);
                                    glVertex2f(maxX - lineLength, maxY);
                                    glEnd();

                                    glLineWidth(boxThicknessProperty.getValue().floatValue());
                                    glBegin(GL_LINES);
                                    RenderUtil.color(color.getRGB());
                                    glVertex2f(minX, minY);
                                    glVertex2f(minX + lineLength, minY);
                                    glVertex2f(minX, minY);
                                    glVertex2f(minX, maxY);
                                    glVertex2f(minX, maxY);
                                    glVertex2f(minX + lineLength, maxY);
                                    glVertex2f(maxX, minY);
                                    glVertex2f(maxX - lineLength, minY);
                                    glVertex2f(maxX, minY);
                                    glVertex2f(maxX, maxY);
                                    glVertex2f(maxX, maxY);
                                    glVertex2f(maxX - lineLength, maxY);
                                    glEnd();
                                    RenderUtil.post3D();
                                    break;
                                }
                                case VERT_SIDES: {
                                    RenderUtil.pre3D();
                                    float lineLength = (maxX - minX) / 3;
                                    glLineWidth(boxThicknessProperty.getValue().floatValue() * 4f);
                                    glColor4f(0, 0, 0, opacity / 255f);
                                    glBegin(GL_LINES);
                                    glVertex2f(minX, minY);
                                    glVertex2f(minX, minY + lineLength);
                                    glVertex2f(minX, minY);
                                    glVertex2f(maxX, minY);
                                    glVertex2f(maxX, minY);
                                    glVertex2f(maxX, minY + lineLength);
                                    glVertex2f(minX, maxY);
                                    glVertex2f(minX, maxY - lineLength);
                                    glVertex2f(minX, maxY);
                                    glVertex2f(maxX, maxY);
                                    glVertex2f(maxX, maxY);
                                    glVertex2f(maxX, maxY - lineLength);
                                    glEnd();
                                    glLineWidth(boxThicknessProperty.getValue().floatValue());
                                    RenderUtil.color(color.getRGB());
                                    glBegin(GL_LINES);
                                    glVertex2f(minX, minY);
                                    glVertex2f(minX, minY + lineLength);
                                    glVertex2f(minX, minY);
                                    glVertex2f(maxX, minY);
                                    glVertex2f(maxX, minY);
                                    glVertex2f(maxX, minY + lineLength);
                                    glVertex2f(minX, maxY);
                                    glVertex2f(minX, maxY - lineLength);
                                    glVertex2f(minX, maxY);
                                    glVertex2f(maxX, maxY);
                                    glVertex2f(maxX, maxY);
                                    glVertex2f(maxX, maxY - lineLength);
                                    glEnd();
                                    RenderUtil.post3D();
                                    break;
                                }
                                case CORNERS: {
                                    RenderUtil.pre3D();
                                    glLineWidth(boxThicknessProperty.getValue().floatValue() * 4f);
                                    glBegin(GL_LINES);
                                    glColor4f(0, 0, 0, opacity / 255f);
                                    float lineLength = (maxX - minX) / 3;
                                    glVertex2f(minX, minY);
                                    glVertex2f(minX + lineLength, minY);
                                    glVertex2f(minX, minY);
                                    glVertex2f(minX, minY + lineLength);

                                    glVertex2f(maxX, minY);
                                    glVertex2f(maxX - lineLength, minY);
                                    glVertex2f(maxX, minY);
                                    glVertex2f(maxX, minY + lineLength);

                                    glVertex2f(minX, maxY);
                                    glVertex2f(minX + lineLength, maxY);
                                    glVertex2f(minX, maxY);
                                    glVertex2f(minX, maxY - lineLength);

                                    glVertex2f(maxX, maxY);
                                    glVertex2f(maxX - lineLength, maxY);
                                    glVertex2f(maxX, maxY);
                                    glVertex2f(maxX, maxY - lineLength);
                                    glEnd();

                                    glLineWidth(boxThicknessProperty.getValue().floatValue());
                                    glBegin(GL_LINES);
                                    RenderUtil.color(color.getRGB());
                                    glVertex2f(minX, minY);
                                    glVertex2f(minX + lineLength, minY);
                                    glVertex2f(minX, minY);
                                    glVertex2f(minX, minY + lineLength);
                                    glVertex2f(maxX, minY);
                                    glVertex2f(maxX - lineLength, minY);
                                    glVertex2f(maxX, minY);
                                    glVertex2f(maxX, minY + lineLength);
                                    glVertex2f(minX, maxY);
                                    glVertex2f(minX + lineLength, maxY);
                                    glVertex2f(minX, maxY);
                                    glVertex2f(minX, maxY - lineLength);
                                    glVertex2f(maxX, maxY);
                                    glVertex2f(maxX - lineLength, maxY);
                                    glVertex2f(maxX, maxY);
                                    glVertex2f(maxX, maxY - lineLength);
                                    glEnd();
                                    RenderUtil.post3D();
                                    break;
                                }
                                case HALF_CORNERS: {
                                    RenderUtil.pre3D();
                                    glLineWidth(boxThicknessProperty.getValue().floatValue() * 4f);
                                    glBegin(GL_LINES);
                                    glColor4f(0, 0, 0, opacity / 255f);
                                    float lineLength = (maxX - minX) / 3;
                                    if (oppositeCornersProperty.getValue()) {
                                        glVertex2f(maxX, minY);
                                        glVertex2f(maxX - lineLength, minY);
                                        glVertex2f(maxX, minY);
                                        glVertex2f(maxX, minY + lineLength);

                                        glVertex2f(minX, maxY);
                                        glVertex2f(minX + lineLength, maxY);
                                        glVertex2f(minX, maxY);
                                        glVertex2f(minX, maxY - lineLength);
                                    } else {
                                        glVertex2f(minX, minY);
                                        glVertex2f(minX + lineLength, minY);
                                        glVertex2f(minX, minY);
                                        glVertex2f(minX, minY + lineLength);

                                        glVertex2f(maxX, maxY);
                                        glVertex2f(maxX - lineLength, maxY);
                                        glVertex2f(maxX, maxY);
                                        glVertex2f(maxX, maxY - lineLength);
                                    }
                                    glEnd();
                                    glLineWidth(boxThicknessProperty.getValue().floatValue());
                                    glBegin(GL_LINES);
                                    RenderUtil.color(color.getRGB());
                                    if (oppositeCornersProperty.getValue()) {
                                        glVertex2f(maxX, minY);
                                        glVertex2f(maxX - lineLength, minY);
                                        glVertex2f(maxX, minY);
                                        glVertex2f(maxX, minY + lineLength);

                                        glVertex2f(minX, maxY);
                                        glVertex2f(minX + lineLength, maxY);
                                        glVertex2f(minX, maxY);
                                        glVertex2f(minX, maxY - lineLength);
                                    } else {
                                        glVertex2f(minX, minY);
                                        glVertex2f(minX + lineLength, minY);
                                        glVertex2f(minX, minY);
                                        glVertex2f(minX, minY + lineLength);

                                        glVertex2f(maxX, maxY);
                                        glVertex2f(maxX - lineLength, maxY);
                                        glVertex2f(maxX, maxY);
                                        glVertex2f(maxX, maxY - lineLength);
                                    }
                                    glEnd();
                                    RenderUtil.post3D();
                                    break;
                                }
                            }
                            break;
                        }
                        case NAMETAGS: {
                            float scale = 0.55f;
                            float leftoverScale = 1 / scale;
                            minX *= leftoverScale;
                            minY *= leftoverScale;
                            maxX *= leftoverScale;
                            maxY *= leftoverScale;
                            glScalef(scale, scale, 1);
                            mc.fontRendererObj.drawStringWithShadow(entity.getDisplayName().getFormattedText(), minX + (maxX - minX) / 2 - mc.fontRendererObj.getStringWidth(entity.getDisplayName().getFormattedText()) / 2f, boxModeProperty.getValue() == BoxMode.BOX || boxModeProperty.getValue() == BoxMode.FILL ? minY - mc.fontRendererObj.FONT_HEIGHT - 3 : minY - mc.fontRendererObj.FONT_HEIGHT / 2f, new Color(255, 255, 255, MathHelper.floor_float(opacity)).getRGB());
                            glScalef(leftoverScale, leftoverScale, 1);
                            minX *= scale;
                            minY *= scale;
                            maxX *= scale;
                            maxY *= scale;
                            break;
                        }
                        case HAND: {
                            if (entity.getHeldItem() != null) {
                                float scale = 0.5f;
                                float leftoverScale = 1 / scale;
                                minX *= leftoverScale;
                                minY *= leftoverScale;
                                maxX *= leftoverScale;
                                maxY *= leftoverScale;
                                glScalef(scale, scale, 1);
                                String text = entity.getHeldItem().getDisplayName();
                                mc.fontRendererObj.drawStringWithShadow(text, minX + (maxX - minX) / 2 - mc.fontRendererObj.getStringWidth(text) / 2f, boxModeProperty.getValue() == BoxMode.BOX || boxModeProperty.getValue() == BoxMode.FILL ? maxY + mc.fontRendererObj.FONT_HEIGHT - 3 : maxY - mc.fontRendererObj.FONT_HEIGHT / 2f, new Color(255, 255, 255, MathHelper.floor_float(opacity)).getRGB());
                                glScalef(leftoverScale, leftoverScale, 1);
                                minX *= scale;
                                minY *= scale;
                                maxX *= scale;
                                maxY *= scale;
                            }
                            break;
                        }
                        case HEALTH: {
                            minX -= 3;
                            maxX -= 3;
                            RenderUtil.pre3D();
                            glLineWidth(boxThicknessProperty.getValue().floatValue() * 4f);
                            glBegin(GL_LINES);
                            glColor4f(0, 0, 0, opacity / 255f);
                            glVertex2f(minX, minY);
                            glVertex2f(minX, maxY);
                            glEnd();
                            glLineWidth(boxThicknessProperty.getValue().floatValue());
                            glBegin(GL_LINES);
                            Color healthColor = Color.GREEN;
                            if (entity.getHealth() < entity.getMaxHealth() / 2) healthColor = Color.YELLOW;
                            if (entity.getHealth() < entity.getMaxHealth() / 3) healthColor = Color.ORANGE;
                            if (entity.getHealth() < entity.getMaxHealth() / 4) healthColor = Color.RED;
                            RenderUtil.color(healthColor, MathHelper.floor_float(opacity));
                            glVertex2f(minX, minY + (maxY - minY));
                            glVertex2f(minX, maxY - (maxY - minY) * (entity.getHealth() / entity.getMaxHealth()));
                            glEnd();
                            RenderUtil.post3D();
                            minX += 3;
                            maxX += 3;
                            break;
                        }
                    }
                }
            }
        }
    };

    private void convertTo2D(AxisAlignedBB interpolatedBB, double[][] vectors, float[] coords) {
        if (coords == null || vectors == null || interpolatedBB == null) return;
        double x = mc.getRenderManager().viewerPosX;
        double y = mc.getRenderManager().viewerPosY;
        double z = mc.getRenderManager().viewerPosZ;

        vectors[0] = RenderUtil.project2D(interpolatedBB.minX - x, interpolatedBB.minY - y,
                interpolatedBB.minZ - z);
        vectors[1] = RenderUtil.project2D(interpolatedBB.minX - x, interpolatedBB.minY - y,
                interpolatedBB.maxZ - z);
        vectors[2] = RenderUtil.project2D(interpolatedBB.minX - x, interpolatedBB.maxY - y,
                interpolatedBB.minZ - z);
        vectors[3] = RenderUtil.project2D(interpolatedBB.maxX - x, interpolatedBB.minY - y,
                interpolatedBB.minZ - z);
        vectors[4] = RenderUtil.project2D(interpolatedBB.maxX - x, interpolatedBB.maxY - y,
                interpolatedBB.minZ - z);
        vectors[5] = RenderUtil.project2D(interpolatedBB.maxX - x, interpolatedBB.minY - y,
                interpolatedBB.maxZ - z);
        vectors[6] = RenderUtil.project2D(interpolatedBB.minX - x, interpolatedBB.maxY - y,
                interpolatedBB.maxZ - z);
        vectors[7] = RenderUtil.project2D(interpolatedBB.maxX - x, interpolatedBB.maxY - y,
                interpolatedBB.maxZ - z);

        float minW = (float) Arrays.stream(vectors).min(Comparator.comparingDouble(pos -> pos[2])).orElse(new double[]{0.5})[2];
        float maxW = (float) Arrays.stream(vectors).max(Comparator.comparingDouble(pos -> pos[2])).orElse(new double[]{0.5})[2];
        if (maxW > 1 || minW < 0) return;
        float minX = (float) Arrays.stream(vectors).min(Comparator.comparingDouble(pos -> pos[0])).orElse(new double[]{0})[0];
        float maxX = (float) Arrays.stream(vectors).max(Comparator.comparingDouble(pos -> pos[0])).orElse(new double[]{0})[0];
        final float top = (mc.displayHeight / (float) new ScaledResolution(mc).getScaleFactor());
        float minY = (float) (top - Arrays.stream(vectors).min(Comparator.comparingDouble(pos -> top - pos[1])).orElse(new double[]{0})[1]);
        float maxY = (float) (top - Arrays.stream(vectors).max(Comparator.comparingDouble(pos -> top - pos[1])).orElse(new double[]{0})[1]);
        coords[0] = minX;
        coords[1] = minY;
        coords[2] = maxX;
        coords[3] = maxY;
    }

    @AllArgsConstructor
    public enum Element {
        BOX("Box"),
        NAMETAGS("NameTags"),
        HEALTH("Health"),
        ARMOR("Amor"),
        HAND("Hand");

        private final String addonName;

        @Override
        public String toString() {return addonName;}
    }

    @AllArgsConstructor
    public enum BoxMode {
        BOX("Box"),
        FILL("Fill"),
        BLUR_FILL("Blur Fill"),
        HORIZ_SIDES("Horizontal Sides"),
        VERT_SIDES("Vertical Sides"),
        CORNERS("Corners"),
        HALF_CORNERS("Half Corners");

        private final String addonName;

        @Override
        public String toString() {return addonName;}
    }
}