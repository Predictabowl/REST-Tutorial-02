package com.examples.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import com.examples.model.Employee;

public class InMemoryEmployeeRepositoryTest {

	private InMemoryEmployeeRepository repository;
	private Map<String, Employee> repositoryMap;
	
	@Before
	public void setUp() {
		/*
		 * as the repository is implemented is should be needed to add a non empty map for test.
		 * But since this is only a practice test we take a shortcut
		 */
		repositoryMap = new HashMap<>();
		repository = new InMemoryEmployeeRepository(repositoryMap);
	}
	
	@Test
	public void test_base_values() {
		assertThat(repositoryMap).containsOnly(
				entry("ID1", new Employee("ID1", "first employee", 1000)),
				entry("ID2", new Employee("ID2", "second employee", 2000)),
				entry("ID3", new Employee("ID3", "third employee", 3000)));
	}
	
	@Test
	public void test_findAll() {
		List<Employee> employees = repository.findAll();
		
		assertThat(employees).containsExactlyElementsOf(repositoryMap.values());
	}
	
	@Test
	public void test_findOne_successful() {
		Optional<Employee> employee = repository.findOne("ID2");
		
		assertThat(employee).contains(new Employee("ID2", "second employee", 2000));
	}
	
	@Test
	public void test_findOne_failure() {
		Optional<Employee> employee = repository.findOne("IDA");
		
		assertThat(employee).isEmpty();
	}
	
	@Test
	public void test_save_new_employee() {
		repository.save(new Employee(null, "Mario", 11000));
		
		assertThat(repositoryMap).hasSize(4)
			.contains(entry("ID4",new Employee("ID4", "Mario", 11000)));
	}
	
	@Test
	public void test_update_employee() {
		repository.save(new Employee("ID3", "Mario", 11000));
		
		assertThat(repositoryMap).containsOnly(
				entry("ID1", new Employee("ID1", "first employee", 1000)),
				entry("ID2", new Employee("ID2", "second employee", 2000)),
				entry("ID3", new Employee("ID3", "Mario", 11000)));
	}
	
	@Test
	public void test_delete_employee() {
		Employee employee = repository.delete("ID2");
		
		assertThat(employee).isEqualTo(new Employee("ID2", "second employee", 2000));
		assertThat(repositoryMap).containsOnly(
				entry("ID1", new Employee("ID1", "first employee", 1000)),
				entry("ID3", new Employee("ID3", "third employee", 3000)));
	}

}
