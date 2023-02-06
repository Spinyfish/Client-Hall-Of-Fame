package dev.elysium.client.ui.elements;

import dev.elysium.client.ui.elements.targethud.TargetInfo;
import dev.elysium.client.ui.elements.targethud.impl.*;
import net.minecraft.entity.EntityLivingBase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class HudManager {
    public static HudManager INSTANCE = new HudManager();
    public static HudManager getHudManager() { return INSTANCE; }

    public CopyOnWriteArrayList<Element> elements = new CopyOnWriteArrayList<Element>();

    public void addElements( List<Element> list) {
        list.add(new Elysium());
        list.add(new Exhibition());
        list.add(new Cross());
        list.add(new Astolfo());
        list.add(new Novoline());
    }

    public HudManager() {
        addElements(elements);
    }

    public Element getByname(String name) {
        for(Element e : elements)
            if(e.name.equalsIgnoreCase(name))
                return e;
        return null;
    }
}
