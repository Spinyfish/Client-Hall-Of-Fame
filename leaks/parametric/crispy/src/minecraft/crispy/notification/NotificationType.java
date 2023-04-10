package crispy.notification;



import java.awt.Color;

public enum NotificationType {
   SUCCESS((new Color(6348946)).getRGB()),
   INFO((new Color(0x6490A7)).getRGB()),
   ERROR((new Color(0xFF2F2F)).getRGB()),
   WARNING((new Color(0xFFE524)).getRGB());

   private final int color;

   NotificationType(int color) {
      this.color = color;
   }

   public final int getColor() {
      return this.color;
   }
}
