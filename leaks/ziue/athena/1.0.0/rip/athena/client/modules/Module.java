package rip.athena.client.modules;

import net.minecraft.client.*;
import rip.athena.client.gui.hud.*;
import rip.athena.client.utils.input.*;
import java.util.*;
import rip.athena.client.*;
import rip.athena.client.config.*;
import rip.athena.client.config.types.*;
import java.lang.reflect.*;
import java.lang.annotation.*;

public class Module
{
    public static Minecraft mc;
    private final Category category;
    private final String name;
    private String icon;
    private boolean toggled;
    private List<ConfigEntry> configEntries;
    private List<HUDElement> hudElements;
    private int keyBind;
    private BindType bindType;
    
    public Module(final String name, final Category category, final String icon) {
        this.icon = "";
        this.keyBind = 0;
        this.bindType = BindType.TOGGLE;
        this.name = name;
        this.category = category;
        this.icon = icon;
        this.configEntries = new ArrayList<ConfigEntry>();
        this.hudElements = new ArrayList<HUDElement>();
        this.processFields();
    }
    
    public Module(final String name, final Category category) {
        this.icon = "";
        this.keyBind = 0;
        this.bindType = BindType.TOGGLE;
        this.name = name;
        this.category = category;
        this.configEntries = new ArrayList<ConfigEntry>();
        this.hudElements = new ArrayList<HUDElement>();
        this.processFields();
    }
    
    public void toggle() {
        if (!(this.toggled = !this.toggled)) {
            this.onDisable();
        }
        else {
            this.onEnable();
        }
    }
    
    public void setEnabled(final boolean enabled) {
        this.toggled = enabled;
        if (enabled) {
            this.onEnable();
        }
        else {
            this.onDisable();
        }
    }
    
    public void onEnable() {
        if (!Athena.INSTANCE.getEventBus().isRegistered(this)) {
            Athena.INSTANCE.getEventBus().register(this);
        }
    }
    
    public void onDisable() {
        if (Athena.INSTANCE.getEventBus().isRegistered(this)) {
            Athena.INSTANCE.getEventBus().unregister(this);
        }
    }
    
    public List<ConfigEntry> getEntries() {
        return this.configEntries;
    }
    
    private void processFields() {
        for (final Field field : this.getClass().getDeclaredFields()) {
            final Type fieldType = field.getType();
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            Object annotation = null;
            if ((annotation = this.getAnnotation(field, ConfigValue.Boolean.class)) != null) {
                final ConfigValue.Boolean boolAnnotation = (ConfigValue.Boolean)annotation;
                if (boolAnnotation.enabled()) {
                    this.configEntries.add(new BooleanEntry(field, boolAnnotation.name(), boolAnnotation.description(), boolAnnotation.visible()));
                }
            }
            else if ((annotation = this.getAnnotation(field, ConfigValue.Integer.class)) != null) {
                final ConfigValue.Integer intAnnotation = (ConfigValue.Integer)annotation;
                if (intAnnotation.enabled()) {
                    this.configEntries.add(new IntEntry(field, intAnnotation.min(), intAnnotation.max(), intAnnotation.name(), intAnnotation.description(), intAnnotation.visible()));
                }
            }
            else if ((annotation = this.getAnnotation(field, ConfigValue.Float.class)) != null) {
                final ConfigValue.Float floatAnnotation = (ConfigValue.Float)annotation;
                if (floatAnnotation.enabled()) {
                    this.configEntries.add(new FloatEntry(field, floatAnnotation.min(), floatAnnotation.max(), floatAnnotation.name(), floatAnnotation.description(), floatAnnotation.visible()));
                }
            }
            else if ((annotation = this.getAnnotation(field, ConfigValue.Double.class)) != null) {
                final ConfigValue.Double doubleAnnotation = (ConfigValue.Double)annotation;
                if (doubleAnnotation.enabled()) {
                    this.configEntries.add(new DoubleEntry(field, doubleAnnotation.min(), doubleAnnotation.max(), doubleAnnotation.name(), doubleAnnotation.description(), doubleAnnotation.visible()));
                }
            }
            else if ((annotation = this.getAnnotation(field, ConfigValue.Color.class)) != null) {
                final ConfigValue.Color colorAnnotation = (ConfigValue.Color)annotation;
                if (colorAnnotation.enabled()) {
                    this.configEntries.add(new ColorEntry(field, colorAnnotation.name(), colorAnnotation.description(), colorAnnotation.visible()));
                }
            }
            else if ((annotation = this.getAnnotation(field, ConfigValue.List.class)) != null) {
                final ConfigValue.List listAnnotation = (ConfigValue.List)annotation;
                if (listAnnotation.enabled()) {
                    this.configEntries.add(new ListEntry(this, field, listAnnotation.values(), listAnnotation.name(), listAnnotation.description(), listAnnotation.visible()));
                }
            }
            else if ((annotation = this.getAnnotation(field, ConfigValue.Keybind.class)) != null) {
                final ConfigValue.Keybind keybindAnnotation = (ConfigValue.Keybind)annotation;
                if (keybindAnnotation.enabled()) {
                    this.configEntries.add(new IntEntry(field, keybindAnnotation.name(), keybindAnnotation.description(), keybindAnnotation.visible()));
                }
            }
            else if ((annotation = this.getAnnotation(field, ConfigValue.Text.class)) != null) {
                final ConfigValue.Text textAnnotation = (ConfigValue.Text)annotation;
                if (textAnnotation.enabled()) {
                    this.configEntries.add(new StringEntry(field, textAnnotation.name(), textAnnotation.description(), textAnnotation.visible()));
                }
            }
        }
    }
    
    private <T extends Annotation> T getAnnotation(final Field field, final Class<T> annotationClass) {
        if (field.isAnnotationPresent(annotationClass)) {
            return field.getAnnotation(annotationClass);
        }
        return null;
    }
    
    public void addHUD(final HUDElement element) {
        element.setParent(this);
        this.getHUDElements().add(element);
    }
    
    public void addHUDDirectly(final HUDElement element) {
        element.setParent(this);
        Athena.INSTANCE.getHudManager().getElements().add(element);
    }
    
    public List<HUDElement> getHUDElements() {
        return this.hudElements;
    }
    
    public boolean isBound() {
        return this.keyBind != 0;
    }
    
    public Category getCategory() {
        return this.category;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getIcon() {
        return this.icon;
    }
    
    public boolean isToggled() {
        return this.toggled;
    }
    
    public List<ConfigEntry> getConfigEntries() {
        return this.configEntries;
    }
    
    public int getKeyBind() {
        return this.keyBind;
    }
    
    public BindType getBindType() {
        return this.bindType;
    }
    
    public void setIcon(final String icon) {
        this.icon = icon;
    }
    
    public void setToggled(final boolean toggled) {
        this.toggled = toggled;
    }
    
    public void setConfigEntries(final List<ConfigEntry> configEntries) {
        this.configEntries = configEntries;
    }
    
    public void setHudElements(final List<HUDElement> hudElements) {
        this.hudElements = hudElements;
    }
    
    public void setKeyBind(final int keyBind) {
        this.keyBind = keyBind;
    }
    
    public void setBindType(final BindType bindType) {
        this.bindType = bindType;
    }
    
    static {
        Module.mc = Minecraft.getMinecraft();
    }
}
