package intent.AquaDev.aqua.modules.visual;

import de.Hero.settings.Setting;
import events.Event;
import events.listeners.EventPostRender2D;
import events.listeners.EventRender2D;
import intent.AquaDev.aqua.Aqua;
import intent.AquaDev.aqua.fr.lavache.anime.Easing;
import intent.AquaDev.aqua.gui.novolineOld.themesScreen.ThemeScreen;
import intent.AquaDev.aqua.modules.Category;
import intent.AquaDev.aqua.modules.Module;
import intent.AquaDev.aqua.utils.ColorUtils;
import intent.AquaDev.aqua.utils.RenderUtil;
import java.awt.Color;
import java.nio.ByteBuffer;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.opengl.GL11;

public class Arraylist extends Module {
   Minecraft MC = Minecraft.getMinecraft();
   Color randomColor;

   public Arraylist() {
      super("Arraylist", Module.Type.Visual, "Arraylist", 0, Category.Visual);
      Aqua.setmgr.register(new Setting("Rainbow", this, true));
      Aqua.setmgr.register(new Setting("BiggerOffset", this, true));
      Aqua.setmgr.register(new Setting("Fade", this, true));
      Aqua.setmgr.register(new Setting("ShaderFade", this, true));
      Aqua.setmgr.register(new Setting("GlowRects", this, true));
      Aqua.setmgr.register(new Setting("GlowStrings", this, true));
      Aqua.setmgr.register(new Setting("Blur", this, true));
      Aqua.setmgr.register(new Setting("Background", this, true));
      Aqua.setmgr.register(new Setting("BackgroundFade", this, true));
      Aqua.setmgr.register(new Setting("FontShadow", this, false));
      Aqua.setmgr.register(new Setting("ReverseFade", this, true));
      Aqua.setmgr.register(new Setting("Sort", this, true));
      Aqua.setmgr.register(new Setting("StringAlpha", this, 255.0, 0.0, 255.0, true));
      Aqua.setmgr.register(new Setting("Sigma", this, 5.0, 0.0, 50.0, true));
      Aqua.setmgr.register(new Setting("Multiplier", this, 1.0, 0.0, 3.0, false));
      Aqua.setmgr.register(new Setting("Alpha", this, 80.0, 5.0, 240.0, true));
      Aqua.setmgr.register(new Setting("Color", this));
      Aqua.setmgr.register(new Setting("Fonts", this, "Comfortaa", new String[]{"Comfortaa", "Minecraft"}));
      Aqua.setmgr.register(new Setting("Shader", this, "Glow", new String[]{"Glow", "Shadow"}));
   }

   @Override
   public void onEnable() {
      Random rand = new Random();
      float r = rand.nextFloat();
      float g = rand.nextFloat();
      float b = rand.nextFloat();
      this.randomColor = new Color(r, g, b);
      super.onEnable();
   }

   @Override
   public void onDisable() {
      super.onDisable();
   }

   public static void drawGlowArray(Runnable runnable, boolean renderTwice) {
   }

   @Override
   public void onEvent(Event event) {
      if (event instanceof EventRender2D) {
         if (ThemeScreen.themeLoaded) {
            if (ThemeScreen.themeRise) {
               this.drawRiseBlur();
            }

            if (ThemeScreen.themeAqua2) {
               drawRectsSuffix2();
            }

            if (ThemeScreen.themeTenacity) {
               this.drawTenacityArraylistBlur();
            }
         }

         if (!ThemeScreen.themeLoaded) {
            if (Aqua.setmgr.getSetting("ArraylistFonts").getCurrentMode().equalsIgnoreCase("Comfortaa")) {
               if (Aqua.setmgr.getSetting("ArraylistBlur").isState()) {
                  if (!Aqua.setmgr.getSetting("ArraylistBiggerOffset").isState()) {
                     this.drawShaderRectsBlur();
                  } else {
                     this.drawShaderRectsBlurBiggerOffset();
                  }
               }

               if (Aqua.setmgr.getSetting("ArraylistGlowRects").isState()) {
                  if (!Aqua.setmgr.getSetting("ArraylistBiggerOffset").isState()) {
                     this.drawShaderRects();
                  } else {
                     this.drawShaderRectsBiggerOffset();
                  }
               }
            }

            if (Aqua.setmgr.getSetting("ArraylistFonts").getCurrentMode().equalsIgnoreCase("Minecraft")) {
               if (Aqua.setmgr.getSetting("ArraylistBlur").isState()) {
                  this.drawShaderRectsMinecraftBlur();
               }

               if (Aqua.setmgr.getSetting("ArraylistGlowRects").isState()) {
                  this.drawShaderRectsMinecraft();
               }
            }
         }
      }

      if (event instanceof EventPostRender2D) {
         if (ThemeScreen.themeHero) {
            this.drawHeroArraylist();
         }

         if (ThemeScreen.themeRise6) {
            this.drawRise6Arraylist();
         }

         if (ThemeScreen.themeAqua2) {
            drawRectsSuffix();
            this.drawStringsSuffix();
         }

         if (ThemeScreen.themeSigma) {
            this.drawSigmaArraylistBackground();
            this.drawSigmaArraylist();
         }

         if (ThemeScreen.themeTenacity) {
            this.drawTenacityArraylist();
         }

         if (ThemeScreen.themeJello) {
            this.drawJelloArraylist();
         }

         if (ThemeScreen.themeRise) {
            this.drawRiseArraylist();
         }

         if (ThemeScreen.themeXave) {
            this.drawXaveArray();
         }

         if (!ThemeScreen.themeLoaded) {
            if (Aqua.setmgr.getSetting("ArraylistFonts").getCurrentMode().equalsIgnoreCase("Comfortaa")) {
               if (!Aqua.setmgr.getSetting("ArraylistBiggerOffset").isState()) {
                  this.drawRects2();
               } else {
                  this.drawRects2BiggerOffset();
               }
            }

            if (Aqua.setmgr.getSetting("ArraylistFonts").getCurrentMode().equalsIgnoreCase("Minecraft")) {
               this.drawRects2Minecraft();
            }

            if (Aqua.setmgr.getSetting("ArraylistFonts").getCurrentMode().equalsIgnoreCase("Comfortaa")) {
               if (Aqua.setmgr.getSetting("ArraylistGlowStrings").isState()) {
                  if (!Aqua.setmgr.getSetting("ArraylistBiggerOffset").isState()) {
                     Shadow.drawGlow(() -> this.drawStrings(), false);
                  }
               } else {
                  Shadow.drawGlow(() -> this.drawStringsBiggerOffset(), false);
               }
            }

            if (Aqua.setmgr.getSetting("ArraylistFonts").getCurrentMode().equalsIgnoreCase("Minecraft")
               && Aqua.setmgr.getSetting("ArraylistGlowStrings").isState()) {
               Shadow.drawGlow(() -> this.drawMinecraftStrings(), false);
            }

            if (Aqua.setmgr.getSetting("ArraylistFonts").getCurrentMode().equalsIgnoreCase("Comfortaa")) {
               if (!Aqua.setmgr.getSetting("ArraylistBiggerOffset").isState()) {
                  this.drawStrings();
               } else {
                  this.drawStringsBiggerOffset();
               }
            }

            if (Aqua.setmgr.getSetting("ArraylistFonts").getCurrentMode().equalsIgnoreCase("Minecraft")) {
               this.drawMinecraftStrings();
            }
         }
      }
   }

   public void drawShaderRectsBlur() {
      ScaledResolution sr = new ScaledResolution(mc);
      int index = 0;
      float offset2 = 0.0F;
      GL11.glBlendFunc(770, 771);
      float offset = 0.0F;
      List<Module> collect = Aqua.moduleManager
         .modules
         .stream()
         .filter(Module::isToggled)
         .sorted(Comparator.comparingInt(value -> (int)((float)(-sr.getScaledWidth()) - Aqua.INSTANCE.comfortaa3.getWidth(value.getName()))))
         .collect(Collectors.toList());
      int i = 0;

      for(int collectSize = collect.size(); i < collectSize; ++i) {
         Module m1 = collect.get(i);
         Module nextModule = null;
         if (i < collectSize - 1) {
            nextModule = collect.get(i + 1);
         }

         if (m1.isToggled()) {
            float wSet2 = (float)sr.getScaledWidth() - Aqua.INSTANCE.comfortaa3.getWidth(m1.getName()) - 5.0F;
            ++index;
            float finalOffset = offset;
            int rainbow = rainbow((int)offset * 9);
            int color = Aqua.setmgr.getSetting("ArraylistReverseFade").isState()
               ? getGradientOffset(new Color(Aqua.setmgr.getSetting("HUDColor").getColor()), new Color(this.getColor2()), (double)index / 12.4).getRGB()
               : getGradientOffset(new Color(this.getColor2()), new Color(Aqua.setmgr.getSetting("HUDColor").getColor()), (double)index / 12.4).getRGB();
            int finalColor = Aqua.setmgr.getSetting("ArraylistFade").isState() ? color : new Color(Aqua.setmgr.getSetting("HUDColor").getColor()).getRGB();
            int doubleFinalColor = Aqua.setmgr.getSetting("ArraylistRainbow").isState() ? rainbow : finalColor;
            Gui.drawRect((int)((float)sr.getScaledWidth() - 5.0F), (int)(offset + 4.0F), sr.getScaledWidth() - 3, (int)(offset + 15.0F), doubleFinalColor);
            if (Aqua.moduleManager.getModuleByName("Blur").isToggled() && Aqua.setmgr.getSetting("ArraylistBlur").isState()) {
               Blur.drawBlurred(
                  () -> Gui.drawRect2(
                        (double)(wSet2 - 5.0F),
                        (double)(finalOffset + 4.0F),
                        (double)(sr.getScaledWidth() - 5),
                        (double)(finalOffset + 15.0F),
                        Integer.MIN_VALUE
                     ),
                  false
               );
            }

            ++index;
            offset += 11.0F;
            ++offset2;
            GL11.glDisable(3042);
         }
      }
   }

