package com.examples;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class EmployeeResourceRestAssuredIT {

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
	public void test_getIt_JSON_success() {
		given()
			.accept(MediaType.APPLICATION_JSON)
		.when()
			.get(FIXTURE_EMPLOYEES+"/ID2")
		.then()
			.statusCode(200)
			.assertThat()
				.contentType(MediaType.APPLICATION_JSON)
				.and()
				.body("id", equalTo("ID2"))
				.body("name", equalTo("second employee"))
				.body("salary", equalTo(2000));
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
	public void test_getIt_JSON_failure() {
		given()
			.accept(MediaType.APPLICATION_JSON)
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
	
	@Test
	public void test_getAllEmployees_JSON() {
		given()
			.accept(MediaType.APPLICATION_JSON)
		.when()
			.get(FIXTURE_EMPLOYEES)
		.then()
			.statusCode(200)
			.assertThat()
				.body("[0].id", equalTo("ID1")
					,"[0].name", equalTo("first employee")
					,"[0].salary", equalTo(1000)
					,"[1].id", equalTo("ID2")
					,"[1].name", equalTo("second employee")
					,"[1].salary", equalTo(2000)
					,"[2].id", equalTo("ID3")
					,"[2].name", equalTo("third employee")
					,"[2].salary", equalTo(3000)
				);
	}
	
	/*
	 * In this test we're running the whole server, so every
	 * service should be available
	 */
	@Test
	public void test_all_Services_are_available() {
		given()
			.accept(MediaType.TEXT_PLAIN)
		.when()
			.get("myresource")
		.then()
			.statusCode(200);
	}
	
	@Test
	public void test_Post_new_employee() {
		JsonObject jsonObject = Json.createObjectBuilder()
				.add("name", "new employee")
				.add("salary", 950)
				.build();
		
		Response response = given()
			.contentType(MediaType.APPLICATION_JSON)
			.body(jsonObject.toString())
		.when()
			.post(FIXTURE_EMPLOYEES);
		
		String id = response.getBody().path("id");
		String uri = response.getHeader("Location");
		
		given()
			.accept(MediaType.APPLICATION_JSON)
		.when()
			.get(uri)
		.then()
			.statusCode(Status.OK.getStatusCode())
			.assertThat()
				.body("id", equalTo(id)
					,"name", equalTo("new employee")
					,"salary",equalTo(950));
			
	}
	
	@Test
	public void test_put_update_employee() {
		JsonObject jsonEmployee = Json.createObjectBuilder()
				.add("name", "updated employee")
				.add("salary", 1950)
				.build();
		
		given()
			.contentType(MediaType.APPLICATION_JSON)
			.body(jsonEmployee.toString())
		.when()
			.put(FIXTURE_EMPLOYEES+"/ID1")
		.then()
			.statusCode(Status.OK.getStatusCode());
		
		given()
			.accept(MediaType.APPLICATION_JSON)
		.when()
			.get(FIXTURE_EMPLOYEES+"/ID1")
		.then()
			.statusCode(Status.OK.getStatusCode())
			.assertThat()
				.body("id", equalTo("ID1")
					,"name",equalTo("updated employee")
					,"salary", equalTo(1950));
	}
	
	@Test
	public void test_delete_employee() {
		given()
			.accept(MediaType.APPLICATION_JSON)
		.when()
			.delete(FIXTURE_EMPLOYEES+"/ID1")
		.then()
			.statusCode(Status.ACCEPTED.getStatusCode())
			.assertThat()
				.contentType(MediaType.APPLICATION_JSON)
				.body("id", equalTo("ID1")
					,"name", equalTo("first employee")
					,"salary", equalTo(1000));
		
		given()
			.accept(MediaType.APPLICATION_JSON)
		.when()
			.get(FIXTURE_EMPLOYEES+"/ID1")
		.then()
			.statusCode(Status.NOT_FOUND.getStatusCode());
	}

}
