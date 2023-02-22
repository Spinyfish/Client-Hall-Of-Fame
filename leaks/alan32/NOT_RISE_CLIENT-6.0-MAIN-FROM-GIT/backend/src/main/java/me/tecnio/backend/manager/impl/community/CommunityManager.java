package me.tecnio.backend.manager.impl.community;

import community.Message;
import me.tecnio.backend.Backend;
import me.tecnio.backend.manager.Manager;
import me.tecnio.backend.network.Client;
import me.tecnio.util.DiscordWebhook;
import packet.Packet;
import packet.impl.client.community.ClientCommunityMessageSend;
import packet.impl.client.community.ClientCommunityPopulateRequest;
import packet.impl.server.community.ServerCommunityMessageSend;
import packet.impl.server.community.ServerCommunityPopulatePacket;
import util.type.EvictingList;

public class CommunityManager extends Manager {

    private DiscordWebhook discordWebhook = new DiscordWebhook("https://discord.com/api/webhooks/1055427142722256967/KB3kq29NN4SjQ8a6IR9WnKeuqCMfpV7hvO-Ebm_jF6I4hu1FYlUyUW0dBCJ27qXcvpzb");
    private EvictingList<Message> messages = new EvictingList<>(200);

    @Override
    public void packet(Packet<?> packet, Client client) {
        if (packet instanceof ClientCommunityPopulateRequest) {
            if (client.timeSinceMessagesPopulated.finished(1000 * 10)) {
                client.timeSinceMessagesPopulated.reset();
                client.getCommunication().write(new ServerCommunityPopulatePacket(messages));
            }
        } else if (packet instanceof ClientCommunityMessageSend) {
            ClientCommunityMessageSend messageSend = ((ClientCommunityMessageSend) packet);

            Message message = new Message(client.getUsername().contains("@") ? "Hidden username" : client.getUsername(), messageSend.getMessage());
            messages.add(message);

            Backend.INSTANCE.getServer().getClients().forEach(c -> c.getCommunication().write(new ServerCommunityMessageSend(message)));

            /*try {
                discordWebhook.setUsername(client.getUsername());
                discordWebhook.setContent(messageSend.getMessage());
                discordWebhook.setAvatarUrl("https://lh3.googleusercontent.com/u/0/drive-viewer/AFDK6gM_fx3wNvZVoqEHYq4H9_sdNfR_jE_iWXZOK-QT8NEN97siau_6Qf3Cz0wAEBK9eV7eJ_oZbiuq65RKaCmnYLo4uNpTOQ=w1257-h565");
                discordWebhook.execute();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }*/

            System.out.println("Messages: " + messages.size());
        }
    }
}
