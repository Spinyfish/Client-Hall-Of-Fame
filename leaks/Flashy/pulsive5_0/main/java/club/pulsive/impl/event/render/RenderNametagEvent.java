package club.pulsive.impl.event.render;

import club.pulsive.api.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.entity.EntityLivingBase;

@Getter
@AllArgsConstructor
public class RenderNametagEvent extends Event {

    private final EntityLivingBase entity;

}