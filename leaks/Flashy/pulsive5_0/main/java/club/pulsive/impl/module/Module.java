package club.pulsive.impl.module;

import club.pulsive.api.event.eventBus.core.EventPriority;
import club.pulsive.api.main.Pulsive;
import club.pulsive.api.minecraft.MinecraftUtil;
import club.pulsive.impl.property.Property;
import club.pulsive.impl.property.PropertyRepository;
import club.pulsive.impl.util.client.TimerUtil;
import club.pulsive.impl.util.render.Translate;
import club.pulsive.impl.util.render.animations.Animation;
import club.pulsive.impl.util.render.animations.Direction;
import club.pulsive.impl.util.render.animations.impl.EaseInOutRect;
import club.pulsive.impl.util.render.animations.impl.MainAnimations;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StringUtils;

import java.util.Objects;
import java.util.function.Supplier;

@Getter
@Setter
public abstract class Module implements MinecraftUtil {

    protected static final PropertyRepository propertyRepository = new PropertyRepository();

    private final ModuleInfo annotation = getClass().getAnnotation(ModuleInfo.class);
    private final String name = annotation.name();
    private final Category category = annotation.category();
    private String renderName = annotation.renderName();
    private final String description = annotation.description();
    private final String[] aliases = annotation.aliases();
    private Translate translate = new Translate(0, 0);
    private int keyBind = annotation.keybind();
    private final Animation moduleAnimation = new MainAnimations(300, 1, Direction.FORWARDS);
    private final TimerUtil timerUtil = new TimerUtil();

    private boolean toggled, hidden, hasSuffix;
    private String suffix;

    public static PropertyRepository propertyRepository() {
        return propertyRepository;
    }

    public void init() {
        propertyRepository.register(this);
    }

    public void addValueChangeListener(Property<?> mode, Supplier<Boolean> pascal) {
        setSuffix(mode.getValue().toString());
        mode.addValueChange((oldValue, value) -> setSuffix(mode.getValue().toString()));
    }
    public Translate getTranslate() {
        return translate;
    }

    public void addValueChangeListener(Property<?> mode) {
        addValueChangeListener(mode, () -> true);
    }

    public void setSuffix(String suffix) {
        hasSuffix = true;
        this.suffix = suffix.replace("_", " ");
    }


    public void toggle() {
        setToggled(!toggled);
    }

    public void setToggled(boolean toggled) {
        if (toggled) {
            if (!this.toggled) {  onEnable(); }
        } else {
            if (this.toggled) onDisable();
        }
        
        this.toggled = toggled;
    }

    public void onEnable() {
        moduleAnimation.setDirection(Direction.FORWARDS);

        if(Objects.equals(getName(), "AutoPotion"))
            Pulsive.INSTANCE.getEventBus().register(this, EventPriority.HIGHEST);
        else
            Pulsive.INSTANCE.getEventBus().register(this);
    }

    public void onDisable() {
        if(mc.thePlayer != null) mc.timer.timerSpeed = 1.0f;
        moduleAnimation.setDirection(Direction.BACKWARDS);
        if (timerUtil.hasElapsed(400)) {
            Pulsive.INSTANCE.getEventBus().unregister(this);
            timerUtil.reset();
        }
    }

    public String getRenderName() {
        return Objects.equals(renderName, "") ? hasSuffix ? name + EnumChatFormatting.GRAY +  " " + suffix : name : hasSuffix ? renderName + EnumChatFormatting.GRAY + " " + suffix : renderName;
    }
}
