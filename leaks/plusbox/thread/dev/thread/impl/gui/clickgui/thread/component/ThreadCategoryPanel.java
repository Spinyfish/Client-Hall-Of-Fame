package dev.thread.impl.gui.clickgui.thread.component;

import dev.thread.api.module.Module;
import dev.thread.api.module.ModuleCategory;
import dev.thread.api.util.math.MathUtil;
import dev.thread.api.util.render.RenderUtil;
import dev.thread.api.util.render.component.Component;
import dev.thread.client.Thread;
import dev.thread.impl.gui.clickgui.thread.ThreadClickGUI;
import lombok.Getter;
import lombok.Setter;
import org.lwjglx.input.Keyboard;

import java.awt.*;

@Getter
public class ThreadCategoryPanel extends Component {
    private float x, y, width, height;
    private final ModuleCategory category;
    private String query = "";
    private boolean searching;
    @Setter
    private ThreadSettingsPanel settingsPanel;

    public ThreadCategoryPanel(Object parent, ModuleCategory category) {
        super(parent);
        this.category = category;
    }

    @Override
    public void render(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        RenderUtil.scissor(x, y, width, height);

        if (settingsPanel == null) {
            RenderUtil.drawRoundedRect(x + 19, y + 35, width - 38, 24, 3, new Color(255, 255, 255, 36));
            Thread.INSTANCE.getFontManager().get("icons").drawString(20, "j", x + 25.5F, y + 40.5F, new Color(255, 255, 255, 112));
            Thread.INSTANCE.getFontManager().get("poppins").drawString(16, !searching && query.isEmpty() ? "search" : query, x + 37.5F, y + 43, new Color(255, 255, 255, 112));

            float index = y + 68;
            for (Module m : Thread.INSTANCE.getModuleManager().values().stream().filter(module -> (query.isEmpty() ? module.getCategory() == category : module.getCategory() == category && module.getName().toLowerCase().contains(query))).toList()) {
                if (m.getCategory() == category) {
                    RenderUtil.drawRoundedRect(x + 19, index, width - 38, 52, 3, new Color(255, 255, 255, (int) (50 * ((ThreadClickGUI) getParent()).getSelectionAnimation().getFactor())));
                    if (m.isEnabled()) {
                        RenderUtil.drawRoundedRect(x + 19, index, width - 38, 52, 3, new Color(0, 133, 255, (int) (130 * ((ThreadClickGUI) getParent()).getSelectionAnimation().getFactor())));
                    }
                    Thread.INSTANCE.getFontManager().get("poppins-semibold").drawString(35, m.getName(), x + 26, index + 12, new Color(255, 255, 255, (int) (255 * ((ThreadClickGUI) getParent()).getSelectionAnimation().getFactor())));
                    Thread.INSTANCE.getFontManager().get("poppins").drawString(25, m.getDescription(), x + 26, index + 30, new Color(255, 255, 255, (int) (168 * ((ThreadClickGUI) getParent()).getSelectionAnimation().getFactor())));
                    RenderUtil.drawRoundedRect(x + 28 + (Thread.INSTANCE.getFontManager().get("poppins-semibold").getStringWidth(35, m.getName()) / 2), index + 18, 35, 7, 3, new Color(217, 217, 217, (int) (48 * ((ThreadClickGUI) getParent()).getSelectionAnimation().getFactor())));
                    Thread.INSTANCE.getFontManager().get("poppins").drawString(10, m.getCategory().name().toLowerCase(), (x + 28 + (Thread.INSTANCE.getFontManager().get("poppins-semibold").getStringWidth(35, m.getName()) / 2)) + ((35 - (Thread.INSTANCE.getFontManager().get("poppins").getStringWidth(10, m.getCategory().name().toLowerCase()) / 2)) / 2), index + 19.5F, new Color(255, 255, 255, (int) (255 * ((ThreadClickGUI) getParent()).getSelectionAnimation().getFactor())));
                    index += 61;
                }
            }
        } else {
            settingsPanel.render(x, y, width, height);
        }

        RenderUtil.endScissor();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (settingsPanel == null) {
            if (MathUtil.isMouseHovered(mouseX, mouseY, x + 19, y + 35, width - 38, 24) && mouseButton == 0) {
                searching = !searching;
            } else if (searching && mouseButton == 0) {
                searching = false;
            }

            float index = y + 68;
            for (Module m : Thread.INSTANCE.getModuleManager().values().stream().filter(module -> (query.isEmpty() ? module.getCategory() == category : module.getCategory() == category && module.getName().contains(query))).toList()) {
                if (MathUtil.isMouseHovered(mouseX, mouseY, x + 19, index, width - 38, 52)) {
                    if (mouseButton == 0) {
                        m.setEnabled(!m.isEnabled());
                    } else {
                        settingsPanel = new ThreadSettingsPanel(this, m);
                    }
                }
                index += 61;
            }
        } else {
            settingsPanel.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void keyTyped(int keyCode) {
        if (settingsPanel == null) {
            if (searching) {
                if (keyCode == Keyboard.KEY_BACK) {
                    if (!query.isEmpty()) {
                        query = query.substring(0, query.length() - 1);
                    }
                } else if (keyCode == 28) {
                    searching = false;
                } else {
                    query += Keyboard.getKeyName(keyCode).toLowerCase();
                }
            }
        } else {
            settingsPanel.keyTyped(keyCode);
        }
    }
}
