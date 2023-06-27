package de.jcm.discordgamesdk.user;

public class DiscordUser
{
    private final long userId;
    private final String username;
    private final String discriminator;
    private final String avatar;
    private final boolean bot;
    
    public DiscordUser(final long userId, final String username, final String discriminator, final String avatar, final boolean bot) {
        this.userId = userId;
        this.username = username;
        this.discriminator = discriminator;
        this.avatar = avatar;
        this.bot = bot;
    }
    
    public long getUserId() {
        return this.userId;
    }
    
    public String getUsername() {
        return this.username;
    }
    
    public String getDiscriminator() {
        return this.discriminator;
    }
    
    public String getAvatar() {
        return this.avatar;
    }
    
    public boolean isBot() {
        return this.bot;
    }
    
    @Override
    public String toString() {
        return "DiscordUser{userId=" + this.userId + ", username='" + this.username + '\'' + ", discriminator='" + this.discriminator + '\'' + ", avatar='" + this.avatar + '\'' + ", bot=" + this.bot + '}';
    }
}
