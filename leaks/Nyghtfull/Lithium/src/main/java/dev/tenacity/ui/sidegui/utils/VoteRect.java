package dev.tenacity.ui.sidegui.utils;

import dev.tenacity.Tenacity;
import dev.tenacity.intent.cloud.CloudUtils;
import dev.tenacity.intent.cloud.data.CloudData;
import dev.tenacity.intent.cloud.data.VoteType;
import dev.tenacity.ui.Screen;
import dev.tenacity.utils.animations.Animation;
import dev.tenacity.utils.animations.impl.DecelerateAnimation;
import dev.tenacity.utils.font.FontUtil;
import dev.tenacity.utils.misc.Multithreading;
import dev.tenacity.utils.render.ColorUtil;
import dev.tenacity.utils.render.RenderUtil;
import dev.tenacity.utils.render.RoundedUtil;
import dev.tenacity.utils.time.TimerUtil;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;

@Setter
@Getter
public class VoteRect implements Screen {

    private final TimerUtil timer = new TimerUtil();
    private final CloudData cloudData;

    private float x, y, width, height, alpha;
    private Color accentColor;

    private final IconButton upvoteButton = new IconButton(FontUtil.UPVOTE_OUTLINE),
            downvoteButton = new IconButton(FontUtil.DOWNVOTE_OUTLINE);

    public VoteRect(CloudData cloudData) {
        this.cloudData = cloudData;
    }

    @Override
    public void initGui() {
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        if(cloudData.getVotes() == null) return;
        int totalVotes = cloudData.getVotes().getTotalVotes();
        width = 11 + ((totalVotes >= 100 || totalVotes <= -100) ? 5 : 0) + ((totalVotes >= 1000 || totalVotes <= -1000) ? 5 : 0);
        height = 30;
        RoundedUtil.drawRound(x, y, width, height, 4, ColorUtil.tripleColor(27, alpha));
        Color defaultTextColor = new Color(191, 191, 191);

        Color greenColor = Tenacity.INSTANCE.getSideGui().getGreenEnabledColor();
        Color redColor = new Color(209, 56, 56);

        upvoteButton.setX(x + width / 2f - (upvoteButton.getWidth() / 2f));
        upvoteButton.setY(y + 4);
        upvoteButton.setAlpha(alpha);
        upvoteButton.setAccentColor(greenColor);
        upvoteButton.setClickAction(() -> {
            if (timer.hasTimeElapsed(300, true)) {
                Multithreading.runAsync(() -> CloudUtils.vote(VoteType.UP, cloudData));
            }
        });
        upvoteButton.setIcon(cloudData.isUpvoted() ? FontUtil.UPVOTE : FontUtil.UPVOTE_OUTLINE);
        upvoteButton.setTextColor(cloudData.isUpvoted() ? greenColor : defaultTextColor);
        upvoteButton.drawScreen(mouseX, mouseY);

        downvoteButton.setX(x + width / 2f - (downvoteButton.getWidth() / 2f));
        downvoteButton.setY(y + height - (downvoteButton.getHeight() + 3));
        downvoteButton.setAlpha(alpha);
        downvoteButton.setAccentColor(redColor);
        downvoteButton.setClickAction(() -> {
            if (timer.hasTimeElapsed(300, true)) {
                Multithreading.runAsync(() -> CloudUtils.vote(VoteType.DOWN, cloudData));
            }
        });
        downvoteButton.setIcon(cloudData.isDownvoted() ? FontUtil.DOWNVOTE : FontUtil.DOWNVOTE_OUTLINE);
        downvoteButton.setTextColor(cloudData.isDownvoted() ? redColor : defaultTextColor);
        downvoteButton.drawScreen(mouseX, mouseY);

        Color voteColor = Color.WHITE;
        if (cloudData.isUpvoted()) {
            voteColor = greenColor;
        } else if (cloudData.isDownvoted()) {
            voteColor = redColor;
        }

        RenderUtil.scissorStart(x, y + height / 2f - (lithiumBoldFont16.getHeight() / 2f + 1.5), width, lithiumBoldFont16.getHeight() + 3);
        drawAnimatedVote(totalVotes, voteColor);
        RenderUtil.scissorEnd();

        //      lithiumBoldFont16.drawCenteredString(s, x + width / 2f, y + lithiumBoldFont16.getMiddleOfBox(height), ColorUtil.applyOpacity(voteColor, alpha));
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(cloudData.getVotes() == null) return;
        upvoteButton.mouseClicked(mouseX, mouseY, button);
        downvoteButton.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
    }


    private int lastVote;

    private Animation voteAnimation;

    public void drawAnimatedVote(int vote, Color color) {
        if (lastVote != vote) {
            int additionalVotes = vote - lastVote;

            if (additionalVotes > 50 || additionalVotes < -50) {
                lastVote = vote;
                return;
            }

            if (voteAnimation == null) {
                voteAnimation = new DecelerateAnimation(Math.min(500, 200 * Math.abs(additionalVotes)),
                        additionalVotes * (lithiumBoldFont16.getHeight() + 3));
            }

            float animation = voteAnimation.getOutput().floatValue();

            int count = 0;
            if (additionalVotes > 0) {

                for (int i = lastVote; i < (vote + 1); i++) {
                    int additionalY = count * (lithiumBoldFont16.getHeight() + 3);

                    float voteY = y + lithiumBoldFont16.getMiddleOfBox(height) + additionalY;
                    lithiumBoldFont16.drawCenteredString(String.valueOf(i), x + width / 2f, voteY - (animation), ColorUtil.applyOpacity(color, alpha));
                    count++;
                }

            } else {
                for (int i = lastVote; i > (vote - 1); i--) {
                    int additionalY = count * (lithiumBoldFont16.getHeight() + 3);
                    float voteY = y + lithiumBoldFont16.getMiddleOfBox(height) - additionalY;
                    lithiumBoldFont16.drawCenteredString(String.valueOf(i), x + width / 2f, voteY - animation, ColorUtil.applyOpacity(color, alpha));
                    count++;
                }
            }


            if (voteAnimation.isDone()) {
                lastVote = vote;
                voteAnimation = null;
            }
        } else {
            lithiumBoldFont16.drawCenteredString(String.valueOf(vote), x + width / 2f,
                    y + lithiumBoldFont16.getMiddleOfBox(height), ColorUtil.applyOpacity(color, alpha));
        }


    }


}
