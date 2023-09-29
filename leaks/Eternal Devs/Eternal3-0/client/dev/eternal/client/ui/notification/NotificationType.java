package dev.eternal.client.ui.notification;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.ResourceLocation;

@RequiredArgsConstructor
@Getter
public enum NotificationType {

  //The icons look like shit
  //TODO: new icons
  WARNING(0xFFf1c40f, new ResourceLocation("eternal/noti/warning.png")),
  ERROR(0xFFe74c3c, new ResourceLocation("eternal/noti/error.png")),
  INFO(0xFF3498db, new ResourceLocation("eternal/noti/info.png")),
  SUCCESS(0xFF6ab04c, new ResourceLocation("eternal/noti/check.png"));

  private final int colour;
  private final ResourceLocation icon;

}
