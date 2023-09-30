package dev.tenacity.config;

import lombok.Getter;
import net.minecraft.client.Minecraft;

import java.io.File;

@Getter
public class LocalConfig {

    private final String name;
    private final File file;

    public LocalConfig(String name) {
        this.name = name;
        this.file = new File(Minecraft.getMinecraft().mcDataDir + "/Tenacity/Configs/" + name + ".json");
    }
}
