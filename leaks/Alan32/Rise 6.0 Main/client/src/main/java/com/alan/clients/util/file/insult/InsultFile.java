package com.alan.clients.util.file.insult;

import com.alan.clients.Client;
import com.alan.clients.module.impl.other.Insults;
import com.alan.clients.util.file.File;
import com.alan.clients.util.file.FileType;
import com.alan.clients.value.impl.SubMode;

import java.nio.file.Files;

public final class InsultFile extends File {

    public InsultFile(final java.io.File file, final FileType fileType) {
        super(file, fileType);
    }

    @Override
    public boolean read() {
        if (!this.getFile().exists() || !this.getFile().isFile() || !this.getFile().canRead()) return false;

        try {
            final Insults insults = Client.INSTANCE.getModuleManager().get(Insults.class);
            final String name = this.getFile().getName().replace(".txt", "");

            insults.mode.add(new SubMode(name));
            insults.map.put(name, Files.readAllLines(this.getFile().toPath()));

            return true;
        } catch (final Exception ex) {
            ex.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean write() {
        try {
            if (!this.getFile().exists()) this.getFile().createNewFile();

            return true;
        } catch (final Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