   public void drawShaderRectsBlurBiggerOffset() {
      ScaledResolution sr = new ScaledResolution(mc);
      int index = 0;
      float offset2 = 0.0F;
      GL11.glBlendFunc(770, 771);
      float offset = 0.0F;
      List<Module> collect = Aqua.moduleManager
         .modules
         .stream()
         .filter(Module::isToggled)
         .sorted(Comparator.comparingInt(value -> (int)((float)(-sr.getScaledWidth()) - Aqua.INSTANCE.comfortaa3.getWidth(value.getName()))))
         .collect(Collectors.toList());
      int i = 0;

      for(int collectSize = collect.size(); i < collectSize; ++i) {
         Module m1 = collect.get(i);
         Module nextModule = null;
         if (i < collectSize - 1) {
            nextModule = collect.get(i + 1);
         }

         if (m1.isToggled()) {
            float wSet2 = (float)sr.getScaledWidth() - Aqua.INSTANCE.comfortaa3.getWidth(m1.getName()) - 5.0F;
            ++index;
            float finalOffset = offset;
            int rainbow = rainbow((int)offset * 9);
            int color = Aqua.setmgr.getSetting("ArraylistReverseFade").isState()
               ? getGradientOffset(new Color(Aqua.setmgr.getSetting("HUDColor").getColor()), new Color(this.getColor2()), (double)index / 12.4).getRGB()
               : getGradientOffset(new Color(this.getColor2()), new Color(Aqua.setmgr.getSetting("HUDColor").getColor()), (double)index / 12.4).getRGB();
            int finalColor = Aqua.setmgr.getSetting("ArraylistFade").isState() ? color : new Color(Aqua.setmgr.getSetting("HUDColor").getColor()).getRGB();
            int doubleFinalColor = Aqua.setmgr.getSetting("ArraylistRainbow").isState() ? rainbow : finalColor;
            if (Aqua.moduleManager.getModuleByName("Blur").isToggled() && Aqua.setmgr.getSetting("ArraylistBlur").isState()) {
               Blur.drawBlurred(
                  () -> Gui.drawRect2(
                        (double)(wSet2 - 5.0F),
                        (double)(finalOffset + 2.0F),
                        (double)(sr.getScaledWidth() - 5),
                        (double)(finalOffset + 16.0F),
                        Integer.MIN_VALUE
                     ),
                  false
               );
            }

            ++index;
            offset += 14.0F;
            ++offset2;
            GL11.glDisable(3042);
         }
      }
   }

   public void drawShaderRects() {
      ScaledResolution sr = new ScaledResolution(mc);
      int index = 0;
      float offset2 = 0.0F;
      GL11.glBlendFunc(770, 771);
      float offset = 0.0F;
      List<Module> collect = Aqua.moduleManager
         .modules
         .stream()
         .filter(Module::isToggled)
         .sorted(Comparator.comparingInt(value -> (int)((float)(-sr.getScaledWidth()) - Aqua.INSTANCE.comfortaa3.getWidth(value.getName()))))
         .collect(Collectors.toList());
      int i = 0;

      for(int collectSize = collect.size(); i < collectSize; ++i) {
         Module m1 = collect.get(i);
         Module nextModule = null;
         if (i < collectSize - 1) {
            nextModule = collect.get(i + 1);
         }

         if (m1.isToggled()) {
            float wSet2 = (float)sr.getScaledWidth() - Aqua.INSTANCE.comfortaa3.getWidth(m1.getName()) - 5.0F;
            ++index;
            float finalOffset = offset;
            int rainbow = rainbow((int)offset * 9);
            int color = Aqua.setmgr.getSetting("ArraylistReverseFade").isState()
               ? getGradientOffset(new Color(Aqua.setmgr.getSetting("HUDColor").getColor()), new Color(this.getColor2()), (double)index / 12.4).getRGB()
               : getGradientOffset(new Color(this.getColor2()), new Color(Aqua.setmgr.getSetting("HUDColor").getColor()), (double)index / 12.4).getRGB();
            int finalColor = Aqua.setmgr.getSetting("ArraylistFade").isState() ? color : new Color(Aqua.setmgr.getSetting("HUDColor").getColor()).getRGB();
            int doubleFinalColor = Aqua.setmgr.getSetting("ArraylistRainbow").isState() ? rainbow : finalColor;
            if (Aqua.setmgr.getSetting("ArraylistShader").getCurrentMode().equalsIgnoreCase("Glow")) {
               ShaderMultiplier.drawGlowESP(
                  () -> Gui.drawRect2(
                        (double)(wSet2 - 5.0F),
                        (double)(finalOffset + 4.0F),
                        (double)(sr.getScaledWidth() - 5),
                        (double)(finalOffset + 15.0F),
                        new Color(doubleFinalColor).getRGB()
                     ),
                  false
               );
            } else {
               Shadow.drawGlow(
                  () -> Gui.drawRect2(
                        (double)(wSet2 - 5.0F),
                        (double)(finalOffset + 4.0F),
                        (double)(sr.getScaledWidth() - 5),
                        (double)(finalOffset + 15.0F),
                        Color.black.getRGB()
                     ),
                  false
               );
            }

            if (Aqua.moduleManager.getModuleByName("Blur").isToggled() && Aqua.setmgr.getSetting("ArraylistBlur").isState()) {
               Blur.drawBlurred(
                  () -> Gui.drawRect2(
                        (double)(wSet2 - 5.0F),
                        (double)(finalOffset + 4.0F),
                        (double)(sr.getScaledWidth() - 5),
                        (double)(finalOffset + 15.0F),
                        Integer.MIN_VALUE
                     ),
                  false
               );
            }

            ++index;
            offset += 11.0F;
            ++offset2;
            GL11.glDisable(3042);
         }
      }
   }

   public void drawShaderRectsBiggerOffset() {
      ScaledResolution sr = new ScaledResolution(mc);
      int index = 0;
      float offset2 = 0.0F;
      GL11.glBlendFunc(770, 771);
      float offset = 0.0F;
      List<Module> collect = Aqua.moduleManager
         .modules
         .stream()
         .filter(Module::isToggled)
         .sorted(Comparator.comparingInt(value -> (int)((float)(-sr.getScaledWidth()) - Aqua.INSTANCE.comfortaa3.getWidth(value.getName()))))
         .collect(Collectors.toList());
      int i = 0;

      for(int collectSize = collect.size(); i < collectSize; ++i) {
         Module m1 = collect.get(i);
         Module nextModule = null;
         if (i < collectSize - 1) {
            nextModule = collect.get(i + 1);
         }

         if (m1.isToggled()) {
            float wSet2 = (float)sr.getScaledWidth() - Aqua.INSTANCE.comfortaa3.getWidth(m1.getName()) - 5.0F;
            ++index;
            float finalOffset = offset;
            int rainbow = rainbow((int)offset * 9);
            int color = Aqua.setmgr.getSetting("ArraylistReverseFade").isState()
               ? getGradientOffset(new Color(Aqua.setmgr.getSetting("HUDColor").getColor()), new Color(this.getColor2()), (double)index / 12.4).getRGB()
               : getGradientOffset(new Color(this.getColor2()), new Color(Aqua.setmgr.getSetting("HUDColor").getColor()), (double)index / 12.4).getRGB();
            int finalColor = Aqua.setmgr.getSetting("ArraylistFade").isState() ? color : new Color(Aqua.setmgr.getSetting("HUDColor").getColor()).getRGB();
            int doubleFinalColor = Aqua.setmgr.getSetting("ArraylistRainbow").isState() ? rainbow : finalColor;
            if (Aqua.setmgr.getSetting("ArraylistShader").getCurrentMode().equalsIgnoreCase("Glow")) {
               Gui.drawRect((int)((float)sr.getScaledWidth() - 5.0F), (int)(offset + 2.0F), sr.getScaledWidth() - 3, (int)(offset + 16.0F), doubleFinalColor);
               drawGlowArray(
                  () -> Gui.drawRect2(
                        (double)(wSet2 - 5.0F),
                        (double)(finalOffset + 2.0F),
                        (double)(sr.getScaledWidth() - 5),
                        (double)(finalOffset + 16.0F),
                        new Color(doubleFinalColor).getRGB()
                     ),
                  false
               );
            } else {
               float finalOffset1 = offset;
               drawGlowArray(
                  () -> Gui.drawRect(
                        (int)((float)sr.getScaledWidth() - 5.0F),
                        (int)(finalOffset1 + 2.0F),
                        sr.getScaledWidth() - 3,
                        (int)(finalOffset1 + 16.0F),
                        doubleFinalColor
                     ),
                  false
               );
               Shadow.drawGlow(
                  () -> Gui.drawRect2(
                        (double)(wSet2 - 5.0F),
                        (double)(finalOffset + 2.0F),
                        (double)(sr.getScaledWidth() - 5),
                        (double)(finalOffset + 16.0F),
                        Color.black.getRGB()
                     ),
                  false
               );
               Gui.drawRect((int)((float)sr.getScaledWidth() - 5.0F), (int)(offset + 2.0F), sr.getScaledWidth() - 3, (int)(offset + 16.0F), doubleFinalColor);
            }

            if (Aqua.moduleManager.getModuleByName("Blur").isToggled() && Aqua.setmgr.getSetting("ArraylistBlur").isState()) {
            }

            ++index;
            offset += 14.0F;
            ++offset2;
            GL11.glDisable(3042);
         }
      }
   }

   public void drawShaderRectsMinecraftBlur() {
      ScaledResolution sr = new ScaledResolution(mc);
      int index = 0;
      float offset2 = 0.0F;
      GL11.glBlendFunc(770, 771);
      float offset = 0.0F;
      List<Module> collect = Aqua.moduleManager
         .modules
         .stream()
         .filter(Module::isToggled)
         .sorted(Comparator.comparingInt(value -> -sr.getScaledWidth() - mc.fontRendererObj.getStringWidth(value.getName())))
         .collect(Collectors.toList());
      int i = 0;

      for(int collectSize = collect.size(); i < collectSize; ++i) {
         Module m1 = collect.get(i);
         Module nextModule = null;
         if (i < collectSize - 1) {
            nextModule = collect.get(i + 1);
         }

         if (m1.isToggled()) {
            float wSet2 = (float)(sr.getScaledWidth() - mc.fontRendererObj.getStringWidth(m1.getName()) - 5);
            ++index;
            float finalOffset = offset;
            int rainbow = rainbow((int)offset * 9);
            int color = Aqua.setmgr.getSetting("ArraylistReverseFade").isState()
               ? getGradientOffset(new Color(Aqua.setmgr.getSetting("HUDColor").getColor()), new Color(this.getColor2()), (double)index / 12.4).getRGB()
               : getGradientOffset(new Color(this.getColor2()), new Color(Aqua.setmgr.getSetting("HUDColor").getColor()), (double)index / 12.4).getRGB();
            int finalColor = Aqua.setmgr.getSetting("ArraylistFade").isState() ? color : new Color(Aqua.setmgr.getSetting("HUDColor").getColor()).getRGB();
            int doubleFinalColor = Aqua.setmgr.getSetting("ArraylistRainbow").isState() ? rainbow : finalColor;
            Gui.drawRect((int)((float)sr.getScaledWidth() - 5.0F), (int)(offset + 4.0F), sr.getScaledWidth() - 3, (int)(offset + 15.0F), doubleFinalColor);
            if (Aqua.moduleManager.getModuleByName("Blur").isToggled()) {
               Blur.drawBlurred(
                  () -> Gui.drawRect2(
                        (double)(wSet2 - 5.0F),
                        (double)(finalOffset + 4.0F),
                        (double)(sr.getScaledWidth() - 5),
                        (double)(finalOffset + 15.0F),
                        Integer.MIN_VALUE
                     ),
                  false
               );
            }

            ++index;
            offset += 11.0F;
            ++offset2;
            GL11.glDisable(3042);
         }
      }
   }

