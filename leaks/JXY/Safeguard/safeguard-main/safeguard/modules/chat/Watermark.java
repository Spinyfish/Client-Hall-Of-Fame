package intentions.modules.chat;

import intentions.Client;
import intentions.modules.Module;
import intentions.util.Timer;
import net.minecraft.client.Minecraft;

public class Watermark extends Module {
  public static Timer timer = new Timer();
  
  public static boolean Watermark;
  
  public static String watermarkText = "| EZ";
  
  public Watermark() {
    super("Watermark", 0, Category.CHAT, "Puts a message after your message", true);
  }
  
  public static Minecraft mc = Minecraft.getMinecraft();
  
  public void onDisable() {
    Watermark = false;
  }
  
  public void onEnable() {
    Watermark = true;
  }
}
