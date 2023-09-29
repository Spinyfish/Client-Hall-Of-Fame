package de.verschwiegener.atero.ui.mainmenu.account;

import de.verschwiegener.atero.Management;
import de.verschwiegener.atero.design.font.Font;
import de.verschwiegener.atero.util.account.Account;
import de.verschwiegener.atero.util.render.RenderUtil;
import net.minecraft.client.Minecraft;

import java.awt.*;
import java.sql.Date;
import java.text.SimpleDateFormat;

public class AccountManagerItem {

    private AccountManagerScreen2 screen;
    private int YOffset;
    private Account account;
    private Font font;
    private int x, y;
    private int listwidth, slotheight;
    private Font baseFont;
    private boolean extension;
    private int extensionX, extensionY;

    public AccountManagerItem(AccountManagerScreen2 screen, int YOffset, Account account) {
        this.screen = screen;
        this.YOffset = YOffset;
        this.account = account;
        font = Management.instance.font;
        baseFont = screen.getBaseFont();
    }

    public void drawScreen(int mouseX, int mouseY, int x, int y, int listwidth, int slotheight, boolean isSelected) {
        this.x = x;
        this.y = y;
        this.listwidth = listwidth;
        this.slotheight = slotheight;
        RenderUtil.fillRect(x, y + YOffset, listwidth, slotheight, Management.instance.colorGray);
        RenderUtil.drawRect(x, y + YOffset, listwidth, slotheight, Management.instance.colorBlack, 1);
        if (account.getLocation() != null) {
            RenderUtil.drawImage(account.getLocation(), x + 1, y + YOffset + 1, 28, slotheight - 2);
        }
        font.drawString((account.getUsername() == "") ? account.getEmail() : account.getUsername(), x + 31, y + 1 + YOffset, Color.WHITE.getRGB());
        baseFont.drawString(account.isLastLoginSuccess() ? "Last Login: Sucessful" : "Last Login: Unsuccessful", x + 31, y + 12 + YOffset, Color.WHITE);
        if (account.getTimeStamp() != 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy");
            Date resultdate = new Date(account.getTimeStamp());
            baseFont.drawString("Last Login: " + sdf.format(resultdate), x + 31, y + 20 + YOffset, Color.WHITE);
        }
        if (isSelected) {
            RenderUtil.drawRect(x + 1, y + YOffset + 1, listwidth - 1, slotheight - 2, Color.GRAY, 1);
        }
        if (extension) {
            RenderUtil.fillRect(extensionX, extensionY, 50, 20, Management.instance.colorBlack);
            baseFont.drawString("Edit", extensionX + 1, extensionY, Color.WHITE);
            baseFont.drawString("Remove", extensionX + 1, extensionY + 10, Color.WHITE);
        }
    }


    public void drawScreen2(int mouseX, int mouseY, int x, int y, int listwidth, int slotheight, boolean isSelected) {
        this.x = x;
        this.y = y;
        this.listwidth = listwidth;
        this.slotheight = slotheight;
        RenderUtil.fillRect(x, y + YOffset, listwidth, slotheight, Management.instance.colorGray);
        RenderUtil.drawRect(x, y + YOffset, listwidth, slotheight, Management.instance.colorBlack, 1);
        if (account.getLocation() != null) {
            RenderUtil.drawImage(account.getLocation(), x + 1, y + YOffset + 1, 28, slotheight - 2);
        }
        font.drawString((account.getUsername() == "") ? account.getEmail() : account.getUsername(), x + 31, y + 1 + YOffset, Color.WHITE.getRGB());
        baseFont.drawString(account.isLastLoginSuccess() ? "Last Login: Sucessful" : "Last Login: Unsuccessful", x + 31, y + 12 + YOffset, Color.WHITE);
        if (account.getTimeStamp() != 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy");
            Date resultdate = new Date(account.getTimeStamp());
            baseFont.drawString("Last Login: " + sdf.format(resultdate), x + 31, y + 20 + YOffset, Color.WHITE);
        }
        if (isSelected) {
            RenderUtil.drawRect(x + 1, y + YOffset + 1, listwidth - 1, slotheight - 2, Color.GRAY, 1);
        }
        if (extension) {
            RenderUtil.fillRect(extensionX, extensionY, 50, 20, Management.instance.colorBlack);
            baseFont.drawString("Edit", extensionX + 1, extensionY, Color.WHITE);
            baseFont.drawString("Remove", extensionX + 1, extensionY + 10, Color.WHITE);
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (extension) {
            if (mouseButton == 0) {
                boolean flagedit = mouseX > extensionX && mouseX < extensionX + 50 && mouseY > extensionY
                        && mouseY < extensionY + 10;
                boolean flagremove = mouseX > extensionX && mouseX < extensionX + 50 && mouseY > extensionY + 10
                        && mouseY < extensionY + 20;
                if (flagedit) {
                    Minecraft.getMinecraft().displayGuiScreen(
                            new AccountLogin(screen, "Edit", account.getEmail(), account.getPassword()));
                } else if (flagremove) {
                    System.out.println("Remove");
                    Management.instance.accountmgr.getAccounts().remove(account);
                    System.out.println("Accounts: " + Management.instance.accountmgr.getAccounts());
                    screen.items.remove(this);
                }
            }
        }
        extension = false;
        if (mouseButton == 1) {
            if (isHovered(mouseX, mouseY)) {
                extensionX = mouseX;
                extensionY = mouseY;
                extension = true;
                screen.setExtensionID(screen.items.indexOf(this));
            }
        }
        screen.setExtension(extension);
    }

    public boolean isHovered(int mouseX, int mouseY) {
        if (mouseX > x && mouseX < x + listwidth) {
            if (mouseY > y + YOffset && mouseY < y + YOffset + slotheight) {
                return true;
            }
        }
        return false;
    }

    public Account getAccount() {
        return account;
    }

    public void setExtension(boolean extension) {
        this.extension = extension;
    }

}
