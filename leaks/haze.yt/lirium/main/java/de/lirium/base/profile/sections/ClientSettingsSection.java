package de.lirium.base.profile.sections;

import com.google.gson.JsonObject;
import de.lirium.Client;
import de.lirium.base.profile.Profile;
import de.lirium.base.profile.ProfileSection;

import java.awt.*;

@ProfileSection.Info(name = "settings")
public class ClientSettingsSection extends ProfileSection {
    public ClientSettingsSection(Profile profile) {
        super(profile);
    }

    @Override
    public void write(JsonObject base) {
        base.addProperty("color", Client.INSTANCE.clientColor.getRGB());
    }

    @Override
    public void read(JsonObject base) {
        if(base.has("color")) {
            Client.INSTANCE.clientColor = new Color(base.get("color").getAsInt());
        }
    }
}
