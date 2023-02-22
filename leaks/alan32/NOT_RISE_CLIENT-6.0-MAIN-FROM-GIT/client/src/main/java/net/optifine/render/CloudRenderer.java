package net.optifine.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

public class CloudRenderer {
    private final Minecraft mc;
    private boolean updated = false;
    private boolean renderFancy = false;
    int cloudTickCounter;
    private Vec3 cloudColor;
    float partialTicks;
    private boolean updateRenderFancy = false;
    private int updateCloudTickCounter = 0;
    private Vec3 updateCloudColor = new Vec3(-1.0D, -1.0D, -1.0D);
    private double updatePlayerX = 0.0D;
    private double updatePlayerY = 0.0D;
    private double updatePlayerZ = 0.0D;
    private int glListClouds = -1;

    public CloudRenderer(final Minecraft mc) {
        this.mc = mc;
        this.glListClouds = GLAllocation.generateDisplayLists(1);
    }

    public void prepareToRender(final boolean renderFancy, final int cloudTickCounter, final float partialTicks, final Vec3 cloudColor) {
        this.renderFancy = renderFancy;
        this.cloudTickCounter = cloudTickCounter;
        this.partialTicks = partialTicks;
        this.cloudColor = cloudColor;
    }

    public boolean shouldUpdateGlList() {
        if (!this.updated) {
            return true;
        } else if (this.renderFancy != this.updateRenderFancy) {
            return true;
        } else if (this.cloudTickCounter >= this.updateCloudTickCounter + 20) {
            return true;
        } else if (Math.abs(this.cloudColor.xCoord - this.updateCloudColor.xCoord) > 0.003D) {
            return true;
        } else if (Math.abs(this.cloudColor.yCoord - this.updateCloudColor.yCoord) > 0.003D) {
            return true;
        } else if (Math.abs(this.cloudColor.zCoord - this.updateCloudColor.zCoord) > 0.003D) {
            return true;
        } else {
            final Entity entity = this.mc.getRenderViewEntity();
            final boolean flag = this.updatePlayerY + (double) entity.getEyeHeight() < 128.0D + (double) (this.mc.gameSettings.ofCloudsHeight * 128.0F);
            final boolean flag1 = entity.prevPosY + (double) entity.getEyeHeight() < 128.0D + (double) (this.mc.gameSettings.ofCloudsHeight * 128.0F);
            return flag1 != flag;
        }
    }

    public void startUpdateGlList() {
        GL11.glNewList(this.glListClouds, GL11.GL_COMPILE);
    }

    public void endUpdateGlList() {
        GL11.glEndList();
        this.updateRenderFancy = this.renderFancy;
        this.updateCloudTickCounter = this.cloudTickCounter;
        this.updateCloudColor = this.cloudColor;
        this.updatePlayerX = this.mc.getRenderViewEntity().prevPosX;
        this.updatePlayerY = this.mc.getRenderViewEntity().prevPosY;
        this.updatePlayerZ = this.mc.getRenderViewEntity().prevPosZ;
        this.updated = true;
        GlStateManager.resetColor();
    }

    public void renderGlList() {
        final Entity entity = this.mc.getRenderViewEntity();
        final double d0 = entity.prevPosX + (entity.posX - entity.prevPosX) * (double) this.partialTicks;
        final double d1 = entity.prevPosY + (entity.posY - entity.prevPosY) * (double) this.partialTicks;
        final double d2 = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * (double) this.partialTicks;
        final double d3 = (float) (this.cloudTickCounter - this.updateCloudTickCounter) + this.partialTicks;
        final float f = (float) (d0 - this.updatePlayerX + d3 * 0.03D);
        final float f1 = (float) (d1 - this.updatePlayerY);
        final float f2 = (float) (d2 - this.updatePlayerZ);
        GlStateManager.pushMatrix();

        if (this.renderFancy) {
            GlStateManager.translate(-f / 12.0F, -f1, -f2 / 12.0F);
        } else {
            GlStateManager.translate(-f, -f1, -f2);
        }

        GlStateManager.callList(this.glListClouds);
        GlStateManager.popMatrix();
        GlStateManager.resetColor();
    }

    public void reset() {
        this.updated = false;
    }
}
