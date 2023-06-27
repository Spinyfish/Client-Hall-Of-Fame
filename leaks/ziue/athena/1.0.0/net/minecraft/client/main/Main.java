package net.minecraft.client.main;

import java.io.*;
import net.minecraft.client.*;
import java.net.*;
import com.mojang.authlib.properties.*;
import java.lang.reflect.*;
import net.minecraft.util.*;
import joptsimple.*;
import java.util.*;
import com.google.gson.*;

public class Main
{
    public static void main(final String[] p_main_0_) {
        System.setProperty("java.net.preferIPv4Stack", "true");
        final OptionParser optionparser = new OptionParser();
        optionparser.allowsUnrecognizedOptions();
        optionparser.accepts("demo");
        optionparser.accepts("fullscreen");
        optionparser.accepts("checkGlErrors");
        final OptionSpec<String> optionspec = (OptionSpec<String>)optionparser.accepts("server").withRequiredArg();
        final OptionSpec<Integer> optionspec2 = (OptionSpec<Integer>)optionparser.accepts("port").withRequiredArg().ofType((Class)Integer.class).defaultsTo((Object)25565, (Object[])new Integer[0]);
        final OptionSpec<File> optionspec3 = (OptionSpec<File>)optionparser.accepts("gameDir").withRequiredArg().ofType((Class)File.class).defaultsTo((Object)new File("."), (Object[])new File[0]);
        final OptionSpec<File> optionspec4 = (OptionSpec<File>)optionparser.accepts("assetsDir").withRequiredArg().ofType((Class)File.class);
        final OptionSpec<File> optionspec5 = (OptionSpec<File>)optionparser.accepts("resourcePackDir").withRequiredArg().ofType((Class)File.class);
        final OptionSpec<String> optionspec6 = (OptionSpec<String>)optionparser.accepts("proxyHost").withRequiredArg();
        final OptionSpec<Integer> optionspec7 = (OptionSpec<Integer>)optionparser.accepts("proxyPort").withRequiredArg().defaultsTo((Object)"8080", (Object[])new String[0]).ofType((Class)Integer.class);
        final OptionSpec<String> optionspec8 = (OptionSpec<String>)optionparser.accepts("proxyUser").withRequiredArg();
        final OptionSpec<String> optionspec9 = (OptionSpec<String>)optionparser.accepts("proxyPass").withRequiredArg();
        final OptionSpec<String> optionspec10 = (OptionSpec<String>)optionparser.accepts("username").withRequiredArg().defaultsTo((Object)("Player" + Minecraft.getSystemTime() % 1000L), (Object[])new String[0]);
        final OptionSpec<String> optionspec11 = (OptionSpec<String>)optionparser.accepts("uuid").withRequiredArg();
        final OptionSpec<String> optionspec12 = (OptionSpec<String>)optionparser.accepts("accessToken").withRequiredArg().required();
        final OptionSpec<String> optionspec13 = (OptionSpec<String>)optionparser.accepts("version").withRequiredArg().required();
        final OptionSpec<Integer> optionspec14 = (OptionSpec<Integer>)optionparser.accepts("width").withRequiredArg().ofType((Class)Integer.class).defaultsTo((Object)854, (Object[])new Integer[0]);
        final OptionSpec<Integer> optionspec15 = (OptionSpec<Integer>)optionparser.accepts("height").withRequiredArg().ofType((Class)Integer.class).defaultsTo((Object)480, (Object[])new Integer[0]);
        final OptionSpec<String> optionspec16 = (OptionSpec<String>)optionparser.accepts("userProperties").withRequiredArg().defaultsTo((Object)"{}", (Object[])new String[0]);
        final OptionSpec<String> optionspec17 = (OptionSpec<String>)optionparser.accepts("profileProperties").withRequiredArg().defaultsTo((Object)"{}", (Object[])new String[0]);
        final OptionSpec<String> optionspec18 = (OptionSpec<String>)optionparser.accepts("assetIndex").withRequiredArg();
        final OptionSpec<String> optionspec19 = (OptionSpec<String>)optionparser.accepts("userType").withRequiredArg().defaultsTo((Object)"legacy", (Object[])new String[0]);
        final OptionSpec<String> optionspec20 = (OptionSpec<String>)optionparser.nonOptions();
        final OptionSet optionset = optionparser.parse(p_main_0_);
        final List<String> list = (List<String>)optionset.valuesOf((OptionSpec)optionspec20);
        if (!list.isEmpty()) {
            System.out.println("Completely ignored arguments: " + list);
        }
        final String s = (String)optionset.valueOf((OptionSpec)optionspec6);
        Proxy proxy = Proxy.NO_PROXY;
        if (s != null) {
            try {
                proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(s, (int)optionset.valueOf((OptionSpec)optionspec7)));
            }
            catch (Exception ex) {}
        }
        final String s2 = (String)optionset.valueOf((OptionSpec)optionspec8);
        final String s3 = (String)optionset.valueOf((OptionSpec)optionspec9);
        if (!proxy.equals(Proxy.NO_PROXY) && isNullOrEmpty(s2) && isNullOrEmpty(s3)) {
            Authenticator.setDefault(new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(s2, s3.toCharArray());
                }
            });
        }
        final int i = (int)optionset.valueOf((OptionSpec)optionspec14);
        final int j = (int)optionset.valueOf((OptionSpec)optionspec15);
        final boolean flag = optionset.has("fullscreen");
        final boolean flag2 = optionset.has("checkGlErrors");
        final boolean flag3 = optionset.has("demo");
        final String s4 = (String)optionset.valueOf((OptionSpec)optionspec13);
        final Gson gson = new GsonBuilder().registerTypeAdapter((Type)PropertyMap.class, (Object)new PropertyMap.Serializer()).create();
        final PropertyMap propertymap = (PropertyMap)gson.fromJson((String)optionset.valueOf((OptionSpec)optionspec16), (Class)PropertyMap.class);
        final PropertyMap propertymap2 = (PropertyMap)gson.fromJson((String)optionset.valueOf((OptionSpec)optionspec17), (Class)PropertyMap.class);
        final File file1 = (File)optionset.valueOf((OptionSpec)optionspec3);
        final File file2 = (File)(optionset.has((OptionSpec)optionspec4) ? optionset.valueOf((OptionSpec)optionspec4) : new File(file1, "assets/"));
        final File file3 = (File)(optionset.has((OptionSpec)optionspec5) ? optionset.valueOf((OptionSpec)optionspec5) : new File(file1, "resourcepacks/"));
        final String s5 = (String)(optionset.has((OptionSpec)optionspec11) ? optionspec11.value(optionset) : ((String)optionspec10.value(optionset)));
        final String s6 = optionset.has((OptionSpec)optionspec18) ? ((String)optionspec18.value(optionset)) : null;
        final String s7 = (String)optionset.valueOf((OptionSpec)optionspec);
        final Integer integer = (Integer)optionset.valueOf((OptionSpec)optionspec2);
        final Session session = new Session((String)optionspec10.value(optionset), s5, (String)optionspec12.value(optionset), (String)optionspec19.value(optionset));
        final GameConfiguration gameconfiguration = new GameConfiguration(new GameConfiguration.UserInformation(session, propertymap, propertymap2, proxy), new GameConfiguration.DisplayInformation(i, j, flag, flag2), new GameConfiguration.FolderInformation(file1, file3, file2, s6), new GameConfiguration.GameInformation(flag3, s4), new GameConfiguration.ServerInformation(s7, integer));
        Runtime.getRuntime().addShutdownHook(new Thread("Client Shutdown Thread") {
            @Override
            public void run() {
                Minecraft.stopIntegratedServer();
            }
        });
        Thread.currentThread().setName("Client thread");
        new Minecraft(gameconfiguration).run();
    }
    
    private static boolean isNullOrEmpty(final String str) {
        return str != null && !str.isEmpty();
    }
}
