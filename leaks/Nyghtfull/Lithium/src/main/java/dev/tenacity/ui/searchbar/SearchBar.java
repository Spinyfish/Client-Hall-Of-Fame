package dev.tenacity.ui.searchbar;

import dev.tenacity.Tenacity;
import dev.tenacity.ui.Screen;
import dev.tenacity.utils.animations.Animation;
import dev.tenacity.utils.animations.Direction;
import dev.tenacity.utils.animations.impl.DecelerateAnimation;
import dev.tenacity.utils.misc.HoveringUtil;
import dev.tenacity.utils.objects.TextField;
import dev.tenacity.utils.render.ColorUtil;
import dev.tenacity.utils.render.RoundedUtil;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Keyboard;

import java.awt.*;

@Getter
@Setter
public class SearchBar implements Screen {

    private boolean focused, typing, hoveringBottomOfScreen;
    private final Animation focusAnimation = new DecelerateAnimation(175, 1).setDirection(Direction.BACKWARDS),
            hoverAnimation = new DecelerateAnimation(175, 1).setDirection(Direction.BACKWARDS),
            openAnimation = new DecelerateAnimation(250, 1).setDirection(Direction.BACKWARDS);
    private final TextField searchField = new TextField(lithiumFont18);
    private float alpha;

    @Override
    public void initGui() {
        openAnimation.setDirection(Direction.FORWARDS);
        searchField.setText("");
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            searchField.setFocused(false);
            return;
        }

        if (GuiScreen.isCtrlKeyDown() && keyCode == Keyboard.KEY_F) {
            searchField.setFocused(true);
            Tenacity.INSTANCE.getModuleCollection().getModules().forEach(module -> module.setExpanded(false));
            return;
        }

        searchField.keyTyped(typedChar, keyCode);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        focused = searchField.isFocused() || !searchField.getText().isEmpty();
        typing = searchField.isFocused();
        ScaledResolution sr = new ScaledResolution(mc);

        float width = sr.getScaledWidth(), height = sr.getScaledHeight();
        hoveringBottomOfScreen = HoveringUtil.isHovering(width / 2f - 120,
                sr.getScaledHeight() - (100), 240, 100, mouseX, mouseY);


        hoverAnimation.setDirection(hoveringBottomOfScreen && !focused ? Direction.FORWARDS : Direction.BACKWARDS);
        focusAnimation.setDirection(focused ? Direction.FORWARDS : Direction.BACKWARDS);
        float focusAnim = focusAnimation.getOutput().floatValue();

        float hover = hoverAnimation.getOutput().floatValue();


        float openAnim = Math.min(1, alpha);
        float searchAlpha = Math.min(1, hover + focusAnim);
      //  lithiumFont26.drawCenteredString("CTRL + F to search for modules", sr.getScaledWidth() / 2f, sr.getScaledHeight() - 75,
           //     ColorUtil.applyOpacity(-1, (.3f * (1 - searchAlpha)) * openAnim));


        searchField.setWidth(200);
        searchField.setHeight(25);
        searchField.setFont(lithiumFont24);
        searchField.setXPosition(sr.getScaledWidth() / 2f - 100);

        searchField.setYPosition(sr.getScaledHeight() - (70 + (25 * hover) + (60 * focusAnim)));
        searchField.setRadius(5);
        searchField.setAlpha(Math.max(hover * .85f, focusAnim));
        searchField.setTextAlpha(searchField.getAlpha());
        searchField.setFill(ColorUtil.tripleColor(17));
        searchField.setOutline(null);
        searchField.setBackgroundText("Search");
        searchField.drawTextBox();


    }

    public void drawEffects() {
        ScaledResolution sr = new ScaledResolution(mc);
        float hover = hoverAnimation.getOutput().floatValue();
        float focusAnim = focusAnimation.getOutput().floatValue();

        float openAnim = Math.min(1, alpha);
        float searchAlpha = Math.min(1, hover + focusAnim);

        lithiumFont26.drawCenteredString("Do §lCTRL§r+§lF§r to open the search bar", sr.getScaledWidth() / 2f, sr.getScaledHeight() - 75,
                ColorUtil.applyOpacity(Color.BLACK, (1 * (1 - searchAlpha)) * openAnim));


        RoundedUtil.drawRound(searchField.getXPosition(), searchField.getYPosition(), 200, searchField.getHeight(), 5,
                ColorUtil.applyOpacity(Color.BLACK, Math.max(hover, focusAnim)));
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        boolean focused = searchField.isFocused();
        searchField.mouseClicked(mouseX, mouseY, button);
        if (!focused && searchField.isFocused()) {
            Tenacity.INSTANCE.getModuleCollection().getModules().forEach(module -> module.setExpanded(false));
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {

    }
}
