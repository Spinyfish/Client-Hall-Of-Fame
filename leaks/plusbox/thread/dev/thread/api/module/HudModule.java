package dev.thread.api.module;

import dev.thread.api.event.bus.annotation.Subscribe;
import dev.thread.impl.event.Render2DEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HudModule extends Module {
    private double x, y, width, height;
    public HudModule(String name, String description, double x, double y) {
        super(name, description, ModuleCategory.RENDER);
        this.x = x;
        this.y = y;
    }

    protected void render() {

    }
    
    @Subscribe
    private void onRender2D(Render2DEvent render2DEvent) {
        System.out.println("hai");
        render();
    }
}