   public void drawShaderRectsMinecraft() {
      ScaledResolution sr = new ScaledResolution(mc);
      int index = 0;
      float offset2 = 0.0F;
      GL11.glBlendFunc(770, 771);
      float offset = 0.0F;
      List<Module> collect = Aqua.moduleManager
         .modules
         .stream()
         .filter(Module::isToggled)
         .sorted(Comparator.comparingInt(value -> -sr.getScaledWidth() - mc.fontRendererObj.getStringWidth(value.getName())))
         .collect(Collectors.toList());
      int i = 0;

      for(int collectSize = collect.size(); i < collectSize; ++i) {
         Module m1 = collect.get(i);
         Module nextModule = null;
         if (i < collectSize - 1) {
            nextModule = collect.get(i + 1);
         }

         if (m1.isToggled()) {
            float wSet2 = (float)(sr.getScaledWidth() - mc.fontRendererObj.getStringWidth(m1.getName()) - 5);
            ++index;
            float finalOffset = offset;
            int rainbow = rainbow((int)offset * 9);
            int color = Aqua.setmgr.getSetting("ArraylistReverseFade").isState()
               ? getGradientOffset(new Color(Aqua.setmgr.getSetting("HUDColor").getColor()), new Color(this.getColor2()), (double)index / 12.4).getRGB()
               : getGradientOffset(new Color(this.getColor2()), new Color(Aqua.setmgr.getSetting("HUDColor").getColor()), (double)index / 12.4).getRGB();
            int finalColor = Aqua.setmgr.getSetting("ArraylistFade").isState() ? color : new Color(Aqua.setmgr.getSetting("HUDColor").getColor()).getRGB();
            int doubleFinalColor = Aqua.setmgr.getSetting("ArraylistRainbow").isState() ? rainbow : finalColor;
            if (Aqua.setmgr.getSetting("ArraylistShader").getCurrentMode().equalsIgnoreCase("Glow")) {
               drawGlowArray(
                  () -> Gui.drawRect2(
                        (double)(wSet2 - 5.0F),
                        (double)(finalOffset + 4.0F),
                        (double)(sr.getScaledWidth() - 5),
                        (double)(finalOffset + 15.0F),
                        new Color(doubleFinalColor).getRGB()
                     ),
                  false
               );
            } else {
               Shadow.drawGlow(
                  () -> Gui.drawRect2(
                        (double)(wSet2 - 5.0F),
                        (double)(finalOffset + 4.0F),
                        (double)(sr.getScaledWidth() - 5),
                        (double)(finalOffset + 15.0F),
                        Color.black.getRGB()
                     ),
                  false
               );
            }

            if (Aqua.moduleManager.getModuleByName("Blur").isToggled()) {
               Blur.drawBlurred(
                  () -> Gui.drawRect2(
                        (double)(wSet2 - 5.0F),
                        (double)(finalOffset + 4.0F),
                        (double)(sr.getScaledWidth() - 5),
                        (double)(finalOffset + 15.0F),
                        Integer.MIN_VALUE
                     ),
                  false
               );
            }

            ++index;
            offset += 11.0F;
            ++offset2;
            GL11.glDisable(3042);
         }
      }
   }

   public void drawRects2() {
      ScaledResolution sr = new ScaledResolution(mc);
      int index = 0;
      float offset2 = 0.0F;
      GL11.glBlendFunc(770, 771);
      float offset = 0.0F;
      List<Module> collect = Aqua.moduleManager
         .modules
         .stream()
         .filter(Module::isToggled)
         .sorted(Comparator.comparingInt(value -> (int)((float)(-sr.getScaledWidth()) - Aqua.INSTANCE.comfortaa3.getWidth(value.getName()))))
         .collect(Collectors.toList());
      int i = 0;

      for(int collectSize = collect.size(); i < collectSize; ++i) {
         Module m1 = collect.get(i);
         Module nextModule = null;
         if (i < collectSize - 1) {
            nextModule = collect.get(i + 1);
         }

         if (m1.isToggled()) {
            float wSet2 = (float)sr.getScaledWidth() - Aqua.INSTANCE.comfortaa3.getWidth(m1.getName()) - 5.0F;
            ++index;
            int alphaBackground = (int)Aqua.setmgr.getSetting("ArraylistAlpha").getCurrentNumber();
            if (Aqua.setmgr.getSetting("ArraylistBackground").isState() && !Aqua.setmgr.getSetting("ArraylistBackgroundFade").isState()) {
               Gui.drawRect2(
                  (double)(wSet2 - 5.0F),
                  (double)(offset + 4.0F),
                  (double)(sr.getScaledWidth() - 5),
                  (double)(offset + 15.0F),
                  new Color(0, 0, 0, alphaBackground).getRGB()
               );
            }

            if (Aqua.setmgr.getSetting("ArraylistBackground").isState() && Aqua.setmgr.getSetting("ArraylistBackgroundFade").isState()) {
               Gui.drawRect2(
                  (double)(wSet2 - 5.0F),
                  (double)(offset + 4.0F),
                  (double)(sr.getScaledWidth() - 5),
                  (double)(offset + 15.0F),
                  new Color(0, 0, 0, alphaBackground).getRGB()
               );
               Gui.drawRect2(
                  (double)(wSet2 - 5.0F),
                  (double)(offset + 4.0F),
                  (double)(sr.getScaledWidth() - 5),
                  (double)(offset + 15.0F),
                  ColorUtils.getColorAlpha(
                        getGradientOffset(new Color(Aqua.setmgr.getSetting("HUDColor").getColor()), new Color(this.getColor2()), (double)index / 12.4)
                           .getRGB(),
                        alphaBackground
                     )
                     .getRGB()
               );
               ++index;
            }

            offset += 11.0F;
            ++offset2;
            GL11.glDisable(3042);
         }
      }
   }

   public void drawRects2BiggerOffset() {
      ScaledResolution sr = new ScaledResolution(mc);
      int index = 0;
      float offset2 = 0.0F;
      GL11.glBlendFunc(770, 771);
      float offset = 0.0F;
      List<Module> collect = Aqua.moduleManager
         .modules
         .stream()
         .filter(Module::isToggled)
         .sorted(Comparator.comparingInt(value -> (int)((float)(-sr.getScaledWidth()) - Aqua.INSTANCE.comfortaa3.getWidth(value.getName()))))
         .collect(Collectors.toList());
      int i = 0;

      for(int collectSize = collect.size(); i < collectSize; ++i) {
         Module m1 = collect.get(i);
         Module nextModule = null;
         if (i < collectSize - 1) {
            nextModule = collect.get(i + 1);
         }

         if (m1.isToggled()) {
            float wSet2 = (float)sr.getScaledWidth() - Aqua.INSTANCE.comfortaa3.getWidth(m1.getName()) - 5.0F;
            ++index;
            int alphaBackground = (int)Aqua.setmgr.getSetting("ArraylistAlpha").getCurrentNumber();
            if (Aqua.setmgr.getSetting("ArraylistBackground").isState()) {
               Gui.drawRect2(
                  (double)(wSet2 - 5.0F),
                  (double)(offset + 2.0F),
                  (double)(sr.getScaledWidth() - 5),
                  (double)(offset + 16.0F),
                  new Color(0, 0, 0, alphaBackground).getRGB()
               );
            }

            offset += 14.0F;
            ++offset2;
            GL11.glDisable(3042);
         }
      }
   }

   public void drawRects2Minecraft() {
      ScaledResolution sr = new ScaledResolution(mc);
      int index = 0;
      float offset2 = 0.0F;
      GL11.glBlendFunc(770, 771);
      float offset = 0.0F;
      List<Module> collect = Aqua.moduleManager
         .modules
         .stream()
         .filter(Module::isToggled)
         .sorted(Comparator.comparingInt(value -> -sr.getScaledWidth() - mc.fontRendererObj.getStringWidth(value.getName())))
         .collect(Collectors.toList());
      int i = 0;

      for(int collectSize = collect.size(); i < collectSize; ++i) {
         Module m1 = collect.get(i);
         Module nextModule = null;
         if (i < collectSize - 1) {
            nextModule = collect.get(i + 1);
         }

         if (m1.isToggled()) {
            float wSet2 = (float)(sr.getScaledWidth() - mc.fontRendererObj.getStringWidth(m1.getName()) - 5);
            ++index;
            int alphaBackground = (int)Aqua.setmgr.getSetting("ArraylistAlpha").getCurrentNumber();
            if (Aqua.setmgr.getSetting("ArraylistBackground").isState()) {
               Gui.drawRect2(
                  (double)(wSet2 - 5.0F),
                  (double)(offset + 4.0F),
                  (double)(sr.getScaledWidth() - 5),
                  (double)(offset + 15.0F),
                  new Color(0, 0, 0, alphaBackground).getRGB()
               );
            }

            ++index;
            offset += 11.0F;
            ++offset2;
            GL11.glDisable(3042);
         }
      }
   }

