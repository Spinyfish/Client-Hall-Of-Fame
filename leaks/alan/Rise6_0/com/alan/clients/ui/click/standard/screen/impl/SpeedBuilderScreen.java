package com.alan.clients.ui.click.standard.screen.impl;

import com.alan.clients.ui.click.standard.RiseClickGUI;
import com.alan.clients.ui.click.standard.screen.Screen;
import com.alan.clients.ui.click.standard.screen.impl.speedbuilder.Modifier;
import com.alan.clients.ui.click.standard.screen.impl.speedbuilder.Tick;
import com.alan.clients.ui.click.standard.screen.impl.speedbuilder.impl.ExemptedValue;
import com.alan.clients.util.gui.GUIUtil;
import com.alan.clients.util.gui.ScrollUtil;
import com.alan.clients.util.render.RenderUtil;
import com.alan.clients.util.vector.Vector2f;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.util.ArrayList;

@Getter
@Setter
public final class SpeedBuilderScreen extends Screen {

    public ScrollUtil scrollUtil = new ScrollUtil();

    private ResourceLocation image = new ResourceLocation("rise/images/SpeedBuilderBackground.png");

    public ArrayList<Modifier> modifiers = new ArrayList<>();
    public ArrayList<Tick> ticks = new ArrayList<>();
    public Tick selectedTick;

    float x, y;
    float width = 307;
    float height = 21;

    public SpeedBuilderScreen() {
        //Adding all OffGroundTick options
        for (int tick = 0; tick <= 11; ++tick) ticks.add(new Tick(tick));

        //Selecting default OffGroundTick
        selectedTick = ticks.get(0);

        //Registering all modifiers
//        final Reflections reflections = new Reflections("com.riseclient.rise.ui.click.standard.screen.impl.speedbuilder.impl");
//
//        reflections.getSubTypesOf(Modifier.class).forEach(clazz -> {
//            try {
//                modifiers.add(clazz.newInstance());
//            } catch (final Exception e) {
//                e.printStackTrace();
//            }
//        });
    }

    @Override
    public void onRender(final int mouseX, final int mouseY, final float partialTicks) {
        final RiseClickGUI clickGUI = getStandardClickGUI();

        x = clickGUI.position.x + 6;
        y = (float) (clickGUI.position.y + 11 + scrollUtil.getScroll());

        /* Scroll */
        scrollUtil.onRender();

        /* Render */

        //Draws category name
        this.nunitoNormal.drawString("Speed Builder", x, y, Color.WHITE.hashCode());

        y += 3;

        width = 307;
        height = 21;

        RenderUtil.dropShadow(36, (int) x + 5, (int) y + 21, width - 12.5f, height * 7, 50, 24);
        RenderUtil.image(image, x - 15, y, 674 / 1.997f, 381 / 1.997f);

        y += 178;
        y += 2;

        double scrollHeight = 0;
        for (final Modifier modifier : selectedTick.getModifiers()) {
            modifier.render(new Vector2f(x, y));
            y += modifier.height + 6;
            scrollHeight += modifier.height + 6;
        }

        y += 2;

        final Color background = new Color(0, 0, 0, 70);

        RenderUtil.dropShadow(36, x, y, width, height, 40, 24);
        RenderUtil.roundedRectangle(x, y, width, height, 11, background);

        final Color plusColor = new Color(255, 255, 255, 255);
        RenderUtil.rectangle(x + width / 2, y + height / 2 - 5, 1, 11, plusColor);
        RenderUtil.rectangle(x + width / 2 - 5, y + height / 2, 11, 1, plusColor);

        RenderUtil.roundedRectangle(x, y, width, height, 11, new Color(0, 0, 0, 30));

        //Setting max scroll
        scrollUtil.setMax(-scrollHeight + 36);
    }

    @Override
    public void onKey(final char typedChar, final int keyCode) {
    }

    @Override
    public void onClick(final int mouseX, final int mouseY, final int mouseButton) {
        if (GUIUtil.mouseOver(x, y, width, height, mouseX, mouseY)) {
            final boolean left = mouseButton == 0;

            if (left) {
                selectedTick.getModifiers().add(new ExemptedValue(selectedTick));
//                getStandardClickGUI().popUp = new ModifierSelectionPopUp();
            }
        }

        for (final Modifier modifier : selectedTick.getModifiers()) {
            modifier.click(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void onMouseRelease() {

    }

    @Override
    public void onBloom() {

    }

    @Override
    public void onInit() {

    }
}
