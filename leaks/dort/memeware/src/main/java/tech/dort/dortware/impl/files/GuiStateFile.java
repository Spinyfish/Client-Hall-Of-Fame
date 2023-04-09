package tech.dort.dortware.impl.files;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import tech.dort.dortware.Client;
import tech.dort.dortware.api.file.FileManager;
import tech.dort.dortware.api.file.MFile;
import tech.dort.dortware.impl.gui.click.PaneState;
import tech.dort.dortware.impl.modules.render.ClickGUI;

import java.io.BufferedReader;

public class GuiStateFile extends MFile {

    public GuiStateFile() {
        super("GuiState.json");
    }

    private final Gson gson = new Gson();
    final ClickGUI clickGUI = Client.INSTANCE.getModuleManager().get(ClickGUI.class);

    @Override
    public void load(FileManager fileManager) {
        System.out.println("Loaded.");
        BufferedReader fileContents;
        try {
            fileContents = fileManager.getBufferedReaderForFile(this);
        } catch (Exception e) {
            fileManager.initializeFile(this);
            return;
        }
        try {
            JsonArray jsonArray = gson.fromJson(fileContents, JsonArray.class);
            if (jsonArray == null) {
                return;
            }
            for (JsonElement jsonElement : jsonArray) {
                clickGUI.addPreloadPaneState(gson.fromJson(jsonElement, PaneState.class));
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void save() {
        final FileManager fileManager = Client.INSTANCE.getFileManager();
        final JsonArray json = new JsonArray();

        for (PaneState state : clickGUI.getPaneStates()) {
            json.add(gson.toJsonTree(state));
        }

        try {
            fileManager.writeFile(this, json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
