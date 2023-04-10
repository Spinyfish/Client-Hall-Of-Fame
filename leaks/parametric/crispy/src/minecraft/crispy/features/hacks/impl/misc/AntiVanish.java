package crispy.features.hacks.impl.misc;

import crispy.Crispy;
import crispy.features.event.Event;
import crispy.features.event.impl.player.EventPacket;
import crispy.features.hacks.Category;
import crispy.features.hacks.Hack;
import crispy.features.hacks.HackInfo;
import crispy.notification.NotificationPublisher;
import crispy.notification.NotificationType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.network.play.server.S13PacketDestroyEntities;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S3APacketTabComplete;
import net.superblaubeere27.valuesystem.ModeValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@HackInfo(name = "AntiVanish", category = Category.MISC)
public class AntiVanish extends Hack {

    private final ModeValue modeValue = new ModeValue("Mode", "Manual", "Manual", "Automatic");

    private Entity validator;
    private final ArrayList<Entity> entities = new ArrayList<>();

    @Override
    public void onEnable() {

        if (Minecraft.theWorld != null) {
            if (modeValue.getObject() == 0) {
                mc.thePlayer.sendQueue.addToSendQueue(new C14PacketTabComplete("/minecraft:msg "));
            }
        }
        entities.clear();
        super.onEnable();
    }

    @Override
    public void onDisable() {

        super.onDisable();
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof EventPacket) {
            EventPacket event = (EventPacket) e;
            if (event.getPacket() instanceof C01PacketChatMessage) {
                C01PacketChatMessage packet = (C01PacketChatMessage) event.getPacket();
                if (packet.getMessage().contains("!check")) {
                    Crispy.addChatMessage("checking for retards ");
                    mc.thePlayer.sendQueue.addToSendQueue(new C14PacketTabComplete("/minecraft:msg "));
                }
                for(String name : getTabPlayerList()) {
                    if (packet.getMessage().contains(name)) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C14PacketTabComplete("/minecraft:msg "));
                        break;
                    }
                }
            }
            if (event.getPacket() instanceof S13PacketDestroyEntities) {
                ArrayList<Integer> ids = new ArrayList<>();
                for (Object o : Minecraft.theWorld.loadedEntityList) {
                    if (o instanceof EntityPlayer) {
                        ids.add(((EntityPlayer) o).getEntityId());
                    }
                }

                S13PacketDestroyEntities s13PacketDestroyEntities = (S13PacketDestroyEntities) event.getPacket();
                for (int id : s13PacketDestroyEntities.getEntityIDs()) {
                    if (ids.contains(id)) {


                        mc.thePlayer.sendQueue.addToSendQueue(new C14PacketTabComplete("/minecraft:msg "));
                        S14PacketEntity s14PacketEntity = new S14PacketEntity(id);
                        Entity entity = s14PacketEntity.getEntity(mc.thePlayer.getEntityWorld());
                        //Fuck you come back...
                        entities.add(entity);

                        validator = entity;


                    }
                }

            }

            if (event.getPacket() instanceof S3APacketTabComplete) {

                S3APacketTabComplete s3APacketTabComplete = (S3APacketTabComplete) event.getPacket();
                String[] tab = s3APacketTabComplete.func_149630_c();

                List<String> names = new ArrayList<>();
                for (String player : getTabPlayerList()) {
                    names.add(player);
                }
                ArrayList<String> flaggedPlayers = new ArrayList<>();
                for (String string : tab) {
                    if (!names.contains(string)) {
                        flaggedPlayers.add(string);
                    }
                }
                if (!flaggedPlayers.isEmpty()) {
                    if (modeValue.getObject() == 0) {
                        Crispy.addChatMessage("Vanish players: ");
                        for (String flagged : flaggedPlayers) {
                            Crispy.addChatMessage(flagged);
                        }
                    } else {//opinion
                        if (flaggedPlayers.contains(validator.getCommandSenderName())) {
                            NotificationPublisher.queue("[Anti Vanish]", "Player " + validator.getCommandSenderName() + " tried to vanish at " + validator.getPosition(), NotificationType.WARNING, 10000);
                            //mc.thePlayer.sendChatMessage("[Anti Vanish] Player " + validator.getCommandSenderName() + " tried to vanish at " + validator.getPosition());
                        }

                    }
                } else if (modeValue.getObject() == 0) {
                    Crispy.addChatMessage("No one is in vanish.");
                }
                if (modeValue.getObject() == 0) {
                    toggle();
                }

            }
        }
    }


    public ArrayList<String> getTabPlayerList() {
        final NetHandlerPlayClient var4 = mc.thePlayer.sendQueue;
        final ArrayList<String> list = new ArrayList<>();
        final java.util.List players = GuiPlayerTabOverlay.field_175252_a.sortedCopy(var4.getPlayerInfoMap());
        for (final Object o : players) {
            final NetworkPlayerInfo info = (NetworkPlayerInfo) o;
            if (info == null) {
                continue;
            }
            list.add(info.getGameProfile().getName());
        }
        return list;
    }
}
