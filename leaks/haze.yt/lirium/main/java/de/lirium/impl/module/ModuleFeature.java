package de.lirium.impl.module;

import de.lirium.Client;
import de.lirium.base.animation.Animation;
import de.lirium.base.event.EventListener;
import de.lirium.base.feature.Feature;
import de.lirium.impl.module.impl.ui.ClickGuiFeature;
import de.lirium.util.interfaces.IMinecraft;
import de.lirium.util.random.RandomUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringJoiner;

@Getter
@Setter
public class ModuleFeature extends EventListener implements Feature {

    public static final List<EntityLivingBase> targets = new ArrayList<>();

    private final ModuleFeature instance;
    private final String name, description;
    private final Category category;
    private String suffix;
    protected boolean enabled, bypassing;
    public final boolean wip;
    private int keyBind;

    public Animation slideAnimation;

    public final int randomColor;

    private static final Random random = new Random();

    public ModuleFeature() {
        final Info info = this.getClass().getAnnotation(Info.class);
        this.instance = this;
        this.name = info.name();
        this.description = info.description();
        this.category = info.category();
        this.keyBind = info.keyBind();
        this.slideAnimation = new Animation();
        this.randomColor = random.nextInt(0xffffff + 1);
        this.wip = info.wip();
    }

    public void setSuffix(String... elements) {
        final StringJoiner joiner = new StringJoiner(", ");
        for(String element : elements) {
            joiner.add(element);
        }
        suffix = joiner.toString();
    }

    public void setEnabled(boolean enabled) {
        if (enabled != this.enabled) {
            this.enabled = enabled;
            try {
                if (enabled) onEnable();
                else onDisable();
            } catch (Exception exception) {
                exception.printStackTrace();
            }

            if (mc.world != null && this.getClass() != ClickGuiFeature.class)
                mc.world.playSound(getPlayer(), getX(), getY(), getZ(), SoundEvents.UI_BUTTON_CLICK, SoundCategory.MASTER, 1F, enabled ? 2F : 1.5F);
            //TODO: why would we need this lol?
            //if (mc.player != null && Client.INSTANCE.getProfileManager() != null)
            //    Client.INSTANCE.getProfileManager().get().save();
        }
    }

    public String getDisplayName() {
        return getDisplayName(false, false);
    }

    public String getDisplayName(boolean tm, boolean tails) {
        if (suffix == null || suffix.isEmpty())
            return (tails ? "▉" : "") + name + (tm ? "§7\u2122" : "");
        return (tails ? "▉" : "") + name + (tm ? "§7\u2122" : "") + " §7" + suffix;
    }

    public void onEnable() {
        Client.INSTANCE.getEventBus().register(instance);
    }

    public void onDisable() {
        Client.INSTANCE.getEventBus().unregister(instance);
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Info {
        String name();

        Category category();

        int keyBind() default 0;

        String description();

        boolean wip() default false;

        boolean visual() default false;
    }

    @AllArgsConstructor
    @Getter
    public enum Category {
            COMBAT("Combat"), MOVEMENT("Movement"), PLAYER("Player"), MISCELLANEOUS("Miscellaneous"), VISUAL("Visual"), UI("UI");
        final String display;
    }
}