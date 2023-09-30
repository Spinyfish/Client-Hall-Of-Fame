package net.halozy;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public interface Hook {
	
	public abstract void hook() throws UnsupportedEncodingException, NoSuchAlgorithmException;
	
	public abstract String getSource();
}
