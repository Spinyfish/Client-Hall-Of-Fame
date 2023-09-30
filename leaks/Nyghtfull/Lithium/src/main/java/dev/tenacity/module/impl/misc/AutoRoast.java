package dev.tenacity.module.impl.misc;

import dev.tenacity.event.impl.network.message.ChatReceivedEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;

import java.util.Random;

public class AutoRoast extends Module {

    public static final String[] AUTO_ROASTS = new String[] {
            // Client Messages.
            "No, I am just using CheatBreaker Client.",
            "No, I am just using Badlion Client.",
            "No, I am just using LabyMod Client.",
            "No, I am just using Lunar Client.",
            "No, I am just using Melon Client",
            "No, I am just using Cloud Client.",

            // Liticane.
            "If you spent as much time practicing as you did accusing others of cheating, maybe you'd actually have a chance.",
            "It's cute how you think accusing me of cheating will make you feel better about losing.",
            "Keep calling me a cheater, maybe one day you'll believe it yourself.",
            "I can't help it if my talent makes you question your own abilities.",
            "Calling me a cheater won't make up for your lack of skill.",

            // Nyghtfull.
            "My skill level is higher than your render distance, you can't even see it.",
            "Hacking? No, I am just levitating above your skill level.",
            "I am not hacking, maybe stop folding so hard?",
            "Just accept that you are way too bad for me."
    };

    public static final String[] CALL_PHRASES = new String[] {
            // stuff
            "hack", "cheat", "staff", "report",

            // modules
            "speed", "bhop", "scaffold", "killaura", "aura"
    };

    public AutoRoast() {
        super("AutoRoast", Category.MISC, "Automatically roasts anyone calling you a hacker.");
    }

    @Override
    public void onChatReceivedEvent(ChatReceivedEvent event) {
        String username = mc.session.getUsername().toLowerCase();
        String message = event.getRawMessage().toLowerCase();

        boolean called = false;

        for (String phrase : CALL_PHRASES) {
            if (message.indexOf(phrase) > 0) {
                called = true;
                break;
            }
        }

        int index = new Random().nextInt(AUTO_ROASTS.length);

        if (message.contains(username) && called) {
            String random = AUTO_ROASTS[index];

            mc.thePlayer.sendChatMessage(random);
        }
    }
}