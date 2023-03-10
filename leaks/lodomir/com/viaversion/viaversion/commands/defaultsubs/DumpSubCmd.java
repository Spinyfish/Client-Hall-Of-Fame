/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.io.CharStreams
 */
package com.viaversion.viaversion.commands.defaultsubs;

import com.google.common.io.CharStreams;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.command.ViaCommandSender;
import com.viaversion.viaversion.api.command.ViaSubCommand;
import com.viaversion.viaversion.dump.DumpTemplate;
import com.viaversion.viaversion.dump.VersionInfo;
import com.viaversion.viaversion.libs.gson.GsonBuilder;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.util.GsonUtil;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InvalidObjectException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.logging.Level;

public class DumpSubCmd
extends ViaSubCommand {
    @Override
    public String name() {
        return "dump";
    }

    @Override
    public String description() {
        return "Dump information about your server, this is helpful if you report bugs.";
    }

    @Override
    public boolean execute(final ViaCommandSender sender, String[] args) {
        final VersionInfo version = new VersionInfo(System.getProperty("java.version"), System.getProperty("os.name"), Via.getAPI().getServerVersion().lowestSupportedVersion(), Via.getManager().getProtocolManager().getSupportedVersions(), Via.getPlatform().getPlatformName(), Via.getPlatform().getPlatformVersion(), Via.getPlatform().getPluginVersion(), "git-ViaVersion-4.1.1:aade94a4", Via.getManager().getSubPlatforms());
        Map<String, Object> configuration = Via.getPlatform().getConfigurationProvider().getValues();
        final DumpTemplate template = new DumpTemplate(version, configuration, Via.getPlatform().getDump(), Via.getManager().getInjector().getDump());
        Via.getPlatform().runAsync(new Runnable(){

            @Override
            public void run() {
                HttpURLConnection con = null;
                try {
                    con = (HttpURLConnection)new URL("https://dump.viaversion.com/documents").openConnection();
                }
                catch (IOException e) {
                    sender.sendMessage("\u00a74Failed to dump, please check the console for more information");
                    Via.getPlatform().getLogger().log(Level.WARNING, "Could not paste ViaVersion dump to ViaVersion Dump", e);
                    return;
                }
                try {
                    con.setRequestProperty("Content-Type", "application/json");
                    con.addRequestProperty("User-Agent", "ViaVersion/" + version.getPluginVersion());
                    con.setRequestMethod("POST");
                    con.setDoOutput(true);
                    OutputStream out = con.getOutputStream();
                    out.write(new GsonBuilder().setPrettyPrinting().create().toJson(template).getBytes(StandardCharsets.UTF_8));
                    out.close();
                    if (con.getResponseCode() == 429) {
                        sender.sendMessage("\u00a74You can only paste once every minute to protect our systems.");
                        return;
                    }
                    String rawOutput = CharStreams.toString((Readable)new InputStreamReader(con.getInputStream()));
                    con.getInputStream().close();
                    JsonObject output = GsonUtil.getGson().fromJson(rawOutput, JsonObject.class);
                    if (!output.has("key")) {
                        throw new InvalidObjectException("Key is not given in Hastebin output");
                    }
                    sender.sendMessage("\u00a72We've made a dump with useful information, report your issue and provide this url: " + DumpSubCmd.this.getUrl(output.get("key").getAsString()));
                }
                catch (Exception e) {
                    sender.sendMessage("\u00a74Failed to dump, please check the console for more information");
                    Via.getPlatform().getLogger().log(Level.WARNING, "Could not paste ViaVersion dump to Hastebin", e);
                    try {
                        if (con.getResponseCode() < 200 || con.getResponseCode() > 400) {
                            String rawOutput = CharStreams.toString((Readable)new InputStreamReader(con.getErrorStream()));
                            con.getErrorStream().close();
                            Via.getPlatform().getLogger().log(Level.WARNING, "Page returned: " + rawOutput);
                        }
                    }
                    catch (IOException e1) {
                        Via.getPlatform().getLogger().log(Level.WARNING, "Failed to capture further info", e1);
                    }
                }
            }
        });
        return true;
    }

    private String getUrl(String id) {
        return String.format("https://dump.viaversion.com/%s", id);
    }
}

