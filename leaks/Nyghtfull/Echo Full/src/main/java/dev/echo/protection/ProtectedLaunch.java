package dev.echo.protection;

import dev.echo.Echo;
import dev.echo.commands.CommandHandler;
import dev.echo.commands.impl.*;
import dev.echo.config.ConfigManager;
import dev.echo.config.DragManager;
import dev.echo.other.intent.cloud.Cloud;
import dev.echo.module.BackgroundProcess;
import dev.echo.module.Module;
import dev.echo.module.ModuleCollection;
import dev.echo.module.impl.combat.*;
import dev.echo.module.impl.exploit.*;
import dev.echo.module.impl.misc.*;
import dev.echo.module.impl.movement.*;
import dev.echo.module.impl.player.Timer;
import dev.echo.module.impl.player.*;
import dev.echo.module.impl.render.*;
import dev.echo.module.impl.render.killeffects.KillEffects;
import dev.echo.module.impl.render.wings.DragonWings;
import dev.echo.ui.altmanager.GuiAltManager;
import dev.echo.ui.altmanager.helpers.KingGenApi;
import dev.echo.utils.render.EntityCulling;
import dev.echo.utils.render.Theme;
import dev.echo.utils.server.PingerUtils;
import net.minecraft.client.Minecraft;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;

public class ProtectedLaunch {

    private static final HashMap<Object, Module> modules = new HashMap<>();

