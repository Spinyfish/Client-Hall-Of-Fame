package store.femboy.spring.impl.module.settings.impl;

import store.femboy.spring.impl.module.settings.Settings;

import java.util.Arrays;
import java.util.List;

public final class ModeSetting extends Settings {

    public int index;
    public List<String> modes;

    public ModeSetting(String name, String defaultMode, String... modes){
        this.name = name;
        this.modes = Arrays.asList(modes);
        index = this.modes.indexOf(defaultMode);
    }

    public String getMode(){
        return modes.get(index);
    }

    public boolean is(String mode){
        return index == modes.indexOf(mode);
    }

    public void cycle(){
        if(index < modes.size() - 1){
            index++;
        }else{
            index = 0;
        }
    }

}
