/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.misc.protection;

import dev.quickprotect.NativeObf;
import java.lang.reflect.Field;
import java.util.Arrays;
import net.minecraft.client.Minecraft;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.config.ConfigSystem;
import wtf.monsoon.api.manager.CommandManager;
import wtf.monsoon.api.manager.FriendManager;
import wtf.monsoon.api.manager.ModuleManager;
import wtf.monsoon.api.manager.ProcessorManager;
import wtf.monsoon.api.setting.Exclude;
import wtf.monsoon.api.setting.ModeProcessor;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.sextoy.SexToyManager;
import wtf.monsoon.api.util.obj.MonsoonPlayerObject;
import wtf.monsoon.impl.command.BindCommand;
import wtf.monsoon.impl.command.ConfigCommand;
import wtf.monsoon.impl.command.DefaultsCommand;
import wtf.monsoon.impl.command.DoxCommand;
import wtf.monsoon.impl.command.DuplicateCommand;
import wtf.monsoon.impl.command.FriendCommand;
import wtf.monsoon.impl.command.NiggerCommand;
import wtf.monsoon.impl.command.SayCommand;
import wtf.monsoon.impl.command.SettingCommand;
import wtf.monsoon.impl.command.SpamCommand;
import wtf.monsoon.impl.command.ToggleCommand;
import wtf.monsoon.impl.module.combat.Aura;
import wtf.monsoon.impl.module.combat.AutoPot;
import wtf.monsoon.impl.module.combat.Criticals;
import wtf.monsoon.impl.module.combat.TargetStrafe;
import wtf.monsoon.impl.module.combat.Velocity;
import wtf.monsoon.impl.module.exploit.Disabler;
import wtf.monsoon.impl.module.exploit.NoC03;
import wtf.monsoon.impl.module.exploit.PingSpoof;
import wtf.monsoon.impl.module.exploit.ResetVL;
import wtf.monsoon.impl.module.exploit.TimerModule;
import wtf.monsoon.impl.module.ghost.AutoBridger;
import wtf.monsoon.impl.module.ghost.AutoClicker;
import wtf.monsoon.impl.module.ghost.ClickDelayRemover;
import wtf.monsoon.impl.module.ghost.HitBox;
import wtf.monsoon.impl.module.ghost.Reach;
import wtf.monsoon.impl.module.hud.Arrows;
import wtf.monsoon.impl.module.hud.CrosshairCustomizer;
import wtf.monsoon.impl.module.hud.HUD;
import wtf.monsoon.impl.module.hud.HUDArrayList;
import wtf.monsoon.impl.module.hud.Hotbar;
import wtf.monsoon.impl.module.hud.InventoryDisplay;
import wtf.monsoon.impl.module.hud.NotificationsModule;
import wtf.monsoon.impl.module.hud.SessionInfo;
import wtf.monsoon.impl.module.hud.Speedometer;
import wtf.monsoon.impl.module.hud.TargetHUD;
import wtf.monsoon.impl.module.movement.Flight;
import wtf.monsoon.impl.module.movement.HighJump;
import wtf.monsoon.impl.module.movement.InventoryMove;
import wtf.monsoon.impl.module.movement.LongJump;
import wtf.monsoon.impl.module.movement.NoSlow;
import wtf.monsoon.impl.module.movement.Speed;
import wtf.monsoon.impl.module.movement.Sprint;
import wtf.monsoon.impl.module.movement.Step;
import wtf.monsoon.impl.module.player.AntiAim;
import wtf.monsoon.impl.module.player.AntiVoid;
import wtf.monsoon.impl.module.player.AutoArmor;
import wtf.monsoon.impl.module.player.AutoHypixel;
import wtf.monsoon.impl.module.player.AutoTool;
import wtf.monsoon.impl.module.player.ChatSpammer;
import wtf.monsoon.impl.module.player.ChestStealer;
import wtf.monsoon.impl.module.player.InventoryManager;
import wtf.monsoon.impl.module.player.LovenseIntegration;
import wtf.monsoon.impl.module.player.NoFall;
import wtf.monsoon.impl.module.player.NoRotate;
import wtf.monsoon.impl.module.player.Phase;
import wtf.monsoon.impl.module.player.Scaffold;
import wtf.monsoon.impl.module.player.ServerSideStrafe;
import wtf.monsoon.impl.module.visual.Accent;
import wtf.monsoon.impl.module.visual.Ambience;
import wtf.monsoon.impl.module.visual.BlockAnimations;
import wtf.monsoon.impl.module.visual.Blur;
import wtf.monsoon.impl.module.visual.CharacterRenderer;
import wtf.monsoon.impl.module.visual.ChestESP;
import wtf.monsoon.impl.module.visual.ChinaHat;
import wtf.monsoon.impl.module.visual.ClickGUI;
import wtf.monsoon.impl.module.visual.ESP;
import wtf.monsoon.impl.module.visual.EnchantColour;
import wtf.monsoon.impl.module.visual.HitMarkers;
import wtf.monsoon.impl.module.visual.Nametags;
import wtf.monsoon.impl.module.visual.NoRender;
import wtf.monsoon.impl.module.visual.ShaderESP;
import wtf.monsoon.impl.module.visual.SuperheroFX;
import wtf.monsoon.impl.module.visual.Tracers;
import wtf.monsoon.impl.module.visual.Trajectories;
import wtf.monsoon.impl.module.visual.ViewModel;
import wtf.monsoon.impl.processor.BlinkProcessor;
import wtf.monsoon.impl.processor.LagbackProcessor;
import wtf.monsoon.impl.ui.character.CharacterManager;
import wtf.monsoon.impl.ui.menu.MainMenu;
import wtf.monsoon.impl.ui.panel.PanelGUI;
import wtf.monsoon.impl.ui.windowgui.WindowGUI;
import wtf.monsoon.misc.vantage.VantageCommunity;

