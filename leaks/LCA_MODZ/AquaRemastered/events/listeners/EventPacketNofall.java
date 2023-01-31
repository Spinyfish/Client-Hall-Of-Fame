package events.listeners;

import events.Event;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;

public class EventPacketNofall extends Event {
   public static Packet packet;
   private boolean cancel;
   private EventPacketNofall.Action eventAction;
   private INetHandler netHandler;
   private EnumPacketDirection packetDirection;

   public EventPacketNofall(EventPacketNofall.Action action, Packet packet, INetHandler netHandler, EnumPacketDirection direction) {
      this.eventAction = action;
      EventPacketNofall.packet = packet;
      this.netHandler = netHandler;
      this.packetDirection = direction;
   }

   public INetHandler getNetHandler() {
      return this.netHandler;
   }

   public void setNetHandler(INetHandler netHandler) {
      this.netHandler = netHandler;
   }

   @Override
   public EnumPacketDirection getDirection() {
      return this.packetDirection;
   }

   public static Packet getPacket() {
      return packet;
   }

   public void setPacket(Packet packet) {
      EventPacketNofall.packet = packet;
   }

   @Override
   public boolean isCancelled() {
      return this.cancel;
   }

   @Override
   public void setCancelled(boolean cancel) {
      this.cancel = cancel;
   }

   public EventPacketNofall.Action getEventAction() {
      return this.eventAction;
   }

   public boolean isSend() {
      return this.getEventAction() == EventPacketNofall.Action.SEND;
   }

   public boolean isReceive() {
      return this.getEventAction() == EventPacketNofall.Action.RECEIVE;
   }

   public static enum Action {
      SEND,
      RECEIVE;
   }
}
