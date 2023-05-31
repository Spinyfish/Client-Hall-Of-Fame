package de.lirium.base.auth;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import de.lirium.Client;
import de.lirium.base.feature.Manager;
import de.lirium.util.interfaces.Logger;
import god.buddy.aot.BCompiler;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Objects;

public class AuthManager implements Manager<Auth> {

    private static final ArrayList<Auth> AUTHS = new ArrayList<>();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static boolean loaded = false;

    @Override
    public ArrayList<Auth> getFeatures() {
        return AUTHS;
    }

    public void init(File DIR) {
        final File dir = new File(DIR, "services");
        if(!dir.exists()) {
            if(dir.mkdir())
                Logger.print("Created services directory");
            addDefault();
        }
        if(load(dir))
            Logger.print("Loaded successful all services!");
    }

    private void addDefault() {
        add("Mojang", "https://authserver.mojang.com", "https://sessionserver.mojang.com");
        add("Altening", "http://authserver.thealtening.com", "http://sessionserver.thealtening.com");
        add("EasyMC", "https://authserver.mojang.com", "https://sessionserver.easymc.io");
        add("ShadowGen", "https://shadowgen.raphimc.net/mc/auth/", "https://shadowgen.raphimc.net/mc/session/");
    }

    @BCompiler(aot = BCompiler.AOT.AGGRESSIVE)
    private boolean load(File dir) {
        for(File file : Objects.requireNonNull(dir.listFiles())) {
            if(file.getName().endsWith(".json")) {
                try {
                    final JsonObject object = GSON.fromJson(new FileReader(file), JsonObject.class);
                    final String name = object.get("name").getAsString();
                    final String session = object.get("session").getAsString();
                    final String auth = object.get("auth").getAsString();
                    AUTHS.add(new Auth(name, session, auth));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return loaded = true;
    }

    public void add(String name, String auth, String session) {
        final File dir = new File(Client.DIR, "services");
        final File file = new File(dir, name.toLowerCase() + ".json");
        final JsonObject object = new JsonObject();
        object.addProperty("name", name);
        object.addProperty("auth", auth);
        object.addProperty("session", session);
        try {
            final BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(GSON.toJson(object));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(loaded) {
            AUTHS.add(new Auth(name, session, auth));
        }
    }

    public void remove(String name) {
        final File dir = new File(Client.DIR, "services");
        final File file = new File(dir, name.toLowerCase() + ".json");
        if(file.exists()) {
            if(file.delete())
                Logger.print("Removed " + name + " from the services!");
        }
        if(loaded) {
            AUTHS.removeIf(auth -> auth.getName().equalsIgnoreCase(name));
        }
    }

    public <U extends Auth> U get(String name) {
        return (U) this.AUTHS.stream().filter(auth -> auth.getName().equalsIgnoreCase(name)).findAny().orElse(null);
    }

    @Override
    public <U extends Auth> U get(Class<U> clazz) {
        return null;
    }

    @Override
    public Auth get(Type type) {
        return null;
    }
}
