package dev.elysium.client.ui.widgets;

import dev.elysium.client.ui.widgets.impl.Keystrokes;

import java.util.ArrayList;
import java.util.List;

public class WidgetManager {
    public List<Widget> widgets = new ArrayList<>();

    public WidgetManager() {
        widgets.add(new Keystrokes());
    }

    public void drawAllWidgets() {
        for(Widget w : widgets) {
            w.draw();
        }
    }

    public Widget getHoveringOver(int mx, int my) {
        for(Widget w : widgets) {
            if(mx > w.x && mx < w.x + w.width) {
                if(my > w.y && my < w.y + w.height) {
                    return w;
                }
            }
        }
        return null;
    }

    public Widget getDragging() {
        for(Widget w : widgets) {
            if(w.dragging) return w;
        }
        return null;
    }

    public Widget byName(String name) {
        for(Widget e : widgets)
            if(e.name.equalsIgnoreCase(name))
                return e;
        return null;
    }
}
