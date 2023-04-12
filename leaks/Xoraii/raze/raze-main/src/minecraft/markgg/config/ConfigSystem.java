package markgg.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import markgg.Client;
import markgg.modules.Module;
import markgg.modules.Module.Category;
import markgg.settings.BooleanSetting;
import markgg.settings.ModeSetting;
import markgg.settings.NumberSetting;
import markgg.settings.Setting;
import net.minecraft.client.Minecraft;

public class ConfigSystem {
	
	private File configFile;
    private List<Module> modules;
	public static String error = "no error";
    
    public ConfigSystem(File configFile) {
        this.configFile = configFile;
        this.modules = new ArrayList<Module>();
    }
    
	public static void saveConfig(String filename) {
		if (filename == null || filename.isEmpty()) {
	        filename = "default.json";
	    } else if (!filename.endsWith(".json")) {
	        filename += ".json";
	    }
		
        try {
        	JsonObject configData = new JsonObject();
            
        	configData.addProperty("Version", Client.version);
        	
        	for (Category category : Category.values()) {
                JsonArray moduleArray = new JsonArray();
                List<Module> modulesInCategory = Client.getModulesByCategory(category);
                for (Module module : modulesInCategory) {
                    JsonObject moduleObject = new JsonObject();
                    moduleObject.addProperty("Name", module.getName());
                    moduleObject.addProperty("Enabled", module.isEnabled());
                    for (Setting setting : module.settings) {
                    	if(setting instanceof ModeSetting)
                    		moduleObject.addProperty(setting.name, ((ModeSetting) setting).getMode());
                    	if(setting instanceof NumberSetting)
                    		moduleObject.addProperty(setting.name, ((NumberSetting) setting).getValue());
                    	if(setting instanceof BooleanSetting)
                    		moduleObject.addProperty(setting.name, ((BooleanSetting) setting).isEnabled());
                    }
                    moduleArray.add(moduleObject);
                }
                configData.add(category.name().toLowerCase(), moduleArray);
            }

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            FileWriter writer = new FileWriter("Raze/configs/" + filename);
            gson.toJson(configData, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	public void loadConfig(String filename) {
		if (filename == null || filename.isEmpty()) {
	        filename = "default.json";
	    } else if (!filename.endsWith(".json")) {
	        filename += ".json";
	    }
		
		try {
	        Gson gson = new Gson();
	        BufferedReader reader = new BufferedReader(new FileReader("Raze/configs/" + filename));
	        JsonObject configData = gson.fromJson(reader, JsonObject.class);
	        reader.close();

	        error = "no error";
	        
	        if (configData.has("Version") && configData.get("Version").getAsString().equals(Client.version)) {
	        	for (Module module : Client.getAllModules()) {
		            String categoryName = module.getCategory().name().toLowerCase();
		            JsonArray moduleArray = configData.getAsJsonArray(categoryName);
		            for (JsonElement element : moduleArray) {
		                JsonObject moduleObject = element.getAsJsonObject();
		                if (module.name.equals(moduleObject.get("Name").getAsString())) {
		                    boolean enabled = moduleObject.get("Enabled").getAsBoolean();
		                    module.toggled = enabled;
		                    for (Setting setting : module.settings) {
		                    	if(setting instanceof BooleanSetting) {
		                    		 boolean bool = moduleObject.get(setting.name).getAsBoolean();
		                    		 BooleanSetting bool2 = (BooleanSetting)setting;
		                    		 bool2.setEnabled(bool);
		                    	}
		                    	if(setting instanceof ModeSetting) {
		                    		 String mode = moduleObject.get(setting.name).getAsString();
		                    		 ModeSetting mode2 = (ModeSetting)setting;
		                    		 mode2.setMode(mode);
		                    	}
		                    	if(setting instanceof NumberSetting) {
		                    		 Double number = moduleObject.get(setting.name).getAsDouble();
		                    		 NumberSetting number1 = (NumberSetting)setting;
		                    		 number1.setValue(number);
		                    	}
		                    }
		                }
		            }
		        }
	        } else {
	        	String version = configData.get("Version").getAsString();
		        if (!version.equals(Client.version)) {
		            error = "Incompatible config version";
		        }else {
		        	error = "no error";
		        }
	            Client.print("[RAZE] - Error loading config: incompatible version.");
	        }
	        
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
}
