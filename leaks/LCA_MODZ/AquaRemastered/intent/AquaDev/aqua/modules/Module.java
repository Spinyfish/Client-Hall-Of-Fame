package intent.AquaDev.aqua.modules;

import events.Event;
import intent.AquaDev.aqua.Aqua;
import intent.AquaDev.aqua.fr.lavache.anime.Animate;
import intent.AquaDev.aqua.gui.novolineOld.themesScreen.ThemeScreen;
import intent.AquaDev.aqua.modules.combat.Killaura;
import intent.AquaDev.aqua.modules.combat.Velocity;
import intent.AquaDev.aqua.modules.misc.AutoHypixel;
import intent.AquaDev.aqua.modules.misc.Disabler;
import intent.AquaDev.aqua.modules.movement.Longjump;
import intent.AquaDev.aqua.modules.movement.NoSlow;
import intent.AquaDev.aqua.modules.movement.Speed;
import intent.AquaDev.aqua.modules.player.ChestStealer;
import intent.AquaDev.aqua.modules.player.InvManager;
import intent.AquaDev.aqua.modules.visual.Animations;
import intent.AquaDev.aqua.modules.visual.Blur;
import intent.AquaDev.aqua.modules.visual.CustomChat;
import intent.AquaDev.aqua.modules.visual.CustomScoreboard;
import intent.AquaDev.aqua.modules.visual.ESP;
import intent.AquaDev.aqua.modules.visual.HUD;
import intent.AquaDev.aqua.modules.visual.SessionInfo;
import intent.AquaDev.aqua.modules.visual.Shadow;
import intent.AquaDev.aqua.modules.visual.TargetHUD;
import intent.AquaDev.aqua.modules.visual.WorldTime;
import intent.AquaDev.aqua.notifications.Notification;
import intent.AquaDev.aqua.notifications.NotificationManager;
import intent.AquaDev.aqua.utils.SoundUtil;
import java.awt.Color;
import java.util.Random;
import net.minecraft.client.Minecraft;

public class Module {
   private String name;
   private String displayname;
   private Category category;
   public boolean toggeld;
   public Module.Type type;
   public Animate anim = new Animate();
   public Animate anim2 = new Animate();
   private int keyBind;
   public int heroColor;
   public int oneTimeColor3;
   private boolean open;
   public static Minecraft mc = Minecraft.getMinecraft();

   public String getDisplayname() {
      return this.displayname;
   }

   public void onEvent(Event event) {
   }

