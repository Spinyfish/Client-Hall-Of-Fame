package dev.tenacity.module.impl.misc;

import dev.tenacity.Tenacity;
import dev.tenacity.event.impl.network.message.ChatReceivedEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.utils.misc.FileUtils;
import dev.tenacity.utils.misc.NetworkingUtils;
import dev.tenacity.utils.player.ChatUtil;
import dev.tenacity.utils.server.ServerUtils;
import net.minecraft.util.StringUtils;

import javax.net.ssl.HttpsURLConnection;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * @author cedo
 * @since 04/20/2022
 */
public class Killsults extends Module {

    public final static File INSULT_DIR = new File(Tenacity.DIRECTORY, "Killsults.txt");
    private final List<String> messages = new ArrayList<>();
    private int index;


    public Killsults() {
        super("Killsults", Category.MISC, "Insults the player that you just killed");
    }

    @Override
    public void onChatReceivedEvent(ChatReceivedEvent event) {
        String message = StringUtils.stripControlCodes(event.message.getUnformattedText());
        if (!message.contains(":") && (
                message.contains("by " + mc.thePlayer.getName()) // hypixel
                        || message.contains("para " + mc.thePlayer.getName())
                        || message.contains("fue destrozado a manos de " + mc.thePlayer.getName())
        )) {
            if (index >= messages.size()) index = 0;
            ChatUtil.send((ServerUtils.isGeniuneHypixel() ? "/ac " : "") + messages.get(index).replace("{player}", message.trim().split(" ")[0]));
            index++;
        }
    }

    @Override
    public void onEnable() {
        messages.clear();
        if (INSULT_DIR.exists()) {
            String killsults = FileUtils.readFile(INSULT_DIR);
            messages.addAll(Arrays.asList(killsults.split("\n")));
        } else {
            fetch();
            writeToFile();
        }
        super.onEnable();
    }

    private void fetch() {
        try {
            NetworkingUtils.HttpResponse res = NetworkingUtils.httpsConnection("https://raw.githubusercontent.com/Tenacity-Client/Public-Repo/main/killsults.txt");
            if (res != null && res.getResponse() == HttpsURLConnection.HTTP_OK) {
                Scanner scanner = new Scanner(res.getContent());
                while (scanner.hasNextLine()) {
                    messages.add(scanner.nextLine());
                }
                scanner.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeToFile() {
        FileUtils.writeFile(INSULT_DIR, String.join("\n", messages));
    }


}
