package net.halozy;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public interface Native {
	
    abstract String getHWID() throws NoSuchAlgorithmException, UnsupportedEncodingException;
}
