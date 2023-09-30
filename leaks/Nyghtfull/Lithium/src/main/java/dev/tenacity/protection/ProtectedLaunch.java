package dev.tenacity.protection;

import de.florianmichael.viamcp.ViaMCP;
import dev.tenacity.Tenacity;
import dev.tenacity.commands.CommandHandler;
import dev.tenacity.commands.impl.*;
import dev.tenacity.config.ConfigManager;
import dev.tenacity.config.DragManager;
import dev.tenacity.intent.api.account.IntentAccount;
import dev.tenacity.module.BackgroundProcess;
import dev.tenacity.module.Module;
import dev.tenacity.module.ModuleCollection;
import dev.tenacity.module.impl.combat.*;
import dev.tenacity.module.impl.exploit.*;
import dev.tenacity.module.impl.misc.*;
import dev.tenacity.module.impl.movement.*;
import dev.tenacity.module.impl.player.*;
import dev.tenacity.module.impl.render.*;
import dev.tenacity.ui.altmanager.GuiAltManager;
import dev.tenacity.ui.altmanager.helpers.KingGenApi;
import dev.tenacity.utils.render.EntityCulling;
import dev.tenacity.utils.render.Theme;
import dev.tenacity.utils.server.PingerUtils;
import net.minecraft.client.Minecraft;
import store.intent.intentguard.annotation.Bootstrap;
import store.intent.intentguard.annotation.Native;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;

@Native
public class ProtectedLaunch {

    private static final HashMap<Object, Module> modules = new HashMap<>();

