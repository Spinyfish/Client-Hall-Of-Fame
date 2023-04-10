package crispy.util.fbi.data;


import crispy.util.fbi.check.Check;
import crispy.util.fbi.check.impl.badpackets.BadPackets;
import crispy.util.fbi.check.impl.movement.SpeedA;
import lombok.Getter;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;


@Getter
public class Data {

    private final EntityPlayer player;
    private final ArrayList<Check> checks = new ArrayList<>();

    public Data(EntityPlayer player) {
        this.player = player;
        checks.add(new BadPackets());
        checks.add(new SpeedA());

    }
}
