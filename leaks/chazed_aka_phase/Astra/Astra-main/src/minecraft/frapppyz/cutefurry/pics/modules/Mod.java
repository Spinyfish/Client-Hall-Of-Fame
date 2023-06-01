package frapppyz.cutefurry.pics.modules;

import com.mojang.realmsclient.gui.ChatFormatting;
import frapppyz.cutefurry.pics.Wrapper;
import frapppyz.cutefurry.pics.event.Event;
import frapppyz.cutefurry.pics.modules.impl.render.HUD;
import frapppyz.cutefurry.pics.modules.settings.Setting;
import frapppyz.cutefurry.pics.notifications.Type;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.Arrays;

public class Mod {
    private String name;
    private String fullname;
    private String description;
    private String suffix;
    private boolean isToggled;
    private int key;
    private Category category;
    private boolean showingsetts;
    private ArrayList<Setting> settings = new ArrayList<>();

    public final Minecraft mc = Minecraft.getMinecraft();

    public Mod(String name, String description, int key, Category category){
        this.name = name;
        this.description = description;
        this.key = key;
        this.category = category;
    }

    public void addSettings(Setting... settings) {
        this.settings.addAll(Arrays.asList(settings));
    }



    public ArrayList<Setting> getSettings() {
        return settings;
    }

    public void setSettingsShowed(boolean showed) {
        this.showingsetts = showed;
    }

    public boolean isSettingsShowed() {
        return showingsetts;
    }

    public String getName(){
        return name;
    }

    public String getSuffix(){return suffix;}

    public void setSuffix(String s){suffix = s;}

    public String getFullname(){
        if(suffix != null){
            if(HUD.suffixType.is("Bracket")){
                return getName() + ChatFormatting.GRAY + " [" + suffix + "]";
            }else if(HUD.suffixType.is("Space")){
                return getName() + ChatFormatting.GRAY + " " + suffix;
            }else{
                return getName();
            }
        }else{
            return getName();
        }


    }

    public String getDescription(){
        return description;
    }

    public void setKey(int key){
        this.key = key;
    }

    public void setToggled(Boolean b){
        this.isToggled = b;
        if(isToggled) onEnable(); else onDisable();
    }

    public void setToggledSilently(Boolean b){
        this.isToggled = b;
    }

    public int getKey(){
        return key;
    }

    public boolean isToggled(){
        return isToggled;
    }

    public Category getCategory(){
        return category;
    }

    public void toggle(){
        this.isToggled = !this.isToggled;
        if(isToggled) onEnable(); else onDisable();
        Wrapper.getNotifications().postNotification("Toggled Module", this.getName() + " is now " + (this.isToggled() ? "on." : "off."), Type.INFO, 750);
    }

    public void onEnable(){

    }

    public void onDisable(){

    }

    public void onEvent(Event e){

    }

    public void onRender(){

    }

}
