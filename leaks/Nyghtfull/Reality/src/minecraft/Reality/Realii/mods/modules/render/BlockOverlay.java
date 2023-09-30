package Reality.Realii.mods.modules.render;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import org.lwjgl.opengl.GL11;
import Reality.Realii.Client;
import Reality.Realii.event.EventHandler;
import Reality.Realii.event.events.rendering.EventRender2D;
import Reality.Realii.event.events.rendering.EventRender3D;
import Reality.Realii.mods.Module;
import Reality.Realii.mods.ModuleType;
import Reality.Realii.mods.modules.ClientSettings;
import Reality.Realii.utils.render.RenderUtil;

import java.awt.*;

public class BlockOverlay extends Module {

    public BlockOverlay() {
        super("BlockOverlay", ModuleType.Render);
    }


    @EventHandler
    public void onRender(final EventRender2D event) {
        if (mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            final FontRenderer fr = mc.fontRendererObj;
            final BlockPos pos = mc.objectMouseOver.getBlockPos();
            final Block block = mc.theWorld.getBlockState(pos).getBlock();
            final int id = Block.getIdFromBlock(block);
            final String s = String.valueOf(String.valueOf(block.getLocalizedName())) + " ID:" + id;
            final String s2 = block.getLocalizedName();
            final String s3 = " ID:" + id;
            if (mc.objectMouseOver != null) {
                final ScaledResolution res = new ScaledResolution(mc);
                final int x = res.getScaledWidth() / 2 + 6;
                final int y = res.getScaledHeight() / 2 - 1;
                Client.fontLoaders.msFont18.drawStringWithShadow(s2, x + 4.0f, y - 2.65f, Color.WHITE.getRGB());
            }
        }
    }

    public static int reAlpha(final int color, final float alpha) {
        final Color c = new Color(color);
        final float r = 0.003921569f * c.getRed();
        final float g = 0.003921569f * c.getGreen();
        final float b = 0.003921569f * c.getBlue();
        return new Color(r, g, b, alpha).getRGB();
    }

    @EventHandler
    public void onRender3D(final EventRender3D event) {
        if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            final BlockPos pos = mc.objectMouseOver.getBlockPos();
            final Block block = mc.theWorld.getBlockState(pos).getBlock();
            final String s = block.getLocalizedName();
            mc.getRenderManager();
            final double n = pos.getX();
            mc.getRenderManager();
            final double x = n - RenderManager.renderPosX;
            mc.getRenderManager();
            final double n2 = pos.getY();
            mc.getRenderManager();
            final double y = n2 - RenderManager.renderPosY;
            mc.getRenderManager();
            final double n3 = pos.getZ();
            mc.getRenderManager();
            final double z = n3 - RenderManager.renderPosZ;
            GL11.glPushMatrix();
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glDisable(3553);
            GL11.glEnable(2848);
            GL11.glDisable(2929);
            GL11.glDepthMask(false);
            GL11.glColor4f(ClientSettings.r.getValue().intValue() / 255.0f, ClientSettings.g.getValue().intValue() / 255.0f, ClientSettings.b.getValue().intValue() / 255.0f, 0.15f);
            final double minX = (block instanceof BlockStairs || Block.getIdFromBlock(block) == 134) ? 0.0 : block.getBlockBoundsMinX();
            final double minY = (block instanceof BlockStairs || Block.getIdFromBlock(block) == 134) ? 0.0 : block.getBlockBoundsMinY();
            final double minZ = (block instanceof BlockStairs || Block.getIdFromBlock(block) == 134) ? 0.0 : block.getBlockBoundsMinZ();
            RenderUtil.drawBoundingBox(new AxisAlignedBB(x + minX, y + minY, z + minZ, x + block.getBlockBoundsMaxX(), y + block.getBlockBoundsMaxY(), z + block.getBlockBoundsMaxZ()));
            GL11.glColor4f(ClientSettings.r.getValue().intValue() / 255.0f, ClientSettings.g.getValue().intValue() / 255.0f, ClientSettings.b.getValue().intValue() / 255.0f, 1.0f);
            GL11.glLineWidth(0.5f);
            RenderUtil.drawOutlinedBoundingBox(new AxisAlignedBB(x + minX, y + minY, z + minZ, x + block.getBlockBoundsMaxX(), y + block.getBlockBoundsMaxY(), z + block.getBlockBoundsMaxZ()));
            GL11.glDisable(2848);
            GL11.glEnable(3553);
            GL11.glEnable(2929);
            GL11.glDepthMask(true);
            GL11.glDisable(3042);
            GL11.glPopMatrix();
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        }
    }
}
