package intent.AquaDev.aqua.modules.visual;

import de.Hero.settings.Setting;
import events.Event;
import events.listeners.EventPostRender2D;
import events.listeners.EventRender2D;
import intent.AquaDev.aqua.Aqua;
import intent.AquaDev.aqua.modules.Category;
import intent.AquaDev.aqua.modules.Module;
import intent.AquaDev.aqua.utils.ColorUtil;
import intent.AquaDev.aqua.utils.ColorUtils;
import intent.AquaDev.aqua.utils.RenderUtil;
import intent.AquaDev.aqua.utils.RotationUtil;
import java.awt.Color;
import java.util.List;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.Display;

public class Tracers extends Module {
   public Tracers() {
      super("Tracers", Module.Type.Visual, "Tracers", 0, Category.Visual);
      Aqua.setmgr.register(new Setting("Blur", this, true));
      Aqua.setmgr.register(new Setting("Bloom", this, true));
      Aqua.setmgr.register(new Setting("BloomMode", this, "Glow", new String[]{"Glow", "Shadow"}));
      Aqua.setmgr.register(new Setting("Alpha", this, 20.0, 2.0, 255.0, false));
      Aqua.setmgr.register(new Setting("DistanceToMiddle", this, 100.0, 2.0, 100.0, false));
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
      if (e instanceof EventPostRender2D) {
         int x = Display.getWidth() / 2 / Math.max(mc.gameSettings.guiScale, 1);
         int y = Display.getHeight() / 2 / Math.max(mc.gameSettings.guiScale, 1);
         List<EntityPlayer> playerList = mc.theWorld.playerEntities;
         playerList.removeIf(entity -> entity == mc.thePlayer || entity.isInvisible());
         int[] rgb = ColorUtil.getRGB(Aqua.setmgr.getSetting("HUDColor").getColor());

         for(EntityPlayer player : playerList) {
            int alpha = (int)(255.0F - Math.min(mc.thePlayer.getDistanceToEntity(player), 255.0F));
            GlStateManager.pushMatrix();
            float angle = RotationUtil.getAngle(player) % 360.0F + 180.0F;
            GlStateManager.translate((float)x, (float)y, 0.0F);
            GlStateManager.rotate(angle, 0.0F, 0.0F, 1.0F);
            GlStateManager.translate((float)(-x), (float)(-y), 0.0F);
            float crossDistance = (float)Aqua.setmgr.getSetting("TracersDistanceToMiddle").getCurrentNumber();
            if (Aqua.setmgr.getSetting("TracersBlur").isState() && Aqua.moduleManager.getModuleByName("Blur").isToggled()) {
               Blur.drawBlurred(
                  () -> RenderUtil.drawTriangleFilled2((float)(x - 5), (float)y + crossDistance, 5.0F, 9.0F, ColorUtils.getColor(rgb[0], rgb[1], rgb[2], 255)),
                  false
               );
            }

            GlStateManager.translate((float)x, (float)y, 0.0F);
            GlStateManager.rotate(-angle, 0.0F, 0.0F, 1.0F);
            GlStateManager.translate((float)(-x), (float)(-y), 0.0F);
            GlStateManager.popMatrix();
            GlStateManager.resetColor();
         }
      }

      if (e instanceof EventRender2D) {
         int x = Display.getWidth() / 2 / Math.max(mc.gameSettings.guiScale, 1);
         int y = Display.getHeight() / 2 / Math.max(mc.gameSettings.guiScale, 1);
         List<EntityPlayer> playerList = mc.theWorld.playerEntities;
         playerList.removeIf(entity -> entity == mc.thePlayer || entity.isInvisible());
         int[] rgb = ColorUtil.getRGB(Aqua.setmgr.getSetting("HUDColor").getColor());

         for(EntityPlayer player : playerList) {
            int alpha = (int)(255.0F - Math.min(mc.thePlayer.getDistanceToEntity(player), 255.0F));
            GlStateManager.pushMatrix();
            float angle = RotationUtil.getAngle(player) % 360.0F + 180.0F;
            GlStateManager.translate((float)x, (float)y, 0.0F);
            GlStateManager.rotate(angle, 0.0F, 0.0F, 1.0F);
            GlStateManager.translate((float)(-x), (float)(-y), 0.0F);
            float crossDistance = (float)Aqua.setmgr.getSetting("TracersDistanceToMiddle").getCurrentNumber();
            int alphaBackground = (int)Aqua.setmgr.getSetting("TracersAlpha").getCurrentNumber();
            if (Aqua.setmgr.getSetting("TracersBloom").isState()) {
               String var12 = Aqua.setmgr.getSetting("TracersBloomMode").getCurrentMode();
               switch(var12) {
                  case "Glow":
                     if (Aqua.moduleManager.getModuleByName("ShaderMultiplier").isToggled()) {
                        ShaderMultiplier.drawGlowESP(
                           () -> RenderUtil.drawTriangleFilled2(
                                 (float)(x - 5), (float)y + crossDistance, 5.0F, 9.0F, ColorUtils.getColor(rgb[0], rgb[1], rgb[2], 255)
                              ),
                           false
                        );
                     }
                     break;
                  case "Shadow":
                     if (Aqua.moduleManager.getModuleByName("Shadow").isToggled()) {
                        Shadow.drawGlow(
                           () -> RenderUtil.drawTriangleFilled2((float)(x - 5), (float)y + crossDistance, 5.0F, 9.0F, Color.black.getRGB()), false
                        );
                     }
               }
            }

            RenderUtil.drawTriangleFilled2((float)(x - 5), (float)y + crossDistance, 5.0F, 9.0F, ColorUtils.getColor(rgb[0], rgb[1], rgb[2], alphaBackground));
            GlStateManager.translate((float)x, (float)y, 0.0F);
            GlStateManager.rotate(-angle, 0.0F, 0.0F, 1.0F);
            GlStateManager.translate((float)(-x), (float)(-y), 0.0F);
            GlStateManager.popMatrix();
            GlStateManager.resetColor();
         }
      }
   }
}
