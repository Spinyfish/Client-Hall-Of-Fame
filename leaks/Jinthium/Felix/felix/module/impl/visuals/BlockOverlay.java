package felix.module.impl.visuals;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import felix.api.annotations.Handler;
import felix.events.render.EventRender2D;
import felix.events.render.EventRender3D;
import felix.module.Module;
import felix.util.visual.RenderUtil;
import felix.value.impl.ColorValue;
import felix.value.impl.NumberValue;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;

public class BlockOverlay extends Module {
	
	private ColorValue color = new ColorValue("Overlay Color", new Color(0, 130, 255).getRGB());
	
	public BlockOverlay() {
		super("BlockOverlay", 0, ModuleCategory.VISUALS);
		addValues(color);
	}
	
	@Handler
    public void onRender3D(final EventRender3D event) {
        if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            BlockPos pos = mc.objectMouseOver.getBlockPos();
            Block block = mc.theWorld.getBlockState(pos).getBlock();
            RenderManager renderManager = mc.getRenderManager();
            String s = block.getLocalizedName();
            mc.getRenderManager();
            double x = (double)pos.getX() - renderManager.getRenderPosX();
            mc.getRenderManager();
            double y = (double)pos.getY() - renderManager.getRenderPosY();
            mc.getRenderManager();
            double z = (double)pos.getZ() - renderManager.getRenderPosZ();
            GL11.glPushMatrix();
            GL11.glEnable((int)3042);
            GL11.glBlendFunc((int)770, (int)771);
            GL11.glDisable((int)3553);
            GL11.glEnable((int)2848);
            GL11.glDisable((int)2929);
            GL11.glDepthMask((boolean)false);
            Color c = new Color(color.getValue());
            int r = c.getRed();
            int g = c.getGreen();
            int b = c.getBlue();
            RenderUtil.glColor(new Color(r, g, b, 50).getRGB());
            double minX = block instanceof BlockStairs || Block.getIdFromBlock((Block)block) == 134 ? 0.0 : block.getBlockBoundsMinX();
            double minY = block instanceof BlockStairs || Block.getIdFromBlock((Block)block) == 134 ? 0.0 : block.getBlockBoundsMinY();
            double minZ = block instanceof BlockStairs || Block.getIdFromBlock((Block)block) == 134 ? 0.0 : block.getBlockBoundsMinZ();
            RenderUtil.drawBoundingBox((AxisAlignedBB)new AxisAlignedBB(x + minX, y + minY, z + minZ, x + block.getBlockBoundsMaxX(), y + block.getBlockBoundsMaxY(), z + block.getBlockBoundsMaxZ()));
            RenderUtil.glColor(new Color(color.getValue()).getRGB());
            GL11.glLineWidth((float)0.5f);
            //RenderUtil.drawOutlinedBoundingBox((AxisAlignedBB)new AxisAlignedBB(x + minX, y + minY, z + minZ, x + block.getBlockBoundsMaxX(), y + block.getBlockBoundsMaxY(), z + block.getBlockBoundsMaxZ()));
            GL11.glDisable((int)2848);
            GL11.glEnable((int)3553);
            GL11.glEnable((int)2929);
            GL11.glDepthMask((boolean)true);
            GL11.glDisable((int)3042);
            GL11.glPopMatrix();
        }
        GL11.glColor4f(1f, 1f, 1f, 1f);
    }
	
	public void onEnable() {
		super.onEnable();
	}
	
	public void onDisable() {
		super.onDisable();
	}
}
