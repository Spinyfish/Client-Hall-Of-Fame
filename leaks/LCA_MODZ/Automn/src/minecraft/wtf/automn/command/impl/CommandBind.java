package wtf.automn.command.impl;


import org.lwjgl.input.Keyboard;
import wtf.automn.Automn;
import wtf.automn.command.Command;
import wtf.automn.module.Module;
import wtf.automn.utils.minecraft.ChatUtil;

import java.util.List;


public class CommandBind extends Command {

    public CommandBind() {
        super("bind", "Binds a Module to a key", ".bind <Module> <Key>", new String[]{"b"});
    }

    @Override
    public void execute(final String[] args) {

        try {
            final Module mod = Automn.instance().moduleManager().getModule(args[0]);
            ChatUtil.messageWithoutPrefix("§7Bind §8<§bModule§8> §8<§bButton§8>§f");
            if (args.length != 2) return;

            final String key = args[1];

            //ChatUtil.messageWithoutPrefix(" §3The Module §5§l" + mod.display() + "§r§3 was set on §9§l" + key.toUpperCase() + "§r.");
            //  FileUtil.saveKeys();
            if (mod != null)
                mod.keybinding(Keyboard.getKeyIndex(args[1].toUpperCase()));
            else
                ChatUtil.messageWithoutPrefix(" §cThe Module §5§l" + args[0] + "§r§c was not found :( .");
        } catch (final ArrayIndexOutOfBoundsException e) {

            ChatUtil.messageWithoutPrefix(" §7Bind §8<§bModule§8> §8<§bButton§8>§f");
            e.printStackTrace();

        }
    }

    @Override
    public List<String> autocomplete(final String[] args) {
        return null;
    }

}
