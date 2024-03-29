package com.examples;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

import javax.ws.rs.core.MediaType;

import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import io.restassured.RestAssured;

public class MyResourceRestAssuredTest {
	
	private HttpServer server;
	
	@BeforeClass
	public static void setUpClass() {
		RestAssured.baseURI = Main.BASE_URI;
	}
	
	@Before
	public void setUp() {
		server = Main.startServer();
	}
	
	@After
	public void tearDown() {
		server.shutdown();
	}

	@Test
	public void test_getIt() {
		given()
			.accept(MediaType.TEXT_PLAIN)
		.when()
			.get("myresource")
		.then()
			.statusCode(200)
			.assertThat()
				.contentType(MediaType.TEXT_PLAIN)
				.and()
				.body(equalTo("Got it!"));
	}

	@Test
	public void test_getItXML() {
		given()
			.accept(MediaType.TEXT_XML)
		.when()
			.get("myresource")
		.then()
			.statusCode(200)
			.assertThat()
				.contentType(MediaType.TEXT_XML)
				.and()
				.body("hello", equalTo("Got it (XML)!"));
	}
	
	@Test
	public void test_getItHTML() {
		given()
			.accept(MediaType.TEXT_HTML)
		.when()
			.get("myresource")
		.then()
			.statusCode(200)
			.assertThat()
				.contentType(MediaType.TEXT_HTML)
				.and()
				.body("html.head.title", equalTo("Hello Jersey"))
				.body("html.body", equalTo("Got it (HTML)!"));
	}
	
}
