package com.softwarexpressllc;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

public class ClientToAuthenticationService {

	public ClientToAuthenticationService() {
		super();
		System.setProperty("javax.net.ssl.trustStore", "C:/Java/jre8/lib/security/cacerts");
		System.setProperty("javax.net.ssl.trustStorePassword", "changeit");		
		System.setProperty("javax.net.ssl.keyStore", "C:/SoftwareXpressLLC/certs/jkrych.p12");
		System.setProperty("javax.net.ssl.keyStorePassword", "actualPassword");
		
		//An alternative is to specify above properties via Eclipse VM arguments
		/*-Djavax.net.ssl.keyStore=C:/SoftwareXpressLLC/certs/jkrych.p12
		-Djavax.net.ssl.keyStorePassword=
		-Djavax.net.ssl.trustStore=C:/Java/jre8/lib/security/cacerts
		-Djavax.net.ssl.trustStorePassword=changeit*/
		
		
		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basicBuilder().credentials("User1", "12345").build();
		
		ClientConfig clientConfig = new ClientConfig();
		clientConfig.register(feature) ;
		
		final Client client = ClientBuilder.newClient(clientConfig);		
		String authenticationUrl = "https://localhost:8443/SchedulingService-1.0/rest/authentication";
		WebTarget webTarget = client.target(authenticationUrl).path("basicAuth");
	     
		Invocation.Builder invocationBuilder =  webTarget.request(MediaType.APPLICATION_JSON);				
		Response response = invocationBuilder.post(Entity.json(""));
	     
	    System.out.println(response.getStatus());
	    System.out.println(response.readEntity(String.class));
	     
	    if(response.getStatus() == 200) {
	    	System.out.println("Authentication successfull.");
	    } else {
	    	System.out.println("Not authorized");
	    }
	}

	public static void main(String[] args) {
		new ClientToAuthenticationService();
	}
}