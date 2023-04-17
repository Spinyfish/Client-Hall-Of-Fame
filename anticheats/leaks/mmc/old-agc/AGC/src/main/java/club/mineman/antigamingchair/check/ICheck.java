package club.mineman.antigamingchair.check;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public interface ICheck<T> {
    void handleCheck(final Player p0, final T p1);

    Class getType();
}
