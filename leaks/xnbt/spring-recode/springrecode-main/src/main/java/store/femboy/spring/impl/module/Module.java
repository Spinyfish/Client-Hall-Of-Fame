package store.femboy.spring.impl.module;

import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import store.femboy.spring.Spring;
import store.femboy.spring.impl.module.settings.Settings;
import store.femboy.spring.impl.module.settings.impl.KeySetting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Module {
    public String name, description;
    public int key;
    public Category category;
    public boolean toggled;

    public static Minecraft mc = Minecraft.getMinecraft();

    public List<Settings> settings = new ArrayList<Settings>();
    public KeySetting keyCode = new KeySetting(0);

    public Module(String name, String description, int key, Category category) {
        this.name = name;
        this.description = description;
        keyCode.key = key;
        this.category = category;
        this.toggled = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getKey() {
        return keyCode.key;
    }

    public void setKey(int key) {
        this.key = keyCode.key;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public boolean isToggled() {
        return toggled;
    }
    public void toggle() {
        toggled = !toggled;
        if (toggled) {
            onEnable();
        } else {
            onDisable();
        }
    }

    public void setToggled(boolean toggled) {
        if(toggled){
            onEnable();
        }else{
            onDisable();
        }
        this.toggled = toggled;
    }

    public void onEnable() {
        Spring.INSTANCE.getBus().register(this);
        System.out.println("Module " + name + " has been enabled!");
    }
    public void onDisable() {
        Spring.INSTANCE.getBus().unregister(this);
        System.out.println("Module " + name + " has been disabled!");
    }

    public void sendPacket(Packet p){
        mc.getNetHandler().addToSendQueue(p);
    }

    public void sendSilentPacket(Packet p){
        mc.getNetHandler().addToSilentSendQueue(p);
    }

    public void addSettings(Settings... settings) {
        this.settings.addAll(Arrays.asList(settings));
        this.settings.sort(Comparator.comparingInt(s -> s == keyCode ? 1 : 0));
    }
}
