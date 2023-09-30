package felix.management;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import felix.Client;
import felix.module.ClientConfig;
import felix.module.Module;
import felix.util.other.FilesReader;
import felix.util.other.Logger;
import felix.value.Value;
import felix.value.impl.BooleanValue;
import felix.value.impl.ColorValue;
import felix.value.impl.EnumValue;
import felix.value.impl.NumberValue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConfigManager {
	
	private ArrayList<ClientConfig> configList = new ArrayList<>();

    public void saveConfig(ClientConfig clientConfig) {
        JsonObject jsonObject = new JsonObject();
      for(Module module : Client.INSTANCE.getModuleManager().getModules()) {
          JsonObject moduleObject = new JsonObject();
          moduleObject.addProperty("name", module.getName());
          moduleObject.addProperty("keybind", module.getKey());
          moduleObject.addProperty("enabled", module.isEnabled());

          module.getValues().forEach(value -> moduleObject.addProperty(value.getLabel(), String.valueOf(value.getValue())));

          jsonObject.add(module.getName(), moduleObject);
      	}

        try {
            FileWriter fileWriter = new FileWriter(clientConfig.getConfigFile());
            fileWriter.write(new GsonBuilder().create().toJson(jsonObject));
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        configList.add(clientConfig);
    }
    
    public List<ClientConfig> getContents() {
    	return this.configList;
    }

    public void loadConfig(ClientConfig clientConfig) {
    	if (!clientConfig.getConfigFile().exists()) {
    		Logger.print("Config not found.");
    		return;
    	}
        Client.INSTANCE.getModuleManager().getModules().forEach(module -> module.setEnabled(false));
        String content = FilesReader.readFile(clientConfig.getConfigFile());

        JsonObject configurationObject = new GsonBuilder().create().fromJson(content, JsonObject.class);

        for(Map.Entry<String, JsonElement> entry : configurationObject.entrySet()) {
            if (entry.getValue() instanceof JsonObject) {
                JsonObject moduleObject = (JsonObject) entry.getValue();

                for(Module module : Client.INSTANCE.getModuleManager().getModules()) {
                    if(module.getName().equalsIgnoreCase(moduleObject.get("name").getAsString())) {
                        module.setKeyBind(moduleObject.get("keybind").getAsInt());
                            if(moduleObject.get("enabled").getAsBoolean()) {
                                module.toggle();
                            } else {
                                module.setEnabled(false);
                        }

                        for(Value value : module.getValues()) {
                            if(moduleObject.get(value.getLabel()) != null) {
                                if(value instanceof NumberValue) {
                                    if(value.getValue() instanceof Double) {
                                        value.setValue(moduleObject.get(value.getLabel()).getAsDouble());
                                    }
                                    if(value.getValue() instanceof Integer) {
                                        value.setValue(moduleObject.get(value.getLabel()).getAsInt());
                                    }
                                    if(value.getValue() instanceof Float) {
                                        value.setValue(moduleObject.get(value.getLabel()).getAsFloat());
                                    }
                                    if (value.getValue() instanceof Short) {
                                    	value.setValue(moduleObject.get(value.getLabel()).getAsShort());
                                    }
                                    if (value.getValue() instanceof Byte) {
                                    	value.setValue(moduleObject.get(value.getLabel()).getAsByte());
                                    }
                                    if (value.getValue() instanceof Long) {
                                    	value.setValue(moduleObject.get(value.getLabel()).getAsLong());
                                    }
                                }
                                if(value instanceof BooleanValue) {
                                    value.setValue(moduleObject.get(value.getLabel()).getAsBoolean());
                                }
                                if (value instanceof ColorValue) {
                                	value.setValue(moduleObject.get(value.getLabel()).getAsInt());
                                }
                                if(value instanceof EnumValue) {
                                    for(int i = 0;i < ((EnumValue) value).getConstants().length;i++) {
                                        if(((EnumValue) value).getConstants()[i].name().equalsIgnoreCase(moduleObject.get(value.getLabel()).getAsString())) {
                                             value.setValue(((EnumValue) value).getConstants()[i]);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void loadConfigs() {
    	if (configList != null) {
	        List<String> searchResult = new ArrayList<>();
	        FilesReader.search(".*\\.txt", Client.INSTANCE.getConfigDirectory(), searchResult);
	        for (String configName : searchResult) {
	
	            this.configList.add(new ClientConfig(configName.replace(".txt", "")));
	        }
    	}
    }

    public void deleteConfig(ClientConfig clientConfig) {
        if(clientConfig.getConfigFile() != null) {
            if(clientConfig.getConfigFile().delete()) {
                this.deleteConfig(clientConfig);
                this.configList.remove(clientConfig);
                this.loadConfigs();
            } else {
            	//emtpy else 
            }
        }
    }
}