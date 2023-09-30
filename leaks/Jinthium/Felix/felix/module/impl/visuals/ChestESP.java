package felix.module.impl.visuals;

import net.minecraft.block.Block;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.*;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

import org.lwjgl.opengl.GL11;

import felix.api.annotations.Handler;
import felix.events.render.EventRender3D;
import felix.module.Module;
import felix.util.other.Vec4f;
import felix.util.visual.GLUProjection;
import felix.util.visual.RenderUtil;
import felix.value.impl.BooleanValue;
import felix.value.impl.ColorValue;
import felix.value.impl.EnumValue;
import felix.value.impl.NumberValue;

import javax.vecmath.Vector3d;
import java.awt.*;

public class ChestESP extends Module {
	
	private EnumValue<Mode> mode = new EnumValue<>("ChestESP Mode", Mode.New);
	
	private NumberValue<Integer> alpha = new NumberValue<>("Alpha", 70, 30, 255);
	
	private ColorValue color = new ColorValue("Chest Color", new Color(0, 130, 255).getRGB());
	
    public ChestESP() {
        super("ChestESP",0, ModuleCategory.VISUALS);
        addValues(mode, color, alpha);
    }
    
    private enum Mode {
    	New, Normal;
    }

    @Handler
    public void onRender3D(EventRender3D eventRender) {
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        switch (mode.getValue()) {
        case New:
        block4 : for (Object o : mc.theWorld.loadedTileEntityList) {
            TileEntity tileEntity = (TileEntity)o;
            if (tileEntity == null || !isStorage(tileEntity)) continue;
            double posX = tileEntity.getPos().getX();
            double posY = tileEntity.getPos().getY();
            double posZ = tileEntity.getPos().getZ();
            Block block;
            AxisAlignedBB axisAlignedBB = null;
            if (tileEntity instanceof TileEntityChest) {
                block = mc.theWorld.getBlockState(new BlockPos(posX, posY, posZ)).getBlock();
                Block x1 = mc.theWorld.getBlockState(new BlockPos(posX + 1.0, posY, posZ)).getBlock();
                Block x2 = mc.theWorld.getBlockState(new BlockPos(posX - 1.0, posY, posZ)).getBlock();
                Block z1 = mc.theWorld.getBlockState(new BlockPos(posX, posY, posZ + 1.0)).getBlock();
                Block z2 = mc.theWorld.getBlockState(new BlockPos(posX, posY, posZ - 1.0)).getBlock();
                if (block != Blocks.trapped_chest) {
                    if (x1 == Blocks.chest) {
                        axisAlignedBB = new AxisAlignedBB(posX + 0.05000000074505806 - mc.getRenderManager().getRenderPosX(), posY - mc.getRenderManager().getRenderPosY(), posZ + 0.05000000074505806 - mc.getRenderManager().getRenderPosZ(), posX + 1.9500000476837158 - mc.getRenderManager().getRenderPosX(), posY + 0.8999999761581421 - mc.getRenderManager().getRenderPosY(), posZ + 0.949999988079071 - mc.getRenderManager().getRenderPosZ());
                    } else if (z2 == Blocks.chest) {
                        axisAlignedBB = new AxisAlignedBB(posX + 0.05000000074505806 - mc.getRenderManager().getRenderPosX(), posY - mc.getRenderManager().getRenderPosY(), posZ + 0.05000000074505806 - mc.getRenderManager().getRenderPosZ() - 1.0, posX + 0.949999988079071 - mc.getRenderManager().getRenderPosX(), posY + 0.8999999761581421 - mc.getRenderManager().getRenderPosY(), posZ + 0.949999988079071 - mc.getRenderManager().getRenderPosZ());
                    } else if (x1 != Blocks.chest && x2 != Blocks.chest && z1 != Blocks.chest && z2 != Blocks.chest) {
                        axisAlignedBB = new AxisAlignedBB(posX + 0.05000000074505806 - mc.getRenderManager().getRenderPosX(), posY - mc.getRenderManager().getRenderPosY(), posZ + 0.05000000074505806 - mc.getRenderManager().getRenderPosZ(), posX + 0.949999988079071 - mc.getRenderManager().getRenderPosX(), posY + 0.8999999761581421 - mc.getRenderManager().getRenderPosY(), posZ + 0.949999988079071 - mc.getRenderManager().getRenderPosZ());
                    }
                } else if (x1 == Blocks.trapped_chest) {
                    axisAlignedBB = new AxisAlignedBB(posX + 0.05000000074505806 - mc.getRenderManager().getRenderPosX(), posY - mc.getRenderManager().getRenderPosY(), posZ + 0.05000000074505806 - mc.getRenderManager().getRenderPosZ(), posX + 1.9500000476837158 - mc.getRenderManager().getRenderPosX(), posY + 0.8999999761581421 - mc.getRenderManager().getRenderPosY(), posZ + 0.949999988079071 - mc.getRenderManager().getRenderPosZ());
                } else if (z2 == Blocks.trapped_chest) {
                    axisAlignedBB = new AxisAlignedBB(posX + 0.05000000074505806 - mc.getRenderManager().getRenderPosX(), posY - mc.getRenderManager().getRenderPosY(), posZ + 0.05000000074505806 - mc.getRenderManager().getRenderPosZ() - 1.0, posX + 0.949999988079071 - mc.getRenderManager().getRenderPosX(), posY + 0.8999999761581421 - mc.getRenderManager().getRenderPosY(), posZ + 0.949999988079071 - mc.getRenderManager().getRenderPosZ());
                } else if (x1 != Blocks.trapped_chest && x2 != Blocks.trapped_chest && z1 != Blocks.trapped_chest && z2 != Blocks.trapped_chest) {
                    axisAlignedBB = new AxisAlignedBB(posX + 0.05000000074505806 - mc.getRenderManager().getRenderPosX(), posY - mc.getRenderManager().getRenderPosY(), posZ + 0.05000000074505806 - mc.getRenderManager().getRenderPosZ(), posX + 0.949999988079071 - mc.getRenderManager().getRenderPosX(), posY + 0.8999999761581421 - mc.getRenderManager().getRenderPosY(), posZ + 0.949999988079071 - mc.getRenderManager().getRenderPosZ());
                }
            } else {
                axisAlignedBB = new AxisAlignedBB(posX - mc.getRenderManager().getRenderPosX(), posY - mc.getRenderManager().getRenderPosY(), posZ - mc.getRenderManager().getRenderPosZ(), posX + 1.0 - mc.getRenderManager().getRenderPosX(), posY + 1.0 - mc.getRenderManager().getRenderPosY(), posZ + 1.0 - mc.getRenderManager().getRenderPosZ());
            }
            if (axisAlignedBB == null) continue block4;
            Color color = new Color(this.color.getValue());
            int r = color.getRed();
            int g = color.getGreen();
            int b = color.getBlue();
            int[] colors = new int[] {r, g, b, 255};
            //RenderUtil.pre3D();
            RenderHelper.drawCompleteBox(axisAlignedBB, 1.0f, toRGBAHex(colors[0] / 255.0f, colors[1] / 255.0f, colors[2] / 255.0f, 0.1254902f), toRGBAHex(colors[0] / 255.0f, colors[1] / 255.0f, colors[2] / 255.0f, 1.0f));
            //RenderUtil.post3D();
            AxisAlignedBB bb = null;
            if (tileEntity instanceof TileEntityChest) {

                block = mc.theWorld.getBlockState(new BlockPos(posX, posY, posZ)).getBlock();
                Block posX1 = mc.theWorld.getBlockState(new BlockPos(posX + 1.0, posY, posZ)).getBlock();
                Block posX2 = mc.theWorld.getBlockState(new BlockPos(posX - 1.0, posY, posZ)).getBlock();
                Block posZ1 = mc.theWorld.getBlockState(new BlockPos(posX, posY, posZ + 1.0)).getBlock();
                Block posZ2 = mc.theWorld.getBlockState(new BlockPos(posX, posY, posZ - 1.0)).getBlock();
                if (block != Blocks.trapped_chest) {
                    if (posX1 == Blocks.chest) {
                        bb = new AxisAlignedBB(posX + 0.05000000074505806, posY, posZ + 0.05000000074505806, posX + 1.9500000476837158, posY + 0.8999999761581421, posZ + 0.949999988079071);
                    } else if (posZ2 == Blocks.chest) {
                        bb = new AxisAlignedBB(posX + 0.05000000074505806, posY, posZ + 0.05000000074505806 - 1.0, posX + 0.949999988079071, posY + 0.8999999761581421, posZ + 0.949999988079071);
                    } else if (posX1 != Blocks.chest && posX2 != Blocks.chest && posZ1 != Blocks.chest && posZ2 != Blocks.chest) {
                        bb = new AxisAlignedBB(posX + 0.05000000074505806, posY, posZ + 0.05000000074505806, posX + 0.949999988079071, posY + 0.8999999761581421, posZ + 0.949999988079071);
                    }
                } else if (posX1 == Blocks.trapped_chest) {
                    bb = new AxisAlignedBB(posX + 0.05000000074505806, posY, posZ + 0.05000000074505806, posX + 1.9500000476837158, posY + 0.8999999761581421, posZ + 0.949999988079071);
                } else if (posZ2 == Blocks.trapped_chest) {
                    bb = new AxisAlignedBB(posX + 0.05000000074505806, posY, posZ + 0.05000000074505806 - 1.0, posX + 0.949999988079071, posY + 0.8999999761581421, posZ + 0.949999988079071);
                } else if (posX1 != Blocks.trapped_chest && posX2 != Blocks.trapped_chest && posZ1 != Blocks.trapped_chest && posZ2 != Blocks.trapped_chest) {
                    bb = new AxisAlignedBB(posX + 0.05000000074505806, posY, posZ + 0.05000000074505806, posX + 0.949999988079071, posY + 0.8999999761581421, posZ + 0.949999988079071);
                }
            } else {
                bb = new AxisAlignedBB(posX, posY, posZ, posX + 1.0, posY + 1.0, posZ + 1.0);
            }
            if (bb == null) break;
            Vector3d[] corners = new Vector3d[]{new Vector3d(bb.minX, bb.minY, bb.minZ), new Vector3d(bb.maxX, bb.maxY, bb.maxZ), new Vector3d(bb.minX, bb.maxY, bb.maxZ), new Vector3d(bb.minX, bb.minY, bb.maxZ), new Vector3d(bb.maxX, bb.minY, bb.maxZ), new Vector3d(bb.maxX, bb.minY, bb.minZ), new Vector3d(bb.maxX, bb.maxY, bb.minZ), new Vector3d(bb.minX, bb.maxY, bb.minZ)};
            GLUProjection.Projection result = null;
            Vec4f transformed = new Vec4f((float)scaledResolution.getScaledWidth() * 2.0f, (float)scaledResolution.getScaledHeight() * 2.0f, -1.0f, -1.0f);
            for (Vector3d vec : corners) {
                result = GLUProjection.getInstance().project(vec.x - mc.getRenderManager().viewerPosX, vec.y - mc.getRenderManager().viewerPosY, vec.z - mc.getRenderManager().viewerPosZ, GLUProjection.ClampMode.NONE, true);
                transformed.setX((float)Math.min((double)transformed.getX(), result.getX()));
                transformed.setY((float)Math.min((double)transformed.getY(), result.getY()));
                transformed.setW((float)Math.max((double)transformed.getW(), result.getX()));
                transformed.setH((float)Math.max((double)transformed.getH(), result.getY()));
            }
            if (result == null) break;
            
        	}
        	break;
		case Normal:
			for (final TileEntity tileEntity : mc.theWorld.loadedTileEntityList) {
                if (tileEntity instanceof TileEntityChest) {
                	final RenderManager renderManager = mc.getRenderManager();
                    double renderX = tileEntity.getPos().getX() - renderManager.getRenderPosX();
                    double renderY = tileEntity.getPos().getY() - renderManager.getRenderPosY();
                    double renderZ = tileEntity.getPos().getZ() - renderManager.getRenderPosZ();
                    GL11.glPushMatrix();
                    GL11.glTranslated(renderX, renderY, renderZ);
                    Color color = new Color(this.color.getValue());
                    int r = color.getRed();
                    int g = color.getGreen();
                    int b = color.getBlue();
                    drawChestEsp(tileEntity, 0.0D, 0.0D, 0.0D, r, g, b, alpha.getValue());
                    GL11.glTranslated(-renderX, -renderY, -renderZ);
                    GL11.glPopMatrix();
                }
            }
			break;
        }
    }
    
