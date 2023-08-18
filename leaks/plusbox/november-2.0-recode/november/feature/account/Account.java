/* November.lol © 2023 */
package lol.november.feature.account;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Base64;
import lol.november.config.json.JsonSerializable;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Gavin
 * @since 2.0.0
 */
@Getter
public class Account implements JsonSerializable {

  /**
   * The {@link java.util.Base64.Decoder} for cookie accounts
   */
  private static final Base64.Decoder DECODER = Base64.getDecoder();

  private String email, password, cookie;

  @Setter
  private String username;

  private AccountType type;

  public Account() {
    // no-args constructor
  }

  /**
   * Creates a cookie alt
   *
   * @param cookie the cookie file content
   */
  public Account(String cookie) {
    this.cookie = cookie;
    type = AccountType.COOKIE;
  }

  /**
   * Creates an account
   *
   * @param email    the email/username
   * @param password the password. if null/empty, account is assumed to be {@link AccountType#CRACKED}
   */
  public Account(String email, String password) {
    this.email = email;
    if (password == null || password.isEmpty()) {
      this.username = email;
      type = AccountType.CRACKED;
    } else {
      this.password = password;
      type = AccountType.MICROSOFT;
    }
  }

  @Override
  public void fromJson(JsonElement element) {
    if (!element.isJsonObject()) return;

    JsonObject object = element.getAsJsonObject();
    if (!object.has("type")) return;

    type = AccountType.from(object.get("type").getAsString());

    switch (type) {
      case MICROSOFT -> {
        email = object.get("email").getAsString();
        password = object.get("password").getAsString();
      }
      case COOKIE -> cookie =
        new String(DECODER.decode(object.get("cookie").getAsString()));
      case CRACKED -> username = object.get("username").getAsString();
      default -> throw new RuntimeException("Unrecognized account option");
    }
  }

  @Override
  public JsonElement toJson() {
    if (type == null) throw new RuntimeException(
      "Account type is null. This shouldn't happen, but it did for some reason"
    );

    JsonObject object = new JsonObject();
    object.addProperty("type", type.getValue());

    if (username != null) object.addProperty("username", username);

    switch (type) {
      case MICROSOFT -> {
        object.addProperty("email", email);
        object.addProperty("password", password);
      }
      case COOKIE -> object.addProperty("cookie", cookie);
    }

    return object;
  }
}
