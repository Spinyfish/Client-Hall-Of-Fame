package frapppyz.cutefurry.pics;

import frapppyz.cutefurry.pics.configs.Config;
import frapppyz.cutefurry.pics.util.DiscordUtil;
import frapppyz.cutefurry.pics.util.HWIDUtil;
import viamcp.ViaMCP;

import java.io.IOException;

public class Astra {
    public String name = "Astra";
    public String ver = "0.2";
    public String username = "none";
    public String formattedName = name + " v" + ver;
    public void startup(){
        DiscordUtil.startup();
        Wrapper.getLogger().info("Loading Astra.");
        Wrapper.getLogger().info("Starting via.");
        try{
            ViaMCP.getInstance().start();
            ViaMCP.getInstance().initAsyncSlider();
        }catch (Exception e) {Wrapper.getLogger().warning(e.getMessage());}
        Wrapper.getLogger().info("Started via.");

        Wrapper.getLogger().info("Loading ModuleManager.");
        Wrapper.getModManager().addMods();
        Wrapper.getLogger().info("Loaded ModuleManager.");

    }

    public void shutdown(){

    }
}
