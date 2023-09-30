package dev.tenacity.ui.sidegui.panels.configpanel;

import dev.tenacity.Tenacity;
import dev.tenacity.config.ConfigManager;
import dev.tenacity.config.LocalConfig;
import dev.tenacity.intent.cloud.CloudUtils;
import dev.tenacity.intent.cloud.data.CloudConfig;
import dev.tenacity.intent.cloud.data.Votes;
import dev.tenacity.module.impl.render.HUDMod;
import dev.tenacity.ui.sidegui.SideGUI;
import dev.tenacity.ui.sidegui.forms.Form;
import dev.tenacity.ui.sidegui.panels.Panel;
import dev.tenacity.ui.sidegui.utils.ActionButton;
import dev.tenacity.ui.sidegui.utils.CarouselButtons;
import dev.tenacity.ui.sidegui.utils.DropdownObject;
import dev.tenacity.ui.sidegui.utils.ToggleButton;
import dev.tenacity.utils.misc.Multithreading;
import dev.tenacity.utils.objects.Scroll;
import dev.tenacity.utils.render.ColorUtil;
import dev.tenacity.utils.render.RoundedUtil;
import dev.tenacity.utils.render.StencilUtil;
import dev.tenacity.utils.time.TimerUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ConfigPanel extends Panel {

    private final DropdownObject sorting = new DropdownObject("Sort by", "Relevance", "Alphabetical", "Top all time", "Recently updated");

    private final CarouselButtons carouselButtons = new CarouselButtons("Cloud", "Local");

    private List<ToggleButton> toggleButtons;
    private ToggleButton compactMode = new ToggleButton("Compact Mode");
    private List<ActionButton> actionButtons;

    @Getter
    private final List<CloudConfigRect> cloudConfigRects = new ArrayList<>();
    private final List<LocalConfigRect> localConfigRects = new ArrayList<>();
    private final Scroll cloudConfigScroll = new Scroll();
    private final Scroll localConfigScroll = new Scroll();

    public ConfigPanel() {
        toggleButtons = new ArrayList<>();
        toggleButtons.add(new ToggleButton("Load visuals"));
        toggleButtons.add(new ToggleButton("Only show configs from current version"));
        toggleButtons.add(new ToggleButton("Only show configs made by you"));

        actionButtons = new ArrayList<>();
        actionButtons.add(new ActionButton("Upload config"));
        actionButtons.add(new ActionButton("Save current config"));
        refresh();
    }

    private String sortingSelection = "Relevance";
    @Setter
    private boolean refresh = false;

    @Override
    public void initGui() {
        sortingSelection = sorting.getSelection();
        sortConfigs();
        cloudConfigScroll.setRawScroll(0);
        localConfigScroll.setRawScroll(0);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {

    }

    private final TimerUtil voteRefreshTimer = new TimerUtil();

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        //Every 20 seconds we update the votes count
        if (voteRefreshTimer.hasTimeElapsed(30000)) {
            Multithreading.runAsync(() -> Tenacity.INSTANCE.getCloudDataManager().refreshVotes());
            voteRefreshTimer.reset();
        }


        Tenacity.INSTANCE.getCloudDataManager().applyVotes();

        lithiumBoldFont40.drawString("Configs", getX() + 8, getY() + 8, getTextColor());

        boolean filterVersion = toggleButtons.get(1).isEnabled();
        boolean filterAuthor = toggleButtons.get(2).isEnabled();


        carouselButtons.setAlpha(getAlpha());
        carouselButtons.setRectWidth(50);
        carouselButtons.setRectHeight(18);
        carouselButtons.setBackgroundColor(ColorUtil.tripleColor(55));
        carouselButtons.setY(getY() + 72 - carouselButtons.getRectHeight());
        carouselButtons.setX(getX() + getWidth() / 2f - carouselButtons.getTotalWidth() / 2f);
        carouselButtons.drawScreen(mouseX, mouseY);


        float spacing = 8;

        int additionalSeparation = 0;
        for (ActionButton button : actionButtons) {
            button.setX(getX() + 10 + additionalSeparation);
            button.setWidth(85);
            button.setHeight(15);
            button.setY(carouselButtons.getY() + carouselButtons.getRectHeight() / 2f - button.getHeight() / 2f);
            button.setAlpha(getAlpha());

            button.setClickAction(() -> {
                switch (button.getName()) {
                    case "Upload config":
                        Form uploadForm = Tenacity.INSTANCE.getSideGui().displayForm("Upload Config");
                        uploadForm.setTriUploadAction((name, description, server) -> {
                            Multithreading.runAsync(() -> {
                                String fileData = Tenacity.INSTANCE.getConfigManager().serialize();
                                CloudUtils.postOnlineConfig(name, description, server, fileData);
                                Tenacity.INSTANCE.getCloudDataManager().refreshData();
                            });
                        });
                        break;
                    case "Save current config":
                        Form saveForm = Tenacity.INSTANCE.getSideGui().displayForm("Save Config");
                        saveForm.setUploadAction((name, description) -> {
                            Multithreading.runAsync(() -> {
                                Tenacity.INSTANCE.getConfigManager().saveConfig(name);
                                Tenacity.INSTANCE.getCloudDataManager().refreshData();
                            });
                        });
                        break;
                }
            });

            button.drawScreen(mouseX, mouseY);

            additionalSeparation += button.getWidth() + spacing;
        }


        float backgroundY = carouselButtons.getY() + carouselButtons.getRectHeight() + spacing;
        float backgroundX = getX() + 8;
        float backgroundWidth = getWidth() - (spacing * 2);
        float backgroundHeight = getHeight() - ((backgroundY - getY()) + 1 + spacing);

        RoundedUtil.drawRound(getX() + spacing, backgroundY, backgroundWidth, backgroundHeight, 5, ColorUtil.tripleColor(27, getAlpha()));
        boolean hovering = SideGUI.isHovering(getX() + spacing, backgroundY, backgroundWidth, backgroundHeight, mouseX, mouseY);
        if (hovering) {
            if (carouselButtons.getCurrentButton().equals("Cloud")) {
                cloudConfigScroll.onScroll(35);
            } else {
                localConfigScroll.onScroll(35);
            }
        }

        if (refresh) {
            refresh();
            return;
        }

        switch (carouselButtons.getCurrentButton()) {
            case "Cloud":
                int seperation = 0;
                for (ToggleButton toggleButton : toggleButtons) {
                    toggleButton.setX(getX() + getWidth() - (toggleButton.getWH() + 12));
                    toggleButton.setY(getY() + 35 + seperation);
                    toggleButton.setAlpha(getAlpha());
                    toggleButton.drawScreen(mouseX, mouseY);
                    seperation += toggleButton.getWH() + 4;
                }

                compactMode.setX(getX() + 69);
                compactMode.setY(getY() + 33);
                compactMode.setAlpha(getAlpha());
                compactMode.drawScreen(mouseX, mouseY);

                if (!sortingSelection.equals(sorting.getSelection())) {
                    Multithreading.runAsync(() -> Tenacity.INSTANCE.getCloudDataManager().refreshData());
                    sortingSelection = sorting.getSelection();
                }

                if (Tenacity.INSTANCE.getCloudDataManager().isRefreshing())
                    return;

                //6 spacing on left and right = 12
                //12 + ((12 spacing between configs) * 2 large spaces because we want 3 configs on the top)
                // This equals 36, so we deduct that from the background width and then divide by 3 to get the width of each config
                float configWidth = (backgroundWidth - 36) / 3f;

                float configHeight = compactMode.isEnabled() ? 38 : 90;
                StencilUtil.initStencilToWrite();
                RoundedUtil.drawRound(getX() + spacing, backgroundY, backgroundWidth, backgroundHeight, 5, ColorUtil.tripleColor(27, getAlpha()));
                StencilUtil.readStencilBuffer(1);


                int count = 0;
                int rectXSeparation = 0;
                int rectYSeparation = 0;
                for (CloudConfigRect cloudConfigRect : cloudConfigRects) {

                    if (filterAuthor) {
                        String configAuthor = cloudConfigRect.getConfig().getAuthor();
                        if (!Tenacity.INSTANCE.getIntentAccount().username.equals(configAuthor)) continue;
                    }

                    if (filterVersion && !cloudConfigRect.getConfig().getVersion().equals(Tenacity.VERSION)) continue;


                    cloudConfigRect.setAlpha(getAlpha());
                    cloudConfigRect.setAccentColor(getAccentColor());
                    //This changes the x and y position for showing 3 configs per line
                    if (count > 2) {
                        rectXSeparation = 0;
                        rectYSeparation += configHeight + 12;
                        count = 0;
                    }

                    cloudConfigRect.setX(backgroundX + 6 + rectXSeparation);
                    cloudConfigRect.setY(backgroundY + 6 + rectYSeparation + cloudConfigScroll.getScroll());
                    cloudConfigRect.setWidth(configWidth);
                    cloudConfigRect.setHeight(configHeight);
                    cloudConfigRect.setCompact(compactMode.isEnabled());

                    if (cloudConfigRect.getY() + cloudConfigRect.getHeight() > backgroundY && cloudConfigRect.getY() < backgroundY + backgroundHeight) {
                        cloudConfigRect.setClickable(true);
                        cloudConfigRect.drawScreen(mouseX, mouseY);
                    } else {
                        cloudConfigRect.setClickable(false);
                    }


                    rectXSeparation += cloudConfigRect.getWidth() + 12;
                    count++;
                }

                cloudConfigScroll.setMaxScroll(rectYSeparation);

                StencilUtil.uninitStencilBuffer();

                sorting.setWidth(120);
                sorting.setHeight(15);
                sorting.setY(getY() + 5);
                sorting.setX(getX() + getWidth() - (sorting.getWidth() + 8));
                sorting.setAccentColor(HUDMod.getClientColors().getFirst());
                sorting.setAlpha(getAlpha());
                sorting.drawScreen(mouseX, mouseY);


                break;
            case "Local":
                ToggleButton loadVisuals = toggleButtons.get(0);
                loadVisuals.setX(getX() + getWidth() - (loadVisuals.getWH() + 8));
                loadVisuals.setY(carouselButtons.getY() + carouselButtons.getRectHeight() / 2f - loadVisuals.getWH() / 2f);
                loadVisuals.setAlpha(getAlpha());
                loadVisuals.drawScreen(mouseX, mouseY);

                if (Tenacity.INSTANCE.getCloudDataManager().isRefreshing())
                    return;

                //6 spacing on left and right = 12
                //12 + ((12 spacing between configs) * 2 large spaces because we want 3 configs on the top)
                // This equals 36, so we deduct that from the background width and then divide by 3 to get the width of each config
                float localConfigWidth = (backgroundWidth - 36) / 3f;

                float loaclConfigHeight = 38;
                StencilUtil.initStencilToWrite();
                RoundedUtil.drawRound(getX() + spacing, backgroundY, backgroundWidth, backgroundHeight, 5, ColorUtil.tripleColor(27, getAlpha()));
                StencilUtil.readStencilBuffer(1);


                int count2 = 0;
                int rectXSeparation2 = 0;
                int rectYSeparation2 = 0;
                for (LocalConfigRect localConfigRect : localConfigRects) {


                    localConfigRect.setAlpha(getAlpha());
                    localConfigRect.setAccentColor(getAccentColor());
                    //This changes the x and y position for showing 3 configs per line
                    if (count2 > 2) {
                        rectXSeparation2 = 0;
                        rectYSeparation2 += loaclConfigHeight + 12;
                        count2 = 0;
                    }

                    localConfigRect.setX(backgroundX + 6 + rectXSeparation2);
                    localConfigRect.setY(backgroundY + 6 + rectYSeparation2 + localConfigScroll.getScroll());
                    localConfigRect.setWidth(localConfigWidth);
                    localConfigRect.setHeight(loaclConfigHeight);

                    if (localConfigRect.getY() + localConfigRect.getHeight() > backgroundY && localConfigRect.getY() < backgroundY + backgroundHeight) {
                        localConfigRect.setClickable(true);
                        localConfigRect.drawScreen(mouseX, mouseY);
                    } else {
                        localConfigRect.setClickable(false);
                    }


                    rectXSeparation2 += localConfigRect.getWidth() + 12;
                    count2++;
                }

                localConfigScroll.setMaxScroll(rectYSeparation2);

                StencilUtil.uninitStencilBuffer();


                break;
        }

        ConfigManager.loadVisuals = toggleButtons.get(0).isEnabled();

    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        boolean isCloud = carouselButtons.getCurrentButton().equals("Cloud");
        if (isCloud) {
            sorting.mouseClicked(mouseX, mouseY, button);
            if (sorting.isClosed()) {
                toggleButtons.forEach(toggleButton -> toggleButton.mouseClicked(mouseX, mouseY, button));
                compactMode.mouseClicked(mouseX, mouseY, button);
            }

            cloudConfigRects.forEach(cloudConfigRect -> cloudConfigRect.mouseClicked(mouseX, mouseY, button));
        } else {
            localConfigRects.forEach(localConfigRect -> localConfigRect.mouseClicked(mouseX, mouseY, button));
            toggleButtons.get(0).mouseClicked(mouseX, mouseY, button);
        }

        carouselButtons.mouseClicked(mouseX, mouseY, button);
        actionButtons.forEach(button1 -> button1.mouseClicked(mouseX, mouseY, button));
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {

    }


    public void refresh() {
        cloudConfigRects.clear();
        for (CloudConfig cloudConfig : Tenacity.INSTANCE.getCloudDataManager().getCloudConfigs()) {
            cloudConfigRects.add(new CloudConfigRect(cloudConfig));
        }
        Tenacity.INSTANCE.getConfigManager().collectConfigs();

        localConfigRects.clear();
        for (LocalConfig localConfig : ConfigManager.localConfigs) {
            localConfigRects.add(new LocalConfigRect(localConfig));
        }
        localConfigRects.sort(Comparator.<LocalConfigRect>comparingLong(local -> local.getBfa().lastModifiedTime().toMillis()).reversed());

        initGui();
        refresh = false;
    }

    public void sortConfigs() {
        switch (sorting.getSelection()) {
            case "Relevance":
                cloudConfigRects.sort(relevanceSorting());
                break;
            case "Alphabetical":
                cloudConfigRects.sort(alphabeticalSorting());
                break;
            case "Top all time":
                cloudConfigRects.sort(allTimeSorting().reversed());
                break;
            case "Recently updated":
                cloudConfigRects.sort(recentlyUpdatedSorting());
                break;
        }
    }

    public Comparator<CloudConfigRect> recentlyUpdatedSorting() {
        return Comparator.comparingInt(configRect -> configRect.getConfig().minutesSinceLastUpdate());
    }

    public Comparator<CloudConfigRect> allTimeSorting() {
        return Comparator.comparingInt(configRect -> configRect.getConfig().getVotes().getTotalVotes());
    }

    public Comparator<CloudConfigRect> alphabeticalSorting() {
        return Comparator.comparing(cloudConfigRect -> cloudConfigRect.getConfig().getName().toLowerCase());
    }


    public Comparator<CloudConfigRect> relevanceSorting() {
        return Comparator.<CloudConfigRect>comparingInt(configRect -> {
            CloudConfig cloudConfig = configRect.getConfig();
            //The algorithm scores configs based on the scores of these following weights
            int serverSameWeight = 10, recentlyUploadedWeight = 20, versionSameWeight = 35, voteRatioWeight = 35;
            int serverScore = 0, recentlyUploadedScore, versionScore, voteRatioScore;

            //If the server is the same as the user's server then the score is automatically 100
            if (!mc.isSingleplayer() && mc.getCurrentServerData() != null && cloudConfig.getServer().equals(mc.getCurrentServerData().serverIP)) {
                serverScore = 100;
            }


            // We then have an exponential decay for the score of the config based on the time since it was uploaded
            // Decay ends at 15 days since upload at which the score will be 0
            int daysSinceLastUpload = cloudConfig.daysSinceLastUpdate();
            recentlyUploadedScore = Math.max(0, 225 - (daysSinceLastUpload * daysSinceLastUpload));

            //If the current client version does not match then it's automatically a score of 0
            versionScore = cloudConfig.getVersion().equals(Tenacity.VERSION) ? 100 : 0;


            //Then we get the ratio of upvotes to total votes
            Votes votes = cloudConfig.getVotes();
            if (votes != null) {
                int totalVotes = votes.getUpvotes() + votes.getDownvotes();
                voteRatioScore = (votes.getUpvotes() / Math.max(1, votes.getUpvotes() + votes.getDownvotes())) * 100;

                if (totalVotes < 15) {
                    voteRatioScore *= .75;
                }
            }else {
                voteRatioScore = 0;
            }


            //Weighted Grade Formula = (w1 x g1 + w2 x g2 + w3 x g3 + …) / (w1 + w2 + w3 + …)

            int totalWeight = serverSameWeight + recentlyUploadedWeight + versionSameWeight + voteRatioWeight;

            int score = (serverScore * serverSameWeight) + (recentlyUploadedScore * recentlyUploadedWeight) +
                    (versionScore * versionSameWeight) + (voteRatioScore * voteRatioWeight);

            return score / totalWeight;
        }).reversed();
    }


}
