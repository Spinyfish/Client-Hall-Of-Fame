package crispy.util.fbi.data;

import lombok.Getter;
import net.minecraft.entity.player.EntityPlayer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public enum DataManager {

    INSTANCE;

    private final Map<EntityPlayer, Data> dataMap = new ConcurrentHashMap<>();


    public Data getData(EntityPlayer player) {
        return this.dataMap.computeIfAbsent(player, uuid -> new Data(player));

    }
}
