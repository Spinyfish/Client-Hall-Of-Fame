package dev.africa.pandaware.manager.file;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.file.FileObject;
import dev.africa.pandaware.api.interfaces.Initializable;
import dev.africa.pandaware.api.interfaces.MinecraftInstance;
import dev.africa.pandaware.impl.container.Container;
import dev.africa.pandaware.impl.file.AccountsFile;
import dev.africa.pandaware.impl.file.SettingsFile;
import dev.africa.pandaware.utils.client.Printer;
import lombok.Getter;

import java.io.File;
import java.util.Arrays;

@Getter
public class FileManager extends Container<FileObject> implements Initializable, MinecraftInstance {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private File rootFolder;

    void addItems() {
        this.getItems().addAll(Arrays.asList(
                new SettingsFile(this.rootFolder, "settings.json", this.gson),
                new AccountsFile(this.rootFolder, "accounts.json", this.gson)
        ));
    }

    @Override
    public void init() {
        this.rootFolder = new File(mc.mcDataDir, Client.getInstance().getManifest().getClientName());
        if (!this.rootFolder.exists()) {
            this.rootFolder.mkdir();
        }

        this.addItems();

        this.getItems().forEach(fileObject -> {
            try {
                if (!fileObject.getFile().exists()) {
                    fileObject.getFile().createNewFile();
                    fileObject.save();
                    return;
                }

                fileObject.load();
            } catch (Exception e) {
                Printer.consoleError("Failed to load file: " + fileObject.getFile().getName());
            }
        });
    }

    @Override
    public void shutdown() {
        this.saveAll();
    }

    public void saveAll() {
        this.getItems().forEach(fileObject -> {
            try {
                fileObject.save();
            } catch (Exception e) {
                Printer.consoleError("Failed to save file: " + fileObject.getFile().getName());
            }
        });
    }

    public <T extends FileObject> T getByClass(Class<? extends FileObject> clazz) {
        return (T) this.getItems().stream().filter(fileObject -> fileObject.getClass() == clazz)
                .findFirst().orElse(null);
    }
}
