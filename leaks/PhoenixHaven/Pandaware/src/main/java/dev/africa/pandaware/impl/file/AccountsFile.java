package dev.africa.pandaware.impl.file;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.file.FileObject;
import dev.africa.pandaware.impl.ui.menu.account.Account;
import dev.africa.pandaware.utils.java.FileUtils;
import dev.africa.pandaware.utils.java.crypt.AESEncryptionUtils;
import dev.africa.pandaware.utils.java.crypt.CustomCryptUtils;

import java.io.File;

public class AccountsFile extends FileObject {
    public AccountsFile(File rootFolder, String fileName, Gson gson) {
        super(rootFolder, fileName, gson);
    }

    @Override
    public void save() throws Exception {
        JsonObject altList = new JsonObject();

        for (Account alt : Client.getInstance().getAccountManager().getItems()) {
            boolean isCracked = (alt.getPassword().equals("") || alt.getPassword().length() <= 0) && !alt.isMicrosoft();

            JsonObject accountDetails = new JsonObject();

            if (!isCracked || alt.isMicrosoft()) {
                accountDetails.addProperty("refreshToken", AESEncryptionUtils.encrypt(CustomCryptUtils.encrypt(alt.getRefreshToken(), Client.getInstance().getManifest().getUsername() + "fdf3j5f"), Client.getInstance().getManifest().getUsername() + "fdf3j5f"));
                accountDetails.addProperty("uuid", AESEncryptionUtils.encrypt(CustomCryptUtils.encrypt(alt.getUuid(), Client.getInstance().getManifest().getUsername() + "fdf3j5f"), Client.getInstance().getManifest().getUsername() + "fdf3j5f"));

                accountDetails.addProperty("mail", AESEncryptionUtils.encrypt(CustomCryptUtils.encrypt(alt.getEmail(), Client.getInstance().getManifest().getUsername() + "fdf3j5f"), Client.getInstance().getManifest().getUsername() + "fdf3j5f"));
                accountDetails.addProperty("password", AESEncryptionUtils.encrypt(CustomCryptUtils.encrypt(alt.getPassword(), Client.getInstance().getManifest().getUsername() + "fdf3j5f"), Client.getInstance().getManifest().getUsername() + "fdf3j5f"));
            }

            accountDetails.addProperty("username", alt.getUsername());
            accountDetails.addProperty("cracked", isCracked);
            accountDetails.addProperty("microsoft", alt.isMicrosoft());

            JsonObject altObject = new JsonObject();
            altObject.add("account", accountDetails);

            altList.add("Account", altObject);
        }

        FileUtils.writeToFile(this.getGson().toJson(altList), this.getFile());
    }

    @Override
    public void load() throws Exception {
        if (!this.getFile().exists()) return;

        String json = FileUtils.readFromFile(this.getFile());
        JsonObject altList = new JsonParser().parse(json).getAsJsonObject();

        altList.entrySet().forEach(emp -> {
            JsonObject altObject = (JsonObject) ((JsonObject) emp.getValue()).get("account");

            if (!altObject.has("mail")) return;

            boolean cracked = altObject.get("cracked").getAsBoolean();
            boolean microsoft = altObject.get("microsoft").getAsBoolean();
            String mail = altObject.get("mail").getAsString();
            String password = altObject.get("password").getAsString();
            String username = altObject.get("username").getAsString();

            if (cracked && !microsoft) {
                Client.getInstance().getAccountManager().getItems().add(new Account("", username, "", "", "", true, false, false));
            } else {
                String refreshToken = altObject.get("refreshToken").getAsString();
                String uuid = altObject.get("uuid").getAsString();

                if (microsoft) {
                    Client.getInstance().getAccountManager().getItems().add(new Account(CustomCryptUtils.decrypt(AESEncryptionUtils.decrypt(mail, Client.getInstance().getManifest().getUsername() + "fdf3j5f"), Client.getInstance().getManifest().getUsername() + "fdf3j5f"),
                            username, CustomCryptUtils.decrypt(AESEncryptionUtils.decrypt(password, Client.getInstance().getManifest().getUsername() + "fdf3j5f"), Client.getInstance().getManifest().getUsername() + "fdf3j5f"),
                            CustomCryptUtils.decrypt(AESEncryptionUtils.decrypt(refreshToken, Client.getInstance().getManifest().getUsername() + "fdf3j5f"), Client.getInstance().getManifest().getUsername() + "fdf3j5f"),
                            CustomCryptUtils.decrypt(AESEncryptionUtils.decrypt(uuid, Client.getInstance().getManifest().getUsername() + "fdf3j5f"), Client.getInstance().getManifest().getUsername() + "fdf3j5f"), false, false, true));
                } else {
                    Client.getInstance().getAccountManager().getItems().add(new Account(CustomCryptUtils.decrypt(AESEncryptionUtils.decrypt(mail, Client.getInstance().getManifest().getUsername() + "fdf3j5f"), Client.getInstance().getManifest().getUsername() + "fdf3j5f"),
                            username, CustomCryptUtils.decrypt(AESEncryptionUtils.decrypt(password, Client.getInstance().getManifest().getUsername() + "fdf3j5f"), Client.getInstance().getManifest().getUsername() + "fdf3j5f"),
                            "", "", false, false, false));
                }
            }
        });
    }
}
