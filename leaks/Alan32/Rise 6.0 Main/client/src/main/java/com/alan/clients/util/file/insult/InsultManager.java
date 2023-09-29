package com.alan.clients.util.file.insult;

import com.alan.clients.util.file.FileManager;
import com.alan.clients.util.file.FileType;

import java.io.File;
import java.util.ArrayList;

public final class InsultManager extends ArrayList<InsultFile> {

    public static final File INSULT_DIRECTORY = new File(FileManager.DIRECTORY, "insults");

    public void init() {
        if (!INSULT_DIRECTORY.exists()) {
            INSULT_DIRECTORY.mkdir();
        }
    }

    public InsultFile get(final String insults) {
        for (final InsultFile insultFile : this) {
            if (insultFile.getFile().getName().equalsIgnoreCase(insults + ".txt")) {
                return insultFile;
            }
        }

        return null;
    }

    public void set(final String insults) {
        final File file = new File(INSULT_DIRECTORY, insults + ".txt");
        InsultFile insultFile = get(insults);

        if (insultFile == null) {
            insultFile = new InsultFile(file, FileType.INSULT);
            add(insultFile);

            System.out.println("Creating new ..");
        } else {
            System.out.println("Overwriting existing ..");
        }

        insultFile.write();

        System.out.println("Insults saved to files.");
    }

    public boolean update() {
        clear();

        final File[] files = INSULT_DIRECTORY.listFiles();

        if (files == null)
            return false;

        for (final File file : files) {
            if (file.getName().endsWith(".txt")) {
                add(new InsultFile(file, FileType.INSULT));
            }
        }

        return true;
    }

    public boolean delete(final String insults) {
        final InsultFile insultFile = get(insults);

        if (insultFile == null)
            return false;

        remove(insultFile);
        return insultFile.getFile().delete();
    }
}
