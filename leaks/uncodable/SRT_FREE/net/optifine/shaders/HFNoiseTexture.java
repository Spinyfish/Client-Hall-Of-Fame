package net.optifine.shaders;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class HFNoiseTexture implements ICustomTexture {
   private int texID = GL11.glGenTextures();
   private final int textureUnit = 15;

   public HFNoiseTexture(int width, int height) {
      byte[] abyte = this.genHFNoiseImage(width, height);
      ByteBuffer bytebuffer = BufferUtils.createByteBuffer(abyte.length);
      bytebuffer.put(abyte);
      ((Buffer)bytebuffer).flip();
      GlStateManager.bindTexture(this.texID);
      GL11.glTexImage2D(3553, 0, 6407, width, height, 0, 6407, 5121, bytebuffer);
      GL11.glTexParameteri(3553, 10242, 10497);
      GL11.glTexParameteri(3553, 10243, 10497);
      GL11.glTexParameteri(3553, 10240, 9729);
      GL11.glTexParameteri(3553, 10241, 9729);
      GlStateManager.bindTexture(0);
   }

   public int getID() {
      return this.texID;
   }

   @Override
   public void deleteTexture() {
      GlStateManager.deleteTexture(this.texID);
      this.texID = 0;
   }

   private int random(int seed) {
      seed ^= seed << 13;
      seed ^= seed >> 17;
      return seed ^ seed << 5;
   }

   private byte random(int x, int y, int z) {
      int i = (this.random(x) + this.random(y * 19)) * this.random(z * 23) - z;
      return (byte)(this.random(i) % 128);
   }

   private byte[] genHFNoiseImage(int width, int height) {
      byte[] abyte = new byte[width * height * 3];
      int i = 0;

      for(int j = 0; j < height; ++j) {
         for(int k = 0; k < width; ++k) {
            for(int l = 1; l < 4; ++l) {
               abyte[i++] = this.random(k, j, l);
            }
         }
      }

      return abyte;
   }

   @Override
   public int getTextureId() {
      return this.texID;
   }

   @Override
   public int getTextureUnit() {
      return 15;
   }

   @Override
   public int getTarget() {
      return 3553;
   }
}
