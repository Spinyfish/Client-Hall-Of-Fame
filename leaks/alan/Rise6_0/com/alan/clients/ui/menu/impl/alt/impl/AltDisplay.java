package com.alan.clients.ui.menu.impl.alt.impl;

import com.alan.clients.ui.menu.component.MenuComponent;
import com.alan.clients.util.MouseUtil;
import com.alan.clients.util.SkinUtil;
import com.alan.clients.util.account.Account;
import com.alan.clients.util.animation.Animation;
import com.alan.clients.util.animation.Easing;
import com.alan.clients.util.font.Font;
import com.alan.clients.util.font.FontManager;
import com.alan.clients.util.font.impl.rise.FontRenderer;
import com.alan.clients.util.render.ColorUtil;
import com.alan.clients.util.render.RenderUtil;
import com.alan.clients.util.render.StencilUtil;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class AltDisplay extends MenuComponent {

    private static final ResourceLocation CLOSE_RESOURCE = new ResourceLocation("rise/icons/close.png");

    private final Account account;
    private final Animation hoverAnimation = new Animation(Easing.EASE_OUT_SINE, 250);
    private final Animation selectedAnimation = new Animation(Easing.EASE_OUT_SINE, 500);

    private boolean selected;

    public AltDisplay(double x, double y, double width, double height, Account account) {
        super(x, y, width, height);
        this.account = account;
    }

    /**
     * Displays the account information at the given position.
     *
     * @param offset The offset from the top of the screen.
     * @param mouseX The mouse's x position.
     * @param mouseY The mouse's y position.
     */
    public boolean draw(final double offset, final int mouseX, final int mouseY) {

        // TODO: Add check if should be drawn - if not, false is returned and we can break the drawing loop
        if (this.getY() + offset + this.getHeight() < 0 || this.getY() + offset > mc.displayHeight) {
            return false;
        }


        NORMAL_RENDER_RUNNABLES.add(() -> {

            if (this.account == null || this.account.getUsername() == null || this.account.getEmail() == null) {
                return;
            }

            // Update animations
            this.hoverAnimation.run(MouseUtil.isHovered(this.getX(), this.getY() + offset, this.getWidth(), this.getHeight(), mouseX, mouseY) ? 50 : 0);
            this.selectedAnimation.run(this.selected ? this.getWidth() : 0.0);

            // Colors for rendering
            final Font fontRenderer = FontManager.getProductSansRegular(20);
            final Color fontColor = ColorUtil.withAlpha(Color.WHITE, (int) (200 + this.hoverAnimation.getValue()));

            // Render background
            RenderUtil.roundedRectangle(this.getX(), this.getY() + offset, this.getWidth(), this.getHeight(), 5, ColorUtil.withAlpha(Color.WHITE, 30 + (int) this.hoverAnimation.getValue() / 10));

            // Renders if account is in use
            if (this.selectedAnimation.getValue() > 0.0) {
                int alpha = (int) ((Math.sin(System.currentTimeMillis() * 0.005) * -0.5 + 0.5) * 150);
                int alpha1 = (int) ((Math.cos(System.currentTimeMillis() * 0.005) * -0.5 + 0.5) * 150);
                RenderUtil.drawRoundedGradientRect(this.getX(), this.getY() + offset, this.selectedAnimation.getValue(), this.getHeight(), 5, new Color(0, 255, 0, alpha1), new Color(0, 255, 0, alpha), false);
            }

            // Renders information
            renderHead(this.getX() + 8, this.getY() + offset + 4, 24);
            fontRenderer.drawStringWithShadow(this.account.getEmail(), this.getX() + 40, this.getY() + offset + this.getHeight() / 2.0F - 10, fontColor.getRGB());
            fontRenderer.drawStringWithShadow(this.account.getPassword().replaceAll(".", "*"), this.getX() + 40, this.getY() + offset + this.getHeight() / 2.0F + 2, fontColor.getRGB());

            // Renders delete button
            int size = 12;
            double deleteX = this.getX() + this.getWidth() - size - 4;
            double deleteY = this.getY() + offset + 4;
            RenderUtil.image(CLOSE_RESOURCE, deleteX, deleteY, size, size, fontColor);
        });

        return true;
    }

    private void renderHead(final double x, final double y, final int size) {
        if (account.getUuid() == null) return;

        StencilUtil.initStencil();
        StencilUtil.bindWriteStencilBuffer();
        RenderUtil.roundedRectangle(x, y, size, size, 5, this.getTheme().getBackgroundShade());
        StencilUtil.bindReadStencilBuffer(1);
        RenderUtil.image(SkinUtil.getResourceLocation(SkinUtil.SkinType.SKIN, account.getUuid(), 24), x, y, size, size, ColorUtil.withAlpha(Color.WHITE, (int) (200 + this.hoverAnimation.getValue())));
        StencilUtil.uninitStencilBuffer();
    }

    public Account getAccount() {
        return account;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }
}
