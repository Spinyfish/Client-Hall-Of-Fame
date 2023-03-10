/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.util.glu.GLU
 */
package net.minecraft.client.renderer;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public class ActiveRenderInfo {
    public static final IntBuffer VIEWPORT = GLAllocation.createDirectIntBuffer(16);
    public static final FloatBuffer MODELVIEW = GLAllocation.createDirectFloatBuffer(16);
    public static final FloatBuffer PROJECTION = GLAllocation.createDirectFloatBuffer(16);
    public static final FloatBuffer OBJECTCOORDS = GLAllocation.createDirectFloatBuffer(3);
    private static Vec3 position = new Vec3(0.0, 0.0, 0.0);
    private static float rotationX;
    private static float rotationXZ;
    private static float rotationZ;
    private static float rotationYZ;
    private static float rotationXY;

    public static void updateRenderInfo(EntityPlayer entityplayerIn, boolean p_74583_1_) {
        GlStateManager.getFloat(2982, MODELVIEW);
        GlStateManager.getFloat(2983, PROJECTION);
        GL11.glGetInteger((int)2978, (IntBuffer)VIEWPORT);
        float f = (VIEWPORT.get(0) + VIEWPORT.get(2)) / 2;
        float f1 = (VIEWPORT.get(1) + VIEWPORT.get(3)) / 2;
        GLU.gluUnProject((float)f, (float)f1, (float)0.0f, (FloatBuffer)MODELVIEW, (FloatBuffer)PROJECTION, (IntBuffer)VIEWPORT, (FloatBuffer)OBJECTCOORDS);
        position = new Vec3(OBJECTCOORDS.get(0), OBJECTCOORDS.get(1), OBJECTCOORDS.get(2));
        int i = p_74583_1_ ? 1 : 0;
        float f2 = entityplayerIn.rotationPitch;
        float f3 = entityplayerIn.rotationYaw;
        rotationX = MathHelper.cos(f3 * (float)Math.PI / 180.0f) * (float)(1 - i * 2);
        rotationZ = MathHelper.sin(f3 * (float)Math.PI / 180.0f) * (float)(1 - i * 2);
        rotationYZ = -rotationZ * MathHelper.sin(f2 * (float)Math.PI / 180.0f) * (float)(1 - i * 2);
        rotationXY = rotationX * MathHelper.sin(f2 * (float)Math.PI / 180.0f) * (float)(1 - i * 2);
        rotationXZ = MathHelper.cos(f2 * (float)Math.PI / 180.0f);
    }

    public static Vec3 projectViewFromEntity(Entity p_178806_0_, double p_178806_1_) {
        double d0 = p_178806_0_.prevPosX + (p_178806_0_.posX - p_178806_0_.prevPosX) * p_178806_1_;
        double d1 = p_178806_0_.prevPosY + (p_178806_0_.posY - p_178806_0_.prevPosY) * p_178806_1_;
        double d2 = p_178806_0_.prevPosZ + (p_178806_0_.posZ - p_178806_0_.prevPosZ) * p_178806_1_;
        double d3 = d0 + ActiveRenderInfo.position.xCoord;
        double d4 = d1 + ActiveRenderInfo.position.yCoord;
        double d5 = d2 + ActiveRenderInfo.position.zCoord;
        return new Vec3(d3, d4, d5);
    }

    public static Block getBlockAtEntityViewpoint(World worldIn, Entity p_180786_1_, float p_180786_2_) {
        Vec3 vec3 = ActiveRenderInfo.projectViewFromEntity(p_180786_1_, p_180786_2_);
        BlockPos blockpos = new BlockPos(vec3);
        IBlockState iblockstate = worldIn.getBlockState(blockpos);
        Block block = iblockstate.getBlock();
        if (block.getMaterial().isLiquid()) {
            float f1;
            float f = 0.0f;
            if (iblockstate.getBlock() instanceof BlockLiquid) {
                f = BlockLiquid.getLiquidHeightPercent(iblockstate.getValue(BlockLiquid.LEVEL)) - 0.11111111f;
            }
            if (vec3.yCoord >= (double)(f1 = (float)(blockpos.getY() + 1) - f)) {
                block = worldIn.getBlockState(blockpos.up()).getBlock();
            }
        }
        return block;
    }

    public static Vec3 getPosition() {
        return position;
    }

    public static float getRotationX() {
        return rotationX;
    }

    public static float getRotationXZ() {
        return rotationXZ;
    }

    public static float getRotationZ() {
        return rotationZ;
    }

    public static float getRotationYZ() {
        return rotationYZ;
    }

    public static float getRotationXY() {
        return rotationXY;
    }
}

