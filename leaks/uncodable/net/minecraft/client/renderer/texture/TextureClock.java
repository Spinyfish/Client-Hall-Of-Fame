package net.minecraft.client.renderer.texture;

import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;

public class TextureClock extends TextureAtlasSprite {
   private double field_94239_h;
   private double field_94240_i;

   public TextureClock(String iconName) {
      super(iconName);
   }

   @Override
   public void updateAnimation() {
      if (!this.framesTextureData.isEmpty()) {
         Minecraft minecraft = Minecraft.getMinecraft();
         double d0 = 0.0;
         if (minecraft.theWorld != null && minecraft.thePlayer != null) {
            d0 = (double)minecraft.theWorld.getCelestialAngle(1.0F);
            if (!minecraft.theWorld.provider.isSurfaceWorld()) {
               d0 = Math.random();
            }
         }

         double d1 = d0 - this.field_94239_h;

         while(d1 < -0.5) {
            ++d1;
         }

         while(d1 >= 0.5) {
            --d1;
         }

         d1 = MathHelper.clamp_double(d1, -1.0, 1.0);
         this.field_94240_i += d1 * 0.1;
         this.field_94240_i *= 0.8;
         this.field_94239_h += this.field_94240_i;
         int i = (int)((this.field_94239_h + 1.0) * (double)this.framesTextureData.size()) % this.framesTextureData.size();

         while(i < 0) {
            i = (i + this.framesTextureData.size()) % this.framesTextureData.size();
         }

         if (i != this.frameCounter) {
            this.frameCounter = i;
            TextureUtil.uploadTextureMipmap(this.framesTextureData.get(this.frameCounter), this.width, this.height, this.originX, this.originY, false, false);
         }
      }
   }
}