   public void drawMinecraftStrings() {
      ScaledResolution sr = new ScaledResolution(mc);
      int index = 0;
      float offset2 = 0.0F;
      GL11.glBlendFunc(770, 771);
      float offset = 0.0F;
      List<Module> collect = Aqua.moduleManager
         .modules
         .stream()
         .filter(Module::isToggled)
         .sorted(Comparator.comparingInt(value -> -sr.getScaledWidth() - mc.fontRendererObj.getStringWidth(value.getName())))
         .collect(Collectors.toList());
      int i = 0;

      for(int collectSize = collect.size(); i < collectSize; ++i) {
         Module m1 = collect.get(i);
         Module nextModule = null;
         if (i < collectSize - 1) {
            nextModule = collect.get(i + 1);
         }

         if (m1.isToggled()) {
            float wSet2 = (float)(sr.getScaledWidth() - mc.fontRendererObj.getStringWidth(m1.getName()) - 5);
            int rainbow = rainbow((int)offset * 9);
            int color = Aqua.setmgr.getSetting("ArraylistReverseFade").isState()
               ? getGradientOffset(new Color(Aqua.setmgr.getSetting("HUDColor").getColor()), new Color(this.getColor2()), (double)index / 12.4).getRGB()
               : getGradientOffset(new Color(this.getColor2()), new Color(Aqua.setmgr.getSetting("HUDColor").getColor()), (double)index / 12.4).getRGB();
            int finalColor = Aqua.setmgr.getSetting("ArraylistFade").isState() ? color : new Color(Aqua.setmgr.getSetting("HUDColor").getColor()).getRGB();
            int doubleFinalColor = Aqua.setmgr.getSetting("ArraylistRainbow").isState() ? rainbow : finalColor;
            if (Aqua.setmgr.getSetting("ArraylistFontShadow").isState()) {
               mc.fontRendererObj.drawStringWithShadow(m1.getName(), wSet2 - 2.0F, offset + 6.0F, doubleFinalColor);
            } else {
               mc.fontRendererObj.drawString(m1.getName(), (int)(wSet2 - 2.0F), (int)(offset + 6.0F), doubleFinalColor);
            }

            ++index;
            ++index;
            offset += 11.0F;
            ++offset2;
            GL11.glDisable(3042);
         }
      }
   }

   public void drawStrings() {
      ScaledResolution sr = new ScaledResolution(mc);
      int index = 0;
      float offset2 = 0.0F;
      GL11.glBlendFunc(770, 771);
      float offset = 0.0F;
      List<Module> collect = Aqua.moduleManager
         .modules
         .stream()
         .filter(Module::isToggled)
         .sorted(Comparator.comparingInt(value -> (int)((float)(-sr.getScaledWidth()) - Aqua.INSTANCE.comfortaa3.getWidth(value.getName()))))
         .collect(Collectors.toList());
      int i = 0;

      for(int collectSize = collect.size(); i < collectSize; ++i) {
         Module m1 = collect.get(i);
         Module nextModule = null;
         if (i < collectSize - 1) {
            nextModule = collect.get(i + 1);
         }

         if (m1.isToggled()) {
            m1.anim
               .setEase(Easing.LINEAR)
               .setMin(0.0F)
               .setMax(Aqua.INSTANCE.comfortaa3.getWidth(m1.getName()) + 5.0F)
               .setSpeed(25.0F)
               .setReversed(!m1.isToggled())
               .update();
            m1.anim2.setEase(Easing.LINEAR).setMin(0.0F).setMax(9.0F).setSpeed(25.0F).setReversed(!m1.isToggled()).update();
            float wSet2 = (float)sr.getScaledWidth() - m1.anim.getValue();
            int rainbow = rainbow((int)offset * 9);
            int color = Aqua.setmgr.getSetting("ArraylistReverseFade").isState()
               ? getGradientOffset(new Color(Aqua.setmgr.getSetting("HUDColor").getColor()), new Color(this.getColor2()), (double)index / 12.4).getRGB()
               : getGradientOffset(new Color(this.getColor2()), new Color(Aqua.setmgr.getSetting("HUDColor").getColor()), (double)index / 12.4).getRGB();
            int finalColor = Aqua.setmgr.getSetting("ArraylistFade").isState() ? color : new Color(Aqua.setmgr.getSetting("HUDColor").getColor()).getRGB();
            int doubleFinalColor = Aqua.setmgr.getSetting("ArraylistRainbow").isState() ? rainbow : finalColor;
            Color alphaFinalColor = ColorUtils.getColorAlpha(
               new Color(doubleFinalColor).getRGB(), (int)Aqua.setmgr.getSetting("ArraylistStringAlpha").getCurrentNumber()
            );
            if (Aqua.setmgr.getSetting("ArraylistFontShadow").isState()) {
               Aqua.INSTANCE.comfortaa3.drawStringWithShadow(m1.getName(), wSet2 - 3.0F, offset + 5.0F, alphaFinalColor.getRGB());
            } else {
               Aqua.INSTANCE.comfortaa3.drawString(m1.getName(), wSet2 - 3.0F, offset + 5.0F, alphaFinalColor.getRGB());
            }

            ++index;
            ++index;
            offset += m1.anim2.getValue() + 2.0F;
            ++offset2;
            GL11.glDisable(3042);
         }
      }
   }

   public void drawStringsBiggerOffset() {
      ScaledResolution sr = new ScaledResolution(mc);
      int index = 0;
      float offset2 = 0.0F;
      GL11.glBlendFunc(770, 771);
      float offset = 0.0F;
      List<Module> collect = Aqua.moduleManager
         .modules
         .stream()
         .filter(Module::isToggled)
         .sorted(Comparator.comparingInt(value -> (int)((float)(-sr.getScaledWidth()) - Aqua.INSTANCE.comfortaa3.getWidth(value.getName()))))
         .collect(Collectors.toList());
      int i = 0;

      for(int collectSize = collect.size(); i < collectSize; ++i) {
         Module m1 = collect.get(i);
         Module nextModule = null;
         if (i < collectSize - 1) {
            nextModule = collect.get(i + 1);
         }

         if (m1.isToggled()) {
            m1.anim
               .setEase(Easing.LINEAR)
               .setMin(0.0F)
               .setMax(Aqua.INSTANCE.comfortaa3.getWidth(m1.getName()) + 5.0F)
               .setSpeed(65.0F)
               .setReversed(!m1.isToggled())
               .update();
            m1.anim2.setEase(Easing.LINEAR).setMin(0.0F).setMax(9.0F).setSpeed(25.0F).setReversed(!m1.isToggled()).update();
            float wSet2 = (float)sr.getScaledWidth() - m1.anim.getValue();
            int rainbow = rainbow((int)offset * 9);
            int color = Aqua.setmgr.getSetting("ArraylistReverseFade").isState()
               ? getGradientOffset(new Color(Aqua.setmgr.getSetting("HUDColor").getColor()), new Color(this.getColor2()), (double)index / 12.4).getRGB()
               : getGradientOffset(new Color(this.getColor2()), new Color(Aqua.setmgr.getSetting("HUDColor").getColor()), (double)index / 12.4).getRGB();
            int finalColor = Aqua.setmgr.getSetting("ArraylistFade").isState() ? color : new Color(Aqua.setmgr.getSetting("HUDColor").getColor()).getRGB();
            int doubleFinalColor = Aqua.setmgr.getSetting("ArraylistRainbow").isState() ? rainbow : finalColor;
            if (Aqua.setmgr.getSetting("ArraylistFontShadow").isState()) {
               Aqua.INSTANCE.comfortaa3.drawStringWithShadow(m1.getName(), wSet2 - 3.0F, offset + 5.0F, doubleFinalColor);
            } else {
               Aqua.INSTANCE.comfortaa3.drawString(m1.getName(), wSet2 - 3.0F, offset + 4.0F, doubleFinalColor);
            }

            ++index;
            ++index;
            offset += m1.anim2.getValue() + 5.0F;
            ++offset2;
            GL11.glDisable(3042);
         }
      }
   }

   public void drawRiseBlur() {
      ScaledResolution sr = new ScaledResolution(mc);
      int index = 0;
      float offset2 = 0.0F;
      GL11.glBlendFunc(770, 771);
      float offset = 0.0F;
      List<Module> collect = Aqua.moduleManager
         .modules
         .stream()
         .filter(Module::isToggled)
         .sorted(Comparator.comparingInt(value -> (int)((float)(-sr.getScaledWidth()) - Aqua.INSTANCE.comfortaa3.getWidth(value.getName()))))
         .collect(Collectors.toList());
      int i = 0;

      for(int collectSize = collect.size(); i < collectSize; ++i) {
         Module m1 = collect.get(i);
         Module nextModule = null;
         if (i < collectSize - 1) {
            nextModule = collect.get(i + 1);
         }

         if (m1.isToggled()) {
            m1.anim
               .setEase(Easing.LINEAR)
               .setMin(0.0F)
               .setMax(Aqua.INSTANCE.comfortaa3.getWidth(m1.getName()) + 5.0F)
               .setSpeed(55.0F)
               .setReversed(!m1.isToggled())
               .update();
            m1.anim2.setEase(Easing.LINEAR).setMin(0.0F).setMax(9.0F).setSpeed(55.0F).setReversed(!m1.isToggled()).update();
            float wSet2 = (float)sr.getScaledWidth() - Aqua.INSTANCE.rise.getWidth(m1.getName()) - 3.0F;
            GlStateManager.enableAlpha();
            float finalOffset = offset;
            if (Aqua.moduleManager.getModuleByName("Blur").isToggled()) {
               Blur.drawBlurred(
                  () -> Gui.drawRect2(
                        (double)(wSet2 - 8.0F),
                        (double)finalOffset + 7.2,
                        (double)(sr.getScaledWidth() - 7),
                        (double)(finalOffset + 19.0F),
                        new Color(0, 0, 0, 200).getRGB()
                     ),
                  false
               );
            }

            ++index;
            ++index;
            offset += m1.anim2.getValue() + 3.0F;
            ++offset2;
            GL11.glDisable(3042);
         }
      }
   }

