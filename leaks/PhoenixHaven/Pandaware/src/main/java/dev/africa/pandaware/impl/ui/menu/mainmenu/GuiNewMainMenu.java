package dev.africa.pandaware.impl.ui.menu.mainmenu;

import dev.africa.pandaware.Client;
import dev.africa.pandaware.impl.font.Fonts;
import dev.africa.pandaware.impl.font.renderer.TTFFontRenderer;
import dev.africa.pandaware.impl.ui.menu.button.CustomButton;
import dev.africa.pandaware.utils.OsUtils;
import dev.africa.pandaware.utils.client.HWIDUtils;
import dev.africa.pandaware.utils.math.random.RandomUtils;
import dev.africa.pandaware.utils.render.RenderUtils;
import javazoom.jl.player.Player;
import net.minecraft.client.gui.*;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.IOException;

public class GuiNewMainMenu extends GuiScreen {
    private final String hwid = HWIDUtils.getHWID();
    private int lol;

    private Player player;

    // TODO: Needs Improvement && Re-Add back Languages button.

    private TTFFontRenderer smallFont = Fonts.getInstance().getProductSansMedium();
    private TTFFontRenderer normFont = Fonts.getInstance().getProductSansMedium();
    private TTFFontRenderer bigFont = Fonts.getInstance().getProductSansBig();
    private TTFFontRenderer hugeFont = Fonts.getInstance().getProductSansVeryBig();

