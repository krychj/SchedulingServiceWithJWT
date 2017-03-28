package com.softwarexpressllc;

import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.glassfish.jersey.internal.util.Base64;

@Provider
public class BasicAuthenticationFilter implements ContainerRequestFilter {

	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String AUTHORIZATION_HEADER_PREFIX = "Basic";
	private static final String BASIC_AUTH_URL_PREFIX = "basicAuth";
	
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		if(requestContext.getUriInfo().getPath().contains(BASIC_AUTH_URL_PREFIX)) {			//This line will ensure that filter is applied only to the specific path.
			List<String> authHeader = requestContext.getHeaders().get(AUTHORIZATION_HEADER);
			if(authHeader != null && authHeader.size() > 0) {
				String authToken = authHeader.get(0);
				if(authToken.contains(AUTHORIZATION_HEADER_PREFIX)) {
					authToken = authToken.replaceFirst(AUTHORIZATION_HEADER_PREFIX, "").replaceAll(" ", "");					
					String decodedUsernamePassword = Base64.decodeAsString(authToken);
					StringTokenizer tokenizer = new StringTokenizer(decodedUsernamePassword, ":");
					String username = tokenizer.nextToken();
					String password = tokenizer.nextToken();
					if(username != null && !username.isEmpty() && password != null && !password.isEmpty()) {
						if(username.equals("User1") && password.equals("12345")) {
							return;
						}
					}
				}
			}
			Response unauthorizedStatus = Response.status(Response.Status.UNAUTHORIZED).entity("User cannot access selected resource.").build();
			requestContext.abortWith(unauthorizedStatus);
		}		
	}
}