   public void drawRiseArraylist() {
      ScaledResolution sr = new ScaledResolution(mc);
      int index = 0;
      float offset2 = 0.0F;
      GL11.glBlendFunc(770, 771);
      float offset = 0.0F;
      List<Module> collect = Aqua.moduleManager
         .modules
         .stream()
         .filter(Module::isToggled)
         .sorted(Comparator.comparingInt(value -> (int)((float)(-sr.getScaledWidth()) - Aqua.INSTANCE.rise.getWidth(value.getName()))))
         .collect(Collectors.toList());
      int i = 0;

      for(int collectSize = collect.size(); i < collectSize; ++i) {
         Module m1 = collect.get(i);
         Module nextModule = null;
         if (i < collectSize - 1) {
            nextModule = collect.get(i + 1);
         }

         m1.anim
            .setEase(Easing.LINEAR)
            .setMin(0.0F)
            .setMax(Aqua.INSTANCE.rise.getWidth(m1.getName()) + 5.0F)
            .setSpeed(55.0F)
            .setReversed(!m1.isToggled())
            .update();
         m1.anim2.setEase(Easing.LINEAR).setMin(0.0F).setMax(9.0F).setSpeed(55.0F).setReversed(!m1.isToggled()).update();
         float wSet2 = (float)sr.getScaledWidth() - Aqua.INSTANCE.rise.getWidth(m1.getName()) - 3.0F;
         GlStateManager.enableAlpha();
         float finalOffset = offset;
         int rainbow = rainbow((int)offset * 9);
         int color = Aqua.setmgr.getSetting("ArraylistReverseFade").isState()
            ? getGradientOffset(new Color(Aqua.setmgr.getSetting("HUDColor").getColor()), new Color(this.getColor2()), (double)index / 12.4).getRGB()
            : getGradientOffset(new Color(this.getColor2()), new Color(Aqua.setmgr.getSetting("HUDColor").getColor()), (double)index / 12.4).getRGB();
         int finalColor = Aqua.setmgr.getSetting("ArraylistFade").isState() ? color : new Color(Aqua.setmgr.getSetting("HUDColor").getColor()).getRGB();
         int doubleFinalColor = Aqua.setmgr.getSetting("ArraylistRainbow").isState() ? rainbow : finalColor;
         Shadow.drawGlow(
            () -> Gui.drawRect2(
                  (double)(wSet2 - 8.0F),
                  (double)finalOffset + 7.2,
                  (double)(sr.getScaledWidth() - 7),
                  (double)(finalOffset + 19.0F),
                  new Color(0, 0, 0, 200).getRGB()
               ),
            false
         );
         Gui.drawRect2(
            (double)(wSet2 - 8.0F),
            (double)finalOffset + 7.2,
            (double)(sr.getScaledWidth() - 7),
            (double)(finalOffset + 19.0F),
            new Color(0, 0, 0, 55).getRGB()
         );
         Aqua.INSTANCE.rise.drawStringWithShadow(m1.getName(), wSet2 - 6.0F, offset + 8.6F, doubleFinalColor);
         ++index;
         ++index;
         offset += m1.anim2.getValue() + 3.0F;
         ++offset2;
         GL11.glDisable(3042);
      }
   }

   public void drawJelloArraylist() {
      ScaledResolution sr = new ScaledResolution(mc);
      int index = 0;
      float offset2 = 0.0F;
      GL11.glBlendFunc(770, 771);
      float offset = 0.0F;
      List<Module> collect = Aqua.moduleManager
         .modules
         .stream()
         .filter(Module::isToggled)
         .sorted(Comparator.comparingInt(value -> (int)((float)(-sr.getScaledWidth()) - Aqua.INSTANCE.jelloClickguiPanelBottom.getWidth(value.getName()))))
         .collect(Collectors.toList());
      int i = 0;

      for(int collectSize = collect.size(); i < collectSize; ++i) {
         Module m1 = collect.get(i);
         Module nextModule = null;
         if (i < collectSize - 1) {
            nextModule = collect.get(i + 1);
         }

         if (m1.isToggled()) {
            m1.anim
               .setEase(Easing.LINEAR)
               .setMin(0.0F)
               .setMax(Aqua.INSTANCE.jelloClickguiPanelBottom.getWidth(m1.getName()) + 5.0F)
               .setSpeed(55.0F)
               .setReversed(!m1.isToggled())
               .update();
            m1.anim2.setEase(Easing.LINEAR).setMin(0.0F).setMax(9.0F).setSpeed(55.0F).setReversed(!m1.isToggled()).update();
            float wSet2 = (float)sr.getScaledWidth() - m1.anim.getValue();
            GlStateManager.enableAlpha();
            float finalOffset = offset;
            Shadow.drawGlow(
               () -> Aqua.INSTANCE.jelloClickguiPanelBottom.drawString(m1.getName(), wSet2 + 2.0F, finalOffset + 5.0F, Color.gray.getRGB()), false
            );
            Aqua.INSTANCE.jelloClickguiPanelBottom.drawString(m1.getName(), wSet2 + 2.0F, offset + 5.0F, new Color(250, 250, 250).getRGB());
            ++index;
            ++index;
            offset += m1.anim2.getValue() + 3.0F;
            ++offset2;
            GL11.glDisable(3042);
         }
      }
   }

   public void drawRise6Arraylist() {
      ScaledResolution sr = new ScaledResolution(mc);
      int index = 0;
      float offset2 = 0.0F;
      GL11.glBlendFunc(770, 771);
      ByteBuffer pixelBuf = ByteBuffer.allocateDirect(4);
      int listSize = (int)Aqua.moduleManager.modules.stream().filter(Module::isToggled).count();
      List<Module> collect = Aqua.moduleManager
         .modules
         .stream()
         .filter(Module::isToggled)
         .sorted(
            Comparator.comparingInt(
               value -> (int)(
                     (float)(-sr.getScaledWidth())
                        - Aqua.INSTANCE
                           .comfortaa4
                           .getWidth(
                              value.getMode().isEmpty()
                                 ? value.getName()
                                 : String.format("%s%s%s", value.getName(), EnumChatFormatting.WHITE, value.getMode() + "-")
                           )
                  )
            )
         )
         .collect(Collectors.toList());
      int i = 0;

      for(int collectSize = collect.size(); i < collectSize; ++i) {
         Module m = collect.get(i);
         Module nextModule = null;
         if (i < collectSize - 1) {
            nextModule = collect.get(i + 1);
         }

         float wSet = (float)sr.getScaledWidth() - Aqua.INSTANCE.comfortaa4.getWidth(m.getName()) - 3.0F;
         float wSet2 = (float)sr.getScaledWidth() - Aqua.INSTANCE.comfortaa4.getWidth(m.getName()) - 5.0F;
         float wSetNext = (float)sr.getScaledWidth();
         if (nextModule != null) {
            wSetNext = (float)sr.getScaledWidth() - Aqua.INSTANCE.comfortaa4.getWidth(nextModule.getName()) - 5.0F;
         }
      }

      float offset = 0.0F;

      for(Module m : Aqua.setmgr.getSetting("ArraylistSort").isState() ? collect : Aqua.moduleManager.modules) {
         if (m.isToggled()) {
            float wSet = (float)sr.getScaledWidth();
            float wSet2 = (float)sr.getScaledWidth()
               - Aqua.INSTANCE
                  .comfortaa4
                  .getWidth(m.getMode().isEmpty() ? m.getName() : String.format("%s%s%s", m.getName(), EnumChatFormatting.GRAY, m.getMode() + "-"))
               - 7.0F;
            int rainbow = rainbow((int)offset * 9);
            int color = Aqua.setmgr.getSetting("ArraylistReverseFade").isState()
               ? getGradientOffset(new Color(Aqua.setmgr.getSetting("HUDColor").getColor()), new Color(this.getColor2()), (double)index / 12.4).getRGB()
               : getGradientOffset(new Color(this.getColor2()), new Color(Aqua.setmgr.getSetting("HUDColor").getColor()), (double)index / 12.4).getRGB();
            int finalColor = Aqua.setmgr.getSetting("ArraylistFade").isState() ? color : new Color(Aqua.setmgr.getSetting("HUDColor").getColor()).getRGB();
            int doubleFinalColor = Aqua.setmgr.getSetting("ArraylistRainbow").isState() ? rainbow : finalColor;
            Color alphaFinalColor = ColorUtils.getColorAlpha(
               new Color(doubleFinalColor).getRGB(), (int)Aqua.setmgr.getSetting("ArraylistStringAlpha").getCurrentNumber()
            );
            float finalOffset = offset;
            ShaderMultiplier.drawGlowESP(
               () -> Aqua.INSTANCE
                     .comfortaa4
                     .drawString(
                        m.getMode().isEmpty() ? m.getName() : String.format("%s %s%s", m.getName(), EnumChatFormatting.WHITE, m.getMode()),
                        (float)((int)(wSet2 - 5.0F)),
                        (float)((int)(finalOffset + 12.0F)),
                        alphaFinalColor.getRGB()
                     ),
               false
            );
            Aqua.INSTANCE
               .comfortaa4
               .drawString(
                  m.getMode().isEmpty() ? m.getName() : String.format("%s %s%s", m.getName(), EnumChatFormatting.WHITE, m.getMode()),
                  (float)((int)(wSet2 - 5.0F)),
                  (float)((int)(offset + 12.0F)),
                  alphaFinalColor.getRGB()
               );
            ++index;
            offset += 11.0F;
         }
      }

      ++index;
      ++index;
      ++offset2;
      GL11.glDisable(3042);
   }

   public void drawSigmaArraylist() {
      ScaledResolution sr = new ScaledResolution(mc);
      int index = 0;
      float offset2 = 0.0F;
      GL11.glBlendFunc(770, 771);
      ByteBuffer pixelBuf = ByteBuffer.allocateDirect(4);
      int listSize = (int)Aqua.moduleManager.modules.stream().filter(Module::isToggled).count();
      List<Module> collect = Aqua.moduleManager
         .modules
         .stream()
         .filter(Module::isToggled)
         .sorted(
            Comparator.comparingInt(
               value -> (int)(
                     (float)(-sr.getScaledWidth())
                        - Aqua.INSTANCE
                           .sigma
                           .getWidth(
                              value.getMode().isEmpty()
                                 ? value.getName()
                                 : String.format("%s%s%s", value.getName(), EnumChatFormatting.WHITE, value.getMode() + "-")
                           )
                  )
            )
         )
         .collect(Collectors.toList());
      int i = 0;

      for(int collectSize = collect.size(); i < collectSize; ++i) {
         Module m = collect.get(i);
         Module nextModule = null;
         if (i < collectSize - 1) {
            nextModule = collect.get(i + 1);
         }

         float wSet = (float)sr.getScaledWidth() - Aqua.INSTANCE.sigma.getWidth(m.getName()) - 3.0F;
         float wSet2 = (float)sr.getScaledWidth() - Aqua.INSTANCE.sigma.getWidth(m.getName()) - 5.0F;
         float wSetNext = (float)sr.getScaledWidth();
         if (nextModule != null) {
            wSetNext = (float)sr.getScaledWidth() - Aqua.INSTANCE.sigma.getWidth(nextModule.getName()) - 5.0F;
         }
      }

      float offset = 0.0F;

      for(Module m : Aqua.setmgr.getSetting("ArraylistSort").isState() ? collect : Aqua.moduleManager.modules) {
         if (m.isToggled()) {
            float wSet = (float)sr.getScaledWidth();
            float wSet2 = (float)sr.getScaledWidth()
               - Aqua.INSTANCE
                  .sigma
                  .getWidth(m.getMode().isEmpty() ? m.getName() : String.format("%s%s%s", m.getName(), EnumChatFormatting.GRAY, m.getMode() + "-"))
               - 7.0F;
            Aqua.INSTANCE
               .sigma
               .drawString(
                  m.getMode().isEmpty() ? m.getName() : String.format("%s %s%s", m.getName(), EnumChatFormatting.GRAY, m.getMode()),
                  (float)((int)(wSet2 + 6.0F)),
                  (float)((int)(offset + 2.0F)),
                  rainbowSigma((int)(offset * 6.0F))
               );
            offset += 11.0F;
         }
      }

      ++index;
      ++index;
      ++offset2;
      GL11.glDisable(3042);
   }

