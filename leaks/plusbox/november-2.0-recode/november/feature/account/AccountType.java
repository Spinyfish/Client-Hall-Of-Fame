/* November.lol © 2023 */
package lol.november.feature.account;

import lombok.Getter;

/**
 * @author Gavin
 * @since 2.0.0
 */
@Getter
public enum AccountType {
  MICROSOFT("microsoft"),
  COOKIE("cookie"),
  CRACKED("cracked");

  private final String value;

  AccountType(String value) {
    this.value = value;
  }

  /**
   * Gets the account type enum constant
   *
   * @param name the value
   * @return the {@link AccountType}
   * @throws IllegalArgumentException if the enum value was invalid
   */
  public static AccountType from(String name) {
    for (AccountType type : values()) {
      if (type.getValue().equalsIgnoreCase(name)) return type;
    }

    throw new IllegalArgumentException("Invalid enum type \"" + name + "\"");
  }
}
