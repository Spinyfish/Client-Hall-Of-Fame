package best.azura.irc.impl.packets.server;

import best.azura.irc.core.entities.User;
import best.azura.irc.core.packet.Packet;
import com.google.gson.JsonObject;


public class S3FunnyPacket extends Packet {

	public static int packetId = 0;

	public S3FunnyPacket(int Id) {
		super(Id);
		packetId = Id;
	}

	public S3FunnyPacket(String funnyAction) {
		super((User) null);
		Id = packetId;

		JsonObject jsonObject = getContent();

		jsonObject.addProperty("funny", funnyAction);

		setContent(jsonObject);
	}
}
