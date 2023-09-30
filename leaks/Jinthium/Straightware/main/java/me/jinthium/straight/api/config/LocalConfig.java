package me.jinthium.straight.api.config;

import net.minecraft.client.Minecraft;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class LocalConfig {

    private final String name;
    private final File file;

    public LocalConfig(String name) {
        this.name = name;
        this.file = new File(Minecraft.getMinecraft().mcDataDir + "/Straightware/Configs/" + name + ".json");
    }

    public String getName() {
        return name;
    }

    public File getFile() {
        return file;
    }

    public String getCreationDate(){
        try {
            BasicFileAttributes attributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
            LocalDateTime creationTime = attributes.creationTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            return String.format("%02d-%02d-%04d", creationTime.getMonthValue(), creationTime.getDayOfMonth(), creationTime.getYear());
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return "Error or no file :)";
    }
}