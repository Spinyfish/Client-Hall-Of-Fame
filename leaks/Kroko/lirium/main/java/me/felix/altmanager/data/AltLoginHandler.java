/*
 * Copyright Felix Hans from AltLoginHandler coded for haze.yt / Lirium . - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited
 */

package me.felix.altmanager.data;

import com.eatthepath.uuid.FastUUID;
import com.mojang.authlib.Agent;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import de.lirium.Client;
import de.lirium.util.interfaces.IMinecraft;
import de.lirium.util.service.ServiceUtil;
import net.minecraft.util.Session;

import java.net.Proxy;

public class AltLoginHandler implements IMinecraft {

    public static void loginIntoAccount(final Account account) {
        final String email = account.getEmail(), password = account.getPassword();
        switch (account.getType()) {
            case CRACKED:
                if(ServiceUtil.switchService("Mojang")) {
                    mc.session = new Session(account.getEmail(), "0", "0", "MOJANG");
                }
                break;
            case ALTENING:
                if(ServiceUtil.switchService("Altening")) {
                }
                break;
            case MC_LEAKS:
                break;
            case EASY_MC:
                if(ServiceUtil.switchService("EasyMC")) {
                    final YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication) new YggdrasilAuthenticationService(Proxy.NO_PROXY, "").createUserAuthentication(Agent.MINECRAFT);
                    auth.setUsername(account.getEmail());
                    auth.setPassword(Client.NAME);
                    try {
                        auth.logIn();
                        if (auth.getSelectedProfile() != null) {
                            final Session session = new Session(auth.getSelectedProfile().getName(), FastUUID.toString(auth.getSelectedProfile().getId()), auth.getAuthenticatedToken(), "mojang");
                            mc.setSession(session);
                            account.response = "Logged in as §e§l" + session.getUsername() + " §7- §5§lShadow-Gen";
                        } else {
                            account.response = "§c§lInvalid Account";
                        }
                    } catch (Exception e) {
                        account.response = "§c§lInvalid account";
                    }
                }
                break;
            case MICROSOFT:
                if(ServiceUtil.switchService("Mojang")) {

                }
                break;
            case SHADOW_GEN:
                if(ServiceUtil.switchService("ShadowGen")) {
                }
                break;

        }
    }

}
