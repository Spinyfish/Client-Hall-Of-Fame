package intentions.modules.chat;

import intentions.Client;
import intentions.modules.Module;
import intentions.settings.BooleanSetting;
import intentions.settings.NumberSetting;
import intentions.settings.Setting;
import intentions.util.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C01PacketChatMessage;

public class Spammer extends Module {
  public static Timer timer = new Timer();
  
  public static String spamText = Client.name + " " + Client.version + " EZZ";
  
  public static NumberSetting time = new NumberSetting("Time", 100.0D, 1.0D, 200.0D, 1.0D);
  
  public static BooleanSetting randomized = new BooleanSetting("Random", false);
  
  public Spammer() {
    super("Spammer", 0, Module.Category.CHAT, "Spams a message in chat", true);
    addSettings(new Setting[] { (Setting)time, (Setting)randomized });
  }
  
  public static boolean spammer;
  
  public static Minecraft mc = Minecraft.getMinecraft();
  public void onDisable() {
    spammer = false;
  }
  
  public void onEnable() {
    spammer = true;
  }
  
  public void onTick() {
    if (timer.hasTimeElapsed((long)time.getValue() * 50L, true) && Spammer.spammer && mc.thePlayer != null)
      mc.thePlayer.sendQueue.addToSendQueue((Packet)new C01PacketChatMessage((String.valueOf(spamText) + (randomized.isEnabled() ? (" >" + random(8) + "<") : "") + (Watermark.Watermark ? (" " + Watermark.watermarkText) : "")))); 
  }
  
  public static String random(int length) {
    String[] values = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".split("");
    String value = "";
    for (int i = 0; i < length; i++)
      value = String.valueOf(value) + values[(int)Math.floor(Math.random() * values.length)]; 
    return value;
  }
}
