package dev.eternal.client.ui.proxy.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ProxyAuthType {

  USER_PASS("Username/Password"),
  USERNAME("User only"),
  NONE("None");

  private final String authType;

  public static ProxyAuthType of(String name) {
    for (ProxyAuthType proxyType : values())
      if (proxyType.authType().equalsIgnoreCase(name))
        return proxyType;
    throw new RuntimeException("Invalid proxy auth type.");
  }

}