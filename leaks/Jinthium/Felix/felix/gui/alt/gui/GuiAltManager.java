package felix.gui.alt.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

import javax.swing.*;

import com.mojang.realmsclient.gui.ChatFormatting;

import felix.Client;
import felix.gui.alt.gui.components.GuiAccountList;
import felix.gui.alt.gui.impl.AccountImport;
import felix.gui.alt.gui.impl.GuiAddAlt;
import felix.gui.alt.gui.impl.GuiAltLogin;
import felix.gui.alt.gui.impl.GuiAlteningLogin;
import felix.gui.alt.gui.thread.AccountLoginThread;
import felix.gui.alt.system.Account;
import felix.gui.menu.ClientMainMenu;
import felix.util.font.FontRenderer;

import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;

public class GuiAltManager extends GuiScreen {
    public static GuiAltManager INSTANCE;
    private GuiAccountList accountList;
    private Account selectAccount = null;
    public static Account currentAccount;
    public static AccountLoginThread loginThread;
    private final Random random = new Random();

    public GuiAltManager() {
        INSTANCE = this;
    }

    public void initGui() {
        accountList = new GuiAccountList(this);
        accountList.registerScrollButtons(7, 8);
        accountList.elementClicked(-1, false, 0, 0);

        this.buttonList.add(new GuiButton(0, this.width / 2 + 158, this.height - 24, 100, 20, "Cancel"));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 154, this.height - 48, 100, 20, "Login"));
        this.buttonList.add(new GuiButton(2, this.width / 2 - 50, this.height - 24, 100, 20, "Remove"));
        this.buttonList.add(new GuiButton(5, this.width / 2 + 4 + 50, this.height - 48, 100, 20, "Import Alts"));
        this.buttonList.add(new GuiButton(4, this.width / 2 - 50, this.height - 48, 100, 20, "Direct Login"));
        this.buttonList.add(new GuiButton(3, this.width / 2 - 154, this.height - 24, 100, 20, "Add Alt"));
        this.buttonList.add(new GuiButton(6, this.width / 2 - 258, this.height - 24, 100, 20, "TheAltening"));
        this.buttonList.add(new GuiButton(7, this.width / 2 + 54, this.height - 24, 100, 20, "Random Alt"));
        this.buttonList.add(new GuiButton(8, this.width / 2 - 258, this.height - 48, 100, 20, "Last Alt"));
        this.buttonList.add(new GuiButton(9, this.width / 2 + 158, this.height - 48, 100, 20, "Clear Alts"));
    }

    @Override
    public void drawScreen(int p_drawScreen_1_, int p_drawScreen_2_, float p_drawScreen_3_){
    	
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        
    	
        accountList.drawScreen(p_drawScreen_1_, p_drawScreen_2_, p_drawScreen_3_);
        super.drawScreen(p_drawScreen_1_, p_drawScreen_2_, p_drawScreen_3_);

        String status = "Idle...";

        if(loginThread != null) status = loginThread.getStatus();
        final FontRenderer fr = Client.INSTANCE.getFontManager().getFont("Display 20", true);
        
        fr.drawCenteredStringWithShadow(ChatFormatting.GREEN + mc.session.getUsername(), width / 23, 13, -1);
        fr.drawCenteredStringWithShadow("Account Manager - " + Client.INSTANCE.getAccountManager().getAccounts().size() + " alts", width / 2, 4, -1);
        fr.drawCenteredStringWithShadow(loginThread == null ? ChatFormatting.GRAY + "Idle..." : loginThread.getStatus(), width / 2, 16, -1);
    }

    @Override
    public void handleMouseInput() throws IOException{
        super.handleMouseInput();
        accountList.handleMouseInput();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException{
        switch (button.id) {
            case 0:
                if (loginThread == null || !loginThread.getStatus().contains("Logging in")) {
                    mc.displayGuiScreen(new ClientMainMenu());
                }
                break;
            case 1:
                if(accountList.selected == -1)
                    return;

                loginThread = new AccountLoginThread(accountList.getSelectedAccount().getEmail(),accountList.getSelectedAccount().getPassword());
                loginThread.start();
                break;
            case 2:
                accountList.removeSelected();
                break;
            case 3:
                if (loginThread != null)
                    loginThread = null;

                mc.displayGuiScreen(new GuiAddAlt(this));
                break;
            case 4:
                if (loginThread != null)
                    loginThread = null;

                mc.displayGuiScreen(new GuiAltLogin(this));
                break;
            case 6:
                mc.displayGuiScreen(new GuiAlteningLogin(this));
                break;
            case 7:
                ArrayList<Account> registry = Client.INSTANCE.getAccountManager().getAccounts();
                if (registry.isEmpty()) return;
                Random random = new Random();
                Account randomAlt = registry.get(random.nextInt(Client.INSTANCE.getAccountManager().getAccounts().size()));
                if(randomAlt.isBanned())
                    return;

                currentAccount = randomAlt;
                login(randomAlt);
                break;
            case 5:
            	 JFrame frame = new JFrame("Import");
                 frame.setAlwaysOnTop(true);
                 AccountImport accountImport = new AccountImport();
                 frame.setContentPane(accountImport);
                 new Thread(() -> accountImport.openButton.doClick()).start();
                 break;
            case 8:
                if(Client.INSTANCE.getAccountManager().getLastAlt() == null)
                    return;
                loginThread = new AccountLoginThread(Client.INSTANCE.getAccountManager().getLastAlt().getEmail(),Client.INSTANCE.getAccountManager().getLastAlt().getPassword());
                loginThread.start();
                break;
            case 9:
                if (Client.INSTANCE.getAccountManager().getAccounts().isEmpty()) return;
                Client.INSTANCE.getAccountManager().getAccounts().clear();
                break;
        }
    }

    public void login(Account account){
        loginThread = new AccountLoginThread(account.getEmail(),account.getPassword());
        loginThread.start();
    }
}
