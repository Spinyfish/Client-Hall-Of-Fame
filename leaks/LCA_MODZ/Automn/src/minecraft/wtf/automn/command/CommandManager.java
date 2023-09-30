package wtf.automn.command;

import wtf.automn.command.impl.CommandBind;
import wtf.automn.command.impl.CommandSetting;
import wtf.automn.command.impl.CommandSkinChanger;
import wtf.automn.command.impl.CommandToggle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandManager {
  public static final String CHAT_PREFIX = ".";
  public List<Command> commands = new ArrayList();

  public CommandManager() {
    this.addCommand(new CommandBind());
    this.addCommand(new CommandSkinChanger());
    this.addCommand(new CommandToggle());
    this.addCommand(new CommandSetting());
  }

  public void addCommand(final Command cmd) {
    commands.add(cmd);
  }

  public boolean execute(String text) {
    if (!text.startsWith(CHAT_PREFIX)) return false;
    text = text.substring(1);
    final String[] arguments = text.split(" ");
    for (final Command cmd : commands) {
      if (cmd.getName().trim().equalsIgnoreCase(arguments[0].trim())) {
        final String[] args = Arrays.copyOfRange(arguments, 1, arguments.length);
        cmd.execute(args);
        return true;
      }
      for (String s : cmd.getAliases()) {
        if (s.trim().equalsIgnoreCase(arguments[0].trim())) {
          final String[] args = Arrays.copyOfRange(arguments, 1, arguments.length);
          cmd.execute(args);
          return true;
        }
      }
    }
    return false;
  }

  public Command getCommand(String s) {
    for (final Command cmd : commands) {
      if (cmd.getName().trim().equalsIgnoreCase(s.trim())) {

        return cmd;
      }
      for (String als : cmd.getAliases()) {
        if (als.trim().equalsIgnoreCase(s.trim())) {
          return cmd;
        }
      }
    }
    return null;
  }

}
