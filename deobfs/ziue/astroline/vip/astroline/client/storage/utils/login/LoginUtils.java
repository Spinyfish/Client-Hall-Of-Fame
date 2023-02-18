/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.Agent
 *  com.mojang.authlib.UserType
 *  com.mojang.authlib.exceptions.AuthenticationException
 *  com.mojang.authlib.exceptions.AuthenticationUnavailableException
 *  com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
 *  com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication
 *  fr.litarvan.openauth.microsoft.MicrosoftAuthResult
 *  fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException
 *  fr.litarvan.openauth.microsoft.MicrosoftAuthenticator
 *  fr.litarvan.openauth.microsoft.model.response.MinecraftProfile
 *  net.minecraft.client.Minecraft
 *  net.minecraft.util.Session
 *  vip.astroline.client.Astroline
 */
package vip.astroline.client.storage.utils.login;

import com.mojang.authlib.Agent;
import com.mojang.authlib.UserType;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import fr.litarvan.openauth.microsoft.model.response.MinecraftProfile;
import java.net.Proxy;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import vip.astroline.client.Astroline;

public class LoginUtils {
    public static String login(String email, String password) {
        return LoginUtils.login(email, password, false);
    }

    public static String login(String email, String password, boolean autoRemove) {
        System.out.println("Logging in with [" + email + ":********]");
        MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
        try {
            MicrosoftAuthResult result = authenticator.loginWithCredentials(email, password);
            MinecraftProfile profile = result.getProfile();
            Minecraft.getMinecraft().session = new Session(profile.getName(), profile.getId(), result.getAccessToken(), "microsoft");
            System.out.println("Logged in with " + Minecraft.getMinecraft().session.getUsername());
            Astroline.currentAlt = new String[]{email, password};
            return null;
        }
        catch (NullPointerException e3) {
            return "Wrong password!";
        }
        catch (MicrosoftAuthenticationException e) {
            return "Wrong credentials!";
        }
    }

    public static Session getSession(String email, String password) {
        System.out.println("Logging in with [" + email + ":********]");
        YggdrasilAuthenticationService authenticationService = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "");
        YggdrasilUserAuthentication authentication = (YggdrasilUserAuthentication)authenticationService.createUserAuthentication(Agent.MINECRAFT);
        authentication.setUsername(email);
        authentication.setPassword(password);
        try {
            authentication.logIn();
            return new Session(authentication.getSelectedProfile().getName(), authentication.getSelectedProfile().getId().toString(), authentication.getAuthenticatedToken(), UserType.MOJANG.getName());
        }
        catch (AuthenticationUnavailableException authenticationUnavailableException) {
        }
        catch (AuthenticationException authenticationException) {
        }
        catch (NullPointerException nullPointerException) {
            // empty catch block
        }
        return null;
    }

    public static void loginToken(String accessToken, String name, String uuid, String clientToken) {
        System.out.println("Logging in with :" + accessToken);
        try {
            Minecraft.getMinecraft().session = new Session(name, uuid, accessToken, UserType.MOJANG.getName());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void changeCrackedName(String newName) {
        Minecraft.getMinecraft().session = new Session(newName, "", "", UserType.LEGACY.getName());
    }

    public static String removeSpecialChar(String input) {
        return input.replaceAll("\\p{C}", "");
    }
}
