package intent.AquaDev.aqua.modules.visual;

import de.Hero.settings.Setting;
import events.Event;
import events.listeners.EventPostRender2D;
import events.listeners.EventRender2D;
import events.listeners.EventTick;
import intent.AquaDev.aqua.Aqua;
import intent.AquaDev.aqua.fr.lavache.anime.Animate;
import intent.AquaDev.aqua.fr.lavache.anime.Easing;
import intent.AquaDev.aqua.gui.novolineOld.themesScreen.ThemeScreen;
import intent.AquaDev.aqua.modules.Category;
import intent.AquaDev.aqua.modules.Module;
import intent.AquaDev.aqua.utils.RenderUtil;
import intent.AquaDev.aqua.utils.TimeUtil;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Keyboard;

public class HUD extends Module {
   Animate anim = new Animate();
   Animate anim2 = new Animate();
   Animate anim3 = new Animate();
   private int index;
   private int indexModules;
   private boolean opened;
   private int last = 0;
   private boolean reversed = false;
   private boolean reversed2 = false;
   private TimeUtil moveDelayTimer = new TimeUtil();
   private int maxModules;

   public HUD() {
      super("HUD", Module.Type.Visual, "HUD", 0, Category.Visual);
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
   public void setup() {
      Aqua.setmgr.register(new Setting("Red", this, 255.0, 0.0, 255.0, true));
      Aqua.setmgr.register(new Setting("Green", this, 26.0, 0.0, 255.0, true));
      Aqua.setmgr.register(new Setting("Blue", this, 42.0, 0.0, 255.0, true));
      Aqua.setmgr.register(new Setting("Color", this));
      Aqua.setmgr.register(new Setting("Watermarks", this, "Modern", new String[]{"Normal", "Japan", "Modern", "NEW", "Aqua"}));
   }

   @Override
   public void onEvent(Event event) {
      if (event instanceof EventTick && ThemeScreen.themeLoaded) {
         List<Module> modules = new ArrayList<>();

         for(Module module : Aqua.moduleManager.modules) {
            if (module.type == Module.Type.values()[this.index]) {
               modules.add(module);
            }
         }

         if (!this.opened) {
            this.indexModules = 0;
         }

         this.maxModules = modules.size();
         if (this.reversed) {
            this.opened = false;
         }

         if (!this.opened) {
            if (Keyboard.isKeyDown(208) && this.index < Module.Type.values().length - 1 && this.moveDelayTimer.hasReached(120L)) {
               ++this.index;
               this.moveDelayTimer.reset();
            }

            if (Keyboard.isKeyDown(200) && this.index > 0 && this.moveDelayTimer.hasReached(120L)) {
               --this.index;
               this.moveDelayTimer.reset();
            }
         } else {
            if (Keyboard.isKeyDown(208) && this.indexModules < this.maxModules - 1 && this.moveDelayTimer.hasReached(120L)) {
               ++this.indexModules;
               this.moveDelayTimer.reset();
            }

            if (Keyboard.isKeyDown(200) && this.indexModules > 0 && this.moveDelayTimer.hasReached(120L)) {
               --this.indexModules;
               this.moveDelayTimer.reset();
            }
         }

         if (Keyboard.isKeyDown(205)) {
            if (this.opened && this.moveDelayTimer.hasReached(200L)) {
               modules.get(this.indexModules).setState(!modules.get(this.indexModules).isToggled());
               this.moveDelayTimer.reset();
            } else {
               this.moveDelayTimer.reset();
               this.reversed = false;
               this.opened = true;
            }
         }

         if (Keyboard.isKeyDown(203)) {
            this.reversed = true;
         }
      }

      if (event instanceof EventRender2D) {
         if (ThemeScreen.themeRise || ThemeScreen.themeRise6) {
            int color = Arraylist.getGradientOffset(
                  new Color(Aqua.setmgr.getSetting("HUDColor").getColor()),
                  new Color(Aqua.setmgr.getSetting("ArraylistColor").getColor()),
                  (double)this.index / 12.4
               )
               .getRGB();
            if (ThemeScreen.themeRise) {
               Aqua.INSTANCE.comfortaa.drawStringWithShadow("Rise", 0.0F, 0.0F, color);
               Aqua.INSTANCE.comfortaa4.drawStringWithShadow("" + Aqua.build, 40.0F, 2.0F, color);
            } else {
               ShaderMultiplier.drawGlowESP(() -> Aqua.INSTANCE.comfortaa.drawString("Rise", 0.0F, 0.0F, color), false);
               Aqua.INSTANCE.comfortaa.drawString("Rise", 0.0F, 0.0F, color);
            }
         }

         if (ThemeScreen.themeXave) {
            Aqua.INSTANCE.esp.drawStringWithShadow("Xave", 0.0F, 0.0F, this.getColor2());
         }

         if (ThemeScreen.themeTenacity) {
            Shadow.drawGlow(() -> Aqua.INSTANCE.tenacity.drawStringWithShadow("Aqua " + Aqua.build, 2.0F, 2.0F, this.getColor2()), false);
            Aqua.INSTANCE.tenacity.drawStringWithShadow("Aqua " + Aqua.build, 2.0F, 2.0F, this.getColor2());
         }

         if (ThemeScreen.themeHero) {
            List<Module> modules = new ArrayList<>();

            for(Module module : Aqua.moduleManager.modules) {
               if (module.type == Module.Type.values()[this.index]) {
                  modules.add(module);
               }
            }

            new ScaledResolution(mc);
            float x = 0.0F;
            float y = 17.0F;
            float height = (float)(Module.Type.values().length * 14);
            float width = 56.0F;
            Gui.drawRect2((double)width, 0.0, (double)(width + 1.0F), (double)height, new Color(6, 226, 70, 160).getRGB());
            Gui.drawRect2(0.0, (double)(height - 1.0F), (double)(width + 1.0F), (double)height, new Color(6, 226, 70, 160).getRGB());
            Gui.drawRect2((double)x, 0.0, (double)(x + width), (double)(y + height - 18.0F), new Color(12, 12, 12, 170).getRGB());
            Gui.drawRect(
               (int)width, (int)(y + (float)(this.index * 11) + 4.0F), 0, (int)(y + 14.0F + (float)(this.index * 11)), new Color(6, 226, 70, 160).getRGB()
            );

            for(int yT = 0; yT < Module.Type.values().length; ++yT) {
               Aqua.INSTANCE.roboto2.drawString(Module.Type.values()[yT].name(), x + 1.0F, y + (float)(yT * 11) + 1.5F, Color.white.getRGB());
            }

            if (this.opened) {
               float moduleHeight = (float)(modules.size() * 11);
               Gui.drawRect2(
                  (double)(x + width + 3.0F),
                  (double)(y + (float)(this.index * 11) + 3.0F),
                  (double)(x + width + 62.5F + 7.5F + (float)this.maxModules),
                  (double)(y + (float)(this.index * 11) + moduleHeight + 4.0F),
                  new Color(12, 12, 12, 170).getRGB()
               );

               for(int i = 0; i < modules.size(); ++i) {
                  Gui.drawRect2(
                     (double)(x + width + 3.0F),
                     (double)(y + (float)(this.index * 11) + (float)(this.indexModules * 11) + 3.0F),
                     (double)(x + width + 5.0F),
                     (double)(y + (float)(this.index * 11) + 13.0F + (float)(this.indexModules * 11) + 2.0F),
                     new Color(6, 226, 70, 160).getRGB()
                  );
                  Aqua.INSTANCE
                     .roboto2
                     .drawString(
                        modules.get(i).getName(),
                        x + width + 6.0F,
                        y + (float)(this.index * 11) + (float)(i * 11) + 2.0F,
                        this.indexModules != i && !modules.get(i).isToggled() ? Color.white.getRGB() : new Color(6, 226, 70, 160).getRGB()
                     );
               }
            }

            Aqua.INSTANCE.robotoTabguiName.drawString("Hero", 2.0F, -4.0F, new Color(6, 226, 70, 160).getRGB());
         }

         if (ThemeScreen.themeJello) {
            List<Module> modules = new ArrayList<>();

            for(Module module : Aqua.moduleManager.modules) {
               if (module.type == Module.Type.values()[this.index]) {
                  modules.add(module);
               }
            }

            new ScaledResolution(mc);
            float x = 5.0F;
            float y = 35.0F;
            float height = (float)(Module.Type.values().length * 15);
            float width = 68.0F;
            Shadow.drawGlow(() -> Gui.drawRect2((double)x, (double)y, (double)(x + width), (double)(y + height - 13.0F), Color.darkGray.getRGB()), false);
            Blur.drawBlurred(
               () -> Gui.drawRect2((double)x, (double)y, (double)(x + width), (double)(y + height - 13.0F), new Color(255, 254, 254, 55).getRGB()), false
            );
            Blur.drawBlurred(
               () -> Gui.drawRect(
                     (int)width,
                     (int)(y + (float)(this.index * 13)),
                     (int)x,
                     (int)(y + 14.0F + (float)(this.index * 13 - 2)),
                     new Color(79, 82, 79, 160).getRGB()
                  ),
               false
            );
            if (this.opened) {
               float moduleHeight = (float)(modules.size() * 13);
               Shadow.drawGlow(
                  () -> Gui.drawRect2(
                        (double)(x + width + 3.0F),
                        (double)(y + (float)(this.index * 13)),
                        (double)(x + width + 62.5F + 7.5F + (float)this.maxModules),
                        (double)(y + (float)(this.index * 13) + moduleHeight),
                        Color.darkGray.getRGB()
                     ),
                  false
               );
               Blur.drawBlurred(
                  () -> Gui.drawRect2(
                        (double)(x + width + 3.0F),
                        (double)(y + (float)(this.index * 13)),
                        (double)(x + width + 62.5F + 7.5F + (float)this.maxModules),
                        (double)(y + (float)(this.index * 13) + moduleHeight),
                        new Color(20, 20, 20, 120).getRGB()
                     ),
                  false
               );

               for(int i = 0; i < modules.size(); ++i) {
                  Gui.drawRect2(
                     (double)(x + width + 3.0F),
                     (double)(y + (float)(this.index * 13) + (float)(this.indexModules * 13)),
                     (double)(x + width + 5.0F),
                     (double)(y + (float)(this.index * 13) + 13.0F + (float)(this.indexModules * 13)),
                     Color.lightGray.getRGB()
                  );
                  Aqua.INSTANCE
                     .sigma
                     .drawString(
                        modules.get(i).getName(),
                        x + width + 6.0F,
                        y + (float)(this.index * 13) + (float)(i * 13) + 2.0F,
                        this.indexModules != i && !modules.get(i).isToggled() ? Color.white.getRGB() : Color.lightGray.getRGB()
                     );
               }
            }

            Shadow.drawGlow(() -> Aqua.INSTANCE.jelloTabGUI.drawString("Sigma", 10.0F, -1.0F, Color.black.getRGB()), false);
            Shadow.drawGlow(() -> Aqua.INSTANCE.jelloClickguiPanelBottom.drawString("Jello", 10.0F, 22.0F, Color.black.getRGB()), false);
            Aqua.INSTANCE.jelloTabGUI.drawString("Sigma", 10.0F, -1.0F, new Color(250, 250, 250, 210).getRGB());
            Aqua.INSTANCE.jelloClickguiPanelBottom.drawString("Jello", 10.0F, 22.0F, new Color(250, 250, 250, 210).getRGB());
         }

         if (ThemeScreen.themeSigma) {
            List<Module> modules = new ArrayList<>();

            for(Module module : Aqua.moduleManager.modules) {
               if (module.type == Module.Type.values()[this.index]) {
                  modules.add(module);
               }
            }

            new ScaledResolution(mc);
            float x = 5.0F;
            float y = 20.0F;
            float height = (float)(Module.Type.values().length * 15);
            float width = 60.0F;
            Gui.drawRect2(
               (double)(x - 4.0F), (double)(y - 20.0F), (double)(x + width - 10.0F), (double)(y + height - 13.0F), new Color(20, 20, 20, 120).getRGB()
            );
            RenderUtil.drawGradientRectHorizontal(
               (double)(x - 4.0F), (double)(y + (float)(this.index * 13)), (double)(x + 47.0F), 12.0, Color.black.getRGB(), this.getColor2()
            );

            for(int yT = 0; yT < Module.Type.values().length; ++yT) {
               Aqua.INSTANCE.sigma.drawString(Module.Type.values()[yT].name(), x - 3.0F, y + (float)(yT * 13 + 2), Color.white.getRGB());
            }

            if (this.opened) {
               float moduleHeight = (float)(modules.size() * 13);
               Gui.drawRect2(
                  (double)(x + width - 8.0F),
                  (double)(y + (float)(this.index * 13)),
                  (double)(x + width + 62.5F + 2.5F + (float)this.maxModules),
                  (double)(y + (float)(this.index * 13) + moduleHeight),
                  new Color(20, 20, 20, 120).getRGB()
               );

               for(int i = 0; i < modules.size(); ++i) {
                  Gui.drawRect2(
                     (double)(x + width - 8.0F),
                     (double)(y + (float)(this.index * 13) + (float)(this.indexModules * 13)),
                     (double)(x + width - 5.0F),
                     (double)(y + (float)(this.index * 13) + 13.0F + (float)(this.indexModules * 13)),
                     this.getColor2()
                  );
                  Aqua.INSTANCE
                     .sigma
                     .drawString(
                        modules.get(i).getName(),
                        x + width - 2.0F,
                        y + (float)(this.index * 13) + (float)(i * 13) + 2.0F,
                        this.indexModules != i && !modules.get(i).isToggled() ? Color.white.getRGB() : this.getColor2()
                     );
               }
            }

            Aqua.INSTANCE.sigma2.drawStringWithShadow("Sigma", 1.0F, 2.0F, Color.lightGray.getRGB());
            Aqua.INSTANCE.sigma3.drawString("v5.0", 38.0F, 2.0F, Arraylist.rainbowSigma(this.index * 6));
            Gui.drawRect2(5.0, 17.5, 51.0, 18.5, Color.white.getRGB());
         }

         if (ThemeScreen.themeLoaded) {
            return;
         }

         if (Aqua.setmgr.getSetting("HUDWatermarks").getCurrentMode().equalsIgnoreCase("Modern")) {
            int posX = 0;
            int posY = 4;
            int width = 55;
            int height = 15;
            if (Aqua.moduleManager.getModuleByName("Blur").isToggled()) {
               Blur.drawBlurred(
                  () -> RenderUtil.drawRoundedRect2Alpha((double)(posX - 5), (double)posY, (double)width, (double)height, 3.0, new Color(0, 0, 0, 255)), false
               );
            }
         }
      }

      if (event instanceof EventPostRender2D) {
         if (Aqua.setmgr.getSetting("HUDWatermarks").getCurrentMode().equalsIgnoreCase("Aqua") && (!ThemeScreen.themeLoaded || ThemeScreen.themeAqua2)) {
            int posX = 3;
            int posY = 6;
            String beta = !Objects.equals(Aqua.INSTANCE.ircClient.getNickname(), "LCA_MODZ")
                  && !Objects.equals(Aqua.INSTANCE.ircClient.getNickname(), "luigiaqua")
               ? ""
               : " Beta";
            int width = Aqua.INSTANCE
                  .tenacityNormal
                  .getStringWidth(
                     Aqua.name
                        + " b"
                        + Aqua.build
                        + (
                           !Objects.equals(Aqua.INSTANCE.ircClient.getNickname(), "LCA_MODZ")
                                 && !Objects.equals(Aqua.INSTANCE.ircClient.getNickname(), "luigiaqua")
                              ? 0
                              : 22
                        )
                        + Minecraft.getDebugFPS()
                        + "FPS |  | "
                        + mc.session.getUsername()
                  )
               + 50;
            int height = 15;
            if (Aqua.moduleManager.getModuleByName("Shadow").isToggled()) {
               Shadow.drawGlow(
                  () -> RenderUtil.drawRoundedRect2Alpha((double)posX, (double)posY, (double)width, (double)(height + 1), 3.4, new Color(20, 20, 20, 200)),
                  false
               );
            }

            RenderUtil.drawRoundedRect2Alpha((double)posX, (double)posY, (double)width, (double)(height + 1), 3.4, new Color(20, 20, 20, 70));
            Aqua.INSTANCE.tenacityNormal.drawString(Aqua.name + " b" + Aqua.build + beta, (float)(posX + 5), (float)(posY + 1), this.getColor2());
            Aqua.INSTANCE
               .tenacityNormal
               .drawString(
                  Minecraft.getDebugFPS() + "FPS",
                  (float)(
                     Aqua.INSTANCE
                           .tenacityNormal
                           .getStringWidth(
                              !Objects.equals(Aqua.INSTANCE.ircClient.getNickname(), "LCA_MODZ")
                                    && !Objects.equals(Aqua.INSTANCE.ircClient.getNickname(), "luigiaqua")
                                 ? ""
                                 : "Beta"
                           )
                        + (
                           !Objects.equals(Aqua.INSTANCE.ircClient.getNickname(), "LCA_MODZ")
                                 && !Objects.equals(Aqua.INSTANCE.ircClient.getNickname(), "luigiaqua")
                              ? posX + 55
                              : posX + 50
                        )
                  ),
                  (float)(posY + 1),
                  -1
               );
            Aqua.INSTANCE
               .tenacityNormal
               .drawString(
                  mc.session.getUsername(),
                  (float)(
                     Aqua.INSTANCE.tenacityNormal.getStringWidth(Aqua.name + " b" + Aqua.build + Minecraft.getDebugFPS() + "FPS | ")
                        + (
                           !Objects.equals(Aqua.INSTANCE.ircClient.getNickname(), "LCA_MODZ")
                                 && !Objects.equals(Aqua.INSTANCE.ircClient.getNickname(), "luigiaqua")
                              ? posX + 17
                              : 35
                        )
                  ),
                  (float)(posY + 1),
                  -1
               );
            Aqua.INSTANCE
               .tenacityNormal
               .drawString(
                  (mc.isSingleplayer() ? "0" : mc.getCurrentServerData().pingToServer) + "MS",
                  (float)(
                     Aqua.INSTANCE
                           .tenacityNormal
                           .getStringWidth(Aqua.name + " b" + Aqua.build + Minecraft.getDebugFPS() + "FPS |  | " + mc.session.getUsername())
                        + (
                           !Objects.equals(Aqua.INSTANCE.ircClient.getNickname(), "LCA_MODZ")
                                 && !Objects.equals(Aqua.INSTANCE.ircClient.getNickname(), "luigiaqua")
                              ? 22
                              : 35
                        )
                  ),
                  (float)(posY + 1),
                  -1
               );
            Aqua.INSTANCE
               .tenacityNormal
               .drawString(
                  " | ",
                  (float)(
                     !Objects.equals(Aqua.INSTANCE.ircClient.getNickname(), "LCA_MODZ") && !Objects.equals(Aqua.INSTANCE.ircClient.getNickname(), "luigiaqua")
                        ? posX + 47
                        : posX + 62
                  ),
                  (float)(posY + 1),
                  new Color(20, 20, 20, 10).getRGB()
               );
            Aqua.INSTANCE
               .tenacityNormal
               .drawString(
                  " | ",
                  (float)(
                     Aqua.INSTANCE.tenacityNormal.getStringWidth(Aqua.name + " b" + Aqua.build + Minecraft.getDebugFPS() + "FPS | ")
                        + (
                           !Objects.equals(Aqua.INSTANCE.ircClient.getNickname(), "LCA_MODZ")
                                 && !Objects.equals(Aqua.INSTANCE.ircClient.getNickname(), "luigiaqua")
                              ? 10
                              : 29
                        )
                  ),
                  (float)(posY + 1),
                  new Color(20, 20, 20, 10).getRGB()
               );
            Aqua.INSTANCE
               .tenacityNormal
               .drawString(
                  "  | ",
                  (float)(
                     Aqua.INSTANCE
                           .tenacityNormal
                           .getStringWidth(Aqua.name + " b" + Aqua.build + Minecraft.getDebugFPS() + "FPS |  | " + mc.session.getUsername())
                        + (
                           !Objects.equals(Aqua.INSTANCE.ircClient.getNickname(), "LCA_MODZ")
                                 && !Objects.equals(Aqua.INSTANCE.ircClient.getNickname(), "luigiaqua")
                              ? 11
                              : 26
                        )
                  ),
                  (float)(posY + 1),
                  new Color(20, 20, 20, 10).getRGB()
               );
         }

         if (Aqua.setmgr.getSetting("HUDWatermarks").getCurrentMode().equalsIgnoreCase("NEW")) {
            int posX = 3;
            int posY = 6;
            int width = (int)Aqua.INSTANCE
               .tenacityNormal
               .getWidth(Aqua.name + " b" + Aqua.build + " | " + mc.getCurrentServerData().serverIP + " | " + mc.session.getUsername() + 15);
            int height = 15;
            Shadow.drawGlow(
               () -> Gui.drawRect(
                     posX,
                     posY,
                     width,
                     posY - 2,
                     Arraylist.getGradientOffset(
                           new Color(Aqua.setmgr.getSetting("ArraylistColor").getColor()),
                           new Color(Aqua.setmgr.getSetting("HUDColor").getColor()),
                           (double)this.index / 12.4
                        )
                        .getRGB()
                  ),
               false
            );
            Shadow.drawGlow(() -> Gui.drawRect(posX, posY - 2, width, height + 2, new Color(0, 0, 0, 160).getRGB()), false);
            Gui.drawRect(posX, posY - 2, width, height + 2, new Color(0, 0, 0, 60).getRGB());
            Gui.drawRect(
               posX,
               posY,
               width,
               posY - 2,
               Arraylist.getGradientOffset(
                     new Color(Aqua.setmgr.getSetting("ArraylistColor").getColor()),
                     new Color(Aqua.setmgr.getSetting("HUDColor").getColor()),
                     (double)this.index / 12.4
                  )
                  .getRGB()
            );
            Aqua.INSTANCE
               .tenacityNormal
               .drawString(
                  Aqua.name + " b" + Aqua.build + " | " + mc.getCurrentServerData().serverIP + " | " + mc.session.getUsername(),
                  (float)(posX + 2),
                  (float)(posY - 2),
                  -1
               );
         }

         if (ThemeScreen.themeJello) {
            List<Module> modules = new ArrayList<>();

            for(Module module : Aqua.moduleManager.modules) {
               if (module.type == Module.Type.values()[this.index]) {
                  modules.add(module);
               }
            }

            new ScaledResolution(mc);
            float x = 5.0F;
            float y = 35.0F;
            float height = (float)(Module.Type.values().length * 15);
            float width = 68.0F;
            Gui.drawRect(
               (int)(width + 5.0F),
               (int)(y + (float)(this.index * 13)),
               (int)x,
               (int)(y + 14.0F + (float)(this.index * 13 - 2)),
               new Color(52, 51, 51, 25).getRGB()
            );

            for(int yT = 0; yT < Module.Type.values().length; ++yT) {
               Aqua.INSTANCE.jelloTabGUIBottom.drawString(Module.Type.values()[yT].name(), x + 1.0F, y + (float)(yT * 13), Color.white.getRGB());
            }

            if (this.opened) {
               float moduleHeight = (float)(modules.size() * 13);

               for(int i = 0; i < modules.size(); ++i) {
                  Gui.drawRect2(
                     (double)(x + width + 3.0F),
                     (double)(y + (float)(this.index * 13) + (float)(this.indexModules * 13)),
                     (double)(x + width + 5.0F),
                     (double)(y + (float)(this.index * 13) + 13.0F + (float)(this.indexModules * 13)),
                     new Color(79, 82, 79, 160).getRGB()
                  );
                  Aqua.INSTANCE
                     .sigma
                     .drawString(
                        modules.get(i).getName(),
                        x + width + 6.0F,
                        y + (float)(this.index * 13) + (float)(i * 13) + 2.0F,
                        this.indexModules != i && !modules.get(i).isToggled() ? Color.white.getRGB() : new Color(79, 82, 79, 160).getRGB()
                     );
               }
            }

            Shadow.drawGlow(() -> Aqua.INSTANCE.jelloTabGUI.drawString("Sigma", 10.0F, -1.0F, Color.gray.getRGB()), false);
            Shadow.drawGlow(() -> Aqua.INSTANCE.jelloClickguiPanelBottom.drawString("Jello", 10.0F, 22.0F, Color.gray.getRGB()), false);
         }

         this.anim2.setEase(Easing.LINEAR).setMin(0.0F).setMax(27.0F).setSpeed(45.0F).setReversed(false).update();
         this.anim.setEase(Easing.LINEAR).setMin(0.0F).setMax(13.0F).setSpeed(45.0F).setReversed(false).update();
         this.anim3.setEase(Easing.LINEAR).setMin(0.0F).setMax(27.0F).setSpeed(45.0F).setReversed(!GuiNewChat.animatedChatOpen).update();
         ScaledResolution scaledResolution = new ScaledResolution(mc);
         int posX = 4;
         int posY = (int)((float)scaledResolution.getScaledHeight() - this.anim2.getValue());
         int posY2 = (int)((float)scaledResolution.getScaledHeight() + this.anim.getValue());
         Gui.drawRect2(
            2.0,
            (double)((float)GuiScreen.height - this.anim3.getValue() + 12.0F),
            (double)(GuiScreen.width - 2),
            (double)GuiScreen.height,
            new Color(0, 0, 0, 70).getRGB()
         );
         if (GuiNewChat.getChatOpen()) {
            GuiNewChat.animatedChatOpen = true;
            Aqua.INSTANCE
               .comfortaa4
               .drawString(
                  "Version : " + Aqua.INSTANCE.ircClient.getRank().name() + " | Nickname : " + mc.session.getUsername() + " | BPS : " + calculateBPS(),
                  (float)posX,
                  (float)posY,
                  this.getColor2()
               );
            this.anim.reset();
         } else {
            GuiNewChat.animatedChatOpen = false;
            this.anim2.reset();
            Aqua.INSTANCE
               .comfortaa4
               .drawString(
                  "Version : " + Aqua.INSTANCE.ircClient.getRank().name() + " | Nickname : " + mc.session.getUsername() + " | BPS : " + calculateBPS(),
                  (float)posX,
                  (float)(posY2 - 25),
                  this.getColor2()
               );
         }

         if (ThemeScreen.themeLoaded) {
            return;
         }

         this.drawLogo();
      }
   }

   public void drawLogo() {
      int i = 0;
      int color = Aqua.setmgr.getSetting("HUDColor").getColor();
      if (Aqua.setmgr.getSetting("HUDWatermarks").getCurrentMode().equalsIgnoreCase("Modern")) {
         int posX = 0;
         int posY = 4;
         int width = 55;
         int height = 15;
         if (Aqua.moduleManager.getModuleByName("Shadow").isToggled()) {
            Shadow.drawGlow(
               () -> RenderUtil.drawRoundedRect2Alpha((double)(posX - 5), (double)posY, (double)width, (double)height, 1.0, new Color(0, 0, 0, 255)), false
            );
         }

         RenderUtil.drawRoundedRect2Alpha((double)(posX - 5), (double)posY, (double)width, (double)height, 1.0, new Color(0, 0, 0, 90));
         Aqua.INSTANCE.comfortaa4.drawString("Aqua ", 6.0F, 7.0F, -1);
         Aqua.INSTANCE
            .comfortaa4
            .drawString(
               "b" + Aqua.build,
               31.0F,
               7.0F,
               Arraylist.getGradientOffset(
                     new Color(Aqua.setmgr.getSetting("HUDColor").getColor()), new Color(Aqua.setmgr.getSetting("ArraylistColor").getColor()), 15.0
                  )
                  .getRGB()
            );
         Gui.drawRect2(
            (double)posX,
            (double)posY,
            2.0,
            (double)(height + 4),
            Arraylist.getGradientOffset(
                  new Color(Aqua.setmgr.getSetting("HUDColor").getColor()), new Color(Aqua.setmgr.getSetting("ArraylistColor").getColor()), 15.0
               )
               .getRGB()
         );
      }

      if (Aqua.setmgr.getSetting("HUDWatermarks").getCurrentMode().equalsIgnoreCase("Normal")) {
         RenderUtil.drawRoundedRect2Alpha(
            5.0,
            4.0,
            (double)Aqua.INSTANCE.comfortaa3.getWidth("Aqua | " + Minecraft.getDebugFPS() + " | " + mc.getSession().getUsername() + 5),
            13.0,
            2.0,
            new Color(20, 20, 20, 70)
         );
         if (Aqua.moduleManager.getModuleByName("Shadow").isToggled()) {
            Shadow.drawGlow(
               () -> RenderUtil.drawRoundedRect(
                     5.0,
                     4.0,
                     (double)Aqua.INSTANCE.comfortaa3.getWidth("Aqua | " + Minecraft.getDebugFPS() + " | " + mc.getSession().getUsername() + 5),
                     13.0,
                     2.0,
                     new Color(0, 0, 0, 255).getRGB()
                  ),
               false
            );
         }

         if (Aqua.moduleManager.getModuleByName("Blur").isToggled()) {
            Blur.drawBlurred(
               () -> RenderUtil.drawRoundedRect(
                     5.0,
                     4.0,
                     (double)Aqua.INSTANCE.comfortaa3.getWidth("Aqua | " + Minecraft.getDebugFPS() + " | " + mc.getSession().getUsername() + 5),
                     16.0,
                     2.0,
                     new Color(0, 0, 0, 255).getRGB()
                  ),
               false
            );
         }

         if (Aqua.moduleManager.getModuleByName("Arraylist").isToggled()) {
            Arraylist.drawGlowArray(
               () -> RenderUtil.drawRoundedRect2Alpha(
                     5.0,
                     17.0,
                     (double)Aqua.INSTANCE.comfortaa3.getWidth("Aqua | " + Minecraft.getDebugFPS() + " | " + mc.getSession().getUsername() + 5),
                     2.0,
                     1.0,
                     Arraylist.getGradientOffset(new Color(this.getColor2()), new Color(this.getColor2()), 0.0)
                  ),
               false
            );
         }

         RenderUtil.drawRoundedRect2Alpha(
            5.0,
            17.0,
            (double)Aqua.INSTANCE.comfortaa3.getWidth("Aqua | " + Minecraft.getDebugFPS() + " | " + mc.getSession().getUsername() + 5),
            2.0,
            1.0,
            Arraylist.getGradientOffset(new Color(this.getColor2()), new Color(this.getColor2()), 0.0)
         );
         Aqua.INSTANCE.comfortaa3.drawString("Aqua | " + Minecraft.getDebugFPS() + " | " + mc.getSession().getUsername(), 7.0F, 5.0F, -1);
      }

      if (Aqua.setmgr.getSetting("HUDWatermarks").getCurrentMode().equalsIgnoreCase("Japan")) {
         Arraylist.drawGlowArray(() -> Gui.drawRect(4, 2, Aqua.INSTANCE.japan.getStringWidth("Aqua") + 8, 21, color), false);
         Gui.drawRect(4, 2, Aqua.INSTANCE.japan.getStringWidth("Aqua") + 8, 21, new Color(0, 0, 0, 60).getRGB());
         Blur.drawBlurred(() -> Gui.drawRect(4, 2, Aqua.INSTANCE.japan.getStringWidth("Aqua") + 8, 21, new Color(0, 0, 0, 60).getRGB()), false);
         Aqua.INSTANCE.japan.drawString("A", 5.0F, 1.0F, color);
         Aqua.INSTANCE.japan.drawString("qua", 23.0F, 1.0F, -1);
      }
   }

   public static int rainbow(int delay) {
      double rainbowState = Math.ceil((double)((System.currentTimeMillis() + (long)delay) / 7L));
      rainbowState %= 360.0;
      return Color.getHSBColor((float)(rainbowState / 360.0), 0.9F, 1.0F).getRGB();
   }

   public int getColor2() {
      try {
         return Aqua.setmgr.getSetting("HUDColor").getColor();
      } catch (Exception var2) {
         return Color.white.getRGB();
      }
   }

   public static Color setAlpha(int color, int alpha) {
      return new Color(new Color(color).getRed(), new Color(color).getGreen(), new Color(color).getBlue(), alpha);
   }

   public static int getColor(int red, int green, int blue, int alpha) {
      int color = MathHelper.clamp_int(alpha, 0, 255) << 24;
      color |= MathHelper.clamp_int(red, 0, 255) << 16;
      color |= MathHelper.clamp_int(green, 0, 255) << 8;
      return color | MathHelper.clamp_int(blue, 0, 255);
   }

   public static double calculateBPS() {
      double calculateTicks = mc.thePlayer.getActivePotionEffect(Potion.moveSpeed) != null && Aqua.moduleManager.getModuleByName("Speed").isToggled()
         ? 30.0
         : 20.0;
      double bps = Math.hypot(mc.thePlayer.posX - mc.thePlayer.prevPosX, mc.thePlayer.posZ - mc.thePlayer.prevPosZ)
         * (double)mc.timer.timerSpeed
         * calculateTicks;
      return (double)Math.round(bps * 100.0) / 100.0;
   }
}
