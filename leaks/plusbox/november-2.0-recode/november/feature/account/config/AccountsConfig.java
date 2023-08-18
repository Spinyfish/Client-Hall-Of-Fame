/* November.lol © 2023 */
package lol.november.feature.account.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.io.File;
import lol.november.config.Config;
import lol.november.feature.account.Account;
import lol.november.feature.account.AccountManager;
import lol.november.utility.fs.FileUtils;
import lol.november.utility.math.EncryptionUtils;
import lombok.extern.log4j.Log4j2;

/**
 * @author Gavin
 * @since 2.0.0
 */
@Log4j2
public class AccountsConfig extends Config {

  private final AccountManager accounts;
  private final int encryptionKey;

  public AccountsConfig(AccountManager accounts) {
    super(new File(FileUtils.directory, "accounts.txt"));
    this.accounts = accounts;

    encryptionKey =
      Math.abs(System.getProperty("user.name").hashCode() - 0x455dcafe);
  }

  @Override
  public void save() {
    if (!getFile().exists()) createIfNotExists();

    JsonArray array = new JsonArray();

    for (Account account : accounts.getAccounts()) {
      JsonElement element = account.toJson();
      if (element.isJsonObject()) array.add(element);
    }

    try {
      String encryptedString = new String(
        EncryptionUtils.xor4(array.toString().toCharArray(), encryptionKey)
      );
      FileUtils.writeFile(getFile(), encryptedString);
    } catch (Exception e) {
      log.error("Failed to save {}", getFile());
      e.printStackTrace();
    }
  }

  @Override
  public void load() {
    if (!getFile().exists()) return;

    String content;
    try {
      content = FileUtils.readFile(getFile());
    } catch (Exception e) {
      log.error("Failed to load {}", getFile());
      e.printStackTrace();
      return;
    }

    if (content == null || content.isEmpty()) return;

    content =
      new String(EncryptionUtils.xor4(content.toCharArray(), encryptionKey));

    // lol
    accounts.getAccounts().clear();

    JsonArray array = FileUtils.jsonParser.parse(content).getAsJsonArray();
    for (JsonElement element : array) {
      if (!element.isJsonObject()) continue;

      Account account = new Account();
      account.fromJson(element);

      if (account.getType() != null) accounts.getAccounts().add(account);
    }

    log.info("Loaded {} account(s)", accounts.getAccounts().size());
  }
}
