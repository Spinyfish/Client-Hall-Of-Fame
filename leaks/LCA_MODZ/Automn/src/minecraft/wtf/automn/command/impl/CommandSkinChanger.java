package wtf.automn.command.impl;

import net.minecraft.client.Minecraft;
import wtf.automn.command.Command;
import wtf.automn.utils.minecraft.ChatUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class CommandSkinChanger extends Command {
  public CommandSkinChanger() {
    super("setskin", "Sets the Skin of yourself serverside", ".setskin <PNG Link>", new String[]{});
  }

  public static void setSkinToPlayer(final String setSkin) {
    try {
      try {
        final URL url = new URL("https://api.minecraftservices.com/minecraft/profile/skins");
        final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Authorization", "Bearer " + Minecraft.getMinecraft().getSession().getToken());
        conn.setRequestProperty("Content-Type", "application/json");
        final String data = "{\n    \"variant\": \"classic\",\n    \"url\": \"" + setSkin + "\"\n}";
        final byte[] out = data.getBytes(StandardCharsets.UTF_8);
        final OutputStream stream = conn.getOutputStream();
        stream.write(out);
        ChatUtil.sendChatMessageWithPrefix(
          "Response: " + conn.getResponseCode() + " " + conn.getResponseMessage());
        if (conn.getResponseMessage().equals("OK"))
          ChatUtil.sendChatMessageWithPrefix("§aSkin was set!");
        else
          ChatUtil.sendChatMessageWithPrefix("§cSkin couldn't be set!");
        conn.disconnect();
      } catch (final IOException ex) {
        System.err.println("Username couldn't be set!");
        ex.printStackTrace();
      }
      ChatUtil.sendChatMessage("set Skin to : " + setSkin);

    } catch (final Exception e) {
    }
  }

  @Override
  public void execute(final String[] args) {

    setSkinToPlayer(args[0]);
    //	this.getSkinURL("bb80a4c1-04a4-4fc4-b080-0b1c897a0830");

  }

  @Override
  public List<String> autocomplete(String[] args) {
    return null;
  }
}
