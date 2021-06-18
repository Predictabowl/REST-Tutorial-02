package com.examples;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.examples.model.Employee;
import com.examples.service.EmployeeService;

@Path("employees")
public class EmployeeResource {
	
	@Inject
	private EmployeeService employeeService;

	@GET
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Path(value = "{id}")
	public Employee getItXML(@PathParam("id") String id) {
		return employeeService.getEmployeeById(id);
	}
	
	@GET
	@Produces(value = {MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public List<Employee> getAllEmployees(){
		return employeeService.allEmployees();
	}
	
	@GET
	@Path(value = "count")
	@Produces(MediaType.TEXT_PLAIN)
	public String count() {
		return String.valueOf(employeeService.allEmployees().size());
	}
		
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addEmployee(Employee employee, @Context UriInfo uriInfo) throws URISyntaxException {
		Employee saved = employeeService.addEmployee(employee);
		return Response
				.created(new URI(uriInfo.getAbsolutePath()+"/"+saved.getEmployeeId()))
				.entity(saved)
				.build();
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = "{id}")
	public Employee updateEmployee(@PathParam("id") String id, Employee employee) {
		return employeeService.replaceEmployee(id,employee);
	}
	
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{id}")
	public Response deleteEmployee(@PathParam("id") String id) {
		return Response
				.accepted(employeeService.deleteEmployee(id))
				.build();
	}
}
