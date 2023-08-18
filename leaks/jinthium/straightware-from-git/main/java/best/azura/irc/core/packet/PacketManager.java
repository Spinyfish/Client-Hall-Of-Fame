package best.azura.irc.core.packet;

import best.azura.irc.impl.IRCConnector;
import best.azura.irc.impl.packets.client.*;
import best.azura.irc.impl.packets.server.*;
import best.azura.irc.utils.AESUtil;
import best.azura.irc.utils.Wrapper;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.HashMap;

public class PacketManager {

	private final HashMap<Integer, Packet> REGISTRY = new HashMap<>();

	public PacketManager() {
		REGISTRY.put(1, new C0LoginRequestPacket(1));
		REGISTRY.put(2, new C1ChatSendPacket(2));
		REGISTRY.put(3, new C2NameChangePacket(3));
		REGISTRY.put(4, new C3KeepAlivePacket(4));
		REGISTRY.put(5, new S0LoginResponsePacket(5));
		REGISTRY.put(6, new S1ChatSendPacket(6));
		REGISTRY.put(7, new S2NameChangePacket(7));
		REGISTRY.put(8, new S3FunnyPacket(8));
		REGISTRY.put(9, new S4BanNotifierPacket(9));
		REGISTRY.put(10, new C4BanNotifierPacket(10));
		REGISTRY.put(11, new S5EmotePacket(11));
		REGISTRY.put(12, new C5EmotePacket(12));
		REGISTRY.put(13, new S6BanPacket(13));
		REGISTRY.put(14, new C6ClientInfoPacket(14));
		REGISTRY.put(15, new C7HandshakeRequest(15));
		REGISTRY.put(16, new S7HandshakeResponse(16));
	}

	public Packet getPacketById(int Id) {
		if (REGISTRY.containsKey(Id)) {
			return REGISTRY.get(Id);
		}

		return null;
	}

	public Packet getPacket(JsonObject jsonObject) {

		if (jsonObject.has("id") && jsonObject.has("content")) {
			Packet packet = getPacketById(jsonObject.get("id").getAsInt());

			if (packet.isEncrypt()) {
				String decrypted = AESUtil.decrypt(jsonObject.get("content").getAsString(), Wrapper.getIRCConnector().getIrcData().getAESKey());

				if (decrypted != null) {
					JsonElement jsonElement1 = new JsonParser().parse(decrypted);
					if (jsonElement1 != null && jsonElement1.isJsonObject())
						packet.setContent(jsonElement1.getAsJsonObject());
				}
			} else {
				packet.setContent(jsonObject.getAsJsonObject("content"));
			}

			return packet;
		}

		return null;
	}


	public void sendPacket(IRCConnector ircConnector, Packet packet) {
		if (ircConnector.getClientSocket().isConnected() &&
				!ircConnector.getClientSocket().isOutputShutdown() &&
				!ircConnector.getClientSocket().isInputShutdown()) {

			if (packet instanceof C2NameChangePacket) {
				C2NameChangePacket c2NameChangePacket = (C2NameChangePacket) packet;
				Wrapper.getLogger().info("Updating own User with IGN: " + c2NameChangePacket.getName() + "!");
			}

			try {
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("id", packet.getId());
				if (packet.isEncrypt()) {
					jsonObject.addProperty("content", AESUtil.encrypt(Wrapper.getIRCConnector().getGson().toJson(packet.getContent()), ircConnector.getIrcData().getAESKey()));
				} else {
					jsonObject.add("content", packet.getContent());
				}
				ircConnector.getClientOutput().println(ircConnector.getGson().toJson(jsonObject));
			} catch (Exception ignore) {
			}
		}
	}

}
