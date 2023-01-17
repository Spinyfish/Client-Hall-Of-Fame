package com.alan.clients.ui.click.standard.screen.impl;

import com.alan.clients.Client;
import com.alan.clients.ui.click.standard.RiseClickGUI;
import com.alan.clients.ui.click.standard.screen.Screen;
import com.alan.clients.ui.click.standard.components.ModuleComponent;
import com.alan.clients.ui.click.standard.components.value.ValueComponent;
import com.alan.clients.ui.click.standard.components.value.impl.StringValueComponent;
import com.alan.clients.util.gui.ScrollUtil;
import com.alan.clients.util.gui.textbox.TextAlign;
import com.alan.clients.util.gui.textbox.TextBox;
import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.util.localization.Localization;
import com.alan.clients.util.render.ColorUtil;
import util.time.StopWatch;
import com.alan.clients.util.vector.Vector2d;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.util.ArrayList;

@Getter
@Setter
public final class SearchScreen extends Screen implements InstanceAccess {

    public final TextBox searchBar = new TextBox(new Vector2d(200, 200), nunitoNormal, Color.WHITE,
            TextAlign.CENTER, Localization.get("ui.search.text"), 150);
    private final StopWatch stopwatch = new StopWatch();

    public ScrollUtil scrollUtil = new ScrollUtil();
    public ArrayList<ModuleComponent> relevantModules = new ArrayList<>();
    private double opacity = 255;
    private double endOfList, startOfList;

    private boolean typedWhileOpen;

    @Override
    public void onRender(final int mouseX, final int mouseY, final float partialTicks) {
        final RiseClickGUI clickGUI = this.getStandardClickGUI();

        /* Setting searchbar color to clickgui fontcolor */
        if (scrollUtil.getTarget() < 0) {
            opacity -= stopwatch.getElapsedTime() * 4;
        } else {
            opacity += stopwatch.getElapsedTime() * 4;
        }
        opacity = Math.min(Math.max(0, opacity), 255);
        searchBar.setColor(ColorUtil.withAlpha(clickGUI.fontColor, (int) opacity));

        /* Setting position of searchbar */
        final Vector2d positionOfSearch = new Vector2d(((clickGUI.position.x + clickGUI.sidebar.sidebarWidth) +
                (clickGUI.scale.x - clickGUI.sidebar.sidebarWidth) / 2), (float) (clickGUI.position.y + 17 + scrollUtil.getScroll()));

        searchBar.setPosition(positionOfSearch);

        /* Draws searchbar */
        searchBar.draw();

        /* Scroll */
        scrollUtil.onRender();

        /* Draws modules in search */
        double positionY = clickGUI.position.y + 35 + scrollUtil.getScroll();
        startOfList = positionY;

        /* Draws all modules */
        double height = 0;
        for (final ModuleComponent module : this.relevantModules) {
            module.draw(new Vector2d(clickGUI.position.x + clickGUI.sidebar.sidebarWidth + 7, positionY), mouseX, mouseY, partialTicks);
            positionY += module.scale.y + 7;
            height += module.scale.y + 7;
        }

        endOfList = positionY;

        scrollUtil.setMax(-height + clickGUI.scale.y - 37);

        double padding = 7;
        double scrollX = clickGUI.getPosition().getX() + clickGUI.getScale().getX() - 4;
        double scrollY = clickGUI.getPosition().getY() + padding;
        scrollUtil.renderScrollBar(new Vector2d(scrollX, scrollY), getStandardClickGUI().scale.y - padding * 2);

        stopwatch.reset();
    }

    @Override
    public void onKey(final char typedChar, final int keyCode) {
        if (!typedWhileOpen && "abcdefghijklmnopqrstuvwxyz1234567890 ".contains(String.valueOf(typedChar).toLowerCase())) {
            typedWhileOpen = true;
            setSearchBarText(" ");
        }

        if (!this.activeTextBox()) {
            searchBar.key(typedChar, keyCode);
            scrollUtil.setTarget(0);

            searchBar.setSelected(true);
        }

        relevantModules = getRelevantModules(searchBar.getText());

        for (final ModuleComponent module : this.getRelevantModules()) {
            module.key(typedChar, keyCode);
        }
    }

    @Override
    public void onClick(final int mouseX, final int mouseY, final int mouseButton) {
        for (final ModuleComponent moduleComponent : relevantModules) {
            moduleComponent.click(mouseX, mouseY, mouseButton);
        }

        searchBar.click(mouseX, mouseY, mouseButton);
    }

    @Override
    public void onMouseRelease() {
        for (final ModuleComponent module : this.getRelevantModules()) {
            module.released();
        }
    }

    @Override
    public void onBloom() {
        for (final ModuleComponent module : this.getRelevantModules()) {
            module.bloom();
        }
    }

    @Override
    public void onInit() {
        relevantModules = getRelevantModules(searchBar.getText());

        typedWhileOpen = false;
    }

    private boolean isInView(final double position) {
        return (position > getStandardClickGUI().position.y && position < getStandardClickGUI().position.y + getStandardClickGUI().scale.y);
    }

    public ArrayList<ModuleComponent> getRelevantModules(final String search) {
        final ArrayList<ModuleComponent> relevantModules = new ArrayList<>();

        for (final ModuleComponent module : Client.INSTANCE.getStandardClickGUI().getModuleList()) {
            if (module.getModule().getModuleInfo().name().toLowerCase().replaceAll(" ", "")
                    .contains(search.toLowerCase().replaceAll(" ", ""))) {
                relevantModules.add(module);
            }
        }

        return relevantModules;
    }

    public void setSearchBarText(final String text) {
        this.searchBar.setText(text);
        relevantModules = getRelevantModules(searchBar.getText());
    }

    public boolean activeTextBox() {
        for (final ModuleComponent moduleComponent : relevantModules) {
            for (final ValueComponent value : moduleComponent.getValueList()) {
                if (value instanceof StringValueComponent && value.position != null && ((StringValueComponent) value).textBox.selected) {
                    return true;
                }
            }
        }

        return false;
    }
}
