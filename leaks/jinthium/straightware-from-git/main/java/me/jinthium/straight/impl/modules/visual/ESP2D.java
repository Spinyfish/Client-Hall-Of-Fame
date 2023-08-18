package me.jinthium.straight.impl.modules.visual;

import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.api.event.Event;
import me.jinthium.straight.api.module.Module;
import me.jinthium.straight.impl.Client;
import me.jinthium.straight.impl.event.render.Render2DEvent;
import me.jinthium.straight.impl.event.render.Render3DEvent;
import me.jinthium.straight.impl.utils.render.GradientUtil;
import me.jinthium.straight.impl.utils.render.RenderUtil;
import me.jinthium.straight.impl.utils.vector.Vector4d;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;

public class ESP2D extends Module {

    private final Map<Entity, Vector4d> entityPosition = new HashMap<>();

    public ESP2D(){
        super("2D ESP", Category.VISUALS);
    }

    @Callback
    final EventCallback<Render3DEvent> render3DEventEventCallback = event -> {
        entityPosition.clear();
        for (final Entity entity : mc.theWorld.loadedEntityList) {
            if (shouldRender(entity) && RenderUtil.isInView(entity)) {
                entityPosition.put(entity, RenderUtil.getEntityPositionsOn2Dd(entity));
            }
        }
    };

    @Callback
    final EventCallback<Render2DEvent> render2DEventEventCallback = event -> {
        Hud hud = Client.INSTANCE.getModuleManager().getModule(Hud.class);
        for(Entity entity : entityPosition.keySet()){
            Vector4d pos = entityPosition.get(entity);
            float x = (float) pos.getX(), y = (float) pos.getY(), right = (float) pos.getZ(), bottom = (float) pos.getW();

            if(entity instanceof EntityLivingBase renderedEntity){
                Color firstColor = hud.getHudColor((float) System.currentTimeMillis() / -600);
                Color secondColor = hud.getHudColor((float) System.currentTimeMillis() / -600).darker().darker();
                Color thirdColor = hud.getHudColor((float) System.currentTimeMillis() / -600).darker().darker();
                Color fourthColor = hud.getHudColor((float) System.currentTimeMillis() / -600).darker().darker();
                float outlineThickness = .5f;
                RenderUtil.resetColor();
                //top
                GradientUtil.drawGradientLR(x, y, right - x, 1, 1, firstColor, secondColor);
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

    private boolean shouldRender(Entity entity) {
        if (entity.isDead || entity.isInvisible()) {
            return false;
        }
        if (entity instanceof EntityPlayer) {
            if (entity == mc.thePlayer) {
                return mc.gameSettings.thirdPersonView != 0;
            }
            return !entity.getDisplayName().getUnformattedText().contains("[NPC");
        }

        return false;
    }
}
