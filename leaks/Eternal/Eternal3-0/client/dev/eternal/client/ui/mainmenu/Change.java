package dev.eternal.client.ui.mainmenu;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;


@Getter
@Setter
@AllArgsConstructor
public class Change {
  private Type type;
  private String body;

  @Getter
  @AllArgsConstructor
  public enum Type {
    ADD(0xFF00FF00, "+"), CHANGE(0xFFF7FF00, "/"), REMOVE(0xFFFF0000, "-");
    private int color;
    private String discriminator;
  }
}
