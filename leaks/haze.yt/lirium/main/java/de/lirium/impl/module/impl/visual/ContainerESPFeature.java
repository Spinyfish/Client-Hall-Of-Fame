package de.lirium.impl.module.impl.visual;

import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import de.lirium.Client;
import de.lirium.base.setting.Dependency;
import de.lirium.base.setting.Value;
import de.lirium.base.setting.impl.CheckBox;
import de.lirium.base.setting.impl.ComboBox;
import de.lirium.base.setting.impl.SliderSetting;
import de.lirium.impl.events.Render2DEvent;
import de.lirium.impl.events.Render3DEvent;
import de.lirium.impl.events.ShaderRenderEvent;
import de.lirium.impl.module.ModuleFeature;
import de.lirium.util.math.InterpolationUtil;
import de.lirium.util.render.ColorUtil;
import de.lirium.util.render.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.*;
import net.minecraft.util.math.AxisAlignedBB;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLSync;

import java.awt.*;
import java.util.function.Predicate;

@ModuleFeature.Info(name = "Container ESP", description = "Highlights containers", category = ModuleFeature.Category.VISUAL)
public class ContainerESPFeature extends ModuleFeature {

    @Value(name = "Mode")
    final ComboBox<String> mode = new ComboBox<>("2D", new String[]{"Box", "Minecraft"});

    @Value(name = "Color Mode")
    private final ComboBox<String> colorMode = new ComboBox<>("Client Color", new String[] {"Random", "Multicolor", "Rainbow"}, new Dependency<>(mode, "Box", "Minecraft"));

    @Value(name = "Chests")
    final CheckBox chests = new CheckBox(true);

    @Value(name = "Ender Chests")
    final CheckBox enderChests = new CheckBox(true);

    @Value(name = "Shulker Boxes")
    final CheckBox shulkerBoxes = new CheckBox(true);

    @Value(name = "Alpha")
    final SliderSetting<Float> alpha = new SliderSetting<>(0.3F, 0.1F, 1F, new Dependency<>(mode, "Box"));

    private final Predicate<TileEntity> predicate = tileEntity -> {
        if (tileEntity instanceof TileEntityChest) {
            return chests.getValue();
        } else if (tileEntity instanceof TileEntityEnderChest) {
            return enderChests.getValue();
        } else if (tileEntity instanceof TileEntityShulkerBox) {
            return shulkerBoxes.getValue();
        }
        return false;
    };

    private final Frustum frustum = new Frustum();

    @EventHandler
    public final Listener<ShaderRenderEvent> shaderRenderEventListener = e -> {
        if (mode.getValue().equalsIgnoreCase("Minecraft")) {
            for (final TileEntity entity : getWorld().loadedTileEntityList) {
                if (predicate.test(entity)) {
                    GlStateManager.disableTexture2D();
                    setColor(getColor(entity));
                    TileEntityRendererDispatcher.instance.drawingBatch = true;
                    TileEntityRendererDispatcher.instance.renderTileEntity(entity, getTimer().partialTicks, 0);
                    TileEntityRendererDispatcher.instance.drawingBatch = false;
                    GlStateManager.enableTexture2D();
                }
            }
        }
    };
    
    @EventHandler
    public final Listener<Render3DEvent> render3DEventListener = e -> {
        switch (mode.getValue()) {
            case "Box":
                for (final TileEntity entity : getWorld().loadedTileEntityList) {
                    final double x = (entity.getPos().getX() - mc.getRenderManager().renderPosX);
                    final double y = (entity.getPos().getY() - mc.getRenderManager().renderPosY);
                    final double z = (entity.getPos().getZ() - mc.getRenderManager().renderPosZ);
                    if (predicate.test(entity)) {
                        final AxisAlignedBB bounding = getWorld().getBlockState(entity.getPos()).getSelectedBoundingBox(mc.world, entity.getPos());
                        final Color color = getColor(entity);
                        GL11.glColor4f(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, alpha.getValue());
                        RenderUtil.drawBox(x, y, z, 1, bounding.maxY - bounding.minY);
                    }
                }
                break;
        }
    };

    @EventHandler
    public final Listener<Render2DEvent> render2DEventListener = e -> {
        if (!mode.getValue().equalsIgnoreCase("2D")) return;
        //frustum.setPosition(mc.getRenderManager().renderPosX, mc.getRenderManager().renderPosY, mc.getRenderManager().renderPosZ);
        for (final TileEntity entity : getWorld().loadedTileEntityList) {
            if (!frustum.isBoundingBoxInFrustum(getWorld().getBlockState(entity.getPos()).getSelectedBoundingBox(getWorld(), entity.getPos()).offset(-mc.getRenderManager().renderPosX, -mc.getRenderManager().renderPosY, -mc.getRenderManager().renderPosZ)))
                continue;
            if (!predicate.test(entity)) {
                continue;
            }
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
    };

    private Color getColor(TileEntity entity) {
        switch (colorMode.getValue()) {
            case "Client Color":
                return Client.INSTANCE.clientColor;
            case "Multicolor":
                return entity.getColor();
            case "Random":
                return entity.randomColor;
            case "Rainbow":
                return ColorUtil.getRainbow(5000, 0, 1F);
        }
        return Color.white;
    }

    private void setColor(Color color) {
        GL11.glColor4f(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, 1F);
    }
}
