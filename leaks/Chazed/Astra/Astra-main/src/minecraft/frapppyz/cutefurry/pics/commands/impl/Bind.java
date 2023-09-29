package frapppyz.cutefurry.pics.commands.impl;

import frapppyz.cutefurry.pics.Wrapper;
import frapppyz.cutefurry.pics.modules.Mod;
import org.lwjgl.input.Keyboard;

public class Bind {
    public static void onCommand(String cmd){
        String[] things = cmd.split(" ");
        if(things.length == 2) {
            for(Mod m : Wrapper.getModManager().mods) {
                if(m.getName().toLowerCase().equals(things[0])) {
                    int key = Keyboard.getKeyIndex(things[1].toUpperCase());
                    m.setKey(key);
                    Wrapper.getLogger().addChat("Bound " + things[0] + " to " + things[1].toUpperCase());
                }
            }
        }else Wrapper.getLogger().error("Incorrect command usage.");
    }
}
