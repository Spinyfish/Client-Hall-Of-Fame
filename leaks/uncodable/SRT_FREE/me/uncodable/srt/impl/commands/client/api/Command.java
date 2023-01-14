package me.uncodable.srt.impl.commands.client.api;

import me.uncodable.srt.Ries;
import net.minecraft.client.Minecraft;

public abstract class Command {
   protected static final Minecraft MC = Minecraft.getMinecraft();
   private final CommandInfo info = this.getClass().getAnnotation(CommandInfo.class);

   public abstract void exec(String[] var1);

   protected void printUsage() {
      Ries.INSTANCE.msg(String.format("Invalid parameters/usage. Proper usage: %s", this.info.usage()));
   }

   public CommandInfo getInfo() {
      return this.info;
   }
}
