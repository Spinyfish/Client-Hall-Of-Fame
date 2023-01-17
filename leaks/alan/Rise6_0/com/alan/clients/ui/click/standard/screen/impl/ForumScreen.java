package com.alan.clients.ui.click.standard.screen.impl;

import com.alan.clients.Client;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.other.BackendPacketEvent;
import com.alan.clients.ui.click.standard.RiseClickGUI;
import com.alan.clients.ui.click.standard.screen.Screen;
import com.alan.clients.ui.click.standard.components.store.FileComponent;
import com.alan.clients.util.chat.ChatUtil;
import com.alan.clients.util.font.FontManager;
import com.alan.clients.util.gui.GUIUtil;
import com.alan.clients.util.gui.ScrollUtil;
import com.alan.clients.util.gui.textbox.TextAlign;
import com.alan.clients.util.gui.textbox.TextBox;
import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.util.render.ColorUtil;
import com.alan.clients.util.render.RenderUtil;
import util.time.StopWatch;
import com.alan.clients.util.vector.Vector2d;
import lombok.Getter;
import lombok.Setter;
import packet.Packet;
import packet.impl.client.store.lIIlIIIIIllIlllllllIIlllIlIllIlI;
import packet.impl.client.store.lllllIIlIIIlIIIIIIIlIlllIlIlIIlI;
import packet.impl.server.store.IllIIIllllIlIlIIIllIlIllllIIllll;
import store.File;
import store.impl.ConfigFile;

import java.awt.*;
import java.util.ArrayList;

@Getter
@Setter
public final class ForumScreen extends Screen implements InstanceAccess {

    public final TextBox searchBar = new TextBox(new Vector2d(200, 200), nunitoNormal, Color.WHITE, TextAlign.CENTER, "Start typing to search...", 150);
    private final StopWatch stopwatch = new StopWatch();

    public ScrollUtil scrollUtil = new ScrollUtil();
    public ArrayList<FileComponent> files = new ArrayList<>();
    public ArrayList<FileComponent> filteredFiles = new ArrayList<>();

    private double opacity = 255;
    private double endOfList, startOfList;

    private boolean typedWhileOpen;

    public ForumScreen() {
        // Has to be a listener to handle the responses
        Client.INSTANCE.getEventBus().register(this);
    }

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

        RenderUtil.dropShadow(4, (float) (clickGUI.position.x + clickGUI.scale.x - 17 - 8 - 5.5),
                (float) (clickGUI.position.y + 15 + scrollUtil.getScroll() - 3), 20, 20, 43, 6);

        RenderUtil.roundedRectangle(clickGUI.position.x + clickGUI.scale.x - 17 - 8 - 5.5,
                clickGUI.position.y + 15 + scrollUtil.getScroll() - 3, 20, 20, 3, clickGUI.sidebarColor);


        FontManager.getNunito(30).drawString("+", clickGUI.position.x + clickGUI.scale.x - 17 - 8,
                clickGUI.position.y + 15 + scrollUtil.getScroll(), Color.WHITE.hashCode());

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
        for (final FileComponent file : this.filteredFiles) {
            file.draw(new Vector2d(clickGUI.position.x + clickGUI.sidebar.sidebarWidth + 7, positionY), mouseX, mouseY, partialTicks);
            positionY += file.scale.y + 7;
            height += file.scale.y + 7;
        }

        endOfList = positionY;

        scrollUtil.setMax(-height + clickGUI.scale.y - 37);

        stopwatch.reset();
    }

    @Override
    public void onKey(final char typedChar, final int keyCode) {
        searchBar.setSelected(true);
        searchBar.key(typedChar, keyCode);

        this.filterFiles();
    }

    @Override
    public void onClick(final int mouseX, final int mouseY, final int mouseButton) {
        final RiseClickGUI clickGUI = this.getStandardClickGUI();

        if (GUIUtil.mouseOver(clickGUI.position.x + clickGUI.scale.x - 17 - 8 - 5.5,
                clickGUI.position.y + 15 + scrollUtil.getScroll() - 3, 20, 20, mouseX, mouseY)) {

            Client.INSTANCE.getNetworkManager().getCommunication().write(new lIIlIIIIIllIlllllllIIlllIlIllIlI(
                    new ConfigFile("Test Config", "This is a test config")));

            this.onInit();

            ChatUtil.display("Sent Config");
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
        Client.INSTANCE.getNetworkManager().getCommunication().write(new lllllIIlIIIlIIIIIIIlIlllIlIlIIlI());
        typedWhileOpen = false;
        ChatUtil.display("Sent");
        searchBar.setText("");
        this.filterFiles();
    }

    public void filterFiles() {
        filteredFiles.clear();

        for (FileComponent file : files) {
            if (file.getFile().getName().toLowerCase().contains(searchBar.getText().toLowerCase())) {
                filteredFiles.add(file);
            }
        }
    }

    @EventLink()
    public final Listener<BackendPacketEvent> onBackendPacket = event -> {

        Packet packet = event.getPacket();

        if (packet instanceof IllIIIllllIlIlIIIllIlIllllIIllll) {
            ChatUtil.display("Received");
            IllIIIllllIlIlIIIllIlIllllIIllll sPacketForumData = ((IllIIIllllIlIlIIIllIlIllllIIllll) packet);

            this.files.clear();

            for (File file : sPacketForumData.getFiles()) {
                this.files.add(new FileComponent(file));
            }
        }
    };

    @Override
    public boolean automaticSearchSwitching() {
        return false;
    }
}
