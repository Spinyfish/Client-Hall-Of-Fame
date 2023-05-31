package de.lirium.base.profile.sections;

import com.google.gson.JsonObject;
import com.viaversion.viaversion.api.Via;
import de.lirium.Client;
import de.lirium.base.profile.Profile;
import de.lirium.base.profile.ProfileSection;
import viamcp.ViaMCP;

@ProfileSection.Info(name = "ViaVersion")
public class ViaVersionSection extends ProfileSection {
    public ViaVersionSection(Profile profile) {
        super(profile);
    }

    @Override
    public void write(JsonObject base) {
        base.addProperty("protocolVersion", ViaMCP.getInstance().getVersion());
    }

    @Override
    public void read(JsonObject base) {
        if(base.has("protocolVersion")) {
            final int protocolVersion = base.get("protocolVersion").getAsInt();
            ViaMCP.getInstance().setVersion(protocolVersion);
            ViaMCP.getInstance().asyncSlider.setVersion(protocolVersion);
        }
    }
}
