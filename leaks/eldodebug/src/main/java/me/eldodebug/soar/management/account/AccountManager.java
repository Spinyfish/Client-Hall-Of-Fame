package me.eldodebug.soar.management.account;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;

import me.eldodebug.soar.Soar;
import me.eldodebug.soar.utils.interfaces.IMixinMinecraft;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Session;
import openauth.microsoft.MicrosoftAuthResult;
import openauth.microsoft.MicrosoftAuthenticationException;
import openauth.microsoft.MicrosoftAuthenticator;

public class AccountManager {

	private Minecraft mc = Minecraft.getMinecraft();
	
	private ArrayList<Account> accounts = new ArrayList<Account>();
	
	public boolean isFirstLogin = false;
	
	private Account currentAccount;
	
	public AccountManager() {
		
		if(Soar.instance.fileManager.getAccountFile().length() == 0) {
			isFirstLogin = true;
		}
		
		this.load();
		this.login(currentAccount);
	}

	public void save() {
		ArrayList<String> toSave = new ArrayList<String>();
		
		for (Account a : Soar.instance.accountManager.getAccounts()) {
			toSave.add("Account:" + a.getAccountType().toString()  + ":" + a.getUsername() + ":" + a.getUuid() + ":" + a.getToken());
		}
		
		toSave.add("Current:" + getCurrentAccount().getUsername());
		
		try {
			PrintWriter pw = new PrintWriter(Soar.instance.fileManager.getAccountFile());
			for (String str : toSave) {
				pw.println(str);
			}
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void load() {
		ArrayList<String> lines = new ArrayList<String>();
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(Soar.instance.fileManager.getAccountFile()));
			String line = reader.readLine();
			while (line != null) {
				lines.add(line);
				line = reader.readLine();
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (String s : lines) {
			
			String[] args = s.split(":");
			
			if (s.toLowerCase().startsWith("account:")) {
				
				AccountType accountType = AccountType.MICROSOFT;
				
				if(args[1].equals("MICROSOFT")) {
					accountType = AccountType.MICROSOFT;
				}
				
				if(args[1].equals("CRACKED")) {
					accountType = AccountType.CRACKED;
				}
				
				accounts.add(new Account(accountType, args[2], args[3], args[4]));
			}
			
			if (s.toLowerCase().startsWith("current:")) {
				setCurrentAccount(getAccountByUsername(args[1]));
			}
		}
	}
	
	private void login(Account a) {
		
		if(a == null) {
			return;
		}
		
		if(a.getAccountType().equals(AccountType.MICROSOFT)) {
			MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
			a.setInfo("Loading...");
			try {
				MicrosoftAuthResult acc = authenticator.loginWithRefreshToken(a.getToken());
				((IMixinMinecraft)mc).setSession(new Session(acc.getProfile().getName(), acc.getProfile().getId(), acc.getAccessToken(), "legacy"));
				a.setInfo(EnumChatFormatting.GREEN + "Success!");
			} catch (MicrosoftAuthenticationException e) {
				e.printStackTrace();
				a.setInfo(EnumChatFormatting.RED + "Error :(");
			}
		}
		
		if(a.getAccountType().equals(AccountType.CRACKED)) {
			a.setInfo(EnumChatFormatting.GREEN + "Success!");
			((IMixinMinecraft)mc).setSession(new Session(a.getUsername(), "0", "0", "legacy"));
		}
	}
	
	public Account getAccountByUsername(String name) {
		return accounts.stream().filter(account -> account.getUsername().equalsIgnoreCase(name)).findFirst().orElse(null);
	}
	
	public ArrayList<Account> getAccounts() {
		return accounts;
	}

	public Account getCurrentAccount() {
		return currentAccount;
	}

	public void setCurrentAccount(Account currentAccount) {
		this.currentAccount = currentAccount;
	}
}
