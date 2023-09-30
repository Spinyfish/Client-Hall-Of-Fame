/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.impl.module.visual;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.impl.event.EventEnchantColour;

public class EnchantColour
extends Module {
    @EventLink
    private final Listener<EventEnchantColour> enchantColourListener = event -> event.setColour(ColorUtil.getAccent()[0]);

    public EnchantColour() {
        super("EnchantColor", "changes the enchant color wtf do you think", Category.VISUAL);
    }
}

