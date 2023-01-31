package intent.AquaDev.aqua.alt.design;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class AltsSaver {
   public static ArrayList<AltTypes> AltTypeList = new ArrayList<>();
   public static final File altFile = null;

   public static void saveAltsToFile() {
      try {
         if (!altFile.exists()) {
            try {
               altFile.createNewFile();
            } catch (IOException var3) {
               var3.printStackTrace();
            }
         }

         PrintWriter writer = new PrintWriter(altFile);

         for(AltTypes slot : AltTypeList) {
            AltManager.i += 40;
            writer.write(slot.getEmail() + ":" + slot.getPassword() + "\n");
         }

         writer.close();
      } catch (Exception var4) {
         var4.printStackTrace();
      }
   }

   public static void altsReader() {
      try {
         if (!altFile.exists()) {
            System.out.println("Alt not found. Error 22");
         }

         BufferedReader reader = new BufferedReader(new FileReader(altFile));
         AltTypes altTypes = new AltTypes("", "");

         String line;
         while((line = reader.readLine()) != null) {
            String[] arguments = line.split(":");
            altTypes.setPassword(arguments[0]);
            altTypes.setEmail(arguments[1]);
            AltTypeList.add(new AltTypes(arguments[0], arguments[1]));
         }

         reader.close();
      } catch (Exception var4) {
         var4.printStackTrace();
      }
   }
}
