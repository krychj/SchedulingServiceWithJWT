package com.softwarexpressllc;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.softwarexpressllc.statService.StatDataInput;
import com.softwarexpressllc.statService.StatDataOutput;

public class ScheduleExecutorQuartzJob implements Job {			
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		System.out.println("Executing scheduled job ...");
		
		System.setProperty("javax.net.ssl.trustStore", "C:/Java/jre8/lib/security/cacerts");
		System.setProperty("javax.net.ssl.trustStorePassword", "changeit");		
		System.setProperty("javax.net.ssl.keyStore", "C:/SoftwareXpressLLC/certs/jkrych.p12");
		System.setProperty("javax.net.ssl.keyStorePassword", "jkrych");
		/**
		 * If this client used different certificate, from that of Scheduling Service client, the certificate would have to be added to
		 * Tomcat's keystore/truststore.
		 */
		
		ClientConfig clientConfig = new ClientConfig();		
		final Client client = ClientBuilder.newClient(clientConfig);		
		String authenticationUrl = "https://localhost:8443/StatService-1.0/rest";
		WebTarget webTargetAuthentication = client.target(authenticationUrl).path("authentication").path("mutualSSL");
	    Invocation.Builder invocationBuilder =  webTargetAuthentication.request(MediaType.APPLICATION_JSON);	    
	    Response responseAuth = invocationBuilder.post(Entity.json(""));
	    if(responseAuth.getStatus() == 200) {
	    	String jwt = responseAuth.readEntity(String.class);	    	    		     
			Invocation.Builder invocationBuilderStat =  createInvokationBuilderStatService();	
			StatDataInput input = new StatDataInput();
			input.setRequestId("req-8:51am");
			float [] numbers = new float[] {2,4,6};
			input.setNumbers(numbers);
			invocationBuilderStat.header("Authorization", jwt);						
			Response responseStat = invocationBuilderStat.post(Entity.json(input));		   
		    if(responseStat.getStatus() == 200) {
		    	System.out.println("StatService call successfull.");
		    	System.out.println(responseStat.getStatus());
			    System.out.println(responseStat.readEntity(StatDataOutput.class));
		    } else {
		    	System.out.println("Not authorized");
		    }			
	    }	    		
	}
	
	Invocation.Builder createInvokationBuilderStatService () {
		ClientConfig clientConfig = new ClientConfig();		
		final Client client = ClientBuilder.newClient(clientConfig);
		System.setProperty("javax.net.ssl.trustStore", "C:/Java/jre8/lib/security/cacerts");
		System.setProperty("javax.net.ssl.trustStorePassword", "changeit");		
		System.setProperty("javax.net.ssl.keyStore", "C:/SoftwareXpressLLC/certs/jkrych.p12");
		System.setProperty("javax.net.ssl.keyStorePassword", "actualPassword");
		/**
		 * If this client used different certificate, from that of Scheduling Service client, the certificate would have to be added to
		 * Tomcat's keystore/truststore.
		 */
		String coreStatUrl = "https://localhost:8443/StatService-1.0/rest";
		WebTarget webTargetStat = client.target(coreStatUrl).path("api").path("statistics").path("coreStat");	     
		Invocation.Builder invocationBuilder = webTargetStat.request(MediaType.APPLICATION_JSON);
		return invocationBuilder;
	}
}
