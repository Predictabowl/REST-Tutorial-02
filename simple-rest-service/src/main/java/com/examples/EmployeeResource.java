package com.examples;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.examples.model.Employee;
import com.examples.repositories.FakeEmployeeRepository;

@Path("employees")
public class EmployeeResource {

	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path(value = "{id}")
	public Employee getItXML(@PathParam("id") String id) {
		return FakeEmployeeRepository.isntance.findOne(id)
				.orElseThrow(() -> new NotFoundException("Employee not found with id: "+id));
	}
	
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public List<Employee> getAllEmployees(){
		return FakeEmployeeRepository.isntance.findAll();
	}
	
}
