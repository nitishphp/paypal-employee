package com.paypal.bfs.test.employeeserv.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.paypal.bfs.test.employeeserv.api.model.Address;
import com.paypal.bfs.test.employeeserv.api.model.Employee;
import com.paypal.bfs.test.employeeserv.common.EntityNotFoundException;
import com.paypal.bfs.test.employeeserv.impl.dao.EmployeeDao;
import com.paypal.bfs.test.employeeserv.impl.entity.AddressEntity;
import com.paypal.bfs.test.employeeserv.impl.entity.EmployeeEntity;

/**
 * Unit tests for {@link EmployeeServiceImpl}.
 */
public class EmployeeServiceTest {
	
	private static final Integer NON_EXISTING_EMPLOYEE_ID = 13;
	private static final Integer EXISTING_EMPLOYEE_ID = 10;
	private static final String FIRST_NAME = "Jane";
	private static final String LAST_NAME = "Doe";
	private static final String DATE_OF_BIRTH_STR = "1990-12-12";
	private static final LocalDate DATE_OF_BIRTH = LocalDate.parse(DATE_OF_BIRTH_STR);
	private static final String LINE1 = "856";
	private static final String LINE2 = "Light sun way";
	private static final String CITY = "Orlando";
	private static final String STATE = "CA";
	private static final String COUNTRY = "USA";
	private static final String ZIP_CODE = "29132";

	@Mock
	private EmployeeDao employeeDao;
	
	@Captor
	private ArgumentCaptor<EmployeeEntity> empEntityCaptor;

