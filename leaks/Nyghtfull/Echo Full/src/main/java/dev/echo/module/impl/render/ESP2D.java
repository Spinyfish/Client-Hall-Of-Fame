package dev.echo.module.impl.render;

import dev.echo.listener.Link;
import dev.echo.listener.Listener;
import dev.echo.utils.tuples.Pair;
import dev.echo.commands.impl.FriendCommand;
import dev.echo.listener.event.impl.render.NametagRenderEvent;
import dev.echo.listener.event.impl.render.Render2DEvent;
import dev.echo.listener.event.impl.render.Render3DEvent;
import dev.echo.listener.event.impl.render.ShaderEvent;
import dev.echo.module.Category;
import dev.echo.module.Module;
import dev.echo.module.settings.ParentAttribute;
import dev.echo.module.settings.impl.*;
import dev.echo.utils.font.AbstractFontRenderer;
import dev.echo.utils.render.*;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StringUtils;
import org.lwjgl.util.vector.Vector4f;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;

public class ESP2D extends Module {

    private final MultipleBoolSetting validEntities = new MultipleBoolSetting("Valid Entities",
            new BooleanSetting("Players", true),
            new BooleanSetting("Animals", true),
            new BooleanSetting("Mobs", true));


    private final BooleanSetting mcfont = new BooleanSetting("Minecraft Font", true);
    public final BooleanSetting boxEsp = new BooleanSetting("Box", true);
    private final ModeSetting boxColorMode = new ModeSetting("Box Mode", "Sync", "Sync", "Custom");
    private final ColorSetting boxColor = new ColorSetting("Box Color", Color.PINK);
    private final BooleanSetting itemHeld = new BooleanSetting("Item Held Text", true);
    private final BooleanSetting equipmentVisual = new BooleanSetting("Equipment", true);
    private final BooleanSetting healthBar = new BooleanSetting("Health Bar", true);
    private final ModeSetting healthBarMode = new ModeSetting("Health Bar Mode", "Color", "Health", "Color");
    private final BooleanSetting nametags = new BooleanSetting("Nametags", true);
    private final NumberSetting scale = new NumberSetting("Tag Scale", .75, 1, .35, .05);
    private final BooleanSetting redTags = new BooleanSetting("Red Tags", false);
    private final MultipleBoolSetting nametagSettings = new MultipleBoolSetting("Nametag Settings",
            redTags,
            new BooleanSetting("Formatted Tags", false),
            new BooleanSetting("Add PostProcessing", false),
            new BooleanSetting("Health Text", true),
            new BooleanSetting("Background", true),
            new BooleanSetting("Red Background", false),
            new BooleanSetting("Round", true));

    public ESP2D() {
        super("2D ESP", Category.RENDER, "Draws a box in 2D space around entitys");
        boxColorMode.addParent(boxEsp, ParentAttribute.BOOLEAN_CONDITION);
        boxColor.addParent(boxColorMode, modeSetting -> modeSetting.is("Custom"));

        scale.addParent(nametags, ParentAttribute.BOOLEAN_CONDITION);
        nametagSettings.addParent(nametags, ParentAttribute.BOOLEAN_CONDITION);
        healthBarMode.addParent(healthBar, ParentAttribute.BOOLEAN_CONDITION);

        addSettings(validEntities, mcfont, boxEsp, boxColorMode, boxColor, itemHeld, healthBar, healthBarMode,
                equipmentVisual, nametags, scale, nametagSettings);
    }


    private final Map<Entity, Vector4f> entityPosition = new HashMap<>();

    @Link
    public Listener<NametagRenderEvent> onNametagRender = e -> {
        if (nametags.isEnabled()) e.setCancelled(true);
    };

    @Link
    public Listener<Render3DEvent> onRender3D = e -> {
        entityPosition.clear();
        for (final Entity entity : mc.theWorld.loadedEntityList) {
            if (shouldRender(entity) && ESPUtil.isInView(entity)) {
                entityPosition.put(entity, ESPUtil.getEntityPositionsOn2D(entity));
            }
        }
    };

