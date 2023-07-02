package me.eldodebug.soar.management.account;

import me.eldodebug.soar.utils.TimerUtils;
import me.eldodebug.soar.utils.animation.simple.SimpleAnimation;

public class Account {

	private AccountType accountType;
	
	private String username, uuid, token;
	
	private String info;
	
	private TimerUtils timer;
	
	public SimpleAnimation opacityAnimation = new SimpleAnimation(0.0F);
	public boolean isDone;
	
	public Account(AccountType accountType, String username, String uuid, String token) {
		this.accountType = accountType;
		this.username = username;
		this.uuid = uuid;
		this.token = token;
		this.info = "";
		this.timer = new TimerUtils();
		this.isDone = true;
	}

	public AccountType getAccountType() {
		return accountType;
	}

	public void setAccountType(AccountType accountType) {
		this.accountType = accountType;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public TimerUtils getTimer() {
		return timer;
	}
}