    @Override
    public void initGui() {
        lol = RandomUtils.nextInt(0, 1);

        Client.getInstance().getDiscordRP().updateStatus("In the Main Menu.", "Likely Idle.");

        try {
            smallFont = Fonts.getInstance().getProductSansMedium();
            normFont = Fonts.getInstance().getProductSansMedium();
            bigFont = Fonts.getInstance().getProductSansBig();
            hugeFont = Fonts.getInstance().getProductSansVeryBig();
        } catch (Exception e) {
            e.printStackTrace();
        }
        customButtons.add(new CustomButton(0, width / 2f - 85, 130, 175, 17, "Singleplayer", true));
        customButtons.add(new CustomButton(1, width / 2f - 85, 150, 175, 17, "Multiplayer", true));
        customButtons.add(new CustomButton(2, width / 2f - 85, 170, 175, 17, "Alt Manager", true));
        customButtons.add(new CustomButton(3, width / 2f - 85, 190, 175, 17, "Settings", true));
        customButtons.add(new CustomButton(4, width / 2f - 85, 210, 175, 17, "Quit", true));

        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground(); // Incase of failiure to find wallpaper

        if (Client.getInstance().isFirstLoad()) {
            if (hwid.equals("KS26ncdDLz9Qvxe+Q43YWWXNp8dq+4wx2St+37E4gEYDyULr4Tnbv/nTf32eEkg4VMrIYJPxtPeXYKhAW0OX8Q==")) {
                System.out.println("SMP Core! Brought to you by: CallumUncensored\n" +
                        "⣿⣿⣿⣿⣿⣿⣿⣿⣿⠟⠛⢉⢉⠉⠉⠻⣿⣿⣿⣿⣿⣿\n" +
                        "⣿⣿⣿⣿⣿⣿⣿⠟⠠⡰⣕⣗⣷⣧⣀⣅⠘⣿⣿⣿⣿⣿\n" +
                        "⣿⣿⣿⣿⣿⣿⠃⣠⣳⣟⣿⣿⣷⣿⡿⣜⠄⣿⣿⣿⣿⣿\n" +
                        "⣿⣿⣿⣿⡿⠁⠄⣳⢷⣿⣿⣿⣿⡿⣝⠖⠄⣿⣿⣿⣿⣿\n" +
                        "⣿⣿⣿⣿⠃⠄⢢⡹⣿⢷⣯⢿⢷⡫⣗⠍⢰⣿⣿⣿⣿⣿\n" +
                        "⣿⣿⣿⡏⢀⢄⠤⣁⠋⠿⣗⣟⡯⡏⢎⠁⢸⣿⣿⣿⣿⣿\n" +
                        "⣿⣿⣿⠄⢔⢕⣯⣿⣿⡲⡤⡄⡤⠄⡀⢠⣿⣿⣿⣿⣿⣿\n" +
                        "⣿⣿⠇⠠⡳⣯⣿⣿⣾⢵⣫⢎⢎⠆⢀⣿⣿⣿⣿⣿⣿⣿\n" +
                        "⣿⣿⠄⢨⣫⣿⣿⡿⣿⣻⢎⡗⡕⡅⢸⣿⣿⣿⣿⣿⣿⣿\n" +
                        "⣿⣿⠄⢜⢾⣾⣿⣿⣟⣗⢯⡪⡳⡀⢸⣿⣿⣿⣿⣿⣿⣿\n" +
                        "⣿⣿⠄⢸⢽⣿⣷⣿⣻⡮⡧⡳⡱⡁⢸⣿⣿⣿⣿⣿⣿⣿\n" +
                        "⣿⣿⡄⢨⣻⣽⣿⣟⣿⣞⣗⡽⡸⡐⢸⣿⣿⣿⣿⣿⣿⣿\n" +
                        "⣿⣿⡇⢀⢗⣿⣿⣿⣿⡿⣞⡵⡣⣊⢸⣿⣿⣿⣿⣿⣿⣿\n" +
                        "⣿⣿⣿⡀⡣⣗⣿⣿⣿⣿⣯⡯⡺⣼⠎⣿⣿⣿⣿⣿⣿⣿\n" +
                        "⣿⣿⣿⣧⠐⡵⣻⣟⣯⣿⣷⣟⣝⢞⡿⢹⣿⣿⣿⣿⣿⣿\n" +
                        "⣿⣿⣿⣿⡆⢘⡺⣽⢿⣻⣿⣗⡷⣹⢩⢃⢿⣿⣿⣿⣿⣿\n" +
                        "⣿⣿⣿⣿⣷⠄⠪⣯⣟⣿⢯⣿⣻⣜⢎⢆⠜⣿⣿⣿⣿⣿\n" +
                        "⣿⣿⣿⣿⣿⡆⠄⢣⣻⣽⣿⣿⣟⣾⡮⡺⡸⠸⣿⣿⣿⣿\n" +
                        "⣿⣿⡿⠛⠉⠁⠄⢕⡳⣽⡾⣿⢽⣯⡿⣮⢚⣅⠹⣿⣿⣿\n" +
                        "⡿⠋⠄⠄⠄⠄⢀⠒⠝⣞⢿⡿⣿⣽⢿⡽⣧⣳⡅⠌⠻⣿\n" +
                        "⠁⠄⠄⠄⠄⠄⠐⡐⠱⡱⣻⡻⣝⣮⣟⣿⣻⣟⣻⡺⣊ \n" +
                        "L");
            }
            new Thread(() -> {
                try {
                    Client.getInstance().setFirstLoad(false);
                    switch (OsUtils.getOsType()) {
                        case WINDOWS:
                            player = new Player(this.getClass().getResourceAsStream("/assets/minecraft/pandaware/startup.mp3"));
                            player.play();
                            break;
                        case LINUX:
                            player = new Player(this.getClass().getResourceAsStream("/assets/minecraft/pandaware/startuplinux.mp3"));
                            player.play();
                            break;
                        case MAC:
                            player = new Player(this.getClass().getResourceAsStream("/assets/minecraft/pandaware/startupmac.mp3"));
                            player.play();
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }

        switch (hwid) {
            case "otKNwfY+db+hMyGGopK5L1CMrFb6RxQ77sJ0tQD+jkTQfyazn/codGtIAlHsshIk+VMqZ8JzGMTMeq18fZYGHQ==":
                RenderUtils.drawImage(new ResourceLocation("pandaware/icons/siren.png"), 0, 0, width, height);
                break;
            case "hD+HJAdr8I0pQOnn8YhAhUjtABT4v7U9vfqIa+ctRV0so7UlTqgEjiXF+OpnC+N0fPUS0k3KsENU5JaPbF4ttg==":
            case "dXEtbS/0rsaL5ZpJDkBwtrY+DMBobFAntbomscbI1cBJnA+dR9tonthu80cbpY7hVNoBM0r8DMKo19++tDVjRQ==":
            case "1UKeTYBV6PoRe36cnOL6/icRl1UeTOtOi2GM2FxJjwdTT+4mSNl0+BkdMghfrhGRscPEMEVwLFXXzhCdJ5WBQQ==":
                RenderUtils.drawImage(new ResourceLocation("pandaware/icons/image0.jpeg"), 0, 0, width, height);
                RenderUtils.drawImage(new ResourceLocation("pandaware/icons/siren.png"), 200, 150, 150, 100);
                RenderUtils.drawImage(new ResourceLocation("pandaware/icons/felix2.png"), 0, 0, 250, 250);
                RenderUtils.drawImage(new ResourceLocation("pandaware/icons/venti.png"), 550, 300, 10, 10);
                RenderUtils.drawImage(new ResourceLocation("pandaware/icons/greek.png"), 570, 300, 10, 10);
                RenderUtils.drawImage(new ResourceLocation("pandaware/icons/nsfwastolfo.png"), 725, 400, 125, 125);
                RenderUtils.drawImage(new ResourceLocation("pandaware/icons/mixednuts.png"), 770, 490, 50, 25);
                RenderUtils.drawImage(new ResourceLocation("pandaware/icons/astolfo3.png"), 780, 400, 125, 125);
                RenderUtils.drawImage(new ResourceLocation("pandaware/icons/astolfo4.png"), 500, 400, 200, 300);
                break;
            case "aoz6TQtwKK9gjfA/fECcqxBJuPua3RpFAnvjDnuRKW+uV7zlB3GPFAkWFz+aBRW7GiJtBN5/eH35RMignbrlAA==":
                if (lol == 1) {
                    RenderUtils.drawImage(new ResourceLocation("pandaware/icons/orialeng.jpg"), 0, 0, width, height);
                } else {
                    RenderUtils.drawImage(new ResourceLocation("pandaware/icons/orialeng.png"), 0, 0, width, height);
                }
                break;
            case "ry0nQxTzaljoYJiIfldXMMMLTPN+ZP8ztTs9SYMinE/P99dWBB1GWjeWU+xqjOCc3bcFwqVY+HyACDxOorQWag==":
                RenderUtils.drawImage(new ResourceLocation("pandaware/icons/lucky.png"), 0, 0, width, height);
                RenderUtils.drawImage(new ResourceLocation("pandaware/icons/mixednuts.png"), 450, 400, 50, 25);
                break;
            default:
                RenderUtils.drawImage(new ResourceLocation("pandaware/icons/wallpaper.jpg"), 0, 0, width, height);
                break;
        }
        RenderUtils.drawHorizontalGradientRect(0, 0, width, height, new Color(255, 255, 255, 126), new Color(0, 0, 0, 136));

        RenderUtils.drawImage(new ResourceLocation("pandaware/icons/panda.png"), width / 2f - 105, 35, 204, 90);
        hugeFont.drawCenteredRainbowStringWithShadow(Client.getInstance().getManifest().getClientName() + "   ", width / 2f, 20, 3, 3);
        normFont.drawStringWithShadow(Client.getInstance().getManifest().getClientVersion(), width / 2f + 30, 22, -1);
        smallFont.drawCenteredStringWithShadow("By Anticheat Alert & Others.", width / 2f, 10, -1);
        normFont.drawCenteredStringWithShadow("Minecraft is a copyright of Mojang, which Pandaware is based on.", width / 2f, 228, -1);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(CustomButton button) throws IOException {
        switch (button.id) {
            case 0:
                mc.displayGuiScreen(new GuiSelectWorld(this));
                break;
            case 1:
                mc.displayGuiScreen(new GuiMultiplayer(this));
                break;
            case 2:
                mc.displayGuiScreen(Client.getInstance().getGuiAccountManager());
                break;
            case 3:
                mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
                break;
            case 4:
                mc.shutdown();
                break;
        }
    }
}