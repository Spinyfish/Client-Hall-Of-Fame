package me.felix.lsShaderParser.file;

import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ReadedFile extends ReadableFile {

    @Getter
    private String readResult;

    private final File file;

    public ReadedFile(final File file) {
        this.file = file;
        this.readFile();
    }

    @Override
    void readFile() {
        try {
            this.readResult = readFromInputStream(Files.newInputStream(file.toPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
