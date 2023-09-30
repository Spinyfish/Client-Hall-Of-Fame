package dev.africa.pandaware.api.file;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;

import java.io.File;

@Getter
@Setter
public abstract class FileObject {
    private final File file;
    private final Gson gson;

    public FileObject(File rootFolder, String fileName, Gson gson) {
        this.file = new File(rootFolder, fileName);
        this.gson = gson;
    }

    public abstract void save() throws Exception;

    public abstract void load() throws Exception;
}
