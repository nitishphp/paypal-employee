 package com.paypal.bfs.test.employeeserv.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.paypal.bfs.test.employeeserv.api.EmployeeResource;
import com.paypal.bfs.test.employeeserv.api.model.Employee;

/**
 * Implementation for {@link EmployeeResource}.
 */
@RestController
public class EmployeeResourceImpl implements EmployeeResource {

	@Autowired 
	private EmployeeService employeeService;
	
    @Override
    public ResponseEntity<Employee> employeeGetById(Integer id) {
    	return convert(employeeService.getEmployee(id));
    }

	@Override
	public ResponseEntity<Employee> createEmployee(Employee employee) {
		Integer empId = employeeService.addEmployee(employee);
		return employeeGetById(empId);
	}
	
	private ResponseEntity<Employee> convert(Employee employee) {
		return new ResponseEntity<>(employee, HttpStatus.OK);
	}
}
	