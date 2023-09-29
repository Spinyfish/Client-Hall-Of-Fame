package dev.eternal.client.font;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FontType {

  ROBOTO_REGULAR("Roboto Regular", "Roboto-Regular"),
  ROBOTO_MEDIUM("Roboto Medium", "Roboto-Medium"),
  ROBOTO_ITALIC("Roboto Italic", "Roboto-Italic"),
  ROBOTO_LIGHT("Roboto Light", "Roboto-Light"),
  ROBOTO_BOLD("Roboto Bold", "Roboto-Bold"),
  ROBOTO_THIN("Roboto Thin", "Roboto-Thin"),
  LEMONMILK_BOLD("LemonMilk Bold", "LEMONMILK-Bold"),
  ICIEL("iCiel Medium", "iCiel");

  private final String fontName;
  private final String pathName;

}
