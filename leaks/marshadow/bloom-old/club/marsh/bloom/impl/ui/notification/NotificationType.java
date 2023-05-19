package club.marsh.bloom.impl.ui.notification;

import lombok.RequiredArgsConstructor;

import java.awt.*;


public enum NotificationType {
    WARNING(new Color(117,218,232)),
    ERROR(new Color(224,120,204,255)),
    INFO(new Color(135,134,237,255));

    Color color;
    NotificationType(Color color) {
        this.color = color;
    }
}
