package me.uncodable.srt.impl.commands.client.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandManager {
   private final List<Command> commands = new ArrayList<>();

   public void addCommands(Command... commandsList) {
      this.commands.addAll(Arrays.asList(commandsList));
   }

   public List<Command> getCommands() {
      return this.commands;
   }
}
