package store.femboy.spring.impl.module.impl.visual;

import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import store.femboy.spring.Spring;
import store.femboy.spring.impl.event.impl.Event2D;
import store.femboy.spring.impl.module.Category;
import store.femboy.spring.impl.module.Module;
import store.femboy.spring.impl.module.ModuleManager;
import store.femboy.spring.impl.module.settings.impl.ModeSetting;
import store.femboy.spring.impl.notifications.NotificationManager;
import store.femboy.spring.impl.util.ColorUtils;
import store.femboy.spring.impl.util.FontUtil;
import store.femboy.spring.impl.util.RenderUtil;

import java.awt.*;
import java.util.Comparator;

public final class HUD extends Module {

    ModeSetting watermark = new ModeSetting("Watermark", "Text", "Text", "Logo", "ConleyWare");

    public HUD() {
        super("HUD", "thing on screen", Keyboard.KEY_NONE, Category.VISUAL);
        this.setToggled(true);
        addSettings(watermark);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @EventHandler()
    public final Listener<Event2D> overlayListener = e -> {

        switch (watermark.getMode()) {
            case "Text":
                RenderUtil.drawRect(0,0, FontUtil.normal.getStringWidth("Spring " + Spring.INSTANCE.version + " - " + Spring.INSTANCE.edition) + 6, FontUtil.normal.getHeight() + 6, new Color(25,25,25).getRGB());
                RenderUtil.drawRect(0,FontUtil.normal.getHeight() + 4, FontUtil.normal.getStringWidth("Spring " + Spring.INSTANCE.version + " - " + Spring.INSTANCE.edition) + 6, 3, ColorUtils.getRainbow(5, 0.8f, 1f));
                FontUtil.normal.drawString("Spring " + Spring.INSTANCE.version + " - " + Spring.INSTANCE.edition, 3, 3, -1);
                break;
            case "Logo":
                RenderUtil.drawImage(new ResourceLocation("store/femboy/spring/logo.png"), -10, -40, 150, 150);
                break;
            case "ConleyWare":
                RenderUtil.drawImage(new ResourceLocation("store/femboy/spring/conleyware.png"), 0, 0, 300, 150);
                break;
        }

        FontUtil.normal.drawStringWithShadow("FPS: " + mc.getDebugFPS(), 5, e.getSr().getScaledHeight() -12, -1);

        //ping
        FontUtil.normal.drawStringWithShadow("Ping: " + mc.getNetHandler().getPlayerInfo(mc.thePlayer.getGameProfile().getId()).getResponseTime(), 5, e.getSr().getScaledHeight() -22, -1);

        //ArrayList
        ModuleManager mods = Spring.INSTANCE.getManager();
        mods.getModules().sort(Comparator.comparingInt(m -> (int) FontUtil.normal.getStringWidth(((Module)m).name)).reversed());
        int i = 0;
        for(Module m : mods.getModules()){
            if(m.isToggled() && !m.getName().equals("HUD") && !m.getName().equals("ClickGui")){
                RenderUtil.drawRoundedRect(e.getSr().getScaledWidth() - (int) FontUtil.normal.getStringWidth(m.getName()) - 10, i * 14, FontUtil.normal.getStringWidth(m.getName()) + 15, FontUtil.normal.getHeight() + 4, 5, new Color(0, 0, 0, 150).getRGB());
                RenderUtil.drawRoundedRect(e.getSr().getScaledWidth() - (int) FontUtil.normal.getStringWidth(m.getName()) - 8, 1.6 + i * 14, 2, 10, 2, ColorUtils.getRainbow(3f, 0.8f, 1f, i * (-100)));
                FontUtil.normal.drawString(m.getName(), e.getSr().getScaledWidth() - (int) FontUtil.normal.getStringWidth(m.getName()) - 3.5, 3+i * 14, ColorUtils.getRainbow(3f, 0.5f, 1f, i * (-100)));
                i++;
            }
        }

        NotificationManager.drawScreen(e.getSr());

    };
}


