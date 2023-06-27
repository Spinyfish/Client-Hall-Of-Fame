package rip.athena.client.modules.impl.render;

import rip.athena.client.config.*;
import java.awt.*;
import rip.athena.client.modules.*;
import rip.athena.client.events.types.render.*;
import net.minecraft.block.*;
import rip.athena.client.events.*;
import org.lwjgl.opengl.*;
import net.minecraft.world.*;
import net.minecraft.client.renderer.*;
import net.minecraft.util.*;
import net.minecraft.client.renderer.vertex.*;

public class BlockOverlay extends Module
{
    @ConfigValue.List(name = "Mode", values = { "Outline", "Highlight", "Both" })
    private String mode;
    @ConfigValue.Color(name = "Outline Color")
    private Color vColor;
    @ConfigValue.Color(name = "Highlight Color")
    private Color hColor;
    @ConfigValue.Float(name = "Line Width", min = 0.1f, max = 7.5f)
    private float lineWidth;
    @ConfigValue.Boolean(name = "Chroma")
    private boolean isUsingChroma;
    
    public BlockOverlay() {
        super("Block Overlay", Category.RENDER, "Athena/gui/mods/blockoverlay.png");
        this.mode = "Outline";
        this.vColor = Color.BLUE;
        this.hColor = Color.BLUE;
        this.lineWidth = 1.0f;
        this.isUsingChroma = false;
    }
    
    @SubscribeEvent
    public void onWorldRenderLast(final RenderEvent event) {
        if (event.getRenderType() != RenderType.WORLD) {
            return;
        }
        final float f = event.getPartialTicks();
        final float px = (float)BlockOverlay.mc.thePlayer.posX;
        final float py = (float)BlockOverlay.mc.thePlayer.posY;
        final float pz = (float)BlockOverlay.mc.thePlayer.posZ;
        final float mx = (float)BlockOverlay.mc.thePlayer.prevPosX;
        final float my = (float)BlockOverlay.mc.thePlayer.prevPosY;
        final float mz = (float)BlockOverlay.mc.thePlayer.prevPosZ;
        final float dx = mx + (px - mx) * f;
        final float dy = my + (py - my) * f;
        final float dz = mz + (pz - mz) * f;
        final MovingObjectPosition mop = BlockOverlay.mc.objectMouseOver;
        if (mop == null) {
            return;
        }
        if (BlockOverlay.mc.objectMouseOver != null && BlockOverlay.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            final Block block = BlockOverlay.mc.theWorld.getBlockState(mop.getBlockPos()).getBlock();
            final BlockPos blockPos = mop.getBlockPos();
            if (this.mode.equalsIgnoreCase("Outline") || this.mode.equalsIgnoreCase("Both")) {
                this.drawLines(block, blockPos, dx, dy, dz);
            }
            if (this.mode.equalsIgnoreCase("Highlight") || this.mode.equalsIgnoreCase("Both")) {
                this.highlight(block, blockPos);
            }
        }
    }
    
