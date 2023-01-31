package intent.AquaDev.aqua.cape;

import java.awt.image.BufferedImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;

public class ImageFrame {
   private final int delay;
   private final BufferedImage image;
   private final String disposal;
   private DynamicTexture texture;
   private ResourceLocation location;

   public ImageFrame(BufferedImage image, int delay, String disposal, String name) {
      this.image = image;
      this.delay = delay;
      this.disposal = disposal;
      this.texture = new DynamicTexture(image);
      this.location = Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation("GIF", this.texture);
   }

   public BufferedImage getImage() {
      return this.image;
   }

   public int getDelay() {
      return this.delay;
   }

   public String getDisposal() {
      return this.disposal;
   }

   public ResourceLocation getLocation() {
      return this.location;
   }
}
