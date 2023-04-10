package crispy.util.render.wings;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.EntityPlayer;

public abstract class CosmeticBase implements LayerRenderer {
  protected final RenderPlayer renderPlayer;
  
  public CosmeticBase(RenderPlayer player) {
    this.renderPlayer = player;
  }
  
  public void doRenderLayer(AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float headYaw, float headPitch, float scale) {
    if (player.hasPlayerInfo() && !player.isInvisible())
      render(player, limbSwing, limbSwingAmount, partialTicks, ageInTicks, headYaw, headPitch, scale); 
  }
  
  public boolean shouldCombineTextures() {
    return false;
  }
  
  public abstract void render(AbstractClientPlayer paramAbstractClientPlayer, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, float paramFloat7);
}
