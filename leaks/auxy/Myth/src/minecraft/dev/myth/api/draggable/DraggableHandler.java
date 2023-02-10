/**
 * @project Myth
 * @author CodeMan
 * @at 05.02.23, 17:15
 */
package dev.myth.api.draggable;

import dev.myth.api.interfaces.IMethods;
import dev.myth.api.utils.render.RenderUtil;
import dev.myth.main.ClientMain;
import dev.myth.managers.DraggableManager;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;
import java.util.ArrayList;

public class DraggableHandler implements IMethods {

    private final DraggableManager draggableManager;
    private DraggableComponent dragging;
    private double clickedX, clickedY;

    public DraggableHandler() {
        draggableManager = ClientMain.INSTANCE.manager.getManager(DraggableManager.class);
    }

    public void drawScreen(double mouseX, double mouseY) {
        ScaledResolution sr = new ScaledResolution(MC);
        for (DraggableComponent draggable : draggableManager.getDraggables()) {

//            draggableManager.save(draggable);
            draggableManager.load(draggable);

            if (dragging != null && dragging.equals(draggable)) {
                draggable.setX((mouseX - clickedX) / sr.getScaledWidth());
                draggable.setY((mouseY - clickedY) / sr.getScaledHeight());

                if(draggable.getX(sr) >= sr.getScaledWidth() - draggable.getWidth() - 3) {
                    draggable.setX((sr.getScaledWidth() - draggable.getWidth()) / sr.getScaledWidth());

                    RenderUtil.drawRect(draggable.getX(sr) + draggable.getWidth() - 1, 0, 1, sr.getScaledHeight(), -1);
                }

                if(draggable.getX(sr) <= 3) {
                    draggable.setX(0);

                    RenderUtil.drawRect(draggable.getX(sr), 0, 1, sr.getScaledHeight(), -1);
                }

                if(draggable.getY(sr) >= sr.getScaledHeight() - draggable.getHeight() - 3) {
                    draggable.setY((sr.getScaledHeight() - draggable.getHeight()) / sr.getScaledHeight());

                    RenderUtil.drawRect(0, draggable.getY(sr) + draggable.getHeight() - 1, sr.getScaledWidth(), 1, -1);
                }

                if(draggable.getY(sr) <= 3) {
                    draggable.setY(0);

                    RenderUtil.drawRect(0, draggable.getY(sr), sr.getScaledWidth(), 1, -1);
                }

                if(Math.abs(draggable.getX(sr) - sr.getScaledWidth() / 2D) <= 3) {
                    draggable.setX((sr.getScaledWidth() / 2D) / sr.getScaledWidth());

                    RenderUtil.drawRect(draggable.getX(sr), 0, 1, sr.getScaledHeight(), -1);
                }

                if(Math.abs(draggable.getY(sr) - sr.getScaledHeight() / 2D) <= 3) {
                    draggable.setY((sr.getScaledHeight() / 2D) / sr.getScaledHeight());

                    RenderUtil.drawRect(0, draggable.getY(sr), sr.getScaledWidth(), 1, -1);
                }

                if(Math.abs(draggable.getX(sr) + (draggable.getWidth() - sr.getScaledWidth()) / 2D) <= 3) {
                    draggable.setX(((sr.getScaledWidth() - draggable.getWidth()) / 2D) / sr.getScaledWidth());

                    RenderUtil.drawRect(draggable.getX(sr) + draggable.getWidth() / 2, 0, 1, sr.getScaledHeight(), -1);
                }

                if(Math.abs(draggable.getY(sr) + (draggable.getHeight() - sr.getScaledHeight()) / 2D) <= 3) {
                    draggable.setY(((sr.getScaledHeight() - draggable.getHeight()) / 2D) / sr.getScaledHeight());

                    RenderUtil.drawRect(0, draggable.getY(sr) + draggable.getHeight() / 2, sr.getScaledWidth(), 1, -1);
                }

                if(Math.abs(draggable.getX(sr) + draggable.getWidth() - sr.getScaledWidth() / 2D) <= 3) {
                    draggable.setX(((sr.getScaledWidth() / 2D) - draggable.getWidth()) / sr.getScaledWidth());

                    RenderUtil.drawRect(draggable.getX(sr) + draggable.getWidth() - 1, 0, 1, sr.getScaledHeight(), -1);
                }

                if(Math.abs(draggable.getY(sr) + draggable.getHeight() - sr.getScaledHeight() / 2D) <= 3) {
                    draggable.setY(((sr.getScaledHeight() / 2D) - draggable.getHeight()) / sr.getScaledHeight());

                    RenderUtil.drawRect(0, draggable.getY(sr) + draggable.getHeight() - 1, sr.getScaledWidth(), 1, -1);
                }

                draggableManager.save(draggable);
            }

            double x = draggable.getX(sr);
            double y = draggable.getY(sr);
            
            if (draggable.isHovered(mouseX, mouseY, sr)) {
                RenderUtil.drawRect(x, y, 1, draggable.getHeight(), -1);
                RenderUtil.drawRect(x, y, draggable.getWidth(), 1, -1);
                RenderUtil.drawRect(x + draggable.getWidth() - 1, y, 1, draggable.getHeight(), -1);
                RenderUtil.drawRect(x, y + draggable.getHeight() - 1, draggable.getWidth(), 1, -1);

                MC.fontRendererObj.drawStringWithShadow(draggable.getName(), (float) x + 3, (float) y + 3, -1);
            }
        }
    }

    public void mouseClicked(double mouseX, double mouseY, int mouseButton) {
        ScaledResolution sr = new ScaledResolution(MC);

        ArrayList<DraggableComponent> draggables = new ArrayList<>();
        for (int i = draggableManager.getDraggables().size() - 1; i >= 0; i--) {
            draggables.add(draggableManager.getDraggables().get(i));
        }

//        doLog(draggables.size() + " draggables");
//        doLog(draggableManager.getDraggables().size() + " draggables");

        if (mouseButton == 0) {
//            doLog("Mouse clicked");
            for (DraggableComponent component : draggables) {
                if (component.isHovered(mouseX, mouseY, sr)) {
//                    doLog(component.getName() + " is clicked");
                    dragging = component;
                    clickedX = mouseX - component.getX(sr);
                    clickedY = mouseY - component.getY(sr);
                    break;
                }
            }
        }

    }

    public void mouseReleased(double mouseX, double mouseY) {
        dragging = null;
    }

    public void onGuiClosed() {
        dragging = null;
    }

}
