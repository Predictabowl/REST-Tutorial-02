package com.examples.service;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

import com.examples.model.Employee;
import com.examples.repositories.EmployeeRespository;

public class EmployeeServiceImpl implements EmployeeService {
	
	private EmployeeRespository repository;
	
	@Inject
	public EmployeeServiceImpl(EmployeeRespository repository) {
		super();
		this.repository = repository;
	}

	@Override
	public synchronized List<Employee> allEmployees() {
		return repository.findAll();
	}

	@Override
	public synchronized Employee getEmployeeById(String id) {
		return repository.findOne(id).orElseThrow(() -> new NotFoundException("Employee not found with id: "+id));
	}

	@Override
	public synchronized Employee addEmployee(Employee employee) {
		validateEmployee(employee);
		return repository.save(employee);
	}


	@Override
	public synchronized Employee replaceEmployee(String id, Employee employee) {
		validateEmployee(employee);
		if(!repository.findOne(id).isPresent())
				throw new NotFoundException("Employee not found with id: "+id);
		employee.setEmployeeId(id);
		return repository.save(employee);
	}

	@Override
	public synchronized Employee deleteEmployee(String id) {
		return repository.delete(id);
	}
	
	private void validateEmployee(Employee employee) {
		if (Objects.isNull(employee))
			throw new BadRequestException("Missing values for employee.");
		if(!Objects.isNull(employee.getEmployeeId()))
			throw new BadRequestException("Unexpected Id specification for employee, Id should be null.");
	}
}