	@InjectMocks
	private EmployeeService service = new EmployeeServiceImpl();

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		when(employeeDao.findById(EXISTING_EMPLOYEE_ID)).thenReturn(Optional.of(buildEmployeeEntity()));
		when(employeeDao.findById(NON_EXISTING_EMPLOYEE_ID)).thenReturn(Optional.empty());
	}
	
	@Test(expected = EntityNotFoundException.class)
	public void testGetEmployee_NotFound() {
		service.getEmployee(NON_EXISTING_EMPLOYEE_ID);
	}
	
	@Test
	public void testGetEmployee() {
		Employee employee = service.getEmployee(EXISTING_EMPLOYEE_ID);
		
		assertNotNull(employee);
		assertEquals(FIRST_NAME, employee.getFirstName());
		assertEquals(LAST_NAME, employee.getLastName());
		assertEquals(DATE_OF_BIRTH_STR, employee.getDateOfBirth());
		
		assertNotNull(employee.getAddress());
		assertEquals(LINE1, employee.getAddress().getLine1());
		assertEquals(LINE2, employee.getAddress().getLine2());
		assertEquals(CITY, employee.getAddress().getCity());
		assertEquals(STATE, employee.getAddress().getState());
		assertEquals(COUNTRY, employee.getAddress().getCountry());
		assertEquals(ZIP_CODE, employee.getAddress().getZipCode());
	}
	
	@Test
	public void testGetEmployee_NoDOB() {
		EmployeeEntity employeeEntity = buildEmployeeEntity();
		employeeEntity.setDateOfBirth(null);
		when(employeeDao.findById(EXISTING_EMPLOYEE_ID)).thenReturn((Optional.of(employeeEntity)));
		
		Employee employee = service.getEmployee(EXISTING_EMPLOYEE_ID);
		
		assertNotNull(employee);
		assertNull(employee.getDateOfBirth());
	}
	
	@Test
	public void testGetEmployee_NoAddress() {
		EmployeeEntity employeeEntity = buildEmployeeEntity();
		employeeEntity.setAddresses(new ArrayList<>());
		when(employeeDao.findById(EXISTING_EMPLOYEE_ID)).thenReturn((Optional.of(employeeEntity)));
		
		Employee employee = service.getEmployee(EXISTING_EMPLOYEE_ID);
		assertNotNull(employee);
		assertNull(employee.getAddress());
		
		employeeEntity.setAddresses(null);
		
		employee = service.getEmployee(EXISTING_EMPLOYEE_ID);
		assertNotNull(employee);
		assertNull(employee.getAddress());
	}
	
	@Test
	public void testGetEmployee_MultipleAddresses() {
		EmployeeEntity employeeEntity = buildEmployeeEntity();
		AddressEntity addressEntity = buildAddressEntity();
		addressEntity.setLine1("New Address");
		employeeEntity.getAddresses().add(addressEntity);
		when(employeeDao.findById(EXISTING_EMPLOYEE_ID)).thenReturn((Optional.of(employeeEntity)));
		
		Employee employee = service.getEmployee(EXISTING_EMPLOYEE_ID);
		
		assertNotNull(employee);
		assertNotNull(employee.getAddress());
		assertEquals(LINE1, employee.getAddress().getLine1());
	}
	
	@Test
	public void testAddEmployee_ValidatesRequiredFields() {
		Employee employee = buildEmployee();
		employee.setFirstName(null);
		assertIllegalArgumentException(employee);
		employee.setFirstName(" ");
		assertIllegalArgumentException(employee);
		
		employee = buildEmployee();
		employee.setLastName(null);
		assertIllegalArgumentException(employee);
		employee.setLastName(" ");
		assertIllegalArgumentException(employee);
		
		employee = buildEmployee();
		employee.setDateOfBirth(null);
		assertIllegalArgumentException(employee);
		employee.setDateOfBirth(" ");
		assertIllegalArgumentException(employee);
		employee.setDateOfBirth("ABC");
		assertIllegalArgumentException(employee);
		employee.setDateOfBirth("12-12-2020");
		assertIllegalArgumentException(employee);
		employee.setDateOfBirth("1990-13-50");
		assertIllegalArgumentException(employee);
		
		employee = buildEmployee();
		employee.getAddress().setLine1(null);
		assertIllegalArgumentException(employee);
		employee.getAddress().setLine1(" ");
		assertIllegalArgumentException(employee);
		
		employee = buildEmployee();
		employee.getAddress().setCity(null);
		assertIllegalArgumentException(employee);
		employee.getAddress().setCity(" ");
		assertIllegalArgumentException(employee);
		
		employee = buildEmployee();
		employee.getAddress().setState(null);
		assertIllegalArgumentException(employee);
		employee.getAddress().setState(" ");
		assertIllegalArgumentException(employee);
		
		employee = buildEmployee();
		employee.getAddress().setCountry(null);
		assertIllegalArgumentException(employee);
		employee.getAddress().setCountry(" ");
		assertIllegalArgumentException(employee);
		
		employee = buildEmployee();
		employee.getAddress().setZipCode(null);
		assertIllegalArgumentException(employee);
		employee.getAddress().setZipCode(" ");
		assertIllegalArgumentException(employee);
	}

	@Test
	public void testAddEmployee() {
		Employee employee = buildEmployee();
		
		service.addEmployee(employee);
		verify(employeeDao).save(empEntityCaptor.capture());
		
		EmployeeEntity employeeEntity = empEntityCaptor.getValue();
		assertEquals(FIRST_NAME, employeeEntity.getFirstName());
		assertEquals(LAST_NAME, employeeEntity.getLastName());
		assertEquals(DATE_OF_BIRTH, employeeEntity.getDateOfBirth());
	
		assertEquals(1, employeeEntity.getAddresses().size());
		AddressEntity addressEntity = employeeEntity.getAddresses().get(0);
		assertEquals(LINE1, addressEntity.getLine1());
		assertEquals(LINE2, addressEntity.getLine2());
		assertEquals(CITY, addressEntity.getCity());
		assertEquals(STATE, addressEntity.getState());
		assertEquals(COUNTRY, addressEntity.getCountry());
		assertEquals(ZIP_CODE, addressEntity.getZipCode());
	}
	
	private Employee buildEmployee() {
		Employee employee = new Employee();
		employee.setFirstName(FIRST_NAME);
		employee.setLastName(LAST_NAME);
		employee.setDateOfBirth(DATE_OF_BIRTH_STR);
		employee.setAddress(buildAddress());
		return employee;
	}

	private Address buildAddress() {
		Address address = new Address();
		address.setLine1(LINE1);
		address.setLine2(LINE2);
		address.setCity(CITY);
		address.setState(STATE);
		address.setCountry(COUNTRY);
		address.setZipCode(ZIP_CODE);
		return address;
	}

	private EmployeeEntity buildEmployeeEntity() {
		return EmployeeEntity.builder().id(EXISTING_EMPLOYEE_ID).firstName(FIRST_NAME).lastName(LAST_NAME)
				.dateOfBirth(DATE_OF_BIRTH).addresses(Lists.newArrayList(buildAddressEntity())).build();
	}
	
	private AddressEntity buildAddressEntity() {
		return AddressEntity.builder().line1(LINE1).line2(LINE2).city(CITY).state(STATE)
				.country(COUNTRY).zipCode(ZIP_CODE).build();
	}
	
	private void assertIllegalArgumentException(Employee employee) {
		try {
			service.addEmployee(employee);
			fail("Expected IllegalArgumentException. Instead got none.");
		} catch (IllegalArgumentException e) {
			// Do nothing. Expected. 
		} catch (Exception e) {
			fail("Expected IllegalArgumentException. Instead got " + e.getClass());
		}
	}
}
