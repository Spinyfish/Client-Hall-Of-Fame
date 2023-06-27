package rip.athena.client.account;

import rip.athena.client.utils.time.*;
import rip.athena.client.utils.animations.simple.*;

public class Account
{
    private AccountType accountType;
    private String username;
    private String uuid;
    private String token;
    private String info;
    private TimerUtil timer;
    public SimpleAnimation opacityAnimation;
    public boolean isDone;
    
    public Account(final AccountType accountType, final String username, final String uuid, final String token) {
        this.opacityAnimation = new SimpleAnimation(0.0f);
        this.accountType = accountType;
        this.username = username;
        this.uuid = uuid;
        this.token = token;
        this.info = "";
        this.timer = new TimerUtil();
        this.isDone = true;
    }
    
    public AccountType getAccountType() {
        return this.accountType;
    }
    
    public void setAccountType(final AccountType accountType) {
        this.accountType = accountType;
    }
    
    public String getUsername() {
        return this.username;
    }
    
    public void setUsername(final String username) {
        this.username = username;
    }
    
    public String getUuid() {
        return this.uuid;
    }
    
    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }
    
    public String getToken() {
        return this.token;
    }
    
    public void setToken(final String token) {
        this.token = token;
    }
    
    public String getInfo() {
        return this.info;
    }
    
    public void setInfo(final String info) {
        this.info = info;
    }
    
    public TimerUtil getTimer() {
        return this.timer;
    }
}
