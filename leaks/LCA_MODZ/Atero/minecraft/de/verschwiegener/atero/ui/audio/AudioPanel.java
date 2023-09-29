package de.verschwiegener.atero.ui.audio;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import de.verschwiegener.atero.Management;
import de.verschwiegener.atero.audio.Stream;
import de.verschwiegener.atero.audio.Stream.Provider;
import de.verschwiegener.atero.design.font.Font;
import de.verschwiegener.atero.design.font.Fontrenderer;
import de.verschwiegener.atero.ui.audio.channels.ChannelButton;
import de.verschwiegener.atero.util.Util;
import de.verschwiegener.atero.util.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.MathHelper;

public class AudioPanel extends GuiScreen {

    private int x, y, scrollY, maxScrollY;
    private final int width;
    private final int height;
    private final ArrayList<ProviderButton> providerButtons = new ArrayList<ProviderButton>();
    private final ArrayList<ChannelButton> channelbuttons = new ArrayList<ChannelButton>();
    private final Minecraft mc = Minecraft.getMinecraft();
    private Provider currentProvider = Provider.values()[0];

    private final Font font;
    private final Font titlefont;
    private final Font songFont;
    double slidery;
    private double rotatevalue = 0;
    private int lengthOffset = 0;
    private boolean sliderselected;
    private int switchRotate2 = 0;

    public AudioPanel() {
	x = 100;
	y = 100;
	width = 500;
	height = 300;
	int buttonYOffset = 25;
	font = Management.instance.font;
	titlefont = new Font("AudioTitleFont", Util.getFontByName("Inter-ExtraLight"), 6F);
	songFont = new Font("SongFont", Util.getFontByName("Inter-ExtraLight"), 4.5F);
	for (final Provider provider : Provider.values()) {
	    providerButtons.add(new ProviderButton(provider.toString(), buttonYOffset));
	    buttonYOffset += 20;
	}
	scrollY = 0;
	createChannelButtons();
    }

    public void createChannelButtons() {
	channelbuttons.clear();
	final int maxX = width / 5 * 4;
	final int maxY = height - 30;
	final int widthOffset = width / 5 * 4 / 3;
	final int heightOffset = maxY / 2;
	int currentX = 0;
	int currentY = 0;
	for (final Stream stream : Management.instance.streamManager.getStreams()) {
	    if (stream.getProvider() == getCurrentProvider() && stream.getImage() != null
		    && stream.getChannelURL() != null) {
		channelbuttons.add(new ChannelButton(stream.getChannelName(), currentX, currentY, this, widthOffset,
			heightOffset));
		currentX += Math.round(widthOffset);
		if (currentX + 1 == maxX) {
		    currentX = 0;
		    currentY += maxY / 2;
		    maxScrollY = currentY - (maxY);
		}
	    }
	}
    }

    @Override
    public void handleMouseInput() throws IOException {
	final int scaleFactor = 2;
	// Scrolling
	int wheelD = (Mouse.getEventDWheel() / 10);
	int mouseY = Mouse.getEventY() / scaleFactor - 30;
	int mouseX = Mouse.getEventX() / scaleFactor;
	if (isMouseHovered(mouseX, mouseY)) {
	    scrollY += wheelD;
	    if (scrollY > 0) {
		scrollY = 0;
	    }
	    if (scrollY < -maxScrollY) {
		scrollY = -maxScrollY;
	    }
	    slidery = (maxScrollY / (height - 30)) * scrollY / 9;

	}
	super.handleMouseInput();
    }

