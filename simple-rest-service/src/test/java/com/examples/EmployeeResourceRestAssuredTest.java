package com.examples;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import java.util.Arrays;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;
import org.mockito.Mock;

import com.examples.model.Employee;
import com.examples.service.EmployeeService;

import io.restassured.RestAssured;

public class EmployeeResourceRestAssuredTest extends JerseyTest{

	private static final String FIXTURE_EMPLOYEES = "employees";
	
	@Mock
	private EmployeeService employeeService;
	
	@Override
	protected Application configure() {
		openMocks(this);
		
		return new ResourceConfig(EmployeeResource.class)
			.register(new AbstractBinder() {
				
				@Override
				protected void configure() {
					bind(employeeService).to(EmployeeService.class);
				}
			});
	}

	// can't use setUp because will override JerseyTest
//	@Before
//	public void configureRestAssured() {
//		RestAssured.baseURI = getBaseUri().toString();
//	}
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		RestAssured.baseURI = getBaseUri().toString();
	}
	
	@Test
	public void test_getItXML_success() {
		when(employeeService.getEmployeeById("ID1"))
			.thenReturn(new Employee("ID1", "first employee", 1000));
		
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
		when(employeeService.getEmployeeById("ID2"))
			.thenReturn(new Employee("ID2", "another employee", 1400));
		
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
				.body("name", equalTo("another employee"))
				.body("salary", equalTo(1400));
	}
		
	@Test
	public void test_getAllEmployees() {
		when(employeeService.allEmployees()).thenReturn(
				Arrays.asList(new Employee("ID1", "first employee", 1000),
						new Employee("ID2", "second employee", 2000),
						new Employee("ID3", "third employee", 3000)));
		
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
		when(employeeService.allEmployees()).thenReturn(
				Arrays.asList(new Employee("ID1", "first employee", 1000),
						new Employee("ID2", "second employee", 2000),
						new Employee("ID3", "third employee", 3000)));
		
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
	 * There's no other services loaded other than the one registered
	 * in the configure. This test purpose is to verify taht's actually
	 * how it works.
	 */
	@Test
	public void learningTest_no_out_of_scope_services() {
		given()
			.accept(MediaType.TEXT_PLAIN)
		.when()
			.get("myresource")
		.then()
			.statusCode(404);
	}
	
	@Test
	public void test_count() {
		List<Employee> employees = Arrays.asList(new Employee(), new Employee());
		when(employeeService.allEmployees()).thenReturn(employees);
		
		given()
		.when()
			.get(FIXTURE_EMPLOYEES+"/count")
		.then()
			.statusCode(200)
			.assertThat()
				.body(equalTo(Integer.toString(employees.size())));
	}
	
	@Test
	public void test_post_new_employee() {
		JsonObject jsonObject = Json.createObjectBuilder()
				.add("name", "passed name")
				.add("salary", 2100)
				.build();
		when(employeeService.addEmployee(new Employee(null,"passed name",2100)))
				.thenReturn(new Employee("ID5", "returned name", 1550));
		
		given()
			.contentType(MediaType.APPLICATION_JSON)
			.body(jsonObject.toString())
		.when()
			.post(FIXTURE_EMPLOYEES)
		.then()
			.statusCode(Status.CREATED.getStatusCode())
			.assertThat()
				.contentType(MediaType.APPLICATION_JSON)
				.body("id", equalTo("ID5")
					,"name", equalTo("returned name")
					,"salary", equalTo(1550)
				)
				.header("Location", response -> endsWith(FIXTURE_EMPLOYEES+"/ID5"));
	}
	
	@Test
	public void test_put_update_employee() {
		JsonObject jsonObject = Json.createObjectBuilder()
				.add("name", "passed name")
				.add("salary", 2100)
				.build();
		when(employeeService.replaceEmployee("ID1",new Employee(null,"passed name",2100)))
				.thenReturn(new Employee("ID1", "returned name", 1550));
		
		given()
			.contentType(MediaType.APPLICATION_JSON)
			.body(jsonObject.toString())
		.when()
			.put(FIXTURE_EMPLOYEES+"/ID1")
		.then()
			.statusCode(Status.OK.getStatusCode())
			.assertThat()
				.body("id", equalTo("ID1")
					,"name",equalTo("returned name")
					,"salary",equalTo(1550));
	}

}
