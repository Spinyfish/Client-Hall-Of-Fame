package me.jinthium.straight.impl.manager;

import me.jinthium.straight.api.component.Component;
import me.jinthium.straight.impl.Client;
import me.jinthium.straight.impl.components.*;

import java.util.ArrayList;
import java.util.Collections;

public final class ComponentManager extends ArrayList<Component> {

    /**
     * Called on client start and when for some reason when we reinitialize
     */
    public void init() {
        addComponent(
                new BadPacketsComponent(),
                new EntityTickComponent(),
                new FallDistanceComponent(),
                new BlinkComponent(),
                new PingSpoofComponent(),
                new ItemDamageComponent(),
                new PacketlessDamageComponent(),
                new ProjectionComponent()
        );
        this.registerToEventBus();
    }

    public void addComponent(Component... component) {
        Collections.addAll(this, component);
    }

    public void registerToEventBus() {
        for (final Component component : this) {
            Client.INSTANCE.getPubSubEventBus().subscribe(component);
        }
    }
}