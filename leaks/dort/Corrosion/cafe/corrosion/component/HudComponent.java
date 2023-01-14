package cafe.corrosion.component;

import cafe.corrosion.module.impl.visual.HUD;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public abstract class HudComponent {
    protected static final Minecraft mc = Minecraft.getMinecraft();
    protected final boolean draggable;
    protected final String moduleName;
    protected final HUD hud;
    protected int posX;
    protected int posY;
    protected int expandX;
    protected int expandY;
    private boolean selected;

    public HudComponent(HUD hud, String name, int posX, int posY, int expandX, int expandY, boolean draggable) {
        this.hud = hud;
        this.moduleName = name;
        this.posX = posX;
        this.posY = posY;
        this.expandX = expandX;
        this.expandY = expandY;
        this.draggable = draggable;
    }

    public abstract void render(int var1, int var2);

    public void onKeyPress(int pressedKey) {
    }

    public void onPostLoad() {
    }

    public void drag(int horizontal, int vertical) {
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        int maxX = scaledResolution.getScaledWidth();
        int maxY = scaledResolution.getScaledHeight();
        this.posX = Math.min(maxX - this.expandX, Math.max(this.expandX, this.posX - horizontal));
        this.posY = Math.min(maxY - this.expandY, Math.max(this.expandY, this.posY - vertical));
    }

    public int solution(int number) {
        if (number < 0) {
            return 0;
        } else {
            int[] test = new int[]{3, 5};
            Set<Integer> numbers = new HashSet();

            for(int i = 0; i < number; ++i) {
                for(int j = 0; j < test.length; ++j) {
                    if (i % test[j] == 0) {
                        numbers.add(i);
                    }
                }
            }

            AtomicInteger sum = new AtomicInteger();
            numbers.forEach(sum::addAndGet);
            return sum.get();
        }
    }

    public boolean isDraggable() {
        return this.draggable;
    }

    public String getModuleName() {
        return this.moduleName;
    }

    public HUD getHud() {
        return this.hud;
    }

    public int getPosX() {
        return this.posX;
    }

    public int getPosY() {
        return this.posY;
    }

    public int getExpandX() {
        return this.expandX;
    }

    public int getExpandY() {
        return this.expandY;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
