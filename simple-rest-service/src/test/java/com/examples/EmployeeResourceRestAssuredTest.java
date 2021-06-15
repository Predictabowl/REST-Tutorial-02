package com.examples;

import static io.restassured.RestAssured.*;
import static org.hamcrest.CoreMatchers.equalTo;

import javax.ws.rs.core.MediaType;

import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import io.restassured.RestAssured;

public class EmployeeResourceRestAssuredTest {

	private static final String FIXTURE_EMPLOYEES = "employees";
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
	public void test_getItXML_success() {
		given()
			.accept(MediaType.APPLICATION_XML)
		.when()
			.get(FIXTURE_EMPLOYEES+"/ID1")
		.then()
			.statusCode(200)
			.assertThat()
				.contentType(MediaType.APPLICATION_XML)
				.and()
				.body("employee.id", equalTo("ID1"))
				.body("employee.name", equalTo("first employee"))
				.body("employee.salary", equalTo("1000"));
	}
	
	@Test
	public void test_getItXML_failure() {
		given()
			.accept(MediaType.APPLICATION_XML)
		.when()
			.get(FIXTURE_EMPLOYEES+"/ID4")
		.then()
			.statusCode(404)
			.assertThat()
				.contentType(MediaType.TEXT_PLAIN)
				.body(equalTo("Employee not found with id: ID4"));
	}
	
	@Test
	public void test_getAllEmployees() {
		given()
			.contentType(MediaType.APPLICATION_XML)
		.when()
			.get(FIXTURE_EMPLOYEES)
		.then()
			.statusCode(200)
			.assertThat()
				.contentType(MediaType.APPLICATION_XML)
				.and()
				.body("employees.employee[0].id", equalTo("ID1")
						,"employees.employee[0].name", equalTo("first employee")
						,"employees.employee[0].salary", equalTo("1000")
						,"employees.employee[1].id", equalTo("ID2")
						,"employees.employee[1].name", equalTo("second employee")
						,"employees.employee[1].salary", equalTo("2000")
						,"employees.employee[2].id", equalTo("ID3")
						,"employees.employee[2].name", equalTo("third employee")
						,"employees.employee[2].salary", equalTo("3000")
				);
	}

}
