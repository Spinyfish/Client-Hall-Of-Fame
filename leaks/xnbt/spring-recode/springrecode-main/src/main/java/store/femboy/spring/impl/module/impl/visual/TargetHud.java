package store.femboy.spring.impl.module.impl.visual;

import best.azura.eventbus.core.EventPriority;
import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import org.lwjgl.input.Keyboard;
import store.femboy.spring.Spring;
import store.femboy.spring.impl.event.impl.Event2D;
import store.femboy.spring.impl.module.Category;
import store.femboy.spring.impl.module.Module;
import store.femboy.spring.impl.module.impl.combat.KillAura;
import store.femboy.spring.impl.util.FontUtil;
import store.femboy.spring.impl.util.RenderUtil;

import java.awt.*;

public final class TargetHud extends Module {

    public TargetHud() {
        super("TargetHud", "show health", Keyboard.KEY_NONE, Category.VISUAL);
    }

    @EventHandler(EventPriority.LOWEST)
    public final Listener<Event2D> overlayListener = e ->{

        double fart = KillAura.getTarget().getMaxHealth() / KillAura.getTarget().getHealth();
        if(KillAura.getTarget().isEntityAlive() && Spring.INSTANCE.getManager().getModule("KillAura").toggled) {
            RenderUtil.drawRoundedRect(e.getSr().getScaledWidth() - 300, e.getSr().getScaledHeight() - 100, 200, 30, 10, new Color(0, 0, 0, 150).getRGB());
            RenderUtil.drawRoundedRect(e.getSr().getScaledWidth() - 295, e.getSr().getScaledHeight() - 95, 190, 5, 5,new Color(0, 0, 0, 200).getRGB());
            RenderUtil.drawRoundedRect(e.getSr().getScaledWidth() - 295, e.getSr().getScaledHeight() - 95, 190 / fart, 5, 5,-1);
            FontUtil.normal.drawStringWithShadow(KillAura.getTarget().getName(), e.getSr().getScaledWidth() - 295, e.getSr().getScaledHeight() - 85, -1);
            FontUtil.normal.drawStringWithShadow((short) Math.round(KillAura.getTarget().getHealth()) + " / " + (short) KillAura.getTarget().getMaxHealth(), e.getSr().getScaledWidth() - 290 + FontUtil.normal.getStringWidth(KillAura.getTarget().getName()), e.getSr().getScaledHeight() - 85, -1);
        }

    };



}
