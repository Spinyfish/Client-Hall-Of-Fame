package club.pulsive.impl.event.player;

import club.pulsive.api.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

@Getter@Setter@AllArgsConstructor
public class AttackEvent extends Event {
    private Entity target;
}
