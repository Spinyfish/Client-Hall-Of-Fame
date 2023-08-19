package dev.thread.api.util.render.component;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public abstract class Component {
    private final Object parent;

    public abstract void render(float x, float y, float width, float height);

    public abstract void mouseClicked(int mouseX, int mouseY, int mouseButton);

    public abstract void keyTyped(int keyCode);
}
