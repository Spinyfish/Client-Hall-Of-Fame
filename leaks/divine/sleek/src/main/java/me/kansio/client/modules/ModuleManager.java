package me.kansio.client.modules;

import lombok.Getter;
import me.kansio.client.Client;
import me.kansio.client.modules.api.ModuleCategory;
import me.kansio.client.modules.impl.Module;
import me.kansio.client.value.Value;
import me.kansio.client.utils.font.MCFontRenderer;
import me.kansio.client.utils.java.ReflectUtils;
import net.minecraft.client.gui.FontRenderer;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

public class ModuleManager {

    @Getter
    private final ArrayList<Module> modules = new ArrayList<>();

    public ModuleManager() {
         for (Class<?> mod : ReflectUtils.getReflects(this.getClass().getPackage().getName() + ".impl", Module.class)) {
             try {
                 Module module = (Module) mod.getDeclaredConstructor().newInstance();
                 registerModule(module);
             } catch (Exception e) {
                 e.printStackTrace();
             }
         }
    }

    public void registerModule(Module module) {
        modules.add(module);

        for (final Field field : module.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                final Object obj = field.get(module);

                if (obj instanceof Value)
                    Collections.addAll(Client.getInstance().getValueManager().getObjects(), (Value) obj);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        //Collections.addAll(Client.getInstance().getValueManager().getObjects(), values);
    }

    public void reloadModules() {
        for (Module mod : modules) {
            if (mod.isToggled())
                mod.toggle();
        }

        modules.clear();

        //load them using reflections
        for (Class<?> mod : ReflectUtils.getReflects(this.getClass().getPackage().getName() + ".impl", Module.class)) {
            try {
                Module module = (Module) mod.getDeclaredConstructor().newInstance();
                registerModule(module);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public List<Module> getModulesSorted(FontRenderer customFontRenderer) {
        List<Module> moduleList = new ArrayList<>(modules);
        moduleList.sort((a, b) -> {
            String dataA = a.getFormattedSuffix() == null ? "" : a.getFormattedSuffix();
            String dataB = b.getFormattedSuffix() == null ? "" : b.getFormattedSuffix();
            String nameA = a.getName();
            String nameB = b.getName();

            int first = (int) customFontRenderer.getStringWidth(nameA + dataA);
            int second = (int) customFontRenderer.getStringWidth(nameB + dataB);
            return second - first;
        });
        return moduleList;
    }

    public List<Module> getModulesSorted(MCFontRenderer customFontRenderer) {
        List<Module> moduleList = new ArrayList<>(modules);
        moduleList.sort((a, b) -> {
            String dataA = a.getFormattedSuffix() == null ? "" : a.getFormattedSuffix();
            String dataB = b.getFormattedSuffix() == null ? "" : b.getFormattedSuffix();
            String nameA = a.getName();
            String nameB = b.getName();

            int first = (int) customFontRenderer.getStringWidth(nameA + dataA);
            int second = (int) customFontRenderer.getStringWidth(nameB + dataB);
            return second - first;
        });
        return moduleList;
    }

    public void sort(FontRenderer fontRenderer) {
        modules.sort((a, b) -> {
            String dataA = a.getFormattedSuffix() == null ? "" : a.getFormattedSuffix();
            String dataB = b.getFormattedSuffix() == null ? "" : b.getFormattedSuffix();
            int first = fontRenderer.getStringWidth(a.getName() + dataA);
            int second = fontRenderer.getStringWidth(b.getName() + dataB);
            return second - first;
        });
    }

    public <T extends Module> T getModuleByClass(Class<? extends T> clazz) {
        return (T) modules.stream().filter(element -> element.getClass().equals(clazz)).findFirst().orElseThrow((() -> new NoSuchElementException("RETARD ALERT: Element belonging to class '" + clazz.getName() + "' not found")));
    }

    public Module getModuleByName(String name) {
        for (Module module : modules) {
            if (module.getName().equalsIgnoreCase(name)) {
                return module;
            }
        }
        return null;
    }

    public Module getModuleByNameIgnoreSpace(String name) {
        for (Module module : modules) {
            if (module.getName().replaceAll(" ", "").equalsIgnoreCase(name)) {
                return module;
            }
        }
        return null;
    }

    public List<Module> getModulesFromCategory(ModuleCategory category) {
        ArrayList<Module> mods = new ArrayList<>();
        for (Module module : modules) {
            if (module.getCategory() == category) {
                mods.add(module);
            }
        }
        return mods;
    }

}
