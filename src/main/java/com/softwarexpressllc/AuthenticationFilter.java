package com.softwarexpressllc;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.io.IOException;
import java.security.Key;
import java.security.Principal;
import java.util.Date;

import javax.annotation.Priority;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.DatatypeConverter;

@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {		
		// Get the HTTP Authorization header from the request
		String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
		
		// Check if the HTTP Authorization header is present and formatted correctly
		if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
			throw new NotAuthorizedException("Authorization header must be provided");
		}
		// Extract the token from the HTTP Authorization header
		String token = authorizationHeader.substring("Bearer".length()).trim();
		try {
			boolean isTokenValid = validateToken(token);
			if(!isTokenValid) {
				requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
			}
		} catch (Exception e) {
			requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
		}
		
		final Principal userPrincipal = requestContext.getSecurityContext().getUserPrincipal();
		//Check if valid user
		requestContext.setSecurityContext(new SecurityContext() {
			
		    @Override
		    public Principal getUserPrincipal() {

		        return new Principal() {

		            @Override
		            public String getName() {
		                if(userPrincipal != null) {
		                	return userPrincipal.getName();
		                } else {
		                	return null;
		                }
		            	
		            }
		        };
		    }

		    @Override
		    public boolean isUserInRole(String role) {
		        return true;
		    }

		    @Override
		    public boolean isSecure() {
		        return false;
		    }

		    @Override
		    public String getAuthenticationScheme() {
		        return null;
		    }
		});
	}

	
	private boolean validateToken(String token) throws Exception {
		try {
			SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;						
			byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary("secret");
			Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
			
			Jws<Claims> claims = Jwts.parser()
					  .setSigningKey(signingKey)
					  .parseClaimsJws(token);
			
			//At the minimum check time-related claims and scope 
			Date exp = claims.getBody().getExpiration();
			Date currDate = new Date();
			if(exp.compareTo(currDate) < 0) {
				return false;
			}
			String scope = (String) claims.getBody().get("scope");
			
			return true;
		} catch (Exception e) {
			return false;
		}				
	}
}