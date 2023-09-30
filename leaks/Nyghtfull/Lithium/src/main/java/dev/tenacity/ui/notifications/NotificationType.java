package dev.tenacity.ui.notifications;

import dev.tenacity.module.impl.render.HUDMod;
import dev.tenacity.utils.font.FontUtil;
import dev.tenacity.utils.render.ColorUtil;
import dev.tenacity.utils.tuples.Pair;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.awt.*;

@Getter
@AllArgsConstructor
public enum NotificationType {



    SUCCESS(new Color(20, 250, 90), FontUtil.CHECKMARK),
    DISABLE(new Color(255, 30, 30), FontUtil.XMARK),
    INFO(Color.WHITE, FontUtil.INFO),
    WARNING(Color.YELLOW, FontUtil.WARNING);
    private final Color color;
    private final String icon;
}