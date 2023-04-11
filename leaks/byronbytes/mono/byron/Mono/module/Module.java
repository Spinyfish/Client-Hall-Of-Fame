package byron.Mono.module;

import byron.Mono.Mono;
import byron.Mono.clickgui.setting.Setting;
import byron.Mono.interfaces.MinecraftInterface;
import byron.Mono.interfaces.ModuleInterface;
import net.minecraft.client.Minecraft;

public class Module implements MinecraftInterface {


    private ModuleInterface moduleInterface = getClass().getAnnotation(ModuleInterface.class);
    private String name = moduleInterface.name(), description = moduleInterface.description();



    private Category category = moduleInterface.category();
    private int key;
    private boolean toggled;

    public Module() {
        setup();
    }

    public void setup()
    {

    }

    public void toggle() {
        if(toggled)
        {
            onDisable();
            toggled = false;
        }
        else
        {
            onEnable();
            toggled = true;
        }
    }


    public final void setToggled(boolean toggled) {
        if(toggled)
        {
            if(!this.toggled)
            {
                toggle();
            }
        }
        else
        {
            if(this.toggled)
            {
                toggle();
            }

        }
    }

    public void onEnable() {
        Mono.INSTANCE.getEventBus().register(this);
        Mono.INSTANCE.sendMessage("Enabled: " + this.name);
    }

    public void onDisable()
    {
        Mono.INSTANCE.getEventBus().unregister(this);
        Mono.INSTANCE.sendMessage("Disabled: " + this.name);
    }

    public final String getName() {
        return name;
    }

    public final String getDescription() {
        return description;
    }

    public final int getKey() {
        return key;
    }

    public final void setKey(int key) {
        this.key = key;
    }

    public final boolean isToggled() {
        return toggled;
    }

    public Category getCategory() {
        return category;
    }

    protected final void rSetting(Setting setting) {
        Mono.INSTANCE.getSettingsManager().rSetting(setting);
    }

    public final static Setting getSetting(String name) {
        return Mono.INSTANCE.getSettingsManager().getSettingByName(name);
    }

    public Module (int key)
    {
        this.key = key;

    }

}
