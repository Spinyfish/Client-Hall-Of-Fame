/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.FileUtils
 *  org.json.JSONArray
 *  org.json.JSONException
 *  org.json.JSONObject
 */
package wtf.monsoon.api.config;

import java.awt.Color;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.manager.alt.Alt;
import wtf.monsoon.api.manager.alt.AltManager;
import wtf.monsoon.api.setting.Bind;
import wtf.monsoon.api.util.entity.PlayerUtil;

public class ConfigSystem {
    private final HashMap<String, File> directories = new HashMap(){
        {
            this.put("root", new File("monsoon"));
            this.put("configs", new File((File)this.get("root"), "configs"));
            this.put("scripts", new File((File)this.get("root"), "scripts"));
        }
    };

    public ConfigSystem() {
        this.directories.forEach((identifier, file) -> {
            if (!file.exists()) {
                file.mkdirs();
            }
        });
    }

    public void save(String name) {
        new Thread(() -> {
            HashMap<String, JSONObject> binds = new HashMap<String, JSONObject>();
            Wrapper.getMonsoon().getModuleManager().getModules().forEach(module -> {
                JSONObject object = new JSONObject();
                try {
                    object.put("Keybinding", (Object)(module.getKey().getValue().getButtonCode() + ":" + module.getKey().getValue().getDevice().toString()));
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
                binds.put(module.getName(), object);
            });
            this.save(new File(this.directories.get("root"), "binds.json"), binds);
            HashMap<String, JSONObject> moduleData = new HashMap<String, JSONObject>();
            try {
                moduleData.put("monsoon-data", new JSONObject().put("client-version", (Object)Wrapper.getMonsoon().getVersion()));
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            Wrapper.getMonsoon().getModuleManager().getModules().forEach(module -> {
                JSONObject object = new JSONObject();
                try {
                    object.put("enabled", module.isEnabled());
                    object.put("visible", module.isVisible());
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
                module.getSettingHierarchy().forEach(setting -> {
                    if (setting == module.getKey()) {
                        return;
                    }
                    try {
                        if (setting.getValue() instanceof Color) {
                            object.put(setting.getPath(), (Object)(((Color)setting.getValue()).getRed() + ":" + ((Color)setting.getValue()).getGreen() + ":" + ((Color)setting.getValue()).getBlue()));
                        } else if (setting.getValue() instanceof Bind) {
                            object.put(setting.getPath(), (Object)(((Bind)setting.getValue()).getButtonCode() + ":" + ((Bind)setting.getValue()).getDevice().name()));
                        } else if (setting.getValue() instanceof Enum) {
                            object.put(setting.getPath(), (Object)((Enum)setting.getValue()).name());
                        } else {
                            object.put(setting.getPath(), setting.getValue());
                        }
                    }
                    catch (JSONException exception) {
                        exception.printStackTrace();
                    }
                });
                moduleData.put(module.getName(), object);
            });
            this.save(new File(this.directories.get("configs"), name + ".json"), moduleData);
        }).start();
    }

    public void load(String name) {
        try {
            this.loadNoCatch(name, false);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void load(String name, boolean ignoreOld) {
        try {
            this.loadNoCatch(name, ignoreOld);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadNoCatch(String name, boolean ignoreOld) throws ConfigForOldVersionException {
        new Thread(() -> {
            File data;
            System.out.println("Loading config " + name);
            File binds = new File(this.directories.get("root"), "binds.json");
            if (binds.exists()) {
                JSONObject jsonBinds = this.loadJSON(binds);
                if (jsonBinds == null) {
                    System.out.println("Failed to load JSON object for " + name);
                    return;
                }
                Wrapper.getMonsoon().getModuleManager().getModules().forEach(module -> {
                    try {
                        String[] values = jsonBinds.getJSONObject(module.getName()).getString(module.getKey().getName()).split(":");
                        module.getKey().setValue(new Bind(Integer.parseInt(values[0]), Bind.Device.valueOf(values[1])));
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
            }
            if (!(data = new File(this.directories.get("configs"), name + ".json")).exists()) {
                System.out.println("Config " + name + " does not exist!");
                return;
            }
            JSONObject jsonData = this.loadJSON(data);
            try {
                String configVersion = jsonData.getJSONObject("monsoon-data").getString("client-version");
                String clientVersion = Wrapper.getMonsoon().getVersion();
                Wrapper.getMonsoon().getModuleManager().getModules().forEach(module -> {
                    try {
                        JSONObject moduleData = jsonData.getJSONObject(module.getName());
                        module.setEnabledSilent(moduleData.getBoolean("enabled"));
                        module.setVisible(moduleData.getBoolean("visible"));
                        module.getSettingHierarchy().forEach(setting -> {
                            try {
                                if (setting.getValue() instanceof Boolean) {
                                    setting.setValue(moduleData.getBoolean(setting.getPath()));
                                } else if (setting.getValue() instanceof Double) {
                                    setting.setValue(moduleData.getDouble(setting.getPath()));
                                } else if (setting.getValue() instanceof Enum) {
                                    try {
                                        Enum enumuration = (Enum)setting.getValue();
                                        Object value = Enum.valueOf(enumuration.getClass(), moduleData.getString(setting.getPath()));
                                        setting.setValue(value);
                                    }
                                    catch (IllegalArgumentException iae) {
                                        PlayerUtil.sendClientMessage("A setting for " + module.getName() + " couldn't be loaded.");
                                    }
                                } else if (setting.getValue() instanceof Color) {
                                    String[] values = moduleData.getString(setting.getPath()).split(":");
                                    setting.setValue(new Color(Integer.parseInt(values[0]), Integer.parseInt(values[1]), Integer.parseInt(values[2])));
                                } else if (setting.getValue() instanceof Bind && setting != module.getKey()) {
                                    String[] values = moduleData.getString(setting.getPath()).split(":");
                                    setting.setValue(new Bind(Integer.parseInt(values[0]), Bind.Device.valueOf(values[1])));
                                }
                            }
                            catch (JSONException e) {
                                e.printStackTrace();
                            }
                        });
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public boolean configExists(String name) {
        return new File(this.directories.get("configs"), name + ".json").exists();
    }

    public void saveAlts(AltManager altManager) {
        new Thread(() -> {
            HashMap<String, JSONObject> altData = new HashMap<String, JSONObject>();
            try {
                altData.put("client-data", new JSONObject().put("ALTENING-API-KEY", (Object)altManager.getApiKey()));
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            altManager.getAlts().forEach(alt -> {
                try {
                    altData.put(alt.getEmail(), new JSONObject().put("password", (Object)alt.getPassword()).put("authenticator", (Object)alt.getAuthenticator().name()).put("username", (Object)alt.getUsername()));
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            });
            this.save(new File(this.directories.get("root"), "alts.json"), altData);
        }).start();
    }

    public void loadAlts(AltManager altManager) {
        new Thread(() -> {
            JSONObject json = this.loadJSON(new File(this.directories.get("root"), "alts.json"));
            if (json != null) {
                try {
                    JSONObject clientData = json.getJSONObject("client-data");
                    altManager.setApiKey(clientData.getString("ALTENING-API-KEY"));
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
                JSONArray keyNames = json.names();
                for (int i = 0; i < keyNames.length(); ++i) {
                    try {
                        String name = keyNames.getString(i);
                        if (name.equals("client-data")) continue;
                        JSONObject data = json.getJSONObject(name);
                        Alt alt = new Alt(name, data.getString("password"), Alt.Authenticator.valueOf(data.getString("authenticator")));
                        if (data.getString("username") != null && !data.getString("username").equals("")) {
                            alt.setUsername(data.getString("username"));
                        }
                        altManager.addAlt(alt);
                        continue;
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public File getDirectory(String name) {
        return this.directories.get(name);
    }

    private void save(File file, Map<String, JSONObject> objects) {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            JSONObject object = new JSONObject();
            objects.forEach((name, json) -> {
                try {
                    object.put(name, json);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            });
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(object.toString(4));
            fileWriter.flush();
            fileWriter.close();
        }
        catch (IOException | JSONException exception) {
            exception.printStackTrace();
        }
    }

    private JSONObject loadJSON(File file) {
        try {
            return new JSONObject(FileUtils.readFileToString((File)file));
        }
        catch (IOException | JSONException e) {
            return null;
        }
    }

    public static class ConfigForOldVersionException
    extends Exception {
        private String message;

        public ConfigForOldVersionException(String message) {
            this.setMessage(message);
        }

        @Override
        public String getMessage() {
            return this.message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}