    @Override
    public void drawScreen(final int mouseX, final int mouseY, float partialTicks) {
	super.drawScreen(mouseX, mouseY, partialTicks);
	x = mc.displayWidth / 4 - width / 2;
	y = mc.displayHeight / 4 - height / 2;
	Stream currentStream = Management.instance.currentStream;

	// GetSlider Value
	if (sliderselected && isMouseSliderHoveredNoneY(mouseX, mouseY)) {
	    double value = getSliderValue(mouseX, mouseY);
	    Management.instance.streamer.setVolume(value / 10);
	}

	// Main Scissor
	GL11.glEnable(GL11.GL_SCISSOR_TEST);
	final int scaleFactor = 2;
	GL11.glScissor(x * scaleFactor, y * scaleFactor + 1, width * scaleFactor, height * scaleFactor);

	// Draw Main rect
	RenderUtil.fillRect(x, y, width, height, Management.instance.colorBlack);
	RenderUtil.fillRect(x, y, width / 5, height, Management.instance.colorGray);
	titlefont.drawString("Musik", x + (width / 5) / 2 - titlefont.getStringWidth2("Musik"), (y + 3), Color.WHITE.getRGB());

	providerButtons.forEach(button -> button.drawButton(this, x, y));
	channelbuttons.forEach(button -> button.drawButton(this, x + width / 5, y + scrollY));

	// DrawScrollSlider
	RenderUtil.fillRect(x + width - 3, this.y, 2, height, Management.instance.colorGray);
	RenderUtil.fillRect(x + width - 3, this.y + -(slidery) - 20, 2, 40, Management.instance.colorBlue);
	// Draw Buttom Rect
	RenderUtil.fillRect(x, y + height - 30, width, 30, Management.instance.colorGray);

	// Draw Play Pause
	RenderUtil.drawFullCircle(x + ((width - (width / 5)) / 2) + (width / 5), y + (height - 15), 14, true,
		Management.instance.colorBlack);
	
	if(!Management.instance.streamer.isPlaying()) {
	    RenderUtil.drawPlay(x + ((width - (width / 5)) / 2) + (width / 5) - 5, y + (height - 25), 15, 20,
			Management.instance.colorGray);
	}else {
	    RenderUtil.drawPause(x + ((width - (width / 5)) / 2) + (width / 5) - 6, y + (height - 25), 15, 20, Management.instance.colorGray);
	}

	drawSlider();

	// Draw Title and Movement
	if (currentStream != null) {
	    // Rotate ChannelName if length > 30
	    if (currentStream.getFulltitle().length() > 30) {
		if (Minecraft.getMinecraft().getSystemTime() % 2 == 0) {
		    if (rotatevalue >= lengthOffset) {
			switchRotate2 = 1;
		    } else if (rotatevalue < -1) {
			switchRotate2 = 0;
		    }
		    double offset = 0.08;
		    if (switchRotate2 == 1) {
			rotatevalue -= offset;
		    } else {
			rotatevalue += offset;
		    }
		}
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		GL11.glScissor(x * scaleFactor + (width / 5) * 2, y * scaleFactor, 170 * scaleFactor,
			font.getBaseStringHeight() * 4);
		font.drawString(currentStream.getFulltitle(),
			(float) ((x) + (width / 5)
				- (rotatevalue * font.getSpaceWidth())),
			(y + (height)) - 22, Color.WHITE.getRGB());
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
	    } else {
		font.drawString(currentStream.getFulltitle(), (x) + (width / 5),
			(y + (height)) - 22, Color.WHITE.getRGB());
	    }
	}
	GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    private void drawSlider() {
	double percent = Management.instance.streamer.getVolume();
	RenderUtil.fillRect(x + ((width / 5) * 4), y + (height - 15), 75, 1.5D, Management.instance.colorBlack);
	RenderUtil.fillRect(x + ((width / 5) * 4), y + (height - 15), (percent * 10) * 75, 1.5D,
		Management.instance.colorBlue);
    }

    public ProviderButton getButtonByPosition(final AudioPanel audiopanel, final int x, final int y) {
	return providerButtons.stream().filter(module -> x > audiopanel.x && x < audiopanel.x + audiopanel.width / 5
		&& y > audiopanel.y + module.y && y < audiopanel.y + module.y + 12).findFirst().orElse(null);
    }

    private boolean isMouseHovered(int mouseX, int mouseY) {
	return mouseX > (x + (width / 5)) && mouseX < (x + width) && mouseY > y && mouseY < (y + height - 30);
    }

    private boolean isMouseSliderHovered(int mouseX, int mouseY) {
	return mouseX > (x + ((width / 5) * 4) - 1) && mouseX < (x + ((width / 5) * 4) + 76)
		&& mouseY > (y + (height - 16.5D)) && mouseY < y + (height - 13.5D);
    }

    private boolean isMouseSliderHoveredNoneY(int mouseX, int mouseY) {
	return mouseX > (x + ((width / 5) * 4) - 1) && mouseX < (x + ((width / 5) * 4) + 76);
    }
    private boolean isPlayPauseHovered(int mouseX, int mouseY) {
	return mouseX > (x + ((width - (width / 5)) / 2) + (width / 5) - 8) && mouseX < (x + ((width - (width / 5)) / 2) + (width / 5) + 5) &&
		mouseY > (y + (height - 25)) && mouseY < (y + (height - 5));
    }

    private double getSliderValue(int mouseX, int mouseY) {
	double dif = 100D / 75D;
	double x = (mouseX - (this.x + ((width / 5) * 4)));
	return (x * dif) / 100;
    }

    public Provider getCurrentProvider() {
	return currentProvider;
    }

    public int getHeight() {
	return height;
    }

    public Font getSongFont() {
	return songFont;
    }

    public Font getTitlefont() {
	return titlefont;
    }

    public int getWidth() {
	return width;
    }

    public int getX() {
	return x;
    }

    public int getY() {
	return y;
    }

    public void setCurrentStream(Stream currentStream) {
	Management.instance.currentStream = currentStream;
	Management.instance.streamer.updateStream();
	rotatevalue = 0;
	lengthOffset = currentStream.getFulltitle().length() - 26;
    }

    @Override
    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException {
	super.mouseClicked(mouseX, mouseY, mouseButton);
	if (mouseY < (y + height - 30) && mouseY > y) {
	    channelbuttons.forEach(button -> button.onMouseClicked(mouseX, mouseY, mouseButton));
	}
	if (mouseButton == 0) {
	    if (mouseY < (y + height - 30) && mouseY > y) {
		final ProviderButton button = getButtonByPosition(this, mouseX, mouseY);
		if (button != null) {
		    currentProvider = Provider.valueOf(button.name);
		    createChannelButtons();
		}
	    } else {
		if (isMouseSliderHovered(mouseX, mouseY)) {
		    sliderselected = true;
		}else if(isPlayPauseHovered(mouseX, mouseY)) {
		   Management.instance.streamer.toggle();
		}
	    }
	}
    }

    @Override
    protected void mouseReleased(final int mouseX, final int mouseY, final int state) {
	super.mouseReleased(mouseX, mouseY, state);
	sliderselected = false;
    }
}
