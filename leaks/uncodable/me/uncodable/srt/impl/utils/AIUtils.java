package me.uncodable.srt.impl.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.stream.Collectors;
import me.uncodable.srt.Ries;
import net.minecraft.client.Minecraft;

public class AIUtils {
   private static final Minecraft MC = Minecraft.getMinecraft();
   private static final String DATABASE_ROOT = "D:\\SRT AI Database\\";
   private static final String QUESTION = "QUESTION";
   private static final String STATEMENT = "STATEMENT";

   public static void logChat(String message) {
      FileOutputStream logger = null;

      try {
         String messageToWrite = "[" + Calendar.getInstance().getTime() + "] " + message + "\n";
         logger = new FileOutputStream(String.format("%sChat\\Chat.txt", "D:\\SRT AI Database\\"), true);
         logger.write(messageToWrite.getBytes(StandardCharsets.UTF_8));
         logger.flush();
      } catch (FileNotFoundException var5) {
         Ries.INSTANCE.msg("Chat log file not found!");

         try {
            assert logger != null;

            logger.close();
         } catch (IOException var4) {
            var4.printStackTrace();
         }
      } catch (IOException var6) {
         var6.printStackTrace();
      }
   }

   public static String readAndGetResponse(String readMessage) {
      String[] splitReadMessage = readMessage.split(" ");
      String bestMatchResponse = "I am uncertain on how to respond to that. Could you try rephrasing it?";
      String category = "";
      int bestMatchCounter = 0;
      int matchCounter = 0;

      try {
         BufferedReader wordReader = new BufferedReader(new FileReader(String.format("%sWords\\Question Signals.txt", "D:\\SRT AI Database\\")));
         if (readMessage.contains("?")) {
            category = "QUESTION";
         } else {
            for(String read : wordReader.lines().collect(Collectors.toList())) {
               for(String s : splitReadMessage) {
                  if (s.equalsIgnoreCase(read)) {
                     category = "QUESTION";
                     break;
                  }
               }
            }
         }

         if (!category.equals("QUESTION")) {
            category = "STATEMENT";
         }

         wordReader.close();
      } catch (FileNotFoundException var18) {
         Ries.INSTANCE.msg("Word database not found!");
         var18.printStackTrace();
      } catch (IOException var19) {
         var19.printStackTrace();
      }

      try {
         BufferedReader sentenceReader = new BufferedReader(new FileReader(String.format("%sSentences\\Sentences.txt", "D:\\SRT AI Database\\")));

         for(String read : sentenceReader.lines().collect(Collectors.toList())) {
            String[] categoryCompare = read.split(":");
            if (category.equals(categoryCompare[categoryCompare.length - 1])) {
               String[] sentenceCompare = read.replace(String.format(":%s", category), "").split(" ");

               for(int i = 0; i < Math.max(sentenceCompare.length, splitReadMessage.length); ++i) {
                  if (i <= sentenceCompare.length - 1 && i <= splitReadMessage.length - 1) {
                     if (sentenceCompare[i].equalsIgnoreCase(splitReadMessage[i])) {
                        MC.thePlayer.sendChatMessage("[DEBUG] matchCounter:" + ++matchCounter);
                     }

                     if (i + 1 < splitReadMessage.length - 1 && sentenceCompare[i].equalsIgnoreCase(splitReadMessage[i + 1])) {
                        MC.thePlayer.sendChatMessage("[DEBUG] matchCounter:" + ++matchCounter);
                     }
                  }
               }

               if (matchCounter > bestMatchCounter) {
                  bestMatchCounter = matchCounter;
                  MC.thePlayer.sendChatMessage("[DEBUG] bestMatchCounter: " + matchCounter);
               }
            }

            matchCounter = 0;
         }

         sentenceReader.close();
      } catch (FileNotFoundException var16) {
         Ries.INSTANCE.msg("Sentence database not found!");
         var16.printStackTrace();
      } catch (IOException var17) {
         var17.printStackTrace();
      }

      return bestMatchResponse;
   }

   public static void respond(String message) {
      Ries.INSTANCE.console("MY NAME: " + MC.thePlayer.getGameProfile().getName());
      String ping = String.format("@%s", MC.thePlayer.getGameProfile().getName());
      if (message.contains(ping)) {
         MC.thePlayer.sendChatMessage(readAndGetResponse(message.split(ping + " ")[1]));
      }
   }
}
