package dev.africa.pandaware.impl.ui.circle;

import dev.africa.pandaware.impl.container.Container;
import dev.africa.pandaware.utils.render.RenderUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.MathHelper;

import java.awt.*;

@Getter
public class ClickCircle extends Container<ClickCircle.Circle> {

    public void render() {
        this.getItems().removeIf(circle -> circle.getAlpha() <= 0);

        this.getItems().forEach(Circle::render);
    }

    public void addCircle(double x, double y, double startRadius, double maxRadius, double speed, Color color) {
        this.getItems().add(new Circle(x, y, startRadius, maxRadius, speed, color));
    }

    @RequiredArgsConstructor
    @Getter
    protected static class Circle {
        private final double x;
        private final double y;
        private final double startRadius;
        private final double maxRadius;
        private final double speed;
        private final Color color;

        private double radius;
        private int alpha = 255;

        public void render() {
            this.radius += this.speed * RenderUtils.fpsMultiplier();
            this.radius = MathHelper.clamp_double(this.radius, this.startRadius, this.maxRadius);

            if (this.radius >= (this.maxRadius / 2)) {
                this.alpha -= (25 * RenderUtils.fpsMultiplier());
                this.alpha = MathHelper.clamp_int(this.alpha, 0, 255);
            }

            RenderUtils.drawCircleOutline(
                    this.x - (this.radius / 2f), this.y - (this.radius / 2f),
                    this.radius, this.radius, 3,
                    new Color(this.color.getRed(), this.color.getGreen(), this.color.getBlue(), this.alpha)
            );
        }
    }
}
