package wtf.automn.command.impl;

import wtf.automn.Automn;
import wtf.automn.command.Command;
import wtf.automn.module.Module;
import wtf.automn.module.settings.*;
import wtf.automn.utils.minecraft.ChatUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CommandSetting extends Command {

  public CommandSetting() {
    super("setting", "Sets values of Settings", ".setting <set/get> <Module> <Setting> <Value>", new String[]{"s", "set"});
  }

  @Override
  public void execute(String[] args) {
    if (args.length >= 3) {
      String type = args[0];
      String modID = args[1];
      Module mod = Automn.instance().moduleManager().getModule(modID);
      if (mod == null) {
        ChatUtil.sendChatMessage("§c" + getSyntax());
        return;
      }
      Setting s = mod.getSetting(args[2]);
      if (s == null) {
        ChatUtil.sendChatMessage("§c" + getSyntax());
        return;
      }
      if (type.equalsIgnoreCase("get")) {
        ChatUtil.sendChatMessage("Value of §e" + s.id + "§r from §e" + mod.display() + "§r is §e" + s.value);
      } else if (type.equalsIgnoreCase("set")) {
        if (args.length == 4) {
          String value = args[3];
          s.value = getCorrectValue(s, value);
          ChatUtil.sendChatMessage("Value of §e" + s.id + "§r from §e" + mod.display() + "§r is §e" + s.value);
        } else {
          ChatUtil.sendChatMessage("§c" + getSyntax());
          return;
        }
      }
      mod.saveToFile(mod.id());
    }
  }

  public Object getCorrectValue(Setting setting, String value) {
    Object o = null;
    if (setting instanceof SettingBoolean) {
      if (value.equalsIgnoreCase("true")) {
        o = true;
      } else {
        o = false;
      }
    } else if (setting instanceof SettingColor) {
      try {
        o = Integer.parseInt(value);
      } catch (Exception e) {
        o = Color.red.getRGB();
      }
    } else if (setting instanceof SettingNumber) {
      try {
        o = Double.parseDouble(value);
      } catch (Exception e) {
        o = 1.0;
      }
    } else if (setting instanceof SettingString) {
      o = value;
    }
    return o;
  }

  @Override
  public List<String> autocomplete(String[] args) {
    if (args.length == 0) {
      List<String> ret = new ArrayList<>();
      ret.add("set");
      ret.add("get");
      return ret;
    }
    if (args.length == 1) {
      List<String> ret = new ArrayList<>();
      if ("set".startsWith(args[0])) {
        ret.add("set");
      }
      if ("get".startsWith(args[0])) {
        ret.add("get");
      }
      return ret;
    }
    if (args.length == 2) {
      List<String> ret = new ArrayList<>();
      for (Module mod : Automn.instance().moduleManager().getModules()) {
        if (args[1].equals("")) {
          ret.add(mod.id());
        } else {
          if (mod.id().startsWith(args[1])) {
            ret.add(mod.id());
          }
        }
      }
      return ret;
    }
    if (args.length == 3) {
      List<String> ret = new ArrayList<>();
      for (Setting setting : Automn.instance().moduleManager().getModule(args[1]).getSettings()) {
        if (args[2].equals("")) {
          ret.add(setting.id);
        } else {
          if (setting.id.startsWith(args[2])) {
            ret.add(setting.id);
          }
        }
      }
      return ret;
    }
    if (args.length == 4 && args[0].equalsIgnoreCase("set")) {
      try {
        List<String> ret = new ArrayList<>();
        String modID = args[1];
        Module mod = Automn.instance().moduleManager().getModule(modID);
        Setting s = mod.getSetting(args[2]);

        if (s instanceof SettingBoolean) {
          if (args[3].equals("")) {
            ret.add("true");
            ret.add("false");
          } else {
            if ("true".startsWith(args[3])) ret.add("true");
            if ("false".startsWith(args[3])) ret.add("false");
          }
        }
        if (s instanceof SettingNumber) {
          if (args[3].equals("")) {
            ret.add("0");
            ret.add("1");
          } else {
            if ("0".startsWith(args[3])) ret.add("0");
            if ("1".startsWith(args[3])) ret.add("1");
          }
        }
        if (s instanceof SettingString) {
          if (args[3].equals("")) {
            ret.add("Text");
          } else {
            if ("Text".startsWith(args[3])) ret.add("Text");
          }
        }
        if (s instanceof SettingColor) {
          if (args[3].equals("")) {
            ret.add(Color.red.getRGB() + "");
          } else {
            if ((Color.red.getRGB() + "").startsWith(args[3]))
              ret.add(Color.red.getRGB() + "");
          }
        }
        return ret;
      } catch (Exception e) {
      }
    }
    return null;
  }
}
