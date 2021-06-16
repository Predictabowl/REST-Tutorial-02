package com.examples.repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.inject.Inject;

import com.examples.model.Employee;

public class InMemoryEmployeeRepository implements EmployeeRespository {

	private Map<String,Employee> employees;
	
	@Inject
	public  InMemoryEmployeeRepository(Map<String, Employee> employees) {
		this.employees = employees;
		put(new Employee(null, "first employee", 1000));
		put(new Employee(null, "second employee", 2000));
		put(new Employee(null, "third employee", 3000));
	}

	private Employee put(Employee employee) {
		if(Objects.isNull(employee.getEmployeeId()))
			// this is only for learning purposes, this ID generation is seriously bad
			employee.setEmployeeId("ID"+(employees.size()+1));		
		employees.put(employee.getEmployeeId(), employee);
		return employee;
	}
	
	@Override
	public List<Employee> findAll() {
		return new ArrayList<>(employees.values());
	}

	@Override
	public Optional<Employee> findOne(String id) {
		return Optional.ofNullable(employees.get(id));
	}

	@Override
	public synchronized Employee save(Employee employee) {
		return put(employee);
	}

}
