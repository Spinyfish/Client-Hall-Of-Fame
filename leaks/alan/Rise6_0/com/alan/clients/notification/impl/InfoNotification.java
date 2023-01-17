package com.alan.clients.notification.impl;

import com.alan.clients.notification.Notification;
import com.alan.clients.util.render.Animator;
import com.alan.clients.util.render.RenderUtil;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;

public final class InfoNotification extends Notification {

    private final Animator animator = new Animator();
    private boolean end;

    public InfoNotification(final String title, final String content, final long delay) {
        super(title, content, delay);
    }

    @Override
    public void render(final int multiplierY) {
        final ScaledResolution resolution = new ScaledResolution(mc);

        this.end = ((this.getInit() + this.getLength()) - System.currentTimeMillis()) < 600;

        final int width = nunitoNormal.width(this.getContent()) + 6;
        final int height = 30;

        final int baseX = resolution.getScaledWidth() - 5 - width;
        final int baseY = resolution.getScaledHeight() - (height * (multiplierY + 1)) - (5 * multiplierY + 1) - 5;

        animator.setTargetX(this.end ? resolution.getScaledWidth() : baseX);
        animator.setTargetY(baseY);

        animator.setSmoothness(0.05F);
        animator.update();

        RenderUtil.rectangle(animator.getPosX(), animator.getPosY(), width, height, new Color(255, 255, 255, 10));
    }

    @Override
    public boolean isEnded() {
        return this.animator.finished() && this.end;
    }
}
