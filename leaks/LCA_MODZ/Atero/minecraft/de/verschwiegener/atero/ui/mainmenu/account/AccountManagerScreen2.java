package de.verschwiegener.atero.ui.mainmenu.account;

import de.verschwiegener.atero.Management;
import de.verschwiegener.atero.design.font.Font;
import de.verschwiegener.atero.util.LoginUtil;
import de.verschwiegener.atero.util.Util;
import de.verschwiegener.atero.util.account.Account;
import de.verschwiegener.atero.util.account.GuiScreenAltening;
import de.verschwiegener.atero.util.chat.ChatFontRenderer;
import de.verschwiegener.atero.util.components.CustomGuiButton;
import de.verschwiegener.atero.util.components.CustomGuiImageButton;
import de.verschwiegener.atero.util.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import javax.annotation.Resource;
import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class AccountManagerScreen2 extends GuiScreen {

    ArrayList<AccountManagerItem> items = new ArrayList<>();
    ArrayList<AccountManagerItem> items2 = new ArrayList<>();
    private GuiScreen parentGUI;
    private int xOffset;
    private int selectedAccountSlot;
    private int scrollOffset, scrollOffset2;
    private Font baseFont;
    private int YOffset = 0;

    private int select = 1;

    private boolean extension;
    private int extensionID;

    public AccountManagerScreen2(GuiScreen parentGUI) {
        this.parentGUI = parentGUI;
        selectedAccountSlot = -1;
        baseFont = new Font("baseFont", Util.getFontByName("Inter-ExtraLight"), 3F);
        for (Account account : Management.instance.accountmgr.getAccounts()) {
            items.add(new AccountManagerItem(this, YOffset, account));
            loadAccounts();
            YOffset += 30;
        }
        LoginUtil.reset();
    }

    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.add(new CustomGuiImageButton(1, width - 70, (3*(height/4))-100, 65, 65, "", new ResourceLocation("atero/assets/Login.png")));
        this.buttonList.add(new CustomGuiImageButton(2, width - 62, height - 80, 63, 47, "", new ResourceLocation("atero/assets/Add.png")));
        this.buttonList.add(new CustomGuiImageButton(3, 10, height - 50, 30, 30, "" ,new ResourceLocation("atero/assets/BackArrow.png")));
        this.buttonList.add(new CustomGuiImageButton(4, width - 72, height/4+10, 65, 65, "", new ResourceLocation("atero/assets/Import.png")));
        this.buttonList.add(new CustomGuiImageButton(5, width-60, 10, 50, 50, "", new ResourceLocation("atero/assets/Altening.png")));
        items.forEach(item -> item.setExtension(false));
        items2.forEach(item -> item.setExtension(false));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        xOffset = (width / 2) - 100;
        RenderUtil.drawBackround(width, height);
        ChatFontRenderer.drawString("AccountManager", xOffset + (ChatFontRenderer.getStringWidth("AccountManager") / 4), 10, Color.WHITE);
        ChatFontRenderer.drawString("Manuell:Added", xOffset - 190 + (ChatFontRenderer.getStringWidth("Manuell:Added") / 4), 10, Color.WHITE);
        ChatFontRenderer.drawString("List:Added", xOffset + 225 + (ChatFontRenderer.getStringWidth("List:Added") / 4), 10, Color.WHITE);
        ChatFontRenderer.drawString(LoginUtil.status, 10, 10, LoginUtil.statusColor);

        for (int i = 0; i < items.size(); i++) {
            try {
                items.get(i).drawScreen(mouseX, mouseY, xOffset - 190, 40 - scrollOffset, 200, 30, (i == selectedAccountSlot && select == 1) ? true : false);
                items2.get(i).drawScreen2(mouseX, mouseY, xOffset + 190, 40 - scrollOffset2, 200, 30, (i == selectedAccountSlot && select == 2) ? true : false);
            }catch(Exception e) {}
        }
       // System.out.println(scrollOffset);
      //  System.out.println(scrollOffset);
        super.drawScreen(mouseX, mouseY, partialTicks);

        int wheelD = Mouse.getDWheel() / 24;//hochscrollen = 120; runter = -120;

        int itemX = xOffset-190;
        int item2X = xOffset+190;

        int maxX = itemX+200;
        int maxX2 = item2X+200;

        if(mouseX >= itemX && mouseX <= maxX) {
            if (wheelD != 0 && items.size() * 30 > height - 60) {
                scrollOffset += -wheelD;
                if (scrollOffset < 0) {
                    scrollOffset = 0;
                }
                if (height + scrollOffset > items.size() * 30 + 60 && scrollOffset > 0) {
                    scrollOffset = items.size() * 30 + 60 - height;
                }
            }

        }
        else if(mouseX >= item2X && mouseX <= maxX2) {
            if (wheelD != 0 && items2.size() * 30 > height - 60) {
                scrollOffset2 += -wheelD;
                if (scrollOffset2 < 0) {
                    scrollOffset2 = 0;
                }
                if (height + scrollOffset2 > items2.size() * 30 + 60 && scrollOffset2 > 0) {
                    scrollOffset2 = items2.size() * 30 + 60 - height;
                }
            }

        }
    }

    private void drawImage(int x, int y, int width, int height, ResourceLocation resourceLocation) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, width, height);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        try {
            int itemX = xOffset-190;
            int item2X = xOffset+190;

            int maxX = itemX+200;
            int maxX2 = item2X+200;
            if(mouseX >= itemX && mouseX <= maxX) {
                select = 1;
            }
            else if(mouseX >= item2X && mouseX <= maxX2) {
                select = 2;
            }
            super.mouseClicked(mouseX, mouseY, mouseButton);
            if (select == 1) {
                if (mouseButton == 0) {
                    for (int i = 0; i < items.size(); i++) {
                        if (items.get(i).isHovered(mouseX, mouseY)) {
                            if (selectedAccountSlot == i) {
                                LoginUtil.LoginAccount(items.get(i).getAccount());
                                selectedAccountSlot = 0;
                            } else {
                                selectedAccountSlot = i;
                            }
                            break;
                        }
                        selectedAccountSlot = -1;
                    }
                }
                System.out.println("test");
            }
            if (!items.isEmpty()) {
                //Throws exception when the item is deleted
                try {
                    items.forEach(item -> item.mouseClicked(mouseX, mouseY, mouseButton));
                } catch (Exception e) {
                }
            }
            if (select == 2) {
                if (mouseButton == 0) {
                    for (int i = 0; i < items2.size(); i++) {
                        if (items2.get(i).isHovered(mouseX, mouseY)) {
                            if (selectedAccountSlot == i) {
                                LoginUtil.LoginAccount(items2.get(i).getAccount());
                                selectedAccountSlot = 0;
                            } else {
                                selectedAccountSlot = i;
                            }
                            break;
                        }
                        selectedAccountSlot = -1;
                    }
                }
            }
            if (!items2.isEmpty()) {
                //Throws exception when the item is deleted
                try {
                    items2.forEach(item -> item.mouseClicked(mouseX, mouseY, mouseButton));
                } catch (Exception e) {
                }
            }
        } catch(Exception e) {}
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 1:
                if (selectedAccountSlot != -1) {
                    LoginUtil.LoginAccount(select == 1 ? items.get(selectedAccountSlot).getAccount() : items2.get(selectedAccountSlot).getAccount());
                } else {
                    mc.displayGuiScreen(new AccountLogin(this, "Login", false));
                }
                break;
            case 2:
                mc.displayGuiScreen(new AccountLogin(this, "Add", false));
                break;

            case 3:
                mc.displayGuiScreen(parentGUI);
                break;
            case 4:
                this.items2.clear();
                JFileChooser chooser = new JFileChooser();
                int returnValue = chooser.showOpenDialog(null);
                List<Account> accountsFromImport = new ArrayList<>();
                if(returnValue == JFileChooser.APPROVE_OPTION) {
//                    System.out.println(chooser.getSelectedFile().toString());

                    BufferedReader reader = new BufferedReader(new FileReader(chooser.getSelectedFile()));

                    String line = reader.readLine();
                    while (line != null) {
                        line = reader.readLine();
                        try {
                            accountsFromImport.add(new Account(line.split(":")[0], line.split(":")[1]));
                        } catch(Exception e) {}

                    }

                }
                int yOffsetTemp = 0;
                for(Account account : accountsFromImport) {
                        this.items2.add(new AccountManagerItem(this, yOffsetTemp, account));
                    yOffsetTemp+=30;
                }
                break;
            case 5:
                mc.displayGuiScreen(new AccountLogin(this, "Login", true));
                break;
        }
    }

    public void handleArgs(String mode, String email, String passwort, boolean altening) {
        if (mode.equalsIgnoreCase("Login")) {
            if(!altening) {
                LoginUtil.login(email, passwort);
            }else {
                LoginUtil.loginAltening(email);
            }
        } else if (mode.equalsIgnoreCase("Add")) {
            Account account = new Account(email, passwort);
            Management.instance.accountmgr.getAccounts().add(account);
            items.add(new AccountManagerItem(this, YOffset, account));
            YOffset += 15;
        } else if (mode.equalsIgnoreCase("Edit")) {
            Account account = Management.instance.accountmgr.getAccounts().get(Management.instance.accountmgr.getAccounts().indexOf(items.get(extensionID).getAccount()));
            account.setEmail(email);
            account.setPassword(passwort);
        }
    }

    public Font getBaseFont() {
        return baseFont;
    }

    public void setExtension(boolean extension) {
        this.extension = extension;
    }

    public void setExtensionID(int extensionID) {
        this.extensionID = extensionID;
    }

    @Override
    public void onGuiClosed() {
        try {
            ArrayList<Object[]> accountvalues = new ArrayList<>();
            for (AccountManagerItem itm : this.items2) {
                Account account = itm.getAccount();

                if(account.getUUID().trim().equalsIgnoreCase("") || account.getUUID() == null) {
                    account.setUUID(UUID.randomUUID().toString());
                }
                System.out.println(account.getUUID().trim().equalsIgnoreCase("") || account.getUUID() == null);
                accountvalues.add(new Object[]{account.getUsername(), account.getEmail(), account.getPassword(), account.getUUID(), account.isLastLoginSuccess(), Long.toString(account.getTimeStamp()), (account.getBannedServer().length != 0 ? Arrays.toString(account.getBannedServer()) : "")});

            }
            Management.instance.fileManager.saveValues(new String[]{"Username", "Email", "Passwort", "UUID", "LastLoginSuccess", "Timestamp", "BannedServer"}, accountvalues, Management.instance.CLIENT_DIRECTORY, "AccountsList");
        } catch(Exception e) {}
        super.onGuiClosed();
    }

    public void loadAccounts() {
        int yOffsetTemp = 0;
        try {
            ArrayList<Object[]> accountvalues = Management.instance.fileManager.loadValues(new String[]{"Username",
                            "Email", "Passwort", "UUID", "LastLoginSuccess", "Timestamp", "BannedServer"},
                    Management.instance.CLIENT_DIRECTORY, "AccountsList");
            for (Object[] object : accountvalues) {
                System.out.println("Ac: " + Arrays.toString(object));
                System.out.println(object[3]);
                Account account = new Account((String) object[1], (String) object[2], (String) object[0],
                        ""+object[3], (Boolean) object[4], ((String) object[6]).split(","), (String) object[5]);
                items2.add(new AccountManagerItem(this, yOffsetTemp, account));
                yOffsetTemp+= 30;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
