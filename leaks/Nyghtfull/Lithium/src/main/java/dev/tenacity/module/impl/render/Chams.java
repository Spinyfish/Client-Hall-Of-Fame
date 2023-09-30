package dev.tenacity.module.impl.render;

import dev.tenacity.event.impl.render.RenderChestEvent;
import dev.tenacity.event.impl.render.RenderModelEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.settings.ParentAttribute;
import dev.tenacity.module.settings.impl.BooleanSetting;
import dev.tenacity.module.settings.impl.ColorSetting;
import dev.tenacity.module.settings.impl.ModeSetting;
import dev.tenacity.module.settings.impl.MultipleBoolSetting;
import dev.tenacity.utils.render.ColorUtil;
import dev.tenacity.utils.render.RenderUtil;
import dev.tenacity.utils.tuples.Pair;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

public class Chams extends Module {


    private final MultipleBoolSetting entities = new MultipleBoolSetting("Entities", "Players", "Animals", "Mobs", "Chests");

    private final BooleanSetting lighting = new BooleanSetting("Lighting", true);
    private final BooleanSetting onlyBehindWalls = new BooleanSetting("Only behind walls", false);
    private final ModeSetting behindWalls = new ModeSetting("Not Visible", "Sync", "Sync", "Opposite", "Red", "Custom");
    private final ColorSetting wallColor = new ColorSetting("Not Visible Color", Color.red);
    private final ModeSetting visibleColorMode = new ModeSetting("Visible", "Sync", "Sync", "Custom");
    private final ColorSetting visibleColor = new ColorSetting("Visible Color", new Color(255,255,255,100));


    public Chams() {
        super("Chams", Category.RENDER, "See people through walls");
        wallColor.addParent(behindWalls, behindWalls -> behindWalls.is("Custom"));
        visibleColorMode.addParent(onlyBehindWalls, ParentAttribute.BOOLEAN_CONDITION.negate());
        visibleColor.addParent(visibleColorMode, modeSetting -> modeSetting.is("Custom"));
        addSettings(entities, lighting, onlyBehindWalls, behindWalls, wallColor, visibleColorMode, visibleColor);
    }


    @Override
    public void onRenderChestEvent(RenderChestEvent event) {
        if (!entities.isEnabled("Chests")) return;
        Color behindWallsColor = Color.WHITE;
        Pair<Color, Color> colors = HUDMod.getClientColors();
        switch (behindWalls.getMode()) {
            case "Sync":
                behindWallsColor = colors.getSecond();
                break;
            case "Opposite":
                behindWallsColor = ColorUtil.getOppositeColor(colors.getFirst());
                break;
            case "Red":
                behindWallsColor = new Color(0xffEF2626);
                break;
            case "Custom":
                behindWallsColor = wallColor.getColor();
                break;
        }

        Color visibleColor = Color.WHITE;
        switch (visibleColorMode.getMode()) {
            case "Sync":
                visibleColor = colors.getFirst();
                break;
            case "Custom":
                visibleColor = this.visibleColor.getColor();
                break;
        }

        glPushMatrix();
        glDisable(GL_DEPTH_TEST);
        glDepthMask(false);
        glDisable(GL_TEXTURE_2D);

        RenderUtil.color(behindWallsColor.getRGB());

        if (!lighting.isEnabled()) {
            glDisable(GL_LIGHTING);
        }

        event.drawChest();

        glDepthMask(true);
        glEnable(GL_DEPTH_TEST);

        RenderUtil.resetColor();

        if (!onlyBehindWalls.isEnabled()) {
            RenderUtil.color(visibleColor.getRGB());
            event.drawChest();
            event.cancel();
        }

        glEnable(GL_LIGHTING);
        glEnable(GL_TEXTURE_2D);
        glPolygonOffset(1.0f, -1000000.0f);
        glDisable(GL_POLYGON_OFFSET_LINE);
        glPopMatrix();
    }

    @Override
    public void onRenderModelEvent(RenderModelEvent event) {
        if (!isValidEntity(event.getEntity())) return;

        Pair<Color, Color> colors = HUDMod.getClientColors();
        if (event.isPre()) {
            Color behindWallsColor = Color.WHITE;

            switch (behindWalls.getMode()) {
                case "Sync":
                    behindWallsColor = colors.getSecond();
                    break;
                case "Opposite":
                    behindWallsColor = ColorUtil.getOppositeColor(colors.getFirst());
                    break;
                case "Red":
                    behindWallsColor = new Color(0xffEF2626);
                    break;
                case "Custom":
                    behindWallsColor = wallColor.getColor();
                    break;
            }

            glPushMatrix();
            glEnable(GL_POLYGON_OFFSET_LINE);
            glPolygonOffset(1.0F, 1000000.0F);

            glDisable(GL_TEXTURE_2D);
            if (!lighting.isEnabled()) {
                glDisable(GL_LIGHTING);
            }
            RenderUtil.color(behindWallsColor.getRGB());

            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
            glDisable(GL_DEPTH_TEST);
            glDepthMask(false);
        } else {
            glEnable(GL_DEPTH_TEST);
            glDepthMask(true);

            Color visibleColor = Color.WHITE;
            switch (visibleColorMode.getMode()) {
                case "Sync":
                    visibleColor = colors.getFirst();
                    break;
                case "Custom":
                    visibleColor = this.visibleColor.getColor();
                    break;
            }

            if (onlyBehindWalls.isEnabled()) {
                glDisable(GL_BLEND);
                glEnable(GL_TEXTURE_2D);
                glEnable(GL_LIGHTING);
                glColor4f(1, 1, 1, 1);
            } else {
                if (!lighting.isEnabled()) {
                    glDisable(GL_LIGHTING);
                }
                RenderUtil.color(visibleColor.getRGB());
            }


            event.drawModel();


            glEnable(GL_TEXTURE_2D);
            glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            glDisable(GL_BLEND);
            glEnable(GL_LIGHTING);

            glPolygonOffset(1.0f, -1000000.0f);
            glDisable(GL_POLYGON_OFFSET_LINE);
            glPopMatrix();
        }

    }

    private boolean isValidEntity(Entity entity) {
        return entities.isEnabled("Players") && entity instanceof EntityPlayer ||
                entities.isEnabled("Animals") && entity instanceof EntityAnimal ||
                entities.isEnabled("Mobs") && entity instanceof EntityMob;
    }


}
