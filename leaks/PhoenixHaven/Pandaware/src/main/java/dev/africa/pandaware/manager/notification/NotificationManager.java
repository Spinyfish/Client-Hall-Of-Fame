package dev.africa.pandaware.manager.notification;

import dev.africa.pandaware.impl.container.Container;
import dev.africa.pandaware.impl.ui.notification.Notification;
import dev.africa.pandaware.utils.render.animator.Easing;
import lombok.Getter;
import net.minecraft.client.gui.ScaledResolution;

@Getter
public class NotificationManager extends Container<Notification> {
    public void addNotification(Notification.Type type, String text, double duration) {
        this.getItems().add(new Notification(type, text, duration));
    }

    public void addNotification(Notification.Type type, String title, String text, double duration) {
        this.getItems().add(new Notification(type, title, text, duration));
    }

    public void renderNotifications(ScaledResolution scaledResolution) {
        if (!this.getItems().isEmpty()) {
            int yPosition = -30;

            for (Notification notification : this.getItems()) {
                notification.getYAnimator()
//                        .setEase(Easing.LINEAR)
                        .setEase(Easing.CIRC_IN)
                        .setMin(0).setMax(1)
                        .setReversed(notification.isShouldAnimateBack())
//                                notification.isShouldAnimateBack() && notification.getRectPosition()
//                                        <= scaledResolution.getScaledWidth() - 3)
                        .setSpeed(3f).update();

                notification.render(scaledResolution, yPosition, this);

                yPosition -= 25 * notification.getYAnimator().getValue();
            }
        }
    }
}
