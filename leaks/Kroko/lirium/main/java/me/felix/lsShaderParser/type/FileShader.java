package me.felix.lsShaderParser.type;

import me.felix.lsShaderParser.file.ReadedFile;
import me.felix.lsShaderParser.type.result.Result;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class FileShader {

    public void read(File file) {
        final ReadedFile readedFile = new ReadedFile(file);

    }

    public static void main(String[] args) {
        readFile();
    }

    public static void readFile() {
        final @NotNull File file = new File("TestShader.ls");

        final ArrayList<String> lines = new ArrayList<>();
        try(final Scanner scanner = new Scanner(file)) {
            while(scanner.hasNext()) lines.add(scanner.nextLine());
        } catch (final FileNotFoundException ignored) {}

        boolean breaking = false;
        if (!lines.get(0).startsWith("#Submit from ")) breaking = true;
        if (!lines.get(1).startsWith("#Date ")) breaking = true;
        if (!lines.get(2).startsWith("#Type ")) breaking = true;
        if (!lines.get(3).startsWith("#Main ")) breaking = true;

        if (breaking) {
            // hier dein code wenn es es kein submit, date, type oder main hat
            System.exit(-1);
            return;
        }

        final String author = lines.get(0).split("Submit from ")[1];
        final String date = lines.get(0).split("Date ")[1];
        final String type = lines.get(0).split("Type ")[1];
        final String method = lines.get(0).split("Main ")[1];
        System.out.println(author + " " + date + " " + type + method);
    }


}
