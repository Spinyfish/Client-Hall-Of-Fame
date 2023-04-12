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
import markgg.settings.KeybindSetting;
import markgg.settings.Setting;

public class KeybindSystem {
	
	private File configFile;
    private List<Module> modules;
	
    public KeybindSystem(File configFile) {
        this.configFile = configFile;
        this.modules = new ArrayList<Module>();
    }
    
	public static void saveKeybinds(String filename) {
		if (filename == null || filename.isEmpty()) {
	        filename = "keybinds.json";
	    } else if (!filename.endsWith(".json")) {
	        filename += ".json";
	    }
        try {
        	JsonObject configData = new JsonObject();
            
        	for (Category category : Category.values()) {
                JsonArray moduleArray = new JsonArray();
                List<Module> modulesInCategory = Client.getModulesByCategory(category);
                for (Module module : modulesInCategory) {
                    JsonObject moduleObject = new JsonObject();
                    moduleObject.addProperty("Name", module.getName());
                    for (Setting setting : module.settings) {
                    	if(setting instanceof KeybindSetting)
                    		moduleObject.addProperty(setting.name, ((KeybindSetting) setting).code);
                    }
                    moduleArray.add(moduleObject);
                }
                configData.add(category.name().toLowerCase(), moduleArray);
            }

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            FileWriter writer = new FileWriter("Raze/" + filename);
            gson.toJson(configData, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	public void loadKeybinds(String filename) {
		if (filename == null || filename.isEmpty()) {
	        filename = "keybinds.json";
	    } else if (!filename.endsWith(".json")) {
	        filename += ".json";
	    }
		try {
	        Gson gson = new Gson();
	        BufferedReader reader = new BufferedReader(new FileReader("Raze/" + filename));
	        JsonObject configData = gson.fromJson(reader, JsonObject.class);
	        reader.close();

	        for (Module module : Client.getAllModules()) {
	            String categoryName = module.getCategory().name().toLowerCase();
	            JsonArray moduleArray = configData.getAsJsonArray(categoryName);
	            for (JsonElement element : moduleArray) {
	                JsonObject moduleObject = element.getAsJsonObject();
	                if (module.name.equals(moduleObject.get("Name").getAsString())) {
	                    for (Setting setting : module.settings) {
	                    	if(setting instanceof KeybindSetting) {
	                    		 int key = moduleObject.get(setting.name).getAsInt();
	                    		 KeybindSetting key2 = (KeybindSetting)setting;
	                    		 key2.setKeyCode(key);
	                    	}
	                    }
	                }
	            }
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
}
