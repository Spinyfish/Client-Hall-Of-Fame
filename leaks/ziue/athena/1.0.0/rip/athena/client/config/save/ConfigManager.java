package rip.athena.client.config.save;

import rip.athena.client.utils.file.*;
import rip.athena.client.*;
import java.io.*;
import java.util.*;
import java.nio.file.attribute.*;
import java.nio.file.*;

public class ConfigManager
{
    public static final String EXTENSION = ".json";
    public static final String DATA_FILE = "last.txt";
    protected static File directory;
    protected Config lastLoadedConfig;
    protected FileHandler lastFile;
    
    public ConfigManager(final File directory) {
        if (!directory.exists()) {
            directory.mkdir();
        }
        final File file = Paths.get(directory.getAbsolutePath(), "last.txt").toFile();
        this.lastFile = new FileHandler(file);
        try {
            this.lastFile.init();
        }
        catch (IOException e) {
            Athena.INSTANCE.getLog().error("Failed to initiate last config handler." + e);
        }
        ConfigManager.directory = directory;
    }
    
    public void postInit() {
        try {
            boolean foundConfig = false;
            final String found = this.lastFile.getContent(true).trim();
            for (final Config config : this.getConfigs()) {
                if (config.getName().equalsIgnoreCase(found.trim())) {
                    config.load();
                    foundConfig = true;
                    break;
                }
            }
            if (!foundConfig) {
                this.getConfig("default").load();
            }
        }
        catch (IOException e) {
            Athena.INSTANCE.getLog().error("Failed to initiate post config init." + e);
        }
    }
    
    public List<Config> getConfigs() {
        final List<Config> files = new ArrayList<Config>();
        for (final File file : ConfigManager.directory.listFiles()) {
            final String name = file.getName();
            if (name.toLowerCase().endsWith(".json".toLowerCase())) {
                files.add(this.getConfig(name.substring(0, name.length() - ".json".length())));
            }
        }
        final File firstFile;
        final File secondFile;
        long firstVal;
        long secondVal;
        BasicFileAttributes firstAttr;
        BasicFileAttributes secondAttr;
        files.sort((first, second) -> {
            firstFile = first.getFileHandler().getFile();
            secondFile = second.getFileHandler().getFile();
            firstVal = firstFile.lastModified();
            secondVal = secondFile.lastModified();
            try {
                firstAttr = Files.readAttributes(firstFile.toPath(), BasicFileAttributes.class, new LinkOption[0]);
                secondAttr = Files.readAttributes(secondFile.toPath(), BasicFileAttributes.class, new LinkOption[0]);
                firstVal = firstAttr.creationTime().toMillis();
                secondVal = secondAttr.creationTime().toMillis();
            }
            catch (IOException e) {
                Athena.INSTANCE.getLog().error("Failed to read file attributes, resorting to last edit for config sorting." + e);
            }
            if (first.getName().equalsIgnoreCase("default")) {
                firstVal = -9223372036854775807L;
            }
            else if (second.getName().equalsIgnoreCase("default")) {
                secondVal = -9223372036854775807L;
            }
            return Long.compare(firstVal, secondVal);
        });
        return files;
    }
    
    public Config getConfig(final String name) {
        final String safeName = name;
        return new Config(this, name, Paths.get(ConfigManager.directory.getAbsolutePath(), safeName + ".json").toFile());
    }
    
    public Config getLoadedConfig() {
        return this.lastLoadedConfig;
    }
    
    public void deleteAllConfigs() {
        for (final Config config : this.getConfigs()) {
            config.delete();
        }
        this.lastLoadedConfig = null;
    }
    
    public void updateLast(final Config config) {
        this.lastLoadedConfig = config;
        final String name = this.lastLoadedConfig.getName();
        try {
            this.lastFile.writeToFile(name, false);
        }
        catch (IOException e) {
            Athena.INSTANCE.getLog().error("Failed to initiate last config handler." + e);
        }
    }
}
