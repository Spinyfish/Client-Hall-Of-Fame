package dev.tenacity.ui.sidegui;

import dev.tenacity.utils.tuples.Pair;
import dev.tenacity.Tenacity;
import dev.tenacity.module.impl.render.HUDMod;
import dev.tenacity.ui.Screen;
import dev.tenacity.ui.sidegui.forms.Form;
import dev.tenacity.ui.sidegui.forms.impl.EditForm;
import dev.tenacity.ui.sidegui.forms.impl.SaveForm;
import dev.tenacity.ui.sidegui.forms.impl.UploadForm;
import dev.tenacity.ui.sidegui.panels.Panel;
import dev.tenacity.ui.sidegui.panels.configpanel.ConfigPanel;
import dev.tenacity.ui.sidegui.panels.infopanel.InfoPanel;
import dev.tenacity.ui.sidegui.panels.searchpanel.SearchPanel;
import dev.tenacity.ui.sidegui.utils.TooltipObject;
import dev.tenacity.utils.animations.Animation;
import dev.tenacity.utils.animations.Direction;
import dev.tenacity.utils.animations.impl.DecelerateAnimation;
import dev.tenacity.utils.misc.HoveringUtil;
import dev.tenacity.utils.misc.MathUtils;
import dev.tenacity.utils.objects.Drag;
import dev.tenacity.utils.render.ColorUtil;
import dev.tenacity.utils.render.RoundedUtil;
import dev.tenacity.utils.time.TimerUtil;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Getter
public class SideGUI implements Screen {

    @Setter
    private boolean focused;
    private float rectWidth, rectHeight;
    private final Animation openAnimation = new DecelerateAnimation(250, 1).setDirection(Direction.BACKWARDS);
    private final Animation hoverAnimation = new DecelerateAnimation(250, 1).setDirection(Direction.BACKWARDS);
    private final Animation clickAnimation = new DecelerateAnimation(250, 1).setDirection(Direction.BACKWARDS);
    private SideGUIHotbar hotbar = new SideGUIHotbar();
    private HashMap<String, Panel> panels;
    private HashMap<String, Form> forms;
    private static Form currentForm;
    private final List<TooltipObject> tooltips = new ArrayList<>();
    public boolean typing = false;
    private final Color greenEnabledColor = new Color(70, 220, 130);
    private final Color redBadColor = new Color(209, 56, 56);
    private final Animation formFadeAnimation = new DecelerateAnimation(250, 1).setDirection(Direction.BACKWARDS);

    private Drag drag;
    private TimerUtil timerUtil;

    @Override
    public void onDrag(int mouseX, int mouseY) {
        if (drag != null) {
            drag.onDraw(mouseX, mouseY);
        }
    }

