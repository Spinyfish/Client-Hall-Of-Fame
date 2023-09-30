package club.pulsive.impl.util.render;

import club.pulsive.api.main.Pulsive;
import club.pulsive.impl.module.Module;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;

public class DraggablesManager {
    @Getter
    private final HashMap<String, Draggable> draggables = new HashMap<>();

    private final File DRAG_DATA = new File(Pulsive.INSTANCE.getClientDir().toFile(), "draggable.json");
    private final Gson GSON = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();

    public void saveDraggableData() {
        if (!DRAG_DATA.exists()) {
            DRAG_DATA.getParentFile().mkdirs();
        }
        try {
            Files.write(DRAG_DATA.toPath(), GSON.toJson(draggables.values()).getBytes(StandardCharsets.UTF_8));
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("Failed to save draggable");
        }
    }

    public void loadDraggableData() {
        if (!DRAG_DATA.exists()) {
            System.out.println("No data found for draggable");
            return;
        }
        Draggable[] draggables1;
        try {
            draggables1 = GSON.fromJson(new String(Files.readAllBytes(DRAG_DATA.toPath()), StandardCharsets.UTF_8), Draggable[].class);
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("Failed to load draggables");
            return;
        }

        for(Draggable draggable3 : draggables1) {
            Draggable currentDrag = draggables.get(draggable3.getName());
            currentDrag.setX(draggable3.getX());
            currentDrag.setY(draggable3.getY());
            draggables.put(draggable3.getName(), currentDrag);
        }
    }


    public Draggable createNewDraggable(Module module, String name, float x, float y) {
        draggables.put(name, new Draggable(module, name, x, y));
        return draggables.get(name);
    }

}