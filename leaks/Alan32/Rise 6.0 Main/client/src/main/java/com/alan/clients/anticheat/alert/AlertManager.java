package com.alan.clients.anticheat.alert;

import com.alan.clients.anticheat.check.Check;
import com.alan.clients.module.impl.other.CheatDetector;
import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.util.packet.PacketUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.event.HoverEvent;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;

public final class AlertManager implements InstanceAccess {

    private final Minecraft mc = Minecraft.getMinecraft();

    public void sendAlert(final Check check) {
        final String color = getTheme().getChatAccentColor().toString();
        final String base = (color + "Rise " + color + "» " + color + "%player%§7 has failed " + color + "%check% §7(" + color +
                "%type%§7)%dev% (" + color + "%vl%§7)")
                .replaceAll("%player%", check.getData().getPlayer().getCommandSenderName())
                .replaceAll("%check%", check.getCheckInfo().name())
                .replaceAll("%type%", check.getCheckInfo().type())
                .replaceAll("%dev%", "")
                .replaceAll("%vl%", String.valueOf(check.getViolations()));

        final NetworkPlayerInfo playerInfo = mc.getNetHandler().getPlayerInfo(mc.thePlayer.getUniqueID());
        final NetworkPlayerInfo targetInfo = mc.getNetHandler().getPlayerInfo(check.getData().getPlayer().getUniqueID());

        final int ping = playerInfo == null || mc.isSingleplayer() ? 0 : playerInfo.getResponseTime();
        final int targetPing = targetInfo == null || mc.isSingleplayer() ? 0 : targetInfo.getResponseTime();

        final String hover = "§dDescription: §f" + check.getCheckInfo().description().concat("\n")
                .concat("§dYour Ping: " + ping).concat("\n")
                .concat("§dTheir Ping: " + targetPing);

        final ChatStyle hoverStyle = new ChatStyle().setChatHoverEvent(
                new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(hover))
        );

        switch (getModule(CheatDetector.class).alertType.getValue().getName()) {
            case "ClientSide":
                mc.thePlayer.addChatMessage(new ChatComponentText(base).setChatStyle(hoverStyle));
                break;

            case "ServerSide":
                PacketUtil.send(new C01PacketChatMessage("Rise > %player% has failed %check% (%type%)%dev% (x%vl%)"
                        .replaceAll("%player%", check.getData().getPlayer().getCommandSenderName())
                        .replaceAll("%check%", check.getCheckInfo().name())
                        .replaceAll("%type%", check.getCheckInfo().type())
                        .replaceAll("%dev%", "")
                        .replaceAll("%vl%", String.valueOf(check.getViolations()))));
                break;
        }
    }
}
