package dev.tenacity.module;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import dev.tenacity.Tenacity;
import dev.tenacity.config.ConfigSetting;
import dev.tenacity.event.ListenerAdapter;
import dev.tenacity.module.impl.render.GlowESP;
import dev.tenacity.module.impl.render.NotificationsMod;
import dev.tenacity.module.settings.Setting;
import dev.tenacity.module.settings.impl.*;
import dev.tenacity.ui.notifications.NotificationManager;
import dev.tenacity.ui.notifications.NotificationType;
import dev.tenacity.utils.Utils;
import dev.tenacity.utils.animations.Animation;
import dev.tenacity.utils.animations.Direction;
import dev.tenacity.utils.animations.impl.DecelerateAnimation;
import dev.tenacity.utils.misc.Multithreading;
import lombok.Getter;
import lombok.Setter;
import org.lwjgl.input.Keyboard;
import store.intent.intentguard.annotation.Exclude;
import store.intent.intentguard.annotation.Strategy;

import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
public class Module extends ListenerAdapter implements Utils {
    @Expose
    @SerializedName("name")
    private final String name;
    private final String description;
    private final Category category;
    private final CopyOnWriteArrayList<Setting> settingsList = new CopyOnWriteArrayList<>();
    private String suffix;
    private String author = "";

    @Expose
    @SerializedName("toggled")
    protected boolean enabled;
    @Expose
    @SerializedName("settings")
    public ConfigSetting[] cfgSettings;


    private boolean expanded;
    private final Animation animation = new DecelerateAnimation(250, 1).setDirection(Direction.BACKWARDS);

    public static int categoryCount;
    public static float allowedClickGuiHeight = 300;

    private final KeybindSetting keybind = new KeybindSetting(Keyboard.KEY_NONE);

    public Module(String name, Category category, String description) {
        this.name = name;
        this.category = category;
        this.description = description;
        addSettings(keybind);
    }

    public boolean isInGame() {
        return mc.theWorld != null && mc.thePlayer != null;
    }

    public void addSettings(Setting... settings) {
        settingsList.addAll(Arrays.asList(settings));
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public void setToggled(boolean toggled) {
        this.enabled = toggled;
        if (toggled) {
            this.onEnable();
        } else {
            this.onDisable();
        }
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public void toggleSilent() {
        this.enabled = !this.enabled;
        if (this.enabled) {
            this.onEnable();
        } else {
            this.onDisable();
        }
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public void toggleSilent(boolean toggled) {
        this.enabled = toggled;
        if (this.enabled) {
            this.onEnable();
        } else {
            this.onDisable();
        }
    }

    //TODO: wtf is this code.
    @Exclude(Strategy.NAME_REMAPPING)
    public void toggle() {
        toggleSilent();
        if (NotificationsMod.toggleNotifications.isEnabled()) {
            String titleToggle = "Module toggled";
            String descriptionToggleOn = this.getName() + " was " + "§aenabled\r";
            String descriptionToggleOff = this.getName() + " was " + "§cdisabled\r";

            switch (NotificationsMod.mode.getMode()) {
                case "Default":
                    if (NotificationsMod.onlyTitle.isEnabled()) titleToggle = this.getName() + " toggled";
                    break;
                case "Jello":
                    if (this.isEnabled()) {
                        titleToggle = "Enabled Module " + this.getName();
                    } else {
                        titleToggle = "Disabled Module " + this.getName();
                    }
                    descriptionToggleOff = "";
                    descriptionToggleOn = "";
                    break;
            }
            if (enabled) {
                NotificationManager.post(NotificationType.SUCCESS, titleToggle, descriptionToggleOn);
            } else {
                NotificationManager.post(NotificationType.DISABLE, titleToggle, descriptionToggleOff);
            }
        }
    }

    public boolean hasMode() {
        return suffix != null;
    }


    public void onEnable() {
        Tenacity.INSTANCE.getEventProtocol().register(this);
    }

    public void onDisable() {
        if (this instanceof GlowESP) {
            GlowESP.fadeIn.setDirection(Direction.BACKWARDS);
            Multithreading.schedule(() -> Tenacity.INSTANCE.getEventProtocol().unregister(this), 250, TimeUnit.MILLISECONDS);
        } else {
            Tenacity.INSTANCE.getEventProtocol().unregister(this);
        }
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public void setKey(int code) {
        this.keybind.setCode(code);
    }


    @Exclude(Strategy.NAME_REMAPPING)
    public String getName() {
        return name;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public String getDescription() {
        return description;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public boolean isEnabled() {
        return enabled;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public int getKeybindCode() {
        return keybind.getCode();
    }


    @Exclude(Strategy.NAME_REMAPPING)
    public NumberSetting getNumberSetting(String name) {
        for (Setting setting : settingsList) {
            if (setting instanceof NumberSetting && setting.getName().equalsIgnoreCase(name)) {
                return (NumberSetting) setting;
            }
        }
        return null;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public BooleanSetting getBooleanSetting(String name) {
        for (Setting setting : settingsList) {
            if (setting instanceof BooleanSetting && setting.getName().equalsIgnoreCase(name)) {
                return (BooleanSetting) setting;
            }
        }
        return null;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public ModeSetting getModeSetting(String name) {
        for (Setting setting : settingsList) {
            if (setting instanceof ModeSetting && setting.getName().equalsIgnoreCase(name)) {
                return (ModeSetting) setting;
            }
        }
        return null;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public StringSetting getStringSetting(String name) {
        for (Setting setting : settingsList) {
            if (setting instanceof StringSetting && setting.getName().equalsIgnoreCase(name)) {
                return (StringSetting) setting;
            }
        }
        return null;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public MultipleBoolSetting getMultiBoolSetting(String name) {
        for (Setting setting : settingsList) {
            if (setting instanceof MultipleBoolSetting && setting.getName().equalsIgnoreCase(name)) {
                return (MultipleBoolSetting) setting;
            }
        }
        return null;
    }


    @Exclude(Strategy.NAME_REMAPPING)
    public ColorSetting getColorSetting(String name) {
        for (Setting setting : settingsList) {
            if (setting instanceof ColorSetting && setting.getName().equalsIgnoreCase(name)) {
                return (ColorSetting) setting;
            }
        }
        return null;
    }

}
