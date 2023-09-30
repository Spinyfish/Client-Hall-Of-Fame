package com.alan.clients.util.file.data;

import com.alan.clients.Client;
import com.alan.clients.util.file.FileType;
import com.google.gson.JsonObject;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Patrick
 * @since 10/19/2021
 */
public class DataFile extends com.alan.clients.util.file.File {

    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd.MM.yyyy");

    private boolean loadKeyCodes;

    public DataFile(final File file, final FileType fileType) {
        super(file, fileType);
    }

    @Override
    public boolean read() {
        if (!this.getFile().exists()) {
            return false;
        }

        try {
            // reads file to a json object
            final FileReader fileReader = new FileReader(getFile());
            final BufferedReader bufferedReader = new BufferedReader(fileReader);
            final JsonObject jsonObject = GSON.fromJson(bufferedReader, JsonObject.class);

            // closes both readers
            bufferedReader.close();
            fileReader.close();

            // checks if there was data read
            if (jsonObject == null) {
                return false;
            }


        } catch (final IOException ignored) {
            return false;
        }

        return true;
    }

    @Override
    public boolean write() {
        try {
            // creates the file
            this.getFile().createNewFile();
            // creates a new json object where all data is stored in
            final JsonObject jsonObject = new JsonObject();

            // Add some extra information to the config
            final JsonObject metadataJsonObject = new JsonObject();
            metadataJsonObject.addProperty("version", Client.VERSION);
            metadataJsonObject.addProperty("creationDate", DATE_FORMATTER.format(new Date()));
            jsonObject.add("Metadata", metadataJsonObject);


            // writes json object data to a file
            final FileWriter fileWriter = new FileWriter(getFile());
            final BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            GSON.toJson(jsonObject, bufferedWriter);

            // closes the writer
            bufferedWriter.flush();
            bufferedWriter.close();
//            fileWriter.flush();
//            fileWriter.close();
        } catch (final IOException ignored) {
            ignored.printStackTrace();
            return false;
        }

        return true;
    }

    public void allowKeyCodeLoading() {
        this.loadKeyCodes = true;
    }
}