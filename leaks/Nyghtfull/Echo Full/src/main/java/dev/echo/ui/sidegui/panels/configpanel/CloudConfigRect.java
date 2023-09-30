package dev.echo.ui.sidegui.panels.configpanel;

import com.google.gson.JsonObject;
import dev.echo.Echo;
import dev.echo.other.intent.cloud.CloudUtils;
import dev.echo.other.intent.cloud.data.CloudConfig;
import dev.echo.ui.Screen;
import dev.echo.ui.notifications.NotificationManager;
import dev.echo.ui.notifications.NotificationType;
import dev.echo.ui.sidegui.SideGUI;
import dev.echo.ui.sidegui.forms.Form;
import dev.echo.ui.sidegui.forms.impl.EditForm;
import dev.echo.ui.sidegui.utils.CloudDataUtils;
import dev.echo.ui.sidegui.utils.IconButton;
import dev.echo.ui.sidegui.utils.TooltipObject;
import dev.echo.ui.sidegui.utils.VoteRect;
import dev.echo.utils.animations.Animation;
import dev.echo.utils.animations.Direction;
import dev.echo.utils.animations.impl.DecelerateAnimation;
import dev.echo.utils.font.FontUtil;
import dev.echo.utils.misc.IOUtils;
import dev.echo.utils.misc.Multithreading;
import dev.echo.utils.render.ColorUtil;
import dev.echo.utils.render.RoundedUtil;
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
        Echo.INSTANCE.getSideGui().getTooltips().add(hoverInformation);
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
        echoBoldFont26.drawString(config.getName(), x + 3, y + 3, textColor);

        float yOffset = compact ? 2.5f : 2;

        echoFont16.drawString(config.getAuthor(), x + 3, y + yOffset + echoBoldFont32.getHeight(), accentColor);

        echoFont16.drawString(CloudDataUtils.getLastEditedTime(config.getLastUpdated()),
                x + 5 + echoFont16.getStringWidth(config.getAuthor()), y + yOffset + echoBoldFont32.getHeight(), ColorUtil.applyOpacity(textColor, .5f));

        boolean hovering = SideGUI.isHovering(x, y, width, height, mouseX, mouseY);
        hoverAnimation.setDirection(hovering ? Direction.FORWARDS : Direction.BACKWARDS);
        hoverAnimation.setDuration(hovering ? 150 : 300);

        if (!compact) {

            echoFont16.drawWrappedText(config.getDescription(), x + 3,
                    y + 6 + echoBoldFont32.getHeight() + echoFont16.getHeight(),
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

                            if (Echo.INSTANCE.getConfigManager().loadConfig(loadData, false)) {
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

                            if (Echo.INSTANCE.getConfigManager().saveConfig(name, saveData)) {
                                NotificationManager.post(NotificationType.SUCCESS, "Success", "Config saved successfully!");
                            } else {
                                NotificationManager.post(NotificationType.WARNING, "Error", "The config did not save successfully!");
                            }

                            Echo.INSTANCE.getSideGui().getTooltips().clear();
                            Echo.INSTANCE.getSideGui().getConfigPanel().setRefresh(true);
                        });
                        break;
                    case FontUtil.EDIT:
                        Form form = Echo.INSTANCE.getSideGui().displayForm("Edit Config");
                        ((EditForm) form).setup(config, false);
                        form.setUploadAction((fileName, updatedDescription) -> {
                            Multithreading.runAsync(() -> {

                                String data = Echo.INSTANCE.getConfigManager().serialize();

                                if (CloudUtils.updateData(config.getShareCode(), updatedDescription, data, false)) {
                                    NotificationManager.post(NotificationType.SUCCESS, "Success", "Config updated successfully!");
                                } else {
                                    NotificationManager.post(NotificationType.DISABLE, "Error", "Error updating config!");
                                }

                                Echo.INSTANCE.getCloudDataManager().refreshData();
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

        boolean hoveringInfo = SideGUI.isHovering(getX() + 3, getY() + getHeight() - (echoFont14.getHeight() + 3),
                iconFont20.getStringWidth(FontUtil.INFO) + 2 + echoFont14.getStringWidth("Hover for more information"),
                echoFont14.getHeight() + 3, mouseX, mouseY);

        hoverInformation.setHovering(hoveringInfo);


        Animation hoverAnim = hoverInformation.getFadeInAnimation();
        float additionalAlpha = .65f * hoverAnim.getOutput().floatValue();

        iconFont16.drawString(FontUtil.INFO, getX() + 3, getY() + getHeight() - (iconFont16.getHeight() + 3), ColorUtil.applyOpacity(textColor, .35f + additionalAlpha));


        echoFont14.drawString("Hover for more information", getX() + 5 + iconFont16.getStringWidth(FontUtil.INFO),
                getY() + getHeight() - (echoFont14.getHeight() + 3), ColorUtil.applyOpacity(textColor, .35f + additionalAlpha));

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
