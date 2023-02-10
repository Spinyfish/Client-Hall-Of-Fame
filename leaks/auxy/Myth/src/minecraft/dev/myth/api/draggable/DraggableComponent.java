/**
 * @project Myth
 * @author CodeMan
 * @at 05.02.23, 17:04
 */
package dev.myth.api.draggable;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.ScaledResolution;

public class DraggableComponent {

    @Getter @Setter
    private double x, y, width, height;

    @Getter @Setter
    private String name;

    public DraggableComponent(String name, double x, double y, double width, double height, ScaledResolution sr) {
        this.name = name;
        this.x = x / sr.getScaledWidth();
        this.y = y / sr.getScaledHeight();
        this.width = width;
        this.height = height;
    }

    public double getX(ScaledResolution sr) {
        return x * sr.getScaledWidth();
    }

    public double getY(ScaledResolution sr) {
        return y * sr.getScaledHeight();
    }

    public boolean isHovered(double mouseX, double mouseY, ScaledResolution sr) {
        return mouseX >= getX(sr) && mouseX <= getX(sr) + width && mouseY >= getY(sr) && mouseY <= getY(sr) + height;
    }

}
