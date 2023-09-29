package dev.eternal.client.ui.proxy.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ProxyType {

  SOCKS5("SOCKSv5"),
  SOCKS4("SOCKSv4"),
  HTTP("HTTP"),
  HTTPS("HTTPS"),
  NONE("None");

  private final String proxyType;

  public static ProxyType of(String name) {
    for (ProxyType proxyType : values())
      if (proxyType.proxyType().equalsIgnoreCase(name))
        return proxyType;
    throw new RuntimeException("Invalid proxy type.");
  }

}