   public void drawTenacityArraylist() {
      ScaledResolution sr = new ScaledResolution(mc);
      int index = 0;
      float offset2 = 0.0F;
      GL11.glBlendFunc(770, 771);
      ByteBuffer pixelBuf = ByteBuffer.allocateDirect(4);
      int listSize = (int)Aqua.moduleManager.modules.stream().filter(Module::isToggled).count();
      List<Module> collect = Aqua.moduleManager
         .modules
         .stream()
         .filter(Module::isToggled)
         .sorted(
            Comparator.comparingInt(
               value -> (int)(
                     (float)(-sr.getScaledWidth())
                        - Aqua.INSTANCE
                           .tenacityNormal
                           .getWidth(
                              value.getMode().isEmpty()
                                 ? value.getName()
                                 : String.format("%s%s%s", value.getName(), EnumChatFormatting.WHITE, value.getMode() + "-")
                           )
                  )
            )
         )
         .collect(Collectors.toList());
      int i = 0;

      for(int collectSize = collect.size(); i < collectSize; ++i) {
         Module m = collect.get(i);
         Module nextModule = null;
         if (i < collectSize - 1) {
            nextModule = collect.get(i + 1);
         }

         float wSet = (float)sr.getScaledWidth() - Aqua.INSTANCE.tenacityNormal.getWidth(m.getName()) - 3.0F;
         float wSet2 = (float)sr.getScaledWidth() - Aqua.INSTANCE.tenacityNormal.getWidth(m.getName()) - 5.0F;
         float wSetNext = (float)sr.getScaledWidth();
         if (nextModule != null) {
            wSetNext = (float)sr.getScaledWidth() - Aqua.INSTANCE.tenacityNormal.getWidth(nextModule.getName()) - 5.0F;
         }
      }

      float offset = 0.0F;

      for(Module m : Aqua.setmgr.getSetting("ArraylistSort").isState() ? collect : Aqua.moduleManager.modules) {
         if (m.isToggled()) {
            float wSet = (float)sr.getScaledWidth();
            float wSet2 = (float)sr.getScaledWidth()
               - Aqua.INSTANCE
                  .tenacityNormal
                  .getWidth(m.getMode().isEmpty() ? m.getName() : String.format("%s%s%s", m.getName(), EnumChatFormatting.GRAY, m.getMode() + "-"))
               - 7.0F;
            int rainbow = rainbow((int)offset * 9);
            int color = Aqua.setmgr.getSetting("ArraylistReverseFade").isState()
               ? getGradientOffset(new Color(Aqua.setmgr.getSetting("HUDColor").getColor()), new Color(this.getColor2()), (double)index / 12.4).getRGB()
               : getGradientOffset(new Color(this.getColor2()), new Color(Aqua.setmgr.getSetting("HUDColor").getColor()), (double)index / 12.4).getRGB();
            int finalColor = Aqua.setmgr.getSetting("ArraylistFade").isState() ? color : new Color(Aqua.setmgr.getSetting("HUDColor").getColor()).getRGB();
            int doubleFinalColor = Aqua.setmgr.getSetting("ArraylistRainbow").isState() ? rainbow : finalColor;
            Color alphaFinalColor = ColorUtils.getColorAlpha(
               new Color(doubleFinalColor).getRGB(), (int)Aqua.setmgr.getSetting("ArraylistStringAlpha").getCurrentNumber()
            );
            float finalOffset = offset;
            Shadow.drawGlow(
               () -> Gui.drawRect2(
                     (double)(wSet2 + 3.0F), (double)finalOffset, (double)sr.getScaledWidth(), (double)(finalOffset + 11.0F), alphaFinalColor.getRGB()
                  ),
               false
            );
            Gui.drawRect2((double)(wSet2 + 3.0F), (double)offset, (double)sr.getScaledWidth(), (double)(offset + 11.0F), new Color(0, 0, 0, 40).getRGB());
            Aqua.INSTANCE
               .tenacityNormal
               .drawStringWithShadow(
                  m.getMode().isEmpty() ? m.getName() : String.format("%s %s%s", m.getName(), EnumChatFormatting.GRAY, m.getMode()),
                  (float)((int)(wSet2 + 5.0F)),
                  (float)((int)(offset - 1.5F)),
                  alphaFinalColor.getRGB()
               );
            ++index;
            offset += 11.0F;
         }
      }

      ++index;
      ++index;
      ++offset2;
      GL11.glDisable(3042);
   }

   public void drawTenacityArraylistBlur() {
      ScaledResolution sr = new ScaledResolution(mc);
      int index = 0;
      float offset2 = 0.0F;
      GL11.glBlendFunc(770, 771);
      ByteBuffer pixelBuf = ByteBuffer.allocateDirect(4);
      int listSize = (int)Aqua.moduleManager.modules.stream().filter(Module::isToggled).count();
      List<Module> collect = Aqua.moduleManager
         .modules
         .stream()
         .filter(Module::isToggled)
         .sorted(
            Comparator.comparingInt(
               value -> (int)(
                     (float)(-sr.getScaledWidth())
                        - Aqua.INSTANCE
                           .tenacityNormal
                           .getWidth(
                              value.getMode().isEmpty()
                                 ? value.getName()
                                 : String.format("%s%s%s", value.getName(), EnumChatFormatting.WHITE, value.getMode() + "-")
                           )
                  )
            )
         )
         .collect(Collectors.toList());
      int i = 0;

      for(int collectSize = collect.size(); i < collectSize; ++i) {
         Module m = collect.get(i);
         Module nextModule = null;
         if (i < collectSize - 1) {
            nextModule = collect.get(i + 1);
         }

         float wSet = (float)sr.getScaledWidth() - Aqua.INSTANCE.tenacityNormal.getWidth(m.getName()) - 3.0F;
         float wSet2 = (float)sr.getScaledWidth() - Aqua.INSTANCE.tenacityNormal.getWidth(m.getName()) - 5.0F;
         float wSetNext = (float)sr.getScaledWidth();
         if (nextModule != null) {
            wSetNext = (float)sr.getScaledWidth() - Aqua.INSTANCE.tenacityNormal.getWidth(nextModule.getName()) - 5.0F;
         }
      }

      float offset = 0.0F;

      for(Module m : Aqua.setmgr.getSetting("ArraylistSort").isState() ? collect : Aqua.moduleManager.modules) {
         if (m.isToggled()) {
            float wSet = (float)sr.getScaledWidth();
            float wSet2 = (float)sr.getScaledWidth()
               - Aqua.INSTANCE
                  .tenacityNormal
                  .getWidth(m.getMode().isEmpty() ? m.getName() : String.format("%s%s%s", m.getName(), EnumChatFormatting.GRAY, m.getMode() + "-"))
               - 7.0F;
            int rainbow = rainbow((int)offset * 9);
            int color = Aqua.setmgr.getSetting("ArraylistReverseFade").isState()
               ? getGradientOffset(new Color(Aqua.setmgr.getSetting("HUDColor").getColor()), new Color(this.getColor2()), (double)index / 12.4).getRGB()
               : getGradientOffset(new Color(this.getColor2()), new Color(Aqua.setmgr.getSetting("HUDColor").getColor()), (double)index / 12.4).getRGB();
            int finalColor = Aqua.setmgr.getSetting("ArraylistFade").isState() ? color : new Color(Aqua.setmgr.getSetting("HUDColor").getColor()).getRGB();
            int doubleFinalColor = Aqua.setmgr.getSetting("ArraylistRainbow").isState() ? rainbow : finalColor;
            Color alphaFinalColor = ColorUtils.getColorAlpha(
               new Color(doubleFinalColor).getRGB(), (int)Aqua.setmgr.getSetting("ArraylistStringAlpha").getCurrentNumber()
            );
            float finalOffset = offset;
            Blur.drawBlurred(
               () -> Gui.drawRect2(
                     (double)(wSet2 + 3.0F), (double)finalOffset, (double)sr.getScaledWidth(), (double)(finalOffset + 11.0F), alphaFinalColor.getRGB()
                  ),
               false
            );
            ++index;
            offset += 11.0F;
         }
      }

      ++index;
      ++index;
      ++offset2;
      GL11.glDisable(3042);
   }

   public void drawSigmaArraylistBackground() {
      ScaledResolution sr = new ScaledResolution(mc);
      int index = 0;
      float offset2 = 0.0F;
      GL11.glBlendFunc(770, 771);
      ByteBuffer pixelBuf = ByteBuffer.allocateDirect(4);
      int listSize = (int)Aqua.moduleManager.modules.stream().filter(Module::isToggled).count();
      List<Module> collect = Aqua.moduleManager
         .modules
         .stream()
         .filter(Module::isToggled)
         .sorted(
            Comparator.comparingInt(
               value -> (int)(
                     (float)(-sr.getScaledWidth())
                        - Aqua.INSTANCE
                           .sigma
                           .getWidth(
                              value.getMode().isEmpty()
                                 ? value.getName()
                                 : String.format("%s%s%s", value.getName(), EnumChatFormatting.WHITE, value.getMode() + "-")
                           )
                  )
            )
         )
         .collect(Collectors.toList());
      int i = 0;

      for(int collectSize = collect.size(); i < collectSize; ++i) {
         Module m = collect.get(i);
         Module nextModule = null;
         if (i < collectSize - 1) {
            nextModule = collect.get(i + 1);
         }

         float wSet = (float)sr.getScaledWidth() - Aqua.INSTANCE.sigma.getWidth(m.getName()) - 3.0F;
         float wSet2 = (float)sr.getScaledWidth() - Aqua.INSTANCE.sigma.getWidth(m.getName()) - 5.0F;
         float wSetNext = (float)sr.getScaledWidth();
         if (nextModule != null) {
            wSetNext = (float)sr.getScaledWidth() - Aqua.INSTANCE.sigma.getWidth(nextModule.getName()) - 5.0F;
         }
      }

      float offset = 0.0F;

      for(Module m : Aqua.setmgr.getSetting("ArraylistSort").isState() ? collect : Aqua.moduleManager.modules) {
         if (m.isToggled()) {
            float wSet = (float)sr.getScaledWidth();
            float wSet2 = (float)sr.getScaledWidth()
               - Aqua.INSTANCE
                  .sigma
                  .getWidth(m.getMode().isEmpty() ? m.getName() : String.format("%s%s%s", m.getName(), EnumChatFormatting.GRAY, m.getMode() + "-"))
               - 7.0F;
            Gui.drawRect2((double)(wSet2 + 4.0F), (double)offset, (double)sr.getScaledWidth(), (double)(offset + 11.0F), new Color(0, 0, 0, 90).getRGB());
            offset += 11.0F;
         }
      }

      ++index;
      ++index;
      ++offset2;
      GL11.glDisable(3042);
   }

