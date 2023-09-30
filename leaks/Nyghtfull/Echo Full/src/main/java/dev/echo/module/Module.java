package dev.echo.module;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import dev.echo.Echo;
import dev.echo.config.ConfigSetting;
import dev.echo.module.impl.render.GlowESP;
import dev.echo.module.impl.render.NotificationsMod;
import dev.echo.module.settings.Setting;
import dev.echo.module.settings.impl.*;
import dev.echo.ui.notifications.NotificationManager;
import dev.echo.ui.notifications.NotificationType;
import dev.echo.utils.Utils;
import dev.echo.utils.animations.Animation;
import dev.echo.utils.animations.Direction;
import dev.echo.utils.animations.impl.DecelerateAnimation;
import dev.echo.utils.misc.Multithreading;
import lombok.Getter;
import lombok.Setter;
import org.lwjgl.input.Keyboard;


import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
public class Module implements Utils {
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

   
    public void setToggled(boolean toggled) {
        this.enabled = toggled;
        if (toggled) {
            this.onEnable();
        } else {
            this.onDisable();
        }
    }

   
    public void toggleSilent() {
        this.enabled = !this.enabled;
        if (this.enabled) {
            this.onEnable();
        } else {
            this.onDisable();
        }
    }

   
    public void toggleSilent(boolean toggled) {
        this.enabled = toggled;
        if (this.enabled) {
            this.onEnable();
        } else {
            this.onDisable();
        }
    }

    //TODO: wtf is this code.
   
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
                        titleToggle = "Enabled Module " + this.getName() + ".";
                    } else {
                        titleToggle = "Disabled Module " + this.getName() + ".";
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
        Echo.INSTANCE.getEventProtocol().subscribe(this);
    }

    public void onDisable() {
        if (this instanceof GlowESP) {
            GlowESP.fadeIn.setDirection(Direction.BACKWARDS);
            Multithreading.schedule(() -> Echo.INSTANCE.getEventProtocol().unsubscribe(this), 250, TimeUnit.MILLISECONDS);
        } else {
            Echo.INSTANCE.getEventProtocol().unsubscribe(this);
        }
    }

   
    public void setKey(int code) {
        this.keybind.setCode(code);
    }


   
    public String getName() {
        return name;
    }

   
    public String getDescription() {
        return description;
    }

   
    public boolean isEnabled() {
        return enabled;
    }

   
    public int getKeybindCode() {
        return keybind.getCode();
    }


   
    public NumberSetting getNumberSetting(String name) {
        for (Setting setting : settingsList) {
            if (setting instanceof NumberSetting && setting.getName().equalsIgnoreCase(name)) {
                return (NumberSetting) setting;
            }
        }
        return null;
    }

   
    public BooleanSetting getBooleanSetting(String name) {
        for (Setting setting : settingsList) {
            if (setting instanceof BooleanSetting && setting.getName().equalsIgnoreCase(name)) {
                return (BooleanSetting) setting;
            }
        }
        return null;
    }

   
    public ModeSetting getModeSetting(String name) {
        for (Setting setting : settingsList) {
            if (setting instanceof ModeSetting && setting.getName().equalsIgnoreCase(name)) {
                return (ModeSetting) setting;
            }
        }
        return null;
    }

   
    public StringSetting getStringSetting(String name) {
        for (Setting setting : settingsList) {
            if (setting instanceof StringSetting && setting.getName().equalsIgnoreCase(name)) {
                return (StringSetting) setting;
            }
        }
        return null;
    }

   
    public MultipleBoolSetting getMultiBoolSetting(String name) {
        for (Setting setting : settingsList) {
            if (setting instanceof MultipleBoolSetting && setting.getName().equalsIgnoreCase(name)) {
                return (MultipleBoolSetting) setting;
            }
        }
        return null;
    }


   
    public ColorSetting getColorSetting(String name) {
        for (Setting setting : settingsList) {
            if (setting instanceof ColorSetting && setting.getName().equalsIgnoreCase(name)) {
                return (ColorSetting) setting;
            }
        }
        return null;
    }

}
