package dev.hera.client.mods;

import dev.hera.client.events.EventBus;
import net.minecraft.client.Minecraft;

public class Mod {

    public String name;
    public String description;
    public Category category;
    public int keyCode;
    public boolean toggled;
    protected Minecraft mc = Minecraft.getMinecraft();

    public Mod(){
        if(!this.getClass().isAnnotationPresent(ModInfo.class)){
            throw new RuntimeException("ModInfo is not present on class " + this.getClass().getName());
        }
        ModInfo info = this.getClass().getAnnotation(ModInfo.class);
        this.name = info.name();
        this.description = info.description();
        this.category = info.category();
        this.keyCode = info.keyCode();
    }

    public void onEnable(){

    }

    public void onDisable(){

    }

    public void toggle(){
        toggled = !toggled;
        if(toggled){
            onEnable();
            EventBus.register(this);
        }else{
            onDisable();
            EventBus.unregister(this);
        }
    }

}