package com.alan.clients.util.gui;

import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.util.math.MathUtil;
import com.alan.clients.util.render.ColorUtil;
import com.alan.clients.util.render.RenderUtil;
import com.alan.clients.util.vector.Vector2d;
import lombok.Getter;
import lombok.Setter;
import org.lwjgl.input.Mouse;
import util.time.StopWatch;

import java.awt.*;

@Getter
@Setter
public class ScrollUtil implements InstanceAccess {

    public double target, scroll, max = 25;
    public StopWatch stopwatch = new StopWatch();
    public StopWatch stopWatch2 = new StopWatch();
    public boolean scrollingIsAllowed;

    public void onRender() {

        //Sets target scroll every tick, this way scrolling will only change if there's less than 1 frame per tick
        if (stopWatch2.finished(50)) {
            final float wheel = Mouse.getDWheel();
            double stretch = 30;
            target = Math.min(Math.max(target + wheel / 2, max - (wheel == 0 ? 0 : stretch)), (wheel == 0 ? 0 : stretch));

            stopWatch2.reset();
        }

        //Moving render scroll towards target
        for (int i = 0; i < stopwatch.getElapsedTime(); ++i) {
            scroll = MathUtil.lerp(scroll, target, 1E-2F);
        }

        //resetting stopwatch
        stopwatch.reset();
    }

    public void renderScrollBar(Vector2d position, double maxHeight) {
        double percentage = getScroll() / getMax();
        double scrollBarHeight = maxHeight - ((getMax() / (getMax() - maxHeight)) * maxHeight);

        scrollingIsAllowed = scrollBarHeight < maxHeight;
        if (!scrollingIsAllowed) return;

        double scrollX = position.x;
        double scrollY = position.y + maxHeight * percentage - scrollBarHeight * percentage;
        Color color = ColorUtil.withAlpha(Color.WHITE, 60);

        RenderUtil.drawRoundedGradientRect(scrollX, scrollY, 1, scrollBarHeight, 0.5,
                color, color, true);
    }

    public void reset() {
        this.scroll = 0;
        this.target = 0;
    }
}
