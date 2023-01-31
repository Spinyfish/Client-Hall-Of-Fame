package intent.AquaDev.aqua.modules.visual;

import events.Event;
import events.listeners.EventPostRender2D;
import events.listeners.EventRender2D;
import intent.AquaDev.aqua.Aqua;
import intent.AquaDev.aqua.fr.lavache.anime.Animate;
import intent.AquaDev.aqua.fr.lavache.anime.Easing;
import intent.AquaDev.aqua.modules.Category;
import intent.AquaDev.aqua.modules.Module;
import intent.AquaDev.aqua.utils.RenderUtil;
import java.awt.Color;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class CustomHotbar extends Module {
   Animate anim = new Animate();
   private final RenderItem itemRenderer = mc.getRenderItem();

   public CustomHotbar() {
      super("CustomHotbar", Module.Type.Visual, "CustomHotbar", 0, Category.Visual);
   }

   @Override
   public void onEnable() {
      super.onEnable();
   }

   @Override
   public void onDisable() {
      super.onDisable();
   }

   @Override
   public void onEvent(Event e) {
      if (e instanceof EventRender2D) {
         this.drawShaders();
      }

      if (e instanceof EventPostRender2D) {
         this.drawRects();
      }
   }

   private void drawShaders() {
      ScaledResolution sr = new ScaledResolution(mc);
      int i = sr.getScaledWidth() / 2;
      this.anim.setEase(Easing.LINEAR).setMin(11.0F).setMax(40.0F).setSpeed(15.0F).setReversed(!GuiNewChat.animatedChatOpen).update();
      if (Aqua.moduleManager.getModuleByName("Blur").isToggled()) {
         Blur.drawBlurred(
            () -> RenderUtil.drawRoundedRect(
                  (double)(i - 91), (double)((float)sr.getScaledHeight() - this.anim.getValue()), 182.0, 22.0, 3.0, new Color(0, 0, 0, 1).getRGB()
               ),
            false
         );
      }
   }

   private void drawRects() {
      ScaledResolution sr = new ScaledResolution(mc);
      int i = sr.getScaledWidth() / 2;
      this.anim
         .setEase(Easing.LINEAR)
         .setMin(11.0F)
         .setMax(40.0F)
         .setSpeed(GuiNewChat.animatedChatOpen ? 15.0F : 40.0F)
         .setReversed(!GuiNewChat.animatedChatOpen)
         .update();
      RenderUtil.drawRoundedRect2Alpha(
         (double)(i - 91), (double)((float)sr.getScaledHeight() - this.anim.getValue()), 182.0, 22.0, 3.0, new Color(0, 0, 0, 50)
      );
      RenderUtil.drawRoundedRect2Alpha(
         (double)((float)sr.getScaledWidth() / 2.0F - 91.0F + (float)(mc.thePlayer.inventory.currentItem * 20)),
         (double)((float)sr.getScaledHeight() - this.anim.getValue()),
         22.0,
         22.0,
         3.0,
         RenderUtil.getColorAlpha(new Color(Aqua.setmgr.getSetting("HUDColor").getColor()).getRGB(), 100)
      );

      for(int j = 0; j < 9; ++j) {
         int k = sr.getScaledWidth() / 2 - 90 + j * 20 + 2;
         int l = (int)((float)sr.getScaledHeight() - this.anim.getValue() + 2.0F);
         RenderHelper.enableStandardItemLighting();
         this.renderHotbarItem(j, k, l, mc.timer.renderPartialTicks, mc.thePlayer);
         RenderHelper.disableStandardItemLighting();
      }
   }

   private void renderHotbarItem(int index, int xPos, int yPos, float partialTicks, EntityPlayer player) {
      ItemStack itemstack = player.inventory.mainInventory[index];
      if (itemstack != null) {
         float f = (float)itemstack.animationsToGo - partialTicks;
         if (f > 0.0F) {
            GlStateManager.pushMatrix();
            float f1 = 1.0F + f / 5.0F;
            GlStateManager.translate((float)(xPos + 8), (float)(yPos + 12), 0.0F);
            GlStateManager.scale(1.0F / f1, (f1 + 1.0F) / 2.0F, 1.0F);
            GlStateManager.translate((float)(-(xPos + 8)), (float)(-(yPos + 12)), 0.0F);
         }

         this.itemRenderer.renderItemAndEffectIntoGUI(itemstack, xPos, yPos);
         if (f > 0.0F) {
            GlStateManager.popMatrix();
         }

         RenderHelper.enableStandardItemLighting();
         this.itemRenderer.renderItemOverlays(mc.fontRendererObj, itemstack, xPos, yPos);
         GlStateManager.resetColor();
      }
   }
}
