package de.fanta.design;

import net.minecraft.client.Minecraft;

import java.io.*;
import java.util.ArrayList;

import de.fanta.Client;
import de.fanta.utils.FriendSystem;

public class AltsSaver {

    public static ArrayList<AltTypes> AltTypeList;
    public static final File altFile;

    static {
        AltTypeList = new ArrayList<AltTypes>();
        altFile = new File(Minecraft.getMinecraft().mcDataDir + "/" + Client.INSTANCE.name + "/alts.txt");
    }

    public static void saveAltsToFile() {
        try {
            if (!altFile.exists()) {
                try {
                    altFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            final PrintWriter writer = new PrintWriter(altFile);
            for (final AltTypes slot : AltTypeList) {
                AltManager.i += 40;
                writer.write(String.valueOf(slot.getEmail()) + ":" + slot.getPassword() + "\n");

            }
            writer.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void altsReader() {
        try {
            if (!altFile.exists()) {
                System.out.println("Alt not found. Error 22");
            }

            final BufferedReader reader = new BufferedReader(new FileReader(altFile));
            String line;
            AltTypes altTypes = new AltTypes("", "");

                while ((line = reader.readLine()) != null) {
                    String[] arguments = line.split(":");
                    altTypes.setPassword(arguments[0]);
                    altTypes.setEmail(arguments[1]);
                    AltTypeList.add(new AltTypes(arguments[0], arguments[1]));


            }
            reader.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
