package club.marsh.bloom.impl.ui.hud;

import club.marsh.bloom.Bloom;
import club.marsh.bloom.impl.events.Render2DEvent;
import club.marsh.bloom.impl.mods.render.Hud;
import club.marsh.bloom.impl.ui.hud.impl.*;
import com.google.common.eventbus.Subscribe;
import net.minecraft.client.gui.GuiScreen;

import java.util.ArrayList;

public class HudDesigner {
    public static ArrayList<Component> components = new ArrayList<>();

    public HudDesigner() {
        Bloom.INSTANCE.eventBus.register(this);
        components.add(new WatermarkComponent());
        components.add(new ArraylistComponent());
        components.add(new BalanceComponent());
        components.add(new TargetHudComponent());
        components.add(new KeystrokesComponent());
    }

    //@Subscribe
    //    //public void onRender2D(Render2DEvent e) {
    //    //    components.stream().filter(component -> component.isEnabled() || Bloom.INSTANCE.hudDesignerUI.open).forEach(component -> {component.render();});
    //}
}
