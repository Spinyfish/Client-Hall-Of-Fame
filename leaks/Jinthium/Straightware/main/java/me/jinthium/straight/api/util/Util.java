package me.jinthium.straight.api.util;

import me.jinthium.straight.impl.utils.font.CustomFont;
import me.jinthium.straight.impl.utils.font.FontUtil;

public interface Util {
    FontUtil.FontType normal = FontUtil.FontType.NORMAL;

    CustomFont normalFont17 = normal.size(17);
    CustomFont normalFont18 = normal.size(18);

    CustomFont normalFont16 = normal.size(16);
    CustomFont normalFont14 = normal.size(14);

    CustomFont normalFont19 = normal.size(19);

    CustomFont iconFont20 = FontUtil.FontType.ICON_FONT.size(64);
    CustomFont iconFont16 = FontUtil.FontType.ICON_FONT.size(16);
    CustomFont normalFont22 = normal.size(22);
    CustomFont normalFont20 = normal.size(20);
    CustomFont normalFont40 = normal.size(40);
}
