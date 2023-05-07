package client.module.impl.player;
import client.event.Listener;
import client.event.annotations.EventLink;
import client.event.impl.motion.MotionEvent;
import client.event.impl.packet.PacketReceiveEvent;
import client.module.Category;
import client.module.Module;
import client.module.ModuleInfo;
import client.util.chat.ChatUtil;
import client.util.player.MathUtils;
import client.util.player.TimerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.server.S02PacketChat;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@ModuleInfo(name = "AutoReport", description = "Report Random Retards on MushMC", category = Category.OTHER)
public class AutoReport extends Module {

    private int index = 0;
    private List<String> StringList;
    private TimerUtil timer = new TimerUtil();
    private Random random = new Random();

    @Override
    public void onDisable() {
        super.onDisable();
        StringList.clear();
        StringList = null;
    }
    @EventLink()
    public final Listener<MotionEvent> onMotionEventListener = event -> {
        if (mc.theWorld == null) return;
        if (timer.sleep((int) (3 * 1000))) {
            if (index < StringList.size()) {
                final ArrayList<NetworkPlayerInfo> niggas = new ArrayList<>(mc.thePlayer.sendQueue.getPlayerInfoMap());
                final String msg = StringList.get(index).replace("%RANDOMPLAYER%", niggas.size() < 2 ? "": niggas.get(random.nextInt(niggas.size())).getGameProfile().getName()).replace("%INVISIBLE%","\u061C").replace("%RANDOMNUMBER%", String.valueOf(MathUtils.getRandomInRange(10000, 99999)));
                mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C01PacketChatMessage(msg));
                //if (Objects.equals("%RANDOMPLAYER%", mc.thePlayer.getName())) return; //balls
                index++;
            } else {
                index = 0;
            }
        }

    };
    @Override
    public void onEnable() {
        super.onEnable();


            StringList = new ArrayList<>();
            StringList.add("/report %RANDOMPLAYER% xitado");

        index = 0;
    }
}