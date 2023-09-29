package de.verschwiegener.atero.util.account;

import de.verschwiegener.atero.Management;

import java.util.ArrayList;
import java.util.Arrays;

public class AccountManager {

    ArrayList<Account> accounts = new ArrayList<>();

    public AccountManager() {
    }

    public ArrayList<Account> getAccounts() {
        return accounts;
    }

    public Account getAccountByEmailPasswort(final String email, String passowrt) {
        return accounts.stream()
                .filter(account -> account.getEmail().equals(email) && account.getPassword().equals(passowrt))
                .findFirst().orElse(null);
    }

    public void saveAccounts() {
        try {
            ArrayList<Object[]> accountvalues = new ArrayList<>();
            for (Account account : accounts) {
                accountvalues.add(new Object[]{account.getUsername(), account.getEmail(), account.getPassword(), account.getUUID(), account.isLastLoginSuccess(), Long.toString(account.getTimeStamp()), (account.getBannedServer().length != 0 ? Arrays.toString(account.getBannedServer()) : "")});
            }
            System.out.println("AccountValues: " + accountvalues);
            Management.instance.fileManager.saveValues(new String[]{"Username", "Email", "Passwort", "UUID", "LastLoginSuccess", "Timestamp", "BannedServer"}, accountvalues, Management.instance.CLIENT_DIRECTORY, "Accounts");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed saving Accounts");
        }
    }

    public void loadAccounts() {
        try {
            ArrayList<Object[]> accountvalues = Management.instance.fileManager.loadValues(new String[]{"Username",
                            "Email", "Passwort", "UUID", "LastLoginSuccess", "Timestamp", "BannedServer"},
                    Management.instance.CLIENT_DIRECTORY, "Accounts");
            for (Object[] object : accountvalues) {
                System.out.println("Ac: " + Arrays.toString(object));
                Account account = new Account((String) object[1], (String) object[2], (String) object[0],
                        (String) object[3], (Boolean) object[4], ((String) object[6]).split(","), (String) object[5]);
                accounts.add(account);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
