package com.examples.exceptions;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Before;
import org.junit.Test;

import io.restassured.RestAssured;

public class BadRequestMapperTest extends JerseyTest{

	@Path("testpath")
	public static class MockResource {
		@GET
		@Path("badrequest")
		public String testEndPoint() {
			throw new BadRequestException("Bad request message");
		}
	}
	
	@Override
	protected Application configure() {
		return new ResourceConfig()
				.register(BadRequestMapper.class)
				.register(MockResource.class);
	}
	
	@Before
	public void configureRestAssured() {
		RestAssured.baseURI = getBaseUri().toString();
	}
	
	@Test
	public void test_badRequest_response() {
		given()
		.when()
			.get("testpath/badrequest")
		.then()
			.statusCode(Status.BAD_REQUEST.getStatusCode())
			.contentType(MediaType.TEXT_PLAIN)
			.body(equalTo("Bad request message"));
	}

}
