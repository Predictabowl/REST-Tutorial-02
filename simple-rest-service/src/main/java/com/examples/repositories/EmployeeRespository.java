package com.examples.repositories;

import java.util.List;
import java.util.Optional;

import com.examples.model.Employee;

public interface EmployeeRespository {
	
	public List<Employee> findAll();
	public Optional<Employee> findOne(String id);
	
}
