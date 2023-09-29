package frapppyz.cutefurry.pics.configs;

import frapppyz.cutefurry.pics.Wrapper;
import frapppyz.cutefurry.pics.modules.Mod;
import frapppyz.cutefurry.pics.modules.settings.Setting;
import frapppyz.cutefurry.pics.modules.settings.impl.Boolean;
import frapppyz.cutefurry.pics.modules.settings.impl.Mode;

import java.io.*;
import java.util.Scanner;

public class Config {
    private static String read(String name){
        String txt = "C:\\astra\\"+name+".cfg";
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        try {
            reader = new BufferedReader(new FileReader(txt));
            String line = reader.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = reader.readLine();
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }
    public static void make(String name) throws IOException {
        Wrapper.getLogger().info(read(name));
        //specify the file location
        String txt = "C:\\astra\\"+name+".cfg";
        //just starts scanner and shit

        FileWriter writer = new FileWriter(txt);

        //just sum debugging
        Wrapper.getLogger().info("Location: " + txt);
        String config = "";
        //get the mod settings etc
        for(Mod m : Wrapper.getModManager().mods){
            String setts = "";
            if(m.getSettings() != null){
                for(Setting s : m.getSettings()){
                    if(s instanceof Mode) {
                        setts = setts + (s.getName()+"!"+((Mode) s).getValueName()+":");
                    }else if(s instanceof Boolean) {
                        setts = setts + (s.getName()+"!"+((Boolean) s).isToggled()+":");
                    }
                }
            }

            config = config + (m.getName() + ":" + setts + m.isToggled() + "\n");
        }
        writer.write(config);
        //close the the reader and writer
        writer.close();
    }
}
