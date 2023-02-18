/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.thealtening.auth.TheAlteningAuthentication
 *  com.thealtening.auth.service.AlteningServiceType
 *  fr.litarvan.openauth.microsoft.MicrosoftAuthResult
 *  fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException
 *  fr.litarvan.openauth.microsoft.MicrosoftAuthenticator
 *  fr.litarvan.openauth.microsoft.model.response.MinecraftProfile
 *  net.minecraft.client.Minecraft
 *  net.minecraft.util.Session
 *  vip.astroline.client.Astroline
 *  vip.astroline.client.layout.altMgr.dialog.impl.AltInputDialog
 *  vip.astroline.client.storage.utils.login.LoginUtils
 */
package vip.astroline.client.layout.altMgr;

import com.thealtening.auth.TheAlteningAuthentication;
import com.thealtening.auth.service.AlteningServiceType;
import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import fr.litarvan.openauth.microsoft.model.response.MinecraftProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import vip.astroline.client.Astroline;
import vip.astroline.client.layout.altMgr.dialog.impl.AltInputDialog;
import vip.astroline.client.storage.utils.login.LoginUtils;

public class GuiAltMgr.DirectLoginDialog
extends AltInputDialog {
    String displayText;

    public GuiAltMgr.DirectLoginDialog() {
        super(0.0f, 0.0f, 200.0f, 80.0f, "Direct login", Minecraft.getMinecraft().getSession().getUsername(), "Username / Email:Password");
    }

    public void acceptAction() {
        new Thread(() -> {
            this.title = "Logging in...";
            if (this.cef.getText().contains("@alt.com")) {
                TheAlteningAuthentication.theAltening().updateService(AlteningServiceType.THEALTENING);
                this.displayText = LoginUtils.login((String)this.cef.getText().split(":")[0], (String)"Flux");
                if (this.displayText != null) {
                    this.title = "[ERR TheAltening] " + this.displayText;
                } else {
                    this.destroy();
                }
                return;
            }
            if (this.cef.getText().split(":").length <= 1) {
                LoginUtils.changeCrackedName((String)this.cef.getText());
                this.displayText = null;
            } else {
                String email = this.cef.getText().split(":")[0];
                String password = this.cef.getText().split(":")[1];
                MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
                try {
                    MicrosoftAuthResult result = null;
                    try {
                        result = authenticator.loginWithCredentials(email, password);
                    }
                    catch (MicrosoftAuthenticationException e) {
                        throw new RuntimeException(e);
                    }
                    MinecraftProfile profile = result.getProfile();
                    Minecraft.getMinecraft().session = new Session(profile.getName(), profile.getId(), result.getAccessToken(), "microsoft");
                    System.out.println("Logged in with " + Minecraft.getMinecraft().session.getUsername());
                    Astroline.currentAlt = new String[]{email, password};
                }
                catch (RuntimeException e) {
                    throw new RuntimeException(e);
                }
            }
            if (this.displayText != null) return;
            this.destroy();
        }).start();
    }
}
