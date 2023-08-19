package cc.swift.config.impl;

import cc.swift.config.Config;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;


@Getter
public final class LocalConfig extends Config {
    private final File file;
    public LocalConfig(File file) throws FileNotFoundException {
        super(file.getName(), JsonParser.parseReader(new FileReader(file)).getAsJsonObject());
        this.file = file;
    }
}
