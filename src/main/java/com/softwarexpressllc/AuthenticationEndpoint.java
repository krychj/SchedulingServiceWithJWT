package com.softwarexpressllc;

import java.security.Key;
import java.security.KeyPair;
import java.util.Date;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.RsaProvider;

import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.DatatypeConverter;


@Path("/authentication")
public class AuthenticationEndpoint {
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)		//"application/x-www-form-urlencoded"
	@Path("/auth1")
	public Response authenticateUser(@FormParam("username") String username, @FormParam("password") String password) {
		try {
			boolean userValid = authenticate(username, password);
			if(userValid) {
				String token = issueToken(username);				
				return Response.ok(token).build();
			} else {
				return Response.status(Response.Status.UNAUTHORIZED).build();
			}
		} catch (Exception e) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/auth2")
	public Response authenticateUser2(Credentials credentials) {
		String username = credentials.getUsername();
		String password = credentials.getPassword();
		try {
			boolean userValid = authenticate(username, password);
			if(userValid) {
				String token = issueToken(username);				
				return Response.ok(token).build();
			} else {
				return Response.status(Response.Status.UNAUTHORIZED).build();
			}
		} catch (Exception e) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
	}
	
	@POST	
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/basicAuth")	
	public Response authenticateUser3() {		
		try {			
			String token = issueToken(null);				
			return Response.ok(token).build();			
		} catch (Exception e) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
	}

	private boolean authenticate(String username, String password) throws Exception {
		// Authenticate against a database, LDAP, file, other. The following is a sample impl. 
		if(username != null && !username.isEmpty() && password != null && !password.isEmpty()) {
			if(username.equals("User1") && password.equals("12345")) {
				return true;
			}
		}
		return false;		
	}

	private String issueToken(String username) {		
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

		long nowMillis = System.currentTimeMillis();
		Date now = new Date(nowMillis);			
 
		byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary("secret");
		Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
		
		JwtBuilder builder = Jwts.builder().setHeaderParam("typ", "JWT")
				.setIssuer("softwarexpressll.com")
				.setIssuedAt(now)				
				.setSubject("User1")
				.claim("scope", "admin")				
				.signWith(signatureAlgorithm, signingKey);
		
		long expMillis = nowMillis + 1 * 60 * 60 * 1000;
		Date exp = new Date(expMillis);
		builder.setExpiration(exp);
		String token = builder.compact();		
		return "Bearer " + token;
	}
		
	KeyPair keyPair;
	
	private String issueToken2(String username) {		
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.RS256;

		long nowMillis = System.currentTimeMillis();
		Date now = new Date(nowMillis);
		
		keyPair = RsaProvider.generateKeyPair();
		JwtBuilder builder = Jwts.builder().setHeaderParam("typ", "JWT")
				.setIssuer("softwarexpressll.com")
				.setIssuedAt(now)				
				.setSubject("User1")
				.claim("scope", "admin")				
				.signWith(signatureAlgorithm, keyPair.getPrivate());
		
		long expMillis = nowMillis + 1 * 60 * 60 * 1000;
		Date exp = new Date(expMillis);
		builder.setExpiration(exp);
		String token = builder.compact();		
		//return "Bearer " + token;
		return token;
	}
	
	private boolean validateToken2(String token) throws Exception {
		try {
			Jws<Claims> claims = Jwts.parser().setSigningKey(keyPair.getPublic()).parseClaimsJws(token);

			// At the minimum check time-related claims and scope
			Date exp = claims.getBody().getExpiration();
			Date currDate = new Date();
			if (exp.compareTo(currDate) < 0) {
				return false;
			}
			String scope = (String) claims.getBody().get("scope");
			if (scope.equals("admin") == false) {
				return false;
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static void main(String [] args) throws Exception {
		AuthenticationEndpoint endpoint = new AuthenticationEndpoint();
		String token = endpoint.issueToken2("Jarek");
		System.out.println(token);		
		boolean valid = endpoint.validateToken2(token);
		System.out.println("Token valid: " + valid);
	}
}
