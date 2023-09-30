package dev.africa.pandaware.impl.ui.clickgui.panel;

import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.interfaces.MinecraftInstance;
import dev.africa.pandaware.api.module.interfaces.Category;
import dev.africa.pandaware.api.screen.GUIRenderer;
import dev.africa.pandaware.impl.font.Fonts;
import dev.africa.pandaware.impl.module.render.HUDModule;
import dev.africa.pandaware.impl.ui.UISettings;
import dev.africa.pandaware.impl.ui.clickgui.ClickGUI;
import dev.africa.pandaware.impl.ui.clickgui.module.ModuleElement;
import dev.africa.pandaware.utils.client.MouseUtils;
import dev.africa.pandaware.utils.math.vector.Vec2i;
import dev.africa.pandaware.utils.render.RenderUtils;
import lombok.Getter;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class Panel implements GUIRenderer, MinecraftInstance {
    private final ClickGUI parent;
    private final Category category;
    private final List<ModuleElement> moduleElements;

    private final Vec2i position;
    private final Vec2i size;

    private double scrolling;

    private boolean extended = true;
    private boolean dragging;
    private Vec2i draggingPosition;

    public Panel(ClickGUI parent, Category category, Vec2i position, Vec2i size) {
        this.parent = parent;
        this.category = category;
        this.position = position;
//        this.size = size;

        this.moduleElements = new LinkedList<>();

        AtomicInteger atomicInteger = new AtomicInteger(position.getY() + 30);
        Client.getInstance().getModuleManager().getInCategory(category).forEach(module ->
                this.moduleElements.add(new ModuleElement(this, module,
                        new Vec2i(position.getX() + 4, atomicInteger.getAndAdd(20)),
                        new Vec2i(size.getX() - 8, 18))));
        this.size = new Vec2i(size.getX(), Math.min(atomicInteger.get() - position.getY(), size.getY()));
    }

    @Override
    public void handleRender(Vec2i mousePosition, float pTicks) {
        if (this.dragging) {
            this.position.setX(mousePosition.getX() + this.draggingPosition.getX());
            this.position.setY(mousePosition.getY() + this.draggingPosition.getY());
        }

        double animate = this.parent.height * this.parent.getAnimator().getValue();

        HUDModule hud = Client.getInstance().getModuleManager().getByClass(HUDModule.class);
        if (hud.getHudMode().getValue() == HUDModule.HUDMode.ROUNDED) {
            RenderUtils.drawRoundedRect(this.position.getX(), this.position.getY(), this.size.getX(),
                    this.extended ? this.size.getY() : 23, 9f, new Color(31, 31, 31, 140));
            RenderUtils.drawRoundedRectOutline(this.position.getX(), this.position.getY(),
                    this.size.getX(), this.extended ? this.size.getY() : 23, 9f,
                    UISettings.CURRENT_COLOR);
        } else {
            RenderUtils.drawVerticalGradientRect(this.position.getX(), this.position.getY(), this.size.getX(),
                    this.extended ? this.size.getY() : 23, UISettings.INTERNAL_COLOR, UISettings.INTERNAL_COLOR);
            RenderUtils.drawVerticalGradientRect(this.position.getX(), this.position.getY(), this.size.getX(),
                    this.extended ? this.size.getY() : 23, UISettings.INTERNAL_COLOR, UISettings.INTERNAL_COLOR);
        }

        int add = this.category == Category.COMBAT ? 4 : 0;
        Fonts.getInstance().getIconsVeryBig().drawString(this.getCategoryIcon(),
                this.getPosition().getX() + 2 + add, this.getPosition().getY() + 5, -1);

        add = this.category == Category.MOVEMENT ? 4 : 0;
        Fonts.getInstance().getProductSansVeryBig().drawCenteredString(this.category.getLabel(),
                this.getPosition().getX() + (this.getSize().getX() / 2) + add,
                this.getPosition().getY() + 4, -1);

        if (this.extended) {
            RenderUtils.drawRect(this.position.getX() + 4, this.position.getY() + 22,
                    this.position.getX() + this.size.getX() - 4, this.position.getY() + 23,
                    UISettings.CURRENT_COLOR.getRGB());

            RenderUtils.startScissorBox();
            RenderUtils.drawScissorBox(this.position.getX() + 2, this.position.getY() + 25 + animate,
                    this.getSize().getX() - 4, this.getSize().getY() - 30);

            if (MouseUtils.isMouseInBounds(mousePosition, this.position, this.size)) {
                AtomicInteger maxScroll = new AtomicInteger(0);
                this.moduleElements.forEach(element -> maxScroll.addAndGet(element.getSize() != null
                        ? element.getSize().getY() : 0));

                boolean shouldScroll = moduleElements.size() > 13;

                if (shouldScroll) {
                    int wheel = Mouse.getDWheel();

                    if (wheel < 0) {
                        this.scrolling += 20;
                    } else if (wheel > 0) {
                        this.scrolling -= 20;
                    }

                    this.scrolling = MathHelper.clamp_double(this.scrolling, 0, maxScroll.get() - 240);
                } else {
                    this.scrolling = 0;
                }
            }

            AtomicInteger atomicInteger = new AtomicInteger((int) ((this.position.getY() + 28) - this.scrolling));
            this.moduleElements.forEach(moduleElement -> {
                moduleElement.update(new Vec2i(this.position.getX() + 4, atomicInteger.getAndAdd(20)));

                moduleElement.handleRender(mousePosition, pTicks);
            });

            RenderUtils.endScissorBox();
        }
    }

    @Override
    public void handleKeyboard(char typedChar, int keyCode) {
        if (this.extended) {
            this.moduleElements.forEach(moduleElement -> moduleElement.handleKeyboard(typedChar, keyCode));
        }
    }

    @Override
    public void handleClick(Vec2i mousePosition, int button) {
        if (this.getParent().getOpenSettingPanel() == null && MouseUtils.isMouseInBounds(mousePosition,
                this.position, new Vec2i(this.size.getX(), 22))) {
            if (button == 0) {
                this.draggingPosition = new Vec2i(this.position.getX() - mousePosition.getX(),
                        this.position.getY() - mousePosition.getY());

                this.dragging = true;
            } else if (button == 1) {
                this.extended = !this.extended;
            }
        }

        if (this.extended) {
            if (mousePosition.getY() <= this.position.getY() + this.size.getY()) {
                this.moduleElements.forEach(moduleElement -> moduleElement.handleClick(mousePosition, button));
            }
        }
    }

    @Override
    public void handleRelease(Vec2i mousePosition, int state) {
        if (state == 0) {
            this.dragging = false;
        }

        this.moduleElements.forEach(moduleElement -> moduleElement.handleRelease(mousePosition, state));
    }

    @Override
    public void handleScreenUpdate() {
        if (this.extended) {
            this.moduleElements.forEach(ModuleElement::handleScreenUpdate);
        }
    }

    @Override
    public void handleGuiClose() {
        this.dragging = false;

        this.moduleElements.forEach(ModuleElement::handleGuiClose);
    }

    @Override
    public void handleGuiInit() {
        this.moduleElements.forEach(ModuleElement::handleGuiInit);
    }

    public String getCategoryIcon() {
        switch (this.category) {
            case COMBAT:
                return "a";
            case MOVEMENT:
                return "E";
            case VISUAL:
                return "D";
            case PLAYER:
                return "d";
            default:
                return "F";
        }
    }
}
