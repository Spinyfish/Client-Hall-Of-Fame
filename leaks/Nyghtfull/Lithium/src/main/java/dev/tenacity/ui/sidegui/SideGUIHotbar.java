package dev.tenacity.ui.sidegui;

import dev.tenacity.Tenacity;
import dev.tenacity.module.impl.render.HUDMod;
import dev.tenacity.ui.Screen;
import dev.tenacity.ui.sidegui.utils.CarouselButtons;
import dev.tenacity.ui.sidegui.utils.DropdownObject;
import dev.tenacity.ui.sidegui.utils.IconButton;
import dev.tenacity.utils.animations.Animation;
import dev.tenacity.utils.animations.Direction;
import dev.tenacity.utils.animations.impl.DecelerateAnimation;
import dev.tenacity.utils.font.FontUtil;
import dev.tenacity.utils.misc.MathUtils;
import dev.tenacity.utils.misc.Multithreading;
import dev.tenacity.utils.objects.TextField;
import dev.tenacity.utils.render.ColorUtil;
import dev.tenacity.utils.render.RenderUtil;
import dev.tenacity.utils.render.RoundedUtil;
import dev.tenacity.utils.time.TimerUtil;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class SideGUIHotbar implements Screen {

    public float x, y, width, height, alpha;

    public final TextField searchField = new TextField(lithiumFont20);
    private final Animation searchAnimation = new DecelerateAnimation(250, 1).setDirection(Direction.BACKWARDS);

    public final DropdownObject searchType = new DropdownObject("Type", "Configs", "Scripts");

    @Getter
    private final CarouselButtons carouselButtons = new CarouselButtons("Scripts", "Configs", "Info");

    private final IconButton refreshButton = new IconButton(FontUtil.REFRESH, "Refresh all of the cloud and local data");

    @Getter
    @Setter
    private String currentPanel;

    @Override
    public void initGui() {
        currentPanel = carouselButtons.getCurrentButton();
    }
    int ticks = 0;

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (keyCode != Keyboard.KEY_ESCAPE) {
            searchField.keyTyped(typedChar, keyCode);
        }
    }

    private final TimerUtil refreshTimer = new TimerUtil();
    private final Animation refreshText = new DecelerateAnimation(250, 1);

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        if (Tenacity.INSTANCE.getCloudDataManager().isRefreshing()) {
            refreshTimer.reset();
        }
        boolean searching = searchField.isFocused() || !searchField.getText().equals("");

        RoundedUtil.drawRound(x + .625f, y + .625f, width - 1.25f, height - 1.25f, 5, ColorUtil.tripleColor(25, alpha));
        Gui.drawRect2(x, y + height - 4, width, 4, ColorUtil.tripleColor(25, alpha * alpha * alpha).getRGB());

        Color textColor = ColorUtil.applyOpacity(Color.WHITE, alpha);


        lithiumBoldFont32.drawString("Lithium", x + 9.5f, y + lithiumBoldFont32.getMiddleOfBox(height), textColor);
        lithiumFont18.drawString(Tenacity.VERSION, x + 9.5f + lithiumBoldFont32.getStringWidth("Lithium") - 2,
                y + lithiumBoldFont32.getMiddleOfBox(height) - 2.5f, ColorUtil.applyOpacity(textColor, .5f));

        searchAnimation.setDirection(searchField.isFocused() || !searchField.getText().equals("") ? Direction.FORWARDS : Direction.BACKWARDS);
        float searchAnim = searchAnimation.getOutput().floatValue();
        float carouselAlpha = alpha * (1 - searchAnim);

        carouselButtons.setAlpha(carouselAlpha);
        carouselButtons.setRectWidth(75);
        carouselButtons.setRectHeight(20.5f);
        carouselButtons.setX(x + width / 2f - carouselButtons.getTotalWidth() / 2f);
        carouselButtons.setY(y + height / 2f - carouselButtons.getRectHeight() / 2f);
        carouselButtons.drawScreen(mouseX, mouseY);


        if(!searching) {
            refreshText.setDirection(refreshTimer.getTime() > 12000 ? Direction.BACKWARDS : Direction.FORWARDS);

            lithiumFont14.drawString(String.valueOf(Math.round(refreshTimer.getTime() / 1000f)), refreshButton.getX() - 15,
                    y + lithiumFont14.getMiddleOfBox(height), ColorUtil.applyOpacity(-1, refreshText.getOutput().floatValue()));

            refreshButton.setAlpha(carouselAlpha);
            refreshButton.setX(carouselButtons.getX() - (refreshButton.getWidth() + 10));
            refreshButton.setY(carouselButtons.getY() + 1 + carouselButtons.getRectHeight() / 2f - refreshButton.getHeight() / 2f);
            refreshButton.setAccentColor(Color.WHITE);
            refreshButton.setIconFont(iconFont20);
            refreshButton.setClickAction(() -> Tenacity.INSTANCE.getConfigManager().collectConfigs());

            boolean refreshing = Tenacity.INSTANCE.getCloudDataManager().isRefreshing();
            if (refreshing) {
                float stringWidth = iconFont20.getStringWidth(FontUtil.REFRESH);
                RenderUtil.rotateStartReal(refreshButton.getX() + stringWidth / 2f, refreshButton.getY() + iconFont20.getHeight() / 2f - 1,
                        stringWidth, iconFont20.getHeight(), (System.currentTimeMillis() % 1080) / 3f);
            }

            refreshButton.drawScreen(mouseX, mouseY);

            if (refreshing) {
                RenderUtil.rotateEnd();
            }
        }


        searchField.setRadius(5);
        searchField.setFill(ColorUtil.tripleColor(17, alpha));
        searchField.setOutline(ColorUtil.applyOpacity(Color.WHITE, 0));

        searchField.setHeight(carouselButtons.getRectHeight());
        searchField.setWidth(145.5f + (200 * searchAnim));
        float searchX = x + width - (searchField.getRealWidth() + 11);

        searchField.setXPosition(MathUtils.interpolateFloat(searchX, x + width / 2f - searchField.getRealWidth() / 2f, searchAnim));
        searchField.setYPosition(y + height / 2f - (searchField.getHeight() / 2f));
        searchField.setBackgroundText("Search");
        searchField.drawTextBox();

        if (!searchAnimation.isDone() || searchAnimation.finished(Direction.FORWARDS)) {

            searchType.setWidth(75);
            searchType.setHeight(carouselButtons.getRectHeight() - 5.5f);
            searchType.setX(x + width - (searchType.getWidth() + 11));
            searchType.setY(y + height / 2f - (searchType.getHeight() / 2f));
            searchType.setAlpha(alpha * searchAnim);
            searchType.setAccentColor(ColorUtil.applyOpacity(HUDMod.getClientColors().getFirst(), searchType.getAlpha()));
            searchType.drawScreen(mouseX, mouseY);
        }

    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        searchField.mouseClicked(mouseX, mouseY, button);

        if (searchField.isFocused() || !searchField.getText().equals("")) {
            searchType.mouseClicked(mouseX, mouseY, button);
            currentPanel = "Search";
            return;
        }

        if (!Tenacity.INSTANCE.getCloudDataManager().isRefreshing() && refreshTimer.hasTimeElapsed(12000)) {
            refreshButton.mouseClicked(mouseX, mouseY, button);
        }
        carouselButtons.mouseClicked(mouseX, mouseY, button);
        currentPanel = carouselButtons.getCurrentButton();
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
    }
}
