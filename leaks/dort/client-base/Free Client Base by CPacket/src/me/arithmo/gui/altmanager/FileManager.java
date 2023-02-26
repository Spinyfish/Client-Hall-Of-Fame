/*
 * Decompiled with CFR 0_122.
 */
package me.arithmo.gui.altmanager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import ProxyManager.Proxies;
import net.minecraft.client.Minecraft;

public class FileManager {
    public static ArrayList<CustomFile> Files = new ArrayList();
    private static File directory = new File(String.valueOf(Minecraft.getMinecraft().mcDataDir.toString()) + "\\" + "MemeClient");

    public FileManager() {
        this.makeDirectories();
        Files.add(new Alts("alts", false, true));
        Files.add(new Proxies("proxies", false, true));
    }

    public void loadFiles() {
        for (CustomFile f : Files) {
            try {
            	if (!f.isloaded) {
            		f.loadFile();
            	}
                if (!f.loadOnStart()) continue;
                f.loadFile();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void saveFiles() {
        for (CustomFile f : Files) {
            try {
                f.saveFile();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public CustomFile getFile(Class<? extends CustomFile> clazz) {
        for (CustomFile file : Files) {
        	if (file.isloaded)
            if (file.getClass() != clazz) continue;
            return file;
        }
        return null;
    }

    public void makeDirectories() {
        if (!directory.exists()) {
            if (directory.mkdir()) {
                System.out.println("Directory is created!");
            } else {
                System.out.println("Failed to create directory!");
            }
        }
    }

    public static abstract class CustomFile {
        public boolean isloaded;
		private final File file;
        private final String name;
        private boolean load;

        public CustomFile(String name, boolean Module2, boolean loadOnStart) {
            this.name = name;
            this.load = loadOnStart;
            this.file = new File(directory, String.valueOf(name) + ".txt");
            if (!this.file.exists()) {
                try {
                    this.saveFile();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public final File getFile() {
            return this.file;
        }

        private boolean loadOnStart() {
            return this.load;
        }

        public final String getName() {
            return this.name;
        }

        public abstract void loadFile() throws IOException;

        public abstract void saveFile() throws IOException;
    }

}