   public void drawXaveArray() {
      ScaledResolution sr = new ScaledResolution(mc);
      int index = 0;
      float offset2 = 0.0F;
      GL11.glBlendFunc(770, 771);
      ByteBuffer pixelBuf = ByteBuffer.allocateDirect(4);
      int listSize = (int)Aqua.moduleManager.modules.stream().filter(Module::isToggled).count();
      List<Module> collect = Aqua.moduleManager
         .modules
         .stream()
         .filter(Module::isToggled)
         .sorted(
            Comparator.comparingInt(
               value -> (int)(
                     (float)(-sr.getScaledWidth())
                        - Aqua.INSTANCE
                           .verdana2
                           .getWidth(
                              value.getMode().isEmpty()
                                 ? value.getName()
                                 : String.format("%s%s%s", value.getName(), EnumChatFormatting.WHITE, value.getMode() + "-")
                           )
                  )
            )
         )
         .collect(Collectors.toList());
      int i = 0;

      for(int collectSize = collect.size(); i < collectSize; ++i) {
         Module m = collect.get(i);
         Module nextModule = null;
         if (i < collectSize - 1) {
            nextModule = collect.get(i + 1);
         }

         float wSet = (float)sr.getScaledWidth() - Aqua.INSTANCE.verdana2.getWidth(m.getName()) - 3.0F;
         float wSet2 = (float)sr.getScaledWidth() - Aqua.INSTANCE.verdana2.getWidth(m.getName()) - 5.0F;
         float wSetNext = (float)sr.getScaledWidth();
         if (nextModule != null) {
            wSetNext = (float)sr.getScaledWidth() - Aqua.INSTANCE.verdana2.getWidth(nextModule.getName()) - 5.0F;
         }
      }

      float offset = 0.0F;

      for(Module m : Aqua.setmgr.getSetting("ArraylistSort").isState() ? collect : Aqua.moduleManager.modules) {
         if (m.isToggled()) {
            float wSet = (float)sr.getScaledWidth();
            float wSet2 = (float)sr.getScaledWidth()
               - Aqua.INSTANCE
                  .verdana2
                  .getWidth(m.getMode().isEmpty() ? m.getName() : String.format("%s%s%s", m.getName(), EnumChatFormatting.GRAY, m.getMode() + "-"))
               - 7.0F;
            Gui.drawRect2((double)(wSet2 + 4.0F), (double)offset, (double)sr.getScaledWidth(), (double)(offset + 11.0F), new Color(40, 40, 40, 100).getRGB());
            Aqua.INSTANCE
               .verdana2
               .drawStringWithShadow(
                  m.getMode().isEmpty() ? m.getName() : String.format("%s %s%s", m.getName(), EnumChatFormatting.GRAY, m.getMode()),
                  (float)((int)(wSet2 + 6.0F)),
                  (float)((int)offset),
                  m.oneTimeColor3
               );
            offset += 11.0F;
         }
      }

      ++index;
      ++index;
      ++offset2;
      GL11.glDisable(3042);
   }

   public void drawHeroArraylist() {
      ScaledResolution sr = new ScaledResolution(mc);
      int index = 0;
      float offset2 = 0.0F;
      GL11.glBlendFunc(770, 771);
      float offset = 0.0F;
      List<Module> collect = Aqua.setmgr.getSetting("ArraylistSort").isState()
         ? Aqua.moduleManager
            .modules
            .stream()
            .filter(Module::isToggled)
            .sorted(Comparator.comparingInt(value -> (int)((float)(-sr.getScaledWidth()) - Aqua.INSTANCE.roboto2.getWidth(value.getName()))))
            .collect(Collectors.toList())
         : Aqua.moduleManager.modules;
      int i = 0;

      for(int collectSize = collect.size(); i < collectSize; ++i) {
         Module m1 = collect.get(i);
         Module nextModule = null;
         if (i < collectSize - 1) {
            nextModule = collect.get(i + 1);
         }

         if (m1.isToggled()) {
            m1.anim
               .setEase(Easing.LINEAR)
               .setMin(0.0F)
               .setMax(Aqua.INSTANCE.roboto2.getWidth(m1.getName()) + 5.0F)
               .setSpeed(25.0F)
               .setReversed(!m1.isToggled())
               .update();
            m1.anim2.setEase(Easing.LINEAR).setMin(0.0F).setMax(9.0F).setSpeed(25.0F).setReversed(!m1.isToggled()).update();
            float wSet2 = (float)sr.getScaledWidth() - m1.anim.getValue();
            GlStateManager.enableAlpha();
            Gui.drawRect((int)((float)sr.getScaledWidth() - 2.0F), (int)offset, sr.getScaledWidth() + 2, (int)(offset + 11.0F), m1.heroColor);
            Gui.drawRect((int)wSet2, (int)offset, sr.getScaledWidth() + 2, (int)(offset + 11.0F), new Color(12, 12, 12, 130).getRGB());
            Aqua.INSTANCE.roboto2.drawString(m1.getName(), wSet2 + 1.0F, offset - 1.5F, m1.heroColor);
            Gui.drawRect2((double)wSet2, (double)offset, (double)(wSet2 - 1.0F), (double)(offset + 11.0F), new Color(12, 12, 12, 170).getRGB());
            ++index;
            ++index;
            offset += m1.anim2.getValue() + 2.0F;
            ++offset2;
            GL11.glDisable(3042);
         }
      }
   }

   public void drawRects() {
      ScaledResolution sr = new ScaledResolution(mc);
      int index = 0;
      float offset2 = 0.0F;
      GL11.glBlendFunc(770, 771);
      int listSize = (int)Aqua.moduleManager.modules.stream().filter(Module::isToggled).count();
      List<Module> collect = Aqua.moduleManager.modules.stream().filter(Module::isToggled).collect(Collectors.toList());
      int i = 0;

      for(int collectSize = collect.size(); i < collectSize; ++i) {
         Module m = collect.get(i);
         Module nextModule = null;
         if (i < collectSize - 1) {
            nextModule = collect.get(i + 1);
         }

         float wSet2 = (float)sr.getScaledWidth() - Aqua.INSTANCE.comfortaa3.getWidth(m.getName()) - 5.0F;
         float wSetNext = (float)sr.getScaledWidth();
         if (nextModule != null) {
            wSetNext = (float)sr.getScaledWidth() - Aqua.INSTANCE.comfortaa3.getWidth(nextModule.getName()) - 5.0F;
         }

         float finalOffset = offset2;
         drawGlowArray(
            () -> Gui.drawRect(
                  (int)((float)sr.getScaledWidth() - 5.0F),
                  (int)(finalOffset + 4.0F),
                  sr.getScaledWidth() - 3,
                  (int)(finalOffset + 15.0F),
                  Aqua.setmgr.getSetting("HUDColor").getColor()
               ),
            false
         );
         Gui.drawRect(
            (int)((float)sr.getScaledWidth() - 5.0F),
            (int)(offset2 + 4.0F),
            sr.getScaledWidth() - 3,
            (int)(offset2 + 15.0F),
            Aqua.setmgr.getSetting("HUDColor").getColor()
         );
         ++index;
         offset2 += 11.0F;
      }

      GL11.glDisable(3042);
   }

   public static void drawRectsSuffix() {
      ScaledResolution sr = new ScaledResolution(mc);
      int index = 0;
      float offset2 = 0.0F;
      GL11.glBlendFunc(770, 771);
      List<Module> collect = Aqua.moduleManager
         .modules
         .stream()
         .filter(Module::isToggled)
         .sorted(
            Comparator.comparingInt(
               value -> (int)(
                     (float)(-sr.getScaledWidth())
                        - Aqua.INSTANCE
                           .comfortaa3
                           .getWidth(
                              value.getMode().isEmpty()
                                 ? value.getName()
                                 : String.format("%s%s%s", value.getName(), EnumChatFormatting.WHITE, value.getMode() + "-")
                           )
                  )
            )
         )
         .collect(Collectors.toList());
      int listSize = collect.size();
      int i = 0;

      for(int collectSize = collect.size(); i < collectSize; ++i) {
         Module m = collect.get(i);
         Module nextModule = null;
         if (i < collectSize - 1) {
            nextModule = collect.get(i + 1);
         }

         float wSet = (float)sr.getScaledWidth() - Aqua.INSTANCE.comfortaa3.getWidth(m.getName()) - 3.0F;
         float wSet2 = (float)sr.getScaledWidth()
            - Aqua.INSTANCE
               .comfortaa3
               .getWidth(m.getMode().isEmpty() ? m.getName() : String.format("%s%s%s", m.getName(), EnumChatFormatting.GRAY, m.getMode() + "-"))
            - 7.0F;
         float wSetNext = (float)sr.getScaledWidth();
         if (nextModule != null) {
            wSetNext = (float)sr.getScaledWidth() - Aqua.INSTANCE.comfortaa3.getWidth(nextModule.getName()) - 5.0F;
         }

         float finalWSetNext1 = wSetNext;
         int finalIndex1 = index;
         float finalOffset3 = offset2;
         int rainbow = 0;
         int color = Aqua.setmgr.getSetting("ArraylistReverseFade").isState()
            ? getGradientOffset(
                  new Color(Aqua.setmgr.getSetting("HUDColor").getColor()),
                  new Color(Aqua.setmgr.getSetting("ArraylistColor").getColor()),
                  (double)index / 12.4
               )
               .getRGB()
            : getGradientOffset(
                  new Color(Aqua.setmgr.getSetting("ArraylistColor").getColor()),
                  new Color(Aqua.setmgr.getSetting("HUDColor").getColor()),
                  (double)index / 12.4
               )
               .getRGB();
         int finalColor = Aqua.setmgr.getSetting("ArraylistFade").isState() ? color : new Color(Aqua.setmgr.getSetting("HUDColor").getColor()).getRGB();
         int doubleFinalColor = Aqua.setmgr.getSetting("ArraylistRainbow").isState() ? rainbow : finalColor;
         Color alphaFinalColor = ColorUtils.getColorAlpha(
            new Color(doubleFinalColor).getRGB(), (int)Aqua.setmgr.getSetting("ArraylistStringAlpha").getCurrentNumber()
         );
         ShaderMultiplier.drawGlowESP(
            () -> RenderUtil.drawRoundedRect3(
                  (double)(wSet2 - 2.0F),
                  (double)(finalOffset3 + 5.0F),
                  (double)((float)sr.getScaledWidth() - wSet2 - 2.0F),
                  11.0,
                  (double)Math.min(3.0F, finalWSetNext1 - wSet2),
                  finalIndex1 == 0,
                  finalIndex1 == 0,
                  finalIndex1 == listSize,
                  true,
                  new Color(20, 20, 20, 230)
               ),
            false
         );
         RenderUtil.drawRoundedRect3(
            (double)(wSet2 - 2.0F),
            (double)(finalOffset3 + 5.0F),
            (double)((float)sr.getScaledWidth() - wSet2 - 2.0F),
            11.0,
            (double)Math.min(3.0F, finalWSetNext1 - wSet2),
            finalIndex1 == 0,
            finalIndex1 == 0,
            finalIndex1 == listSize,
            true,
            new Color(20, 20, 20, 70)
         );
         ++index;
         offset2 += 11.0F;
      }

      float offset = 0.0F;

      for(Module m : Aqua.moduleManager.modules) {
         if (m.isToggled()) {
            float wSet = (float)sr.getScaledWidth();
            float wSet2 = (float)sr.getScaledWidth() - Aqua.INSTANCE.comfortaa3.getWidth(m.getName()) - 5.0F;
            ++index;
            ++index;
            ++offset;
            ++offset2;
         }
      }

      GL11.glDisable(3042);
   }

