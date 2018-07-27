package com.shattered.baxt;

import java.util.ArrayList;

import org.springframework.stereotype.Service;

@Service
public class ValidationService {

	public static ArrayList<String> validateNewUserInfo(String username, String password, String passConf, String email) {
		ArrayList<String> errorCodes = new ArrayList<>();
		if (MyUserDetailsService.doesUserExist(username)) {
			errorCodes.add("user-exists");
			return errorCodes;
		}
		if (username.length() < 5 || username.length() > 10) {
			errorCodes.add("username-size");
		}
		if (password.length() < 1 || password.length() > 10) {
			errorCodes.add("password-size");
		}
		if(!password.equals(passConf)){ 
			errorCodes.add("bad-password-match");
		}
		return errorCodes;
	}
}
