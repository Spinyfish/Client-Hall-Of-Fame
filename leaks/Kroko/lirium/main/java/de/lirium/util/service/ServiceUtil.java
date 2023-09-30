package de.lirium.util.service;

import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import de.lirium.Client;
import de.lirium.base.auth.Auth;
import god.buddy.aot.BCompiler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.felix.mainmenu.MainMenu;

import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;

public class ServiceUtil {

    @BCompiler(aot = BCompiler.AOT.AGGRESSIVE)
    public static void switchService(String session, String auth) {
        try {

            setStaticField(YggdrasilUserAuthentication.class, "BASE_URL", auth);
            setStaticField(YggdrasilUserAuthentication.class, "ROUTE_AUTHENTICATE", new URL(auth + "/authenticate"));
            setStaticField(YggdrasilUserAuthentication.class, "ROUTE_INVALIDATE", new URL(auth + "/invalidate"));
            setStaticField(YggdrasilUserAuthentication.class, "ROUTE_REFRESH", new URL(auth + "/refresh"));
            setStaticField(YggdrasilUserAuthentication.class, "ROUTE_VALIDATE", new URL(auth + "/validate"));
            setStaticField(YggdrasilUserAuthentication.class, "ROUTE_SIGNOUT", new URL(auth + "/signout"));

            setStaticField(YggdrasilMinecraftSessionService.class, "BASE_URL", session + "/session/minecraft/");
            setStaticField(YggdrasilMinecraftSessionService.class, "JOIN_URL", new URL(session + "/session/minecraft/join"));
            setStaticField(YggdrasilMinecraftSessionService.class, "CHECK_URL", new URL(session + "/session/minecraft/hasJoined"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public static boolean switchService(String name) {
        final Auth auth = Client.INSTANCE.getAuthManager().get(name);
        if(auth == null) {
            MainMenu.loginStatus = "§c" + name + " service is missing!";
            return false;
        }
        switchService(auth.session, auth.auth);
        return true;
    }

    @BCompiler(aot = BCompiler.AOT.AGGRESSIVE)
    private static void setStaticField(final Class<?> clazz, final String fieldName, final Object value) {
        try {
            final Field staticField = clazz.getDeclaredField(fieldName);
            staticField.setAccessible(true);
            final Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(staticField, staticField.getModifiers() & 0xFFFFFFEF);
            staticField.set(null, value);
        }
        catch (final NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
