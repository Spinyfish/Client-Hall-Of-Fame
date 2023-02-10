/**
 * @project Myth
 * @author CodeMan
 * @at 25.09.22, 11:19
 */
package dev.myth.ui.clickgui;

import lombok.Getter;
import lombok.Setter;

import java.util.function.Supplier;

public class ChildComponent extends Component {

    @Getter private Component parent;
    @Getter @Setter private Supplier<Boolean> visible;

    public ChildComponent(Component parent, double x, double y, double width, double height) {
        this.parent = parent;
        setX(x);
        setY(y);
        setWidth(width);
        setHeight(height);
    }

    public double getFullX() {
        double x = getX();
        Component parent = getParent();
        while (parent != null) {
            if(parent.isVisible()){
                x += parent.getX();
            }
            if(parent instanceof ChildComponent) {
                parent = ((ChildComponent) parent).getParent();
            } else {
                parent = null;
            }
        }
        return x;
    }

    public double getFullY() {
        double y = getY();
        Component parent = getParent();
        while (parent != null) {
            if(parent.isVisible()){
                y += parent.getY();
            }
            if(parent instanceof ChildComponent) {
                parent = ((ChildComponent) parent).getParent();
            } else {
                parent = null;
            }
        }
        return y;
    }

    @Override
    public boolean isVisible() {
        if(visible == null) return true;
        return visible.get();
    }
}
