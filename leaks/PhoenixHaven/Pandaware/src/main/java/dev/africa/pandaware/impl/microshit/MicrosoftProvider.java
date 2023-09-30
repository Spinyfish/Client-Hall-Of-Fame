package dev.africa.pandaware.impl.microshit;

import dev.africa.pandaware.Client;
import dev.africa.pandaware.impl.microshit.server.AuthManagerWebServer;
import lombok.Getter;

import java.awt.*;
import java.net.URI;

public class MicrosoftProvider {
    @Getter
    private final AuthManagerWebServer authManagerWebServer = new AuthManagerWebServer();
    public boolean expectingAltAdd;

    public void openUrl() {
        if (Client.getInstance().isKillSwitch()) {
            throw new NullPointerException();
        }
        this.expectingAltAdd = true;

        try {
            String link = "https://login.live.com/oauth20_authorize.srf?client_id=af463f20-a01b-4382-8899-834df1494dae&response_type=code&redirect_uri=http://localhost:%WEB_PORT%/auth&scope=XboxLive.signin%20offline_access&state=STORAGE_ID";
            Desktop.getDesktop().browse(new URI(link.replace("%WEB_PORT%", String.valueOf(AuthManagerWebServer.WEB_PORT))));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
