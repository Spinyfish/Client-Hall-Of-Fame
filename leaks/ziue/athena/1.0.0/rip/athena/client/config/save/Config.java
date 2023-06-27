package rip.athena.client.config.save;

import rip.athena.client.utils.file.*;
import rip.athena.client.*;
import java.io.*;
import rip.athena.client.modules.*;
import rip.athena.client.gui.clickgui.pages.*;
import rip.athena.client.utils.input.*;
import rip.athena.client.config.*;
import java.awt.*;
import rip.athena.client.gui.hud.*;
import rip.athena.client.theme.impl.*;
import rip.athena.client.cosmetics.cape.*;
import rip.athena.client.macros.*;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.tileentity.*;
import net.minecraft.util.*;
import rip.athena.client.modules.impl.render.*;
import rip.athena.client.utils.*;
import rip.athena.client.events.types.client.*;
import rip.athena.client.events.*;
import java.util.*;
import org.json.*;

public class Config
{
    private ConfigManager parent;
    private FileHandler file;
    private String name;
    
    public Config(final ConfigManager parent, final String name, final File file) {
        this.parent = parent;
        this.name = name;
        this.file = new FileHandler(file);
        try {
            this.file.init();
            if (this.file.isFresh()) {
                this.save(file.getName());
            }
        }
        catch (IOException e) {
            Athena.INSTANCE.getLog().error("Failed to initiate config " + name + "." + e);
        }
    }
    
    public void load() {
        String content = "";
        try {
            content = this.file.getContent(false);
        }
        catch (IOException e) {
            Athena.INSTANCE.getLog().error("Failed to load config " + this.name + " from file." + e);
        }
        this.load(content);
    }
    
