package me.uncodable.srt.impl.commands.client.impl;

import me.uncodable.srt.Ries;
import me.uncodable.srt.impl.commands.client.api.Command;
import me.uncodable.srt.impl.commands.client.api.CommandInfo;

@CommandInfo(
   name = "Username",
   desc = "Retrieves your username.",
   usage = ".username"
)
public class Username extends Command {
   @Override
   public void exec(String[] args) {
      Ries.INSTANCE.msg(String.format("Your current username is \"%s.\"", MC.thePlayer.getGameProfile().getName()));
   }
}
