/* November.lol © 2023 */
package lol.november.feature.account;

import java.util.ArrayList;
import java.util.List;
import lol.november.November;
import lol.november.feature.account.config.AccountsConfig;
import lol.november.feature.account.microshit.MicroshitAuthenticator;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

/**
 * @author Gavin
 * @since 2.0.0
 */
@Log4j2
@Getter
public class AccountManager {

  /**
   * An {@link ArrayList} of {@link Account}
   */
  private final List<Account> accounts = new ArrayList<>();

  /**
   * The {@link MicroshitAuthenticator} authenticator
   */
  private final MicroshitAuthenticator microshit = new MicroshitAuthenticator();

  @SneakyThrows
  public AccountManager() {
    November.instance().configs().add(new AccountsConfig(this));
  }
}
