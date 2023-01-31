package intent.AquaDev.aqua.modules.visual;

import de.Hero.settings.Setting;
import events.Event;
import events.listeners.EventPostRender2D;
import events.listeners.EventRender2D;
import intent.AquaDev.aqua.Aqua;
import intent.AquaDev.aqua.modules.Category;
import intent.AquaDev.aqua.modules.Module;
import intent.AquaDev.aqua.utils.RenderUtil;
import java.awt.Color;

public class KeyStrokes extends Module {
   public KeyStrokes() {
      super("KeyStrokes", Module.Type.Visual, "KeyStrokes", 0, Category.Visual);
      Aqua.setmgr.register(new Setting("Left|Right", this, false));
      Aqua.setmgr.register(new Setting("ClientColor", this, true));
      Aqua.setmgr.register(new Setting("PosY", this, 120.0, 0.0, 400.0, false));
      Aqua.setmgr.register(new Setting("Color", this));
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
         this.render();
      }

      if (e instanceof EventRender2D) {
         this.renderShaders();
      }
   }

   public void renderShaders() {
      int posX = 4;
      int posY = (int)Aqua.setmgr.getSetting("KeyStrokesPosY").getCurrentNumber();
      int width = 20;
      int height = 20;
      int cornerRadius = 3;
      boolean lmbPressed;
      if (mc.gameSettings.keyBindAttack.pressed) {
         lmbPressed = true;
      } else {
         lmbPressed = false;
      }

      boolean rmbPressed;
      if (mc.gameSettings.keyBindUseItem.pressed) {
         rmbPressed = true;
      } else {
         rmbPressed = false;
      }

      boolean spacePressed;
      if (mc.gameSettings.keyBindJump.pressed) {
         spacePressed = true;
      } else {
         spacePressed = false;
      }

      boolean aPressed;
      if (mc.gameSettings.keyBindLeft.pressed) {
         aPressed = true;
      } else {
         aPressed = false;
      }

      boolean sPressed;
      if (mc.gameSettings.keyBindBack.pressed) {
         sPressed = true;
      } else {
         sPressed = false;
      }

      boolean dPressed;
      if (mc.gameSettings.keyBindRight.pressed) {
         dPressed = true;
      } else {
         dPressed = false;
      }

      boolean wPressed;
      if (mc.gameSettings.keyBindForward.pressed) {
         wPressed = true;
      } else {
         wPressed = false;
      }

      boolean blurEnabled = false;
      boolean shadow;
      if (Aqua.moduleManager.getModuleByName("Shadow").isToggled()) {
         shadow = true;
      } else {
         shadow = false;
      }

      boolean glow;
      if (Aqua.moduleManager.getModuleByName("ShaderMultiplier").isToggled()) {
         glow = true;
      } else {
         glow = false;
      }

      if (Aqua.moduleManager.getModuleByName("Blur").isToggled()) {
         blurEnabled = true;
      } else {
         blurEnabled = false;
      }

      Color pressed = RenderUtil.getColorAlpha(
         Aqua.setmgr.getSetting("KeyStrokesClientColor").isState()
            ? new Color(Aqua.setmgr.getSetting("HUDColor").getColor()).getRGB()
            : new Color(this.getColor2()).getRGB(),
         120
      );
      int color = Color.black.getRGB();
      if (blurEnabled) {
         Blur.drawBlurred(
            () -> RenderUtil.drawRoundedRect((double)posX, (double)posY, (double)width, (double)height, (double)cornerRadius, pressed.getRGB()), false
         );
      }

      if (aPressed) {
         if (glow) {
            ShaderMultiplier.drawGlowESP(
               () -> RenderUtil.drawRoundedRect((double)posX, (double)posY, (double)width, (double)height, (double)cornerRadius, pressed.getRGB()), false
            );
         }
      } else if (shadow) {
         Shadow.drawGlow(() -> RenderUtil.drawRoundedRect((double)posX, (double)posY, (double)width, (double)height, (double)cornerRadius, color), false);
      }

      if (blurEnabled) {
         Blur.drawBlurred(
            () -> RenderUtil.drawRoundedRect((double)(posX + 25), (double)posY, (double)width, (double)height, (double)cornerRadius, pressed.getRGB()), false
         );
      }

      if (sPressed) {
         if (glow) {
            ShaderMultiplier.drawGlowESP(
               () -> RenderUtil.drawRoundedRect((double)(posX + 25), (double)posY, (double)width, (double)height, (double)cornerRadius, pressed.getRGB()),
               false
            );
         }
      } else if (shadow) {
         Shadow.drawGlow(
            () -> RenderUtil.drawRoundedRect((double)(posX + 25), (double)posY, (double)width, (double)height, (double)cornerRadius, color), false
         );
      }

      if (blurEnabled) {
         Blur.drawBlurred(
            () -> RenderUtil.drawRoundedRect((double)(posX + 50), (double)posY, (double)width, (double)height, (double)cornerRadius, pressed.getRGB()), false
         );
      }

      if (dPressed) {
         if (glow) {
            ShaderMultiplier.drawGlowESP(
               () -> RenderUtil.drawRoundedRect((double)(posX + 50), (double)posY, (double)width, (double)height, (double)cornerRadius, pressed.getRGB()),
               false
            );
         }
      } else if (shadow) {
         Shadow.drawGlow(
            () -> RenderUtil.drawRoundedRect((double)(posX + 50), (double)posY, (double)width, (double)height, (double)cornerRadius, color), false
         );
      }

      if (blurEnabled) {
         Blur.drawBlurred(
            () -> RenderUtil.drawRoundedRect((double)(posX + 25), (double)(posY - 25), (double)width, (double)height, (double)cornerRadius, pressed.getRGB()),
            false
         );
      }

      if (wPressed) {
         if (glow) {
            ShaderMultiplier.drawGlowESP(
               () -> RenderUtil.drawRoundedRect(
                     (double)(posX + 25), (double)(posY - 25), (double)width, (double)height, (double)cornerRadius, pressed.getRGB()
                  ),
               false
            );
         }
      } else if (shadow) {
         Shadow.drawGlow(
            () -> RenderUtil.drawRoundedRect((double)(posX + 25), (double)(posY - 25), (double)width, (double)height, (double)cornerRadius, color), false
         );
      }

      if (Aqua.setmgr.getSetting("KeyStrokesLeft|Right").isState()) {
         if (blurEnabled) {
            Blur.drawBlurred(
               () -> RenderUtil.drawRoundedRect(
                     (double)posX, (double)(posY + 25), (double)(width + 15), (double)(height - 5), (double)cornerRadius, pressed.getRGB()
                  ),
               false
            );
         }

         if (lmbPressed) {
            if (glow) {
               ShaderMultiplier.drawGlowESP(
                  () -> RenderUtil.drawRoundedRect(
                        (double)posX, (double)(posY + 25), (double)(width + 15), (double)(height - 5), (double)cornerRadius, pressed.getRGB()
                     ),
                  false
               );
            }
         } else if (shadow) {
            Shadow.drawGlow(
               () -> RenderUtil.drawRoundedRect((double)posX, (double)(posY + 25), (double)(width + 15), (double)(height - 5), (double)cornerRadius, color),
               false
            );
         }

         if (blurEnabled) {
            Blur.drawBlurred(
               () -> RenderUtil.drawRoundedRect(
                     (double)(posX + 36), (double)(posY + 25), (double)(width + 15), (double)(height - 5), (double)cornerRadius, pressed.getRGB()
                  ),
               false
            );
         }

         if (rmbPressed) {
            if (glow) {
               ShaderMultiplier.drawGlowESP(
                  () -> RenderUtil.drawRoundedRect(
                        (double)(posX + 36), (double)(posY + 25), (double)(width + 15), (double)(height - 5), (double)cornerRadius, pressed.getRGB()
                     ),
                  false
               );
            }
         } else if (shadow) {
            Shadow.drawGlow(
               () -> RenderUtil.drawRoundedRect(
                     (double)(posX + 36), (double)(posY + 25), (double)(width + 15), (double)(height - 5), (double)cornerRadius, color
                  ),
               false
            );
         }

         if (blurEnabled) {
            Blur.drawBlurred(
               () -> RenderUtil.drawRoundedRect(
                     (double)posX, (double)(posY + 45), (double)(width + 50), (double)(height - 5), (double)cornerRadius, pressed.getRGB()
                  ),
               false
            );
         }

         if (spacePressed) {
            if (glow) {
               ShaderMultiplier.drawGlowESP(
                  () -> RenderUtil.drawRoundedRect(
                        (double)posX, (double)(posY + 45), (double)(width + 50), (double)(height - 5), (double)cornerRadius, pressed.getRGB()
                     ),
                  false
               );
            }
         } else if (shadow) {
            Shadow.drawGlow(
               () -> RenderUtil.drawRoundedRect((double)posX, (double)(posY + 45), (double)(width + 50), (double)(height - 5), (double)cornerRadius, color),
               false
            );
         }
      } else {
         if (blurEnabled) {
            Blur.drawBlurred(
               () -> RenderUtil.drawRoundedRect(
                     (double)posX, (double)(posY + 25), (double)(width + 50), (double)(height - 5), (double)cornerRadius, pressed.getRGB()
                  ),
               false
            );
         }

         if (spacePressed) {
            if (glow) {
               ShaderMultiplier.drawGlowESP(
                  () -> RenderUtil.drawRoundedRect(
                        (double)posX, (double)(posY + 25), (double)(width + 50), (double)(height - 5), (double)cornerRadius, pressed.getRGB()
                     ),
                  false
               );
            }
         } else if (shadow) {
            Shadow.drawGlow(
               () -> RenderUtil.drawRoundedRect((double)posX, (double)(posY + 25), (double)(width + 50), (double)(height - 5), (double)cornerRadius, color),
               false
            );
         }
      }
   }

   public void render() {
      int posX = 4;
      int posY = (int)Aqua.setmgr.getSetting("KeyStrokesPosY").getCurrentNumber();
      int width = 20;
      int height = 20;
      int cornerRadius = 3;
      boolean lmbPressed;
      if (mc.gameSettings.keyBindAttack.pressed) {
         lmbPressed = true;
      } else {
         lmbPressed = false;
      }

      boolean rmbPressed;
      if (mc.gameSettings.keyBindUseItem.pressed) {
         rmbPressed = true;
      } else {
         rmbPressed = false;
      }

      boolean spacePressed;
      if (mc.gameSettings.keyBindJump.pressed) {
         spacePressed = true;
      } else {
         spacePressed = false;
      }

      boolean aPressed;
      if (mc.gameSettings.keyBindLeft.pressed) {
         aPressed = true;
      } else {
         aPressed = false;
      }

      boolean sPressed;
      if (mc.gameSettings.keyBindBack.pressed) {
         sPressed = true;
      } else {
         sPressed = false;
      }

      boolean dPressed;
      if (mc.gameSettings.keyBindRight.pressed) {
         dPressed = true;
      } else {
         dPressed = false;
      }

      boolean wPressed;
      if (mc.gameSettings.keyBindForward.pressed) {
         wPressed = true;
      } else {
         wPressed = false;
      }

      Color pressed = RenderUtil.getColorAlpha(
         Aqua.setmgr.getSetting("KeyStrokesClientColor").isState()
            ? new Color(Aqua.setmgr.getSetting("HUDColor").getColor()).getRGB()
            : new Color(this.getColor2()).getRGB(),
         120
      );
      if (aPressed) {
         RenderUtil.drawRoundedRect2Alpha((double)posX, (double)posY, (double)width, (double)height, (double)cornerRadius, pressed);
      } else {
         RenderUtil.drawRoundedRect2Alpha((double)posX, (double)posY, (double)width, (double)height, (double)cornerRadius, new Color(20, 20, 20, 70));
      }

      if (sPressed) {
         RenderUtil.drawRoundedRect2Alpha((double)(posX + 25), (double)posY, (double)width, (double)height, (double)cornerRadius, pressed);
      } else {
         RenderUtil.drawRoundedRect2Alpha((double)(posX + 25), (double)posY, (double)width, (double)height, (double)cornerRadius, new Color(20, 20, 20, 70));
      }

      if (dPressed) {
         RenderUtil.drawRoundedRect2Alpha((double)(posX + 50), (double)posY, (double)width, (double)height, (double)cornerRadius, pressed);
      } else {
         RenderUtil.drawRoundedRect2Alpha((double)(posX + 50), (double)posY, (double)width, (double)height, (double)cornerRadius, new Color(20, 20, 20, 70));
      }

      if (wPressed) {
         RenderUtil.drawRoundedRect2Alpha((double)(posX + 25), (double)(posY - 25), (double)width, (double)height, (double)cornerRadius, pressed);
      } else {
         RenderUtil.drawRoundedRect2Alpha(
            (double)(posX + 25), (double)(posY - 25), (double)width, (double)height, (double)cornerRadius, new Color(20, 20, 20, 70)
         );
      }

      if (Aqua.setmgr.getSetting("KeyStrokesLeft|Right").isState()) {
         if (lmbPressed) {
            RenderUtil.drawRoundedRect2Alpha((double)posX, (double)(posY + 25), (double)(width + 15), (double)(height - 5), (double)cornerRadius, pressed);
         } else {
            RenderUtil.drawRoundedRect2Alpha(
               (double)posX, (double)(posY + 25), (double)(width + 15), (double)(height - 5), (double)cornerRadius, new Color(20, 20, 20, 70)
            );
         }

         if (rmbPressed) {
            RenderUtil.drawRoundedRect2Alpha(
               (double)(posX + 36), (double)(posY + 25), (double)(width + 15), (double)(height - 5), (double)cornerRadius, pressed
            );
         } else {
            RenderUtil.drawRoundedRect2Alpha(
               (double)(posX + 36), (double)(posY + 25), (double)(width + 15), (double)(height - 5), (double)cornerRadius, new Color(20, 20, 20, 70)
            );
         }

         if (spacePressed) {
            RenderUtil.drawRoundedRect2Alpha((double)posX, (double)(posY + 45), (double)(width + 50), (double)(height - 5), (double)cornerRadius, pressed);
         } else {
            RenderUtil.drawRoundedRect2Alpha(
               (double)posX, (double)(posY + 45), (double)(width + 50), (double)(height - 5), (double)cornerRadius, new Color(20, 20, 20, 70)
            );
         }
      } else if (spacePressed) {
         RenderUtil.drawRoundedRect2Alpha((double)posX, (double)(posY + 25), (double)(width + 50), (double)(height - 5), (double)cornerRadius, pressed);
      } else {
         RenderUtil.drawRoundedRect2Alpha(
            (double)posX, (double)(posY + 25), (double)(width + 50), (double)(height - 5), (double)cornerRadius, new Color(20, 20, 20, 70)
         );
      }

      Aqua.INSTANCE.comfortaa3.drawString("A", (float)(posX + 6), (float)(posY + 5), -1);
      Aqua.INSTANCE.comfortaa3.drawString("S", (float)(posX + 32), (float)(posY + 5), -1);
      Aqua.INSTANCE.comfortaa3.drawString("D", (float)(posX + 56), (float)(posY + 5), -1);
      Aqua.INSTANCE.comfortaa3.drawString("W", (float)(posX + 30), (float)(posY - 20), -1);
      if (Aqua.setmgr.getSetting("KeyStrokesLeft|Right").isState()) {
         Aqua.INSTANCE.comfortaa3.drawString("LMB", (float)(posX + 6), (float)(posY + 27), -1);
         Aqua.INSTANCE.comfortaa3.drawString("RMB", (float)(posX + 43), (float)(posY + 27), -1);
         Aqua.INSTANCE.comfortaa3.drawString("Space", (float)(posX + 20), (float)(posY + 47), -1);
      } else {
         Aqua.INSTANCE.comfortaa3.drawString("Space", (float)(posX + 20), (float)(posY + 27), -1);
      }
   }

   public int getColor2() {
      try {
         return Aqua.setmgr.getSetting("KeyStrokesColor").getColor();
      } catch (Exception var2) {
         return Color.white.getRGB();
      }
   }
}
