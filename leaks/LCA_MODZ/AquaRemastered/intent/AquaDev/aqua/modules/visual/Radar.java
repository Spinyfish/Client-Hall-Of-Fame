package intent.AquaDev.aqua.modules.visual;

import de.Hero.settings.Setting;
import events.Event;
import events.listeners.EventPostRender2D;
import events.listeners.EventRender2D;
import intent.AquaDev.aqua.Aqua;
import intent.AquaDev.aqua.modules.Category;
import intent.AquaDev.aqua.modules.Module;
import intent.AquaDev.aqua.utils.ColorUtils;
import intent.AquaDev.aqua.utils.RenderUtil;
import java.awt.Color;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

public class Radar extends Module {
   public static int mouseX;
   public static int mouseY;
   public static int lastMouseX;
   public static int lastMouseY;
   public static boolean dragging;
   public static int posX = 2;
   public static int posY = 23;
   private static double Alpha = 0.0;

   public Radar() {
      super("Radar", Module.Type.Overlay, "Radar", 0, Category.Visual);
      Aqua.setmgr.register(new Setting("PosY", this, 23.0, 0.0, 325.0, false));
      Aqua.setmgr.register(new Setting("Size", this, 55.0, 18.0, 300.0, false));
      Aqua.setmgr.register(new Setting("Mode", this, "Glow", new String[]{"Glow", "Shadow"}));
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
         renderRadarShaders(mouseX, mouseY);
      }

      if (e instanceof EventPostRender2D) {
         renderRadar(mouseX, mouseY);
      }
   }

   public static void renderRadar(int mouseX, int mouseY) {
      double width = (double)((int)Aqua.setmgr.getSetting("RadarSize").getCurrentNumber());
      double height = (double)((int)Aqua.setmgr.getSetting("RadarSize").getCurrentNumber());
      Radar.posY = (int)Aqua.setmgr.getSetting("RadarPosY").getCurrentNumber();
      if (dragging) {
         Radar.mouseX = mouseX - lastMouseX;
         Radar.mouseY = mouseY - lastMouseY;
      }

      int posX = Radar.posX + Radar.mouseX;
      int posY = Radar.posY + Radar.mouseY;
      RenderUtil.drawRoundedRect2Alpha((double)posX, (double)(posY + 2), width, height - 2.0, 3.0, new Color(22, 22, 22, 60));
      double halfWidth = width / 2.0 + 0.5;
      double halfHeight = height / 2.0 - 0.5;
      Gui.drawRect2(
         (double)posX + halfWidth,
         (double)posY + halfHeight,
         (double)posX + halfWidth + 1.0,
         (double)posY + halfHeight + 1.0,
         ColorUtils.getColorAlpha(
               Arraylist.getGradientOffset(
                  new Color(Aqua.setmgr.getSetting("HUDColor").getColor()), new Color(Aqua.setmgr.getSetting("ArraylistColor").getColor()), 15.0
               ),
               255
            )
            .getRGB()
      );

      for(EntityPlayer player : mc.theWorld.playerEntities) {
         if (player != mc.thePlayer) {
            double playerX = player.posX;
            double playerZ = player.posZ;
            double diffX = playerX - mc.thePlayer.posX;
            double diffZ = playerZ - mc.thePlayer.posZ;
            if (MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ) < 50.0F) {
               double clampedX = MathHelper.clamp_double(diffX, -halfWidth + 3.0, halfWidth - 3.0);
               double clampedY = MathHelper.clamp_double(diffZ, -halfHeight + 5.0, halfHeight - 3.0);
               Gui.drawRect2(
                  (double)posX + halfWidth + clampedX,
                  (double)posY + halfHeight + clampedY,
                  (double)posX + halfWidth + clampedX + 1.0,
                  (double)posY + halfHeight + clampedY + 1.0,
                  Color.white.getRGB()
               );
            }
         }
      }
   }

   public static void renderRadarShaders(int mouseX, int mouseY) {
      double width = (double)((int)Aqua.setmgr.getSetting("RadarSize").getCurrentNumber());
      double height = (double)((int)Aqua.setmgr.getSetting("RadarSize").getCurrentNumber());
      Radar.posY = (int)Aqua.setmgr.getSetting("RadarPosY").getCurrentNumber();
      if (dragging) {
         Radar.mouseX = mouseX - lastMouseX;
         Radar.mouseY = mouseY - lastMouseY;
      }

      int posX = Radar.posX + Radar.mouseX;
      int posY = Radar.posY + Radar.mouseY;
      if (Aqua.moduleManager.getModuleByName("Blur").isToggled()) {
         Blur.drawBlurred(
            () -> RenderUtil.drawRoundedRect((double)posX, (double)(posY + 2), width, height - 2.0, 3.0, new Color(22, 22, 22, 100).getRGB()), false
         );
      }

      if (Aqua.setmgr.getSetting("RadarMode").getCurrentMode().equalsIgnoreCase("Glow")) {
         if (Aqua.moduleManager.getModuleByName("Arraylist").isToggled()) {
            ShaderMultiplier.drawGlowESP(
               () -> RenderUtil.drawRoundedRect(
                     (double)posX,
                     (double)(posY + 2),
                     width,
                     height - 2.0,
                     3.0,
                     ColorUtils.getColorAlpha(
                           Arraylist.getGradientOffset(
                              new Color(Aqua.setmgr.getSetting("HUDColor").getColor()), new Color(Aqua.setmgr.getSetting("ArraylistColor").getColor()), 15.0
                           ),
                           255
                        )
                        .getRGB()
                  ),
               false
            );
         }
      } else if (Aqua.moduleManager.getModuleByName("Shadow").isToggled()) {
         Shadow.drawGlow(() -> RenderUtil.drawRoundedRect((double)posX, (double)(posY + 2), width, height - 2.0, 3.0, Color.black.getRGB()), false);
      }
   }
}
