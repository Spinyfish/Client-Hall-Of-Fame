package de.lirium.base.drag;

import com.google.gson.*;
import de.lirium.base.helper.OverlappingHelper;
import de.lirium.impl.module.ModuleFeature;
import de.lirium.util.interfaces.Logger;
import lombok.extern.java.Log;
import net.minecraft.client.Minecraft;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class DragHandler {
    public static final HashMap<String, Draggable> draggableHashMap = new HashMap<>();

    public static final OverlappingHelper<Draggable> overlappingHelper = new OverlappingHelper<>();
    
    private static final File DIR = new File(Minecraft.getMinecraft().mcDataDir, "Lirium/Data");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static boolean dragged = false;

    public static Draggable setupDrag(ModuleFeature module, String name, float startX, float startY, boolean resizeCompatible) {
        final Draggable draggable = new Draggable(name, module, startX, startY, resizeCompatible);
        draggableHashMap.put(name, draggable);
        overlappingHelper.elements.add(draggable);
        DragHandler.loadDraggable();
        return draggableHashMap.get(name);
    }

    public static void save() {
        dragged = false;
        Logger.print("Saving draggable settings");
        if (!DIR.exists())
            if (DIR.mkdirs())
                Logger.print("Created data directory");
            else
                Logger.print("Couldn't create directory");
        if (DIR.exists()) {
            final FileWriter fileWriter;
            try {
                fileWriter = new FileWriter(DIR + "/drag.json");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            GSON.toJson(saveDraggable(), fileWriter);
            try {
                fileWriter.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            Logger.print("Directory doesn't exist");
        }
    }

    public static void loadDraggable() {
        final File f = new File(DIR + "\\" + "drag" + ".json");
        if (f.exists()) {
            try {
                load(GSON.fromJson(new FileReader(f), JsonElement.class));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        } else {
            Logger.print("Draggable file is missing");
        }
    }

    private static void load(final JsonElement jsonElement) {
        for (Map.Entry<String, JsonElement> entry : jsonElement.getAsJsonObject().entrySet()) {
            final JsonObject jsonModule = (JsonObject) entry.getValue();
            if (jsonModule.has("Data")) {
                String x = jsonModule.get("Data").getAsJsonObject().get("x").getAsString();
                String y = jsonModule.get("Data").getAsJsonObject().get("y").getAsString();
                String name = entry.getKey();
                final Draggable currentDrag = draggableHashMap.get(name);
                if (currentDrag == null) return;
                currentDrag.setXPosition(Float.parseFloat(x));
                currentDrag.setYPosition(Float.parseFloat(y));
                currentDrag.setStartX(Float.parseFloat(x));
                currentDrag.setStartY(Float.parseFloat(y));
                currentDrag.screenPosition = ScreenPosition.fromAbsolute((int) currentDrag.getXPosition(), (int) currentDrag.getYPosition());
            }
        }
    }

    public static JsonObject saveDraggable() {
        final JsonObject jsonObject = new JsonObject();
        DragHandler.draggableHashMap.forEach((s, draggable) -> {
            JsonObject nameJson = new JsonObject();
            nameJson.addProperty("DragName", draggable.getName());
            final JsonObject jsonObject1 = new JsonObject();
            jsonObject1.addProperty("x", draggable.getXPosition());
            jsonObject1.addProperty("y", draggable.getYPosition());

            nameJson.add("Data", jsonObject1);
            jsonObject.add(draggable.getName(), nameJson);
        });
        return jsonObject;
    }

}
