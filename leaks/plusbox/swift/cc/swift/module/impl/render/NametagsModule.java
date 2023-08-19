package cc.swift.module.impl.render;

import cc.swift.events.BlurEvent;
import cc.swift.events.RenderNametagEvent;
import cc.swift.events.RenderOverlayEvent;
import cc.swift.events.RenderWorldEvent;
import cc.swift.module.Module;
import cc.swift.util.render.RenderUtil;
import cc.swift.util.render.font.CFontRenderer;
import cc.swift.util.render.font.FontRenderer;
import cc.swift.value.impl.BooleanValue;
import cc.swift.value.impl.DoubleValue;
import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;

import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class NametagsModule extends Module {

    public DoubleValue opacity = new DoubleValue("Opacity", 0d, 0d, 255d, 1d);

    public BooleanValue health = new BooleanValue("Health", true);
    public BooleanValue distance = new BooleanValue("Distance", false);
    public BooleanValue blur = new BooleanValue("Blur", false);
    // public BooleanValue ping = new BooleanValue("Ping", false);


    public NametagsModule() {
        super("Nametags", Category.RENDER);
        registerValues(opacity, health, distance, blur);
    }

    private final Map<Entity, float[]> entityPositionMap = new HashMap<>();
    @Handler
    public final Listener<RenderOverlayEvent> renderOverlayEventListener = event -> {
        CFontRenderer vietnamFont = FontRenderer.getFont("vietnam.ttf", 12);

        int backgroundColor = new Color(0, 0, 0, opacity.getValue().intValue()).getRGB(); // Semi-transparent black
        float padding = 3.5f; // Padding around the text

        for (Map.Entry<Entity, float[]> entry : this.entityPositionMap.entrySet()) {
            if (!RenderUtil.isInViewFrustrum(entry.getKey().getEntityBoundingBox())) continue;

            float[] position = entry.getValue();
            Entity ent = entry.getKey();
            float x = position[0];
            float y = position[2];
            float x1 = position[1];
            float y1 = position[3];

            assert ent instanceof EntityPlayer;


            String healthStr = health.getValue() ? " | " + (int) Math.round((((EntityPlayer) ent).getHealth() * 100) / 100) + " HP" : "";
            String distStr = distance.getValue() ? " | " + (int) Math.round(mc.thePlayer.getDistanceToEntity(ent)) + "m" : "";
            String infoStr = ent.getName() + healthStr + distStr;


            double stringWidth = vietnamFont.getStringWidth(infoStr);
            float width = (x1 - x);

            // Draw a rectangle background to the text rendered below

            double rectX = (x + width / 2f - stringWidth / 2f - padding);
            double rectY =  (y - 12 - padding);
            double rectWidth =  (stringWidth + padding * 2f);
            double rectHeight = (vietnamFont.getHeight() + padding * 2);
            Gui.drawRect(rectX, rectY, rectX + rectWidth, rectY + rectHeight, backgroundColor);

            vietnamFont.drawCenteredStringWithShadow(infoStr, x + width / 2f, y - 10, -1);
        }
    };

    @Handler
    public final Listener<RenderNametagEvent> renderNametagEventListener = event -> {
        event.setCancelled(true);
    };

    @Handler
    public final Listener<BlurEvent> blurEventListener = event -> {
        if(!blur.getValue()) return;
        CFontRenderer vietnamFont = FontRenderer.getFont("vietnam.ttf", 12);

        for (Map.Entry<Entity, float[]> entry : this.entityPositionMap.entrySet()) {
            if (!RenderUtil.isInViewFrustrum(entry.getKey().getEntityBoundingBox())) continue;

            float[] position = entry.getValue();
            Entity ent = entry.getKey();
            float x = position[0];
            float y = position[2];
            float x1 = position[1];
            float y1 = position[3];

            assert ent instanceof EntityPlayer;


            String healthStr = health.getValue() ? " | " + (int) Math.round((((EntityPlayer) ent).getHealth() * 100) / 100) + " HP" : "";
            String distStr = distance.getValue() ? " | " + (int) Math.round(mc.thePlayer.getDistanceToEntity(ent)) + "m" : "";
            String infoStr = ent.getName() + healthStr + distStr;


            double stringWidth = vietnamFont.getStringWidth(infoStr);
            float width = (x1 - x);

            // Draw a rectangle background to the text rendered below
            int backgroundColor = new Color(0, 0, 0, opacity.getValue().intValue()).getRGB(); // Semi-transparent black
            float padding = 3.5f; // Padding around the text
            double rectX = (x + width / 2f - stringWidth / 2f - padding);
            double rectY = (y - 12 - padding);
            double rectWidth = (stringWidth + padding * 2f);
            double rectHeight = (vietnamFont.getHeight() + padding * 2);
            Gui.drawRect(rectX, rectY, rectX + rectWidth, rectY + rectHeight, backgroundColor);
        }
    };

    @Handler
    public final Listener<RenderWorldEvent> renderWorldEventListener = event -> {
        entityPositionMap.clear();

        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (entity == mc.thePlayer && mc.gameSettings.thirdPersonView == 0) continue;
            if (!(entity instanceof EntityPlayer)) continue;
            else if (entity.getName().startsWith("§")) continue;

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
}
