package dev.tenacity.ui.sidegui.panels.scriptpanel;

import com.google.gson.JsonObject;
import dev.tenacity.Tenacity;
import dev.tenacity.intent.cloud.CloudUtils;
import dev.tenacity.intent.cloud.data.CloudScript;
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
import dev.tenacity.utils.misc.FileUtils;
import dev.tenacity.utils.misc.IOUtils;
import dev.tenacity.utils.misc.Multithreading;
import dev.tenacity.utils.render.ColorUtil;
import dev.tenacity.utils.render.RoundedUtil;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CloudScriptRect implements Screen {
    private float x, y, width, height, alpha;
    private Color accentColor;
    private boolean compact;
    private int searchScore;
    private boolean clickable = true;

    private final VoteRect voteRect;

    private final List<IconButton> iconButtons = new ArrayList<>();
    private final TooltipObject hoverInformation = new TooltipObject();
    private final CloudScript script;
    private final Animation hoverAnimation = new DecelerateAnimation(250, 1);


    public CloudScriptRect(CloudScript script) {
        this.script = script;
        voteRect = new VoteRect(script);
        Tenacity.INSTANCE.getSideGui().getTooltips().add(hoverInformation);
        iconButtons.add(new IconButton(FontUtil.ADD_FILE, "Download and add this script to local scripts"));
        iconButtons.add(new IconButton(FontUtil.EDIT, "Edit this script"));
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
        lithiumBoldFont26.drawString(script.getName(), x + 3, y + 3, textColor);

        float yOffset = compact ? 2.5f : 2;

        lithiumFont16.drawString(script.getAuthor(), x + 3, y + yOffset + lithiumBoldFont32.getHeight(), accentColor);

        lithiumFont16.drawString(CloudDataUtils.getLastEditedTime(script.getLastUpdated()),
                x + 5 + lithiumFont16.getStringWidth(script.getAuthor()), y + yOffset + lithiumBoldFont32.getHeight(), ColorUtil.applyOpacity(textColor, .5f));

        boolean hovering = SideGUI.isHovering(x, y, width, height, mouseX, mouseY);
        hoverAnimation.setDirection(hovering ? Direction.FORWARDS : Direction.BACKWARDS);
        hoverAnimation.setDuration(hovering ? 150 : 300);

        if (!compact) {
            lithiumFont16.drawWrappedText(script.getDescription(), x + 3,
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

        int seperation = 0;
        for (IconButton iconButton : iconButtons) {
            iconButton.setX(x + width - (iconButton.getWidth() + buttonOffsetX + seperation));
            iconButton.setY(y + getHeight() - (iconButton.getHeight() + buttonOffsetY));
            iconButton.setAlpha(getAlpha());
            iconButton.setIconFont(iconFont20);
            iconButton.setAccentColor(getAccentColor());

            iconButton.setClickAction(() -> {
                switch (iconButton.getIcon()) {
                    case FontUtil.ADD_FILE:
                        JsonObject object = CloudUtils.getData(script.getShareCode());

                        if (object == null) {
                            NotificationManager.post(NotificationType.WARNING, "Error", "The online script was invalid!");
                            return;
                        }

                        String name = script.getName();
                        String scriptContent = object.get("body").getAsString();
                        File scriptFile = new File(Minecraft.getMinecraft().mcDataDir + "/Tenacity/Scripts/" + name + ".js");

                        Multithreading.runAsync(() -> downloadScriptToFile(scriptFile, scriptContent));

                        Tenacity.INSTANCE.getSideGui().getTooltips().clear();
                        break;
                    case FontUtil.EDIT:
                        Form form = Tenacity.INSTANCE.getSideGui().displayForm("Edit Script");
                        ((EditForm) form).setup(script, true);
                        form.setUploadAction((fileName, updatedDescription) -> {
                            Multithreading.runAsync(() -> {
                                Tenacity.INSTANCE.getCloudDataManager().refreshData();
                            });

                        });
                        break;
                }
            });


            if (iconButton.getIcon().equals(FontUtil.EDIT)) {
                if (script.isOwnership()) {
                    iconButton.drawScreen(mouseX, mouseY);
                } else {
                    iconButton.setClickable(false);
                }
            } else {
                iconButton.drawScreen(mouseX, mouseY);
            }


            seperation += iconButton.getWidth() + 8;
        }


        String formatCode = "§a";
        hoverInformation.setTip(formatCode + "Share Code§r: " + script.getShareCode() + "\n");

        hoverInformation.setAdditionalInformation(compact ? (formatCode + "Description§r: " + script.getDescription()) : null);

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
            if(button == 0 && hoverInformation.isHovering() && Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                IOUtils.copy(script.getShareCode());
                NotificationManager.post(NotificationType.SUCCESS, "Success", "Script share-code copied to clipboard!");
                return;
            }
            voteRect.mouseClicked(mouseX, mouseY, button);
            iconButtons.forEach(iconButton -> iconButton.mouseClicked(mouseX, mouseY, button));
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {

    }


    public void downloadScriptToFile(File file, String content) {
        try {
            Files.write(file.toPath(), content.getBytes(StandardCharsets.UTF_8));
            NotificationManager.post(NotificationType.SUCCESS, "Success", "Script downloaded to " + file.getPath(), 7);
        } catch (IOException e) {
            e.printStackTrace();
            NotificationManager.post(NotificationType.DISABLE, "Error", "Could not download script to " + file.getAbsolutePath(), 7);
        }
    }


}
