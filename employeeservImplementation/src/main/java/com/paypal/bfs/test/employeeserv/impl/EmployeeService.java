package com.paypal.bfs.test.employeeserv.impl;

import com.paypal.bfs.test.employeeserv.api.model.Employee;

/**
 * Interface to manage employee objects.
 */
public interface EmployeeService {

	/**
	 * Loads {@link Employee} object by provided employee id. Throws an
	 * {@link EntityNotFoundException} if no employee found for the id.
	 * 
	 * @param id
	 * @return
	 */
	public Employee getEmployee(Integer id);

	/**
	 * Creates {@link Employee} object with the provided details. Returns the new
	 * employee Id. Throws an {@link IllegalArgumentException} if the object fails
	 * validation.
	 * 
	 * @param employee
	 * @return
	 */
	public Integer addEmployee(Employee employee);

}
