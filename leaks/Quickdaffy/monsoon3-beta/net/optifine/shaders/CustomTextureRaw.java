/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GL12
 */
package net.optifine.shaders;

import java.nio.ByteBuffer;
import net.optifine.shaders.ICustomTexture;
import net.optifine.texture.InternalFormat;
import net.optifine.texture.PixelFormat;
import net.optifine.texture.PixelType;
import net.optifine.texture.TextureType;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class CustomTextureRaw
implements ICustomTexture {
    private TextureType type;
    private int textureUnit;
    private int textureId;

    public CustomTextureRaw(TextureType type, InternalFormat internalFormat, int width, int height, int depth, PixelFormat pixelFormat, PixelType pixelType, ByteBuffer data, int textureUnit) {
        this.type = type;
        this.textureUnit = textureUnit;
        this.textureId = GL11.glGenTextures();
        GL11.glBindTexture((int)this.getTarget(), (int)this.textureId);
        switch (type) {
            case TEXTURE_1D: {
                GL11.glTexImage1D((int)3552, (int)0, (int)internalFormat.getId(), (int)width, (int)0, (int)pixelFormat.getId(), (int)pixelType.getId(), (ByteBuffer)data);
                GL11.glTexParameteri((int)3552, (int)10242, (int)33071);
                GL11.glTexParameteri((int)3552, (int)10240, (int)9729);
                GL11.glTexParameteri((int)3552, (int)10241, (int)9729);
                break;
            }
            case TEXTURE_2D: {
                GL11.glTexImage2D((int)3553, (int)0, (int)internalFormat.getId(), (int)width, (int)height, (int)0, (int)pixelFormat.getId(), (int)pixelType.getId(), (ByteBuffer)data);
                GL11.glTexParameteri((int)3553, (int)10242, (int)33071);
                GL11.glTexParameteri((int)3553, (int)10243, (int)33071);
                GL11.glTexParameteri((int)3553, (int)10240, (int)9729);
                GL11.glTexParameteri((int)3553, (int)10241, (int)9729);
                break;
            }
            case TEXTURE_3D: {
                GL12.glTexImage3D((int)32879, (int)0, (int)internalFormat.getId(), (int)width, (int)height, (int)depth, (int)0, (int)pixelFormat.getId(), (int)pixelType.getId(), (ByteBuffer)data);
                GL11.glTexParameteri((int)32879, (int)10242, (int)33071);
                GL11.glTexParameteri((int)32879, (int)10243, (int)33071);
                GL11.glTexParameteri((int)32879, (int)32882, (int)33071);
                GL11.glTexParameteri((int)32879, (int)10240, (int)9729);
                GL11.glTexParameteri((int)32879, (int)10241, (int)9729);
                break;
            }
            case TEXTURE_RECTANGLE: {
                GL11.glTexImage2D((int)34037, (int)0, (int)internalFormat.getId(), (int)width, (int)height, (int)0, (int)pixelFormat.getId(), (int)pixelType.getId(), (ByteBuffer)data);
                GL11.glTexParameteri((int)34037, (int)10242, (int)33071);
                GL11.glTexParameteri((int)34037, (int)10243, (int)33071);
                GL11.glTexParameteri((int)34037, (int)10240, (int)9729);
                GL11.glTexParameteri((int)34037, (int)10241, (int)9729);
            }
        }
        GL11.glBindTexture((int)this.getTarget(), (int)0);
    }

    @Override
    public int getTarget() {
        return this.type.getId();
    }

    @Override
    public int getTextureId() {
        return this.textureId;
    }

    @Override
    public int getTextureUnit() {
        return this.textureUnit;
    }

    @Override
    public void deleteTexture() {
        if (this.textureId > 0) {
            GL11.glDeleteTextures((int)this.textureId);
            this.textureId = 0;
        }
    }
}

