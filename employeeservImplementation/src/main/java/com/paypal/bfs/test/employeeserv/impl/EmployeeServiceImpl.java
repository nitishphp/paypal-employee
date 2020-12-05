package com.paypal.bfs.test.employeeserv.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import javax.transaction.Transactional;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paypal.bfs.test.employeeserv.api.model.Address;
import com.paypal.bfs.test.employeeserv.api.model.Employee;
import com.paypal.bfs.test.employeeserv.common.EntityNotFoundException;
import com.paypal.bfs.test.employeeserv.impl.dao.EmployeeDao;
import com.paypal.bfs.test.employeeserv.impl.entity.AddressEntity;
import com.paypal.bfs.test.employeeserv.impl.entity.EmployeeEntity;

import jersey.repackaged.com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation for {@link EmployeeService}
 */
@Slf4j
@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService { 
	
	private static final String ISO_SHORT_FORMAT = "yyyy-MM-dd";
	
	@Autowired
	private EmployeeDao employeeDao;
	
	@Override
	public Employee getEmployee(Integer id) {
		Optional<EmployeeEntity> employeeEntity = employeeDao.findById(id);
		if (employeeEntity.isPresent()) {
			return convertEmployee(employeeEntity.get());
		} else {
			throw new EntityNotFoundException("Could not find employee for employeeId=" + id);
		}
	}

	@Override
	public Integer addEmployee(Employee employee) {
		validateNewEmployee(employee);
		validateAddress(employee.getAddress());
		EmployeeEntity empEntity = convertEmployee(employee);
		employeeDao.save(empEntity);
		return empEntity.getId();
	}
	
	private void validateNewEmployee(Employee employee) {
		if (StringUtils.isBlank(employee.getFirstName())) {
			throw new IllegalArgumentException("Missing required field first_name");
		}
		if (StringUtils.isBlank(employee.getLastName())) {
			throw new IllegalArgumentException("Missing required field last_name");
		}
		if (StringUtils.isBlank(employee.getDateOfBirth())) {
			throw new IllegalArgumentException("Missing required field date_of_birth");
		}
	}
	
	private void validateAddress(Address address) {
		if (StringUtils.isBlank(address.getLine1())) {
			throw new IllegalArgumentException("Missing required field line1");
		}
		if (StringUtils.isBlank(address.getCity())) {
			throw new IllegalArgumentException("Missing required field city");
		}
		if (StringUtils.isBlank(address.getState())) {
			throw new IllegalArgumentException("Missing required field state");
		}
		if (StringUtils.isBlank(address.getCountry())) {
			throw new IllegalArgumentException("Missing required field country");
		}
		if (StringUtils.isBlank(address.getZipCode())) {
			throw new IllegalArgumentException("Missing required field zip code");
		}
	}

	private EmployeeEntity convertEmployee(Employee employee) {
		EmployeeEntity employeeEntity = new EmployeeEntity();
		employeeEntity.setFirstName(employee.getFirstName());
		employeeEntity.setLastName(employee.getLastName());
        try {
        	employeeEntity.setDateOfBirth(LocalDate.parse(employee.getDateOfBirth(), DateTimeFormatter.ofPattern(ISO_SHORT_FORMAT)));
        } catch (DateTimeParseException e) {
        	throw new IllegalArgumentException(e.getMessage() + " Format of chosen date (should be " + ISO_SHORT_FORMAT + ") is invalid: " + employee.getDateOfBirth(), e);
        }
		employeeEntity.setAddresses(Lists.newArrayList(convertAddress(employee.getAddress())));
		return employeeEntity;
	}

	private AddressEntity convertAddress(Address address) {
		AddressEntity addressEntity = new AddressEntity();
		addressEntity.setLine1(address.getLine1());
		if (address.getLine2() != null) {
			addressEntity.setLine2(address.getLine2());
		}
		addressEntity.setCity(address.getCity());
		addressEntity.setState(address.getState());
		addressEntity.setCountry(address.getCountry());
		addressEntity.setZipCode(address.getZipCode());
		return addressEntity;
	}

	private Employee convertEmployee(EmployeeEntity employeeEntity) {
		Employee employee = new Employee();
		employee.setId(employeeEntity.getId());
		employee.setFirstName(employeeEntity.getFirstName());
		employee.setLastName(employeeEntity.getLastName());
		if (employeeEntity.getDateOfBirth() != null) {
			employee.setDateOfBirth(employeeEntity.getDateOfBirth().format(DateTimeFormatter.ofPattern(ISO_SHORT_FORMAT)));
		}
		
		// Currently the API supports only one address for an employee. But the DB allows multiple addresses.
		// This implementation considers the first address (if available) as the primary address. 
		// Currently if more than 1 address is found, it would signify an invalid scenario. Log a warning.
		if (employeeEntity.getAddresses() != null && !employeeEntity.getAddresses().isEmpty()) {
			employee.setAddress(convertAddress(employeeEntity.getAddresses().get(0)));
			if (employeeEntity.getAddresses().size() > 1) {
				log.warn("Found multiple addresses for employeeId={}", employeeEntity.getId());
			}
		}
		return employee;
	}
	
	private Address convertAddress(AddressEntity addressEntity) {
		Address address = new Address();
		address.setLine1(addressEntity.getLine1());
		address.setLine2(addressEntity.getLine2());	
		address.setCity(addressEntity.getCity());
		address.setState(addressEntity.getState());
		address.setCountry(addressEntity.getCountry());
		address.setZipCode(addressEntity.getZipCode());
		return address;
	}
}
