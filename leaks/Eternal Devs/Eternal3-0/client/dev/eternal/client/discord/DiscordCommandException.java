package dev.eternal.client.discord;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DiscordCommandException extends Exception {
  private final String failure;
}