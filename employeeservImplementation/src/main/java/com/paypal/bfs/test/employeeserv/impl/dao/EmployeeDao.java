package com.paypal.bfs.test.employeeserv.impl.dao;

import org.springframework.data.repository.CrudRepository;

import com.paypal.bfs.test.employeeserv.impl.entity.EmployeeEntity;

/**
 * Interface to manage {@link EmployeeEntity} objects in database.
 */
public interface EmployeeDao extends CrudRepository<EmployeeEntity, Integer>{

}
