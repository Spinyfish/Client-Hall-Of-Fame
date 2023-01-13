package net.minecraft.client.renderer.texture;

import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class TextureCompass extends TextureAtlasSprite {
   public double currentAngle;
   public double angleDelta;
   public static String field_176608_l;

   public TextureCompass(String iconName) {
      super(iconName);
      field_176608_l = iconName;
   }

   @Override
   public void updateAnimation() {
      Minecraft minecraft = Minecraft.getMinecraft();
      if (minecraft.theWorld != null && minecraft.thePlayer != null) {
         this.updateCompass(minecraft.theWorld, minecraft.thePlayer.posX, minecraft.thePlayer.posZ, (double)minecraft.thePlayer.rotationYaw, false, false);
      } else {
         this.updateCompass(null, 0.0, 0.0, 0.0, true, false);
      }
   }

   public void updateCompass(World worldIn, double p_94241_2_, double p_94241_4_, double p_94241_6_, boolean p_94241_8_, boolean p_94241_9_) {
      if (!this.framesTextureData.isEmpty()) {
         double d0 = 0.0;
         if (worldIn != null && !p_94241_8_) {
            BlockPos blockpos = worldIn.getSpawnPoint();
            double d1 = (double)blockpos.getX() - p_94241_2_;
            double d2 = (double)blockpos.getZ() - p_94241_4_;
            p_94241_6_ %= 360.0;
            d0 = -((p_94241_6_ - 90.0) * Math.PI / 180.0 - Math.atan2(d2, d1));
            if (!worldIn.provider.isSurfaceWorld()) {
               d0 = Math.random() * Math.PI * 2.0;
            }
         }

         if (p_94241_9_) {
            this.currentAngle = d0;
         } else {
            double d3 = d0 - this.currentAngle;

            while(d3 < -Math.PI) {
               d3 += Math.PI * 2;
            }

            while(d3 >= Math.PI) {
               d3 -= Math.PI * 2;
            }

            d3 = MathHelper.clamp_double(d3, -1.0, 1.0);
            this.angleDelta += d3 * 0.1;
            this.angleDelta *= 0.8;
            this.currentAngle += this.angleDelta;
         }

         int i = (int)((this.currentAngle / (Math.PI * 2) + 1.0) * (double)this.framesTextureData.size()) % this.framesTextureData.size();

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
