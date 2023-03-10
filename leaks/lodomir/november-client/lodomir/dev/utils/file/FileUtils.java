/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.utils.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import net.minecraft.client.Minecraft;

public class FileUtils {
    protected final Minecraft mc = Minecraft.getMinecraft();
    private final String SEPARATOR = File.separator;
    private final String PATH;

    public FileUtils() {
        this.PATH = this.mc.mcDataDir.getAbsolutePath() + this.SEPARATOR + "november" + this.SEPARATOR;
    }

    public boolean exists(String fileName) {
        return this.getFileOrPath(fileName).exists();
    }

    public boolean exists(File file) {
        return file.exists();
    }

    public boolean dirExists() {
        return new File(this.PATH).exists();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean saveFile(String fileName, boolean override, String content) {
        BufferedWriter writer = null;
        try {
            File file = this.getFileOrPath(fileName);
            if (!this.exists(file)) {
                this.createDir();
                this.createFile(file);
            } else if (!override) {
                boolean bl = false;
                return bl;
            }
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(content);
            writer.flush();
        }
        catch (Throwable t) {
            t.printStackTrace();
            boolean bl = false;
            return bl;
        }
        finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            }
            catch (Throwable t) {
                t.printStackTrace();
                throw new IllegalStateException("Failed to close writer!");
            }
        }
        return true;
    }

    public String loadFile(String fileName) {
        try {
            String line;
            File file = this.getFileOrPath(fileName);
            if (!this.exists(file)) {
                return null;
            }
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String content = reader.readLine();
            while ((line = reader.readLine()) != null) {
                content = content + "\r\n" + line;
            }
            reader.close();
            return content;
        }
        catch (Throwable t) {
            t.printStackTrace();
            throw new IllegalStateException("Failed to read file!");
        }
    }

    public void createDir() {
        new File(this.PATH).mkdirs();
    }

    public void createDirectory(String directoryName) {
        this.getFileOrPath(directoryName.replace("\\", this.SEPARATOR)).mkdirs();
    }

    public void createFile(String fileName) {
        try {
            this.getFileOrPath(fileName).mkdirs();
            this.getFileOrPath(fileName).createNewFile();
        }
        catch (Throwable t) {
            throw new IllegalStateException("Unable to create Rise directory!", t);
        }
    }

    public File[] listFiles(String path) {
        return this.getFileOrPath(path).listFiles();
    }

    public File[] listFiles(File file) {
        return file.listFiles();
    }

    public File getFileOrPath(String fileName) {
        return new File(this.PATH + fileName.replace("\\", this.SEPARATOR));
    }

    public void delete(String fileName) {
        if (this.exists(fileName) && !this.getFileOrPath(fileName).delete()) {
            throw new IllegalStateException("Unable to delete file!");
        }
    }

    public void delete(File file) {
        if (this.exists(file) && !file.delete()) {
            throw new IllegalStateException("Unable to delete file!");
        }
    }

    private void createFile(File file) {
        try {
            file.createNewFile();
        }
        catch (Throwable t) {
            throw new IllegalStateException("Unable to create file!", t);
        }
    }
}

