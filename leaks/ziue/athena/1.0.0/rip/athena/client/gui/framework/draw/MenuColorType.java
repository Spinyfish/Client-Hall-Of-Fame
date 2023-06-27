package rip.athena.client.gui.framework.draw;

import java.awt.*;
import java.util.*;

public class MenuColorType
{
    Map<String, Color> colors;
    
    public MenuColorType() {
        this.colors = new HashMap<String, Color>();
    }
    
    public int getColor(final DrawType type, final ButtonState state) {
        if (this.colors.containsKey(type.toString() + state.toString())) {
            return this.colors.get(type.toString() + state.toString()).getRGB();
        }
        return this.colors.get(type.toString()).getRGB();
    }
    
    public void setColor(final DrawType type, final ButtonState state, final Color color) {
        this.colors.put(type.toString() + state.toString(), color);
    }
}
