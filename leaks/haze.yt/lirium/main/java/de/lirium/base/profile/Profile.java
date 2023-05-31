package de.lirium.base.profile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import de.lirium.base.feature.Feature;
import de.lirium.base.profile.sections.*;
import de.lirium.util.interfaces.Logger;
import god.buddy.aot.BCompiler;
import lombok.Getter;

import java.io.*;
import java.util.ArrayList;

@Getter
public class Profile implements Feature {
    final String name;
    final ArrayList<ProfileSection> sections = new ArrayList<>();

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final File file;

    public Profile(String name) {
        this.name = name;
        this.file = new File(ProfileManager.DIR, name + ".json");
    }

    public void init() {
        this.sections.add(new ModuleSection(this));
        this.sections.add(new ViaVersionSection(this));
        this.sections.add(new ClientSettingsSection(this));
    }

    @BCompiler(aot = BCompiler.AOT.AGGRESSIVE)
    public void read() {
        if(!file.exists())
            return;
        try {
            final JsonObject base = gson.fromJson(new FileReader(file), JsonObject.class);
            if(base == null)
                return;
            sections.forEach(sections -> {
                if(base.has(sections.getSectionName().toLowerCase())) {
                    final JsonObject section = base.get(sections.getSectionName().toLowerCase()).getAsJsonObject();
                    sections.read(section);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @BCompiler(aot = BCompiler.AOT.AGGRESSIVE)
    public void save() {
        if(!file.exists()) {
            try {
                if(file.createNewFile())
                    Logger.print("Created profile file from " + this.name);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            JsonObject base = gson.fromJson(new FileReader(file), JsonObject.class);
            if(base == null)
                base = new JsonObject();
            final JsonObject finalBase = base;
            sections.forEach(sections -> {
                final JsonObject section = new JsonObject();
                sections.write(section);
                finalBase.add(sections.getSectionName().toLowerCase(), section);
            });
            final FileWriter writer = new FileWriter(file);
            writer.write(gson.toJson(finalBase));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
