package dev.tenacity.ui.sidegui.panels.configpanel;

import com.google.gson.JsonObject;
import dev.tenacity.Tenacity;
import dev.tenacity.intent.cloud.CloudUtils;
import dev.tenacity.intent.cloud.data.CloudConfig;
import dev.tenacity.ui.Screen;
import dev.tenacity.ui.notifications.NotificationManager;
import dev.tenacity.ui.notifications.NotificationType;
import dev.tenacity.ui.sidegui.SideGUI;
import dev.tenacity.ui.sidegui.forms.Form;
import dev.tenacity.ui.sidegui.forms.impl.EditForm;
import dev.tenacity.ui.sidegui.utils.CloudDataUtils;
import dev.tenacity.ui.sidegui.utils.IconButton;
import dev.tenacity.ui.sidegui.utils.TooltipObject;
import dev.tenacity.ui.sidegui.utils.VoteRect;
import dev.tenacity.utils.animations.Animation;
import dev.tenacity.utils.animations.Direction;
import dev.tenacity.utils.animations.impl.DecelerateAnimation;
import dev.tenacity.utils.font.FontUtil;
import dev.tenacity.utils.misc.IOUtils;
import dev.tenacity.utils.misc.Multithreading;
import dev.tenacity.utils.render.ColorUtil;
import dev.tenacity.utils.render.RoundedUtil;
import lombok.Getter;
import lombok.Setter;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CloudConfigRect implements Screen {
    private float x, y, width, height, alpha;
    private Color accentColor;
    private boolean compact;
    private int searchScore;
    private boolean clickable = true;

    private final VoteRect voteRect;

    private final List<IconButton> iconButtons = new ArrayList<>();
    private final TooltipObject hoverInformation = new TooltipObject();
    private final CloudConfig config;
    private final Animation hoverAnimation = new DecelerateAnimation(250, 1);

    public CloudConfigRect(CloudConfig config) {
        this.config = config;
        voteRect = new VoteRect(config);
        Tenacity.INSTANCE.getSideGui().getTooltips().add(hoverInformation);
        iconButtons.add(new IconButton(FontUtil.LOAD, "Load this config"));
        iconButtons.add(new IconButton(FontUtil.SAVE, "Save this config to your local files"));
        iconButtons.add(new IconButton(FontUtil.EDIT, "Edit this config"));
    }


    @Override
    public void initGui() {
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {

    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {

        Color textColor = ColorUtil.applyOpacity(Color.WHITE, alpha);
        RoundedUtil.drawRound(x, y, width, height, 5, ColorUtil.tripleColor(37, alpha));
        lithiumBoldFont26.drawString(config.getName(), x + 3, y + 3, textColor);

        float yOffset = compact ? 2.5f : 2;

        lithiumFont16.drawString(config.getAuthor(), x + 3, y + yOffset + lithiumBoldFont32.getHeight(), accentColor);

        lithiumFont16.drawString(CloudDataUtils.getLastEditedTime(config.getLastUpdated()),
                x + 5 + lithiumFont16.getStringWidth(config.getAuthor()), y + yOffset + lithiumBoldFont32.getHeight(), ColorUtil.applyOpacity(textColor, .5f));

        boolean hovering = SideGUI.isHovering(x, y, width, height, mouseX, mouseY);
        hoverAnimation.setDirection(hovering ? Direction.FORWARDS : Direction.BACKWARDS);
        hoverAnimation.setDuration(hovering ? 150 : 300);

        if (!compact) {

            lithiumFont16.drawWrappedText(config.getDescription(), x + 3,
                    y + 6 + lithiumBoldFont32.getHeight() + lithiumFont16.getHeight(),
                    ColorUtil.applyOpacity(textColor.getRGB(), .5f + (.5f * hoverAnimation.getOutput().floatValue())), width - 12, 3);
        }


        voteRect.setAlpha(getAlpha());
        voteRect.setX(x + width - (voteRect.getWidth() + 4));
        voteRect.setY(y + 4);
        voteRect.setAccentColor(getAccentColor());
        voteRect.drawScreen(mouseX, mouseY);

        float buttonOffsetX = compact ? 20 : 4;
        float buttonOffsetY = compact ? 3 : 4;

        int seperationX = 0;
        for (IconButton iconButton : iconButtons) {
            iconButton.setX(x + width - (iconButton.getWidth() + buttonOffsetX + seperationX));
            iconButton.setY(y + height - (iconButton.getHeight() + buttonOffsetY));
            iconButton.setAlpha(getAlpha());
            iconButton.setAccentColor(getAccentColor());
            iconButton.setIconFont(iconFont20);


            iconButton.setClickAction(() -> {
                switch (iconButton.getIcon()) {
                    case FontUtil.LOAD:
                        Multithreading.runAsync(() -> {
                            JsonObject loadObject = CloudUtils.getData(config.getShareCode());

                            if (loadObject == null) {
                                NotificationManager.post(NotificationType.WARNING, "Error", "The online config was invalid!");
                                return;
                            }

                            String loadData = loadObject.get("body").getAsString();

                            if (Tenacity.INSTANCE.getConfigManager().loadConfig(loadData, false)) {
                                NotificationManager.post(NotificationType.SUCCESS, "Success", "Config loaded successfully!");
                            } else {
                                NotificationManager.post(NotificationType.WARNING, "Error", "The online config did not load successfully!");
                            }
                        });
                        break;
                    case FontUtil.SAVE:
                        Multithreading.runAsync(() -> {
                            JsonObject saveObject = CloudUtils.getData(config.getShareCode());

                            if (saveObject == null) {
                                NotificationManager.post(NotificationType.WARNING, "Error", "The online config was invalid!");
                                return;
                            }

                            String name = config.getName();
                            String saveData = saveObject.get("body").getAsString();

                            if (Tenacity.INSTANCE.getConfigManager().saveConfig(name, saveData)) {
                                NotificationManager.post(NotificationType.SUCCESS, "Success", "Config saved successfully!");
                            } else {
                                NotificationManager.post(NotificationType.WARNING, "Error", "The config did not save successfully!");
                            }

                            Tenacity.INSTANCE.getSideGui().getTooltips().clear();
                            Tenacity.INSTANCE.getSideGui().getConfigPanel().setRefresh(true);
                        });
                        break;
                    case FontUtil.EDIT:
                        Form form = Tenacity.INSTANCE.getSideGui().displayForm("Edit Config");
                        ((EditForm) form).setup(config, false);
                        form.setUploadAction((fileName, updatedDescription) -> {
                            Multithreading.runAsync(() -> {

                                String data = Tenacity.INSTANCE.getConfigManager().serialize();

                                if (CloudUtils.updateData(config.getShareCode(), updatedDescription, data, false)) {
                                    NotificationManager.post(NotificationType.SUCCESS, "Success", "Config updated successfully!");
                                } else {
                                    NotificationManager.post(NotificationType.DISABLE, "Error", "Error updating config!");
                                }

                                Tenacity.INSTANCE.getCloudDataManager().refreshData();
                            });

                        });
                        break;
                }
            });

            if (iconButton.getIcon().equals(FontUtil.EDIT)) {
                if (config.isOwnership()) {
                    iconButton.drawScreen(mouseX, mouseY);
                } else {
                    iconButton.setClickable(false);
                }
            } else {
                iconButton.drawScreen(mouseX, mouseY);
            }

            seperationX += (iconButton.getWidth() + 7);
        }

        String formatCode = "§a";
        hoverInformation.setTip(formatCode + "Server IP§r: " + config.getServer() + "\n" +
                formatCode + "Client Version§r: " + config.getVersion() + "\n" +
                formatCode + "Share Code§r: " + config.getShareCode() + "\n");

        hoverInformation.setAdditionalInformation(compact ? (formatCode + "Description§r: " + config.getDescription()) : null);

        boolean hoveringInfo = SideGUI.isHovering(getX() + 3, getY() + getHeight() - (lithiumFont14.getHeight() + 3),
                iconFont20.getStringWidth(FontUtil.INFO) + 2 + lithiumFont14.getStringWidth("Hover for more information"),
                lithiumFont14.getHeight() + 3, mouseX, mouseY);

        hoverInformation.setHovering(hoveringInfo);


        Animation hoverAnim = hoverInformation.getFadeInAnimation();
        float additionalAlpha = .65f * hoverAnim.getOutput().floatValue();

        iconFont16.drawString(FontUtil.INFO, getX() + 3, getY() + getHeight() - (iconFont16.getHeight() + 3), ColorUtil.applyOpacity(textColor, .35f + additionalAlpha));


        lithiumFont14.drawString("Hover for more information", getX() + 5 + iconFont16.getStringWidth(FontUtil.INFO),
                getY() + getHeight() - (lithiumFont14.getHeight() + 3), ColorUtil.applyOpacity(textColor, .35f + additionalAlpha));

    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (clickable) {
            if (button == 0 && hoverInformation.isHovering() && Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                IOUtils.copy(config.getShareCode());
                NotificationManager.post(NotificationType.SUCCESS, "Success", "Config share-code copied to clipboard!");
                return;
            }

            voteRect.mouseClicked(mouseX, mouseY, button);
            iconButtons.forEach(iconButton -> iconButton.mouseClicked(mouseX, mouseY, button));
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {

    }


}
