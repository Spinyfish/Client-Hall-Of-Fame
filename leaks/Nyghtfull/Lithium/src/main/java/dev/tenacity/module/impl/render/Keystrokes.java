package dev.tenacity.module.impl.render;

import dev.tenacity.Tenacity;
import dev.tenacity.event.impl.render.Render2DEvent;
import dev.tenacity.event.impl.render.ShaderEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.settings.impl.NumberSetting;
import dev.tenacity.utils.animations.Animation;
import dev.tenacity.utils.animations.Direction;
import dev.tenacity.utils.animations.impl.DecelerateAnimation;
import dev.tenacity.utils.font.AbstractFontRenderer;
import dev.tenacity.utils.font.CustomFont;
import dev.tenacity.utils.objects.Dragging;
import dev.tenacity.utils.render.ColorUtil;
import dev.tenacity.utils.render.RenderUtil;
import dev.tenacity.utils.render.RoundedUtil;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

import java.awt.*;

/**
 * @author cedo
 * @since 03/21/2022
 */
public class Keystrokes extends Module {

    private final NumberSetting offsetValue = new NumberSetting("Offset", 3, 10, 2.5, .5);
    private final NumberSetting sizeValue = new NumberSetting("Size", 25, 35, 15, 1);
    private static final NumberSetting opacity = new NumberSetting("Opacity", .5, 1, 0, .05);
    private static final NumberSetting radius = new NumberSetting("Radius", 3, 17.5, 1, .5);

    public Keystrokes() {
        super("Keystrokes", Category.RENDER, "Shows keystrokes");
        addSettings(sizeValue, offsetValue, opacity, radius);
    }

    private final Dragging dragging = Tenacity.INSTANCE.createDrag(this, "keystrokes", 10, 300);

    private Button keyBindForward;
    private Button keyBindLeft;
    private Button keyBindBack;
    private Button keyBindRight;
    private Button keyBindJump;

    @Override
    public void onShaderEvent(ShaderEvent e) {
        if (keyBindForward == null) return;
        float offset = offsetValue.getValue().floatValue();
        float x = dragging.getX(), y = dragging.getY(), width = dragging.getWidth(), height = dragging.getHeight(), size = sizeValue.getValue().floatValue();

        float increment = size + offset;
        keyBindForward.renderForEffects(x + width / 2f - size / 2f, y, size, e);
        keyBindLeft.renderForEffects(x, y + increment, size, e);
        keyBindBack.renderForEffects(x + increment, y + increment, size, e);
        keyBindRight.renderForEffects(x + (increment * 2), y + increment, size, e);
        keyBindJump.renderForEffects(x, y + increment * 2, width, size, e);
    }

    @Override
    public void onRender2DEvent(Render2DEvent e) {
        float offset = offsetValue.getValue().floatValue();
        dragging.setHeight((float) ((sizeValue.getValue() + offset) * 3) - offset);
        dragging.setWidth((float) ((sizeValue.getValue() + offset) * 3) - offset);

        if (keyBindForward == null) {
            keyBindForward = new Button(mc.gameSettings.keyBindForward);
            keyBindLeft = new Button(mc.gameSettings.keyBindLeft);
            keyBindBack = new Button(mc.gameSettings.keyBindBack);
            keyBindRight = new Button(mc.gameSettings.keyBindRight);
            keyBindJump = new Button(mc.gameSettings.keyBindJump);
        }

        float x = dragging.getX(), y = dragging.getY(), width = dragging.getWidth(), height = dragging.getHeight(), size = sizeValue.getValue().floatValue();

        if (HUDMod.customFont.isEnabled()) {
            Button.font = lithiumFont22;
        } else {
            Button.font = mc.fontRendererObj;
        }

        float increment = size + offset;
        keyBindForward.render(x + width / 2f - size / 2f, y, size);
        keyBindLeft.render(x, y + increment, size);
        keyBindBack.render(x + increment, y + increment, size);
        keyBindRight.render(x + (increment * 2), y + increment, size);
        keyBindJump.render(x, y + increment * 2, width, size);
    }


    public static class Button {

        private static AbstractFontRenderer font;
        private final KeyBinding binding;
        private final Animation clickAnimation = new DecelerateAnimation(125, 1);

        public Button(KeyBinding binding) {
            this.binding = binding;
        }

        public void renderForEffects(float x, float y, float size, ShaderEvent event) {
            renderForEffects(x, y, size, size, event);
        }

        public void renderForEffects(float x, float y, float width, float height, ShaderEvent event) {
            Color color = Color.BLACK;
            RoundedUtil.drawRound(x, y, width, height, radius.getValue().floatValue(), color);
        }

        public void render(float x, float y, float size) {
            render(x, y, size, size);
        }

        public void render(float x, float y, float width, float height) {
            Color color = ColorUtil.applyOpacity(Color.BLACK, opacity.getValue().floatValue());
            clickAnimation.setDirection(binding.isKeyDown() ? Direction.FORWARDS : Direction.BACKWARDS);

            RoundedUtil.drawRound(x, y, width, height, radius.getValue().floatValue(), color);
            float offsetX = font instanceof CustomFont ? 0 : .5f;
            int offsetY = font instanceof CustomFont ? 0 : 1;

            font.drawCenteredString(Keyboard.getKeyName(binding.getKeyCode()), x + width / 2 + offsetX, y + height / 2 - font.getHeight() / 2f + offsetY, Color.WHITE);

            if (!clickAnimation.finished(Direction.BACKWARDS)) {
                float animation = (float) clickAnimation.getOutput().floatValue();
                Color color2 = ColorUtil.applyOpacity(Color.WHITE, (0.5f * animation));
                RenderUtil.scaleStart(x + width / 2f, y + height / 2f, animation);
                float diff = (height / 2f) - radius.getValue().floatValue();
                RoundedUtil.drawRound(x, y, width, height, ((height / 2f) - (diff * animation)), color2);
                RenderUtil.scaleEnd();
            }


        }


    }


}
