package com.alan.clients.util.render;

import com.alan.clients.util.math.MathUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public final class Animator {

    private float targetX, targetY;
    private float posX, posY;
    private float smoothness;

    public void update() {
        // Running the LERP method for epic smoothness.
        this.posX = MathUtil.lerp(this.posX, this.targetX, this.smoothness);
        this.posY = MathUtil.lerp(this.posY, this.targetY, this.smoothness);

        // Rounding
        if (Math.abs(this.posX - this.targetX) < 0.05F) this.posX = this.targetX;
        if (Math.abs(this.posY - this.targetY) < 0.05F) this.posY = this.targetY;
    }

    public boolean finished() {
        return this.posX == this.targetX && this.posY == this.targetY;
    }
}
