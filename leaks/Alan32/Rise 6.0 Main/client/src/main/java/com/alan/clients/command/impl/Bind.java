package com.alan.clients.command.impl;

import com.alan.clients.Client;
import com.alan.clients.command.Command;
import com.alan.clients.module.Module;
import com.alan.clients.util.chat.ChatUtil;
import com.alan.clients.util.localization.Localization;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import org.lwjgl.input.Keyboard;

/**
 * @author Patrick
 * @since 10/19/2021
 */
public final class Bind extends Command {

    public Bind() {
        super("command.bind.description", "bind", "binds", "keybind");
    }

    @Override
    public void execute(final String[] args) {
        if (args.length == 3) {
            final Module module = Client.INSTANCE.getModuleManager().get(args[1]);

            if (module == null) {
                ChatUtil.display("command.bind.invalidmodule");
                return;
            }

            final String inputCharacter = args[2].toUpperCase();
            final int keyCode = Keyboard.getKeyIndex(inputCharacter);

            module.setKeyCode(keyCode);
            ChatUtil.display("Bound " + Localization.get(module.getDisplayName()) + " to " + Keyboard.getKeyName(keyCode) + ".");
        } else if (args.length == 2 && args[1].equalsIgnoreCase("list")) {

            ChatUtil.display("Displaying all active binds");

            Client.INSTANCE.getModuleManager().getAll().forEach(module -> {
                if (module.getKeyCode() != 0) {
                    final String color = getTheme().getChatAccentColor().toString();

                    final ChatComponentText chatText = new ChatComponentText(color + "> " + Localization.get(module.getDisplayName()) + "§f " + Keyboard.getKeyName(module.getKeyCode()));
                    final ChatComponentText hoverText = new ChatComponentText("Click to remove " + Localization.get(module.getDisplayName()) + " bind");

                    chatText.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, ".bind " + Localization.get(module.getDisplayName()).replace(" ","") + " none"))
                            .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText));

                    mc.thePlayer.addChatMessage(chatText);
                }
            });

        } else {
            error(".bind <list/module> (KEY)");
        }
    }
}