   public void setDisplayname(String displayname) {
      this.displayname = displayname;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public int getKeyBind() {
      return this.keyBind;
   }

   public void setKeyBind(int keyBind) {
      this.keyBind = keyBind;
   }

   public Category getCategory() {
      return this.category;
   }

   public void setCategory(Category category) {
      this.category = category;
   }

   public boolean isToggled() {
      return this.toggeld;
   }

   public void toggle() {
      this.setState(!this.isToggled());
   }

   public void toggleOpen() {
      this.open = !this.open;
   }

   public void setState(boolean state) {
      if (state != this.toggeld) {
         if (state) {
            NotificationManager.addNotificationToQueue(
               new Notification("Module", "§a" + this.getName() + " Enabled", 1000L, Notification.NotificationType.INFO)
            );
            if (ThemeScreen.themeJello) {
               SoundUtil.play(SoundUtil.toggleOnSound);
            }

            this.onEnable();
         } else {
            NotificationManager.addNotificationToQueue(
               new Notification("Module", "§c" + this.getName() + " Disabled", 1000L, Notification.NotificationType.INFO)
            );
            if (ThemeScreen.themeJello) {
               SoundUtil.play(SoundUtil.toggleOffSound);
            }

            this.anim.reset();
            this.anim2.reset();
            this.onDisable();
         }
      }

      this.toggeld = state;
   }

   public void setup() {
   }

   public void onEnable() {
   }

   public void onDisable() {
   }

   public Module(String name, Module.Type type, String displayname, int keyBind, Category category) {
      this.type = type;
      this.name = name;
      this.displayname = displayname;
      this.category = category;
      this.keyBind = keyBind;
      this.heroColor = getColor(new Random().nextInt(255), new Random().nextInt(255), new Random().nextInt(255), 255);
      this.oneTimeColor3 = getColor(new Random().nextInt(191), new Random().nextInt(191), new Random().nextInt(191), 255);
      this.setup();
   }

   public String getMode() {
      String mode = "";
      if (this instanceof Disabler) {
         mode = "" + Aqua.setmgr.getSetting("DisablerModes").getCurrentMode();
      }

      if (this instanceof TargetHUD) {
         mode = "" + Aqua.setmgr.getSetting("TargetHUDTargetHUDMode").getCurrentMode();
      }

      if (this instanceof Velocity) {
         mode = "" + Aqua.setmgr.getSetting("VelocityMode").getCurrentMode();
      }

      if (this instanceof SessionInfo) {
         mode = "" + Aqua.setmgr.getSetting("SessionInfoInfoMode").getCurrentMode();
      }

      if (this instanceof AutoHypixel) {
         mode = "" + Aqua.setmgr.getSetting("AutoHypixelMode").getCurrentMode();
      }

      if (this instanceof Animations) {
         mode = "" + Aqua.setmgr.getSetting("AnimationsMode").getCurrentMode();
      }

      if (this instanceof NoSlow) {
         mode = "Watchdog";
      }

      if (this instanceof CustomChat) {
         mode = "" + Aqua.setmgr.getSetting("CustomChatMode").getCurrentMode();
      }

      if (this instanceof Speed) {
         mode = "" + Aqua.setmgr.getSetting("SpeedMode").getCurrentMode();
      }

      if (this instanceof Longjump) {
         mode = "" + Aqua.setmgr.getSetting("LongjumpMode").getCurrentMode();
      }

      if (this instanceof CustomScoreboard) {
         mode = "" + Aqua.setmgr.getSetting("CustomScoreboardMode").getCurrentMode();
      }

      if (this instanceof ChestStealer) {
         mode = "" + Math.round(Double.parseDouble("" + Aqua.setmgr.getSetting("ChestStealerDelay").getValue())) + ".0";
      }

      if (this instanceof InvManager) {
         mode = "" + Math.round(Double.parseDouble("" + Aqua.setmgr.getSetting("InvManagerDelay").getValue())) + ".0";
      }

      if (this instanceof ESP) {
         mode = "" + Aqua.setmgr.getSetting("ESPMode").getValue();
      }

      if (this instanceof WorldTime) {
         mode = "" + Math.round(Double.parseDouble("" + Aqua.setmgr.getSetting("WorldTimeTime").getValue())) + ".0";
      }

      if (this instanceof Blur) {
         mode = "Sigma " + Math.round(Double.parseDouble("" + Aqua.setmgr.getSetting("BlurSigma").getValue())) + ".0";
      }

      if (this instanceof Shadow) {
         mode = "Sigma " + Math.round(Double.parseDouble("" + Aqua.setmgr.getSetting("ShadowSigma").getValue())) + ".0";
      }

      if (this instanceof Killaura) {
         mode = "" + Math.round(Double.parseDouble("" + Aqua.setmgr.getSetting("KillauraRange").getValue())) + ".0";
      }

      if (this instanceof HUD) {
         mode = "" + Aqua.setmgr.getSetting("HUDWatermarks").getValue();
      }

      return mode;
   }

   public Module.Type getType() {
      return this.type;
   }

   public void setType(Module.Type type) {
      this.type = type;
   }

   public static int getColor(int r, int g, int b, int a) {
      return new Color(r, g, b, a).getRGB();
   }

   public boolean isOpen() {
      return this.open;
   }

   public static enum Type {
      Combat(Color.decode("#E74C3C").getRGB(), 'j', "a"),
      Movement(Color.decode("#2ECC71").getRGB(), 'b', "b"),
      Visual(Color.decode("#3700CE").getRGB(), 'g', "g"),
      Player(Color.decode("#8E44AD").getRGB(), 'k', "c"),
      Overlay(new Color(255, 0, 25, 255).getRGB(), 'g', "g"),
      Misc(new Color(255, 0, 25, 255).getRGB(), 'g', "g"),
      World(Color.decode("#3498DB").getRGB(), 'h', "f");

      private int color;
      private char icon;
      private final String iconPenis;

      private Type(int color, char icon, String iconPenis) {
         this.color = color;
         this.icon = icon;
         this.iconPenis = iconPenis;
      }

      public int getColor() {
         return this.color;
      }

      public char getIcon() {
         return this.icon;
      }

      public final String getIconPenis() {
         return this.iconPenis;
      }
   }
}
