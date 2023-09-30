package club.pulsive.impl.event.player;

import club.pulsive.api.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.EntityLivingBase;

@Getter
@Setter
@AllArgsConstructor
public class PlayerJumpEvent extends Event {
    private EntityLivingBase entity;
    private double motionY;
    private float yaw;
}
