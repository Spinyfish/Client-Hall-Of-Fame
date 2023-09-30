package club.pulsive.impl.util.render;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.List;

import club.pulsive.impl.util.math.apache.ApacheMath;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import static org.lwjgl.opengl.GL11.*; 
/**
 * @author antja03
 */
public class GuiUtil {
	  private static final Frustum frustum = new Frustum();
	  protected static Minecraft mc = Minecraft.getMinecraft();
	    private static final FloatBuffer windPos = BufferUtils.createFloatBuffer(4);
	    private static final IntBuffer intBuffer = GLAllocation.createDirectIntBuffer(16);
	    private static final FloatBuffer floatBuffer1 = GLAllocation.createDirectFloatBuffer(16);
	    private static final FloatBuffer floatBuffer2 = GLAllocation.createDirectFloatBuffer(16);
	 public static boolean isInView(Entity ent) {
	        frustum.setPosition(Minecraft.getMinecraft().getRenderViewEntity().posX, Minecraft.getMinecraft().getRenderViewEntity().posY, Minecraft.getMinecraft().getRenderViewEntity().posZ);
	        return frustum.isBoundingBoxInFrustum(ent.getEntityBoundingBox());
	    }

	 public static double interpolate(double current, double old, double scale) {
	        return old + (current - old) * scale;
	    }
	 
	    public static double[] e277(Entity entity) {
	        float ticks = mc.timer.renderPartialTicks;
	        return new double[]{
	                interpolate(entity.lastTickPosX, entity.posX, ticks) - mc.getRenderManager().viewerPosX,
	                interpolate(entity.lastTickPosY, entity.posY, ticks) - mc.getRenderManager().viewerPosY,
	                interpolate(entity.lastTickPosZ, entity.posZ, ticks) - mc.getRenderManager().viewerPosZ
	        };
	    }
	    
	    public static Vector4f e256(Entity entity) {
	        final double[] renderingEntityPos = e277(entity);
	        final double entityRenderWidth = entity.width / 1.5;
	        final AxisAlignedBB bb = new AxisAlignedBB(renderingEntityPos[0] - entityRenderWidth,
	                renderingEntityPos[1], renderingEntityPos[2] - entityRenderWidth, renderingEntityPos[0] + entityRenderWidth,
	                renderingEntityPos[1] + entity.height + (entity.isSneaking() ? -0.3 : 0.18), renderingEntityPos[2] + entityRenderWidth).expand(0.15, 0.15, 0.15);

	        final List<Vector3f> vectors = Arrays.asList(
	                new Vector3f((float) bb.minX, (float) bb.minY, (float) bb.minZ),
	                new Vector3f((float) bb.minX, (float) bb.maxY, (float) bb.minZ),
	                new Vector3f((float) bb.maxX, (float) bb.minY, (float) bb.minZ),
	                new Vector3f((float) bb.maxX, (float) bb.maxY, (float) bb.minZ),
	                new Vector3f((float) bb.minX, (float) bb.minY, (float) bb.maxZ),
	                new Vector3f((float) bb.minX, (float) bb.maxY, (float) bb.maxZ),
	                new Vector3f((float) bb.maxX, (float) bb.minY, (float) bb.maxZ),
	                new Vector3f((float) bb.maxX, (float) bb.maxY, (float) bb.maxZ));

	        Vector4f entityPos2 = new Vector4f(Float.MAX_VALUE, Float.MAX_VALUE, -1.0f, -1.0f);
	        
	        ScaledResolution sr = new ScaledResolution(mc);
	        for (Vector3f vector3f : vectors) {
	            vector3f = e888(vector3f.x, vector3f.y, vector3f.z, sr.getScaleFactor());
	            if (vector3f != null && vector3f.z >= 0.0 && vector3f.z < 1.0) {
	                entityPos2.x = ApacheMath.min(vector3f.x, entityPos2.x);
	                entityPos2.y = ApacheMath.min(vector3f.y, entityPos2.y);
	                entityPos2.z = ApacheMath.max(vector3f.x, entityPos2.z);
	                entityPos2.w = ApacheMath.max(vector3f.y, entityPos2.w);
	            }
	        }
	       
	        return entityPos2;
	    }
	    
	    public static Vector3f e888(float x, float y, float z, int scaleFactor) {
	        glGetFloat(GL_MODELVIEW_MATRIX, floatBuffer1);
	        glGetFloat(GL_PROJECTION_MATRIX, floatBuffer2);
	        glGetInteger(GL_VIEWPORT, intBuffer);
	        if (GLU.gluProject(x, y, z, floatBuffer1, floatBuffer2, intBuffer, windPos)) {
	            return new Vector3f(windPos.get(0) / scaleFactor, (mc.displayHeight - windPos.get(1)) / scaleFactor, windPos.get(2));
	        }
	        return null;
	    }
		

}
