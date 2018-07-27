package com.shattered.baxt;

import java.util.HashMap;

import org.springframework.stereotype.Repository;

@Repository
public class CodeRepo {


	private final static HashMap<String, String> codesAndMessages = new HashMap<>() ;

	public CodeRepo() {
		codesAndMessages.put("already-auth", "You are already logged in.");
		codesAndMessages.put("already-out", "You are not logged in.");
		codesAndMessages.put("good-login", "You have successfully logged in.");
		codesAndMessages.put("bad-login", "Your username or password information is incorrect.");	
		codesAndMessages.put("good-logout", "You have successfully logged out.");	
		codesAndMessages.put("access-denied", "You do not have access to this page.");	
		codesAndMessages.put("user-exists", "That user already exists.");	
		codesAndMessages.put("password-size", "password must be between 1 and 10 characters.");	
		codesAndMessages.put("username-size", "Username must be between 5 and 10 characters.");	
		codesAndMessages.put("email-exists", "That email is aready in use.");	
		codesAndMessages.put("bad-creation-info", "Please check your information.");
		codesAndMessages.put("good-creation-info", "You have successfully created your account.");
		codesAndMessages.put("file-exists", "A file with this name already exists.");
		codesAndMessages.put("add-pdf-confirmation", "Your PDF has been successfully added to the database.");
		codesAndMessages.put("bad-file", "The submitted file is not appropriate here.");
		codesAndMessages.put("user-delete-name-bad-match", "The name entered did not match the username");
		codesAndMessages.put("user-delete-conf", "The user has been deleted");
		codesAndMessages.put("bad-email-file", "The email file you uploaded has no matching PDF file.");
		codesAndMessages.put("bad-password-match", "The passwords did not match.");
		codesAndMessages.put("password-change-success", "The password has been changed.");
		codesAndMessages.put("bad-download", "There was an error with your download.");

		System.out.println("codes");

	}

	public static String getMessage(String code) { 
		return codesAndMessages.get(code);
	}

}
