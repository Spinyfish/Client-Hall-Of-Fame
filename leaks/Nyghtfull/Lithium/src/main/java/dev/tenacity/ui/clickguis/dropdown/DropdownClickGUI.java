package dev.tenacity.ui.clickguis.dropdown;

import dev.tenacity.utils.tuples.Pair;
import dev.tenacity.Tenacity;
import dev.tenacity.module.Category;
import dev.tenacity.module.impl.movement.InventoryMove;
import dev.tenacity.module.impl.render.ClickGUIMod;
import dev.tenacity.ui.searchbar.SearchBar;
import dev.tenacity.ui.sidegui.SideGUI;
import dev.tenacity.utils.animations.Animation;
import dev.tenacity.utils.animations.Direction;
import dev.tenacity.utils.animations.impl.EaseBackIn;
import dev.tenacity.utils.render.RenderUtil;
import dev.tenacity.utils.render.Theme;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

public class DropdownClickGUI extends GuiScreen {

    private final Pair<Animation, Animation> openingAnimations = Pair.of(
            new EaseBackIn(400, 1, 2f),
            new EaseBackIn(400, .4f, 2f));


    private List<CategoryPanel> categoryPanels;

    public boolean binding;


    public static boolean gradient;

    @Override
    public void onDrag(int mouseX, int mouseY) {
        for (CategoryPanel catPanels : categoryPanels) {
            catPanels.onDrag(mouseX, mouseY);
        }
        Tenacity.INSTANCE.getSideGui().onDrag(mouseX, mouseY);
    }

    @Override
    public void initGui() {
        openingAnimations.use((fade, opening) -> {
            fade.setDirection(Direction.FORWARDS);
            opening.setDirection(Direction.FORWARDS);
        });


        if (categoryPanels == null) {
            categoryPanels = new ArrayList<>();
            for (Category category : Category.values()) {
                categoryPanels.add(new CategoryPanel(category, openingAnimations));
            }
        }

        Tenacity.INSTANCE.getSideGui().initGui();
        Tenacity.INSTANCE.getSearchBar().initGui();


        for (CategoryPanel catPanels : categoryPanels) {
            catPanels.initGui();
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (keyCode == Keyboard.KEY_ESCAPE && !binding) {
            if (Tenacity.INSTANCE.getSearchBar().isFocused()) {
                Tenacity.INSTANCE.getSearchBar().getSearchField().setText("");
                Tenacity.INSTANCE.getSearchBar().getSearchField().setFocused(false);
                return;
            }

            if (Tenacity.INSTANCE.getSideGui().isFocused()) {
                Tenacity.INSTANCE.getSideGui().setFocused(false);
                return;
            }

            Tenacity.INSTANCE.getSearchBar().getOpenAnimation().setDirection(Direction.BACKWARDS);
            openingAnimations.use((fade, opening) -> {
                fade.setDirection(Direction.BACKWARDS);
                opening.setDirection(Direction.BACKWARDS);
            });
        }
        Tenacity.INSTANCE.getSideGui().keyTyped(typedChar, keyCode);
        Tenacity.INSTANCE.getSearchBar().keyTyped(typedChar, keyCode);
        categoryPanels.forEach(categoryPanel -> categoryPanel.keyTyped(typedChar, keyCode));
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        binding = categoryPanels.stream().anyMatch(CategoryPanel::isTyping) ||
                (Tenacity.INSTANCE.getSideGui().isFocused() && Tenacity.INSTANCE.getSideGui().typing) || Tenacity.INSTANCE.getSearchBar().isTyping();


     //  Gui.drawRect2(0,0, width, height, ColorUtil.applyOpacity(0, Tenacity.INSTANCE.getSearchBar().getFocusAnimation().getOutput().floatValue() * .25f));
        if (ClickGUIMod.walk.isEnabled() && !binding) {
            InventoryMove.updateStates();
        }

        // If the closing animation finished then change the gui screen to null
        if (openingAnimations.getSecond().finished(Direction.BACKWARDS)) {
            mc.displayGuiScreen(null);
            return;
        }

        gradient = Theme.getCurrentTheme().isGradient() || ClickGUIMod.gradient.isEnabled();


        boolean focusedConfigGui = Tenacity.INSTANCE.getSideGui().isFocused() || Tenacity.INSTANCE.getSearchBar().isTyping();
        int fakeMouseX = focusedConfigGui ? 0 : mouseX, fakeMouseY = focusedConfigGui ? 0 : mouseY;
        ScaledResolution sr = new ScaledResolution(mc);


        RenderUtil.scaleStart(sr.getScaledWidth() / 2f, sr.getScaledHeight() / 2f, openingAnimations.getSecond().getOutput().floatValue() + .6f);

        for (CategoryPanel catPanels : categoryPanels) {
            catPanels.drawScreen(fakeMouseX, fakeMouseY);
        }

        RenderUtil.scaleEnd();
        categoryPanels.forEach(categoryPanel -> categoryPanel.drawToolTips(fakeMouseX, fakeMouseY));

        // Draw Side GUI
        SideGUI sideGUI = Tenacity.INSTANCE.getSideGui();
        sideGUI.getOpenAnimation().setDirection(openingAnimations.getFirst().getDirection());
        sideGUI.drawScreen(mouseX, mouseY);

        SearchBar searchBar = Tenacity.INSTANCE.getSearchBar();
        searchBar.setAlpha(openingAnimations.getFirst().getOutput().floatValue() * (1 - sideGUI.getClickAnimation().getOutput().floatValue()));
        searchBar.drawScreen(fakeMouseX, fakeMouseY);
    }

    public void renderEffects() {
        ScaledResolution sr = new ScaledResolution(mc);
        RenderUtil.scaleStart(sr.getScaledWidth() / 2f, sr.getScaledHeight() / 2f, openingAnimations.getSecond().getOutput().floatValue() + .6f);
        for (CategoryPanel catPanels : categoryPanels) {
            catPanels.renderEffects();
        }
        RenderUtil.scaleEnd();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        boolean focused = Tenacity.INSTANCE.getSideGui().isFocused();
        Tenacity.INSTANCE.getSideGui().mouseClicked(mouseX, mouseY, mouseButton);
        Tenacity.INSTANCE.getSearchBar().mouseClicked(mouseX, mouseY, mouseButton);
        if (!focused) {
            categoryPanels.forEach(cat -> cat.mouseClicked(mouseX, mouseY, mouseButton));
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        boolean focused = Tenacity.INSTANCE.getSideGui().isFocused();
        Tenacity.INSTANCE.getSideGui().mouseReleased(mouseX, mouseY, state);
        Tenacity.INSTANCE.getSearchBar().mouseReleased(mouseX, mouseY, state);
        if (!focused) {
            categoryPanels.forEach(cat -> cat.mouseReleased(mouseX, mouseY, state));
        }
    }

    @Override
    public void onGuiClosed() {
        if (ClickGUIMod.rescale.isEnabled()) {
            mc.gameSettings.guiScale = ClickGUIMod.prevGuiScale;
        }
    }


}
