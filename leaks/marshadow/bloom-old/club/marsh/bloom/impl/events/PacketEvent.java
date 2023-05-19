package club.marsh.bloom.impl.events;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.Packet;

public class PacketEvent extends Event {
	@Getter @Setter
	private Packet packet;
	public PacketEvent(Packet packet) {
		this.packet = packet;
	}
}
