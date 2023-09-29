package me.jinthium.straight.api.module;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import io.mxngo.echo.core.EventListener;
import me.jinthium.straight.api.clickgui.FontAwesomeIcons;
import me.jinthium.straight.api.clickgui.dropdown.components.ConfigPanel;
import me.jinthium.straight.api.clickgui.dropdown.components.Drag;
import me.jinthium.straight.api.config.ConfigSetting;
import me.jinthium.straight.api.config.LocalConfig;
import me.jinthium.straight.api.dragging.Dragging;
import me.jinthium.straight.api.notification.NotificationType;
import me.jinthium.straight.api.setting.Setting;
import me.jinthium.straight.api.util.MinecraftInstance;
import me.jinthium.straight.api.util.Util;
import me.jinthium.straight.impl.Client;
import me.jinthium.straight.impl.modules.visual.GlowESP;
import me.jinthium.straight.impl.settings.*;
import me.jinthium.straight.impl.settings.mode.ModuleMode;
import me.jinthium.straight.impl.settings.mode.Toggleable;
import me.jinthium.straight.impl.utils.Multithreading;
import me.jinthium.straight.impl.utils.Scroll;
import me.jinthium.straight.impl.utils.animation.Animation;
import me.jinthium.straight.impl.utils.animation.Direction;
import me.jinthium.straight.impl.utils.animation.impl.DecelerateAnimation;
import me.jinthium.straight.impl.utils.file.FileUtils;
import me.jinthium.straight.impl.utils.sound.SoundUtil;
import net.minecraft.util.EnumChatFormatting;
import org.lwjglx.input.Keyboard;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

public class Module implements MinecraftInstance, Util, EventListener {

    public static boolean sounds = false;

    @Expose
    @SerializedName("name")
    private String name;

    private String suffix;
    @Expose
    @SerializedName("enabled")
    private boolean enabled;

    private boolean expanded;
    public static int categoryCount;
    public static float allowedClickGuiHeight = 300;

    private final Category category;
    private ModuleMode<?> lastMode;
    private ModuleMode<?> currentMode;
    private NewModeSetting modeSetting;
    private final CopyOnWriteArrayList<Setting> settingsList = new CopyOnWriteArrayList<>();

    @Expose
    @SerializedName("settings")
    public ConfigSetting[] cfgSettings;

    private String author = "";

    private KeybindSetting keybind;
    private boolean extended;
    private final Animation animation = new DecelerateAnimation(250, 1);

