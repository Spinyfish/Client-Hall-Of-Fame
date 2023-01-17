package com.alan.clients.component.impl.event;

import com.alan.clients.api.Rise;
import com.alan.clients.component.Component;
import com.alan.clients.Client;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.Priorities;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PreMotionEvent;
import com.alan.clients.newevent.impl.other.AttackEvent;
import com.alan.clients.newevent.impl.other.KillEvent;
import com.alan.clients.newevent.impl.other.WorldChangeEvent;
import net.minecraft.entity.Entity;

@Rise
//@Priority(priority = -100) /* Must be run before all modules */
public class EntityKillEventComponent extends Component {

    Entity target = null;

    @EventLink(value = Priorities.LOW)
    public final Listener<PreMotionEvent> onPreMotionEvent = event -> {
        threadPool.execute(() -> {
            if (target != null && !mc.theWorld.loadedEntityList.contains(target)) {
                target = null;
                Client.INSTANCE.getEventBus().handle(new KillEvent(target));
            }
        });
    };

    @EventLink(value = Priorities.LOW)
    public final Listener<AttackEvent> onAttackEvent = event -> {
        target = event.getTarget();
    };

    @EventLink(value = Priorities.LOW)
    public final Listener<WorldChangeEvent> onWorldChange = event -> {
        target = null;
    };
}