    public static void start() {
        Echo.INSTANCE.setModuleCollection(new ModuleCollection());
        modules.put(Test.class, new Test());
        // Combat
        modules.put(KillAura.class, new KillAura());
        modules.put(TPAura.class, new TPAura());
        modules.put(Velocity.class, new Velocity());
        modules.put(Criticals.class, new Criticals());
        modules.put(TargetStrafe.class, new TargetStrafe());
        modules.put(AutoPot.class, new AutoPot());
        modules.put(FastBow.class, new FastBow());
        modules.put(KeepSprint.class, new KeepSprint());
        modules.put(ExtraKB.class, new ExtraKB());
        modules.put(BackTrack.class, new BackTrack());

        // Exploit
        modules.put(Disabler.class, new Disabler());
        modules.put(PingSpoof.class, new PingSpoof());
        modules.put(AntiInvis.class, new AntiInvis());
        modules.put(Regen.class, new Regen());
        modules.put(Crasher.class, new Crasher());

        // Misc
        modules.put(AntiTabComplete.class, new AntiTabComplete());
        modules.put(Spammer.class, new Spammer());
        modules.put(LightningTracker.class, new LightningTracker());
        modules.put(AutoHypixel.class, new AutoHypixel());
        modules.put(NoRotate.class, new NoRotate());
        modules.put(MCF.class, new MCF());
        modules.put(AutoAuthenticate.class, new AutoAuthenticate());
        modules.put(Killsults.class, new Killsults());
        modules.put(Sniper.class, new Sniper());

        // Movement
        modules.put(Sprint.class, new Sprint());
        modules.put(MoveCorrection.class, new MoveCorrection());
        modules.put(ClickTP.class, new ClickTP());
        modules.put(Scaffold.class, new Scaffold());
        modules.put(Speed.class, new Speed());
        modules.put(Flight.class, new Flight());
        modules.put(LongJump.class, new LongJump());
        modules.put(Step.class, new Step());
        modules.put(FastLadder.class, new FastLadder());
        modules.put(InventoryMove.class, new InventoryMove());
        modules.put(Spider.class, new Spider());

        // Player
        modules.put(ChestStealer.class, new ChestStealer());
        modules.put(InvManager.class, new InvManager());
        modules.put(AutoArmor.class, new AutoArmor());
        modules.put(Blink.class, new Blink());
        modules.put(NoFall.class, new NoFall());
        modules.put(Timer.class, new Timer());
        modules.put(Freecam.class, new Freecam());
        modules.put(Breaker.class, new Breaker());
        modules.put(FastPlace.class, new FastPlace());
        modules.put(SafeWalk.class, new SafeWalk());
        modules.put(NoSlow.class, new NoSlow());
        modules.put(AutoTool.class, new AutoTool());
        modules.put(AntiVoid.class, new AntiVoid());
        modules.put(KillEffects.class, new KillEffects());

        // Render
        modules.put(ArrayListMod.class, new ArrayListMod());
        modules.put(NotificationsMod.class, new NotificationsMod());
        modules.put(ScoreboardMod.class, new ScoreboardMod());
        modules.put(HUDMod.class, new HUDMod());
        modules.put(ClickGUIMod.class, new ClickGUIMod());
        modules.put(Radar.class, new Radar());
        modules.put(Animations.class, new Animations());
        modules.put(Ambience.class, new Ambience());
        modules.put(ChinaHat.class, new ChinaHat());
        modules.put(Particles.class, new Particles());
        modules.put(GlowESP.class, new GlowESP());
        modules.put(FullBright.class, new FullBright());
        modules.put(ESP2D.class, new ESP2D());
        modules.put(PostProcessing.class, new PostProcessing());
        modules.put(Statistics.class, new Statistics());
        modules.put(TargetHUDMod.class, new TargetHUDMod());
        modules.put(Glint.class, new Glint());
        modules.put(Breadcrumbs.class, new Breadcrumbs());
        modules.put(Streamer.class, new Streamer());
        modules.put(NoHurtCam.class, new NoHurtCam());
        modules.put(Keystrokes.class, new Keystrokes());
        modules.put(ItemPhysics.class, new ItemPhysics());
        modules.put(XRay.class, new XRay());
        modules.put(EntityCulling.class, new EntityCulling());
        modules.put(DragonWings.class, new DragonWings());
        modules.put(PlayerList.class, new PlayerList());
        modules.put(JumpCircle.class, new JumpCircle());
        modules.put(CustomModel.class, new CustomModel());
        modules.put(EntityEffects.class, new EntityEffects());
        modules.put(Chams.class, new Chams());
        modules.put(BrightPlayers.class, new BrightPlayers());
        modules.put(Nametags.class, new Nametags());
        modules.put(Snake.class, new Snake());

        Echo.INSTANCE.getModuleCollection().setModules(modules);

        Theme.init();

        Echo.INSTANCE.setPingerUtils(new PingerUtils());

        CommandHandler commandHandler = new CommandHandler();
        commandHandler.commands.addAll(Arrays.asList(
                new FriendCommand(), new CopyNameCommand(), new BindCommand(), new UnbindCommand(),
                new ScriptCommand(), new SettingCommand(), new HelpCommand(),
                new VClipCommand(), new ClearBindsCommand(), new ClearConfigCommand(),
                new LoadCommand(), new ToggleCommand()
        ));
        Echo.INSTANCE.setCommandHandler(commandHandler);
        Echo.INSTANCE.getEventProtocol().subscribe(new BackgroundProcess());

        Echo.INSTANCE.setConfigManager(new ConfigManager());
        ConfigManager.defaultConfig = new File(Minecraft.getMinecraft().mcDataDir + "/Echo/Config.json");
        Echo.INSTANCE.getConfigManager().collectConfigs();
        if (ConfigManager.defaultConfig.exists()) {
            Echo.INSTANCE.getConfigManager().loadConfig(Echo.INSTANCE.getConfigManager().readConfigData(ConfigManager.defaultConfig.toPath()), true);
        }

        DragManager.loadDragData();

        Echo.INSTANCE.setAltManager(new GuiAltManager());

        String apiKey = "Balls";


        Cloud.setApiKey(apiKey);


        Echo.INSTANCE.kingGenApi = new KingGenApi();
    }

    private static void downloadDiscordImages() {
    }

    @SafeVarargs
    private static void addModules(Class<? extends Module>... classes) {
        for (Class<? extends Module> moduleClass : classes) {
            try {
                modules.put(moduleClass, moduleClass.newInstance());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

}
