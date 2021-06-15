package com.examples.repositories;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.examples.model.Employee;

public class FakeEmployeeRepository implements EmployeeRespository {

	public static final FakeEmployeeRepository isntance = new FakeEmployeeRepository();
	
	private List<Employee> employees = new LinkedList<>();
	
	private FakeEmployeeRepository() {
		employees.add(new Employee("ID1", "first employee", 1000));
		employees.add(new Employee("ID2", "second employee", 2000));
		employees.add(new Employee("ID3", "third employee", 3000));
	}
	
	@Override
	public List<Employee> findAll() {
		return employees;
	}

	@Override
	public Optional<Employee> findOne(String id) {
		return employees.stream().filter(e -> e.getEmployeeId().equals(id))
				.findFirst();
	}

}
