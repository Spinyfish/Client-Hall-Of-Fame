package client.command.impl;


import client.command.Command;

import client.util.chat.ChatUtil;
import com.mojang.realmsclient.gui.ChatFormatting;

import java.util.ArrayList;
import java.util.List;


public final class Retard extends Command {
    public Retard() {
        super("Add player to retard list", "retard", "rt");
    }

    public static List<String> retardNames = new ArrayList<>();

    public void gat2t23() {
        ChatUtil.display(ChatFormatting.GRAY + "Ошибка в использовании" + ChatFormatting.WHITE + ":");
        ChatUtil.display(ChatFormatting.WHITE + ".staff " + ChatFormatting.GRAY + "<"
                + ChatFormatting.RED + "add; remove; clear; list." + ChatFormatting.GRAY + ">");
    }

    @Override
    public void execute(String[] args) {
        if (args.length >= 2) {
            if (args[1].equalsIgnoreCase("add")) {
                if (retardNames.contains(args[2])) {
                    ChatUtil.display(ChatFormatting.RED + "Этот игрок уже в Staff List!");
                } else {
                    retardNames.add(args[2]);
                    ChatUtil.display(ChatFormatting.GREEN + "Ник " + ChatFormatting.WHITE + args[2] + ChatFormatting.GREEN + " добавлен в Staff List");
                }
            }
            if (args[1].equalsIgnoreCase("remove")) {
                if (retardNames.contains(args[2])) {
                    retardNames.remove(args[2]);
                    ChatUtil.display(ChatFormatting.GREEN + "Name " + ChatFormatting.WHITE + args[2] + ChatFormatting.GREEN + " removed from retard list");
                } else {
                    ChatUtil.display(ChatFormatting.RED + "Этого игрока нет в Staff List!");
                }
            }
            if (args[1].equalsIgnoreCase("clear")) {
                if (retardNames.isEmpty()) {
                    ChatUtil.display(ChatFormatting.RED + "Staff List пуст!");
                } else {
                    retardNames.clear();
                    ChatUtil.display(ChatFormatting.GREEN + "Staff List очищен");
                }
            }
            if (args[1].equalsIgnoreCase("list")) {
                ChatUtil.display(ChatFormatting.GRAY + "Список Staff:");
                for (String name : retardNames) {
                    ChatUtil.display(ChatFormatting.WHITE + name);
                }
            }

        } else {
            gat2t23();
        }
    }
}
