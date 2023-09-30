package net.minecraft.client.main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.properties.PropertyMap.Serializer;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

import java.io.File;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.List;

public class Main {
    public static void main(final String[] p_main_0_) {
        System.setProperty("java.net.preferIPv4Stack", "true");
        final OptionParser optionparser = new OptionParser();
        optionparser.allowsUnrecognizedOptions();
        optionparser.accepts("demo");
        optionparser.accepts("fullscreen");
        optionparser.accepts("checkGlErrors");
        final OptionSpec<String> optionspec = optionparser.accepts("server").withRequiredArg();
        final OptionSpec<Integer> optionspec1 = optionparser.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo(25565);
        final OptionSpec<File> optionspec2 = optionparser.accepts("gameDir").withRequiredArg().ofType(File.class).defaultsTo(new File("."));
        final OptionSpec<File> optionspec3 = optionparser.accepts("assetsDir").withRequiredArg().ofType(File.class);
        final OptionSpec<File> optionspec4 = optionparser.accepts("resourcePackDir").withRequiredArg().ofType(File.class);
        final OptionSpec<String> optionspec5 = optionparser.accepts("proxyHost").withRequiredArg();
        final OptionSpec<Integer> optionspec6 = optionparser.accepts("proxyPort").withRequiredArg().defaultsTo("8080", new String[0]).ofType(Integer.class);
        final OptionSpec<String> optionspec7 = optionparser.accepts("proxyUser").withRequiredArg();
        final OptionSpec<String> optionspec8 = optionparser.accepts("proxyPass").withRequiredArg();
        final OptionSpec<String> optionspec9 = optionparser.accepts("username").withRequiredArg().defaultsTo("Rise");
        final OptionSpec<String> optionspec10 = optionparser.accepts("uuid").withRequiredArg();
        final OptionSpec<String> optionspec11 = optionparser.accepts("accessToken").withRequiredArg().required();
        final OptionSpec<String> optionspec12 = optionparser.accepts("version").withRequiredArg().required();
        final OptionSpec<Integer> optionspec13 = optionparser.accepts("width").withRequiredArg().ofType(Integer.class).defaultsTo(854);
        final OptionSpec<Integer> optionspec14 = optionparser.accepts("height").withRequiredArg().ofType(Integer.class).defaultsTo(480);
        final OptionSpec<String> optionspec15 = optionparser.accepts("userProperties").withRequiredArg().defaultsTo("{}");
        final OptionSpec<String> optionspec16 = optionparser.accepts("profileProperties").withRequiredArg().defaultsTo("{}");
        final OptionSpec<String> optionspec17 = optionparser.accepts("assetIndex").withRequiredArg();
        final OptionSpec<String> optionspec18 = optionparser.accepts("userType").withRequiredArg().defaultsTo("legacy");
        final OptionSpec<String> optionspec19 = optionparser.nonOptions();
        final OptionSet optionset = optionparser.parse(p_main_0_);
        final List<String> list = optionset.valuesOf(optionspec19);

        if (!list.isEmpty()) {
            System.out.println("Completely ignored arguments: " + list);
        }

        final String s = optionset.valueOf(optionspec5);
        Proxy proxy = Proxy.NO_PROXY;

        if (s != null) {
            try {
                proxy = new Proxy(Type.SOCKS, new InetSocketAddress(s, optionset.valueOf(optionspec6)));
            } catch (final Exception ignored) {
            }
        }

        final String s1 = optionset.valueOf(optionspec7);
        final String s2 = optionset.valueOf(optionspec8);

        if (!proxy.equals(Proxy.NO_PROXY) && isNullOrEmpty(s1) && isNullOrEmpty(s2)) {
            Authenticator.setDefault(new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(s1, s2.toCharArray());
                }
            });
        }

        final int i = optionset.valueOf(optionspec13);
        final int j = optionset.valueOf(optionspec14);
        final boolean flag = optionset.has("fullscreen");
        final boolean flag1 = optionset.has("checkGlErrors");
        final boolean flag2 = optionset.has("demo");
        final String s3 = optionset.valueOf(optionspec12);
        final Gson gson = (new GsonBuilder()).registerTypeAdapter(PropertyMap.class, new Serializer()).create();
        final PropertyMap propertymap = gson.fromJson(optionset.valueOf(optionspec15), PropertyMap.class);
        final PropertyMap propertymap1 = gson.fromJson(optionset.valueOf(optionspec16), PropertyMap.class);
        final File file1 = optionset.valueOf(optionspec2);
        final File file2 = optionset.has(optionspec3) ? optionset.valueOf(optionspec3) : new File(file1, "assets/");
        final File file3 = optionset.has(optionspec4) ? optionset.valueOf(optionspec4) : new File(file1, "resourcepacks/");
        final String s4 = optionset.has(optionspec10) ? optionspec10.value(optionset) : optionspec9.value(optionset);
        final String s5 = optionset.has(optionspec17) ? optionspec17.value(optionset) : null;
        final String s6 = optionset.valueOf(optionspec);
        final Integer integer = optionset.valueOf(optionspec1);
        final Session session = new Session(optionspec9.value(optionset), s4, optionspec11.value(optionset), optionspec18.value(optionset));
        final GameConfiguration gameconfiguration = new GameConfiguration(new GameConfiguration.UserInformation(session, propertymap, propertymap1, proxy), new GameConfiguration.DisplayInformation(i, j, flag, flag1), new GameConfiguration.FolderInformation(file1, file3, file2, s5), new GameConfiguration.GameInformation(flag2, s3), new GameConfiguration.ServerInformation(s6, integer));
        Runtime.getRuntime().addShutdownHook(new Thread("Client Shutdown Thread") {
            public void run() {
                Minecraft.stopIntegratedServer();
            }
        });
        Thread.currentThread().setName("Client thread");
        (new Minecraft(gameconfiguration)).run();
    }

    private static boolean isNullOrEmpty(final String str) {
        return str != null && !str.isEmpty();
    }
}
