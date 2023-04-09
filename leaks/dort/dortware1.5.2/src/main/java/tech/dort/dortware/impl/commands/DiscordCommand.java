package tech.dort.dortware.impl.commands;

import tech.dort.dortware.api.command.Command;
import tech.dort.dortware.api.command.CommandData;
import tech.dort.dortware.impl.utils.player.ChatUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class DiscordCommand extends Command {

    private String discordLink;

    public DiscordCommand() {
        super(new CommandData("discord", "disc"));
        URL url;
        try {
            url = new URL("https://gardeningtool.dev/dortware/discord.txt");
            URLConnection connection = url.openConnection();
            connection.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)");
            connection.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                discordLink = line;
                break;
            }
        } catch (IOException exc) {
            discordLink = "An error has occured!";
        }

    }

    @Override
    public void run(String command, String... args) {
        ChatUtil.displayChatMessage("The Discord link is " + discordLink);
    }
}
