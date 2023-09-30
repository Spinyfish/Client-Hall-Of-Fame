package dev.africa.pandaware.impl.command.client;

import dev.africa.pandaware.api.command.Command;
import dev.africa.pandaware.api.command.interfaces.CommandInformation;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

@CommandInformation(name = "sus", description = "have you check out Juul yet?")
public class SusCommand extends Command {

    private Player player;

    @Override
    public void process(String[] arguments) {
        new Thread(() -> {
            try {
                player = new Player(this.getClass().getResourceAsStream("/assets/minecraft/pandaware/juul.mp3"));
                player.play();
            } catch (JavaLayerException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
