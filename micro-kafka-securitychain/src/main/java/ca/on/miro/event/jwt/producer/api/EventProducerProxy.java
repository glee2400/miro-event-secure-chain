package ca.on.miro.event.jwt.producer.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ca.on.miro.event.jwt.producer.model.AuthenticationRequest;
import ca.on.miro.event.jwt.producer.model.AuthenticationResponse;
import ca.on.miro.event.jwt.producer.model.CaseData;
import ca.on.miro.event.jwt.producer.model.CasePayload;
import ca.on.miro.event.jwt.producer.service.MyUserDetailsService;
import ca.on.miro.event.jwt.producer.util.JwtUtil;

/**
 * Publisher need to be "Source"
 * The Source must be "org.springframework.cloud.stream.messaging.Source"
 *
 */

@EnableBinding(Source.class)
@RestController
public class EventProducerProxy {

	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private MyUserDetailsService userDetailsService;
	@Autowired
	private JwtUtil jwtTokenUtil;
	
	@Autowired
	private MessageChannel output;

	/* Publisher will send JSON object in the HTTPRequest (Postman) */
	@PostMapping("/publish")
	public CaseData publishEvent(@RequestBody CaseData casedata, @RequestHeader HttpHeaders headers){
		
		String jwtBearer = headers.getFirst("Authorization");
		System.out.println("  --> Kafka Payload Header:"+jwtBearer);
        
        CasePayload casePayload = new CasePayload(jwtBearer==null?"jwt_Null":jwtBearer, casedata);
		
		output.send(MessageBuilder.withPayload(casePayload).build());
		return casedata;
	}
	
	
	@RequestMapping ({ "/hello" })
	public String hello(@RequestHeader MultiValueMap<String, String> headers) {
		
		System.out.println("  --> hello: Hit me!");
		headers.forEach((key, value) -> {
			System.out.println(String.format("      Header '%s' = %s", key, value));
		 });
		return "<h1>Hello World!</h1>";
	}

	/*
	 * Use JWT Token do Authentication
	 */
	@RequestMapping(value = "/authenticate", method = RequestMethod.POST)
	public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {

		try {
			/* User AuthenticaitonManager to Authenticate the Request*/
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), 
							authenticationRequest.getPassword())
			);
		}
		catch (BadCredentialsException e) {
			/* If not Authenticated, throw Exception */
			throw new Exception("Incorrect username or password", e);
		}


		/* Fetch user from UserDetailService */
		final UserDetails userDetails = userDetailsService
				.loadUserByUsername(authenticationRequest.getUsername());
		/* Use JWT Util to get JWT token outof UserDetails */
		final String jwt = jwtTokenUtil.generateToken(userDetails);

		/* Create authentication HTTP response with Token */
		return ResponseEntity.ok(new AuthenticationResponse(jwt));
	}
	
	
	
	
}
