package dev.eternal.client;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClientSettings {
  private final String name, version;
  private String commandPrefix;
}
