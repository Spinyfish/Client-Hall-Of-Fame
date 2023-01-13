package me.uncodable.srt.impl.commands.metasploit.api;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

public abstract class MetasploitCommand {
   private static final Minecraft MC = Minecraft.getMinecraft();
   private final MetasploitCommandInfo info = this.getClass().getAnnotation(MetasploitCommandInfo.class);

   public abstract void exec(String[] var1);

   public void good(String msg) {
      MC.thePlayer.addChatMessage(new ChatComponentText(String.format("§5[§d§lMetasploit §d§lFramework§5] %s", msg).replace(" ", " §a")));
   }

   public void bad(String msg) {
      MC.thePlayer.addChatMessage(new ChatComponentText(String.format("§5[§d§lMetasploit §d§lFramework§5] %s", msg).replace(" ", " §c")));
   }

   public void info(String msg) {
      MC.thePlayer.addChatMessage(new ChatComponentText(String.format("§5[§d§lMetasploit §d§lFramework§5] %s", msg).replace(" ", " §9")));
   }

   public MetasploitCommandInfo getInfo() {
      return this.info;
   }
}
