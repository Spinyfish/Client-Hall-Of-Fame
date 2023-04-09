package tech.dort.dortware.impl.modules.misc;

import com.google.common.eventbus.Subscribe;
import net.minecraft.network.play.server.S02PacketChat;
import org.apache.commons.lang3.RandomUtils;
import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.api.module.ModuleData;
import tech.dort.dortware.api.property.impl.BooleanValue;
import tech.dort.dortware.impl.events.PacketEvent;

import java.util.ArrayList;
import java.util.List;

public class KillInsult extends Module {

    private final List<String> insults = new ArrayList<>();

    private final BooleanValue shout = new BooleanValue("Shout", this, false);

    public KillInsult(ModuleData moduleData) {
        super(moduleData);
        register(shout);
        insults.add("are you gonna cry?");
        insults.add("you're the type of person to try filling a wicker bucket with water");
        insults.add("you're the reason makeup has \"external use only\" written on the container.");
        insults.add("you're the reason this world has to put a how to use guide on shampoo");
        insults.add("somewhere in the wild is a tree producing oxygen for you, apologize to it.");
        insults.add("i bet you measure how long you slept with a ruler every morning");
        insults.add("you're the reason there's a do not drink label on bleach.");
        insults.add("why is this fat bitch begging me to turn off my hacks?");
        insults.add("on itsjhalt you got shit on harder than archybot");
        insults.add("even lolitsalex has better aim");
        insults.add("your ass is jealous of all that shit that comes out of your mouth.");
        insults.add("you're so retarded, you don't even need to play hockey to wear a helmet.");
        insults.add("if i could be one person for a day, it sure as hell wouldn't be you.");
        insults.add("somewhere in this world, someone is laughing at your profile.");
        insults.add("sometimes, i wonder how retards like you found out how to join the server");
        insults.add("even a storm trooper has better aim accuracy");
        insults.add("you're the reason there's \"do not submerge in water\" written on electric appliances.");
        insults.add("you're the type of guy to loot a store to support your race.");
        insults.add("i hope you choke on all the shit that comes out of your mouth");
        insults.add("you're the type of person to wash dishes with laundry detergent");
        insults.add("you're so ugly, you made sloth from the goonies jealous.");
        insults.add("your forehead is so big i could draw tic tac toe on it");
    }

    private String getRandomMessage() {
        return insults.get(RandomUtils.nextInt(0, insults.size()));
    }

    @Subscribe
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof S02PacketChat) {
            final String[] search = {
                    "killed by " + mc.thePlayer.getName(),
                    "slain by " + mc.thePlayer.getName(),
                    "void while escaping " + mc.thePlayer.getName(),
                    "was killed with magic while fighting " + mc.thePlayer.getName(),
                    "couldn't fly while escaping " + mc.thePlayer.getName(),
                    "fell to their death while escaping " + mc.thePlayer.getName(),
                    "was thrown into the void by " + mc.thePlayer.getName(),
                    "foi morto por " + mc.thePlayer.getName(),
                    "was thrown off a cliff by " + mc.thePlayer.getName(),
                    mc.thePlayer.getName() + " killed ",
                    "You have killed ",
                    "You have received a reward for killing ",
                    "Has ganado ",
                    "You have been rewarded $50 and 2 point(s)!"};
            S02PacketChat packetChat = event.getPacket();
            for (String string : search) {
                if (packetChat.getChatComponent().getUnformattedText().toLowerCase().contains(string.toLowerCase())) {
                    mc.thePlayer.sendChatMessage(shout.getValue() ? "/shout " + getRandomMessage() : getRandomMessage());
                    return;
                }
            }
        }
    }
}
