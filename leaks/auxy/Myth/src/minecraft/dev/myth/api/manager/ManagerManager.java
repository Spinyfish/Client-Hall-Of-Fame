/**
 * @project Myth
 * @author Skush/Duzey
 * @at 05.08.2022
 */
package dev.myth.api.manager;

import dev.myth.managers.*;
import lombok.Getter;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class ManagerManager {

    @Getter
    private final Map<Class<? extends Manager>, Manager> managers = new HashMap<>();

    public ManagerManager() {
        this.addManager(CommandManager.class);
        this.addManager(ShaderManager.class);
        this.addManager(ConfigManager.class);
        this.addManager(FriendManager.class);
        this.addManager(DraggableManager.class);
        this.addManager(FeatureManager.class);

        getManager(FeatureManager.class).run();
        for(final Manager manager : this.managers.values()) {
//            System.out.println(manager.getClass().getSimpleName());
            if(!(manager instanceof FeatureManager)) new Thread(manager::run).start();
        }
    }

    public void shutdown() {
        this.managers.forEach((aClass, manager) -> manager.shutdown());
    }

    private <T extends Manager> void addManager(final Class<T> clazz) {
        try {
            this.managers.put(clazz, clazz.getConstructor().newInstance());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    public <T extends Manager> T getManager(final Class<T> clazz) {
        return (T) this.managers.get(clazz);
    }
}