public class ProtectedInitialization {
    private boolean doneLoading = false;

    @NativeObf
    public void start() {
        if (!Wrapper.loggedIn) {
            return;
        }
        if (!Wrapper.getMonsoon().getNetworkManager().isConnected()) {
            return;
        }
        this.doneLoading = false;
        Wrapper.getMonsoon().setModuleManager(new ModuleManager());
        Wrapper.getEventBus().subscribe(Wrapper.getMonsoon().getModuleManager());
        Wrapper.setSexToyManager(new SexToyManager());
        Wrapper.getSexToyManager().init();
        Wrapper.getEventBus().subscribe(Wrapper.getSexToyManager());
        try {
            Wrapper.getLogger().info("Loading scripts...");
            Wrapper.getMonsoon().getScriptLoader().loadScripts();
        }
        catch (Exception e) {
            Wrapper.getLogger().error("Couldn't load scripts.");
            e.printStackTrace();
        }
        Wrapper.getMonsoon().setPlayer(new MonsoonPlayerObject());
        Wrapper.getEventBus().subscribe(Wrapper.getMonsoon().getPlayer());
        Wrapper.getMonsoon().getModuleManager().putModule(Aura.class, new Aura());
        Wrapper.getMonsoon().getModuleManager().putModule(TargetStrafe.class, new TargetStrafe());
        Wrapper.getMonsoon().getModuleManager().putModule(Velocity.class, new Velocity());
        Wrapper.getMonsoon().getModuleManager().putModule(Criticals.class, new Criticals());
        Wrapper.getMonsoon().getModuleManager().putModule(AutoPot.class, new AutoPot());
        Wrapper.getMonsoon().getModuleManager().putModule(Flight.class, new Flight());
        Wrapper.getMonsoon().getModuleManager().putModule(HighJump.class, new HighJump());
        Wrapper.getMonsoon().getModuleManager().putModule(InventoryMove.class, new InventoryMove());
        Wrapper.getMonsoon().getModuleManager().putModule(LongJump.class, new LongJump());
        Wrapper.getMonsoon().getModuleManager().putModule(NoSlow.class, new NoSlow());
        Wrapper.getMonsoon().getModuleManager().putModule(Speed.class, new Speed());
        Wrapper.getMonsoon().getModuleManager().putModule(Sprint.class, new Sprint());
        Wrapper.getMonsoon().getModuleManager().putModule(Step.class, new Step());
        Wrapper.getMonsoon().getModuleManager().putModule(AutoArmor.class, new AutoArmor());
        Wrapper.getMonsoon().getModuleManager().putModule(AutoHypixel.class, new AutoHypixel());
        Wrapper.getMonsoon().getModuleManager().putModule(ChestStealer.class, new ChestStealer());
        Wrapper.getMonsoon().getModuleManager().putModule(InventoryManager.class, new InventoryManager());
        Wrapper.getMonsoon().getModuleManager().putModule(LovenseIntegration.class, new LovenseIntegration());
        Wrapper.getMonsoon().getModuleManager().putModule(NoFall.class, new NoFall());
        Wrapper.getMonsoon().getModuleManager().putModule(Phase.class, new Phase());
        Wrapper.getMonsoon().getModuleManager().putModule(Scaffold.class, new Scaffold());
        Wrapper.getMonsoon().getModuleManager().putModule(ServerSideStrafe.class, new ServerSideStrafe());
        Wrapper.getMonsoon().getModuleManager().putModule(AntiVoid.class, new AntiVoid());
        Wrapper.getMonsoon().getModuleManager().putModule(NoRotate.class, new NoRotate());
        Wrapper.getMonsoon().getModuleManager().putModule(AutoTool.class, new AutoTool());
        Wrapper.getMonsoon().getModuleManager().putModule(AntiAim.class, new AntiAim());
        Wrapper.getMonsoon().getModuleManager().putModule(ChatSpammer.class, new ChatSpammer());
        Wrapper.getMonsoon().getModuleManager().putModule(Accent.class, new Accent());
        Wrapper.getMonsoon().getModuleManager().putModule(Ambience.class, new Ambience());
        Wrapper.getMonsoon().getModuleManager().putModule(BlockAnimations.class, new BlockAnimations());
        Wrapper.getMonsoon().getModuleManager().putModule(Blur.class, new Blur());
        Wrapper.getMonsoon().getModuleManager().putModule(ChestESP.class, new ChestESP());
        Wrapper.getMonsoon().getModuleManager().putModule(ChinaHat.class, new ChinaHat());
        Wrapper.getMonsoon().getModuleManager().putModule(ClickGUI.class, new ClickGUI());
        Wrapper.getMonsoon().getModuleManager().putModule(EnchantColour.class, new EnchantColour());
        Wrapper.getMonsoon().getModuleManager().putModule(ESP.class, new ESP());
        Wrapper.getMonsoon().getModuleManager().putModule(Nametags.class, new Nametags());
        Wrapper.getMonsoon().getModuleManager().putModule(NoRender.class, new NoRender());
        Wrapper.getMonsoon().getModuleManager().putModule(ShaderESP.class, new ShaderESP());
        Wrapper.getMonsoon().getModuleManager().putModule(SuperheroFX.class, new SuperheroFX());
        Wrapper.getMonsoon().getModuleManager().putModule(Tracers.class, new Tracers());
        Wrapper.getMonsoon().getModuleManager().putModule(Trajectories.class, new Trajectories());
        Wrapper.getMonsoon().getModuleManager().putModule(ViewModel.class, new ViewModel());
        Wrapper.getMonsoon().getModuleManager().putModule(CharacterRenderer.class, new CharacterRenderer());
        Wrapper.getMonsoon().getModuleManager().putModule(HitMarkers.class, new HitMarkers());
        Wrapper.getMonsoon().getModuleManager().putModule(Disabler.class, new Disabler());
        Wrapper.getMonsoon().getModuleManager().putModule(NoC03.class, new NoC03());
        Wrapper.getMonsoon().getModuleManager().putModule(PingSpoof.class, new PingSpoof());
        Wrapper.getMonsoon().getModuleManager().putModule(TimerModule.class, new TimerModule());
        Wrapper.getMonsoon().getModuleManager().putModule(ResetVL.class, new ResetVL());
        Wrapper.getMonsoon().getModuleManager().putModule(AutoBridger.class, new AutoBridger());
        Wrapper.getMonsoon().getModuleManager().putModule(AutoClicker.class, new AutoClicker());
        Wrapper.getMonsoon().getModuleManager().putModule(ClickDelayRemover.class, new ClickDelayRemover());
        Wrapper.getMonsoon().getModuleManager().putModule(HitBox.class, new HitBox());
        Wrapper.getMonsoon().getModuleManager().putModule(Reach.class, new Reach());
        Wrapper.getMonsoon().getModuleManager().putModule(HUDArrayList.class, new HUDArrayList());
        Wrapper.getMonsoon().getModuleManager().putModule(Arrows.class, new Arrows());
        Wrapper.getMonsoon().getModuleManager().putModule(Hotbar.class, new Hotbar());
        Wrapper.getMonsoon().getModuleManager().putModule(HUD.class, new HUD());
        Wrapper.getMonsoon().getModuleManager().putModule(InventoryDisplay.class, new InventoryDisplay());
        Wrapper.getMonsoon().getModuleManager().putModule(NotificationsModule.class, new NotificationsModule());
        Wrapper.getMonsoon().getModuleManager().putModule(SessionInfo.class, new SessionInfo());
        Wrapper.getMonsoon().getModuleManager().putModule(Speedometer.class, new Speedometer());
        Wrapper.getMonsoon().getModuleManager().putModule(TargetHUD.class, new TargetHUD());
        Wrapper.getMonsoon().getModuleManager().putModule(CrosshairCustomizer.class, new CrosshairCustomizer());
        Wrapper.getMonsoon().getModuleManager().getModules().forEach(module -> {
            Arrays.stream(module.getClass().getDeclaredFields()).filter(field -> Setting.class.isAssignableFrom(field.getType())).forEach(field -> {
                field.setAccessible(true);
                try {
                    Setting setting = (Setting)field.get(module);
                    if (!field.isAnnotationPresent(Exclude.class) && setting.getParent() == null) {
                        module.getSettings().add(setting);
                    }
                    if (setting.getValue() instanceof Enum) {
                        for (Enum en : (Enum[])((Enum)setting.getValue()).getClass().getEnumConstants()) {
                            for (Field f : en.getClass().getDeclaredFields()) {
                                if (!f.getType().isAssignableFrom(ModeProcessor.class)) continue;
                                f.setAccessible(true);
                                ModeProcessor value = (ModeProcessor)f.get(en);
                                Arrays.asList(value.getModeSettings()).forEach(mode -> {
                                    module.getSettings().add((Setting<?>)mode);
                                    mode.visibleWhen(() -> setting.getValue().equals(en));
                                });
                            }
                        }
                    }
                }
                catch (IllegalAccessException | IllegalArgumentException e) {
                    e.printStackTrace();
                }
            });
            module.getSettings().add(module.getKey());
        });
        Wrapper.getMonsoon().setCommandManager(new CommandManager());
        Wrapper.getMonsoon().getCommandManager().getCommands().add(new SayCommand());
        Wrapper.getMonsoon().getCommandManager().getCommands().add(new ConfigCommand());
        Wrapper.getMonsoon().getCommandManager().getCommands().add(new NiggerCommand());
        Wrapper.getMonsoon().getCommandManager().getCommands().add(new ToggleCommand());
        Wrapper.getMonsoon().getCommandManager().getCommands().add(new FriendCommand());
        Wrapper.getMonsoon().getCommandManager().getCommands().add(new SettingCommand());
        Wrapper.getMonsoon().getCommandManager().getCommands().add(new DefaultsCommand());
        Wrapper.getMonsoon().getCommandManager().getCommands().add(new DoxCommand());
        Wrapper.getMonsoon().getCommandManager().getCommands().add(new DuplicateCommand());
        Wrapper.getMonsoon().getCommandManager().getCommands().add(new BindCommand());
        Wrapper.getMonsoon().getCommandManager().getCommands().add(new SpamCommand());
        Wrapper.getMonsoon().setFriendManager(new FriendManager());
        Wrapper.getMonsoon().setWindowGUI(new WindowGUI());
        Wrapper.getMonsoon().setConfigSystem(new ConfigSystem());
        Wrapper.getMonsoon().getConfigSystem().load("current", true);
        Wrapper.getMonsoon().getConfigSystem().loadAlts(Wrapper.getMonsoon().getAltManager());
        Wrapper.getMonsoon().setPanelGUI(new PanelGUI());
        Wrapper.getMonsoon().setCharacterManager(new CharacterManager());
        Wrapper.getMonsoon().setProcessorManager(new ProcessorManager());
        Wrapper.getMonsoon().getProcessorManager().addProcessor(LagbackProcessor.class, new LagbackProcessor());
        Wrapper.getMonsoon().getProcessorManager().addProcessor(BlinkProcessor.class, new BlinkProcessor());
        Wrapper.getMonsoon().setCommunity(new VantageCommunity());
        Wrapper.getEventBus().subscribe(Wrapper.getMonsoon().getCommunity());
        this.doneLoading = true;
    }

    @NativeObf
    public void checkIfDoneLoading() {
        if (this.doneLoading) {
            Minecraft.getMinecraft().displayGuiScreen(new MainMenu());
        }
    }
}

