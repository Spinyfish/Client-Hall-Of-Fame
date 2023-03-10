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
import org.lwjgl.opengl.GL11;
import shadersmod.client.MultiTexID;
import shadersmod.client.ShadersTex;

public abstract class AbstractTexture
implements ITextureObject {
    protected int glTextureId = -1;
    protected boolean blur;
    protected boolean mipmap;
    protected boolean blurLast;
    protected boolean mipmapLast;
    private static final String __OBFID = "CL_00001047";
    public MultiTexID multiTex;

    public void setBlurMipmapDirect(boolean p_174937_1_, boolean p_174937_2_) {
        int short1;
        int i;
        this.blur = p_174937_1_;
        this.mipmap = p_174937_2_;
        boolean flag = true;
        boolean flag1 = true;
        if (p_174937_1_) {
            i = p_174937_2_ ? 9987 : 9729;
            short1 = 9729;
        } else {
            i = p_174937_2_ ? 9986 : 9728;
            short1 = 9728;
        }
        GlStateManager.bindTexture(this.getGlTextureId());
        GL11.glTexParameteri((int)3553, (int)10241, (int)i);
        GL11.glTexParameteri((int)3553, (int)10240, (int)short1);
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

