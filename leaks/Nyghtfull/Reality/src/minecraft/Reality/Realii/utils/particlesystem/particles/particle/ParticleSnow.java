package Reality.Realii.utils.particlesystem.particles.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import Reality.Realii.utils.cheats.player.Helper;
import Reality.Realii.utils.render.RenderUtil;

import java.util.Random;

public class ParticleSnow extends Particle {
   private Random random = new Random();
   private ScaledResolution res;

   public void draw(int xAdd) {
      this.prepare();
//      this.move();
      this.drawPixel(xAdd);
      this.resetPos();
   }

   private void prepare() {
      this.res = new ScaledResolution(Minecraft.getMinecraft());
   }

   private void drawPixel(int xAdd) {
      float size = 10.0F;

//      for(int i = 0; i < 10; ++i) {
//         int alpha = Math.min(0, 1 - i / 10);
         RenderUtil.circle(this.vector.x, this.vector.y, size + 1.0F + 0.4F, Helper.reAlpha(-1, 0.3F));
//      }

//     RenderUtil.circle(this.vector.x + (float)xAdd, this.vector.y, 1.1F, Helper.reAlpha(-1, 0.2F));
//     RenderUtil.circle(this.vector.x + (float)xAdd, this.vector.y, 0.8F, Helper.reAlpha(-1, 0.4F));
//     RenderUtil.circle(this.vector.x + (float)xAdd, this.vector.y, 0.5F, Helper.reAlpha(-1, 0.6F));
//     RenderUtil.circle(this.vector.x + (float)xAdd, this.vector.y, 0.3F, Helper.reAlpha(-1, 1.0F));
   }

   private void move() {
//      float speed = 100.0F;
//      this.vector.y += this.random.nextFloat();
//      this.vector.x -= this.random.nextFloat();
   }

   private void resetPos() {
      if (this.vector.x < 0.0F) {
         this.vector.x = (float)this.res.getScaledWidth();
      }

      if (this.vector.y > (float)this.res.getScaledHeight()) {
         this.vector.y = 0.0F;
      }

   }
}
