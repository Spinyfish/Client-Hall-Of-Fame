package v4n1ty.module;

import net.minecraft.client.Minecraft;
import v4n1ty.events.Event;

public class Module {

    protected Minecraft mc = Minecraft.getMinecraft();
    private String name;
    private int key;
    private boolean toggled;
    private Category category;

    public Module(String nm, int k, Category c) {
        name = nm;
        key = k;
        category = c;
        toggled = false;
        setup();

    }

    public void toggle() {
        toggled = !toggled;
        if(toggled) {
            onEnable();
        }else {
            onDisable();
        }

    }

    public void disable() {
        toggled = !toggled;
        if(toggled) {
            onDisable();
        }
    }

    public void onEvent(Event e){

    }

    public void onEnable() {}
    public void onDisable() {}
    public void onUpdate() {}
    public void onRender() {}
    public void setup() {}


    public Minecraft getMc() {
        return mc;
    }

    public void setMc(Minecraft mc) {
        this.mc = mc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public boolean isToggled() {
        return toggled;
    }

    public void setToggled(boolean toggled) {
        this.toggled = toggled;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}