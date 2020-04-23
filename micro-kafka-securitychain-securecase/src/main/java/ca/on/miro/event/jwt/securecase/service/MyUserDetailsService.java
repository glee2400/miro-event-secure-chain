package ca.on.miro.event.jwt.securecase.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/*
 * User detail is hard-coded for JWT Security POC
 * However, it can be retrieved form database
 */
@Service
public class MyUserDetailsService implements UserDetailsService{
	
	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException{	
		return new User("demo", "demo", new ArrayList<SimpleGrantedAuthority>());
	}

}