    @Native
    @Bootstrap
    public static void start() {
        IntentAccount account = new IntentAccount();
        account.username = "Username";
        account.client_uid = 8;

        Tenacity.INSTANCE.setIntentAccount(account);
        Tenacity.INSTANCE.setModuleCollection(new ModuleCollection());

        // Combat
        modules.put(AutoPlay.class, new AutoPlay());
        modules.put(KillAura.class, new KillAura());
        modules.put(Velocity.class, new Velocity());
        modules.put(Criticals.class, new Criticals());
        modules.put(AutoGapple.class, new AutoGapple());
        modules.put(AutoHead.class, new AutoHead());
        modules.put(AutoPotion.class, new AutoPotion());
        modules.put(AntiBot.class, new AntiBot());
        modules.put(Teams.class, new Teams());
        modules.put(ClickTP.class, new ClickTP());

        // Exploit
        modules.put(Disabler.class, new Disabler());
        modules.put(AntiInvis.class, new AntiInvis());
        modules.put(Regen.class, new Regen());
        modules.put(AntiAura.class, new AntiAura());
        modules.put(ResetVL.class, new ResetVL());
        modules.put(Crasher.class, new Crasher());
        modules.put(Damage.class, new Damage());
        modules.put(TickRange.class, new TickRange());
        modules.put(BackTrack.class, new BackTrack());
        modules.put(GodMode.class, new GodMode());

        // Misc
        modules.put(AntiDesync.class, new AntiDesync());
        modules.put(AntiTabComplete.class, new AntiTabComplete());
        modules.put(Spammer.class, new Spammer());
        modules.put(AntiFreeze.class, new AntiFreeze());
        modules.put(LightningTracker.class, new LightningTracker());
        modules.put(AntiCheat.class, new AntiCheat());
        modules.put(MurderDetector.class, new MurderDetector());
        modules.put(AutoHypixel.class, new AutoHypixel());
        modules.put(NoRotate.class, new NoRotate());
        modules.put(AutoRespawn.class, new AutoRespawn());
        modules.put(MCF.class, new MCF());
        modules.put(AutoLogin.class, new AutoLogin());
        modules.put(Killsults.class, new Killsults());
        modules.put(Sniper.class, new Sniper());
        modules.put(FlagCheck.class, new FlagCheck());
        modules.put(AutoRoast.class, new AutoRoast());

        // Movement
        modules.put(Sprint.class, new Sprint());
        modules.put(KeepSprint.class, new KeepSprint());
        modules.put(Speed.class, new Speed());
        modules.put(Flight.class, new Flight());
        modules.put(LongJump.class, new LongJump());
        modules.put(Step.class, new Step());
        modules.put(InventoryMove.class, new InventoryMove());
        modules.put(Jesus.class, new Jesus());
        modules.put(Spider.class, new Spider());
        modules.put(NoSlow.class, new NoSlow());
        modules.put(SafeWalk.class, new SafeWalk());
        modules.put(Scaffold.class, new Scaffold());

        // Player
        modules.put(CustomScaffold.class, new CustomScaffold());
        modules.put(ChestStealer.class, new ChestStealer());
        modules.put(InvManager.class, new InvManager());
        modules.put(AutoArmor.class, new AutoArmor());
        modules.put(SpeedMine.class, new SpeedMine());
        modules.put(Blink.class, new Blink());
        modules.put(NoFall.class, new NoFall());
        modules.put(Timer.class, new Timer());
        modules.put(Freecam.class, new Freecam());
        modules.put(FastPlace.class, new FastPlace());
        modules.put(AutoTool.class, new AutoTool());
        modules.put(AntiVoid.class, new AntiVoid());

        // Render
        modules.put(ArrayListMod.class, new ArrayListMod());
        modules.put(NotificationsMod.class, new NotificationsMod());
        modules.put(ScoreboardMod.class, new ScoreboardMod());
        modules.put(HUDMod.class, new HUDMod());
        modules.put(ClickGUIMod.class, new ClickGUIMod());
        modules.put(Radar.class, new Radar());
        modules.put(Breaker.class, new Breaker());
        modules.put(Animations.class, new Animations());
        modules.put(SpotifyMod.class, new SpotifyMod());
        modules.put(Ambience.class, new Ambience());
        modules.put(ChinaHat.class, new ChinaHat());
        modules.put(GlowESP.class, new GlowESP());
        modules.put(Brightness.class, new Brightness());
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
        modules.put(PlayerList.class, new PlayerList());
        modules.put(JumpCircle.class, new JumpCircle());
        modules.put(Hotbar.class, new Hotbar());
        modules.put(Chams.class, new Chams());

        Tenacity.INSTANCE.getModuleCollection().setModules(modules);

        Theme.init();

        Tenacity.INSTANCE.setPingerUtils(new PingerUtils());


        CommandHandler commandHandler = new CommandHandler();
        commandHandler.commands.addAll(Arrays.asList(
                new FriendCommand(), new CopyNameCommand(), new BindCommand(), new UnbindCommand(),
                new ScriptCommand(), new SettingCommand(), new HelpCommand(),
                new VClipCommand(), new ClearBindsCommand(), new ClearConfigCommand(),
                new LoadCommand(), new ToggleCommand()
        ));
        Tenacity.INSTANCE.setCommandHandler(commandHandler);
        Tenacity.INSTANCE.getEventProtocol().register(new BackgroundProcess());

        Tenacity.INSTANCE.setConfigManager(new ConfigManager());
        ConfigManager.defaultConfig = new File(Minecraft.getMinecraft().mcDataDir + "/Tenacity/Config.json");
        Tenacity.INSTANCE.getConfigManager().collectConfigs();
        if (ConfigManager.defaultConfig.exists()) {
            Tenacity.INSTANCE.getConfigManager().loadConfig(Tenacity.INSTANCE.getConfigManager().readConfigData(ConfigManager.defaultConfig.toPath()), true);
        }

        DragManager.loadDragData();

        Tenacity.INSTANCE.setAltManager(new GuiAltManager());

        Tenacity.INSTANCE.setKingGenApi(new KingGenApi());

        try {
            ViaMCP.create();
            ViaMCP.INSTANCE.initAsyncSlider(); // For top left aligned slider
        } catch (Exception e) {
            e.printStackTrace();
        }
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
