package club.pulsive.impl.event.player;

import club.pulsive.api.event.Event;
import club.pulsive.api.event.eventBus.handler.EventHandler;
import club.pulsive.api.event.eventBus.handler.Listener;
import club.pulsive.impl.event.network.PacketEvent;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.play.server.S30PacketWindowItems;

import static club.pulsive.api.minecraft.MinecraftUtil.mc;

@Getter
@Setter
public final class PlayerSlowEvent extends Event {
   
}