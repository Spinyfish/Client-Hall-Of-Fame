package crispy.util.render;

import arithmo.gui.altmanager.Colors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.multiplayer.ServerData;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class GuiServerFinder extends GuiScreen {
    public static int failedServers;
    public static int foundServers;
    public static ArrayList<ServerData> serverData;
    private final GuiScreen previousScreen;
    private GuiTextField ip;
    private final int maxThreads = 120;
    private final ArrayList<Pogger> poggers = new ArrayList<>();
    private State state;

    public GuiServerFinder(GuiScreen previousScreen) {
        this.previousScreen = previousScreen;


    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution res = new ScaledResolution(this.mc, mc.displayWidth, mc.displayHeight);
        Gui.drawRect(0.0, 0.0, ScaledResolution.getScaledWidth(), ScaledResolution.getScaledHeight(), Colors.getColor(0));
        ip.drawTextBox();
        drawCenteredString(Minecraft.fontRendererObj, state.toString(), width / 2, 20, -1);


    }


    public void initGui() {
        state = State.NONE;
        int var3 = height / 4 + 24;
        foundServers = 0;
        failedServers = 0;
        this.ip = new GuiTextField(0, Minecraft.fontRendererObj, width / 2 - 100, 100, 200, 20);
        this.ip.setFocused(true);
        this.buttonList.add(new GuiButton(0, width / 2 - 100, var3 + 72 + 12, "Search"));
        Keyboard.enableRepeatEvents(true);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        state = State.CANCELLED;
        poggers.clear();

    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        if (typedChar == '\r') {
            this.actionPerformed((GuiButton) this.buttonList.get(0));

        }
        this.ip.textboxKeyTyped(typedChar, keyCode);

    }

    public void findServers() throws UnknownHostException {
        InetAddress addr = InetAddress.getByName(ip.getText());
        int[] ipParts = new int[4];
        for (int i = 0; i < 4; i++) {
            ipParts[i] = addr.getAddress()[i] & 0xff;
        }
        state = State.SEARCHING;
        int[] changes = {0, 1, -1, 2, -2, 3, -3};
        for (int change : changes) {
            for (int i2 = 0; i2 <= 255; i2++) {
                if (state == State.CANCELLED)
                    return;

                int[] ipParts2 = ipParts.clone();
                ipParts2[2] = ipParts[2] + change & 0xff;
                ipParts2[3] = i2;
                String ip = ipParts2[0] + "." + ipParts2[1] + "."
                        + ipParts2[2] + "." + ipParts2[3];

                Pogger pinger = new Pogger(ip);
                poggers.add(pinger);
                while (poggers.size() >= maxThreads) {
                    if (state == State.CANCELLED)
                        return;

                }
            }
            while (poggers.size() > 0) {
                if (state == State.CANCELLED)
                    return;
                updatePoggers();

            }
            state = State.DONE;
        }


    }

    public void updateStatus() {
        if (state.isRunning()) {
            poggers.clear();
            state = State.CANCELLED;
        } else {
            foundServers = 0;
            failedServers = 0;
            state = State.RESOLVING;
            new Thread(() -> {
                try {
                    findServers();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }, "Server Finder").start();

        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        try {
            switch (button.id) {
                case 0: {
                    updateStatus();
                }
            }
        } catch (Exception e) {

        }
    }

    public void updatePoggers() {
        for (Pogger pogger : poggers) {
            if (pogger.done) {

            }

        }
    }


    @Override
    protected void mouseClicked(int x, int y, int button) {
        try {
            super.mouseClicked(x, y, button);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.ip.mouseClicked(x, y, button);
    }

    @Override
    public void updateScreen() {
        this.ip.updateCursorCounter();
    }

    enum State {
        NONE("Idle"),
        SEARCHING("\247aSearching [Press enter again to cancel]"),
        RESOLVING("\247aResolving [Press enter again to cancel]"),
        UNKNOWN_HOST("\2474Unknown Host"),
        CANCELLED("\2474Cancelled"),
        DONE("\247aDone"),
        ERROR("\2474An error has occurred.");

        private final String name;

        State(String name) {
            this.name = name;
        }

        public boolean isRunning() {
            return this == SEARCHING || this == RESOLVING;
        }

        @Override
        public String toString() {
            return name;
        }

    }
}
