package com.alan.clients.ui.menu.component;

import com.alan.clients.ui.menu.MenuColors;
import com.alan.clients.util.interfaces.InstanceAccess;
import lombok.Getter;

@Getter
public class MenuComponent implements InstanceAccess, MenuColors {

    private final double x;
    private final double y;
    private final double width;
    private final double height;

    public MenuComponent(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
}
