package de.fanta.command;

import static de.fanta.utils.ChatUtil.messageWithoutPrefix;

import de.fanta.Client;

public class Command {

    private String name;

    public Command(String name){
        this.name = name;
    }

    public void execute(String[] args) {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static void messageWithPrefix(String msg){ messageWithoutPrefix(Client.INSTANCE.prefix + msg); }

}
