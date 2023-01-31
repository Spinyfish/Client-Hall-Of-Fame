package skid;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import intent.AquaDev.aqua.Aqua;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import javax.swing.JOptionPane;
import net.minecraft.client.main.LauncherAPI;

public class Updater {
   public static final String URL_BASE = "https://aquaclient.github.io/";
   private static final String LOCAL_PATH = new File(AquaSkid.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParentFile().getPath();

   private static String sendMeLTCOrGay() {
      return "Lf6jHpjnUd7bT9x5o2eQYWbn2bmGqZP3cY";
   }

   private static JsonObject fetchLatestVersion() {
      try (BufferedReader in = new BufferedReader(new InputStreamReader(new URL("https://aquaclient.github.io/version.json").openStream()))) {
         StringBuilder sb = new StringBuilder();

         String inputLine;
         while((inputLine = in.readLine()) != null) {
            sb.append(inputLine);
         }

         return new Gson().fromJson(sb.toString(), JsonObject.class);
      } catch (IOException var16) {
         var16.printStackTrace();
         return null;
      }
   }

   public static boolean isUpdateAvailable() {
      String latestVersion = fetchLatestVersion().get("version").getAsString();
      double sumLatest = Double.parseDouble(latestVersion);
      double sumCurrent = Double.parseDouble(Aqua.build);
      return sumLatest > sumCurrent;
   }

   public static void update() {
      FileDownload download = new FileDownload("https://aquaclient.github.io/Updater.jar", LOCAL_PATH + File.separator + "Updater.jar");
      download.start(
         () -> {
            System.out.println("Finished downloading Updater.jar");
            String jre = System.getProperty("java.home");
            if (!jre.startsWith("1.8")) {
               int result = JOptionPane.showConfirmDialog(
                  null, "You must have Java 1.8 installed!\n If you don't want to use the updater, press 'No'", "Updater", 0
               );
               if (result == 0) {
                  try {
                     Desktop.getDesktop().browse(new URI("https://www.java.com/de/download/manual.jsp"));
                  } catch (URISyntaxException | IOException var6) {
                     JOptionPane.showMessageDialog(null, "Failed to open download page");
                     var6.printStackTrace();
                  }
               } else if (result == 1) {
                  System.out.println("User skipped update");
                  return;
               }
            }
   
            ProcessBuilder builder = null;
            switch(LauncherAPI.OS.getOperatingSystem()) {
               case WINDOWS:
                  builder = new ProcessBuilder("cmd.exe", "/c", "java -jar Updater.jar --update");
                  break;
               case LINUX:
                  builder = new ProcessBuilder("java", "-jar", "Updater.jar", "--update");
                  break;
               case MACOS:
                  builder = new ProcessBuilder("java", "-jar", "Updater.jar", "--update");
            }
   
            if (builder == null) {
               throw new RuntimeException("Unsupported OS");
            } else {
               try {
                  builder.directory(new File("versions" + File.separator + "Aqua"));
                  builder.inheritIO();
                  builder.start();
               } catch (IOException var5) {
                  var5.printStackTrace();
               }
   
               int exitCode = -1;
   
               try {
                  exitCode = builder.start().waitFor();
               } catch (IOException | InterruptedException var4) {
                  System.exit(1);
               }
   
               System.exit(exitCode);
            }
         }
      );
   }

   private static boolean isJavaInstalled() {
      String os = System.getProperty("os.name").toLowerCase();
      if (os.contains("win")) {
         return isJavaInstalledWindows();
      } else {
         return !os.contains("mac") && !os.contains("nix") && !os.contains("nux") && !os.contains("aix") ? false : isJavaInstalledLinux();
      }
   }

   private static boolean isJavaInstalledLinux() {
      String javaHome = System.getProperty("java.home");
      if (javaHome == null) {
         return false;
      } else {
         String javaPath = javaHome + "/bin/java";
         File javaFile = new File(javaPath);
         return javaFile.exists();
      }
   }

   private static boolean isJavaInstalledWindows() {
      String javaHome = System.getProperty("java.home");
      if (javaHome == null) {
         return false;
      } else {
         String javaPath = javaHome + "\\bin\\java.exe";
         File javaFile = new File(javaPath);
         return javaFile.exists();
      }
   }
}
