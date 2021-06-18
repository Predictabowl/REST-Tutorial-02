package com.examples.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.examples.model.Employee;
import com.examples.repositories.EmployeeRespository;

public class EmployeeServiceImplTest {

	@Mock
	private EmployeeRespository repository;
	
	@InjectMocks
	private EmployeeServiceImpl service;
	
	@Before
	public void setUp() {
		openMocks(this);
	}
	
	@Test
	public void test_allEmployees() {
		LinkedList<Employee> employees = new LinkedList<>();
		employees.add(new Employee("ID1", "first", 1000));
		employees.add(new Employee("ID2", "second", 2000));
		
		when(repository.findAll()).thenReturn(employees);
		
		List<Employee> allEmployees = service.allEmployees();
		
		assertThat(allEmployees).isSameAs(employees);
	}
	
	@Test
	public void test_getEmployeeById_success() {
		Employee employee = new Employee("ID2", "second", 2000);
		when(repository.findOne("ID2")).thenReturn(Optional.of(employee));
		
		Employee findOne = service.getEmployeeById("ID2");
		
		assertThat(findOne).isSameAs(employee);
	}
	
	@Test
	public void test_getEmployeeById_failure() {
		when(repository.findOne(isA(String.class))).thenReturn(Optional.empty());
		
		assertThatThrownBy(() ->  service.getEmployeeById("ID2"))
			.isInstanceOf(NotFoundException.class)
			.hasMessage("Employee not found with id: ID2");
	}
	
	@Test
	public void test_add_new_Employee_successful() {
		Employee employee = new Employee(null,"to be saved",500);
		Employee employee2 = new Employee("ID4", "saved", 1000);
		when(repository.save(employee))
			.thenReturn(employee2);
		
		Employee savedEmployee = service.addEmployee(employee);
		
		assertThat(savedEmployee).isSameAs(employee2);
	}
	
	@Test
	public void test_add_new_Employee_with_not_null_Id() {
		Employee employee = new Employee("ID3","to be saved",500);
		
		assertThatThrownBy(() -> service.addEmployee(employee))
			.isInstanceOf(BadRequestException.class)
			.hasMessage("Unexpected Id specification for employee, Id should be null.");
		
		verifyNoInteractions(repository);
	}
	
	@Test
	public void test_add_new_Employee_with_employee_null() {
		assertThatThrownBy(() -> service.addEmployee(null))
			.isInstanceOf(BadRequestException.class)
			.hasMessage("Missing values for employee.");
		
		verifyNoInteractions(repository);
	}
	
	
	@Test
	public void test_update_Employee_successful() {
		Employee employee = new Employee(null,"updated",500);
		Employee newEmployee = new Employee("ID2","updated",500);
		when(repository.findOne("ID2")).thenReturn(Optional.of(new Employee()));
		when(repository.save(employee))
			.thenReturn(newEmployee);
		
		Employee savedEmployee = service.replaceEmployee("ID2",employee);
		
		assertThat(savedEmployee).isSameAs(newEmployee);
		verify(repository).findOne("ID2");
	}
	
	@Test
	public void test_update_Employee_with_not_null_Id() {
		Employee employee = new Employee("ID2","updated",500);
		
		assertThatThrownBy(() -> service.replaceEmployee("ID2",employee))
			.isInstanceOf(BadRequestException.class)
			.hasMessage("Unexpected Id specification for employee, Id should be null.");
		
		verifyNoInteractions(repository);
	}
	
	@Test
	public void test_update_Employee_with_null_employee() {
		assertThatThrownBy(() -> service.replaceEmployee("ID2",null))
			.isInstanceOf(BadRequestException.class)
			.hasMessage("Missing values for employee.");
		
		verifyNoInteractions(repository);
	}
	
	@Test
	public void test_update_Employee_not_found() {
		Employee employee = new Employee(null,"updated",500);
		when(repository.findOne(isA(String.class))).thenReturn(Optional.empty());
		
		assertThatThrownBy(() -> service.replaceEmployee("IDE",employee))
			.isInstanceOf(NotFoundException.class)
			.hasMessage("Employee not found with id: IDE");
		
		verify(repository).findOne("IDE");
		verifyNoMoreInteractions(repository);
	}
	
	@Test
	public void test_delete_employee_success() {
		Employee toDelete = new Employee();
		when(repository.delete("IDE")).thenReturn(toDelete);
		
		Employee employee = service.deleteEmployee("IDE");
		
		assertThat(employee).isSameAs(toDelete);
	}

}
