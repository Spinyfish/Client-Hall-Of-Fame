package de.verschwiegener.atero.util.files.config;

import java.util.ArrayList;

import de.verschwiegener.atero.module.Module;
import net.minecraft.client.Minecraft;

public class ConfigManager {
    
    public ArrayList<Config> configs = new ArrayList<>();

    
    public Config getConfigByName(final String name, ConfigType type) {
	return configs.stream().filter(config -> config.getName().toLowerCase().equalsIgnoreCase(name.toLowerCase()) && (config.getType() == type))
		.findFirst().orElse(null);
    }
    public Config getConfigByName(final String name) {
	return configs.stream().filter(config -> config.getName().toLowerCase().equalsIgnoreCase(name.toLowerCase()))
		.findFirst().orElse(null);
    }
    public Config getConfigByStartsWith(final String name, ConfigType type) {
   	return configs.stream().filter(config -> config.getName().toLowerCase().startsWith(name.toLowerCase()) && (config.getType() == type))
   		.findFirst().orElse(null);
       }
    public ArrayList getConfigsforServer(String ServerIP) {
	ArrayList<Config> configs = new ArrayList<>();
	for(Config c : this.configs) {
	    if(c.getRecommendedServerIP().equalsIgnoreCase(ServerIP)){
		configs.add(c);
	    }
	}
	return configs;
    }
}