    public Module(String name, Category category){
        this.name = name;
        this.category = category;
        this.addSettings(keybind = new KeybindSetting(0));
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public Module(String name, int funny, Category category){
        this(name, category);
    }

    public void addSettings(Setting... settings) {
        settingsList.addAll(Arrays.asList(settings));
    }

    public Category getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public String getSuffix() {
        return suffix;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public ModuleMode<?> getCurrentMode() {
        return currentMode;
    }

    public void setCurrentMode(ModuleMode<?> currentMode) {
        this.currentMode = currentMode;
    }

    public NewModeSetting getModeSetting() {
        return modeSetting;
    }

    public void setModeSetting(NewModeSetting modeSetting) {
        this.modeSetting = modeSetting;
    }

    public CopyOnWriteArrayList<Setting> getSettingsList() {
        return settingsList;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public KeybindSetting getKeybind() {
        return keybind;
    }

    public boolean isExtended() {
        return extended;
    }

    public void setExtended(boolean extended) {
        this.extended = extended;
    }

    public Animation getAnimation() {
        return animation;
    }

    public void onEnable(){
        if(this.getCategory() == Category.CONFIG){
            mc.displayGuiScreen(new ConfigPanel());
            this.enabled = false;
        }else {
            this.animation.setDirection(Direction.FORWARDS);
            Client.INSTANCE.getPubSubEventBus().subscribe(this);

            if (this.modeSetting != null && this.modeSetting.getCurrentMode() != null) {
                this.currentMode = this.modeSetting.getCurrentMode();

                if (this.currentMode != null) {
                    this.lastMode = this.currentMode;
                    Client.INSTANCE.getPubSubEventBus().subscribe(this.currentMode);

                    if (mc.thePlayer != null) {
                        this.currentMode.onEnable();
                    }
                }
            }

            if (!Objects.equals(getName(), "ClickGui"))
                Client.INSTANCE.getNotificationManager().post("Enabled " + getName(), "You enabled " + getName() + "!", NotificationType.INFO);
        }
    }


    public void onDisable(){
        if(mc.thePlayer != null)
            mc.timer.timerSpeed = 1.0f;

        this.animation.setDirection(Direction.BACKWARDS);
        Client.INSTANCE.getPubSubEventBus().unsubscribe(this);

        if (this.modeSetting != null && this.modeSetting.getCurrentMode() != null) {
            if (this.currentMode != null) {
                Client.INSTANCE.getPubSubEventBus().unsubscribe(this.currentMode);

                if (mc.thePlayer != null) {
                    this.currentMode.onDisable();
                }

                if (this.lastMode != null) {
                    Client.INSTANCE.getPubSubEventBus().unsubscribe(lastMode);
                    if (mc.thePlayer != null) {
                        this.lastMode.onDisable();
                    }
                }
            }
        }

        if(!Objects.equals(getName(), "ClickGui"))
            Client.INSTANCE.getNotificationManager().post("Disabled " + getName(), "You disabled " + getName() + "!", NotificationType.INFO);
    }

    public void toggle(){
        setEnabled(!enabled);
    }

    public void setEnabled(boolean enabled){
        if(this.enabled == enabled)
            return;

        this.enabled = enabled;

        if(sounds)
            Multithreading.runAsync(() -> SoundUtil.toggleSound(enabled));

        if(enabled)
            onEnable();
        else
            onDisable();
    }

    protected final void registerModes(ModuleMode<?>... moduleModes) {
        this.modeSetting = new NewModeSetting("Mode", Arrays.asList(moduleModes), moduleModes[0]);

        this.currentMode = this.modeSetting.getCurrentMode();
        this.lastMode = this.currentMode;

        this.addSettings(this.modeSetting);
    }

    public void updateModes() {
        this.lastMode = this.currentMode;
        this.currentMode = this.modeSetting.getCurrentMode();

        if (isEnabled() && this.lastMode != null) {
            Client.INSTANCE.getPubSubEventBus().unsubscribe(this.lastMode);

            Client.INSTANCE.getPubSubEventBus().subscribe(this.currentMode);
        }
    }


    public void setKey(int key){
        this.keybind.setCode(key);
    }

    public int getKey(){
        return this.keybind.getCode();
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

    public String getDisplayName(){
        return (suffix == null || suffix.equals("")) ? name : (name + " " + EnumChatFormatting.GRAY + suffix);
    }

    
    public StringSetting getStringSetting(String name) {
        for (Setting setting : settingsList) {
            if (setting instanceof StringSetting && setting.getName().equalsIgnoreCase(name)) {
                return (StringSetting) setting;
            }
        }
        return null;
    }
    
    public MultiBoolSetting getMultiBoolSetting(String name) {
        for (Setting setting : settingsList) {
            if (setting instanceof MultiBoolSetting && setting.getName().equalsIgnoreCase(name)) {
                return (MultiBoolSetting) setting;
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

    public enum Category {
        COMBAT(FontAwesomeIcons.Sword, "Combat"),
        MOVEMENT(FontAwesomeIcons.Walking, "Movement"),
        PLAYER(FontAwesomeIcons.User, "Player"),
        MISC(FontAwesomeIcons.Wrench, "Misc"),
        EXPLOIT(FontAwesomeIcons.ArrowsAlt, "Exploit"),
        VISUALS(FontAwesomeIcons.Eye, "Visuals"),
        GHOST(FontAwesomeIcons.Ghost, "Ghost"),
        CONFIG(FontAwesomeIcons.Folder, "Config"),
        SCRIPTS(FontAwesomeIcons.Screwdriver, "Script");

        public final int posX;
        public final boolean expanded;

        private final Scroll scroll = new Scroll();

        private final Drag drag;
        public int posY = 20;

        private final String icon, categoryName;

        Category(String icon, String categoryName){
            this.categoryName = categoryName;
            this.icon = icon;
            posX = 10 + (Module.categoryCount * 120);
            drag = new Drag(posX, posY);
            expanded = true;
            Module.categoryCount++;
        }

        public Drag getDrag() {
            return drag;
        }

        public String getIcon() {
            return icon;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public Scroll getScroll() {
            return scroll;
        }
    }
}