    public void load(final String content) {
        this.parent.updateLast(this);
        try {
            final JSONObject obj = new JSONObject(content);
            for (final Module mod : Athena.INSTANCE.getModuleManager().getModules()) {
                mod.setEnabled(false);
            }
            Athena.INSTANCE.getMacroManager().getMacros().clear();
            SettingsPage.TILE_ENTITIES.clear();
            SettingsPage.ENTITIES.clear();
            SettingsPage.BLOCKS.clear();
            SettingsPage.PARTICLES.clear();
            for (final String key : obj.keySet()) {
                if (!key.equalsIgnoreCase("macros")) {
                    if (key.equalsIgnoreCase("crosshair-data")) {
                        continue;
                    }
                    final Module module = Athena.INSTANCE.getModuleManager().getModule(key);
                    if (module == null) {
                        Athena.INSTANCE.getLog().warn("Loaded config " + this.name + " with left over setting " + key + " which is no longer used.");
                    }
                    else {
                        final JSONObject json = obj.getJSONObject(key);
                        if (!json.has("enabled")) {
                            continue;
                        }
                        if (!json.has("bind")) {
                            continue;
                        }
                        final boolean enabled = json.getBoolean("enabled");
                        final int bind = json.getInt("bind");
                        final BindType bindType = BindType.getBind(json.getString("bindtype"));
                        module.setEnabled(enabled);
                        module.setKeyBind(bind);
                        module.setBindType(bindType);
                        final JSONObject settings = json.getJSONObject("settings");
                        for (final String setting : settings.keySet()) {
                            for (final ConfigEntry entry : module.getEntries()) {
                                if (entry.getKey().equalsIgnoreCase(setting)) {
                                    Object value = settings.get(setting);
                                    if (value instanceof JSONObject) {
                                        final JSONObject color = (JSONObject)value;
                                        value = new Color(color.getInt("red"), color.getInt("green"), color.getInt("blue"), color.getInt("alpha"));
                                    }
                                    else if (entry.getType() == Float.TYPE) {
                                        value = (float)settings.getDouble(setting);
                                    }
                                    entry.setValue(module, value);
                                    break;
                                }
                            }
                        }
                        final JSONObject hud = json.getJSONObject("hud");
                        for (final String identifier : hud.keySet()) {
                            final String element = identifier;
                            final JSONObject elementObj = hud.getJSONObject(identifier);
                            for (final HUDElement modElement : module.getHUDElements()) {
                                if (modElement.getIdentifier().equalsIgnoreCase(identifier)) {
                                    modElement.setX(elementObj.getInt("x"));
                                    modElement.setY(elementObj.getInt("y"));
                                    modElement.setScale(elementObj.getDouble("scale"));
                                    modElement.setVisible(elementObj.getBoolean("visible"));
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            final String themeIdentifier = obj.getString("theme");
            for (final AccentTheme theme : AccentTheme.values()) {
                if (theme.name().equalsIgnoreCase(themeIdentifier)) {
                    Athena.INSTANCE.getThemeManager().setTheme(theme);
                    break;
                }
            }
            final String capeIdentifier = obj.getString("cape");
            for (final Cape cape : Athena.INSTANCE.getCosmeticsController().getCapeManager().getCapes()) {
                if (cape.getName().equalsIgnoreCase(capeIdentifier)) {
                    Athena.INSTANCE.getLog().info(cape + capeIdentifier + cape.getName());
                    Athena.INSTANCE.getCosmeticsController().getCapeManager().setSelectedCape(cape);
                    break;
                }
            }
            final JSONArray macroList = obj.getJSONArray("macros");
            for (int i = 0; i < macroList.length(); ++i) {
                final JSONObject macro = macroList.getJSONObject(i);
                Athena.INSTANCE.getMacroManager().getMacros().add(new Macro(macro.getString("name"), macro.getString("command"), macro.getInt("key"), macro.getBoolean("enabled")));
            }
            final JSONObject fps = obj.getJSONObject("fps");
            final JSONArray blocks = fps.getJSONArray("blocks");
            final JSONArray entities = fps.getJSONArray("entities");
            final JSONArray tileentities = fps.getJSONArray("tile-entities");
            final JSONArray particles = fps.getJSONArray("particles");
            final List<String> list = new ArrayList<String>();
            for (int j = 0; j < blocks.length(); ++j) {
                list.add(blocks.getString(j));
            }
            for (final String item : list) {
                try {
                    final String string = new String(item);
                    final Class<?> clazz = Class.forName(string);
                    if (!Block.class.isAssignableFrom(clazz)) {
                        continue;
                    }
                    final Class<? extends Block> block = (Class<? extends Block>)clazz;
                    if (SettingsPage.BLOCKS.contains(block)) {
                        continue;
                    }
                    SettingsPage.BLOCKS.add(block);
                }
                catch (IllegalArgumentException | LinkageError | ClassNotFoundException ex) {
                    final Throwable t;
                    final Throwable e = t;
                    e.printStackTrace();
                }
            }
            list.clear();
            for (int j = 0; j < entities.length(); ++j) {
                list.add(entities.getString(j));
            }
            for (final String item : list) {
                try {
                    String string = new String(item);
                    if (string.contains(" ")) {
                        string = string.split(" ", 2)[1];
                    }
                    final Class<?> clazz = Class.forName(string);
                    if (!Entity.class.isAssignableFrom(clazz)) {
                        continue;
                    }
                    final Class<? extends Entity> entity = (Class<? extends Entity>)clazz;
                    if (SettingsPage.ENTITIES.contains(entity)) {
                        continue;
                    }
                    SettingsPage.ENTITIES.add(entity);
                }
                catch (IllegalArgumentException | LinkageError | ClassNotFoundException ex2) {
                    final Throwable t2;
                    final Throwable e = t2;
                    e.printStackTrace();
                }
            }
            list.clear();
            for (int j = 0; j < tileentities.length(); ++j) {
                list.add(tileentities.getString(j));
            }
            for (final String item : list) {
                try {
                    final String string = new String(item);
                    final Class<?> clazz = Class.forName(string);
                    if (!TileEntity.class.isAssignableFrom(clazz)) {
                        continue;
                    }
                    final Class<? extends TileEntity> tileEntity = (Class<? extends TileEntity>)clazz;
                    if (SettingsPage.TILE_ENTITIES.contains(tileEntity)) {
                        continue;
                    }
                    SettingsPage.TILE_ENTITIES.add(tileEntity);
                }
                catch (IllegalArgumentException | LinkageError | ClassNotFoundException ex3) {
                    final Throwable t3;
                    final Throwable e = t3;
                    e.printStackTrace();
                }
            }
            list.clear();
            for (int j = 0; j < particles.length(); ++j) {
                list.add(particles.getString(j));
            }
            for (final String item : list) {
                try {
                    final String string = new String(item);
                    final EnumParticleTypes particle = EnumParticleTypes.valueOf(string);
                    if (particle == null) {
                        continue;
                    }
                    if (SettingsPage.PARTICLES.contains(particle)) {
                        continue;
                    }
                    SettingsPage.PARTICLES.add(particle);
                }
                catch (IllegalArgumentException e2) {
                    e2.printStackTrace();
                }
            }
            list.clear();
            final int[][] data = Crosshair.crosshair;
            for (int k = 0; k < data.length; ++k) {
                for (int ii = 0; ii < data[k].length; ++ii) {
                    data[k][ii] = 0;
                }
            }
            final String[] parts = obj.getString("crosshair-data").split("]");
            final List<List<Integer>> crosshairData = new ArrayList<List<Integer>>();
            for (String formatted : parts) {
                final String part = formatted;
                if (formatted.startsWith("[")) {
                    formatted = formatted.substring(1);
                }
                formatted = formatted.trim();
                final List<Integer> row = new ArrayList<Integer>();
                for (final String rowEntry : formatted.split(",")) {
                    final String entry2 = rowEntry.trim();
                    if (StringUtils.isInteger(entry2)) {
                        row.add(Integer.parseInt(entry2));
                    }
                }
                if (row.size() > 0) {
                    crosshairData.add(row);
                }
            }
            for (int l = 0; l < crosshairData.size(); ++l) {
                final List<Integer> crosshairList = crosshairData.get(l);
                if (l < data.length) {
                    for (int ii2 = 0; ii2 < crosshairList.size(); ++ii2) {
                        final int row2 = crosshairList.get(ii2);
                        if (ii2 < data[l].length) {
                            data[l][ii2] = row2;
                        }
                    }
                }
            }
        }
        catch (JSONException e3) {
            Athena.INSTANCE.getLog().error("Failed to load config " + this.name + ", improper json." + e3);
        }
        Athena.INSTANCE.getEventBus().post(new ConfigChangeEvent());
    }
    
    public void save(final String content) {
        try {
            this.file.writeToFile(content, false);
        }
        catch (IOException e) {
            Athena.INSTANCE.getLog().error("Failed to save config " + this.name + "." + e);
        }
    }
    
    public void save() {
        this.save(this.toString());
    }
    
    @Override
    public String toString() {
        final JSONObject config = new JSONObject();
        for (final Module mod : Athena.INSTANCE.getModuleManager().getModules()) {
            final JSONObject obj = new JSONObject();
            final JSONObject settingsObj = new JSONObject();
            final JSONObject hudObj = new JSONObject();
            for (final HUDElement element : mod.getHUDElements()) {
                final JSONObject elementObj = new JSONObject();
                elementObj.put("x", element.getX());
                elementObj.put("y", element.getY());
                elementObj.put("scale", element.getScale());
                elementObj.put("visible", element.isVisible());
                hudObj.put(element.getIdentifier(), elementObj);
            }
            obj.put("enabled", mod.isToggled());
            obj.put("bind", mod.getKeyBind());
            obj.put("bindtype", mod.getBindType().toString());
            for (final ConfigEntry entry : mod.getEntries()) {
                entry.appendToConfig(entry.getKey(), entry.getValue(mod), settingsObj);
            }
            obj.put("settings", settingsObj);
            obj.put("hud", hudObj);
            config.put(mod.getName(), obj);
        }
        config.put("theme", Athena.INSTANCE.getThemeManager().getTheme().name());
        config.put("cape", Athena.INSTANCE.getCosmeticsController().getCapeManager().getSelectedCape().getName());
        final JSONArray macros = new JSONArray();
        for (final Macro macro : Athena.INSTANCE.getMacroManager().getMacros()) {
            final JSONObject obj2 = new JSONObject();
            obj2.put("name", macro.getName());
            obj2.put("command", macro.getCommand());
            obj2.put("key", macro.getKey());
            obj2.put("enabled", macro.isEnabled());
            macros.put(obj2);
        }
        config.put("macros", macros);
        final JSONObject fps = new JSONObject();
        final List<String> buffer = new ArrayList<String>();
        for (final Class<?> clazz : SettingsPage.BLOCKS) {
            buffer.add(clazz.getCanonicalName());
        }
        fps.put("blocks", new JSONArray(buffer.toArray(new String[buffer.size()])));
        buffer.clear();
        for (final Class<?> clazz : SettingsPage.ENTITIES) {
            buffer.add(clazz.getCanonicalName());
        }
        fps.put("entities", new JSONArray(buffer.toArray(new String[buffer.size()])));
        buffer.clear();
        for (final Class<?> clazz : SettingsPage.TILE_ENTITIES) {
            buffer.add(clazz.getCanonicalName());
        }
        fps.put("tile-entities", new JSONArray(buffer.toArray(new String[buffer.size()])));
        buffer.clear();
        for (final EnumParticleTypes particle : SettingsPage.PARTICLES) {
            buffer.add(particle.toString());
        }
        fps.put("particles", new JSONArray(buffer.toArray(new String[buffer.size()])));
        buffer.clear();
        config.put("fps", fps);
        final StringBuilder crosshairData = new StringBuilder();
        final int[][] data = Crosshair.crosshair;
        for (int i = 0; i < data.length; ++i) {
            final StringBuilder inner = new StringBuilder();
            inner.append("[");
            for (int ii = 0; ii < data[i].length; ++ii) {
                if (inner.length() > 1) {
                    inner.append(",");
                }
                inner.append(data[i][ii]);
            }
            inner.append("]");
            if (!crosshairData.toString().isEmpty()) {
                crosshairData.append(", ");
            }
            crosshairData.append(inner.toString());
        }
        config.put("crosshair-data", crosshairData);
        return config.toString(4);
    }
    
    public String getName() {
        return this.name;
    }
    
    public void delete() {
        if (this.parent.lastLoadedConfig == this) {
            this.parent.lastLoadedConfig = null;
        }
        this.file.getFile().delete();
    }
    
    public FileHandler getFileHandler() {
        return this.file;
    }
    
    public boolean isEnabled() {
        return this.parent.getLoadedConfig() != null && this.parent.getLoadedConfig().getName().equalsIgnoreCase(this.getName());
    }
}
