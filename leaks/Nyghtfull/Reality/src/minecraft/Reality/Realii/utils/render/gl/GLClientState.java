package Reality.Realii.utils.render.gl;

public enum GLClientState implements GLenum {
   COLOR("GL_COLOR_ARRAY", '\u8076'),
   EDGE("GL_EDGE_FLAG_ARRAY", '\u8079'),
   FOG("GL_FOG_COORD_ARRAY", '\u8457'),
   INDEX("GL_INDEX_ARRAY", '\u8077'),
   NORMAL("GL_NORMAL_ARRAY", '\u8075'),
   SECONDARY_COLOR("GL_SECONDARY_COLOR_ARRAY", '\u845e'),
   TEXTURE("GL_TEXTURE_COORD_ARRAY", '\u8078'),
   VERTEX("GL_VERTEX_ARRAY", '\u8074');

   private final String name;
   private final int cap;

   private GLClientState(String name, int cap) {
      this.name = name;
      this.cap = cap;
   }

   public String getName() {
      return this.name;
   }

   public int getCap() {
      return this.cap;
   }
}