   public static void drawRectsSuffix2() {
      ScaledResolution sr = new ScaledResolution(mc);
      int index = 0;
      float offset2 = 0.0F;
      GL11.glBlendFunc(770, 771);
      List<Module> collect = Aqua.moduleManager
         .modules
         .stream()
         .filter(Module::isToggled)
         .sorted(
            Comparator.comparingInt(
               value -> (int)(
                     (float)(-sr.getScaledWidth())
                        - Aqua.INSTANCE
                           .comfortaa3
                           .getWidth(
                              value.getMode().isEmpty()
                                 ? value.getName()
                                 : String.format("%s%s%s", value.getName(), EnumChatFormatting.WHITE, value.getMode() + "-")
                           )
                  )
            )
         )
         .collect(Collectors.toList());
      int listSize = collect.size();
      int i = 0;

      for(int collectSize = collect.size(); i < collectSize; ++i) {
         Module m = collect.get(i);
         Module nextModule = null;
         if (i < collectSize - 1) {
            nextModule = collect.get(i + 1);
         }

         float wSet = (float)sr.getScaledWidth() - Aqua.INSTANCE.comfortaa3.getWidth(m.getName()) - 3.0F;
         float wSet2 = (float)sr.getScaledWidth()
            - Aqua.INSTANCE
               .comfortaa3
               .getWidth(m.getMode().isEmpty() ? m.getName() : String.format("%s%s%s", m.getName(), EnumChatFormatting.GRAY, m.getMode() + "-"))
            - 7.0F;
         float wSetNext = (float)sr.getScaledWidth();
         if (nextModule != null) {
            wSetNext = (float)sr.getScaledWidth() - Aqua.INSTANCE.comfortaa3.getWidth(nextModule.getName()) - 5.0F;
         }

         float finalWSetNext1 = wSetNext;
         int finalIndex1 = index;
         float finalOffset3 = offset2;
         int rainbow = 0;
         Blur.drawBlurred(
            () -> RenderUtil.drawRoundedRect3(
                  (double)(wSet2 - 2.0F),
                  (double)(finalOffset3 + 5.0F),
                  (double)((float)sr.getScaledWidth() - wSet2 - 2.0F),
                  11.0,
                  (double)Math.min(3.0F, finalWSetNext1 - wSet2),
                  finalIndex1 == 0,
                  finalIndex1 == 0,
                  finalIndex1 == listSize,
                  true,
                  Color.black
               ),
            false
         );
         ++index;
         offset2 += 11.0F;
      }

      float offset = 0.0F;

      for(Module m : Aqua.moduleManager.modules) {
         if (m.isToggled()) {
            float wSet = (float)sr.getScaledWidth();
            float wSet2 = (float)sr.getScaledWidth() - Aqua.INSTANCE.comfortaa3.getWidth(m.getName()) - 5.0F;
            ++index;
            ++index;
            ++offset;
            ++offset2;
         }
      }

      GL11.glDisable(3042);
   }

   public void drawStringsSuffix() {
      ScaledResolution sr = new ScaledResolution(mc);
      int index = 0;
      float offset2 = 0.0F;
      GL11.glBlendFunc(770, 771);
      ByteBuffer pixelBuf = ByteBuffer.allocateDirect(4);
      int listSize = (int)Aqua.moduleManager.modules.stream().filter(Module::isToggled).count();
      List<Module> collect = Aqua.moduleManager
         .modules
         .stream()
         .filter(Module::isToggled)
         .sorted(
            Comparator.comparingInt(
               value -> (int)(
                     (float)(-sr.getScaledWidth())
                        - Aqua.INSTANCE
                           .comfortaa3
                           .getWidth(
                              value.getMode().isEmpty()
                                 ? value.getName()
                                 : String.format("%s%s%s", value.getName(), EnumChatFormatting.WHITE, value.getMode() + "-")
                           )
                  )
            )
         )
         .collect(Collectors.toList());
      int i = 0;

      for(int collectSize = collect.size(); i < collectSize; ++i) {
         Module m = collect.get(i);
         Module nextModule = null;
         if (i < collectSize - 1) {
            nextModule = collect.get(i + 1);
         }

         float wSet = (float)sr.getScaledWidth() - Aqua.INSTANCE.comfortaa3.getWidth(m.getName()) - 3.0F;
         float wSet2 = (float)sr.getScaledWidth() - Aqua.INSTANCE.comfortaa3.getWidth(m.getName()) - 5.0F;
         float wSetNext = (float)sr.getScaledWidth();
         if (nextModule != null) {
            wSetNext = (float)sr.getScaledWidth() - Aqua.INSTANCE.comfortaa3.getWidth(nextModule.getName()) - 5.0F;
         }
      }

      float offset = 0.0F;

      for(Module m : collect) {
         if (m.isToggled()) {
            float wSet = (float)sr.getScaledWidth();
            float wSet2 = (float)sr.getScaledWidth()
               - Aqua.INSTANCE
                  .comfortaa3
                  .getWidth(m.getMode().isEmpty() ? m.getName() : String.format("%s%s%s", m.getName(), EnumChatFormatting.GRAY, m.getMode() + "-"))
               - 7.0F;
            int rainbow = rainbow((int)offset * 9);
            int color = Aqua.setmgr.getSetting("ArraylistReverseFade").isState()
               ? getGradientOffset(new Color(Aqua.setmgr.getSetting("HUDColor").getColor()), new Color(this.getColor2()), (double)index / 12.4).getRGB()
               : getGradientOffset(new Color(this.getColor2()), new Color(Aqua.setmgr.getSetting("HUDColor").getColor()), (double)index / 12.4).getRGB();
            int finalColor = Aqua.setmgr.getSetting("ArraylistFade").isState() ? color : new Color(Aqua.setmgr.getSetting("HUDColor").getColor()).getRGB();
            int doubleFinalColor = Aqua.setmgr.getSetting("ArraylistRainbow").isState() ? rainbow : finalColor;
            Color alphaFinalColor = ColorUtils.getColorAlpha(
               new Color(doubleFinalColor).getRGB(), (int)Aqua.setmgr.getSetting("ArraylistStringAlpha").getCurrentNumber()
            );
            float finalOffset = offset;
            Shadow.drawGlow(
               () -> Aqua.INSTANCE
                     .comfortaa3
                     .drawString(
                        m.getMode().isEmpty() ? m.getName() : String.format("%s %s%s", m.getName(), EnumChatFormatting.GRAY, m.getMode()),
                        wSet2 + 0.5F,
                        finalOffset + 5.0F,
                        alphaFinalColor.getRGB()
                     ),
               false
            );
            Aqua.INSTANCE
               .comfortaa3
               .drawString(
                  m.getMode().isEmpty() ? m.getName() : String.format("%s %s%s", m.getName(), EnumChatFormatting.GRAY, m.getMode()),
                  wSet2 + 0.5F,
                  offset + 5.0F,
                  alphaFinalColor.getRGB()
               );
            offset += 11.0F;
            ++index;
         }
      }

      ++index;
      ++offset2;
      GL11.glDisable(3042);
   }

   private void drawTexturedQuad1(int texture, double width, double height) {
      GlStateManager.enableBlend();
      GL11.glBindTexture(3553, texture);
      GL11.glBegin(7);
      GL11.glTexCoord2d(0.0, 1.0);
      GL11.glVertex2d(0.0, 0.0);
      GL11.glTexCoord2d(0.0, 0.0);
      GL11.glVertex2d(0.0, height);
      GL11.glTexCoord2d(1.0, 0.0);
      GL11.glVertex2d(width, height);
      GL11.glTexCoord2d(1.0, 1.0);
      GL11.glVertex2d(width, 0.0);
      GL11.glEnd();
   }

   public static Color getGradientOffset(Color color1, Color color2, double index) {
      double offs = (double)Math.abs(System.currentTimeMillis() / 13L) / 60.0 + index;
      if (offs > 1.0) {
         double left = offs % 1.0;
         int off = (int)offs;
         offs = off % 2 == 0 ? left : 1.0 - left;
      }

      double inverse_percent = 1.0 - offs;
      int redPart = (int)((double)color1.getRed() * inverse_percent + (double)color2.getRed() * offs);
      int greenPart = (int)((double)color1.getGreen() * inverse_percent + (double)color2.getGreen() * offs);
      int bluePart = (int)((double)color1.getBlue() * inverse_percent + (double)color2.getBlue() * offs);
      return new Color(redPart, greenPart, bluePart);
   }

   public int getColor2() {
      try {
         return Aqua.setmgr.getSetting("ArraylistColor").getColor();
      } catch (Exception var2) {
         return Color.white.getRGB();
      }
   }

   public static int rainbow(int delay) {
      double rainbowState = Math.ceil((double)((System.currentTimeMillis() + (long)delay) / 7L));
      rainbowState %= 360.0;
      return Color.getHSBColor((float)(rainbowState / 360.0), 0.9F, 1.0F).getRGB();
   }

   public static int rainbowSigma(int delay) {
      double rainbowState = Math.ceil((double)((System.currentTimeMillis() + (long)delay) / 9L));
      rainbowState %= 360.0;
      return Color.getHSBColor((float)(rainbowState / 360.0), 0.5F, 1.0F).getRGB();
   }
}
