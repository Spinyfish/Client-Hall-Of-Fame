package dev.echo.hackerdetector;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.player.EntityPlayer;

@Getter
@Setter
public abstract class Detection {

    private String name;
    private Category type;
    private long lastViolated;

    public Detection(String name, Category type) {
        this.name = name;
        this.type = type;
    }

    public abstract boolean runCheck(EntityPlayer player);
}
