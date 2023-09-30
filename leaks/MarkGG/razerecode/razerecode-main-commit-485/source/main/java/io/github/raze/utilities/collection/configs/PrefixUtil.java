package io.github.raze.utilities.collection.configs;

import io.github.raze.utilities.collection.visual.ChatUtil;

import java.io.*;
import java.util.Properties;

public class PrefixUtil {

    public static void savePrefix(String inputPrefix) {

        File directory = new File("raze");
        if (!directory.exists())
            directory.mkdirs();

        File cfgFile = new File("raze/config.properties");
        try {
            if (!cfgFile.exists())
                cfgFile.createNewFile();

            Properties properties = new Properties();
            OutputStream outputStream = new FileOutputStream("raze/config.properties");
            properties.setProperty("prefix", inputPrefix);
            properties.store(outputStream, "Raze Properties");
            outputStream.close();
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    public static String getPrefixFromFile() {
        File directory = new File("raze");
        if (!directory.exists())
            directory.mkdirs();

        File cfgFile = new File("raze/config.properties");
        try {
            if (!cfgFile.exists())
                return ".";

            Properties properties = new Properties();
            InputStream inputStream = new FileInputStream("raze/config.properties");
            properties.load(inputStream);
            inputStream.close();

            String prefix = properties.getProperty("prefix");
            if (prefix != null && !prefix.isEmpty())
                return prefix;

        } catch (IOException io) {
            io.printStackTrace();
        }

        return "";
    }

}
