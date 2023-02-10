/**
 * @project Myth
 * @author Skush/Duzey
 * @at 05.08.2022
 */
package dev.myth.api.feature;

import dev.myth.api.interfaces.IMethods;
import dev.myth.api.setting.Setting;
import dev.myth.api.utils.render.animation.Animation;
import dev.myth.api.utils.render.animation.Easings;
import dev.myth.features.display.HUDFeature;
import dev.myth.main.ClientMain;
import dev.myth.managers.FeatureManager;
import lombok.Getter;
import lombok.Setter;
import org.lwjgl.input.Keyboard;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class Feature implements IMethods {
    private final Info info = getClass().getAnnotation(Info.class);

    @Getter private final String name = info.name();
    @Getter @Setter private String description = info.description();
    @Getter @Setter private int keyBind = info.keyBind();
    @Getter private final Category category = info.category();
    @Getter @Setter private boolean enabled;
    @Getter private final ArrayList<Setting<?>> settings = new ArrayList<>();

    @Getter private final Animation arrayListAnimation = new Animation().setValue(1);

    private HUDFeature hudFeature;

    /** Feature Stuff */
    public void toggle() {
        enabled = !enabled;
        if (this.enabled) {
            this.onEnable();
        } else {
            this.onDisable();
        }

        if(hudFeature == null) {
            hudFeature = ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(HUDFeature.class);
        }

//        if(hudFeature != null && hudFeature.arrayListAnimation.getValue()) arrayListAnimation.animate(enabled ? 0 : 1, 300, enabled ? Easings.BACK_OUT : Easings.BACK_IN);
        if(hudFeature != null && hudFeature.arrayListAnimation.getValue()) arrayListAnimation.animate(enabled ? 0 : 1, 100, enabled ? Easings.QUAD_OUT : Easings.QUAD_IN);
        else arrayListAnimation.setValue(0);
    }
    public void onDisable() {
        ClientMain.INSTANCE.eventBus.unsubscribe(this);
    }
    public void onEnable() {
        ClientMain.INSTANCE.eventBus.subscribe(this);
    }
    public void reflectSettings() {
        if (getClass().getDeclaredFields().length == 0) return;

        for (Field field : getClass().getDeclaredFields()) {
            try {
                boolean isAssignable = field.get(this) instanceof Setting;
                if (isAssignable) {
                    if (!field.isAccessible()) {
                        field.setAccessible(true);
                    }
                    settings.add((Setting<?>) field.get(this));
                }
            } catch (IllegalAccessException ignored) {}
        }
    }
    public <T extends Setting<?>> T getSettingByName(String name) {
        return (T) settings.stream().filter(setting -> setting.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
    public String getSuffix() {
        return "";
    }
    public String getDisplayName() {
        String name = getName();
        if(!getSuffix().isEmpty()) name += " §7" + getSuffix();
        return name;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Info {
        String name();
        String description() default "";
        int keyBind() default Keyboard.KEY_NONE;
        Category category();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Debug {}

    public enum Category {
        COMBAT("Combat"),
        MOVEMENT("Movement"),
        PLAYER("Player"),
        VISUAL("Visual"),
        EXPLOIT("Exploit"),
        DISPLAY("Display");

        @Getter private final String name;
        Category(final String name) {
            this.name = name;
        }
    }
}
