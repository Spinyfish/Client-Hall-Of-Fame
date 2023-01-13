package me.uncodable.srt;

import me.uncodable.srt.impl.commands.client.api.CommandManager;
import me.uncodable.srt.impl.commands.client.impl.Configuration;
import me.uncodable.srt.impl.commands.client.impl.Help;
import me.uncodable.srt.impl.commands.client.impl.Username;
import me.uncodable.srt.impl.commands.client.impl.VClip;
import me.uncodable.srt.impl.commands.metasploit.api.MetasploitCommandManager;
import me.uncodable.srt.impl.commands.metasploit.impl.MS12_020;
import me.uncodable.srt.impl.modules.api.FriendManager;
import me.uncodable.srt.impl.modules.api.KeyBindSystem;
import me.uncodable.srt.impl.modules.api.ModuleManager;
import me.uncodable.srt.impl.modules.api.settings.SettingManager;
import me.uncodable.srt.impl.modules.impl.combat.AntiBot;
import me.uncodable.srt.impl.modules.impl.combat.Aura;
import me.uncodable.srt.impl.modules.impl.combat.AutoArmor;
import me.uncodable.srt.impl.modules.impl.combat.Criticals;
import me.uncodable.srt.impl.modules.impl.combat.FastBow;
import me.uncodable.srt.impl.modules.impl.combat.QuakeBot;
import me.uncodable.srt.impl.modules.impl.combat.Regen;
import me.uncodable.srt.impl.modules.impl.combat.ReverseKnockback;
import me.uncodable.srt.impl.modules.impl.combat.Velocity;
import me.uncodable.srt.impl.modules.impl.exploits.AntiCheatDisabler;
import me.uncodable.srt.impl.modules.impl.exploits.ClientSpoof;
import me.uncodable.srt.impl.modules.impl.exploits.CraftingCarry;
import me.uncodable.srt.impl.modules.impl.exploits.InstantPickaxe;
import me.uncodable.srt.impl.modules.impl.exploits.PluginDisabler;
import me.uncodable.srt.impl.modules.impl.exploits.ServerCrasher;
import me.uncodable.srt.impl.modules.impl.fuzzers.SpeedFuzzer;
import me.uncodable.srt.impl.modules.impl.ghost.Reach;
import me.uncodable.srt.impl.modules.impl.ghost.TriggerBot;
import me.uncodable.srt.impl.modules.impl.legit.Safeguard;
import me.uncodable.srt.impl.modules.impl.miscellaneous.AutoTip;
import me.uncodable.srt.impl.modules.impl.miscellaneous.ChatBypass;
import me.uncodable.srt.impl.modules.impl.miscellaneous.ChatSpammer;
import me.uncodable.srt.impl.modules.impl.miscellaneous.ClientCommands;
import me.uncodable.srt.impl.modules.impl.miscellaneous.Derp;
import me.uncodable.srt.impl.modules.impl.miscellaneous.DiscordRPC;
import me.uncodable.srt.impl.modules.impl.miscellaneous.MagnetMode;
import me.uncodable.srt.impl.modules.impl.miscellaneous.MetasploitCommands;
import me.uncodable.srt.impl.modules.impl.miscellaneous.MinemenBoats;
import me.uncodable.srt.impl.modules.impl.miscellaneous.NetherPortalFix;
import me.uncodable.srt.impl.modules.impl.miscellaneous.PostCoordinates;
import me.uncodable.srt.impl.modules.impl.miscellaneous.TimeChecker;
import me.uncodable.srt.impl.modules.impl.miscellaneous.Timer;
import me.uncodable.srt.impl.modules.impl.movement.AirJump;
import me.uncodable.srt.impl.modules.impl.movement.AntiVoid;
import me.uncodable.srt.impl.modules.impl.movement.FastLadder;
import me.uncodable.srt.impl.modules.impl.movement.Flight;
import me.uncodable.srt.impl.modules.impl.movement.HighJump;
import me.uncodable.srt.impl.modules.impl.movement.InventoryMove;
import me.uncodable.srt.impl.modules.impl.movement.LongJump;
import me.uncodable.srt.impl.modules.impl.movement.NoFall;
import me.uncodable.srt.impl.modules.impl.movement.NoSlowdown;
import me.uncodable.srt.impl.modules.impl.movement.Speed;
import me.uncodable.srt.impl.modules.impl.movement.Spider;
import me.uncodable.srt.impl.modules.impl.movement.Sprint;
import me.uncodable.srt.impl.modules.impl.movement.Step;
import me.uncodable.srt.impl.modules.impl.movement.Unstuck;
import me.uncodable.srt.impl.modules.impl.player.ChestStealer;
import me.uncodable.srt.impl.modules.impl.player.FastRespawn;
import me.uncodable.srt.impl.modules.impl.player.FastUse;
import me.uncodable.srt.impl.modules.impl.player.InventoryManager;
import me.uncodable.srt.impl.modules.impl.player.NoRotate;
import me.uncodable.srt.impl.modules.impl.player.NoTeleport;
import me.uncodable.srt.impl.modules.impl.visual.Ambience;
import me.uncodable.srt.impl.modules.impl.visual.Animations;
import me.uncodable.srt.impl.modules.impl.visual.AntiInvisibles;
import me.uncodable.srt.impl.modules.impl.visual.ClickGUI;
import me.uncodable.srt.impl.modules.impl.visual.ClickGUIDeprecated;
import me.uncodable.srt.impl.modules.impl.visual.ESP;
import me.uncodable.srt.impl.modules.impl.visual.EnchantmentGlint;
import me.uncodable.srt.impl.modules.impl.visual.FullBright;
import me.uncodable.srt.impl.modules.impl.visual.Hud;
import me.uncodable.srt.impl.modules.impl.visual.Keystrokes;
import me.uncodable.srt.impl.modules.impl.visual.LowFire;
import me.uncodable.srt.impl.modules.impl.visual.NoEffectRender;
import me.uncodable.srt.impl.modules.impl.visual.NoHurtDisplay;
import me.uncodable.srt.impl.modules.impl.visual.NoPumpkinOverlay;
import me.uncodable.srt.impl.modules.impl.visual.NoStrike;
import me.uncodable.srt.impl.modules.impl.visual.Weather;
import me.uncodable.srt.impl.modules.impl.world.BlockReach;
import me.uncodable.srt.impl.modules.impl.world.FastPlace;
import me.uncodable.srt.impl.utils.FileUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import store.intent.intentguard.annotation.Bootstrap;
import store.intent.intentguard.annotation.Native;

