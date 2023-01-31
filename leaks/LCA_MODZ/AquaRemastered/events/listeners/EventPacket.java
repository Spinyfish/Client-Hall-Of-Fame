package events.listeners;

import events.Event;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;

public class EventPacket extends Event {
   public static Packet packet;
   private boolean cancel;
   private EventPacket.Action eventAction;
   private INetHandler netHandler;
   private EnumPacketDirection packetDirection;

   public EventPacket(EventPacket.Action action, Packet packet, INetHandler netHandler, EnumPacketDirection direction) {
      this.eventAction = action;
      EventPacket.packet = packet;
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
      EventPacket.packet = packet;
   }

   @Override
   public boolean isCancelled() {
      return this.cancel;
   }

   @Override
   public void setCancelled(boolean cancel) {
      this.cancel = cancel;
   }

   public EventPacket.Action getEventAction() {
      return this.eventAction;
   }

   public boolean isSend() {
      return this.getEventAction() == EventPacket.Action.SEND;
   }

   public boolean isReceive() {
      return this.getEventAction() == EventPacket.Action.RECEIVE;
   }

   public static enum Action {
      SEND,
      RECEIVE;
   }
}
