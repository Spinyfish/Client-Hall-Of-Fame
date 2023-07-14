/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package net.minecraft.client.renderer.texture;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.optifine.shaders.MultiTexID;
import net.optifine.shaders.ShadersTex;
import org.lwjgl.opengl.GL11;

public abstract class AbstractTexture
implements ITextureObject {
    protected int glTextureId = -1;
    protected boolean blur;
    protected boolean mipmap;
    protected boolean blurLast;
    protected boolean mipmapLast;
    public MultiTexID multiTex;

    public void setBlurMipmapDirect(boolean p_174937_1_, boolean p_174937_2_) {
        this.blur = p_174937_1_;
        this.mipmap = p_174937_2_;
        int i = -1;
        int j = -1;
        if (p_174937_1_) {
            i = p_174937_2_ ? 9987 : 9729;
            j = 9729;
        } else {
            i = p_174937_2_ ? 9986 : 9728;
            j = 9728;
        }
        GlStateManager.bindTexture(this.getGlTextureId());
        GL11.glTexParameteri((int)3553, (int)10241, (int)i);
        GL11.glTexParameteri((int)3553, (int)10240, (int)j);
    }

    @Override
    public void setBlurMipmap(boolean p_174936_1_, boolean p_174936_2_) {
        this.blurLast = this.blur;
        this.mipmapLast = this.mipmap;
        this.setBlurMipmapDirect(p_174936_1_, p_174936_2_);
    }

    @Override
    public void restoreLastBlurMipmap() {
        this.setBlurMipmapDirect(this.blurLast, this.mipmapLast);
    }

    @Override
    public int getGlTextureId() {
        if (this.glTextureId == -1) {
            this.glTextureId = TextureUtil.glGenTextures();
        }
        return this.glTextureId;
    }

    public void deleteGlTexture() {
        ShadersTex.deleteTextures(this, this.glTextureId);
        if (this.glTextureId != -1) {
            TextureUtil.deleteTexture(this.glTextureId);
            this.glTextureId = -1;
        }
    }

    @Override
    public MultiTexID getMultiTexID() {
        return ShadersTex.getMultiTexID(this);
    }
}

