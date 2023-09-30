/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package wtf.monsoon.api.util.render.builder;

import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.opengl.GL11;
import wtf.monsoon.api.util.render.builder.BoxRenderMode;

public class RenderBuilder {
    private boolean depth = false;
    private boolean blend = false;
    private boolean texture = false;
    private boolean alpha = false;
    private AxisAlignedBB boundingBox;
    private Color innerColour;
    private Color outerColour;
    private BoxRenderMode renderMode = BoxRenderMode.BOTH;

    public RenderBuilder start() {
        GL11.glPushMatrix();
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glEnable((int)2848);
        GL11.glHint((int)3154, (int)4354);
        return this;
    }

    public void build(boolean offset) {
        if (offset) {
            this.boundingBox = this.boundingBox.offset(-Minecraft.getMinecraft().getRenderManager().viewerPosX, -Minecraft.getMinecraft().getRenderManager().viewerPosY, -Minecraft.getMinecraft().getRenderManager().viewerPosZ);
        }
        if (this.renderMode == BoxRenderMode.FILL || this.renderMode == BoxRenderMode.BOTH) {
            RenderGlobal.renderFilledBox(this.boundingBox, this.innerColour.getRed(), this.innerColour.getGreen(), this.innerColour.getBlue(), this.innerColour.getAlpha());
        }
        if (this.renderMode == BoxRenderMode.FILL || this.renderMode == BoxRenderMode.BOTH) {
            RenderGlobal.bleh_drawSelectionBoundingBox(this.boundingBox, this.outerColour.getRed(), this.outerColour.getGreen(), this.outerColour.getBlue(), this.outerColour.getAlpha());
        }
        if (this.depth) {
            GL11.glDepthMask((boolean)true);
            GL11.glEnable((int)2929);
        }
        if (this.blend) {
            GL11.glEnable((int)3042);
        }
        if (this.alpha) {
            GL11.glEnable((int)3008);
        }
        if (this.texture) {
            GL11.glEnable((int)3553);
        }
        GL11.glDisable((int)2848);
        GL11.glPopMatrix();
    }

    public RenderBuilder boundingBox(AxisAlignedBB bb) {
        this.boundingBox = bb;
        return this;
    }

    public RenderBuilder type(BoxRenderMode mode) {
        this.renderMode = mode;
        return this;
    }

    public RenderBuilder depth() {
        GL11.glDisable((int)2929);
        GL11.glDepthMask((boolean)false);
        this.depth = true;
        return this;
    }

    public RenderBuilder blend() {
        GL11.glEnable((int)3042);
        this.blend = true;
        return this;
    }

    public RenderBuilder texture() {
        GL11.glDisable((int)3553);
        this.texture = true;
        return this;
    }

    public RenderBuilder alpha() {
        GL11.glDisable((int)3008);
        this.alpha = true;
        return this;
    }

    public RenderBuilder lineWidth(float lineWidth) {
        GL11.glLineWidth((float)lineWidth);
        return this;
    }

    public RenderBuilder innerColour(Color colour) {
        this.innerColour = colour;
        return this;
    }

    public RenderBuilder outerColour(Color colour) {
        this.outerColour = colour;
        return this;
    }
}

