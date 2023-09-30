package dev.tenacity.utils.objects;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.ResourceLocation;

@Setter
@Getter
public class DiscordAccount {
    public String bannerColor;
    public ResourceLocation discordAvatar;
    public ResourceLocation discordBanner;
}
