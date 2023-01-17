package com.alan.clients.module.impl.render.interfaces;

import com.alan.clients.Client;
import com.alan.clients.component.impl.render.ParticleComponent;
import com.alan.clients.module.impl.render.Interface;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.other.KillEvent;
import com.alan.clients.newevent.impl.other.TickEvent;
import com.alan.clients.newevent.impl.render.Render2DEvent;
import com.alan.clients.util.font.Font;
import com.alan.clients.util.vector.Vector2d;
import com.alan.clients.value.impl.BooleanValue;
import com.alan.clients.value.Mode;
import com.alan.clients.util.font.FontManager;
import com.alan.clients.util.render.ColorUtil;
import com.alan.clients.util.render.RenderUtil;
import com.alan.clients.util.render.particle.Particle;
import util.time.StopWatch;
import com.alan.clients.util.vector.Vector2f;
import com.alan.clients.value.impl.ModeValue;
import com.alan.clients.value.impl.SubMode;
import net.minecraft.client.gui.GuiChat;

import java.awt.*;

public class ModernInterface extends Mode<Interface> {

    private final Font productSansMedium36 = FontManager.getProductSansMedium(36);
    private final Font productSansRegular = FontManager.getProductSansRegular(18);
    private final Font minecraft = FontManager.getMinecraft();

    private Font arrayListFont = productSansRegular;
    private final Font productSansMedium18 = FontManager.getProductSansMedium(18);
    private final StopWatch stopWatch = new StopWatch();

    private final ModeValue colorMode = new ModeValue("ArrayList Color Mode", this) {{
        add(new SubMode("Static"));
        add(new SubMode("Fade"));
        add(new SubMode("Breathe"));
        setDefault("Fade");
    }};

    private final ModeValue font = new ModeValue("ArrayList Font", this) {{
        add(new SubMode("Product Sans"));
        add(new SubMode("Minecraft"));
        setDefault("Product Sans");
    }};

    private final BooleanValue sidebar = new BooleanValue("Sidebar", this, true);
    private final BooleanValue particles = new BooleanValue("Particles on Kill", this, true);
    private final BooleanValue background = new BooleanValue("Background", this, true);

    private boolean glow;
    private String username, coordinates;
    private float nameWidth, userWidth, xyzWidth;

    public ModernInterface(String name, Interface parent) {
        super(name, parent);
    }

