package intent.AquaDev.aqua.modules.visual;

import events.Event;
import events.listeners.EventRenderNameTags;
import intent.AquaDev.aqua.modules.Category;
import intent.AquaDev.aqua.modules.Module;
import java.awt.Color;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class Nametags extends Module {
   final Color back = new Color(28, 25, 24, 255);
   final Color orange = new Color(255, 99, 0, 255);
   private String name = "";

   public Nametags() {
      super("Nametags", Module.Type.Visual, "Nametags", 0, Category.Visual);
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
   public void onEvent(Event event) {
      if (event instanceof EventRenderNameTags && mc.thePlayer != null && mc.theWorld != null) {
         for(EntityPlayer e : mc.theWorld.playerEntities) {
            if (mc.thePlayer.getDistanceToEntity(e) < 250.0F && e != mc.thePlayer) {
               String health = String.valueOf(Math.round(e.getHealth()));
               this.name = e.getDisplayName().getFormattedText();
               this.name = this.name.replaceAll("", "");
               GlStateManager.pushMatrix();
               GlStateManager.disableTexture2D();
               float pT = mc.timer.renderPartialTicks;
               double x = e.lastTickPosX + (e.posX - e.lastTickPosX) * (double)pT - RenderManager.renderPosX;
               double y = e.lastTickPosY + (e.posY - e.lastTickPosY) * (double)pT - RenderManager.renderPosY;
               double z = e.lastTickPosZ + (e.posZ - e.lastTickPosZ) * (double)pT - RenderManager.renderPosZ;
               float d = mc.thePlayer.getDistanceToEntity(e);
               float s = Math.min(Math.max(1.21F * d * 0.1F, 1.25F), 6.0F) * 2.0F / 100.0F;
               GlStateManager.translate((float)x, (float)y + e.height + 1.8F - e.height / 2.0F, (float)z);
               GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
               GlStateManager.rotate(mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
               GlStateManager.scale(-s, -s, s);
               float string_width = (float)mc.fontRendererObj.getStringWidth(this.name) / 2.0F - 2.0F;
               GlStateManager.enableTexture2D();
               GlStateManager.resetColor();
               GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
               Shadow.drawGlow(
                  () -> Gui.drawRect(-mc.fontRendererObj.getStringWidth(this.name) / 2 - 3, -2, (int)string_width + 8, 13, new Color(30, 30, 30, 255).getRGB()),
                  false
               );
               Gui.drawRect(-mc.fontRendererObj.getStringWidth(this.name) / 2 - 3, -2, (int)string_width + 8, 13, new Color(30, 30, 30, 100).getRGB());
               GlStateManager.resetColor();
               GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
               mc.fontRendererObj.drawString(this.name, (int)(-string_width), 2, Color.white.getRGB());
               GlStateManager.disableBlend();
               GlStateManager.enableDepth();
               GlStateManager.resetColor();
               GlStateManager.disableLighting();
               GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
               GlStateManager.popMatrix();
            }
         }
      }
   }

   public static String removeColorCode(String text) {
      String finalText = text;

      for(int i = 0; i < finalText.length(); ++i) {
         if (Character.toString(finalText.charAt(i)).equals("")) {
            try {
               String part1 = finalText.substring(0, i);
               String part2 = finalText.substring(Math.min(i + 2, finalText.length()));
               finalText = part1 + part2;
            } catch (Exception var5) {
            }
         }
      }

      return finalText;
   }

   public void renderItem(ItemStack item, int xPos, int yPos, int zPos) {
      GL11.glPushMatrix();
      GlStateManager.enableRescaleNormal();
      GlStateManager.enableAlpha();
      GlStateManager.alphaFunc(516, 0.1F);
      GlStateManager.enableBlend();
      GL11.glBlendFunc(770, 771);
      GlStateManager.disableLighting();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      IBakedModel ibakedmodel = mc.getRenderItem().getItemModelMesher().getItemModel(item);
      mc.getRenderItem().textureManager.bindTexture(TextureMap.locationBlocksTexture);
      GlStateManager.scale(16.0F, 16.0F, 0.0F);
      GL11.glTranslated((double)(((float)xPos - 7.85F) / 16.0F), (double)((float)(-5 + yPos) / 16.0F), (double)((float)zPos / 16.0F));
      GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
      if (ibakedmodel.isBuiltInRenderer()) {
         GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.translate(-0.5F, -0.5F, -0.5F);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         TileEntityItemStackRenderer.instance.renderByItem(item);
      } else {
         mc.getRenderItem().renderModel(ibakedmodel, -1, item);
      }

      GlStateManager.enableAlpha();
      GlStateManager.disableLighting();
      GlStateManager.popMatrix();
   }

   public String getHealthColor(int hp) {
      if (hp > 15) {
         return "a";
      } else if (hp > 10) {
         return "e";
      } else if (hp > 5) {
         return "6";
      } else {
         return hp < 2 ? "4" : "c";
      }
   }
}
