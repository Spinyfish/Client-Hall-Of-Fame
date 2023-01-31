package skid;

public class AquaSkid {
   public static void checkForUpdates() {
      System.out.println("Checking for updates...");
      if (Updater.isUpdateAvailable()) {
         System.out.println("Update found!");
         Updater.update();
      }
   }
}
