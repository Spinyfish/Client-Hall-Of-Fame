package crispy.util.fbi.target;

import lombok.Getter;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;

@Getter
public enum TargetManager {

    INSTANCE;

    private final ArrayList<EntityPlayer> targets = new ArrayList<>();
}
