/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.api.manager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.impl.module.movement.HighJump;
import wtf.monsoon.impl.module.movement.LongJump;
import wtf.monsoon.impl.module.movement.Speed;

public class ModuleManager {
    private final LinkedHashMap<Class<? extends Module>, Module> modules = new LinkedHashMap();
    private final List<Module> retardModules = new ArrayList<Module>();

    public void putModule(Class<? extends Module> clazz, Module module) {
        this.modules.put(clazz, module);
        this.retardModules.add(module);
    }

    public List<Module> getModules() {
        return this.retardModules;
    }

    public List<Module> getSortedModules() {
        return this.getModules().stream().sorted(Comparator.comparing(module -> Wrapper.getFontUtil().productSans.getStringWidth(((Module)module).getDisplayName())).reversed()).collect(Collectors.toList());
    }

    public <T extends Module> T getModule(Class<T> clazz) {
        return (T)this.modules.get(clazz);
    }

    public Module getModuleByName(String name) {
        for (Module module : this.modules.values()) {
            if (!module.getName().replace(" ", "").equalsIgnoreCase(name)) continue;
            return module;
        }
        return null;
    }

    public List<Module> getModulesByName(String name) {
        ArrayList<Module> shit = new ArrayList<Module>();
        for (Module module : this.getModules()) {
            if (!module.getName().replace(" ", "").equalsIgnoreCase(name)) continue;
            shit.add(module);
        }
        return shit;
    }

    public List<Module> getModulesByCategory(Category category) {
        return this.getModules().stream().filter(module -> module.getCategory() == category).collect(Collectors.toList());
    }

    public List<Module> getModulesToDisableOnFlag() {
        ArrayList<Module> moduleList = new ArrayList<Module>();
        moduleList.add(this.getModule(Speed.class));
        moduleList.add(this.getModule(LongJump.class));
        moduleList.add(this.getModule(HighJump.class));
        return moduleList;
    }

    public List<Module> getRetardModules() {
        return this.retardModules;
    }
}