    @EventLink()
    public final Listener<Render2DEvent> onRender2D = event -> {

        if (mc == null || mc.gameSettings.showDebugInfo || mc.theWorld == null || mc.thePlayer == null) {
            return;
        }

        this.getParent().setModuleSpacing(this.productSansRegular.height());
        this.getParent().setWidthComparator(this.arrayListFont);
        this.getParent().setEdgeOffset(10);

        // modules in the top right corner of the screen
        for (final ModuleComponent moduleComponent : this.getParent().getActiveModuleComponents()) {
            if (moduleComponent.animationTime == 0) {
                continue;
            }

            String name = (this.getParent().lowercase.getValue() ? moduleComponent.getModule().getModuleInfo().name().toLowerCase() :
                    moduleComponent.getModule().getModuleInfo().name()).replace(getParent().getRemoveSpaces().getValue() ? " " : "", "");
            String tag = (this.getParent().lowercase.getValue() ? moduleComponent.getTag().toLowerCase() : moduleComponent.getTag())
                    .replace(getParent().getRemoveSpaces().getValue() ? " " : "", "");

            final double x = moduleComponent.getPosition().getX();
            final double y = moduleComponent.getPosition().getY();
            final Color finalColor = moduleComponent.getColor();

            if (this.background.getValue()) {

                final double widthOffset = arrayListFont == minecraft ? 3.5 : 2;

                Runnable backgroundRunnable = () ->
                        RenderUtil.rectangle(x + 0.5 - widthOffset, y - 2.5,
                                (moduleComponent.nameWidth + moduleComponent.tagWidth) + 2 + widthOffset,
                                this.getParent().moduleSpacing, getTheme().getBackgroundShade());

                NORMAL_RENDER_RUNNABLES.add(backgroundRunnable);
                NORMAL_BLUR_RUNNABLES.add(() ->
                        RenderUtil.rectangle(x + 0.5 - widthOffset, y - 2.5,
                                (moduleComponent.nameWidth + moduleComponent.tagWidth) + 2 + widthOffset,
                                this.getParent().moduleSpacing, Color.BLACK));

                // Draw the glow/shadow around the module
                NORMAL_POST_BLOOM_RUNNABLES.add(() -> {
                    if (!this.getParent().getShader().getValue().getName().equals("None")) {

                        RenderUtil.rectangle(x + 0.5 - widthOffset, y - 2.5,
                                (moduleComponent.nameWidth + moduleComponent.tagWidth) + 2 + widthOffset,
                                this.getParent().moduleSpacing, glow ? ColorUtil.withAlpha(finalColor, 164) : getTheme().getDropShadow());
                    }
                });
            }

            final boolean hasTag = !moduleComponent.getTag().isEmpty() && this.getParent().suffix.getValue();

            Runnable textRunnable = () -> {
                arrayListFont.drawString(name, x, y, finalColor.getRGB());

                if (hasTag) {
                    arrayListFont.drawString(tag, x + moduleComponent.getNameWidth() + 3, y, 0xFFCCCCCC);
                }
            };

            Runnable shadowRunnable = () -> {
                arrayListFont.drawString(name, x, y, Color.BLACK.getRGB());

                arrayListFont.drawString(tag, x + moduleComponent.getNameWidth() + 3, y, Color.BLACK.getRGB());
            };

            NORMAL_RENDER_RUNNABLES.add(textRunnable);

            if (glow) {
                NORMAL_POST_BLOOM_RUNNABLES.add(textRunnable);
            } else if (this.getParent().getShader().getValue().getName().equals("Shadow")) {
                NORMAL_POST_BLOOM_RUNNABLES.add(shadowRunnable);
            }

            if (this.sidebar.getValue()) {
                Runnable runnable = () -> RenderUtil.roundedRectangle(x + moduleComponent.getNameWidth() + moduleComponent.getTagWidth() + 2, y - 1.5f,
                        2, 9, 1, finalColor);

                runnable.run();
                NORMAL_POST_BLOOM_RUNNABLES.add(runnable);
            }
        }

        if (coordinates == null || username == null) return;

        NORMAL_POST_BLOOM_RUNNABLES.add(() -> {
            if (!this.getParent().getShader().getValue().getName().equals("None")) {
                // information of user in the bottom right corner of the screen
                final float x = event.getScaledResolution().getScaledWidth();
                final float y = event.getScaledResolution().getScaledHeight() - this.productSansRegular.height() - 1;
                this.productSansRegular.drawString("User:", x - nameWidth - userWidth - 5, y, 0xFFCCCCCC);
                this.productSansMedium18.drawString(username, x - nameWidth - 5, y, 0xFFCCCCCC);

                // coordinates of user in the bottom left corner of the screen
                final float coordX = 5;
                this.productSansRegular.drawString("XYZ:", coordX, y - (mc.currentScreen instanceof GuiChat ? 13 : 0), 0xFFCCCCCC);
                this.productSansMedium18.drawString(coordinates, coordX + xyzWidth, y - (mc.currentScreen instanceof GuiChat ? 13 : 0), 0xFFCCCCCC);
            }

            if (!stopWatch.finished(2000)) {
                int value = (int) Math.min(Math.max((Math.sin(stopWatch.getElapsedTime() / 2000f * 5) * 255), 0), 255);
                this.productSansMedium36.drawString(Client.NAME, 6, 6, ColorUtil.withAlpha(getTheme().getFirstColor()
                        , value).hashCode());

                ParticleComponent.render();
            }
        });

        // information of user in the bottom right corner of the screen
        final float x = event.getScaledResolution().getScaledWidth();
        final float y = event.getScaledResolution().getScaledHeight() - this.productSansRegular.height() - 1;
        this.productSansRegular.drawString("User:", x - nameWidth - userWidth - 5, y, 0xFFCCCCCC);
        this.productSansMedium18.drawString(username, x - nameWidth - 5, y, 0xFFCCCCCC);

        // coordinates of user in the bottom left corner of the screen
        final float coordX = 5;
        this.productSansRegular.drawString("XYZ:", coordX, y - (mc.currentScreen instanceof GuiChat ? 13 : 0), 0xFFCCCCCC);
        this.productSansMedium18.drawString(coordinates, coordX + xyzWidth, y - (mc.currentScreen instanceof GuiChat ? 13 : 0), 0xFFCCCCCC);

        // title in the top left corner of the screen
        this.productSansMedium36.drawString(Client.NAME, 6, 6, this.getTheme().getFirstColor().getRGB());

        if (mc.thePlayer.ticksExisted % 150 == 0) {
            stopWatch.reset();
        }
    };

