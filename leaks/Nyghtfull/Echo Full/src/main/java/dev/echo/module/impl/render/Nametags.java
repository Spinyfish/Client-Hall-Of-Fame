package dev.echo.module.impl.render;

import dev.echo.commands.impl.FriendCommand;
import dev.echo.listener.Link;
import dev.echo.listener.Listener;
import dev.echo.listener.event.impl.render.NametagRenderEvent;
import dev.echo.listener.event.impl.render.Render2DEvent;
import dev.echo.listener.event.impl.render.Render3DEvent;
import dev.echo.listener.event.impl.render.ShaderEvent;
import dev.echo.module.Category;
import dev.echo.module.Module;
import dev.echo.module.settings.impl.*;
import dev.echo.utils.font.AbstractFontRenderer;
import dev.echo.utils.render.*;
import net.minecraft.client.gui.Gui;
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

public class Nametags extends Module {

    private final MultipleBoolSetting validEntities = new MultipleBoolSetting("Valid Entities",
            new BooleanSetting("Players", true),
            new BooleanSetting("Animals", true),
            new BooleanSetting("Mobs", true));

    private final BooleanSetting nametags = new BooleanSetting("Nametags", true);

    private final NumberSetting capacity = new NumberSetting("Capacity", 130, 255, 0, 5);
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



    public Nametags() {
        super("Nametags", Category.RENDER, "Mhh i wonder");
        addSettings(nametags,scale,redTags,nametagSettings,validEntities,capacity);
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

    private final NumberFormat df = new DecimalFormat("0.#");

    private final Color backgroundColor = new Color(10, 10, 10, 130);

    @Link
    public Listener<Render2DEvent> onRender2D = e -> {
        for (Entity entity : entityPosition.keySet()) {
            Vector4f pos = entityPosition.get(entity);
            float x = pos.getX(),
                    y = pos.getY() + 10,
                    //or just normal Y
                    right = pos.getZ();

            if (entity instanceof EntityLivingBase) {
                AbstractFontRenderer font = mc.fontRendererObj;
                EntityLivingBase renderingEntity = (EntityLivingBase) entity;
                if (nametags.isEnabled()) {
                    String ircName = "";
                    float healthValue = renderingEntity.getHealth() / renderingEntity.getMaxHealth();
                    Color healthColor = healthValue > .75 ? new Color(66, 246, 123) : healthValue > .5 ? new Color(228, 255, 105) : healthValue > .35 ? new Color(236, 100, 64) : new Color(255, 65, 68);
                    String name = (nametagSettings.getSetting("Formatted Tags").isEnabled() ? renderingEntity.getDisplayName().getFormattedText() : StringUtils.stripControlCodes(renderingEntity.getDisplayName().getUnformattedText())) + ircName;
                    StringBuilder text = new StringBuilder(
                            (FriendCommand.isFriend(renderingEntity.getName()) ? "§d" : redTags.isEnabled() ? "§c" : "§f") + name);
                    if (nametagSettings.getSetting("Health Text").isEnabled()) {
                        text.append(String.format(" §7§r%s HP§7", df.format(renderingEntity.getHealth())));
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
                        Color backgroundTagColor = new Color(0,0,0,capacity.getValue().intValue());
                        if (nametagSettings.getSetting("Round").isEnabled()) {
                            RoundedUtil.drawRound(middle - 3, (float) (y - (fontHeight + 7)), textWidth + 6,
                                    (float) ((fontHeight / fontScale) + 4), 4, backgroundTagColor);
                        } else {
                            Gui.drawRect2(middle - 3, (float) (y - (fontHeight + 7)), textWidth + 6,
                                    (fontHeight / fontScale) + 4, backgroundTagColor.getRGB());
                        }
                    }


                    RenderUtil.resetColor();
                        RenderUtil.resetColor();
                        mc.fontRendererObj.drawStringWithShadow(StringUtils.stripControlCodes(text.toString()), middle + .5f, (float) (y - (fontHeight + 4)) + .5f, Color.BLACK);
                        RenderUtil.resetColor();
                        mc.fontRendererObj.drawStringWithShadow(text.toString(), middle, (float) (y - (fontHeight + 4)), healthColor.getRGB());

                    glPopMatrix();
                }
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
                        font = mc.fontRendererObj;
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
