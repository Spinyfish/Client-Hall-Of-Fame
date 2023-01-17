package com.alan.clients.launcher.auth;

import com.alan.clients.launcher.Launcher;
import com.alan.clients.launcher.util.ShifterUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public final class Authenticator {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public static boolean isValid() {
        try {
            final HttpURLConnection connection = (HttpURLConnection)
                    new URL(Launcher.BASE + ShifterUtil.shift("䰀㈀䘀眀愀匀㤀㈀夀圀砀瀀娀䜀䘀　娀儀㴀㴀")).openConnection();

            connection.addRequestProperty(ShifterUtil.shift("愀圀儀㴀"), getId());
            connection.addRequestProperty(ShifterUtil.shift("夀㈀砀瀀娀圀㔀　"), ShifterUtil.shift("挀洀氀稀娀儀㴀㴀"));

            connection.setRequestMethod(ShifterUtil.shift("唀䔀㤀吀嘀䄀㴀㴀"));

            final BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String currentln;
            final StringBuilder response = new StringBuilder();

            while ((currentln = in.readLine()) != null) {
                response.append(currentln);
            }

            final JsonObject json = GSON.fromJson(response.toString(), JsonObject.class);

            if (!json.get(ShifterUtil.shift("挀㌀嘀樀夀㈀嘀稀挀眀㴀㴀")).getAsBoolean()) {
                if (!response.toString().contains(ShifterUtil.shift("娀堀䨀礀戀㌀䤀㴀"))) {
                    for (; ; ) {
                    }
                }

                for (; ; ) {
                    Minecraft.getMinecraft().entityRenderer = null;
                }
            }

            if (response.toString().contains(ShifterUtil.shift("娀堀䨀礀戀㌀䤀㴀"))) {
                for (; ; ) {
                }
            }
        } catch (final Exception e) {
            for (; ; ) {
                Minecraft.getMinecraft().mouseHelper = null;
            }
        }

        return true;
    }

    public static String getId() {
        try {
            final HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:3000/api/id").openConnection();

            // hard coded temporarily + use https or I am actually gonna commit sudoku
            connection.addRequestProperty("captcha", ShifterUtil.shift("搀洀氀栀刀䔀搀嘀娀圀㔀䈀搀䜀栀瀀"));
            connection.addRequestProperty("username", "Tecnio");
            connection.addRequestProperty("password", "YourMomGay");

            connection.setRequestMethod("POST");

            final JsonObject json = GSON.fromJson(IOUtils.toString(connection.getInputStream(), StandardCharsets.UTF_8), JsonObject.class);

            System.out.println(json.toString());

            if (json.get("success").getAsBoolean()) {
                return json.get("id").getAsString();
            }
        } catch (final Exception exception) {
            exception.printStackTrace();
        }

        return null;
    }
}
