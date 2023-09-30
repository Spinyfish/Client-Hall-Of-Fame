package club.pulsive.impl.managers;

import club.pulsive.api.minecraft.MinecraftUtil;
import club.pulsive.impl.module.Category;
import club.pulsive.impl.module.Module;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ModuleManager implements MinecraftUtil {

    private HashMap<Class<? extends Module>, Module> modules = new HashMap<>();

    public void init() {
        Reflections reflections = new Reflections("club.pulsive.impl.module");
        Set<Class<? extends Module>> classes = reflections.getSubTypesOf(Module.class);
        try {
            for (Class<?> aClass : classes) {
                try {
                    Module module = (Module) aClass.newInstance();
                    modules.put(module.getClass(), module);
                    module.init();
                    if (module.getCategory() == Category.CLIENT) {
                        module.setHidden(true);
                        module.setToggled(true);
                    }
                } catch (InstantiationException | IllegalAccessException ex) {
                    ex.printStackTrace();
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public List<Module> getModules() {
        return new ArrayList<>(this.modules.values());
    }

    public List<Module> getModulesInCategory(Category c) {
        return this.modules.values().stream().filter(m -> m.getCategory() == c).collect(Collectors.toList());
    }

    public <T extends Module> T getModule(Class<T> tClass) {
        return (T) getModules().stream().filter(mod -> mod.getClass().equals(tClass)).findFirst().orElse(null);
    }

    public <T extends Module> T getModuleByName(String name) {
        return (T) this.modules.values().stream().filter(m -> m.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public List<Module> getModulesContains(String text) {
        return this.modules.values().stream().filter(m -> m.getName().toLowerCase().contains(text.toLowerCase())).collect(Collectors.toList());
    }

    public final List<Module> getToggledModules() {
        return this.modules.values().stream().filter(Module::isToggled).collect(Collectors.toList());
    }

}
