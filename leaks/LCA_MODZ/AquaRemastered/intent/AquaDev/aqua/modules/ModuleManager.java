package intent.AquaDev.aqua.modules;

import intent.AquaDev.aqua.Aqua;
import intent.AquaDev.aqua.modules.combat.AntiFireball;
import intent.AquaDev.aqua.modules.combat.Antibot;
import intent.AquaDev.aqua.modules.combat.Backtrack;
import intent.AquaDev.aqua.modules.combat.BedAura;
import intent.AquaDev.aqua.modules.combat.Killaura;
import intent.AquaDev.aqua.modules.combat.TimerRange;
import intent.AquaDev.aqua.modules.combat.TpAura;
import intent.AquaDev.aqua.modules.combat.Velocity;
import intent.AquaDev.aqua.modules.ghost.AutoClicker;
import intent.AquaDev.aqua.modules.ghost.Triggerbot;
import intent.AquaDev.aqua.modules.misc.AutoEnable;
import intent.AquaDev.aqua.modules.misc.AutoHypixel;
import intent.AquaDev.aqua.modules.misc.Disabler;
import intent.AquaDev.aqua.modules.misc.KillMessage;
import intent.AquaDev.aqua.modules.movement.AntiVoid;
import intent.AquaDev.aqua.modules.movement.Fly;
import intent.AquaDev.aqua.modules.movement.Longjump;
import intent.AquaDev.aqua.modules.movement.NoSlow;
import intent.AquaDev.aqua.modules.movement.Speed;
import intent.AquaDev.aqua.modules.movement.Sprint;
import intent.AquaDev.aqua.modules.movement.Step;
import intent.AquaDev.aqua.modules.movement.Strafe;
import intent.AquaDev.aqua.modules.movement.TargetStrafe;
import intent.AquaDev.aqua.modules.player.AutoTool;
import intent.AquaDev.aqua.modules.player.ChestStealer;
import intent.AquaDev.aqua.modules.player.FastUse;
import intent.AquaDev.aqua.modules.player.InvManager;
import intent.AquaDev.aqua.modules.player.InvMove;
import intent.AquaDev.aqua.modules.player.MCF;
import intent.AquaDev.aqua.modules.player.Nofall;
import intent.AquaDev.aqua.modules.player.Regen;
import intent.AquaDev.aqua.modules.visual.Animations;
import intent.AquaDev.aqua.modules.visual.Arraylist2;
import intent.AquaDev.aqua.modules.visual.Blur;
import intent.AquaDev.aqua.modules.visual.ChinaHat;
import intent.AquaDev.aqua.modules.visual.CustomCapes;
import intent.AquaDev.aqua.modules.visual.CustomChat;
import intent.AquaDev.aqua.modules.visual.CustomHitEffekt;
import intent.AquaDev.aqua.modules.visual.CustomHotbar;
import intent.AquaDev.aqua.modules.visual.CustomScoreboard;
import intent.AquaDev.aqua.modules.visual.ESP2D;
import intent.AquaDev.aqua.modules.visual.FakeBlock;
import intent.AquaDev.aqua.modules.visual.Fullbright;
import intent.AquaDev.aqua.modules.visual.GUI;
import intent.AquaDev.aqua.modules.visual.Glint;
import intent.AquaDev.aqua.modules.visual.GuiElements;
import intent.AquaDev.aqua.modules.visual.HUD;
import intent.AquaDev.aqua.modules.visual.HeldItem;
import intent.AquaDev.aqua.modules.visual.KeyStrokes;
import intent.AquaDev.aqua.modules.visual.Nametags;
import intent.AquaDev.aqua.modules.visual.NoHurtCam;
import intent.AquaDev.aqua.modules.visual.Notifications;
import intent.AquaDev.aqua.modules.visual.Radar;
import intent.AquaDev.aqua.modules.visual.SessionInfo;
import intent.AquaDev.aqua.modules.visual.ShaderESP;
import intent.AquaDev.aqua.modules.visual.ShaderMultiplier;
import intent.AquaDev.aqua.modules.visual.Shadow;
import intent.AquaDev.aqua.modules.visual.TargetHUD;
import intent.AquaDev.aqua.modules.visual.Tracers;
import intent.AquaDev.aqua.modules.visual.WorldColor;
import intent.AquaDev.aqua.modules.visual.WorldTime;
import intent.AquaDev.aqua.modules.world.BedFucker;
import intent.AquaDev.aqua.modules.world.Eagle;
import intent.AquaDev.aqua.modules.world.Scaffold;
import intent.AquaDev.aqua.utils.UnicodeFontRenderer;
import intent.AquaDev.aqua.utils.UnicodeFontRenderer3;
import intent.AquaDev.aqua.utils.UnicodeFontRenderer5;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ModuleManager {
   public List<Module> modules = new ArrayList<>();

   public ModuleManager() {
      this.addModule(new WorldColor());
      this.addModule(new Nofall());
      this.addModule(new Killaura());
      this.addModule(new Velocity());
      this.addModule(new Sprint());
      this.addModule(new AutoClicker());
      this.addModule(new Blur());
      this.addModule(new Shadow());
      this.addModule(new ShaderMultiplier());
      this.addModule(new Arraylist2());
      this.addModule(new ShaderESP());
      this.addModule(new ESP2D());
      this.addModule(new Radar());
      this.addModule(new TargetHUD());
      this.addModule(new SessionInfo());
      this.addModule(new HUD());
      this.addModule(new GUI());
      this.addModule(new Eagle());
      this.addModule(new Animations());
      this.addModule(new AutoHypixel());
      this.addModule(new Triggerbot());
      this.addModule(new Fly());
      this.addModule(new Disabler());
      this.addModule(new Longjump());
      this.addModule(new Speed());
      this.addModule(new WorldTime());
      this.addModule(new NoSlow());
      this.addModule(new MCF());
      this.addModule(new FakeBlock());
      this.addModule(new ChestStealer());
      this.addModule(new Scaffold());
      this.addModule(new InvMove());
      this.addModule(new CustomScoreboard());
      this.addModule(new CustomChat());
      this.addModule(new InvManager());
      this.addModule(new GuiElements());
      this.addModule(new TargetStrafe());
      this.addModule(new Step());
      this.addModule(new Glint());
      this.addModule(new KeyStrokes());
      this.addModule(new CustomHitEffekt());
      this.addModule(new NoHurtCam());
      this.addModule(new TimerRange());
      this.addModule(new Backtrack());
      this.addModule(new HeldItem());
      this.addModule(new AntiFireball());
      this.addModule(new CustomHotbar());
      this.addModule(new Notifications());
      this.addModule(new Regen());
      this.addModule(new FastUse());
      this.addModule(new BedFucker());
      this.addModule(new KillMessage());
      this.addModule(new AntiVoid());
      this.addModule(new AutoTool());
      this.addModule(new Fullbright());
      this.addModule(new ChinaHat());
      this.addModule(new Strafe());
      this.addModule(new AutoEnable());
      this.addModule(new Antibot());
      this.addModule(new Nametags());
      this.addModule(new CustomCapes());
      this.addModule(new Tracers());
      this.addModule(new BedAura());
      if (Aqua.INSTANCE.ircClient.getNickname().equalsIgnoreCase("LCA_MODZ")) {
         this.addModule(new TpAura());
      }
   }

   public void addModule(Module module) {
      this.modules.add(module);
   }

   public List<Module> getModules() {
      return this.modules;
   }

   public List<Module> getModulesOrdered(Category category, boolean length, final UnicodeFontRenderer fontRenderer) {
      List<Module> mods = new ArrayList<>();

      for(Module module : this.getModules()) {
         if (module.getCategory() == category) {
            mods.add(module);
         }
      }

      if (length) {
         mods.sort(new Comparator<Module>() {
            public int compare(Module o1, Module o2) {
               return -Integer.compare(fontRenderer.getStringWidth(o1.getDisplayname()), fontRenderer.getStringWidth(o2.getDisplayname()));
            }
         });
      }

      return mods;
   }

   public List<Module> getModulesOrderedRoboto(Category category, boolean length, final UnicodeFontRenderer3 fontRenderer) {
      List<Module> mods = new ArrayList<>();

      for(Module module : this.getModules()) {
         if (module.getCategory() == category) {
            mods.add(module);
         }
      }

      if (length) {
         mods.sort(new Comparator<Module>() {
            public int compare(Module o1, Module o2) {
               return -Integer.compare(fontRenderer.getStringWidth(o1.getDisplayname()), fontRenderer.getStringWidth(o2.getDisplayname()));
            }
         });
      }

      return mods;
   }

   public List<Module> getModulesOrdered2(Category category, boolean length, final UnicodeFontRenderer5 fontRenderer) {
      List<Module> mods = new ArrayList<>();

      for(Module module : this.getModules()) {
         if (module.getCategory() == category) {
            mods.add(module);
         }
      }

      if (length) {
         mods.sort(new Comparator<Module>() {
            public int compare(Module o1, Module o2) {
               return -Integer.compare(fontRenderer.getStringWidth(o1.getDisplayname()), fontRenderer.getStringWidth(o2.getDisplayname()));
            }
         });
      }

      return mods;
   }

   public Module getModuleByName(String moduleName) {
      for(Module mod : this.modules) {
         if (mod.getName().trim().equalsIgnoreCase(moduleName) || mod.toString().trim().equalsIgnoreCase(moduleName.trim())) {
            return mod;
         }
      }

      return null;
   }

   public Module getModule(Class<? extends Module> clazz) {
      for(Module m : this.modules) {
         if (m.getClass() == clazz) {
            return m;
         }
      }

      return null;
   }
}
