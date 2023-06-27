package rip.athena.client.gui.framework;

import java.util.concurrent.*;
import rip.athena.client.gui.framework.components.*;
import java.util.*;

public class Menu
{
    private String title;
    private int x;
    private int y;
    private int width;
    private int height;
    private int mouseX;
    private int mouseY;
    private List<MenuComponent> components;
    
    public Menu(final String title, final int width, final int height) {
        this.title = title;
        this.x = 0;
        this.y = 0;
        this.width = width;
        this.height = height;
        this.components = new CopyOnWriteArrayList<MenuComponent>();
    }
    
    public void onRender(final int mouseX, final int mouseY) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        Collections.sort(this.components, (a, b) -> Integer.compare(a.getPriority().getPriority(), b.getPriority().getPriority()));
        Collections.reverse(this.components);
        int passThroughIndex = -1;
        int index = this.components.size();
        for (final MenuComponent component : this.components) {
            component.setRenderOffsetX(this.x);
            component.setRenderOffsetY(this.y);
            if (!component.passesThrough() && passThroughIndex == -1) {
                passThroughIndex = index;
            }
            --index;
        }
        Collections.reverse(this.components);
        final int oldIndex = index = index;
        for (final MenuComponent component2 : this.components) {
            if (index >= passThroughIndex - 1) {
                this.mouseX = mouseX;
                this.mouseY = mouseY;
            }
            else {
                if (component2 instanceof MenuDraggable) {
                    ++index;
                    continue;
                }
                this.mouseX = Integer.MAX_VALUE;
                this.mouseY = Integer.MAX_VALUE;
            }
            component2.onPreSort();
            component2.onRender();
            ++index;
        }
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }
    
    public void onMouseClick(final int button) {
        Collections.sort(this.components, (a, b) -> Integer.compare(a.getPriority().getPriority(), b.getPriority().getPriority()));
        Collections.reverse(this.components);
        boolean returnMode = false;
        for (final MenuComponent component : this.components) {
            if (!returnMode || component instanceof MenuScrollPane) {
                component.onMouseClick(button);
            }
            if (!component.passesThrough()) {
                returnMode = true;
            }
        }
    }
    
    public void onMouseClickMove(final int button) {
        Collections.sort(this.components, (a, b) -> Integer.compare(a.getPriority().getPriority(), b.getPriority().getPriority()));
        Collections.reverse(this.components);
        boolean returnMode = false;
        for (final MenuComponent component : this.components) {
            if (!returnMode || component instanceof MenuScrollPane) {
                component.onMouseClickMove(button);
            }
            if (!component.passesThrough()) {
                returnMode = true;
            }
        }
    }
    
    public void onKeyDown(final char character, final int key) {
        for (final MenuComponent component : this.components) {
            component.onKeyDown(character, key);
        }
    }
    
    public boolean onMenuExit(final int key) {
        boolean cancel = false;
        for (final MenuComponent component : this.components) {
            if (component.onExitGui(key)) {
                cancel = true;
            }
        }
        return cancel;
    }
    
    public void onScroll(final int scroll) {
        this.components.sort(Comparator.comparingInt(a -> a.getPriority().getPriority()));
        Collections.reverse(this.components);
        for (final MenuComponent component : this.components) {
            component.onMouseScroll(scroll);
            if (!component.passesThrough()) {
                break;
            }
        }
    }
    
    public void setLocation(final int x, final int y) {
        this.x = x;
        this.y = y;
    }
    
    public void addComponent(final MenuComponent component) {
        if (!this.components.contains(component)) {
            component.setParent(this);
            this.components.add(component);
        }
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public void setTitle(final String title) {
        this.title = title;
    }
    
    public int getX() {
        return this.x;
    }
    
    public void setX(final int x) {
        this.x = x;
    }
    
    public int getY() {
        return this.y;
    }
    
    public void setY(final int y) {
        this.y = y;
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public void setWidth(final int width) {
        this.width = width;
    }
    
    public int getHeight() {
        return this.height;
    }
    
    public void setHeight(final int height) {
        this.height = height;
    }
    
    public int getMouseX() {
        return this.mouseX;
    }
    
    public void setMouseX(final int mouseX) {
        this.mouseX = mouseX;
    }
    
    public int getMouseY() {
        return this.mouseY;
    }
    
    public void setMouseY(final int mouseY) {
        this.mouseY = mouseY;
    }
    
    public List<MenuComponent> getComponents() {
        return this.components;
    }
    
    public void setComponents(final List<MenuComponent> components) {
        this.components = components;
    }
}
