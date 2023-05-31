package de.lirium.base.drag;

import de.lirium.Client;
import de.lirium.base.animation.Animation;
import de.lirium.base.animation.Easings;
import de.lirium.impl.module.ModuleFeature;
import de.lirium.util.interfaces.IMinecraft;
import de.lirium.util.math.Vec;
import de.lirium.util.render.FontRenderer;
import de.lirium.util.render.RenderUtil;
import de.lirium.util.render.shader.shaders.RoundedRectOutlineShader;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class Draggable implements IMinecraft {

    private final ModuleFeature module;

    private Animation alphaAnimation = new Animation();
    private Animation alphaAnimationText = new Animation();

    private int smX, smY;
    private int prevX, prevY;

    private float xPosition, yPosition, objectWidth, objectHeight, startX, startY;

    private boolean dragging;

    private String name;

    public ScreenPosition screenPosition;

    public boolean resizeCompatible, didDragPreviously = false;

    private double logDifferenceX, logDifferenceY;
    private boolean didLock;

    private double neededDifferenceFromPositionForNextLog = 50;

    public HashMap<PositionData, Draggable> entry = new HashMap<>();
    private boolean isHoverResizing, isDraggingResizing;
    private int dragMouseX, dragMouseY, dragBarMouseX, dragBarMouseY;

    private int additionalWidth, additionalHeight;

    public Draggable(String name, final ModuleFeature module, float startX, float startY, boolean resizeCompatible) {
        this.startX = startX;
        this.startY = startY;
        this.module = module;
        this.name = name;
        if (screenPosition == null) {
            screenPosition = ScreenPosition.fromAbsolute((int) startX, (int) startY);
        }

        this.resizeCompatible = resizeCompatible;

        adjustBounds(screenPosition);
    }

    public void onResize() {
        if (!didDragPreviously) return;
        this.xPosition = (float) screenPosition.getAbsoluteX();
        this.yPosition = (float) screenPosition.getAbsoluteY();
    }

    public RoundedRectOutlineShader roundedRectOutlineShader = new RoundedRectOutlineShader();
    public FontRenderer font = null;

    public void onDragAndDraw(final int mouseX, final int mouseY) {
        if (font == null) {
            font = Client.INSTANCE.getFontLoader().get("arial", 20);
        }
        final boolean hover = isHovering(mouseX, mouseY, xPosition, yPosition - 4, objectWidth, objectHeight + 4);

        final ScaledResolution sr = new ScaledResolution(mc);

        if (dragging)
            Gui.drawRect(0, 0, sr.getScaledWidth(), sr.getScaledHeight(), Integer.MIN_VALUE);

        if (dragging) {
            DragHandler.dragged = true;
            if (didDragPreviously) {
                screenPosition.setAbsolute(screenPosition.getAbsoluteX() + mouseX - this.prevX, screenPosition.getAbsoluteY() + mouseY - this.prevY);

                adjustBounds(screenPosition);
            } else {
                screenPosition.setAbsolute(mouseX, mouseY);
            }
            this.didDragPreviously = true;
        }
        if (didDragPreviously) {
            this.prevX = mouseX;
            this.prevY = mouseY;
        }
        this.smX = mouseX;
        this.smY = mouseY;

        if (didDragPreviously) {
            this.xPosition = (float) screenPosition.getAbsoluteX();
            this.yPosition = (float) screenPosition.getAbsoluteY();
        }

        alphaAnimation.update();
        alphaAnimation.animate(hover ? 220 : 0, .2, Easings.BOUNCE_IN);

        alphaAnimationText.update();
        alphaAnimationText.animate(hover ? 255 : 0, .2, Easings.BACK_IN4);

        this.isHoverResizing = resizeCompatible && isHovering(mouseX, mouseY, xPosition - 5 + objectWidth, yPosition - 5 + objectHeight, 22, 22);

        if (alphaAnimation.getValue() > 0)
            roundedRectOutlineShader.drawRound(xPosition, yPosition, objectWidth, objectHeight + 4, 3, 2, new Color(255, 255, 255, (int) alphaAnimation.getValue()));
        if (alphaAnimationText.getValue() > 62 && !dragging) {
            font.drawString(name, xPosition + 4, yPosition + objectHeight + 8, new Color(255, 255, 255, (int) alphaAnimationText.getValue()).getRGB());
        }


        for (Map.Entry<String, Draggable> entry : DragHandler.draggableHashMap.entrySet()) {
            final Draggable draggable = entry.getValue();

            if (dragging && (draggable.module == null || draggable.module.isEnabled()) && draggable != this) {

                final double diffLeft = Math.abs(draggable.xPosition - xPosition);
                final double diffRight = Math.abs((draggable.xPosition + draggable.objectWidth) - (xPosition + objectWidth));

                final int maxDifference = 1;

                if (diffLeft < maxDifference) { //Left from rect
                    final Vec myVector = new Vec(xPosition, yPosition, 0), theirVector = new Vec(draggable.xPosition, draggable.yPosition, 0);
                    RenderUtil.line(myVector, theirVector, Color.WHITE.getRGB());
                }

                if (diffRight < maxDifference) { // Right from vec
                    final Vec myVector = new Vec(xPosition + objectWidth, yPosition, 0), theirVector = new Vec(draggable.xPosition + draggable.objectWidth, draggable.yPosition, 0);
                    RenderUtil.line(myVector, theirVector, Color.WHITE.getRGB());
                }

                final double middleDifferenceY = (draggable.xPosition + draggable.objectWidth / 2) - (xPosition + objectWidth / 2);

                final double middleDifferenceABSY = Math.abs(middleDifferenceY);

                if (middleDifferenceABSY < maxDifference) { //Y Middle
                    final Vec myVector = new Vec(xPosition + objectWidth / 2, yPosition, 0), theirVector = new Vec(draggable.xPosition + draggable.objectWidth / 2, draggable.yPosition, 0);
                    RenderUtil.line(myVector, theirVector, Color.WHITE.getRGB());
                }

                final double middleDifferenceX = (draggable.yPosition + draggable.objectHeight / 2) - (yPosition + objectHeight / 2);

                final double middleDifferenceXABS = Math.abs(middleDifferenceX);

                if (middleDifferenceXABS < 2) {
                    final Vec myVector = new Vec(xPosition, yPosition + objectHeight / 2, 0), theirVector = new Vec(draggable.xPosition, draggable.yPosition + draggable.objectHeight / 2, 0);
                    RenderUtil.line(myVector, theirVector, Color.WHITE.getRGB());
                }

            }
        }

        final double middleDifference = (sr.getScaledWidth() / 2F) - (xPosition + objectWidth / 2);

        //todo recode ist ja eklig was ich hier geschrieben habe
        if (dragging) {
            if (Math.abs(middleDifference) <= 2) {
                if ((Math.abs(logDifferenceX - mouseX) <= 20 || logDifferenceX == 0 || logDifferenceY == 0)) {
                    setXPosition((sr.getScaledWidth() / 2F) - (xPosition + objectWidth / 2));
                    dragging = false;
                    this.didLock = true;
                }
            } else {
                didLock = false;
            }
        }
        if (didLock) {
            this.logDifferenceX = xPosition;
            this.logDifferenceY = yPosition;
        } else {
            this.logDifferenceX = 0;
            this.logDifferenceY = 0;
        }
    }


    public final void onRelease(int button) {
        if (button == 0) isDraggingResizing = false;
        if (button == 0) dragging = false;
    }

    public void onClick(int mouseX, int mouseY, int button) {
        boolean canDrag = isHovering(mouseX, mouseY, xPosition, yPosition - 4, objectWidth, objectHeight + 4);

        this.isDraggingResizing = isHoverResizing && button == 0;

        if (button == 0 && canDrag) {
            this.prevX = mouseX;
            this.prevY = mouseY;

            dragging = true;
        }
    }
    private void adjustBounds(ScreenPosition pos) {

        ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());

        int screenWidth = res.getScaledWidth();
        int screenHeight = res.getScaledHeight();

        int absoluteX = (int) Math.max(0, Math.min(pos.getAbsoluteX(), Math.max(screenWidth - objectWidth, 0)));
        int absoluteY = (int) Math.max(0, Math.min(pos.getAbsoluteY(), Math.max(screenHeight - objectHeight, 0)));

        pos.setAbsolute(absoluteX, absoluteY);
    }

    public boolean isHovering(int mouseX, int mouseY, float x, float y, float width, float height) {
        return mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + height;
    }

    public static class PositionData {
        int x, y;

        public PositionData(final int x, final int y) {
            this.x = x;
            this.y = y;
        }

    }

}
