package intent.AquaDev.aqua.modules.visual;

import de.Hero.settings.Setting;
import events.Event;
import events.listeners.EventPostRender2D;
import gui.jello.ClickguiScreen;
import intent.AquaDev.aqua.Aqua;
import intent.AquaDev.aqua.gui.hero.ClickguiScreenHero;
import intent.AquaDev.aqua.gui.novolineOld.themesScreen.ThemeScreen;
import intent.AquaDev.aqua.gui.suicideX.ClickguiScreenNovoline;
import intent.AquaDev.aqua.modules.Category;
import intent.AquaDev.aqua.modules.Module;

public class GUI extends Module {
   public GUI() {
      super("GUI", Module.Type.Visual, "GUI", 54, Category.Visual);
      Aqua.setmgr.register(new Setting("AutoPosReset", this, true));
      Aqua.setmgr.register(new Setting("RoundedPicker", this, true));
      Aqua.setmgr.register(new Setting("ColorPickerGlow", this, true));
      Aqua.setmgr.register(new Setting("DarkMode", this, true));
      Aqua.setmgr.register(new Setting("Gradiant", this, false));
      Aqua.setmgr.register(new Setting("GUIScizzor", this, 10.0, 10.0, 16.0, false));
      Aqua.setmgr.register(new Setting("Color", this));
      Aqua.setmgr.register(new Setting("GlowMode", this, "Complete", new String[]{"Complete", "AmbientLighting"}));
      Aqua.setmgr.register(new Setting("BooleanMode", this, "Novoline", new String[]{"Novoline", "Rounded", "Square"}));
      Aqua.setmgr.register(new Setting("Mode", this, "Tenacity", new String[]{"Aqua", "SuicideBased", "NovolineOld", "Tenacity", "Jello"}));
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
      if (event instanceof EventPostRender2D) {
         if (ThemeScreen.themeHero && ThemeScreen.themeLoaded) {
            mc.displayGuiScreen(new ClickguiScreenHero(null));
            this.setState(false);
         } else {
            if (Aqua.setmgr.getSetting("GUIMode").getCurrentMode().equalsIgnoreCase("SuicideBased")) {
               mc.displayGuiScreen(new ClickguiScreenNovoline(null));
               this.setState(false);
            }

            if (Aqua.setmgr.getSetting("GUIMode").getCurrentMode().equalsIgnoreCase("Aqua")) {
               mc.displayGuiScreen(new intent.AquaDev.aqua.gui.aqua.ClickguiScreenNovoline(null));
               this.setState(false);
            }

            if (Aqua.setmgr.getSetting("GUIMode").getCurrentMode().equalsIgnoreCase("Jello")) {
               mc.displayGuiScreen(new ClickguiScreen(null));
               this.setState(false);
            }

            if (Aqua.setmgr.getSetting("GUIMode").getCurrentMode().equalsIgnoreCase("Tenacity")) {
               mc.displayGuiScreen(new intent.AquaDev.aqua.gui.tenacity.ClickguiScreenNovoline(null));
               this.setState(false);
            }

            if (Aqua.setmgr.getSetting("GUIMode").getCurrentMode().equalsIgnoreCase("NovolineOld")) {
               mc.displayGuiScreen(new intent.AquaDev.aqua.gui.novolineOld.ClickguiScreenNovoline(null));
               this.setState(false);
            }
         }
      }
   }
}
