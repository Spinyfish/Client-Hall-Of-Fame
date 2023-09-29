package de.lirium.base.profile;

import de.lirium.Client;
import de.lirium.base.feature.Manager;
import de.lirium.util.interfaces.Logger;
import lombok.Setter;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Objects;

@Setter
public class ProfileManager implements Manager<Profile> {
    private static final ArrayList<Profile> profiles = new ArrayList<>();
    private Profile current = null;

    public static final File DIR = new File(Client.DIR, "Profiles");

    public ProfileManager() {
        final File dir = new File(Client.DIR, "Profiles");
        if (!dir.exists())
            if(dir.mkdir())
                Logger.print("Created profile directory");
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            final String name = file.getName().replaceFirst("[.][^.]+$", "");
            final Profile profile = new Profile(name);
            profile.init();
            profiles.add(profile);
        }
    }

    @Override
    public ArrayList<Profile> getFeatures() {
        return profiles;
    }

    @Override
    public <U extends Profile> U get(Class<U> clazz) {
        return (U) this.getFeatures().stream().filter(profile -> profile.getClass().equals(clazz)).findAny().orElse(null);
    }

    @Override
    public Profile get(Type type) {
        return this.getFeatures().stream().filter(m -> m.getClass().getName().equals(type.getTypeName())).findFirst().orElse(null);
    }

    public Profile get() {
        return current;
    }

    public void save() {
        current.save();
    }

    public void read() {
        current.read();
    }
}
