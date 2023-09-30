package com.alan.clients.util.file.alt;

import com.alan.clients.util.account.Account;
import com.alan.clients.util.file.FileManager;
import com.alan.clients.util.file.FileType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Patrick
 * @since 10/19/2021
 */
public class AltManager extends ArrayList<AltFile> {

    public static final File ALT_DIRECTORY = new File(FileManager.DIRECTORY, "alts");
    private final List<Account> accounts = new ArrayList<>();

    public void init() {
        if (!ALT_DIRECTORY.exists()) {
            ALT_DIRECTORY.mkdir();
        }
    }

    public AltFile get(final String alts) {
        final File file = new File(AltManager.ALT_DIRECTORY, alts + ".json");
        return new AltFile(file, FileType.ACCOUNT);
    }

    public void set(final String alts) {
        final File file = new File(ALT_DIRECTORY, alts + ".json");
        AltFile altFile = get(alts);

        if (altFile == null) {
            altFile = new AltFile(file, FileType.ACCOUNT);
            add(altFile);

            System.out.println("Creating new alts...");
        } else {
            System.out.println("Overwriting existing alts...");
        }

        altFile.write();

        System.out.println("Config saved to files.");
    }

    public boolean update() {
        clear();

        final File[] files = ALT_DIRECTORY.listFiles();

        if (files == null)
            return false;

        for (final File file : files) {
            if (file.getName().endsWith(".json")) {
                add(new AltFile(file, FileType.ACCOUNT));
            }
        }

        return true;
    }

    public boolean delete(final String config) {
        final AltFile altFile = get(config);

        if (altFile == null)
            return false;

        remove(altFile);
        return altFile.getFile().delete();
    }

    public List<Account> getAccounts() {
        return accounts;
    }
}