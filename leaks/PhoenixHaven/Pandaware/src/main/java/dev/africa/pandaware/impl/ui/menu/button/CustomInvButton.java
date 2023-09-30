package dev.africa.pandaware.impl.ui.menu.button;

import dev.africa.pandaware.api.interfaces.MinecraftInstance;
import dev.africa.pandaware.impl.font.Fonts;
import dev.africa.pandaware.utils.client.MouseUtils;
import dev.africa.pandaware.utils.math.vector.Vec2i;
import dev.africa.pandaware.utils.render.animator.Animator;
import dev.africa.pandaware.utils.render.animator.Easing;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;

@Setter
@Getter
@RequiredArgsConstructor
public class CustomInvButton implements MinecraftInstance {
    private final String text;
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final boolean soundOnClick;
    private final Runnable onClick;

    private ButtonRenderAddon buttonRenderAddon;

    private boolean hovered;

    private final Animator animator = new Animator()
            .setSpeed(10)
            .setMin(0)
            .setMax(1)
            .setEase(Easing.LINEAR);

    public void render(int mouseX, int mouseY) {
        this.hovered = MouseUtils.isMouseInBounds(new Vec2i(mouseX, mouseY),
                new Vec2i(this.x, this.y), new Vec2i(this.width, this.height));

        this.animator.setReversed(!this.hovered).update();
        int hoverAlpha = (int) (45 * this.animator.getValue());

        if (this.buttonRenderAddon != null) {
            this.buttonRenderAddon.render(this.animator.getValue(), this.hovered);
        }

        int center = this.x + this.width / 2;

        Fonts.getInstance().getArialBdNormal().drawCenteredStringWithShadow(this.text,
                center, this.y + (this.height / 2) - 4, -1);
    }

    public void onClick() {
        if (this.onClick != null && this.hovered) {
            this.onClick.run();

            if (this.soundOnClick) {
                mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
            }
        }
    }

    public CustomInvButton setRenderAddon(ButtonRenderAddon buttonRenderAddon) {
        this.buttonRenderAddon = buttonRenderAddon;
        return this;
    }

    public interface ButtonRenderAddon {
        void render(double animation, boolean hovered);
    }
}