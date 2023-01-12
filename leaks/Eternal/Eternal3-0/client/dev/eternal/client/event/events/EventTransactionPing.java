package dev.eternal.client.event.events;

import dev.eternal.client.event.AbstractEvent;
import dev.eternal.client.util.network.PacketUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;

@Getter
@Setter
@AllArgsConstructor
public class EventTransactionPing extends AbstractEvent {

  private int txWindowId;
  private short txActionId;

  public void duplicate() {
    for (int i = 0; i < 2; i++) {
      PacketUtil.sendSilent(new C0FPacketConfirmTransaction(txWindowId, txActionId, true));
    }
    cancelled(true);
  }

}
