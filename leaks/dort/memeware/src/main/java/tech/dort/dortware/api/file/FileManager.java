package tech.dort.dortware.api.file;

import tech.dort.dortware.api.manager.Manager;
import tech.dort.dortware.impl.files.GuiStateFile;
import tech.dort.dortware.impl.files.KeybindsFile;
import tech.dort.dortware.impl.files.SettingsFile;

import java.io.*;
import java.util.ArrayList;

public class FileManager extends Manager<MFile> {

    public FileManager() {
        super(new ArrayList<>());
    }

    @Override
    public void onCreated() {
        this.getObjects().add(new KeybindsFile());
        this.getObjects().add(new SettingsFile());
        this.getObjects().add(new GuiStateFile());
        this.getObjects().forEach(file -> file.load(this));
    }

    public void writeFile(MFile file, String contents) throws IOException {
        File jFile = new File("Dortware/");
        if (!jFile.exists()) {
            jFile.mkdirs();
        }

        jFile = new File("Dortware/" + file.getPath() + file.getName());
        if (!jFile.exists()) {
            jFile.createNewFile();
        }

        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("Dortware/" + file.getPath() + file.getName()));
        bufferedWriter.write(contents);
        bufferedWriter.close();
    }

    public BufferedReader getBufferedReaderForFile(MFile file) throws FileNotFoundException {
        return new BufferedReader(new FileReader("Dortware/" + file.getPath() + file.getName()));
    }

    public void initializeFile(MFile file) {
        try {
            writeFile(file, "");
        } catch (Exception ignored) {

        }
    }
}
