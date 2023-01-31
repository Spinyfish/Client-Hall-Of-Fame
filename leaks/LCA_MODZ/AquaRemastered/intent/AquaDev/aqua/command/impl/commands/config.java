package intent.AquaDev.aqua.command.impl.commands;

import intent.AquaDev.aqua.Aqua;
import intent.AquaDev.aqua.command.Command;
import intent.AquaDev.aqua.config.Config;
import intent.AquaDev.aqua.config.ConfigOnline;
import intent.AquaDev.aqua.utils.ChatUtil;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;

public class config extends Command {
   private final File dir = new File(Minecraft.getMinecraft().mcDataDir + "/" + Aqua.name + "/configs/");

   public config() {
      super("config");
   }

   @Override
   public void execute(String[] args) {
      if (args.length >= 1) {
         if (args[0].equalsIgnoreCase("load")) {
            StringBuilder s = new StringBuilder();

            for(int i = 1; i < args.length; ++i) {
               s.append(args[i]);
            }

            s = new StringBuilder(s.toString().trim());
            Config cfg = new Config(s.toString());
            cfg.load();
         }

         if (args[0].equalsIgnoreCase("onlineload")) {
            StringBuilder s = new StringBuilder();

            for(int i = 1; i < args.length; ++i) {
               s.append(args[i]);
            }

            s = new StringBuilder(s.toString().trim());
            ConfigOnline skid = new ConfigOnline();
            skid.loadConfigOnline(s.toString());
         }

         if (args[0].equalsIgnoreCase("save")) {
            StringBuilder s = new StringBuilder();

            for(int i = 1; i < args.length; ++i) {
               s.append(args[i]);
            }

            s = new StringBuilder(s.toString().trim());
            Config cfg = new Config(s.toString());
            cfg.saveCurrent();
         }

         if (args[0].equalsIgnoreCase("onlinelist")) {
            Thread thread = new Thread(
               () -> {
                  ChatUtil.sendChatMessageWithPrefix("Loading...");
                  ArrayList<String> configs = new ArrayList<>();
   
                  try {
                     URLConnection urlConnection = new URL("https://raw.githubusercontent.com/aquaclient/aquaclient.github.io/main/configs/configs.json")
                        .openConnection();
                     urlConnection.setConnectTimeout(10000);
                     urlConnection.connect();
   
                     String text;
                     try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()))) {
                        while((text = bufferedReader.readLine()) != null) {
                           if (text.contains("404: Not Found")) {
                              ChatUtil.sendChatMessageWithPrefix("An error occurred while loading the configs from Github.");
                              return;
                           }
   
                           configs.add(text);
                        }
                     }
                  } catch (IOException var17x) {
                     ChatUtil.sendChatMessageWithPrefix("An error occurred while loading the configs from Github.");
                     var17x.printStackTrace();
                  }
   
                  for(String config : configs) {
                     ChatUtil.sendChatMessageWithPrefix(config);
                  }
               }
            );
            thread.start();
         }

         if (args[0].equalsIgnoreCase("list")) {
            try {
               File[] files = this.dir.listFiles();
               String list = "";

               for(int i = 0; i < files.length; ++i) {
                  list = list + ", " + files[i].getName().replace(".txt", "");
               }

               ChatUtil.sendChatMessageWithPrefix("§7Configs: §f" + list.substring(2));
            } catch (Exception var5) {
            }
         }
      } else {
         ChatUtil.sendChatMessageWithPrefix("§7config §8<§bload/save/list§8> §8<§bname§8>§f");
      }
   }
}
