package ca.on.miro.event.jwt.producer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@EnableWebSecurity
public class SecurityConfigurer extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private MyUserDetailsService myUserDetailsService;
	@Autowired
	private JwtRequestFilter jwtRequestFilter;
	
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception{
		System.out.println("==>>00. Hit Configured -->Authentication!"+auth);
		auth.userDetailsService(myUserDetailsService);
		
		/*
		  auth.inMemoryAuthentication()
			.withUser("demo")
			.password("demo")
			.roles("ADMIN")
			.and()
			.withUser("foo")
			.password("foo")
			.roles("USER")
			;
			*/
	}
	
	
	//Basic Authorization with "csrf" disabled, with Basic-Authentication
	/*
	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		System.out.println("==>>01. Hit Configured -->Authorization!");
		
		httpSecurity.csrf().disable();
		httpSecurity.authorizeRequests().anyRequest().fullyAuthenticated().and().httpBasic();
			            
		System.out.println("==>>02. Hit Configured -->Authorization!");
		httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

	}
	*/
	
	// --Remove Authorization and Filter -- JWT version
	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.csrf().disable()
				.authorizeRequests().antMatchers("/authenticate").permitAll().
						anyRequest().authenticated()
						.and()
						.exceptionHandling()
						.and().sessionManagement()
						.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		
		System.out.println("== >>Hit Configured, add my filter before the default!");
		httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

	}
	
	
	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		System.out.println("==>>-01. Hit authenticationManagerBean !");

		return super.authenticationManagerBean();
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return NoOpPasswordEncoder.getInstance();
	}

}
