package club.marsh.bloom.impl.ui.hud.impl;

import static club.marsh.bloom.impl.mods.render.Hud.rgb;

import java.awt.Color;
import java.util.Comparator;
import java.util.concurrent.CopyOnWriteArrayList;

import org.lwjgl.opengl.GL11;

import club.marsh.bloom.Bloom;
import club.marsh.bloom.api.module.Category;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.impl.ui.hud.Component;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

public class ArraylistComponent extends Component {
    public ArraylistComponent() {
        super("Arraylist",0, ScaledResolution.getScaledHeight()/2, true);
        addValue("Bloom",false);
        addValue("Hide Render Modules", false);
    }
    @Override
    public void render() {
        FontRenderer fontRenderer = getMc().fontRendererObj;
        final int[] y = {0};
        CopyOnWriteArrayList<Module> modules = (CopyOnWriteArrayList<Module>) Bloom.INSTANCE.moduleManager.getModules().clone();
        modules.removeIf((module) -> !module.toggled || getValue("Hide Render Modules") && module.category == Category.VISUAL);

        if (getY() < ScaledResolution.getScaledHeight()/2)
            modules.sort(Comparator.comparingDouble(module -> fontRenderer.getStringWidth(((Module) module).getDisplayName().replace("\247", "").replace("7", "").toLowerCase())).reversed());
        else
            modules.sort(Comparator.comparingDouble(module -> fontRenderer.getStringWidth(((Module) module).getDisplayName().replace("\247", "").replace("7", "").toLowerCase())));
        modules.forEach(module -> {
            int textX = super.getX() < ScaledResolution.getScaledWidth()/2 ? super.getX() : super.getX()-fontRenderer.getStringWidth(module.getDisplayName().toLowerCase());
            if (getValue("Bloom")) {
                //bloom clarinet bloom is funny
            	Bloom.INSTANCE.bloomHandler.bloom(textX - 5, getY()+y[0]-2, fontRenderer.getStringWidth(module.getDisplayName()) + 10, 12, 10, new Color(0, 0, 0, 75));
            	GlStateManager.resetColor();
            }
            renderText(module.getDisplayName(), textX, getY()+y[0], y[0]/10);

            y[0] += 10;
        });
        if (getY() < ScaledResolution.getScaledHeight()/2)
            setWidth(super.getX() < ScaledResolution.getScaledWidth()/2 ? (modules.size() == 0 ? 0 : fontRenderer.getStringWidth(modules.get(0).getDisplayName())) : -(modules.size() == 0 ? 0 : fontRenderer.getStringWidth(modules.get(0).getDisplayName())));
        else
            setWidth(super.getX() < ScaledResolution.getScaledWidth()/2 ? (modules.size() == 0 ? 0 : fontRenderer.getStringWidth(modules.get(modules.size()-1).getDisplayName())) : -(modules.size() == 0 ? 0 : fontRenderer.getStringWidth(modules.get(modules.size()-1).getDisplayName())));
        setHeight(modules.size()*10);
    }

    public int renderText(String text, double x,double y,int thing) {
        int ind = 0;
        boolean hasChangedColor = false;
        char[] t = text.toCharArray();
        for (int i = 0; i <= text.length()-1; ++i) {
            if (String.valueOf(t[i]).contains("\247"))
                hasChangedColor = true;
            if (hasChangedColor)
                break;
            getMc().fontRendererObj.drawStringWithShadow(String.valueOf(t[i]), (int) (x+ind),(int) (y), rgb(thing).getRGB());
            ind += getMc().fontRendererObj.getCharWidth(t[i]);
            thing += 1;
        }
        if (hasChangedColor) {
            getMc().fontRendererObj.drawStringWithShadow("\247" + String.valueOf(text.split("\247")[1]), (int) (x+ind),(int) (y), rgb(thing).getRGB());
            ind += getMc().fontRendererObj.getStringWidth(text.split("\247")[1]);
        }
        return thing;
    }

    public void drawScissorBox(double x, double y, double width, double height) {
        width = Math.max(width, 0.1);

        ScaledResolution sr = new ScaledResolution(getMc());
        double scale = sr.getScaleFactor();

        y = sr.getScaledHeight() - y;

        x *= scale;
        y *= scale;
        width *= scale;
        height *= scale;

        GL11.glScissor((int) x, (int) (y - height), (int) width, (int) height);
    }

    @Override
    protected boolean isHolding(int mouseX, int mouseY) {
        return getWidth() > 0 ?
                mouseX >= super.getX() && mouseY >= getY() && mouseX < super.getX()+getWidth() && mouseY < getY()+getHeight()
                :
                mouseX >= super.getX()+getWidth() && mouseY >= getY() && mouseX < super.getX() && mouseY < getY()+getHeight();
    }
}