    @Override
    public void initGui() {
        if (panels == null) {
            panels = new HashMap<>();
            panels.put("Search", new SearchPanel());
            panels.put("Info", new InfoPanel());
            panels.put("Configs", new ConfigPanel());
        }
        if (forms == null) {
            forms = new HashMap<>();
            forms.put("Upload Script", new UploadForm("Script"));
            forms.put("Upload Config", new UploadForm("Config"));
            forms.put("Edit Script", new EditForm("Script"));
            forms.put("Edit Config", new EditForm("Config"));
            forms.put("Save Config", new SaveForm());
        }

        hotbar.initGui();
        panels.values().forEach(Panel::initGui);
        focused = false;
        currentForm = null;
        timerUtil = new TimerUtil();
        rectWidth = 550;
        rectHeight = 350;
        ScaledResolution sr = new ScaledResolution(mc);
        drag = new Drag(sr.getScaledWidth() - 40, sr.getScaledHeight() / 2f - rectHeight / 2f);
    }


    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (focused) {
            if (currentForm != null) {
                currentForm.keyTyped(typedChar, keyCode);
                if (keyCode == Keyboard.KEY_ESCAPE) {
                    formFadeAnimation.setDirection(Direction.BACKWARDS);
                    currentForm.clear();
                }
                return;
            }
            hotbar.keyTyped(typedChar, keyCode);
            if (keyCode == Keyboard.KEY_ESCAPE) {
                if (hotbar.searchField.isFocused() || !hotbar.searchField.getText().equals("")) {
                    hotbar.setCurrentPanel(hotbar.getCarouselButtons().getCurrentButton());
                }
                hotbar.searchField.setText("");
                hotbar.searchField.setFocused(false);
            }
        }
    }

    private float animateX = 0;
    private boolean canSnap;

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        ScaledResolution sr = new ScaledResolution(mc);
        HUDMod hudMod = (HUDMod) Tenacity.INSTANCE.getModuleCollection().get(HUDMod.class);
        Pair<Color, Color> colors = HUDMod.getClientColors();

        //This is for moving the side gui to the middle of the screen when it's clicked
        clickAnimation.setDirection(focused ? Direction.FORWARDS : Direction.BACKWARDS);

        if (animateX == 0) {
            animateX = drag.getX();
        } else if (clickAnimation.isDone()) {
            animateX = drag.getX();
        }

        if (clickAnimation.getDirection().forwards() && !clickAnimation.isDone()) {
            //Animate the Side GUI to the middle when it is clicked
            drag.setX(MathUtils.interpolateFloat(animateX, sr.getScaledWidth() / 2f - rectWidth / 2f, clickAnimation.getOutput()));
        } else {
            drag.setX(MathUtils.interpolateFloat(drag.getInitialX(), animateX, clickAnimation.getOutput()));
        }

        //Animating the Side GUI to the right of the screen when clickguis are opened
        float x = MathUtils.interpolateFloat(sr.getScaledWidth(), drag.getX(), openAnimation.getOutput());
        float y = drag.getY();

        //If hovering on the gui when it's on the side then make alpha more
        boolean hovering = SideGUI.isHovering(x, y, rectWidth, rectHeight, mouseX, mouseY);

        //This if statement prevents hoveranimations during the click animation
        if (clickAnimation.isDone()) {
            hoverAnimation.setDirection(hovering || focused ? Direction.FORWARDS : Direction.BACKWARDS);
        }

        // Only need to do this if the gui is focused and the animation finished
        if (clickAnimation.finished(Direction.FORWARDS)) {
            // If 25% of the GUI is not visible then make it transparent to show the user that when they release it will be snapped back to the right
            canSnap = (drag.getX() + (rectWidth / 2f) + (rectWidth / 4f)) > sr.getScaledWidth();
            hoverAnimation.setDirection(canSnap ? Direction.BACKWARDS : hoverAnimation.getDirection());
        } else canSnap = false;


        //I want the opacity to go up faster with the click animation, so i exceed normal values and the tripleColor method corrects it to limit to 1
        Color color = ColorUtil.tripleColor(35, .7f + (.25f * hoverAnimation.getOutput().floatValue()) + (.05f * clickAnimation.getOutput().floatValue()));


        RoundedUtil.drawRound(x + .625f, y + .625f, rectWidth - 1.25f, rectHeight - 1.25f, 5, color);


        float alpha = .25f + (.15f * hoverAnimation.getOutput().floatValue()) + (.6f * clickAnimation.getOutput().floatValue());

        hotbar.x = x;
        hotbar.y = y;
        hotbar.width = rectWidth;
        hotbar.height = 36;
        hotbar.alpha = alpha;
        hotbar.drawScreen(mouseX, mouseY);

        typing = hotbar.searchField.isFocused() || !hotbar.searchField.getText().equals("") || currentForm != null;

        if (!focused) return;
        if (panels.containsKey(hotbar.getCurrentPanel())) {
            Panel panel = panels.get(hotbar.getCurrentPanel());

            if (panel instanceof SearchPanel) {
                ((SearchPanel) panel).setSearchType(hotbar.searchType.getSelection());
            }

            panel.setX(x);
            panel.setY(y + hotbar.height);
            panel.setWidth(rectWidth);
            panel.setHeight(rectHeight - hotbar.height);
            panel.setAlpha(alpha);
            panel.drawScreen(mouseX, mouseY);
        }

        if (formFadeAnimation.finished(Direction.BACKWARDS)) {
            currentForm = null;
        }

        if (!formFadeAnimation.isDone() || formFadeAnimation.finished(Direction.FORWARDS)) {
            float formAnim = formFadeAnimation.getOutput().floatValue();
            RoundedUtil.drawRound(x + .625f, y + .625f, rectWidth - 1.25f,
                    rectHeight - 1.25f, 5, ColorUtil.applyOpacity(Color.BLACK, .4f * formAnim));

            currentForm.setX(x + (rectWidth / 2f) - (currentForm.getWidth() / 2f));
            currentForm.setY(y + (rectHeight / 2f) - (currentForm.getHeight() / 2f));
            currentForm.setAlpha(alpha * formAnim);
            currentForm.drawScreen(mouseX, mouseY);

        }

        if (!Tenacity.INSTANCE.getCloudDataManager().isRefreshing()) {
            tooltips.forEach(tooltip -> tooltip.drawScreen(mouseX, mouseY));
        }

    }

    public void drawForEffects(boolean bloom) {
        ScaledResolution sr = new ScaledResolution(mc);
        float x = MathUtils.interpolateFloat(sr.getScaledWidth(), drag.getX(), openAnimation.getOutput());
        float y = drag.getY();

        RoundedUtil.drawRound(x + .625f, y + .625f, rectWidth - 1.25f, rectHeight - 1.25f, 5, Color.BLACK);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        boolean hovering = SideGUI.isHovering(drag.getX(), drag.getY(), rectWidth, rectHeight, mouseX, mouseY);
        if (!focused && hovering) {
            focused = true;
        } else if (focused) {

            if (currentForm != null) {
                currentForm.mouseClicked(mouseX, mouseY, button);
                return;
            }
            // Only want to drag when clicking the top
            boolean hoveringTop = SideGUI.isHovering(drag.getX(), drag.getY(), rectWidth, 40, mouseX, mouseY);
            if (!hotbar.searchField.isFocused() && !hotbar.getCarouselButtons().isHovering()) {
                drag.onClick(mouseX, mouseY, button, hoveringTop);
            }
            hotbar.mouseClicked(mouseX, mouseY, button);


            if (panels.containsKey(hotbar.getCurrentPanel())) {
                panels.get(hotbar.getCurrentPanel()).mouseClicked(mouseX, mouseY, button);
            }

        }


    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int button) {
        drag.onRelease(button);
        //If the transparent animation is shown and the user released the mouse then snap it back to the right
        if (canSnap) {
            focused = false;
        }
    }

    public ConfigPanel getConfigPanel() {
        return (ConfigPanel) panels.get("Configs");
    }

    public void addTooltip(TooltipObject tooltip) {
        if (tooltips.contains(tooltip)) return;
        tooltips.add(tooltip);
    }

    public Form displayForm(String form) {
        if (form == null) {
            currentForm.clear();
            formFadeAnimation.setDirection(Direction.BACKWARDS);
            return null;
        }

        currentForm = forms.get(form);

        formFadeAnimation.setDirection(Direction.FORWARDS);
        return currentForm;
    }

    public static boolean isHovering(float x, float y, float width, float height, int mouseX, int mouseY) {
        return currentForm == null && HoveringUtil.isHovering(x, y, width, height, mouseX, mouseY);
    }

}
