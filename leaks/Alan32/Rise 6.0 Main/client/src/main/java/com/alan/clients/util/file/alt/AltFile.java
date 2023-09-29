package com.alan.clients.util.file.alt;

import com.alan.clients.Client;
import com.alan.clients.util.account.Account;
import com.alan.clients.util.file.FileType;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * @author Patrick
 * @since 10/19/2021
 */
public class AltFile extends com.alan.clients.util.file.File {

    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd.MM.yyyy");

    public AltFile(final File file, final FileType fileType) {
        super(file, fileType);
    }

    @Override
    public boolean read() {
        if (!this.getFile().exists()) {
            return false;
        }

        Client.INSTANCE.getAltManager().getAccounts().clear();

        try {
            // reads file to a json object
            final FileReader fileReader = new FileReader(this.getFile());
            final BufferedReader bufferedReader = new BufferedReader(fileReader);
            final JsonObject jsonObject = GSON.fromJson(bufferedReader, JsonObject.class);

            // closes both readers
            bufferedReader.close();
            fileReader.close();

            // checks if there was data read
            if (jsonObject == null) {
                return false;
            }

            for (Map.Entry<String, JsonElement> jsonElement : jsonObject.entrySet()) {
                if (jsonElement.getKey().equals("Metadata")) {
                    continue;
                }

                // TODO: Might wanna add "has" checks for each field so it doesn't shit itself while loading
                JsonObject accountJSONElement = jsonElement.getValue().getAsJsonObject();
                String email = accountJSONElement.get("email").getAsString();
                String password = accountJSONElement.get("password").getAsString();
                String uuid = accountJSONElement.get("uuid").getAsString();
                String refreshToken = accountJSONElement.get("refreshtoken").getAsString();
                Account account = new Account(email, password, jsonElement.getKey(), uuid, refreshToken);
                Client.INSTANCE.getAltManager().getAccounts().add(account);
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
            if (!this.getFile().exists()) {
                this.getFile().createNewFile();
            }

            // creates a new json object where all data is stored in
            final JsonObject jsonObject = new JsonObject();

            // Add some extra information to the config
            final JsonObject metadataJsonObject = new JsonObject();
            metadataJsonObject.addProperty("version", Client.VERSION);
            metadataJsonObject.addProperty("creationDate", DATE_FORMATTER.format(new Date()));
            jsonObject.add("Metadata", metadataJsonObject);

            for (Account account : Client.INSTANCE.getAltManager().getAccounts()) {
                if (account.getUsername() == null) {
                    continue;
                }

                final JsonObject moduleJsonObject = new JsonObject();
                moduleJsonObject.addProperty("email", account.getEmail());
                moduleJsonObject.addProperty("password", account.getPassword());
                moduleJsonObject.addProperty("uuid", account.getUuid());
                moduleJsonObject.addProperty("refreshtoken", account.getRefreshToken());
                jsonObject.add(account.getUsername(), moduleJsonObject);
            }

            // writes json object data to a file
            final FileWriter fileWriter = new FileWriter(getFile());
            final BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            GSON.toJson(jsonObject, bufferedWriter);

            // closes the writer
            bufferedWriter.flush();
            bufferedWriter.close();
            fileWriter.flush();
            fileWriter.close();
        } catch (final IOException ignored) {
            return false;
        }

        return true;
    }
}