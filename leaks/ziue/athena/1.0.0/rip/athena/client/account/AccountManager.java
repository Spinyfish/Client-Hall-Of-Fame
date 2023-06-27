package rip.athena.client.account;

import net.minecraft.client.*;
import rip.athena.client.*;
import java.util.*;
import java.io.*;
import net.minecraft.util.*;
import fr.litarvan.openauth.microsoft.*;

public class AccountManager
{
    private Minecraft mc;
    private ArrayList<Account> accounts;
    public boolean isFirstLogin;
    private Account currentAccount;
    
    public AccountManager() {
        this.mc = Minecraft.getMinecraft();
        this.accounts = new ArrayList<Account>();
        this.isFirstLogin = false;
        if (Athena.ACCOUNTS_DIR.length() == 0L) {
            this.isFirstLogin = true;
        }
        this.load();
        this.login(this.currentAccount);
    }
    
    public void save() {
        final ArrayList<String> toSave = new ArrayList<String>();
        for (final Account a : Athena.INSTANCE.getAccountManager().getAccounts()) {
            toSave.add("Account:" + a.getAccountType().toString() + ":" + a.getUsername() + ":" + a.getUuid() + ":" + a.getToken());
        }
        toSave.add("Current:" + this.getCurrentAccount().getUsername());
        try {
            final PrintWriter pw = new PrintWriter(Athena.ACCOUNTS_DIR);
            for (final String str : toSave) {
                pw.println(str);
            }
            pw.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    public void load() {
        final ArrayList<String> lines = new ArrayList<String>();
        try {
            final BufferedReader reader = new BufferedReader(new FileReader(Athena.ACCOUNTS_DIR));
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                lines.add(line);
            }
            reader.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        for (final String s : lines) {
            final String[] args = s.split(":");
            if (s.toLowerCase().startsWith("account:")) {
                AccountType accountType = AccountType.MICROSOFT;
                if (args[1].equals("MICROSOFT")) {
                    accountType = AccountType.MICROSOFT;
                }
                if (args[1].equals("SESSION")) {
                    accountType = AccountType.SESSION;
                }
                if (args[1].equals("CRACKED")) {
                    accountType = AccountType.CRACKED;
                }
                this.accounts.add(new Account(accountType, args[2], args[3], args[4]));
            }
            if (s.toLowerCase().startsWith("current:")) {
                this.setCurrentAccount(this.getAccountByUsername(args[1]));
            }
        }
    }
    
    private void login(final Account a) {
        if (a == null) {
            return;
        }
        if (a.getAccountType().equals(AccountType.MICROSOFT)) {
            final MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
            a.setInfo("Loading...");
            try {
                final MicrosoftAuthResult acc = authenticator.loginWithRefreshToken(a.getToken());
                this.mc.session = new Session(acc.getProfile().getName(), acc.getProfile().getId(), acc.getAccessToken(), "legacy");
                a.setInfo("Success");
            }
            catch (MicrosoftAuthenticationException e) {
                e.printStackTrace();
                a.setInfo("Error");
            }
        }
        if (a.getAccountType().equals(AccountType.SESSION)) {
            a.setInfo("Loading...");
            try {
                this.mc.session = new Session(a.getUsername(), a.getUuid(), a.getToken(), "mojang");
                a.setInfo("Success!");
            }
            catch (Exception e2) {
                e2.printStackTrace();
                a.setInfo("Error");
            }
        }
        if (a.getAccountType().equals(AccountType.CRACKED)) {
            a.setInfo("Success");
            this.mc.session = new Session(a.getUsername(), "0", "0", "legacy");
        }
    }
    
    public Account getAccountByUsername(final String name) {
        return this.accounts.stream().filter(account -> account.getUsername().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
    
    public ArrayList<Account> getAccounts() {
        return this.accounts;
    }
    
    public Account getCurrentAccount() {
        return this.currentAccount;
    }
    
    public void setCurrentAccount(final Account currentAccount) {
        this.currentAccount = currentAccount;
    }
}
