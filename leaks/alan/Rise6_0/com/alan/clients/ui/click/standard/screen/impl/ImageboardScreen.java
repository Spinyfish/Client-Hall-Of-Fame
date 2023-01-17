package com.alan.clients.ui.click.standard.screen.impl;

import com.alan.clients.ui.click.standard.RiseClickGUI;
import com.alan.clients.ui.click.standard.screen.Screen;
import com.alan.clients.util.gui.ScrollUtil;
import com.alan.clients.util.render.RenderUtil;
import net.kodehawa.lib.imageboards.DefaultImageBoards;
import net.kodehawa.lib.imageboards.ImageBoard;
import net.kodehawa.lib.imageboards.entities.BoardImage;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class ImageboardScreen extends Screen {

    public ScrollUtil scrollUtil = new ScrollUtil();

    private List<BoardImage> urls;
    private List<BufferedImage> images;
    private List<ResourceLocation> cache = new ArrayList<>();

    private boolean attempted, done;

    private static int page = 0;

    @Override
    public void onInit() {
        urls = null;
        images = null;
        cache = new ArrayList<>();

        attempted = done = false;
    }

    @Override
    public void onRender(final int mouseX, final int mouseY, final float partialTicks) {
        scrollUtil.onRender();

        final RiseClickGUI clickGUI = this.getStandardClickGUI();

        if ((urls == null || urls.isEmpty()) && !this.attempted) {
            ImageBoard.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.84 Safari/537.36 OPR/85.0.4341.65");

            DefaultImageBoards.GELBOORU.search(page, 10, "-animated -loli -shota -bestiality -vore -gore -pokemon").async(images -> {
                this.urls = images.stream().filter(image -> !isTooFuckedUp(image)).collect(Collectors.toList());
                this.images = images.stream().map(image -> {
                    try {
                        return ImageIO.read(new URL(image.getURL()));
                    } catch (final Exception ex) {
                        ex.printStackTrace();
                    }

                    return null;
                }).collect(Collectors.toList());

                this.done = true;
            });

            page += 1;

            this.attempted = true;
        }

        final float baseX = (float) (clickGUI.position.x + clickGUI.sidebar.sidebarWidth + 4);
        final float baseY = clickGUI.position.y + 8;

        if (this.done) {
            nunitoNormal.drawString("Results:", baseX, (float) (baseY + scrollUtil.getScroll()), Color.WHITE.getRGB());
        } else if (this.attempted) {
            nunitoNormal.drawString("Fetching...", baseX, (float) (baseY + scrollUtil.getScroll()), Color.WHITE.getRGB());
        }

        if (this.images != null && !this.images.isEmpty()) {
            if (this.cache.isEmpty()) {
                int i = 0;

                for (final BufferedImage image : images) {
                    if (image != null) {
                        this.cache.add(mc.getTextureManager().getDynamicTextureLocation("anime" + i, new DynamicTexture(image)));

                        ++i;
                    }
                }
            }

            final int width = 242;
            final int height = 220;

            int i = 0;

            for (final ResourceLocation location : cache) {
                if (location != null) {
                    RenderUtil.image(location, baseX, (float) (baseY + 14 + scrollUtil.getScroll()) + (height * i) + (i != 0 ? 5 * i : 0), width, height);

                    ++i;
                }
            }

            scrollUtil.setMax(-height * (i - 1));
        }

        if (!done) scrollUtil.setMax(0);
    }

    private boolean isTooFuckedUp(final BoardImage image) {
        return image.getTags().contains("loli")
                || image.getTags().contains("shota")
                || image.getTags().contains("bestiality")
                || image.getTags().contains("gore")
                || image.getTags().contains("vore")
                || image.getTags().contains("animated")
                || image.getTags().contains("pokimon")
                || image.isPending();
    }

    public static Dimension getScaledDimension(final Dimension imgSize, final Dimension boundary) {
        final int original_width = imgSize.width;
        final int original_height = imgSize.height;
        final int bound_width = boundary.width;
        final int bound_height = boundary.height;

        int new_width = original_width;
        int new_height = original_height;

        if (original_width > bound_width) {
            new_width = bound_width;
            new_height = (new_width * original_height) / original_width;
        }

        if (new_height > bound_height) {
            new_height = bound_height;
            new_width = (new_height * original_width) / original_height;
        }

        return new Dimension(new_width, new_height);
    }
}
