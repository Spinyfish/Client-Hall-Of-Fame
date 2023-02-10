/**
 * @project Myth
 * @author CodeMan
 * @at 05.02.23, 16:36
 */
package dev.myth.managers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.myth.api.command.Command;
import dev.myth.api.draggable.DraggableComponent;
import dev.myth.api.draggable.DraggableHandler;
import dev.myth.api.manager.Manager;
import dev.myth.api.utils.FileUtil;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.Display;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DraggableManager implements Manager {

    @Getter
    private final ArrayList<DraggableComponent> draggables = new ArrayList<>();

    @Getter
    private DraggableHandler draggableHandler;

    private final File file = new File("myth/draggables.json");
    private JsonObject config;

    @Override
    public void run() {
        draggableHandler = new DraggableHandler();
        if (file.exists()) {
            config = FileUtil.readJsonFromFile(file.getAbsolutePath());
        }
    }

    @Override
    public void shutdown() {
        for (int i = 0; i < draggables.size(); i++) {
            DraggableComponent draggable = draggables.get(i);
            save(draggable);
        }
        FileUtil.writeJsonToFile(config, file.getAbsolutePath());
    }

    public void registerDraggable(DraggableComponent draggable) {
        if (!draggables.contains(draggable)) draggables.add(draggable);
        load(draggable);
    }

    public void unregisterDraggable(DraggableComponent draggable) {
        draggables.remove(draggable);
        save(draggable);
    }

    public void save(DraggableComponent draggable) {
        if (config != null) {
            if (config.has(draggable.getName())) {
                config.remove(draggable.getName());
            }
        } else {
            config = new JsonObject();
        }
        JsonObject draggableConfig = new JsonObject();

        double y = draggable.getY();
        double x = draggable.getX();

        draggableConfig.addProperty("x", x);
        draggableConfig.addProperty("y", y);
        config.add(draggable.getName(), draggableConfig);
    }

    public void load(DraggableComponent draggable) {
        if (config != null) {
            if (config.has(draggable.getName())) {
                draggable.setX(config.get(draggable.getName()).getAsJsonObject().get("x").getAsDouble());
                draggable.setY(config.get(draggable.getName()).getAsJsonObject().get("y").getAsDouble());
            }
        } else {
            config = new JsonObject();
        }
    }
}
