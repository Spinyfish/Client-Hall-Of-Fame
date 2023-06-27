package rip.athena.client.utils.render;

import rip.athena.client.utils.animations.simple.*;
import java.awt.*;

public class ClickEffect
{
    private float x;
    private float y;
    private SimpleAnimation animation;
    
    public ClickEffect(final float x, final float y) {
        this.animation = new SimpleAnimation(0.0f);
        this.x = x;
        this.y = y;
        this.animation.setValue(0.0f);
    }
    
    public void draw() {
        this.animation.setAnimation(100.0f, 12.0);
        final double radius = 8.0f * this.animation.getValue() / 100.0f;
        final int alpha = (int)(255.0f - 255.0f * this.animation.getValue() / 100.0f);
        final int color = new Color(255, 255, 255, alpha).getRGB();
    }
    
    public boolean canRemove() {
        return this.animation.getValue() > 99.0f;
    }
}
