package axolotl.cheats.config;

import axolotl.Axolotl;
import axolotl.cheats.modules.Module;
import axolotl.cheats.modules.ModuleManager;
import axolotl.cheats.settings.ModeSetting;
import axolotl.cheats.settings.Setting;
import axolotl.cheats.settings.SettingCluster;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConfigManager {

    public Config createConfig(ModuleManager m, String name) {

        JSONModuleManager man = new JSONModuleManager();
        Gson gson = new Gson();

        List<JSONModule> list = new ArrayList<>();

        for(Module mod : m.modules) {
            list.add(man.getJMFM(mod));
        }

        return new Config(gson.toJson(list), name);
    }

    public Config getConfig(String configName) throws FileNotFoundException {
        String axo = Axolotl.INSTANCE.name;
        String location;
        if(configName.equalsIgnoreCase(System.getProperty("user.dir") + "\\" + axo + "\\currentConfig.json")) {
            location = System.getProperty("user.dir") + "\\" + axo + "\\currentConfig.json";
        } else location = System.getProperty("user.dir") + "\\" + axo + "\\configs\\" + configName + ".json";
        File f = new File(location);
        Scanner scanner = new Scanner(f);
        String out = "";
        while(scanner.hasNextLine()) {
            out += scanner.nextLine();
        }
        return new Config(out, location);
    }

    public void saveConfig(Config c) throws IOException {
        File file = new File(c.location);
        file.createNewFile();
        FileWriter writer = new FileWriter(file);
        writer.write(c.json);
        writer.close();
    }

    public String loadConfig(String config) {
        try {

            Config c = Axolotl.INSTANCE.configManager.getConfig(config);

            Gson gson = new Gson();

            List moduleList = gson.fromJson(c.json, ArrayList.class);

            List<JSONModule> jsonModule = new ArrayList<>();

            for(Object m : moduleList) {

                LinkedTreeMap jsonModuleTree = (LinkedTreeMap)m;

                String name = (String) jsonModuleTree.get("name");
                boolean toggled = (Boolean)jsonModuleTree.get("toggled");
                List settings = (ArrayList) jsonModuleTree.get("settings");

                List<JSONSetting> jsonSetting = new ArrayList<>();

                for(Object s : settings) {

                    LinkedTreeMap jsonSettingTree = (LinkedTreeMap)s;

                    String sname = (String) jsonSettingTree.get("name");
                    Object svalue = jsonSettingTree.get("value");
                    String stype = (String) jsonSettingTree.get("type");

                    jsonSetting.add(new JSONSetting(sname, svalue, stype));

                }

                jsonModule.add(new JSONModule(name, toggled, jsonSetting.toArray()));

            }

            for(JSONModule m : jsonModule) {
                for(Module mod : Axolotl.INSTANCE.moduleManager.modules) {
                    String modname = mod.name.replace("NefKilla", "Aura").replace("UncodableESP", "ESP").replace("NefSexESP", "ESP");
                    String mname = m.name.replace("NefKilla", "Aura").replace("UncodableESP", "ESP").replace("NefSexESP", "ESP");
                    if(modname.equalsIgnoreCase(mname)) {
                        mod.toggled = m.toggled;
                        for(Object s : m.settings) {
                            JSONSetting set = (JSONSetting) s;
                            if (set.name.startsWith(".")) {
                                for(Setting modSet : mod.settings.getSettings()) {
                                    if(modSet.sname.equalsIgnoreCase("ModeSetting")) {
                                        if(modSet.name.equalsIgnoreCase(set.name)) {
                                            ModeSetting modeSet = (ModeSetting)modSet;
                                            SettingCluster cluster = modeSet.settingsCluster.get(modeSet.index);
                                            for(Setting clusterSet : cluster.settings.settings) {
                                                if(clusterSet.name.equalsIgnoreCase(set.name.substring(1))) {
                                                    clusterSet.setValue(set.value);
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                for (Setting modSet : mod.settings.getSettings()) {
                                    if (modSet.name.equalsIgnoreCase(set.name)) {
                                        modSet.setValue(set.value);
                                        break;
                                    }
                                }
                            }
                        }
                        break;
                    }
                }
            }

            return "Successfully loaded config.";

        } catch (FileNotFoundException e) {
            return "Invalid config name.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Your config was corrupted.";
        }
    }
}
