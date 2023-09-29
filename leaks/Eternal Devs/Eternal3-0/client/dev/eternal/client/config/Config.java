package dev.eternal.client.config;

import java.io.File;
import java.util.Date;
import java.util.Objects;

public record Config(File configFile, String author, String name, Date creation, String version) {

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Config config = (Config) o;
    return Objects.equals(name, config.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }
}
