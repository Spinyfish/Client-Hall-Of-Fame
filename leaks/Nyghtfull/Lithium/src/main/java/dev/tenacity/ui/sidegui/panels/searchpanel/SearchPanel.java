package dev.tenacity.ui.sidegui.panels.searchpanel;

import dev.tenacity.Tenacity;
import dev.tenacity.intent.cloud.data.CloudConfig;
import dev.tenacity.intent.cloud.data.CloudScript;
import dev.tenacity.ui.sidegui.panels.Panel;
import dev.tenacity.ui.sidegui.panels.configpanel.CloudConfigRect;
import dev.tenacity.ui.sidegui.panels.scriptpanel.CloudScriptRect;
import dev.tenacity.ui.sidegui.utils.ToggleButton;
import dev.tenacity.utils.objects.Scroll;
import dev.tenacity.utils.render.ColorUtil;
import dev.tenacity.utils.render.RoundedUtil;
import dev.tenacity.utils.render.StencilUtil;
import me.xdrop.fuzzywuzzy.FuzzySearch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class SearchPanel extends Panel {
    private String searchType = "";
    private String searchTypeHold = "";
    private String searchHold = "";
    private final ToggleButton compactMode = new ToggleButton("Compact Mode");
    private final List<CloudConfigRect> cloudConfigRects = new ArrayList<>();
    private final List<CloudScriptRect> cloudScriptRects = new ArrayList<>();

    private final Scroll searchScroll = new Scroll();

    @Override
    public void initGui() {

    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {

    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        lithiumFont18.drawString("Press ESC to return to the menu", getX() + 8, getY() + 8 + lithiumBoldFont40.getHeight() + 2, ColorUtil.applyOpacity(getTextColor(), .3f));

        lithiumBoldFont40.drawString("Search Results", getX() + 8, getY() + 8, getTextColor());

        float spacing = 8;
        float backgroundX = getX() + spacing, backgroundY = getY() + (45 + spacing), backgroundWidth = getWidth() - (spacing * 2), backgroundHeight = getHeight() - (45 + spacing * 2);
        RoundedUtil.drawRound(getX() + spacing, getY() + (45 + spacing), getWidth() - (spacing * 2), getHeight() - (45 + (spacing * 2)), 5, ColorUtil.tripleColor(27, getAlpha()));

        compactMode.setX(getX() + getWidth() - (compactMode.getWH() + 15));
        compactMode.setY(getY() + 33);
        compactMode.setAlpha(getAlpha());
        compactMode.drawScreen(mouseX, mouseY);

        String search = Tenacity.INSTANCE.getSideGui().getHotbar().searchField.getText();

        if (check(search)) return;


        if (searchType.equals("Configs")) {
            drawConfigs(backgroundX, backgroundY, backgroundWidth, backgroundHeight, spacing, mouseX, mouseY, search);
        }else {
            drawScripts(backgroundX, backgroundY, backgroundWidth, backgroundHeight, spacing, mouseX, mouseY, search);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        compactMode.mouseClicked(mouseX, mouseY, button);
        if (searchType.equals("Configs")) {
            cloudConfigRects.forEach(cloudConfigRect -> cloudConfigRect.mouseClicked(mouseX, mouseY, button));
        } else {
            cloudScriptRects.forEach(cloudScriptRect -> cloudScriptRect.mouseClicked(mouseX, mouseY, button));
        }

    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {

    }

    public boolean check(String search) {
        if (!searchHold.equals(search)) {
            searchScroll.setRawScroll(0);
            searchHold = search;
        }

        if (!searchTypeHold.equals(searchType)) {
            switch (searchType) {
                case "Configs":
                    cloudConfigRects.clear();
                    cloudScriptRects.clear();
                    cloudConfigRects.addAll(Tenacity.INSTANCE.getSideGui().getConfigPanel().getCloudConfigRects());
                    break;
                case "Scripts":
                    cloudConfigRects.clear();
                    cloudScriptRects.clear();
                    break;
            }
            searchTypeHold = searchType;
            return true;
        }
        return false;
    }


    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    public void drawScripts(float x, float y, float width, float height, float spacing, int mouseX, int mouseY, String search) {
        if (Tenacity.INSTANCE.getCloudDataManager().isRefreshing())
            return;


        cloudScriptRects.sort(Comparator.comparing(CloudScriptRect::getSearchScore).reversed());

        //6 spacing on left and right = 12
        //12 + ((12 spacing between configs) * 2 large spaces because we want 3 configs on the top)
        // This equals 36, so we deduct that from the background width and then divide by 3 to get the width of each config
        float scriptWidth = (width - 36) / 3f;

        float scriptHeight = compactMode.isEnabled() ? 38 : 90;
        StencilUtil.initStencilToWrite();
        RoundedUtil.drawRound(x, y, width, height, 5, ColorUtil.tripleColor(27, getAlpha()));
        StencilUtil.readStencilBuffer(1);


        int count = 0;
        int rectXSeparation = 0;
        int rectYSeparation = 0;
        for (CloudScriptRect cloudScriptRect : cloudScriptRects) {
            CloudScript cloudScript = cloudScriptRect.getScript();

            cloudScriptRect.setSearchScore(FuzzySearch.extractOne(search, Arrays.asList(cloudScript.getName(), cloudScript.getAuthor())).getScore());

            cloudScriptRect.setAlpha(getAlpha());
            cloudScriptRect.setAccentColor(getAccentColor());
            //This changes the x and y position for showing 3 configs per line
            if (count > 2) {
                rectXSeparation = 0;
                rectYSeparation += scriptHeight + 12;
                count = 0;
            }

            cloudScriptRect.setX(x + 6 + rectXSeparation);
            cloudScriptRect.setY(y + 6 + rectYSeparation + searchScroll.getScroll());
            cloudScriptRect.setWidth(scriptWidth);
            cloudScriptRect.setHeight(scriptHeight);
            cloudScriptRect.setCompact(compactMode.isEnabled());

            if (cloudScriptRect.getY() + cloudScriptRect.getHeight() > y && cloudScriptRect.getY() < y + height) {
                cloudScriptRect.setClickable(true);
                cloudScriptRect.drawScreen(mouseX, mouseY);
            } else {
                cloudScriptRect.setClickable(false);
            }


            rectXSeparation += cloudScriptRect.getWidth() + 12;
            count++;
        }

        searchScroll.setMaxScroll(rectYSeparation);

        StencilUtil.uninitStencilBuffer();



    }

    public void drawConfigs(float x, float y, float width, float height, float spacing, int mouseX, int mouseY, String search) {

        if (Tenacity.INSTANCE.getCloudDataManager().isRefreshing())
            return;


        cloudConfigRects.sort(Comparator.comparing(CloudConfigRect::getSearchScore).reversed());

        //6 spacing on left and right = 12
        //12 + ((12 spacing between configs) * 2 large spaces because we want 3 configs on the top)
        // This equals 36, so we deduct that from the background width and then divide by 3 to get the width of each config
        float configWidth = (width - 36) / 3f;

        float configHeight = compactMode.isEnabled() ? 38 : 90;
        StencilUtil.initStencilToWrite();
        RoundedUtil.drawRound(x, y, width, height, 5, ColorUtil.tripleColor(27, getAlpha()));
        StencilUtil.readStencilBuffer(1);


        int count = 0;
        int rectXSeparation = 0;
        int rectYSeparation = 0;
        for (CloudConfigRect cloudConfigRect : cloudConfigRects) {
            CloudConfig cloudConfig = cloudConfigRect.getConfig();
            cloudConfigRect.setSearchScore(FuzzySearch.extractOne(search, Arrays.asList(cloudConfig.getName(), cloudConfig.getServer(), cloudConfig.getAuthor())).getScore());


            cloudConfigRect.setAlpha(getAlpha());
            cloudConfigRect.setAccentColor(getAccentColor());
            //This changes the x and y position for showing 3 configs per line
            if (count > 2) {
                rectXSeparation = 0;
                rectYSeparation += configHeight + 12;
                count = 0;
            }

            cloudConfigRect.setX(x + 6 + rectXSeparation);
            cloudConfigRect.setY(y + 6 + rectYSeparation + searchScroll.getScroll());
            cloudConfigRect.setWidth(configWidth);
            cloudConfigRect.setHeight(configHeight);
            cloudConfigRect.setCompact(compactMode.isEnabled());

            if (cloudConfigRect.getY() + cloudConfigRect.getHeight() > y && cloudConfigRect.getY() < y + height) {
                cloudConfigRect.setClickable(true);
                cloudConfigRect.drawScreen(mouseX, mouseY);
            } else {
                cloudConfigRect.setClickable(false);
            }


            rectXSeparation += cloudConfigRect.getWidth() + 12;
            count++;
        }

        searchScroll.setMaxScroll(rectYSeparation);

        StencilUtil.uninitStencilBuffer();

    }


}
