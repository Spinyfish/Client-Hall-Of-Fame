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
import intent.AquaDev.aqua.utils.RenderUtil;
import java.awt.Color;
import java.nio.ByteBuffer;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.opengl.GL11;

public class Arraylist2 extends Module {
   public Arraylist2() {
      super("Arraylist", Module.Type.Visual, "Arraylist", 0, Category.Visual);
      Aqua.setmgr.register(new Setting("Fade", this, true));
      Aqua.setmgr.register(new Setting("Rainbow", this, false));
      Aqua.setmgr.register(new Setting("Bloom", this, true));
      Aqua.setmgr.register(new Setting("Sort", this, true));
      Aqua.setmgr.register(new Setting("Background", this, true));
      Aqua.setmgr.register(new Setting("BloomMode", this, "Glow", new String[]{"Glow", "Shadow"}));
      Aqua.setmgr.register(new Setting("SuffixMode", this, "-", new String[]{"-", "<>"}));
      Aqua.setmgr.register(new Setting("Fonts", this, "Arial", new String[]{"Comforta", "Arial", "Tenacity", "MC"}));
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
      }

      if (e instanceof EventRender2D) {
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
            String var2 = Aqua.setmgr.getSetting("ArraylistFonts").getCurrentMode();
            switch(var2) {
               case "Arial":
                  ScaledResolution sr = new ScaledResolution(mc);
                  int index = 0;
                  float offset2 = 0.0F;
                  GL11.glBlendFunc(770, 771);
                  ByteBuffer pixelBuf = ByteBuffer.allocateDirect(4);
                  List<Module> collect = Aqua.setmgr.getSetting("ArraylistSuffixMode").getCurrentMode().equalsIgnoreCase("-")
                     ? Aqua.moduleManager
                        .modules
                        .stream()
                        .filter(Module::isToggled)
                        .sorted(
                           Comparator.comparingInt(
                              value -> (int)(
                                    (float)(-sr.getScaledWidth())
                                       - Aqua.INSTANCE
                                          .novoline
                                          .getWidth(
                                             value.getMode().isEmpty()
                                                ? value.getName()
                                                : String.format("%s %s- %s", value.getName(), EnumChatFormatting.WHITE, value.getMode() + "")
                                          )
                                 )
                           )
                        )
                        .collect(Collectors.toList())
                     : Aqua.moduleManager
                        .modules
                        .stream()
                        .filter(Module::isToggled)
                        .sorted(
                           Comparator.comparingInt(
                              value -> (int)(
                                    (float)(-sr.getScaledWidth())
                                       - Aqua.INSTANCE
                                          .novoline
                                          .getWidth(
                                             value.getMode().isEmpty()
                                                ? value.getName()
                                                : String.format("%s %s<%s>", value.getName(), EnumChatFormatting.WHITE, value.getMode() + "")
                                          )
                                 )
                           )
                        )
                        .collect(Collectors.toList());
                  int i = 0;
                  int collectSize = collect.size();

                  for(; i < collectSize; ++i) {
                     Module m = collect.get(i);
                     Module nextModule = null;
                     if (i < collectSize - 1) {
                        nextModule = collect.get(i + 1);
                     }

                     float wSetNext = (float)sr.getScaledWidth();
                     if (nextModule != null) {
                        wSetNext = (float)sr.getScaledWidth() - Aqua.INSTANCE.novoline.getWidth(nextModule.getName()) - 5.0F;
                     }
                  }

                  float offset = 0.0F;

                  for(Module m : collect) {
                     if (m.isToggled()) {
                        float wSet2 = Aqua.setmgr.getSetting("ArraylistSuffixMode").getCurrentMode().equalsIgnoreCase("-")
                           ? (float)sr.getScaledWidth()
                              - Aqua.INSTANCE
                                 .novoline
                                 .getWidth(
                                    m.getMode().isEmpty() ? m.getName() : String.format("%s %s- %s", m.getName(), EnumChatFormatting.GRAY, m.getMode() + "")
                                 )
                           : (float)sr.getScaledWidth()
                              - Aqua.INSTANCE
                                 .novoline
                                 .getWidth(
                                    m.getMode().isEmpty() ? m.getName() : String.format("%s %s<%s>", m.getName(), EnumChatFormatting.GRAY, m.getMode() + "")
                                 );
                        if (Aqua.setmgr.getSetting("ArraylistBackground").isState()) {
                           Gui.drawRect2(
                              (double)(wSet2 - 5.0F), (double)offset, (double)sr.getScaledWidth(), (double)(offset + 11.0F), new Color(0, 0, 0, 90).getRGB()
                           );
                        }

                        int rainbow = Arraylist.rainbow((int)offset * 9);
                        int color = Arraylist.getGradientOffset(
                              new Color(Aqua.setmgr.getSetting("HUDColor").getColor()), new Color(this.getArraylistColor()), (double)index / 12.4
                           )
                           .getRGB();
                        int finalColor = Aqua.setmgr.getSetting("ArraylistFade").isState()
                           ? color
                           : new Color(Aqua.setmgr.getSetting("HUDColor").getColor()).getRGB();
                        int doubleFinalColor = Aqua.setmgr.getSetting("ArraylistRainbow").isState() ? rainbow : finalColor;
                        if (Aqua.setmgr.getSetting("ArraylistBloom").isState()) {
                           String var89 = Aqua.setmgr.getSetting("ArraylistBloomMode").getCurrentMode();
                           switch(var89) {
                              case "Glow":
                                 if (Aqua.moduleManager.getModuleByName("ShaderMultiplier").isToggled()) {
                                    ShaderMultiplier.drawGlowESP(
                                       () -> Gui.drawRect2(
                                             (double)(wSet2 - 5.0F), (double)offset, (double)sr.getScaledWidth(), (double)(offset + 11.0F), doubleFinalColor
                                          ),
                                       false
                                    );
                                 }
                                 break;
                              case "Shadow":
                                 if (Aqua.moduleManager.getModuleByName("Shadow").isToggled()) {
                                    Shadow.drawGlow(
                                       () -> Gui.drawRect2(
                                             (double)(wSet2 - 5.0F),
                                             (double)offset,
                                             (double)sr.getScaledWidth(),
                                             (double)(offset + 11.0F),
                                             new Color(0, 0, 0, 255).getRGB()
                                          ),
                                       false
                                    );
                                 }
                           }
                        }

                        String var90 = Aqua.setmgr.getSetting("ArraylistSuffixMode").getCurrentMode();
                        switch(var90) {
                           case "-":
                              Aqua.INSTANCE
                                 .novoline
                                 .drawString(
                                    m.getMode().isEmpty() ? m.getName() : String.format("%s %s- %s", m.getName(), EnumChatFormatting.GRAY, m.getMode()),
                                    wSet2 - 3.0F,
                                    offset,
                                    doubleFinalColor
                                 );
                              break;
                           case "<>":
                              Aqua.INSTANCE
                                 .novoline
                                 .drawString(
                                    m.getMode().isEmpty() ? m.getName() : String.format("%s %s<%s>", m.getName(), EnumChatFormatting.GRAY, m.getMode()),
                                    wSet2 - 3.0F,
                                    offset,
                                    doubleFinalColor
                                 );
                        }

                        ++index;
                        offset += 11.0F;
                     }
                  }
                  break;
               case "Comforta":
                  ScaledResolution sr = new ScaledResolution(mc);
                  int index = 0;
                  float offset2 = 0.0F;
                  GL11.glBlendFunc(770, 771);
                  ByteBuffer pixelBuf = ByteBuffer.allocateDirect(4);
                  List<Module> collect = Aqua.setmgr.getSetting("ArraylistSuffixMode").getCurrentMode().equalsIgnoreCase("-")
                     ? Aqua.moduleManager
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
                                                : String.format("%s %s- %s", value.getName(), EnumChatFormatting.WHITE, value.getMode() + "")
                                          )
                                 )
                           )
                        )
                        .collect(Collectors.toList())
                     : Aqua.moduleManager
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
                                                : String.format("%s %s<%s>", value.getName(), EnumChatFormatting.WHITE, value.getMode() + "")
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

                     float wSetNext = (float)sr.getScaledWidth();
                     if (nextModule != null) {
                        wSetNext = (float)sr.getScaledWidth() - Aqua.INSTANCE.novoline.getWidth(nextModule.getName()) - 5.0F;
                     }
                  }

                  float offset = 0.0F;

                  for(Module m : collect) {
                     if (m.isToggled()) {
                        float wSet2 = Aqua.setmgr.getSetting("ArraylistSuffixMode").getCurrentMode().equalsIgnoreCase("-")
                           ? (float)sr.getScaledWidth()
                              - Aqua.INSTANCE
                                 .comfortaa3
                                 .getWidth(
                                    m.getMode().isEmpty() ? m.getName() : String.format("%s %s- %s", m.getName(), EnumChatFormatting.GRAY, m.getMode() + "")
                                 )
                           : (float)sr.getScaledWidth()
                              - Aqua.INSTANCE
                                 .comfortaa3
                                 .getWidth(
                                    m.getMode().isEmpty() ? m.getName() : String.format("%s %s<%s>", m.getName(), EnumChatFormatting.GRAY, m.getMode() + "")
                                 );
                        if (Aqua.setmgr.getSetting("ArraylistBackground").isState()) {
                           Gui.drawRect2(
                              (double)(wSet2 - 5.0F), (double)offset, (double)sr.getScaledWidth(), (double)(offset + 11.0F), new Color(0, 0, 0, 90).getRGB()
                           );
                        }

                        int rainbow = Arraylist.rainbow((int)offset * 9);
                        int color = Arraylist.getGradientOffset(
                              new Color(Aqua.setmgr.getSetting("HUDColor").getColor()), new Color(this.getArraylistColor()), (double)index / 12.4
                           )
                           .getRGB();
                        int finalColor = Aqua.setmgr.getSetting("ArraylistFade").isState()
                           ? color
                           : new Color(Aqua.setmgr.getSetting("HUDColor").getColor()).getRGB();
                        int doubleFinalColor = Aqua.setmgr.getSetting("ArraylistRainbow").isState() ? rainbow : finalColor;
                        if (Aqua.setmgr.getSetting("ArraylistBloom").isState()) {
                           String var87 = Aqua.setmgr.getSetting("ArraylistBloomMode").getCurrentMode();
                           switch(var87) {
                              case "Glow":
                                 if (Aqua.moduleManager.getModuleByName("ShaderMultiplier").isToggled()) {
                                    ShaderMultiplier.drawGlowESP(
                                       () -> Gui.drawRect2(
                                             (double)(wSet2 - 5.0F), (double)offset, (double)sr.getScaledWidth(), (double)(offset + 11.0F), doubleFinalColor
                                          ),
                                       false
                                    );
                                 }
                                 break;
                              case "Shadow":
                                 if (Aqua.moduleManager.getModuleByName("Shadow").isToggled()) {
                                    Shadow.drawGlow(
                                       () -> Gui.drawRect2(
                                             (double)(wSet2 - 5.0F),
                                             (double)offset,
                                             (double)sr.getScaledWidth(),
                                             (double)(offset + 11.0F),
                                             new Color(0, 0, 0, 255).getRGB()
                                          ),
                                       false
                                    );
                                 }
                           }
                        }

                        String var88 = Aqua.setmgr.getSetting("ArraylistSuffixMode").getCurrentMode();
                        switch(var88) {
                           case "-":
                              Aqua.INSTANCE
                                 .comfortaa3
                                 .drawString(
                                    m.getMode().isEmpty() ? m.getName() : String.format("%s %s- %s", m.getName(), EnumChatFormatting.GRAY, m.getMode()),
                                    wSet2 - 3.0F,
                                    offset,
                                    doubleFinalColor
                                 );
                              break;
                           case "<>":
                              Aqua.INSTANCE
                                 .comfortaa3
                                 .drawString(
                                    m.getMode().isEmpty() ? m.getName() : String.format("%s %s<%s>", m.getName(), EnumChatFormatting.GRAY, m.getMode()),
                                    wSet2 - 3.0F,
                                    offset,
                                    doubleFinalColor
                                 );
                        }

                        ++index;
                        offset += 11.0F;
                     }
                  }
                  break;
               case "Tenacity":
                  ScaledResolution sr = new ScaledResolution(mc);
                  int index = 0;
                  float offset2 = 0.0F;
                  GL11.glBlendFunc(770, 771);
                  ByteBuffer pixelBuf = ByteBuffer.allocateDirect(4);
                  List<Module> collect = Aqua.setmgr.getSetting("ArraylistSuffixMode").getCurrentMode().equalsIgnoreCase("-")
                     ? Aqua.moduleManager
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
                                                : String.format("%s %s- %s", value.getName(), EnumChatFormatting.WHITE, value.getMode() + "")
                                          )
                                 )
                           )
                        )
                        .collect(Collectors.toList())
                     : Aqua.moduleManager
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
                                                : String.format("%s %s<%s>", value.getName(), EnumChatFormatting.WHITE, value.getMode() + "")
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

                     float wSetNext = (float)sr.getScaledWidth();
                     if (nextModule != null) {
                        wSetNext = (float)sr.getScaledWidth() - Aqua.INSTANCE.tenacityNormal.getWidth(nextModule.getName()) - 5.0F;
                     }
                  }

                  float offset = 0.0F;

                  for(Module m : collect) {
                     if (m.isToggled()) {
                        float wSet2 = Aqua.setmgr.getSetting("ArraylistSuffixMode").getCurrentMode().equalsIgnoreCase("-")
                           ? (float)sr.getScaledWidth()
                              - Aqua.INSTANCE
                                 .tenacityNormal
                                 .getWidth(
                                    m.getMode().isEmpty() ? m.getName() : String.format("%s %s- %s", m.getName(), EnumChatFormatting.GRAY, m.getMode() + "")
                                 )
                           : (float)sr.getScaledWidth()
                              - Aqua.INSTANCE
                                 .tenacityNormal
                                 .getWidth(
                                    m.getMode().isEmpty() ? m.getName() : String.format("%s %s<%s>", m.getName(), EnumChatFormatting.GRAY, m.getMode() + "")
                                 );
                        if (Aqua.setmgr.getSetting("ArraylistBackground").isState()) {
                           Gui.drawRect2(
                              (double)(wSet2 - 5.0F), (double)offset, (double)sr.getScaledWidth(), (double)(offset + 11.0F), new Color(0, 0, 0, 90).getRGB()
                           );
                        }

                        int rainbow = Arraylist.rainbow((int)offset * 9);
                        int color = Arraylist.getGradientOffset(
                              new Color(Aqua.setmgr.getSetting("HUDColor").getColor()), new Color(this.getArraylistColor()), (double)index / 12.4
                           )
                           .getRGB();
                        int finalColor = Aqua.setmgr.getSetting("ArraylistFade").isState()
                           ? color
                           : new Color(Aqua.setmgr.getSetting("HUDColor").getColor()).getRGB();
                        int doubleFinalColor = Aqua.setmgr.getSetting("ArraylistRainbow").isState() ? rainbow : finalColor;
                        if (Aqua.setmgr.getSetting("ArraylistBloom").isState()) {
                           String var85 = Aqua.setmgr.getSetting("ArraylistBloomMode").getCurrentMode();
                           switch(var85) {
                              case "Glow":
                                 if (Aqua.moduleManager.getModuleByName("ShaderMultiplier").isToggled()) {
                                    ShaderMultiplier.drawGlowESP(
                                       () -> Gui.drawRect2(
                                             (double)(wSet2 - 5.0F), (double)offset, (double)sr.getScaledWidth(), (double)(offset + 11.0F), doubleFinalColor
                                          ),
                                       false
                                    );
                                 }
                                 break;
                              case "Shadow":
                                 if (Aqua.moduleManager.getModuleByName("Shadow").isToggled()) {
                                    Shadow.drawGlow(
                                       () -> Gui.drawRect2(
                                             (double)(wSet2 - 5.0F),
                                             (double)offset,
                                             (double)sr.getScaledWidth(),
                                             (double)(offset + 11.0F),
                                             new Color(0, 0, 0, 255).getRGB()
                                          ),
                                       false
                                    );
                                 }
                           }
                        }

                        String var86 = Aqua.setmgr.getSetting("ArraylistSuffixMode").getCurrentMode();
                        switch(var86) {
                           case "-":
                              Aqua.INSTANCE
                                 .tenacityNormal
                                 .drawString(
                                    m.getMode().isEmpty() ? m.getName() : String.format("%s %s- %s", m.getName(), EnumChatFormatting.GRAY, m.getMode()),
                                    wSet2 - 3.0F,
                                    offset - 1.0F,
                                    doubleFinalColor
                                 );
                              break;
                           case "<>":
                              Aqua.INSTANCE
                                 .tenacityNormal
                                 .drawString(
                                    m.getMode().isEmpty() ? m.getName() : String.format("%s %s<%s>", m.getName(), EnumChatFormatting.GRAY, m.getMode()),
                                    wSet2 - 3.0F,
                                    offset - 1.0F,
                                    doubleFinalColor
                                 );
                        }

                        ++index;
                        offset += 11.0F;
                     }
                  }
                  break;
               case "MC":
                  ScaledResolution sr = new ScaledResolution(mc);
                  int index = 0;
                  float offset2 = 0.0F;
                  GL11.glBlendFunc(770, 771);
                  ByteBuffer pixelBuf = ByteBuffer.allocateDirect(4);
                  List<Module> collect = Aqua.setmgr.getSetting("ArraylistSuffixMode").getCurrentMode().equalsIgnoreCase("-")
                     ? Aqua.moduleManager
                        .modules
                        .stream()
                        .filter(Module::isToggled)
                        .sorted(
                           Comparator.comparingInt(
                              value -> -sr.getScaledWidth()
                                    - mc.fontRendererObj
                                       .getStringWidth(
                                          value.getMode().isEmpty()
                                             ? value.getName()
                                             : String.format("%s %s- %s", value.getName(), EnumChatFormatting.WHITE, value.getMode() + "")
                                       )
                           )
                        )
                        .collect(Collectors.toList())
                     : Aqua.moduleManager
                        .modules
                        .stream()
                        .filter(Module::isToggled)
                        .sorted(
                           Comparator.comparingInt(
                              value -> -sr.getScaledWidth()
                                    - mc.fontRendererObj
                                       .getStringWidth(
                                          value.getMode().isEmpty()
                                             ? value.getName()
                                             : String.format("%s %s<%s>", value.getName(), EnumChatFormatting.WHITE, value.getMode() + "")
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

                     float wSetNext = (float)sr.getScaledWidth();
                     if (nextModule != null) {
                        wSetNext = (float)(sr.getScaledWidth() - mc.fontRendererObj.getStringWidth(nextModule.getName()) - 5);
                     }
                  }

                  float offset = 0.0F;

                  for(Module m : collect) {
                     if (m.isToggled()) {
                        float wSet2 = Aqua.setmgr.getSetting("ArraylistSuffixMode").getCurrentMode().equalsIgnoreCase("-")
                           ? (float)(
                              sr.getScaledWidth()
                                 - mc.fontRendererObj
                                    .getStringWidth(
                                       m.getMode().isEmpty()
                                          ? m.getName()
                                          : String.format("%s %s- %s", m.getName(), EnumChatFormatting.GRAY, m.getMode() + "")
                                    )
                           )
                           : (float)(
                              sr.getScaledWidth()
                                 - mc.fontRendererObj
                                    .getStringWidth(
                                       m.getMode().isEmpty()
                                          ? m.getName()
                                          : String.format("%s %s<%s>", m.getName(), EnumChatFormatting.GRAY, m.getMode() + "")
                                    )
                           );
                        if (Aqua.setmgr.getSetting("ArraylistBackground").isState()) {
                           Gui.drawRect2(
                              (double)(wSet2 - 5.0F), (double)offset, (double)sr.getScaledWidth(), (double)(offset + 11.0F), new Color(0, 0, 0, 90).getRGB()
                           );
                        }

                        int rainbow = Arraylist.rainbow((int)offset * 9);
                        int color = Arraylist.getGradientOffset(
                              new Color(Aqua.setmgr.getSetting("HUDColor").getColor()), new Color(this.getArraylistColor()), (double)index / 12.4
                           )
                           .getRGB();
                        int finalColor = Aqua.setmgr.getSetting("ArraylistFade").isState()
                           ? color
                           : new Color(Aqua.setmgr.getSetting("HUDColor").getColor()).getRGB();
                        int doubleFinalColor = Aqua.setmgr.getSetting("ArraylistRainbow").isState() ? rainbow : finalColor;
                        if (Aqua.setmgr.getSetting("ArraylistBloom").isState()) {
                           String var17 = Aqua.setmgr.getSetting("ArraylistBloomMode").getCurrentMode();
                           switch(var17) {
                              case "Glow":
                                 if (Aqua.moduleManager.getModuleByName("ShaderMultiplier").isToggled()) {
                                    ShaderMultiplier.drawGlowESP(
                                       () -> Gui.drawRect2(
                                             (double)(wSet2 - 5.0F), (double)offset, (double)sr.getScaledWidth(), (double)(offset + 11.0F), doubleFinalColor
                                          ),
                                       false
                                    );
                                 }
                                 break;
                              case "Shadow":
                                 if (Aqua.moduleManager.getModuleByName("Shadow").isToggled()) {
                                    Shadow.drawGlow(
                                       () -> Gui.drawRect2(
                                             (double)(wSet2 - 5.0F),
                                             (double)offset,
                                             (double)sr.getScaledWidth(),
                                             (double)(offset + 11.0F),
                                             new Color(0, 0, 0, 255).getRGB()
                                          ),
                                       false
                                    );
                                 }
                           }
                        }

                        String var84 = Aqua.setmgr.getSetting("ArraylistSuffixMode").getCurrentMode();
                        switch(var84) {
                           case "-":
                              mc.fontRendererObj
                                 .drawString(
                                    m.getMode().isEmpty() ? m.getName() : String.format("%s %s- %s", m.getName(), EnumChatFormatting.GRAY, m.getMode()),
                                    (int)(wSet2 - 3.0F),
                                    (int)(offset + 2.0F),
                                    doubleFinalColor
                                 );
                              break;
                           case "<>":
                              mc.fontRendererObj
                                 .drawString(
                                    m.getMode().isEmpty() ? m.getName() : String.format("%s %s<%s>", m.getName(), EnumChatFormatting.GRAY, m.getMode()),
                                    (int)(wSet2 - 3.0F),
                                    (int)(offset + 2.0F),
                                    doubleFinalColor
                                 );
                        }

                        ++index;
                        offset += 11.0F;
                     }
                  }
            }
         }

         GL11.glDisable(3042);
      }
   }

   public int getArraylistColor() {
      try {
         return Aqua.setmgr.getSetting("ArraylistColor").getColor();
      } catch (Exception var2) {
         return Color.white.getRGB();
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
         int rainbow = Arraylist.rainbow((int)offset * 9);
         int color = Arraylist.getGradientOffset(
               new Color(Aqua.setmgr.getSetting("HUDColor").getColor()), new Color(this.getArraylistColor()), (double)index / 12.4
            )
            .getRGB();
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
            int rainbow = Arraylist.rainbow((int)offset * 9);
            int color = Arraylist.getGradientOffset(
                  new Color(Aqua.setmgr.getSetting("HUDColor").getColor()), new Color(this.getArraylistColor()), (double)index / 12.4
               )
               .getRGB();
            int finalColor = Aqua.setmgr.getSetting("ArraylistFade").isState() ? color : new Color(Aqua.setmgr.getSetting("HUDColor").getColor()).getRGB();
            int doubleFinalColor = Aqua.setmgr.getSetting("ArraylistRainbow").isState() ? rainbow : finalColor;
            float finalOffset = offset;
            ShaderMultiplier.drawGlowESP(
               () -> Aqua.INSTANCE
                     .comfortaa4
                     .drawString(
                        m.getMode().isEmpty() ? m.getName() : String.format("%s %s%s", m.getName(), EnumChatFormatting.WHITE, m.getMode()),
                        (float)((int)(wSet2 - 5.0F)),
                        (float)((int)(finalOffset + 12.0F)),
                        doubleFinalColor
                     ),
               false
            );
            Aqua.INSTANCE
               .comfortaa4
               .drawString(
                  m.getMode().isEmpty() ? m.getName() : String.format("%s %s%s", m.getName(), EnumChatFormatting.WHITE, m.getMode()),
                  (float)((int)(wSet2 - 5.0F)),
                  (float)((int)(offset + 12.0F)),
                  doubleFinalColor
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
                  Arraylist.rainbowSigma((int)(offset * 6.0F))
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
            int rainbow = Arraylist.rainbow((int)offset * 9);
            int color = Arraylist.getGradientOffset(
                  new Color(Aqua.setmgr.getSetting("HUDColor").getColor()), new Color(this.getArraylistColor()), (double)index / 12.4
               )
               .getRGB();
            int finalColor = Aqua.setmgr.getSetting("ArraylistFade").isState() ? color : new Color(Aqua.setmgr.getSetting("HUDColor").getColor()).getRGB();
            int doubleFinalColor = Aqua.setmgr.getSetting("ArraylistRainbow").isState() ? rainbow : finalColor;
            float finalOffset = offset;
            Shadow.drawGlow(
               () -> Gui.drawRect2((double)(wSet2 + 3.0F), (double)finalOffset, (double)sr.getScaledWidth(), (double)(finalOffset + 11.0F), doubleFinalColor),
               false
            );
            Gui.drawRect2((double)(wSet2 + 3.0F), (double)offset, (double)sr.getScaledWidth(), (double)(offset + 11.0F), new Color(0, 0, 0, 40).getRGB());
            Aqua.INSTANCE
               .tenacityNormal
               .drawStringWithShadow(
                  m.getMode().isEmpty() ? m.getName() : String.format("%s %s%s", m.getName(), EnumChatFormatting.GRAY, m.getMode()),
                  (float)((int)(wSet2 + 5.0F)),
                  (float)((int)(offset - 1.5F)),
                  doubleFinalColor
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
            int rainbow = Arraylist.rainbow((int)offset * 9);
            int color = Arraylist.getGradientOffset(
                  new Color(Aqua.setmgr.getSetting("HUDColor").getColor()), new Color(this.getArraylistColor()), (double)index / 12.4
               )
               .getRGB();
            int finalColor = Aqua.setmgr.getSetting("Arraylist2Fade").isState() ? color : new Color(Aqua.setmgr.getSetting("HUDColor").getColor()).getRGB();
            int doubleFinalColor = Aqua.setmgr.getSetting("Arraylist2Rainbow").isState() ? rainbow : finalColor;
            float finalOffset = offset;
            Blur.drawBlurred(
               () -> Gui.drawRect2((double)(wSet2 + 3.0F), (double)finalOffset, (double)sr.getScaledWidth(), (double)(finalOffset + 11.0F), doubleFinalColor),
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
         if (Aqua.moduleManager.getModuleByName("ShaderMultiplier").isToggled()) {
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
         } else if (Aqua.moduleManager.getModuleByName("Shadow").isToggled()) {
            Shadow.drawGlow(
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
         }

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
            int rainbow = Arraylist.rainbow((int)offset * 9);
            int color = Arraylist.getGradientOffset(
                  new Color(Aqua.setmgr.getSetting("HUDColor").getColor()), new Color(this.getArraylistColor()), (double)index / 12.4
               )
               .getRGB();
            int finalColor = Aqua.setmgr.getSetting("ArraylistFade").isState() ? color : new Color(Aqua.setmgr.getSetting("HUDColor").getColor()).getRGB();
            int doubleFinalColor = Aqua.setmgr.getSetting("ArraylistRainbow").isState() ? rainbow : finalColor;
            float finalOffset = offset;
            Shadow.drawGlow(
               () -> Aqua.INSTANCE
                     .comfortaa3
                     .drawString(
                        m.getMode().isEmpty() ? m.getName() : String.format("%s %s%s", m.getName(), EnumChatFormatting.GRAY, m.getMode()),
                        wSet2 + 0.5F,
                        finalOffset + 5.0F,
                        doubleFinalColor
                     ),
               false
            );
            Aqua.INSTANCE
               .comfortaa3
               .drawString(
                  m.getMode().isEmpty() ? m.getName() : String.format("%s %s%s", m.getName(), EnumChatFormatting.GRAY, m.getMode()),
                  wSet2 + 0.5F,
                  offset + 5.0F,
                  doubleFinalColor
               );
            offset += 11.0F;
            ++index;
         }
      }

      ++index;
      ++offset2;
      GL11.glDisable(3042);
   }
}
