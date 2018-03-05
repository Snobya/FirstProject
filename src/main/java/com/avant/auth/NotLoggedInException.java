package com.avant.auth;

public class NotLoggedInException extends RuntimeException {
	public NotLoggedInException (String s) {
		super(s);
	}
}
