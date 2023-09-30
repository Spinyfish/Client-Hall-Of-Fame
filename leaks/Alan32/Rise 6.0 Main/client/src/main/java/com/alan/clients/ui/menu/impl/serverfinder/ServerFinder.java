package com.alan.clients.ui.menu.impl.serverfinder;

import com.alan.clients.ui.menu.Menu;
import com.alan.clients.ui.menu.impl.main.MainMenu;
import com.alan.clients.util.player.ServerUtil;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.resources.I18n;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Auth
 * @since 10/07/2022
 */
public final class ServerFinder extends Menu {

    private final Pattern IP_ADDRESS = Pattern.compile(
            "((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4]"
                    + "[0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]"
                    + "[0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}"
                    + "|[1-9][0-9]|[0-9]))");

    private final Pattern PORT = Pattern.compile(
            "([1-9][0-9]{0,3}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2]" +
                    "[0-9]|6553[0-5])");

    private final List<ServerData> servers = new ArrayList<>();

    private ThreadPoolExecutor executor = null;

    private boolean done;

    private GuiTextField ipRange;
    private GuiTextField portRange;
    private GuiTextField threads;

    private void start() {
        this.buttonList.get(0).displayString = "Stop";

        final ServerIP minIp;
        ServerIP maxIp;

        final String[] ipRange = this.ipRange.getText().split("-");
        if (IP_ADDRESS.matcher(ipRange[0]).matches()) {
            minIp = new ServerIP(ipRange[0]);
            maxIp = new ServerIP(ipRange[0]);
        } else {
            this.stop();
            return;
        }
        if (ipRange.length > 1) {
            final Matcher maxMatcher = IP_ADDRESS.matcher(ipRange[1]);
            if (maxMatcher.matches()) {
                maxIp = new ServerIP(ipRange[1]);
            } else {
                this.stop();
                return;
            }
        }

        final int minPort;
        int maxPort;
        final String[] portSplit = this.portRange.getText().split("-");
        final Matcher minMatcher = PORT.matcher(portSplit[0]);

        if (minMatcher.matches()) {
            minPort = Integer.parseInt(portSplit[0]);
            maxPort = Integer.parseInt(portSplit[0]);
        } else {
            this.stop();
            return;
        }
        if (portSplit.length > 1) {
            final Matcher maxMatcher = PORT.matcher(portSplit[1]);

            if (maxMatcher.matches()) {
                maxPort = Integer.parseInt(portSplit[1]);
            } else {
                this.stop();
                return;
            }
        }

        final String threadsString = this.threads.getText();

        if (!StringUtils.isNumeric(threadsString)) {
            this.stop();
            return;
        }

        final int threads = Integer.parseInt(threadsString);

        this.executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threads);

        System.out.println("Started with " + threads + " threads");
        servers.clear();
        done = false;
        findServers(ServerIP.min(minIp, maxIp), ServerIP.max(minIp, maxIp), Math.min(minPort, maxPort), Math.max(minPort, maxPort));
    }

    private void findServers(final ServerIP minIP, final ServerIP maxIP, final int minPort, final int maxPort) {
        final ServerIP currentServer = new ServerIP(minIP.getFirst(), minIP.getSecond(), minIP.getThird(), minIP.getFourth());
        for (int subnetMask = 0; subnetMask < 4; subnetMask++) {
            for (int subnet = minIP.getPart(subnetMask); subnet <= maxIP.getPart(subnetMask); subnet++) {
                currentServer.setPart(subnetMask, subnet);
                final ServerIP host = new ServerIP(currentServer.getPart(0), currentServer.getPart(1), currentServer.getPart(2), currentServer.getPart(3));
                for (int port = minPort; port <= maxPort; port++) {
                    final int finalPort = port;
                    executor.execute(() -> {
                        if (done) return;
                        System.out.println("CHECKING " + host + ":" + finalPort);
                        final ServerData data = ServerUtil.isOnline(host.toString(), finalPort, 500);
                        if (data != null) servers.add(data);

                        if (host.toString().equals(maxIP.toString()) && finalPort == maxPort) this.stop();
                    });
                }
            }
        }
    }

    private void stop() {
        servers.removeIf(server -> server.populationInfo == null);
        System.out.println(servers.size() + " servers found");
        for (final ServerData data : servers) {
            GuiMultiplayer.usingFinderList = true;
            GuiMultiplayer.savedServerList.loadServerList();
            GuiMultiplayer.savedServerList.addServerData(data);
            GuiMultiplayer.savedServerList.saveServerList();
        }
        this.buttonList.get(0).displayString = "Start";
        this.done = true;
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen() {
        this.ipRange.updateCursorCounter();
        this.portRange.updateCursorCounter();
        this.threads.updateCursorCounter();
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui() {
        final int height = 20;

        this.ipRange = new GuiTextField(0, this.fontRendererObj, this.width / 2 - 100, 56, 200, 20);
        this.ipRange.setMaxStringLength(31);
        this.ipRange.setFocused(true);
        this.ipRange.setText("1.1.1.1-255.255.255.255");

        this.portRange = new GuiTextField(1, this.fontRendererObj, this.width / 2 - 100, 96, 200, 20);
        this.portRange.setMaxStringLength(11);
        this.portRange.setText("1-25565");

        this.threads = new GuiTextField(2, this.fontRendererObj, this.width / 2 - 100, 136, 200, 20);
        this.threads.setMaxStringLength(4);
        this.threads.setText("128");

        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 96 + 12, "Start"));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 96 + 12 + height + 4, I18n.format("gui.done")));

        super.initGui();
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(final GuiButton button) throws IOException {
        switch (button.id) {
            case 0:
                switch (this.buttonList.get(0).displayString) {
                    case "Start":
                        this.start();
                        break;

                    case "Stop":
                        this.stop();
                        break;
                }
                break;

            case 1:
                this.mc.displayGuiScreen(new GuiMultiplayer(new MainMenu()));
                break;
        }
    }

    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped(final char typedChar, final int keyCode) throws IOException {
        this.ipRange.textboxKeyTyped(typedChar, keyCode);
        this.portRange.textboxKeyTyped(typedChar, keyCode);
        this.threads.textboxKeyTyped(typedChar, keyCode);
    }

    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.ipRange.mouseClicked(mouseX, mouseY, mouseButton);
        this.portRange.mouseClicked(mouseX, mouseY, mouseButton);
        this.threads.mouseClicked(mouseX, mouseY, mouseButton);
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        this.drawDefaultBackground();
        this.drawString(this.fontRendererObj, "IP Range", this.width / 2 - 100, 40, 10526880);
        this.drawString(this.fontRendererObj, "Port Range", this.width / 2 - 100, 82, 10526880);
        this.drawString(this.fontRendererObj, "Threads", this.width / 2 - 100, 122, 10526880);
        this.ipRange.drawTextBox();
        this.portRange.drawTextBox();
        this.threads.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
