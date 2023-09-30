package wtf.automn.gui.alt.management;

import com.google.gson.annotations.Expose;
import wtf.automn.utils.io.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AccountManager {

  @Expose
  private List<Account> accounts = new ArrayList<>();

  public AccountManager() {
  }

  public void init() {
    File dir = FileUtil.getDirectory("accounts");
    if (dir.list() == null || dir.list().length == 0) return;
    for (String s : dir.list()) {
      if (!s.endsWith(".json"))
        continue;
      String uuid = s.substring(0, s.length() - 5);
      Account acc = new Account(uuid, dir);
      if (!acc.getOwner().equalsIgnoreCase("None") && !acc.getToken().equalsIgnoreCase("None")
        && !acc.getUUIDString().equalsIgnoreCase("None")) {
        this.addAccount(acc);
      }
    }
  }

  public void save() {
    for (Account account : getAccounts()) {
      File dir = FileUtil.getDirectory("accounts");
      account.writeToFile(dir);
    }
  }

  public void shutdown() {
    try {
      save();
      for (Account acc : getAccounts()) {
        removeAccount(acc);
      }
    } catch (Exception e) {
    }
  }

  public void addAccount(Account acc) {
    this.accounts.add(acc);
  }

  public void removeAccount(Account acc) {
    this.accounts.remove(acc);
  }

  public List<Account> getAccounts() {
    return accounts;
  }

  public Account getAccountByName(String name) {
    for (Account acc : getAccounts()) {
      if (acc.getOwner().equalsIgnoreCase(name))
        return acc;
    }
    return null;
  }

  public List<Account> getAccountsBySearch(String name) {
    List<Account> accounts = new ArrayList<>();
    for (Account acc : getAccounts()) {
      if (acc.getOwner().startsWith(name))
        accounts.add(acc);
    }
    return accounts;
  }

}
