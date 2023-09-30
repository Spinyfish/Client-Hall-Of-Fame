// 
// Decompiled by Procyon v0.5.36
// 

package today.getbypass.module;

import today.getbypass.events.Event;
import today.getbypass.utils.fontrenderer.FontRenderer;
import net.minecraft.client.Minecraft;

public class Module
{
    protected Minecraft mc;
    private String name;
    private int key;
    public boolean toggled;
    public Category category;
    private String desc;
    FontRenderer descRen;
    FontRenderer nameRen;
    
    public Module(final String nm, final int k, final String description, final Category c) {
        this.mc = Minecraft.getMinecraft();
        this.name = nm;
        this.descRen = new FontRenderer("assets/minecraft/GetBypass/font/Inter-Regular.ttf", 20.0f);
        this.nameRen = new FontRenderer("assets/minecraft/GetBypass/font/Inter-Regular.ttf", 34.0f);
        this.key = k;
        this.category = c;
        this.toggled = false;
        this.desc = description;
    }
    
    public void toggle() {
        this.toggled = !this.toggled;
        if (this.toggled) {
            this.onEnable();
        }
        else {
            this.onDisable();
        }
    }
    
    public void onEnable() {
    }
    
    public void onDisable() {
    }
    
    public void onUpdate() {
    }
    
    public void onRender() {
    }
    
    public void onEvent(final Event e) {
    }
    
    public Minecraft getMc() {
        return this.mc;
    }
    
    public void setMc(final Minecraft mc) {
        this.mc = mc;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public int getKey() {
        return this.key;
    }
    
    public void setKey(final int key) {
        this.key = key;
    }
    
    public boolean isToggled() {
        return this.toggled;
    }
    
    public void setToggled(final boolean toggled) {
        this.toggled = toggled;
    }
    
    public String getDesc() {
        return this.desc;
    }
    
    public FontRenderer getNameRen() {
        return this.nameRen;
    }
    
    public FontRenderer getDescRen() {
        return this.descRen;
    }
}