    public static void drawChestEsp(TileEntity tileEntity, double x, double y, double z, int r, int g, int b, int a) {
		double xOff = 0;
		double zOff = 0;
		double xOff2 = 0;
		double zOff2 = 0;

		if (tileEntity instanceof TileEntityChest) {
			TileEntityChest tileEntityChest = (TileEntityChest) tileEntity;
			if (tileEntityChest.adjacentChestXPos != null) {
				xOff = -1;
				xOff2 = -1;
			} else if (tileEntityChest.adjacentChestXNeg != null) {
				xOff = -1 - 0.002;
				xOff2 = 0.0125;
			} else if (tileEntityChest.adjacentChestZPos != null) {
				zOff = -1;
				zOff2 = -1;
			} else if (tileEntityChest.adjacentChestZNeg != null) {
				zOff = -1 - 0.002;
				zOff2 = 0.0125;
			}
		}

		if (xOff == -1 || xOff2 == -1 || zOff == -1 || zOff2 == -1)
			return;

		GlStateManager.pushAttrib();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GL11.glBlendFunc(770, 771);

		//GlStateManager.disableLighting();
		//GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(false);
		GL11.glColor4f(r / 255f, g / 255f, b / 255f, a / 255f);
		GL11.glLineWidth(1.5F);
		
		
		GL11.glBegin(7);
		GL11.glVertex3d(x + 0.0625 + xOff, y, z + 0.0625 + zOff);
		GL11.glVertex3d(x + 0.0625 + xOff, y - (0.062f * 2) + 1.0D, z + 0.0625 + zOff);
		GL11.glVertex3d(x - 0.0625 + xOff2 + 1.0D, y - (0.062f * 2) + 1.0D, z + 0.0625 + zOff);
		GL11.glVertex3d(x - 0.0625 + xOff2 + 1.0D, y, z + 0.0625 + zOff);
		GL11.glVertex3d(x - 0.0625 + xOff2 + 1.0D, y, z + 0.0625 + zOff);
		GL11.glVertex3d(x - 0.0625 + xOff2 + 1.0D, y - (0.062f * 2) + 1.0D, z + 0.0625 + zOff);
		GL11.glVertex3d(x - 0.0625 + xOff2 + 1.0D, y - (0.062f * 2) + 1.0D, z - 0.0625 + zOff2 + 1.0D);
		GL11.glVertex3d(x - 0.0625 + xOff2 + 1.0D, y, z - 0.0625 + zOff2 + 1.0D);
		GL11.glVertex3d(x - 0.0625 + xOff2 + 1.0D, y, z - 0.0625 + zOff2 + 1.0D);
		GL11.glVertex3d(x - 0.0625 + xOff2 + 1.0D, y - (0.062f * 2) + 1.0D, z - 0.0625 + zOff2 + 1.0D);
		GL11.glVertex3d(x + 0.0625 + xOff, y - (0.062f * 2) + 1.0D, z - 0.0625 + zOff2 + 1.0D);
		GL11.glVertex3d(x + 0.0625 + xOff, y, z - 0.0625 + zOff2 + 1.0D);
		GL11.glVertex3d(x + 0.0625 + xOff, y, z - 0.0625 + zOff2 + 1.0D);
		GL11.glVertex3d(x + 0.0625 + xOff, y - (0.062f * 2) + 1.0D, z - 0.0625 + zOff2 + 1.0D);
		GL11.glVertex3d(x + 0.0625 + xOff, y - (0.062f * 2) + 1.0D, z + 0.0625 + zOff);
		GL11.glVertex3d(x + 0.0625 + xOff, y, z + 0.0625 + zOff);
		GL11.glVertex3d(x + 0.0625 + xOff, y, z + 0.0625 + zOff);
		GL11.glVertex3d(x - 0.0625 + xOff2 + 1.0D, y, z + 0.0625 + zOff);
		GL11.glVertex3d(x - 0.0625 + xOff2 + 1.0D, y, z - 0.0625 + zOff2 + 1.0D);
		GL11.glVertex3d(x + 0.0625 + xOff, y, z - 0.0625 + zOff2 + 1.0D);
		GL11.glVertex3d(x + 0.0625 + xOff, y - (0.062f * 2) + 1.0D, z + 0.0625 + zOff);
		GL11.glVertex3d(x - 0.0625 + xOff2 + 1.0D, y - (0.062f * 2) + 1.0D, z + 0.0625 + zOff);
		GL11.glVertex3d(x - 0.0625 + xOff2 + 1.0D, y - (0.062f * 2) + 1.0D, z - 0.0625 + zOff2 + 1.0D);
		GL11.glVertex3d(x + 0.0625 + xOff, y - (0.062f * 2) + 1.0D, z - 0.0625 + zOff2 + 1.0D);
		GL11.glEnd();

		GlStateManager.enableTexture2D();
		//GlStateManager.enableLighting();
		GlStateManager.disableBlend();
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(true);
		GlStateManager.popAttrib();
	}

	public static boolean isStorage(TileEntity entity) {
        return entity instanceof TileEntityChest || entity instanceof TileEntityEnderChest;
    }
    
    public static int toRGBAHex(float r, float g, float b, float a) {
        return ((int)(a * 255.0f) & 255) << 24 | ((int)(r * 255.0f) & 255) << 16 | ((int)(g * 255.0f) & 255) << 8 | (int)(b * 255.0f) & 255;
    }
}
