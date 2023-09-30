package dev.africa.pandaware.manager.script.module;

import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.module.interfaces.Category;
import dev.africa.pandaware.impl.script.Script;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.lwjgl.input.Keyboard;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public class ScriptModuleManager {
    private final Script script;

    public Module register(String name, String description, String category) {
        Category categoryEnum = Arrays.stream(Category.values())
                .filter(cat -> cat.getLabel().equalsIgnoreCase(category))
                .findFirst()
                .orElse(Category.MISC);

        Module module = new Module(name, description, categoryEnum, Keyboard.KEY_NONE, true);

        this.script.setModule(module);

        Client.getInstance().getScriptManager().getItems().add(this.script);
        Client.getInstance().getModuleManager().getMap().put(module.getClass(), module);

        return module;
    }

    public boolean isModuleEnabled(String name) {
        return Client.getInstance().getModuleManager()
                .getByName(name)
                .map(module -> module.getData().isEnabled())
                .orElse(false);
    }

    public void toggleModule(String name) {
        Client.getInstance().getModuleManager()
                .getByName(name)
                .ifPresent(Module::toggle);
    }

    public String getModuleMode(String name) {
        return Client.getInstance().getModuleManager()
                .getMap()
                .values()
                .stream()
                .filter(module -> module.getData().getName().equalsIgnoreCase(name) &&
                        module.getCurrentMode() != null)
                .map(module -> module.getCurrentMode().getName())
                .findFirst()
                .orElse("NO_MODE_FOUND");
    }
}
