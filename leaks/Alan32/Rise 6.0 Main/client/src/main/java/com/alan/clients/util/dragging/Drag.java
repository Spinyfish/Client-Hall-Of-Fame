package com.alan.clients.util.dragging;

import com.alan.clients.util.gui.GUIUtil;
import com.alan.clients.util.vector.Vector2d;
import util.time.StopWatch;

public class Drag {
    public Vector2d position, targetPosition, scale, offset = new Vector2d(0, 0);
    public boolean dragging = false;
    public StopWatch stopWatch = new StopWatch();

    public Drag(Vector2d position, Vector2d scale) {
        this.position = targetPosition = position;
        this.scale = scale;
    }

    public void onClick(final int mouseButton) {
        Vector2d mouse = Mouse.getMouse();

        if (GUIUtil.mouseOver(position, scale, mouse.x, mouse.y) && mouseButton == 0) {
            dragging = true;

            offset.x = targetPosition.x - mouse.x;
            offset.y = targetPosition.y - mouse.y;
        }
    }

    public void render() {
        Vector2d mouse = Mouse.getMouse();

        if (dragging) {
            if (targetPosition == null) targetPosition = new Vector2d(0, 0);
            targetPosition.x = mouse.x + offset.x;
            targetPosition.y = mouse.y + offset.y;
        }

        if (targetPosition == null) {
            return;
        }

        if (Math.abs(position.x - targetPosition.x) > 1 ||
                Math.abs(position.y - targetPosition.y) > 1) {

            for (int i = 0; i <= stopWatch.getElapsedTime(); ++i) {
                position.x = (position.x * 38 + targetPosition.x) / 39;
                position.y = (position.y * 38 + targetPosition.y) / 39;
            }
        }

        stopWatch.reset();
    }

    public void release() {
        dragging = false;
    }
}
