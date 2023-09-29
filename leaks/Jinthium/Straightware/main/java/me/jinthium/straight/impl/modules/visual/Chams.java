package me.jinthium.straight.impl.modules.visual;

import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.api.module.Module;
import me.jinthium.straight.api.setting.ParentAttribute;
import me.jinthium.straight.impl.Client;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;
import me.jinthium.straight.impl.event.render.RenderChestEvent;
import me.jinthium.straight.impl.event.render.RenderModelEvent;
import me.jinthium.straight.impl.settings.BooleanSetting;
import me.jinthium.straight.impl.settings.ColorSetting;
import me.jinthium.straight.impl.settings.ModeSetting;
import me.jinthium.straight.impl.settings.MultiBoolSetting;
import me.jinthium.straight.impl.utils.render.ColorUtil;
import me.jinthium.straight.impl.utils.render.RenderUtil;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

public class Chams extends Module {


    private final MultiBoolSetting entities = new MultiBoolSetting("Entities", "Players", "Animals", "Mobs", "Chests");

    private final BooleanSetting lighting = new BooleanSetting("Lighting", true);
    private final BooleanSetting onlyBehindWalls = new BooleanSetting("Only behind walls", false);
    private final ModeSetting behindWalls = new ModeSetting("Not Visible", "Sync", "Sync", "Opposite", "Red", "Custom");
    private final ColorSetting wallColor = new ColorSetting("Not Visible Color", Color.red);
    private final ModeSetting visibleColorMode = new ModeSetting("Visible", "Sync", "Sync", "Custom");
    private final ColorSetting visibleColor = new ColorSetting("Visible Color", Color.BLUE);


    public Chams() {
        super("Chams", Category.VISUALS);
        wallColor.addParent(behindWalls, behindWalls -> behindWalls.is("Custom"));
        visibleColorMode.addParent(onlyBehindWalls, ParentAttribute.BOOLEAN_CONDITION.negate());
        visibleColor.addParent(visibleColorMode, modeSetting -> modeSetting.is("Custom"));
        addSettings(entities, lighting, onlyBehindWalls, behindWalls, wallColor, visibleColorMode, visibleColor);
    }


    @Callback
    final EventCallback<RenderChestEvent> renderChestEventEventCallback = event -> {
        if (!entities.isEnabled("Chests")) return;
        Color behindWallsColor = Color.WHITE;
        Hud hud = Client.INSTANCE.getModuleManager().getModule(Hud.class);
        switch (behindWalls.getMode()) {
            case "Sync" -> behindWallsColor = hud.getHudColor((float) System.currentTimeMillis() / 600).darker();
            case "Opposite" ->
                    behindWallsColor = ColorUtil.getOppositeColor(hud.getHudColor((float) System.currentTimeMillis() / 600));
            case "Red" -> behindWallsColor = new Color(0xffEF2626);
            case "Custom" -> behindWallsColor = wallColor.getColor();
        }

        Color visibleColor = switch (visibleColorMode.getMode()) {
            case "Sync" -> hud.getHudColor((float) System.currentTimeMillis() / 600);
            case "Custom" -> this.visibleColor.getColor();
            default -> Color.WHITE;
        };

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
    };

    @Callback
    final EventCallback<RenderModelEvent> renderModelEventEventCallback = event -> {
        if (!isValidEntity(event.getEntity())) return;

        Hud hud = Client.INSTANCE.getModuleManager().getModule(Hud.class);
        if (event.isPre()) {
            Color behindWallsColor = switch (behindWalls.getMode()) {
                case "Sync" -> hud.getHudColor((float) System.currentTimeMillis() / 600).darker();
                case "Opposite" ->
                        ColorUtil.getOppositeColor(hud.getHudColor((float) System.currentTimeMillis() / 600));
                case "Red" -> new Color(0xffEF2626);
                case "Custom" -> wallColor.getColor();
                default -> Color.WHITE;
            };

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

            Color visibleColor = switch (visibleColorMode.getMode()) {
                case "Sync" -> hud.getHudColor((float) System.currentTimeMillis() / 600);
                case "Custom" -> this.visibleColor.getColor();
                default -> Color.WHITE;
            };

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

    };

    private boolean isValidEntity(Entity entity) {
        return entities.isEnabled("Players") && entity instanceof EntityPlayer ||
                entities.isEnabled("Animals") && entity instanceof EntityAnimal ||
                entities.isEnabled("Mobs") && entity instanceof EntityMob;
    }


}
