package me.felix.lsShaderParser;

import me.felix.lsShaderParser.file.ReadedFile;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestMain {

    public static void main(String[] args) {
        final String filename = "TestShader.ls";

        final Path pathToFile = Paths.get(filename);
        System.out.println(pathToFile.toAbsolutePath());
        final ReadedFile readedFile = new ReadedFile(pathToFile.toFile());
        System.out.println(readedFile.getReadResult());
    }

}
