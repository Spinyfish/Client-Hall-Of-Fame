package client.module.impl.player;
import client.command.impl.Retard;
import client.event.Listener;
import client.event.annotations.EventLink;
import client.event.impl.packet.PacketReceiveEvent;
import client.module.Category;
import client.module.Module;
import client.module.ModuleInfo;
import client.util.chat.ChatUtil;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.scoreboard.ScorePlayerTeam;
import tv.twitch.chat.Chat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@ModuleInfo(name = "AntiRetard", description = "", category = Category.OTHER)
public class AntiRetard extends Module {
    private static final Pattern validUserPattern = Pattern.compile("^\\w{3,16}$");
    private final String[] retardList = {
            "SheikRAimundo",
            "fajoszz",
            "brxx07",
            "Lz7_",
            "LariNotFound_",
            "fellipcs",
            "stckZz",
            "vinibakana",
            "Drakzx",
            "minijv135",
            "duuuuh__",
            "PERTOLA",
            "LaggandoDD",
            "patao69",
            "iNowk4i20",
            "Rnzez",
            "yankv1",
            "L1P3_KKKJKJK",
            "jpsamuel123",
            "despresado",
            "KronusZNT",
            "dyzer",
            "LUKCZ1Nx",
            "ItachiGamer90",
            "0atao1",
            "zWPereira",
            "0atao1",
            "jakshushg",
            "minecraftFa011",
            "Aloown",
            "EuLessa",
            "Nwxo",
            "galixzee",
            "McLeok",
            "doutorbielzt",
            "zHxRar",
            "Poggue",
            "loro1_onlyfans",
            "rdzkkkkk",
            "NiightGod",
            "japaaxz",
            "tanjirinadecria",
            "Ronaldo32",
            "kiitoliroo",
            "netodetoo2",
            "fael9777",
            "Spr4n"
    };
    public static List<String> getOnlinePlayer() {
        return mc.getNetHandler().getPlayerInfoMap().stream()
                .map(NetworkPlayerInfo::getGameProfile)
                .map(GameProfile::getName)
                .filter(profileName -> validUserPattern.matcher(profileName).matches())
                .collect(Collectors.toList());
    }
    @EventLink()
    public final Listener<PacketReceiveEvent> onPacketReceive = event -> {
        if (event.getPacket() instanceof S02PacketChat) {
            final S02PacketChat packet = (S02PacketChat) event.getPacket();
            String message = packet.getChatComponent().getUnformattedText();


            for (String retard : retardList) {
                if (message.contains(retard)) {
                    ChatUtil.display("DETECTED RETARD!!! HIS NAME IS " + retard);
                }
            }

        }



    };
}