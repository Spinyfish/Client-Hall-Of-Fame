package net.optifine.shaders;

import java.nio.ByteBuffer;
import net.optifine.texture.InternalFormat;
import net.optifine.texture.PixelFormat;
import net.optifine.texture.PixelType;
import net.optifine.texture.TextureType;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class CustomTextureRaw implements ICustomTexture {
   private final TextureType type;
   private final int textureUnit;
   private int textureId;

   public CustomTextureRaw(
      TextureType type,
      InternalFormat internalFormat,
      int width,
      int height,
      int depth,
      PixelFormat pixelFormat,
      PixelType pixelType,
      ByteBuffer data,
      int textureUnit
   ) {
      this.type = type;
      this.textureUnit = textureUnit;
      this.textureId = GL11.glGenTextures();
      GL11.glBindTexture(this.getTarget(), this.textureId);
      switch(type) {
         case TEXTURE_1D:
            GL11.glTexImage1D(3552, 0, internalFormat.getId(), width, 0, pixelFormat.getId(), pixelType.getId(), data);
            GL11.glTexParameteri(3552, 10242, 33071);
            GL11.glTexParameteri(3552, 10240, 9729);
            GL11.glTexParameteri(3552, 10241, 9729);
            break;
         case TEXTURE_2D:
            GL11.glTexImage2D(3553, 0, internalFormat.getId(), width, height, 0, pixelFormat.getId(), pixelType.getId(), data);
            GL11.glTexParameteri(3553, 10242, 33071);
            GL11.glTexParameteri(3553, 10243, 33071);
            GL11.glTexParameteri(3553, 10240, 9729);
            GL11.glTexParameteri(3553, 10241, 9729);
            break;
         case TEXTURE_3D:
            GL12.glTexImage3D(32879, 0, internalFormat.getId(), width, height, depth, 0, pixelFormat.getId(), pixelType.getId(), data);
            GL11.glTexParameteri(32879, 10242, 33071);
            GL11.glTexParameteri(32879, 10243, 33071);
            GL11.glTexParameteri(32879, 32882, 33071);
            GL11.glTexParameteri(32879, 10240, 9729);
            GL11.glTexParameteri(32879, 10241, 9729);
            break;
         case TEXTURE_RECTANGLE:
            GL11.glTexImage2D(34037, 0, internalFormat.getId(), width, height, 0, pixelFormat.getId(), pixelType.getId(), data);
            GL11.glTexParameteri(34037, 10242, 33071);
            GL11.glTexParameteri(34037, 10243, 33071);
            GL11.glTexParameteri(34037, 10240, 9729);
            GL11.glTexParameteri(34037, 10241, 9729);
      }

      GL11.glBindTexture(this.getTarget(), 0);
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
         GL11.glDeleteTextures(this.textureId);
         this.textureId = 0;
      }
   }
}