    @EventLink()
    public final Listener<KillEvent> onKill = event -> {

        if (!stopWatch.finished(2000) && this.particles.getValue()) {
            for (int i = 0; i <= 10; i++) {
                ParticleComponent.add(new Particle(new Vector2f(0, 0),
                        new Vector2f((float) Math.random(), (float) Math.random())));
            }
        }

        stopWatch.reset();
    };

    @EventLink()
    public final Listener<TickEvent> onTick = event -> {

        threadPool.execute(() -> {
            glow = this.getParent().getShader().getValue().getName().equals("Glow");
            arrayListFont = this.font.getValue().getName().equals("Minecraft") ? minecraft : productSansRegular;

            username = mc.getSession() == null || mc.getSession().getUsername() == null ? "null" : mc.getSession().getUsername();
            nameWidth = this.productSansMedium18.width(username);
            userWidth = this.productSansRegular.width("User:") + 2;
            coordinates = (int) mc.thePlayer.posX + ", " + (int) mc.thePlayer.posY + ", " + (int) mc.thePlayer.posZ;
            xyzWidth = this.productSansMedium18.width("XYZ:") + 2;

            // modules in the top right corner of the screen
            for (final ModuleComponent moduleComponent : this.getParent().getActiveModuleComponents()) {
                if (moduleComponent.animationTime == 0) {
                    continue;
                }
                final boolean hasTag = !moduleComponent.getTag().isEmpty() && this.getParent().suffix.getValue();

                String name = (this.getParent().lowercase.getValue() ? moduleComponent.getModule().getModuleInfo().name().toLowerCase() :
                        moduleComponent.getModule().getModuleInfo().name()).replace(getParent().getRemoveSpaces().getValue() ? " " : "", "");
                String tag = (this.getParent().lowercase.getValue() ? moduleComponent.getTag().toLowerCase() : moduleComponent.getTag())
                        .replace(getParent().getRemoveSpaces().getValue() ? " " : "", "");

                Color color = this.getTheme().getFirstColor();
                switch (this.colorMode.getValue().getName()) {
                    case "Breathe": {
                        double factor = this.getTheme().getBlendFactor(new Vector2d(0, 0));
                        color = ColorUtil.mixColors(this.getTheme().getFirstColor(), this.getTheme().getSecondColor(), factor);
                        break;
                    }
                    case "Fade": {
                        color = this.getTheme().getAccentColor(new Vector2d(0, moduleComponent.getPosition().getY()));
                        break;
                    }
                }

                moduleComponent.setColor(color);

                moduleComponent.setNameWidth(arrayListFont.width(name));
                moduleComponent.setTagWidth(hasTag ? (arrayListFont.width(tag) + 4) : 0);
            }
        });
    };
}
