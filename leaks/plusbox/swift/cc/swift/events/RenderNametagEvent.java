package cc.swift.events;

import dev.codeman.eventbus.Event;
import dev.codeman.eventbus.Handler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.player.EntityPlayer;

@Getter
@Setter
@AllArgsConstructor
public class RenderNametagEvent extends Event {
    private EntityPlayer player;
}
