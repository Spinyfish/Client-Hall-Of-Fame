package dev.africa.pandaware.impl.ui.clickgui.setting;

import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.interfaces.MinecraftInstance;
import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.api.screen.GUIRenderer;
import dev.africa.pandaware.api.setting.Setting;
import dev.africa.pandaware.impl.font.Fonts;
import dev.africa.pandaware.impl.module.render.HUDModule;
import dev.africa.pandaware.impl.setting.*;
import dev.africa.pandaware.impl.ui.UISettings;
import dev.africa.pandaware.impl.ui.clickgui.module.ModuleElement;
import dev.africa.pandaware.impl.ui.clickgui.setting.api.Element;
import dev.africa.pandaware.impl.ui.clickgui.setting.impl.*;
import dev.africa.pandaware.impl.ui.clickgui.setting.impl.text.TextElement;
import dev.africa.pandaware.utils.client.MouseUtils;
import dev.africa.pandaware.utils.math.vector.Vec2i;
import dev.africa.pandaware.utils.render.RenderUtils;
import dev.africa.pandaware.utils.render.animator.Animator;
import dev.africa.pandaware.utils.render.animator.Easing;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class SettingPanel implements GUIRenderer, MinecraftInstance {
    private final ModuleElement parent;

    private final Module module;
    private final List<Element<?>> elementList;

    @Setter
    private Vec2i position;
    private final Vec2i size;

    private double scrolling;
    private boolean opened;
    private boolean shouldClose;

    private final Animator animator = new Animator();

    public SettingPanel(ModuleElement parent, Module module, Vec2i position, Vec2i size) {
        this.parent = parent;
        this.module = module;
        this.position = position;
        this.size = size;

        this.elementList = new LinkedList<>();

        this.module.getSettings().keySet().forEach(setting -> {
            this.addElements(null, setting);
        });

        if (module.getModeSetting() != null) {
            if (!module.getModeSetting().getValues().isEmpty()) {
                module.getModeSetting().getValues().forEach(moduleMode -> {
                    if (!moduleMode.getSettings().isEmpty()) {
                        moduleMode.getSettings().keySet().forEach(setting ->
                                this.addElements(moduleMode, setting));
                    }
                });
            }
        }
    }

    public void open() {
        this.animator.reset();
        this.shouldClose = false;
    }

    public void close() {
        this.animator.reset();
        this.shouldClose = false;
    }

    @Override
    public void handleRender(Vec2i mousePosition, float pTicks) {
        double animate = this.parent.getParent().getParent().height *
                this.parent.getParent().getParent().getAnimator().getValue();

        this.animator.setEase(this.shouldClose ? Easing.QUINTIC_IN : Easing.QUINTIC_OUT).setSpeed(3).setMin(0)
                .setReversed(this.shouldClose).setMax(1).update();

        if (this.shouldClose && this.animator.getValue() <= 0.2) {
            this.opened = false;

            this.parent.getParent().getParent().setOpenSettingPanel(null);

            this.close();
            return;
        }

        RenderUtils.drawRect(0, -animate, mc.displayWidth, mc.displayHeight,
                new Color(0, 0, 0, (int) (80 * this.animator.getValue())).getRGB());

        GlStateManager.pushMatrix();

        double offset = 0;
        if (this.animator.getValue() > 0) {
            offset = (this.getParent().getParent().getParent().height) * (1 - this.animator.getValue());

            GlStateManager.translate(0, -offset, 0);
        }

        HUDModule hud = Client.getInstance().getModuleManager().getByClass(HUDModule.class);
        if (hud.getHudMode().getValue() == HUDModule.HUDMode.ROUNDED) {
            RenderUtils.drawRoundedRect(
                    this.position.getX(), this.position.getY(),
                    this.size.getX(), this.size.getY(),
                    7, new Color(80, 80, 80, 120)
            );

            RenderUtils.drawRoundedRectOutline(
                    this.position.getX(), this.position.getY(),
                    this.size.getX(), this.size.getY(),
                    7, UISettings.CURRENT_COLOR
            );
        } else {
            RenderUtils.drawVerticalGradientRect(this.position.getX(), this.position.getY(), this.size.getX(),
                    this.size.getY(), new Color(0, 0, 0, 150), new Color(0, 0, 0, 255));
        }

        RenderUtils.drawRect(
                this.position.getX() + 4,
                this.position.getY() + 23,
                this.position.getX() + this.size.getX() - 4,
                this.position.getY() + 24,
                UISettings.CURRENT_COLOR.getRGB()
        );

        Fonts.getInstance().getComfortaBig().drawCenteredString(
                this.module.getData().getName(),
                this.position.getX() + (this.size.getX() / 2),
                this.position.getY() + 6,
                -1
        );
        Fonts.getInstance().getIconsVeryBig().drawString(
                this.parent.getParent().getCategoryIcon(),
                this.position.getX() + 5,
                this.position.getY() + 6,
                -1
        );

        RenderUtils.startScissorBox();
        RenderUtils.drawScissorBox(
                this.position.getX() + 5,
                this.position.getY() + 25 + animate - offset,
                this.size.getX() - 10,
                this.size.getY() - 30
        );

        AtomicInteger maxScroll = new AtomicInteger(0);
        this.elementList.stream()
                .filter(element -> element.getSetting().getSupplier() != null && (boolean) element.getSetting().getSupplier().get() &&
                        element.getModuleMode() == null ||
                        element.getSetting().getSupplier() != null &&
                                element.getModule().getCurrentMode() != null && element.getModule().getCurrentMode()
                                == element.getModuleMode() && (boolean) element.getSetting().getSupplier().get())
                .forEach(element -> maxScroll.addAndGet(element.getSize() != null ? element.getSize().getY() : 0));

        boolean shouldScroll = maxScroll.get() > (this.size.getY() - 255);

        if (shouldScroll) {
            int wheel = Mouse.getDWheel();

            if (wheel < 0) {
                this.scrolling += 20;
            } else if (wheel > 0) {
                this.scrolling -= 20;
            }

            this.scrolling = MathHelper.clamp_double(this.scrolling, 0, maxScroll.get() - 20);
        } else {
            this.scrolling = 0;
        }

        Vec2i scrollCopy = this.position.copy();
        scrollCopy.setY(this.position.getY() - (int) this.scrolling);

        AtomicInteger atomicInteger = new AtomicInteger();
        this.elementList.stream()
                .filter(element -> element.getSetting().getSupplier() != null && (boolean) element.getSetting().getSupplier().get() &&
                        element.getModuleMode() == null ||
                        element.getSetting().getSupplier() != null &&
                                element.getModule().getCurrentMode() != null && element.getModule().getCurrentMode()
                                == element.getModuleMode() && (boolean) element.getSetting().getSupplier().get())
                .forEach(element -> {
                    element.update(
                            new Vec2i(scrollCopy.getX() + 7, scrollCopy.getY() + 35 + atomicInteger.get()),
                            new Vec2i(this.size.getX() - 10, 20)
                    );

                    if (element.getPosition().getY() <= (this.position.getY() + this.getSize().getY() - 5)
                            && (element.getPosition().getY() + element.getSize().getY()) >=
                            (this.position.getY() + 24)) {
                        element.handleRender(mousePosition, pTicks);
                    }

                    atomicInteger.addAndGet(element.getSize().getY() + 4);
                });

        RenderUtils.endScissorBox();
        GlStateManager.popMatrix();

        this.opened = true;
        this.parent.getParent().getParent().setSettingPanelFirstOpen(true);
    }

    @Override
    public void handleKeyboard(char typedChar, int keyCode) {
        this.elementList.stream()
                .filter(element -> element.getSetting().getSupplier() != null && (boolean) element.getSetting().getSupplier().get() &&
                        element.getModuleMode() == null ||
                        element.getSetting().getSupplier() != null &&
                                element.getModule().getCurrentMode() != null && element.getModule().getCurrentMode()
                                == element.getModuleMode() && (boolean) element.getSetting().getSupplier().get())
                .forEach(element -> element.handleKeyboard(typedChar, keyCode));
    }

    @Override
    public void handleClick(Vec2i mousePosition, int button) {
        boolean hovered = MouseUtils.isMouseInBounds(mousePosition, this.position, this.size);

        if (hovered) {
            if (button == 0) {
                if (mousePosition.getY() > (this.position.getY() + 24) &&
                        mousePosition.getY() < (this.position.getY() + this.getSize().getY() - 5)) {
                    this.elementList.stream()
                            .filter(element -> element.getSetting().getSupplier() != null && (boolean) element.getSetting().getSupplier().get() &&
                                    element.getModuleMode() == null ||
                                    element.getSetting().getSupplier() != null &&
                                            element.getModule().getCurrentMode() != null && element.getModule().getCurrentMode()
                                            == element.getModuleMode() && (boolean) element.getSetting().getSupplier().get())
                            .forEach(element -> element.handleClick(mousePosition, button));
                } else {
                    this.parent.getParent().getParent().setSettingPanelDraggingPosition(new Vec2i(
                            this.position.getX() - mousePosition.getX(),
                            this.position.getY() - mousePosition.getY()
                    ));

                    this.parent.getParent().getParent().setSettingPanelDragging(true);
                }
            }
        } else {
            if (this.opened) {
                this.shouldClose = true;
            }
        }
    }

    @Override
    public void handleRelease(Vec2i mousePosition, int state) {
        if (state == 0) {
            this.parent.getParent().getParent().setSettingPanelDragging(false);
        }

        this.elementList.stream()
                .filter(element -> element.getSetting().getSupplier() != null && (boolean) element.getSetting().getSupplier().get() &&
                        element.getModuleMode() == null ||
                        element.getSetting().getSupplier() != null &&
                                element.getModule().getCurrentMode() != null && element.getModule().getCurrentMode()
                                == element.getModuleMode() && (boolean) element.getSetting().getSupplier().get())
                .forEach(element -> element.handleRelease(mousePosition, state));
    }

    @Override
    public void handleScreenUpdate() {
        this.elementList.stream()
                .filter(element -> element.getSetting().getSupplier() != null && (boolean) element.getSetting().getSupplier().get() &&
                        element.getModuleMode() == null ||
                        element.getSetting().getSupplier() != null &&
                                element.getModule().getCurrentMode() != null && element.getModule().getCurrentMode()
                                == element.getModuleMode() && (boolean) element.getSetting().getSupplier().get())
                .forEach(GUIRenderer::handleScreenUpdate);
    }

    @Override
    public void handleGuiClose() {
        this.parent.getParent().getParent().setSettingPanelDragging(false);

        this.elementList.stream()
                .filter(element -> element.getSetting().getSupplier() != null && (boolean) element.getSetting().getSupplier().get() &&
                        element.getModuleMode() == null ||
                        element.getSetting().getSupplier() != null &&
                                element.getModule().getCurrentMode() != null && element.getModule().getCurrentMode()
                                == element.getModuleMode() && (boolean) element.getSetting().getSupplier().get())
                .forEach(GUIRenderer::handleScreenUpdate);
    }

    @Override
    public void handleGuiInit() {
        this.elementList.stream()
                .filter(element -> element.getSetting().getSupplier() != null && (boolean) element.getSetting().getSupplier().get() &&
                        element.getModuleMode() == null ||
                        element.getSetting().getSupplier() != null &&
                                element.getModule().getCurrentMode() != null && element.getModule().getCurrentMode()
                                == element.getModuleMode() && (boolean) element.getSetting().getSupplier().get())
                .forEach(GUIRenderer::handleScreenUpdate);
    }

    void addElements(ModuleMode<?> moduleMode, Setting<?> setting) {
        if (setting instanceof BooleanSetting) {
            this.elementList.add(new BooleanElement(this.module, moduleMode, (BooleanSetting) setting));
        } else if (setting instanceof NumberSetting) {
            this.elementList.add(new NumberElement(this.module, moduleMode, (NumberSetting) setting));
        } else if (setting instanceof ColorSetting) {
            this.elementList.add(new ColorElement(this.module, moduleMode, (ColorSetting) setting));
        } else if (setting instanceof EnumSetting<?>) {
            this.elementList.add(new EnumElement(this.module, moduleMode, (EnumSetting<?>) setting));
        } else if (setting instanceof ModeSetting) {
            this.elementList.add(new ModeElement(this.module, moduleMode, (ModeSetting) setting));
        } else if (setting instanceof NumberRangeSetting) {
            this.elementList.add(new RangeElement(this.module, moduleMode, (NumberRangeSetting) setting));
        } else if (setting instanceof TextBoxSetting) {
            this.elementList.add(new TextElement(this.module, moduleMode, (TextBoxSetting) setting));
        }
    }
}
