package club.marsh.bloom.impl.ui.hud;

import club.marsh.bloom.Bloom;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;

import java.util.HashMap;

@Getter
@Setter
public abstract class Component {
    private Minecraft mc = Minecraft.getMinecraft();
    private String name;
    private int x;
    private int y;
    private int width = 0;
    private int height = 0;
    private boolean enabled;
    HashMap<String, Boolean> values = new HashMap<>();
    public Component(String name, int x, int y, boolean enabled) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.enabled = enabled;
        Bloom.INSTANCE.eventBus.register(this);
    }

    public boolean getValue(String name) {
        return getValues().get(name);
    }

    public void addValue(String name, Boolean value) {
        values.put(name,value);
    }
    public abstract void render();
    protected abstract boolean isHolding(int mouseX, int mouseY);
}
