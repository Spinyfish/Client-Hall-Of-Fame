package wtf.automn.command.impl;

import wtf.automn.Automn;
import wtf.automn.command.Command;
import wtf.automn.command.CommandManager;
import wtf.automn.module.Module;
import wtf.automn.utils.minecraft.ChatUtil;

import java.util.List;

public class CommandToggle extends Command {

  public CommandToggle() {
    super("toggle", "Toggles a module", ".toggle <Module>", new String[]{"t"});
  }

  @Override
  public void execute(String[] args) {
    if (args.length == 1) {
      Module mod = Automn.instance().moduleManager().getModule(args[0]);
      if (mod != null) {
        mod.toggle();
      } else {
        ChatUtil.sendChatMessage("§cPlease use " + CommandManager.CHAT_PREFIX + "toggle <Module>");
      }
    } else {
      ChatUtil.sendChatMessage("§cPlease use " + CommandManager.CHAT_PREFIX + "toggle <Module>");
    }
  }

  @Override
  public List<String> autocomplete(String[] args) {
    return null;
  }
}
