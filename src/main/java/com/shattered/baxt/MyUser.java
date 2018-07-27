package com.shattered.baxt;



import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class MyUser implements UserDetails {
	
	 private static final long serialVersionUID = 2396654715019746670L;


	String id;
	String username;
	String password;
	Collection<GrantedAuthority> auths ;
	
	public MyUser(String id, String name, String password, String authorities) {
		this.auths = new ArrayList<>();
		this.id = id;
		this.username = name;
		this.password = password;
		auths.add(new SimpleGrantedAuthority("ROLE_" + authorities));
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return auths;
	}
	
	public String getId() {
		return id;
	}
	
	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}