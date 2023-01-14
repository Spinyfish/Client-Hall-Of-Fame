package cafe.corrosion.font;

import org.lwjgl.opengl.GL11;

class CharacterData {
    public char character;
    public float width;
    public float height;
    private final int textureId;

    public CharacterData(char character, float width, float height, int textureId) {
        this.character = character;
        this.width = width;
        this.height = height;
        this.textureId = textureId;
    }

    public void bind() {
        GL11.glBindTexture(3553, this.textureId);
    }
}
