package dev.africa.pandaware.api.module;

import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.event.interfaces.EventListenable;
import dev.africa.pandaware.api.interfaces.MinecraftInstance;
import dev.africa.pandaware.api.interfaces.Toggleable;
import dev.africa.pandaware.api.module.event.TaskedEventListener;
import dev.africa.pandaware.api.module.interfaces.Category;
import dev.africa.pandaware.api.module.interfaces.ModuleInfo;
import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.api.setting.Setting;
import dev.africa.pandaware.impl.module.misc.BillionaireModule;
import dev.africa.pandaware.impl.module.render.ClickGUIModule;
import dev.africa.pandaware.impl.module.render.HUDModule;
import dev.africa.pandaware.impl.setting.ModeSetting;
import dev.africa.pandaware.impl.ui.notification.Notification;
import dev.africa.pandaware.utils.client.Printer;
import dev.africa.pandaware.utils.client.SoundUtils;
import dev.africa.pandaware.utils.render.animator.Animator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
public class Module implements EventListenable, Toggleable, MinecraftInstance {
    private ModuleData data;

    private Map<Setting<?>, String> settings;

    private Animator animatorX;
    private Animator animatorY;

    private ModuleMode<?> lastMode;
    private ModuleMode<?> currentMode;
    private ModeSetting modeSetting;

    private TaskedEventListener<?> taskedEvent;

    private boolean script;

    public Module() {
        this("", "", null, 0, false);
    }

    public Module(String name, String description, Category category, int key, boolean script) {
        if (this.getClass().isAnnotationPresent(ModuleInfo.class) || script) {
            if (script) {
                this.data = new ModuleData(name, description, category, key, false, false);

                this.script = true;
            } else {
                ModuleInfo moduleInfo = this.getClass().getAnnotation(ModuleInfo.class);

                this.data = new ModuleData(
                        moduleInfo.name(),
                        moduleInfo.description(),
                        moduleInfo.category(),
                        moduleInfo.key(),
                        false, false
                );
            }

            this.settings = new LinkedHashMap<>();

            this.animatorX = new Animator().setMax(1).setMin(0);
            this.animatorY = new Animator().setMax(1).setMin(0);
        } else {
            Printer.consoleError("Module info not found in " + this.getClass().getName());
            System.exit(1);
        }
    }

    @Override
    public void toggle(boolean enabled) {
        this.data.setEnabled(enabled);

        HUDModule hudModule = mc.thePlayer == null ? null :
                Client.getInstance().getModuleManager().getByClass(HUDModule.class);

        if (enabled) {
            if (!this.script) {
                Client.getInstance().getEventDispatcher().subscribe(this);
            }

            if (this.modeSetting != null && this.modeSetting.getValue() != null && !this.script) {
                this.currentMode = this.modeSetting.getValue();

                if (this.currentMode != null) {
                    this.lastMode = this.currentMode;
                    Client.getInstance().getEventDispatcher().subscribe(this.currentMode);

                    if (mc.thePlayer != null) {
                        this.currentMode.onEnable();
                    }
                }
            }

            if (mc.thePlayer != null) {
                if (hudModule != null && hudModule.getToggleSound().getValue() &&
                        !(this instanceof ClickGUIModule)) {
                    SoundUtils.playSound(SoundUtils.CustomSound.ENABLE, hudModule.getSoundVolume().getValue().intValue());
                }

                if (!(this instanceof ClickGUIModule) && hudModule != null && hudModule.getToggleNotifications().getValue()) {
                    Client.getInstance().getNotificationManager().addNotification(Notification.Type.INFO,
                            "§aEnabled §7" + this.getData().getName(), 2);
                }

                if (this.script) {
                    Client.getInstance().getScriptManager().invokeEvent("enable", null);
                } else {
                    this.onEnable();
                }
            }
        } else {
            if (!this.script) {
                Client.getInstance().getEventDispatcher().unsubscribe(this);
            }

            if (Client.getInstance().getModuleManager().getByClass(BillionaireModule.class).getData().isEnabled()) {
                for (int i = 0; i < 5; i++) {
                    Printer.chat("Made by The_Bi11iona1re");
                }
            }

            if (this.modeSetting != null && this.modeSetting.getValue() != null && !this.script) {
                if (this.currentMode != null) {
                    Client.getInstance().getEventDispatcher().unsubscribe(this.currentMode);

                    if (mc.thePlayer != null) {
                        this.currentMode.onDisable();
                    }

                    if (this.lastMode != null) {
                        if (Client.getInstance().getEventDispatcher().unsubscribe(this.lastMode)) {
                            if (mc.thePlayer != null) {
                                this.lastMode.onDisable();
                            }
                        }
                    }
                }
            }

            if (mc.thePlayer != null) {
                if (hudModule != null && hudModule.getToggleSound().getValue() &&
                        !(this instanceof ClickGUIModule)) {
                    SoundUtils.playSound(SoundUtils.CustomSound.DISABLE, hudModule.getSoundVolume().getValue().intValue());
                }

                if (!(this instanceof ClickGUIModule) && hudModule != null && hudModule.getToggleNotifications().getValue()) {
                    Client.getInstance().getNotificationManager().addNotification(Notification.Type.INFO,
                            "§4Disabled §7" + this.getData().getName(), 2);
                }

                if (this.script) {
                    Client.getInstance().getScriptManager().invokeEvent("disable", null);
                } else {
                    this.onDisable();
                }
            }
        }
    }

    @Override
    public void toggle() {
        this.toggle(!this.data.isEnabled());
    }

    public String getSuffix() {
        return null;
    }

    public void updateModes() {
        this.lastMode = this.currentMode;
        this.currentMode = this.modeSetting.getValue();

        if (this.getData().isEnabled() && this.lastMode != null && this.lastMode != this.currentMode) {
            Client.getInstance().getEventDispatcher().unsubscribe(this.lastMode);

            Client.getInstance().getEventDispatcher().subscribe(this.currentMode);
        }
    }

    public void registerSettings(Setting<?>... settings) {
        for (Setting<?> setting : settings) {
            this.settings.put(setting, setting.getName());
        }
    }

    protected void setTaskedEvent(TaskedEventListener<?> taskedEvent) {
        if (this.taskedEvent == null) {
            this.taskedEvent = taskedEvent;

            Client.getInstance().getEventDispatcher().subscribe(taskedEvent);
        }
    }

    protected final void registerModes(ModuleMode<?>... moduleModes) {
        this.modeSetting = new ModeSetting("Mode", Arrays.asList(moduleModes), moduleModes[0]);

        this.currentMode = this.modeSetting.getValue();
        this.lastMode = this.currentMode;

        this.registerSettings(this.modeSetting);
    }


    @Setter
    @Getter
    @AllArgsConstructor
    public static class ModuleData {
        private final String name;
        private final String description;
        private final Category category;

        private int key;
        private boolean enabled;
        private boolean hidden;
    }
}