@Native
public enum Ries {
   INSTANCE;

   private static final String NAME = "SRT";
   private static final String BUILD = "DEV";
   private CommandManager commandManager;
   private ModuleManager moduleManager;
   private SettingManager settingManager;
   private KeyBindSystem keyBindSystem;
   private FriendManager friendManager;
   private MetasploitCommandManager metasploitCommandManager;

   public void msg(String message) {
      if (Minecraft.getMinecraft().thePlayer != null) {
         Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("§5[§d§lSRT" + "§5] ".concat(message).replace(" ", " §7")));
      }
   }

   public void console(String message) {
      System.out.printf("[SRT] %s%n", message);
   }

   public void initialize() {
      this.keyBindSystem = new KeyBindSystem();
      this.settingManager = new SettingManager();
      this.commandManager = new CommandManager();
      this.friendManager = new FriendManager();
      this.moduleManager = new ModuleManager();
      this.metasploitCommandManager = new MetasploitCommandManager();
   }

   @Native
   @Bootstrap
   public static void intentInitialization() {
      INSTANCE.getModuleManager()
         .addModules(
            new ClickGUIDeprecated(54, false),
            new Hud(0, true),
            new Aura(0, false),
            new AirJump(0, false),
            new HighJump(0, false),
            new BlockReach(0, false),
            new Reach(0, false),
            new Ambience(0, false),
            new AutoArmor(0, false),
            new AntiCheatDisabler(0, false),
            new ReverseKnockback(0, false),
            new QuakeBot(0, false),
            new EnchantmentGlint(0, false),
            new Keystrokes(0, false),
            new Safeguard(0, false),
            new FullBright(0, false),
            new Timer(0, false),
            new Velocity(0, false),
            new Weather(0, false),
            new InventoryMove(0, false),
            new NoFall(0, false),
            new TriggerBot(0, false),
            new FastPlace(0, false),
            new ChestStealer(0, false),
            new NoSlowdown(0, false),
            new Regen(0, false),
            new FastUse(0, false),
            new Sprint(0, false),
            new LowFire(0, false),
            new FastBow(0, false),
            new Unstuck(0, false),
            new ClientCommands(0, false),
            new FastLadder(0, false),
            new ServerCrasher(0, false),
            new LongJump(0, false),
            new Speed(0, false),
            new Flight(0, false),
            new InventoryManager(0, false),
            new AntiInvisibles(0, false),
            new NoEffectRender(0, false),
            new NetherPortalFix(0, false),
            new NoHurtDisplay(0, false),
            new ClientSpoof(0, false),
            new MagnetMode(0, false),
            new Animations(0, false),
            new Spider(0, false),
            new TimeChecker(0, false),
            new PostCoordinates(0, false),
            new MinemenBoats(0, false),
            new NoPumpkinOverlay(0, false),
            new FastRespawn(0, false),
            new ChatSpammer(0, false),
            new CraftingCarry(0, false),
            new ChatBypass(0, false),
            new Derp(0, false),
            new AntiVoid(0, false),
            new Criticals(0, false),
            new DiscordRPC(0, false),
            new PluginDisabler(0, false),
            new ESP(0, false),
            new Step(0, false),
            new MetasploitCommands(0, false),
            new AutoTip(0, false),
            new InstantPickaxe(0, false),
            new NoStrike(0, false),
            new NoRotate(0, false),
            new NoTeleport(0, false),
            new AntiBot(0, false),
            new SpeedFuzzer(0, false),
            new ClickGUI(0, false)
         );
      INSTANCE.getModuleManager().finishInitialization();
      INSTANCE.getCommandManager().addCommands(new VClip(), new Username(), new Configuration(), new Help());
      INSTANCE.getMetasploitCommandManager().addCommands(new MS12_020());

      try {
         Thread.sleep(250L);
      } catch (Exception var1) {
         INSTANCE.console("Failed busy wait.");
         var1.printStackTrace();
      }

      FileUtils.init();
   }

   public String getName() {
      return "SRT";
   }

   public String getBuild() {
      return "DEV";
   }

   public CommandManager getCommandManager() {
      return this.commandManager;
   }

   public ModuleManager getModuleManager() {
      return this.moduleManager;
   }

   public SettingManager getSettingManager() {
      return this.settingManager;
   }

   public KeyBindSystem getKeyBindSystem() {
      return this.keyBindSystem;
   }

   public FriendManager getFriendManager() {
      return this.friendManager;
   }

   public MetasploitCommandManager getMetasploitCommandManager() {
      return this.metasploitCommandManager;
   }
}
