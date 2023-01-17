package com.alan.clients.component;

import com.alan.clients.Client;
import org.atteo.classindex.ClassIndex;

import java.lang.reflect.Modifier;
import java.util.ArrayList;

/**
 * @author Alan
 * @since 01/27/2022
 */
public final class ComponentManager extends ArrayList<Component> {

    /**
     * Called on client start and when for some reason when we reinitialize
     */
    public void init() {

        // below every single component is added
//        ClassIndex.getSubclasses(Component.class, Component.class.getClassLoader()).forEach(clazz -> {
//            try {
//                if (!Modifier.isAbstract(clazz.getModifiers())) {
//                    this.add(clazz.newInstance());
//                }
//            } catch (final Exception e) {
//                e.printStackTrace();
//            }
//        });

        //Registers all components to the eventbus
        this.registerToEventBus();
    }

    public void registerToEventBus() {
        for (final Component component : this) {
            Client.INSTANCE.getEventBus().register(component);
        }
    }
}