    private void highlight(final Block block, final BlockPos pos) {
        float red = this.hColor.getRed() / 255.0f;
        float green = this.hColor.getGreen() / 255.0f;
        float blue = this.hColor.getBlue() / 255.0f;
        float alpha = this.hColor.getAlpha() / 255.0f;
        if (this.isUsingChroma) {
            final float hue = System.currentTimeMillis() % 20000L / 20000.0f;
            final int chroma = Color.HSBtoRGB(hue, 1.0f, 1.0f);
            alpha = (chroma >> 24 & 0xFF) / 255.0f;
            red = (chroma >> 16 & 0xFF) / 255.0f;
            green = (chroma >> 8 & 0xFF) / 255.0f;
            blue = (chroma & 0xFF) / 255.0f;
        }
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer vertexBuffer = tessellator.getWorldRenderer();
        final double renderPosX = BlockOverlay.mc.getRenderManager().viewerPosX;
        final double renderPosY = BlockOverlay.mc.getRenderManager().viewerPosY;
        final double renderPosZ = BlockOverlay.mc.getRenderManager().viewerPosZ;
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        GL11.glLineWidth(this.lineWidth);
        GL11.glColor4f(red, green, blue, 0.5f);
        final AxisAlignedBB bb = block.getSelectedBoundingBox(BlockOverlay.mc.theWorld, pos).expand(0.0020000000949949026, 0.0020000000949949026, 0.0020000000949949026).offset(-renderPosX, -renderPosY, -renderPosZ);
        this.drawFilledBoundingBox(bb);
        GL11.glLineWidth(1.0f);
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
    
    private void drawLines(final Block block, final BlockPos b, final float px, final float py, final float pz) {
        float red = this.vColor.getRed() / 255.0f;
        float green = this.vColor.getGreen() / 255.0f;
        float blue = this.vColor.getBlue() / 255.0f;
        float alpha = this.vColor.getAlpha() / 255.0f;
        if (this.isUsingChroma) {
            final float hue = System.currentTimeMillis() % 20000L / 20000.0f;
            final int chroma = Color.HSBtoRGB(hue, 1.0f, 1.0f);
            alpha = (chroma >> 24 & 0xFF) / 255.0f;
            red = (chroma >> 16 & 0xFF) / 255.0f;
            green = (chroma >> 8 & 0xFF) / 255.0f;
            blue = (chroma & 0xFF) / 255.0f;
        }
        final double renderPosX = BlockOverlay.mc.getRenderManager().viewerPosX;
        final double renderPosY = BlockOverlay.mc.getRenderManager().viewerPosY;
        final double renderPosZ = BlockOverlay.mc.getRenderManager().viewerPosZ;
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        GL11.glLineWidth(this.lineWidth);
        GL11.glColor4f(red, green, blue, 1.0f);
        final AxisAlignedBB box = block.getSelectedBoundingBox(BlockOverlay.mc.theWorld, b).expand(0.0020000000949949026, 0.0020000000949949026, 0.0020000000949949026).offset(-renderPosX, -renderPosY, -renderPosZ);
        this.drawSelectionBoundingBox(box);
        GL11.glLineWidth(1.0f);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
    
    private void drawSelectionBoundingBox(final AxisAlignedBB p_181561_0_) {
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(3, DefaultVertexFormats.POSITION);
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.minY, p_181561_0_.minZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.minY, p_181561_0_.minZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.minY, p_181561_0_.maxZ).endVertex();
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.minY, p_181561_0_.maxZ).endVertex();
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.minY, p_181561_0_.minZ).endVertex();
        tessellator.draw();
        worldrenderer.begin(3, DefaultVertexFormats.POSITION);
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.maxY, p_181561_0_.minZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.maxY, p_181561_0_.minZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.maxY, p_181561_0_.maxZ).endVertex();
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.maxY, p_181561_0_.maxZ).endVertex();
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.maxY, p_181561_0_.minZ).endVertex();
        tessellator.draw();
        worldrenderer.begin(1, DefaultVertexFormats.POSITION);
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.minY, p_181561_0_.minZ).endVertex();
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.maxY, p_181561_0_.minZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.minY, p_181561_0_.minZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.maxY, p_181561_0_.minZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.minY, p_181561_0_.maxZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.maxY, p_181561_0_.maxZ).endVertex();
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.minY, p_181561_0_.maxZ).endVertex();
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.maxY, p_181561_0_.maxZ).endVertex();
        tessellator.draw();
    }
    
    private void drawFilledBoundingBox(final AxisAlignedBB box) {
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(box.minX, box.minY, box.minZ).endVertex();
        worldRenderer.pos(box.minX, box.maxY, box.minZ).endVertex();
        worldRenderer.pos(box.maxX, box.minY, box.minZ).endVertex();
        worldRenderer.pos(box.maxX, box.maxY, box.minZ).endVertex();
        worldRenderer.pos(box.maxX, box.minY, box.maxZ).endVertex();
        worldRenderer.pos(box.maxX, box.maxY, box.maxZ).endVertex();
        worldRenderer.pos(box.minX, box.minY, box.maxZ).endVertex();
        worldRenderer.pos(box.minX, box.maxY, box.maxZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(box.maxX, box.maxY, box.minZ).endVertex();
        worldRenderer.pos(box.maxX, box.minY, box.minZ).endVertex();
        worldRenderer.pos(box.minX, box.maxY, box.minZ).endVertex();
        worldRenderer.pos(box.minX, box.minY, box.minZ).endVertex();
        worldRenderer.pos(box.minX, box.maxY, box.maxZ).endVertex();
        worldRenderer.pos(box.minX, box.minY, box.maxZ).endVertex();
        worldRenderer.pos(box.maxX, box.maxY, box.maxZ).endVertex();
        worldRenderer.pos(box.maxX, box.minY, box.maxZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(box.minX, box.maxY, box.minZ).endVertex();
        worldRenderer.pos(box.maxX, box.maxY, box.minZ).endVertex();
        worldRenderer.pos(box.maxX, box.maxY, box.maxZ).endVertex();
        worldRenderer.pos(box.minX, box.maxY, box.maxZ).endVertex();
        worldRenderer.pos(box.minX, box.maxY, box.minZ).endVertex();
        worldRenderer.pos(box.minX, box.maxY, box.maxZ).endVertex();
        worldRenderer.pos(box.maxX, box.maxY, box.maxZ).endVertex();
        worldRenderer.pos(box.maxX, box.maxY, box.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(box.minX, box.minY, box.minZ).endVertex();
        worldRenderer.pos(box.maxX, box.minY, box.minZ).endVertex();
        worldRenderer.pos(box.maxX, box.minY, box.maxZ).endVertex();
        worldRenderer.pos(box.minX, box.minY, box.maxZ).endVertex();
        worldRenderer.pos(box.minX, box.minY, box.minZ).endVertex();
        worldRenderer.pos(box.minX, box.minY, box.maxZ).endVertex();
        worldRenderer.pos(box.maxX, box.minY, box.maxZ).endVertex();
        worldRenderer.pos(box.maxX, box.minY, box.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(box.minX, box.minY, box.minZ).endVertex();
        worldRenderer.pos(box.minX, box.maxY, box.minZ).endVertex();
        worldRenderer.pos(box.minX, box.minY, box.maxZ).endVertex();
        worldRenderer.pos(box.minX, box.maxY, box.maxZ).endVertex();
        worldRenderer.pos(box.maxX, box.minY, box.maxZ).endVertex();
        worldRenderer.pos(box.maxX, box.maxY, box.maxZ).endVertex();
        worldRenderer.pos(box.maxX, box.minY, box.minZ).endVertex();
        worldRenderer.pos(box.maxX, box.maxY, box.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(box.minX, box.maxY, box.maxZ).endVertex();
        worldRenderer.pos(box.minX, box.minY, box.maxZ).endVertex();
        worldRenderer.pos(box.minX, box.maxY, box.minZ).endVertex();
        worldRenderer.pos(box.minX, box.minY, box.minZ).endVertex();
        worldRenderer.pos(box.maxX, box.maxY, box.minZ).endVertex();
        worldRenderer.pos(box.maxX, box.minY, box.minZ).endVertex();
        worldRenderer.pos(box.maxX, box.maxY, box.maxZ).endVertex();
        worldRenderer.pos(box.maxX, box.minY, box.maxZ).endVertex();
        tessellator.draw();
    }
}