    @Link
    public Listener<ShaderEvent> onShaderEvent = e -> {
        if (nametagSettings.getSetting("Add PostProcessing").isEnabled() && nametags.isEnabled()) {
            for (Entity entity : entityPosition.keySet()) {
                Vector4f pos = entityPosition.get(entity);
                float x = pos.getX(), y = pos.getY(), right = pos.getZ(), bottom = pos.getW();

                if (entity instanceof EntityLivingBase) {
                    AbstractFontRenderer font = echoBoldFont20;
                    if (mcfont.isEnabled()) {
                        font = mc.fontRendererObj;
                    }
                    EntityLivingBase renderingEntity = (EntityLivingBase) entity;
                    String ircName = "";
                    String name = (nametagSettings.getSetting("Formatted Tags").isEnabled() ? renderingEntity.getDisplayName().getFormattedText() : StringUtils.stripControlCodes(renderingEntity.getDisplayName().getUnformattedText())) + ircName;
                    StringBuilder text = new StringBuilder(
                            (FriendCommand.isFriend(renderingEntity.getName()) ? "§d" : redTags.isEnabled() ? "§c" : "§f") + name);

                    double fontScale = scale.getValue();
                    float middle = x + ((right - x) / 2);
                    float textWidth = 0;
                    double fontHeight;
                    textWidth = font.getStringWidth(text.toString());
                    middle -= (textWidth * fontScale) / 2f;
                    fontHeight = font.getHeight() * fontScale;

                    glPushMatrix();
                    glTranslated(middle, y - (fontHeight + 2), 0);
                    glScaled(fontScale, fontScale, 1);
                    glTranslated(-middle, -(y - (fontHeight + 2)), 0);

                    Color backgroundTagColor = nametagSettings.getSetting("Red Background").isEnabled() ? Color.RED : Color.BLACK;
                    RenderUtil.resetColor();
                    GLUtil.startBlend();
                    if (nametagSettings.getSetting("Round").isEnabled()) {

                        RoundedUtil.drawRound(middle - 3, (float) (y - (fontHeight + 7)), textWidth + 6,
                                (float) ((fontHeight / fontScale) + 4), 4, backgroundTagColor);
                    } else {
                        Gui.drawRect2(middle - 3, (float) (y - (fontHeight + 7)), textWidth + 6,
                                (fontHeight / fontScale) + 4, backgroundTagColor.getRGB());
                    }

                    glPopMatrix();

                }
            }
        }
    };

    private final NumberFormat df = new DecimalFormat("0.#");
    private final Color backgroundColor = new Color(10, 10, 10, 130);

    private Color firstColor = Color.BLACK, secondColor = Color.BLACK, thirdColor = Color.BLACK, fourthColor = Color.BLACK;


