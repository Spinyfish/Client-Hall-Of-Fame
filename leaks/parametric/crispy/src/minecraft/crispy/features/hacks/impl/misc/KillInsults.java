package crispy.features.hacks.impl.misc;

import crispy.features.event.Event;
import crispy.features.event.impl.player.EventPacket;
import crispy.features.hacks.Category;
import crispy.features.hacks.Hack;
import crispy.features.hacks.HackInfo;
import crispy.features.hacks.impl.combat.Aura;
import crispy.util.file.Filer;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S02PacketChat;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

@HackInfo(name = "KillInsults", category = Category.MISC)
public class KillInsults extends Hack {
    @Getter
    Filer filer = new Filer("KillInsults", "Crispy");
    ArrayList<String> killInsults = new ArrayList<>();
    String[] defaultKill = {
            "Hey {name}, go commit on PrenstonPlayz looser",
            "LOL {name}, what client you using FLUX?",
            "Have you taken a dump lately {name}? Cause i beat the shit out of you.",
            "Have you taken your medicine {name} you shit closet cheater.",
            "Dang why you so mad {name}",
            "{name}, my blind grandpa has better aim than you.",
            "{name}, Stop hacking you blantant cheater!",
            "{name}, You're the type of person to get 3rd place in a 1v1.",
            "{name}, LMAO what client Crispy?",
            "{name}, please consider not alive.",
            "{name}, How are you so bad. I'm loosing brain cells looking at you.",
            "{name}, You're the inspiration for birth control",
            "{name}, Get off pretonplayz looser, you 7 year old fan boy",
            "{name}, /friend me so we can talk how much of a looser you are.",
            "{name}, Go commit not living",
            "{name}, Bhopping STRIKE",
            "{name}, What client is that crystalgae?",
            "{name}, Is that moon shit I smell",
            "{name}, Hope you finally choke on your anti-depressant pills so you can finally masturbate to your own death",
            "Wow what client is that {name}? I bet it's skidded."


    };
    private final File file = new File(Minecraft.getMinecraft().mcDataDir + "/Crispy/KillInsults.txt");

    public KillInsults() {
        if (!file.exists()) {
            try {
                filer.write(defaultKill);

            } catch (Exception e) {

            }
        }
        killInsults = filer.read();

    }


    @Override
    public void onEnable() {
        killInsults = filer.read();
        super.onEnable();
    }

    @Override
    public void onDisable() {

        super.onDisable();
    }

    @Override
    public void onEvent(Event e) {

        if (e instanceof EventPacket) {
            Packet p = ((EventPacket) e).getPacket();
            if (p instanceof S02PacketChat) {
                S02PacketChat packet = (S02PacketChat) p;
                String message = packet.getChatComponent().getUnformattedText();

                if (message.contains("killed by " + mc.thePlayer.getCommandSenderName()) || message.contains("morto por " + mc.thePlayer.getCommandSenderName()) || message.contains("died to " + mc.thePlayer.getCommandSenderName())) {
                    ArrayList<String> WordsList = new ArrayList<>();
                    ArrayList<EntityPlayer> players = new ArrayList<>();
                    String[] words = message.split(" ");

                    for (String eachWord : words) {
                        WordsList.add(eachWord);

                    }
                    for(Object o : Minecraft.theWorld.loadedEntityList) {
                        if(o instanceof EntityPlayer) {
                            players.add((EntityPlayer) o);
                        }
                    }
                    EntityPlayer randomPlayer = players.get(Aura.randomNumber(players.size() - 1, 0));

                    EntityPlayer anotherRandom = players.get(Aura.randomNumber(players.size() - 1, 0));
                    String victim = WordsList.get(3);
                    Random r = new Random();
                    int randomNumber = r.nextInt(killInsults.size());
                    mc.thePlayer.sendChatMessage(killInsults.get(randomNumber).replace("{name}", victim).replace("{random}", randomPlayer.getCommandSenderName()).replace("{random2}", anotherRandom.getCommandSenderName()));

                } else if(message.contains("You killed")) {
                    ArrayList<String> WordsList = new ArrayList<>();
                    ArrayList<EntityPlayer> players = new ArrayList<>();
                    String[] words = message.split(" ");

                    for (String eachWord : words) {
                        WordsList.add(eachWord);

                    }
                    for(Object o : Minecraft.theWorld.loadedEntityList) {
                        if(o instanceof EntityPlayer) {
                            players.add((EntityPlayer) o);
                        }
                    }
                    EntityPlayer randomPlayer = players.get(Aura.randomNumber(players.size() - 1, 0));

                    EntityPlayer anotherRandom = players.get(Aura.randomNumber(players.size() - 1, 0));
                    String victim = WordsList.get(3);
                    Random r = new Random();
                    int randomNumber = r.nextInt(killInsults.size());
                    mc.thePlayer.sendChatMessage(killInsults.get(randomNumber).replace("{name}", victim).replace("{random}", randomPlayer.getCommandSenderName()).replace("{random2}", anotherRandom.getCommandSenderName()));
                }


            }
        }
    }
}
