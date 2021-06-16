package com.examples;

import static io.restassured.RestAssured.given;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.core.MediaType;

import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import io.restassured.RestAssured;

public class EmployeeResourceConcurrencyIT{

	private static final String FIXTURE_EMPLOYEES = "employees";
	private static final int FIXTURE_NUM_THREADS = 20;
	
	private HttpServer server;
	
	
	@BeforeClass
	public static void setUpClass() {
		RestAssured.baseURI = Main.BASE_URI;
	}

	@Before
	public void setUp() throws Exception {
		server = Main.startServer();
	}
	
	@After
	public void tearDown() {
		server.shutdown();
	}
	
	
	
	@Test
	public void test_Post_new_employee_concurrent()  throws Exception{
		JsonObject jsonEmployee = Json.createObjectBuilder()
				.add("name", "Mario")
				.add("salary", 1750)
				.build();
		
		Collection<String> ids = new ConcurrentLinkedQueue<>();
		
		ExecutorService threadPool = newFixedThreadPool(FIXTURE_NUM_THREADS);
		List<Future<?>> futures = IntStream.range(0, FIXTURE_NUM_THREADS)
			.mapToObj(i -> (Runnable) () -> ids.add(
				given()
					.contentType(MediaType.APPLICATION_JSON)
					.body(jsonEmployee.toString())
				.when()
					.post(FIXTURE_EMPLOYEES)
					.path("id")
			))
			.map(r -> threadPool.submit(r))
			.collect(Collectors.toList());
		
		await().atMost(10,TimeUnit.SECONDS)
			.until(() -> futures.stream().allMatch(f -> f.isDone()));
		
		assertThat(ids).doesNotHaveDuplicates();
	}


}