    @Link
    public Listener<Render2DEvent> onRender2D = e -> {

        if (boxColorMode.is("Custom")) {
            if (boxColor.isRainbow()) {
                firstColor = boxColor.getRainbow().getColor(0);
                secondColor = boxColor.getRainbow().getColor(90);
                thirdColor = boxColor.getRainbow().getColor(180);
                fourthColor = boxColor.getRainbow().getColor(270);
            } else {
                gradientColorWheel(Pair.of(boxColor.getColor(), boxColor.getAltColor()));
            }
        } else {
            if (HUDMod.isRainbowTheme()) {
                firstColor = HUDMod.color1.getRainbow().getColor(0);
                secondColor = HUDMod.color1.getRainbow().getColor(90);
                thirdColor = HUDMod.color1.getRainbow().getColor(180);
                fourthColor = HUDMod.color1.getRainbow().getColor(270);
            } else {
                gradientColorWheel(HUDMod.getClientColors());
            }
        }

        for (Entity entity : entityPosition.keySet()) {
            Vector4f pos = entityPosition.get(entity);
            float x = pos.getX(),
                    y = pos.getY(),
                    right = pos.getZ(),
                    bottom = pos.getW();

            if (entity instanceof EntityLivingBase) {
                AbstractFontRenderer font = echoBoldFont20;
                if (mcfont.isEnabled()) {
                    font = mc.fontRendererObj;
                }
                EntityLivingBase renderingEntity = (EntityLivingBase) entity;
                if (nametags.isEnabled()) {
                    String ircName = "";
                    float healthValue = renderingEntity.getHealth() / renderingEntity.getMaxHealth();
                    Color healthColor = healthValue > .75 ? new Color(66, 246, 123) : healthValue > .5 ? new Color(228, 255, 105) : healthValue > .35 ? new Color(236, 100, 64) : new Color(255, 65, 68);
                    String name = (nametagSettings.getSetting("Formatted Tags").isEnabled() ? renderingEntity.getDisplayName().getFormattedText() : StringUtils.stripControlCodes(renderingEntity.getDisplayName().getUnformattedText())) + ircName;
                    StringBuilder text = new StringBuilder(
                            (FriendCommand.isFriend(renderingEntity.getName()) ? "§d" : redTags.isEnabled() ? "§c" : "§f") + name);
                    if (nametagSettings.getSetting("Health Text").isEnabled()) {
                        text.append(String.format(" §7[§r%s HP§7]", df.format(renderingEntity.getHealth())));
                    }
                    double fontScale = scale.getValue();
                    float middle = x + ((right - x) / 2);
                    float textWidth;
                    double fontHeight = font.getHeight() * fontScale;
                    textWidth = font.getStringWidth(text.toString());
                    middle -= (textWidth * fontScale) / 2f;

                    glPushMatrix();
                    glTranslated(middle, y - (fontHeight + 2), 0);
                    glScaled(fontScale, fontScale, 1);
                    glTranslated(-middle, -(y - (fontHeight + 2)), 0);


                    if (nametagSettings.getSetting("Background").isEnabled()) {
                        Color backgroundTagColor = nametagSettings.getSetting("Red Background").isEnabled() ? ColorUtil.applyOpacity(Color.RED, .65f) : backgroundColor;
                        if (nametagSettings.getSetting("Round").isEnabled()) {
                            RoundedUtil.drawRound(middle - 3, (float) (y - (fontHeight + 7)), textWidth + 6,
                                    (float) ((fontHeight / fontScale) + 4), 4, backgroundTagColor);
                        } else {
                            Gui.drawRect2(middle - 3, (float) (y - (fontHeight + 7)), textWidth + 6,
                                    (fontHeight / fontScale) + 4, backgroundTagColor.getRGB());
                        }
                    }


                    RenderUtil.resetColor();
                    if (mcfont.isEnabled()) {
                        RenderUtil.resetColor();
                        mc.fontRendererObj.drawString(StringUtils.stripControlCodes(text.toString()), middle + .5f, (float) (y - (fontHeight + 4)) + .5f, Color.BLACK);
                        RenderUtil.resetColor();
                        mc.fontRendererObj.drawString(text.toString(), middle, (float) (y - (fontHeight + 4)), healthColor.getRGB());
                    } else {
                        echoBoldFont20.drawSmoothStringWithShadow(text.toString(), middle, (float) (y - (fontHeight + 5)), healthColor.getRGB());
                    }
                    glPopMatrix();
                }

                if (itemHeld.isEnabled()) {
                    if (renderingEntity.getHeldItem() != null) {

                        float fontScale = .5f;
                        float middle = x + ((right - x) / 2);
                        float textWidth;
                        String text = renderingEntity.getHeldItem().getDisplayName();
                        textWidth = font.getStringWidth(text);
                        middle -= (textWidth * fontScale) / 2f;

                        glPushMatrix();
                        glTranslated(middle, (bottom + 4), 0);
                        glScaled(fontScale, fontScale, 1);
                        glTranslated(-middle, -(bottom + 4), 0);
                        GlStateManager.bindTexture(0);
                        RenderUtil.resetColor();
                        Gui.drawRect2(middle - 3, bottom + 1, font.getStringWidth(text) + 6, font.getHeight() + 5, backgroundColor.getRGB());
                        if (mcfont.isEnabled()) {
                            mc.fontRendererObj.drawStringWithShadow(text, middle, bottom + 4, -1);
                        } else {
                            echoBoldFont20.drawSmoothStringWithShadow(text, middle, bottom + 4, -1);
                        }
                        glPopMatrix();
                    }
                }

                if (equipmentVisual.isEnabled()) {
                    float scale = .4f;
                    float equipmentX = right + 5;
                    float equipmentY = y - 1;
                    glPushMatrix();
                    glTranslated(equipmentX, equipmentY, 0);
                    glScaled(scale, scale, 1);
                    glTranslated(-equipmentX, -y, 0);
                    RenderUtil.resetColor();
                    RenderHelper.enableGUIStandardItemLighting();
                    float seperation = 0f;
                    float length = (bottom - y) - 2;
                    for (int i = 3; i >= 0; i--) {
                        if (renderingEntity.getCurrentArmor(i) == null) {
                            seperation += (length / 3) / scale;
                            continue;
                        }
                        mc.getRenderItem().renderItemAndEffectIntoGUI(renderingEntity.getCurrentArmor(i), (int) equipmentX, (int) (equipmentY + seperation));
                        seperation += (length / 3) / scale;
                    }

                    RenderHelper.disableStandardItemLighting();
                    glPopMatrix();
                }


                if (healthBar.isEnabled()) {
                    float healthValue = renderingEntity.getHealth() / renderingEntity.getMaxHealth();
                    Color healthColor = healthValue > .75 ? new Color(66, 246, 123) : healthValue > .5 ? new Color(228, 255, 105) : healthValue > .35 ? new Color(236, 100, 64) : new Color(255, 65, 68);

                    float height = (bottom - y) + 1;
                    Gui.drawRect2(x - 3.5f, y - .5f, 2, height + 1, new Color(0, 0, 0, 180).getRGB());
                    if (healthBarMode.is("Color")) {
                        GradientUtil.drawGradientTB(x - 3, y, 1, height, .3f, firstColor, fourthColor);
                        GradientUtil.drawGradientTB(x - 3, y + (height - (height * healthValue)), 1, height * healthValue, 1, firstColor, fourthColor);
                    } else {
                        Gui.drawRect2(x - 3, y, 1, height, ColorUtil.applyOpacity(healthColor, .3f).getRGB());
                        Gui.drawRect2(x - 3, y + (height - (height * healthValue)), 1, height * healthValue, healthColor.getRGB());
                    }
                }
            }

            if (boxEsp.isEnabled()) {
                float outlineThickness = .5f;
                RenderUtil.resetColor();
                //top
                GradientUtil.drawGradientLR(x, y, (right - x), 1, 1, firstColor, secondColor);
                //left
                GradientUtil.drawGradientTB(x, y, 1, bottom - y, 1, firstColor, fourthColor);
                //bottom
                GradientUtil.drawGradientLR(x, bottom, right - x, 1, 1, fourthColor, thirdColor);
                //right
                GradientUtil.drawGradientTB(right, y, 1, (bottom - y) + 1, 1, secondColor, thirdColor);

                //Outline

                //top
                Gui.drawRect2(x - .5f, y - outlineThickness, (right - x) + 2, outlineThickness, Color.BLACK.getRGB());
                //Left
                Gui.drawRect2(x - outlineThickness, y, outlineThickness, (bottom - y) + 1, Color.BLACK.getRGB());
                //bottom
                Gui.drawRect2(x - .5f, (bottom + 1), (right - x) + 2, outlineThickness, Color.BLACK.getRGB());
                //Right
                Gui.drawRect2(right + 1, y, outlineThickness, (bottom - y) + 1, Color.BLACK.getRGB());


                //top
                Gui.drawRect2(x + 1, y + 1, (right - x) - 1, outlineThickness, Color.BLACK.getRGB());
                //Left
                Gui.drawRect2(x + 1, y + 1, outlineThickness, (bottom - y) - 1, Color.BLACK.getRGB());
                //bottom
                Gui.drawRect2(x + 1, (bottom - outlineThickness), (right - x) - 1, outlineThickness, Color.BLACK.getRGB());
                //Right
                Gui.drawRect2(right - outlineThickness, y + 1, outlineThickness, (bottom - y) - 1, Color.BLACK.getRGB());

            }

        }

    };

    private void gradientColorWheel(Pair<Color, Color> colors) {
        firstColor = ColorUtil.interpolateColorsBackAndForth(15, 0, colors.getFirst(), colors.getSecond(), false);
        secondColor = ColorUtil.interpolateColorsBackAndForth(15, 90, colors.getFirst(), colors.getSecond(), false);
        thirdColor = ColorUtil.interpolateColorsBackAndForth(15, 180, colors.getFirst(), colors.getSecond(), false);
        fourthColor = ColorUtil.interpolateColorsBackAndForth(15, 270, colors.getFirst(), colors.getSecond(), false);
    }

    private boolean shouldRender(Entity entity) {
        if (entity.isDead || entity.isInvisible()) {
            return false;
        }
        if (validEntities.getSetting("Players").isEnabled() && entity instanceof EntityPlayer) {
            if (entity == mc.thePlayer) {
                return mc.gameSettings.thirdPersonView != 0;
            }
            return !entity.getDisplayName().getUnformattedText().contains("[NPC");
        }
        if (validEntities.getSetting("Animals").isEnabled() && entity instanceof EntityAnimal) {
            return true;
        }

        return validEntities.getSetting("mobs").isEnabled() && entity instanceof EntityMob;
    }


}
