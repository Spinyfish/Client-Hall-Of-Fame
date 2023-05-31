package de.lirium.impl.module.impl.visual;

import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import de.lirium.Client;
import de.lirium.base.setting.Dependency;
import de.lirium.base.setting.Value;
import de.lirium.base.setting.impl.CheckBox;
import de.lirium.base.setting.impl.ComboBox;
import de.lirium.base.setting.impl.SliderSetting;
import de.lirium.impl.events.IsOutlineEvent;
import de.lirium.impl.events.OutlineColorEvent;
import de.lirium.impl.events.Render2DEvent;
import de.lirium.impl.events.Render3DEvent;
import de.lirium.impl.module.ModuleFeature;
import de.lirium.util.math.InterpolationUtil;
import de.lirium.util.render.ColorUtil;
import de.lirium.util.render.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.function.Predicate;

@ModuleFeature.Info(name = "ESP", description = "Highlight entities", category = ModuleFeature.Category.VISUAL)
public class ESPFeature extends ModuleFeature {

    @Value(name = "Mode")
    private final ComboBox<String> mode = new ComboBox<>("Minecraft", new String[]{"2D", "Box"});

    @Value(name = "Players")
    private final CheckBox players = new CheckBox(true);

    @Value(name = "Animals")
    private final CheckBox animals = new CheckBox(false);

    @Value(name = "Mobs")
    private final CheckBox mobs = new CheckBox(false);

    @Value(name = "Items")
    private final CheckBox items = new CheckBox(false);

    @Value(name = "Recolor")
    private final CheckBox recolor = new CheckBox(true, new Dependency<>(mode, "Minecraft"));

    @Value(name = "Color Mode")
    private final ComboBox<String> colorMode = new ComboBox<>("Client Color", new String[]{"Multicolor", "Random", "Rainbow"}, new Dependency<>(mode, "Minecraft", "Box"));

    @Value(name = "Alpha")
    final SliderSetting<Float> alpha = new SliderSetting<>(0.3F, 0.1F, 1F, new Dependency<>(mode, "Box"));

    private final Frustum frustum = new Frustum();

    private final Predicate<Entity> isValid = entity -> {
        if (entity.isInvisible()) return false;
        if (entity instanceof EntityPlayer && players.getValue()) return true;
        if (entity instanceof EntityCreature && mobs.getValue()) return true;
        if (entity instanceof EntityItem && items.getValue()) return true;
        if ((entity instanceof EntityVillager || entity instanceof EntityAnimal) && animals.getValue()) return true;
        return false;
    };

    @EventHandler
    private final Listener<OutlineColorEvent> outlineColorEventListener = e -> {
        if (mode.getValue().equalsIgnoreCase("Minecraft") && recolor.getValue()) {
            e.color = getColor(e.entity);
        } else if (!recolor.getValue()) {
            e.setCancelled(true);
        }
    };

    @EventHandler
    public final Listener<IsOutlineEvent> isOutlineEventListener = e -> {
        if (mode.getValue().equalsIgnoreCase("Minecraft")) {
            frustum.setPosition(mc.getRenderManager().renderPosX, mc.getRenderManager().renderPosY, mc.getRenderManager().renderPosZ);
            if (!frustum.isBoundingBoxInFrustum(e.getEntity().getEntityBoundingBox())) return;
            if (isValid.test(e.getEntity()))
                e.setOverrideOutline(true);
        }
    };

    @EventHandler
    public final Listener<Render3DEvent> render3DEventListener = e -> {
        final float renderPartialTicks = e.partialTicks;
        switch (mode.getValue()) {
            case "Box":
                for (final Entity entity : getWorld().loadedEntityList) {
                    if (entity == getPlayer() && getGameSettings().thirdPersonView == 0) continue;
                    if (isValid.test(entity)) {
                        final int color = getColor(entity);
                        final float red = (float)(color >> 16 & 255) / 255.0F;
                        final float green = (float)(color >> 8 & 255) / 255.0F;
                        final float blue = (float)(color & 255) / 255.0F;
                        GL11.glColor4f(red, green, blue, alpha.getValue());
                        final double x = ((entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * renderPartialTicks) - mc.getRenderManager().renderPosX);
                        final double y = ((entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * renderPartialTicks) - mc.getRenderManager().renderPosY);
                        final double z = ((entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * renderPartialTicks) - mc.getRenderManager().renderPosZ);
                        RenderUtil.drawBox(x + entity.width * 1.5 / 2, y, z + entity.width * 1.5 / 2, entity.width * -1.5, entity.height + 0.2);
                    }
                }
                break;
        }
    };

    @EventHandler
    public final Listener<Render2DEvent> render2DEventListener = e -> {
        switch (mode.getValue()) {
            case "2D":
                frustum.setPosition(mc.getRenderManager().renderPosX, mc.getRenderManager().renderPosY, mc.getRenderManager().renderPosZ);
                for (final Entity entity : getWorld().loadedEntityList) {
                    if (!frustum.isBoundingBoxInFrustum(entity.getEntityBoundingBox())) continue;
                    if (entity == getPlayer() && mc.gameSettings.thirdPersonView == 0) continue;
                    if (!isValid.test(entity)) continue;
                    final double[] coords = new double[4];
                    InterpolationUtil.convertBox(coords, entity);
                    RenderUtil.drawLine(coords[0], coords[1], coords[2], coords[1], 2.0f, Color.BLACK.getRGB());
                    RenderUtil.drawLine(coords[0], coords[1], coords[0], coords[3], 2.0f, Color.BLACK.getRGB());
                    RenderUtil.drawLine(coords[0], coords[3], coords[2], coords[3], 2.0f, Color.BLACK.getRGB());
                    RenderUtil.drawLine(coords[2], coords[1], coords[2], coords[3], 2.0f, Color.BLACK.getRGB());

                    RenderUtil.drawLine(coords[0], coords[1], coords[2], coords[1], 1.0f, Color.WHITE.getRGB());
                    RenderUtil.drawLine(coords[0], coords[1], coords[0], coords[3], 1.0f, Color.WHITE.getRGB());
                    RenderUtil.drawLine(coords[0], coords[3], coords[2], coords[3], 1.0f, Color.WHITE.getRGB());
                    RenderUtil.drawLine(coords[2], coords[1], coords[2], coords[3], 1.0f, Color.WHITE.getRGB());

                    GlStateManager.resetColor();
                }
                break;
        }
    };

    private int getColor(Entity entity) {
        switch (colorMode.getValue()) {
            case "Client Color":
                return Client.INSTANCE.clientColor.getRGB();
            case "Multicolor":
                return entity.getColor();
            case "Random":
                return entity.randomColor;
            case "Rainbow":
                return ColorUtil.getRainbow(5000, 0, 1F).getRGB();
        }
        return 0;
    }
}