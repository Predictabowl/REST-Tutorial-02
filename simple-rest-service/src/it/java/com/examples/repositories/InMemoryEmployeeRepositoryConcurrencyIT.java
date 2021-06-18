package com.examples.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;

import com.examples.model.Employee;
import com.examples.service.EmployeeServiceImpl;

public class InMemoryEmployeeRepositoryConcurrencyIT {

	private static final int NUM_THREADS = 200;
	private Map<String, Employee> repoMap;
	private EmployeeRespository employeeRepository;
	private EmployeeRespository spyEmployeeRepo;
	private EmployeeServiceImpl employeeService;
	
	@Before
	public void setUp(){
		repoMap = new LinkedHashMap<>();
		employeeRepository = new InMemoryEmployeeRepository(repoMap);
		spyEmployeeRepo = spy(employeeRepository);
		employeeService = new EmployeeServiceImpl(spyEmployeeRepo);
		
	}
	
	
	@Test
	public void test_replace_delete_race_condition() {
		when(spyEmployeeRepo.findOne("ID3"))
			.thenAnswer(inv -> {
				Optional<Employee> employee = employeeRepository.findOne(inv.getArgument(0));
				await().atLeast(100,TimeUnit.MILLISECONDS).untilTrue(new AtomicBoolean(true));
				return employee;
			});
		
		Employee employee = new Employee(null, "replaced", 750);
		ExecutorService threadPool = Executors.newFixedThreadPool(NUM_THREADS);
		List<Runnable> runnables = new LinkedList<>();
		
		runnables.addAll(IntStream.range(0, NUM_THREADS).mapToObj(i -> (Runnable) () -> 
			employeeService.replaceEmployee("ID3", employee))
				.collect(Collectors.toList()));
		
		runnables.add((Runnable) () -> employeeService.deleteEmployee("ID3"));
		
		List<Future<?>> futures = runnables.parallelStream().map(r -> threadPool.submit(r))
				.collect(Collectors.toList());
		
		await().atMost(10,TimeUnit.SECONDS).until(() -> 
			futures.stream().allMatch(f -> f.isDone()));
		
		assertThat(repoMap).doesNotContainKey("ID3");
	}

}
