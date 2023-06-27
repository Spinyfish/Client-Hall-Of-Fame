package rip.athena.client.modules;

import org.reflections.scanners.*;
import org.reflections.*;
import java.util.*;
import rip.athena.client.*;

public class ModuleManager
{
    private final HashMap<Class<? extends Module>, Module> modules;
    
    public ModuleManager() {
        this.modules = new HashMap<Class<? extends Module>, Module>();
        final Reflections reflections = new Reflections("rip.athena.client.modules", new Scanner[0]);
        final Set<Class<? extends Module>> classes = (Set<Class<? extends Module>>)reflections.getSubTypesOf((Class)Module.class);
        for (final Class<?> clazz : classes) {
            try {
                final Module module = (Module)clazz.newInstance();
                this.modules.put(module.getClass(), module);
            }
            catch (InstantiationException | IllegalAccessException ex2) {
                final ReflectiveOperationException ex;
                final ReflectiveOperationException e = ex;
                e.printStackTrace();
            }
        }
    }
    
    public List<Module> getModules() {
        final List<Module> moduleList = new ArrayList<Module>(this.modules.values());
        if (this.modules.isEmpty()) {
            Athena.INSTANCE.getLog().warn("No modules registered.");
        }
        return moduleList;
    }
    
    public Module getModule(final String name) {
        final Module requestedModule = this.modules.values().stream().filter(m -> m.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
        if (requestedModule == null) {
            Athena.INSTANCE.getLog().warn("Tried accessing non-existing module: " + name);
        }
        return requestedModule;
    }
    
    public Module get(final Class<? extends Module> module) {
        final Module requestedModule = this.modules.get(module);
        if (requestedModule == null) {
            Athena.INSTANCE.getLog().warn("Tried accessing non existing module " + module.getSimpleName());
        }
        return requestedModule;
    }
}
