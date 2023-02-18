/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  vip.astroline.client.service.module.Category
 *  vip.astroline.client.service.module.Module
 *  vip.astroline.client.service.module.impl.render.Hud
 */
package vip.astroline.client.service.module.impl.render;

import java.awt.Color;
import vip.astroline.client.service.module.Category;
import vip.astroline.client.service.module.Module;
import vip.astroline.client.service.module.impl.render.Hud;

public class ChestESP
extends Module {
    public ChestESP() {
        super("ChestESP", Category.Render, 0, false);
    }

    public static float[] getColorForTileEntity() {
        Color color = Hud.hudColor1.getValue();
        return new float[]{color.getRed(), color.getGreen(), color.getBlue(), 200.0f};
    }
}
