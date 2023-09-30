/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.surge.animation.Animation
 *  me.surge.animation.Easing
 */
package wtf.monsoon.api.module;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import me.surge.animation.Animation;
import me.surge.animation.Easing;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.setting.Bind;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.obj.MonsoonPlayerObject;
import wtf.monsoon.impl.module.annotation.DefaultBind;
import wtf.monsoon.impl.ui.notification.NotificationType;

public class Module
implements Cloneable {
    private String name;
    private final String description;
    private boolean enabled;
    private boolean visible = true;
    private Setting<Bind> key = new Setting<Bind>("Keybinding", new Bind(0, Bind.Device.KEYBOARD)).describedBy("The bind used to toggle the module");
    private Category category;
    private final Animation animation = new Animation(() -> Float.valueOf(250.0f), false, () -> Easing.CUBIC_IN_OUT);
    private final Animation toggleHudAnimation = new Animation(() -> Float.valueOf(250.0f), false, () -> Easing.CUBIC_IN_OUT);
    private final List<Setting<?>> settings = new ArrayList();
    private Supplier<String> metadata = () -> "";
    public Minecraft mc = Wrapper.getMinecraft();
    public MonsoonPlayerObject player = Wrapper.getMonsoon().getPlayer();
    private boolean isDuplicate = false;

    public Module(String name, String description, Category category) {
        this.name = name;
        this.description = description;
        this.category = category;
        if (this.getClass().isAnnotationPresent(DefaultBind.class)) {
            DefaultBind bindData = this.getClass().getAnnotation(DefaultBind.class);
            this.getKey().setValue(new Bind(bindData.code(), bindData.device()));
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        this.getAnimation().setState(enabled);
        if (enabled) {
            this.onEnable();
        } else {
            this.onDisable();
        }
    }

    public void setEnabledSilent(boolean enabled) {
        this.enabled = enabled;
        this.getAnimation().setState(enabled);
        if (enabled) {
            Wrapper.getEventBus().subscribe(this);
        } else {
            Wrapper.getEventBus().unsubscribe(this);
        }
    }

    public void toggle() {
        this.setEnabled(!this.enabled);
    }

    public void onEnable() {
        Wrapper.getEventBus().subscribe(this);
        Wrapper.getNotifManager().notify(NotificationType.YES, "Enabled Module", this.name);
    }

    public void onDisable() {
        Wrapper.getEventBus().unsubscribe(this);
        Wrapper.getNotifManager().notify(NotificationType.NO, "Disabled Module", this.name);
    }

    public String getDisplayName() {
        return this.getName() + (!this.getMetaData().equals("") ? " " + (Object)((Object)EnumChatFormatting.GRAY) + this.getMetaData() : "");
    }

    public String getMetaData() {
        return this.metadata.get();
    }

    public Module clone() throws CloneNotSupportedException {
        return (Module)super.clone();
    }

    public List<Setting<?>> getSettingHierarchy() {
        ArrayList hierarchy = new ArrayList();
        for (Setting<?> setting : this.settings) {
            hierarchy.add(setting);
            hierarchy.addAll(setting.getHierarchy());
        }
        return hierarchy;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public Setting<Bind> getKey() {
        return this.key;
    }

    public void setKey(Setting<Bind> key) {
        this.key = key;
    }

    public Category getCategory() {
        return this.category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Animation getAnimation() {
        return this.animation;
    }

    public Animation getToggleHudAnimation() {
        return this.toggleHudAnimation;
    }

    public List<Setting<?>> getSettings() {
        return this.settings;
    }

    public void setMetadata(Supplier<String> metadata) {
        this.metadata = metadata;
    }

    public boolean isDuplicate() {
        return this.isDuplicate;
    }

    public void setDuplicate(boolean isDuplicate) {
        this.isDuplicate = isDuplicate;
    }
}

