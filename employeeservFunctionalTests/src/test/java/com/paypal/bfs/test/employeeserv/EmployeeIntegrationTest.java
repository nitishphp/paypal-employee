package com.paypal.bfs.test.employeeserv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.paypal.bfs.test.employeeserv.api.EmployeeResource;
import com.paypal.bfs.test.employeeserv.api.model.Address;
import com.paypal.bfs.test.employeeserv.api.model.Employee;

/**
 * Integration test for {@link EmployeeResource}}
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = EmployeeservApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeIntegrationTest {

	private static final Integer NON_EXISTING_EMPLOYEE_ID = 9999;
	private static final String FIRST_NAME = "Jane";
	private static final String LAST_NAME = "Doe";
	private static final String DATE_OF_BIRTH_STR = "1990-12-12";
	private static final String LINE1 = "856";
	private static final String LINE2 = "Light sun way";
	private static final String CITY = "Orlando";
	private static final String STATE = "CA";
	private static final String COUNTRY = "USA";
	private static final String ZIP_CODE = "29132";

	@LocalServerPort
	private int port;

	private TestRestTemplate restTemplate = new TestRestTemplate();

	private HttpHeaders headers = new HttpHeaders();

	/*
	 * Test a non existing employee id returns a 404 not found.
	 */
	@Test
	public void testGetEmployee_NonExisting() throws JSONException {

		HttpEntity<String> entity = new HttpEntity<String>(null, headers);

		ResponseEntity<String> getResponse = restTemplate.exchange(
				buildURL("/v1/bfs/employees/" + NON_EXISTING_EMPLOYEE_ID), HttpMethod.GET, entity, String.class);

		assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
	}

	/*
	 * Persist an employee with a post and ensure that the same employee can be
	 * retrieved via get.
	 */
	@Test
	public void testCreateAndGetEmployee() {

		Employee employee = buildEmployee();

		HttpEntity<Employee> entity = new HttpEntity<>(employee, headers);

		ResponseEntity<Employee> postResponse = restTemplate.exchange(buildURL("/v1/bfs/employees"), HttpMethod.POST,
				entity, Employee.class);

		assertEquals(HttpStatus.OK, postResponse.getStatusCode());
		assertNotNull(postResponse.getBody());
		assertNotNull(postResponse.getBody().getId());

		Integer employeeId = postResponse.getBody().getId();

		verifyResponseFields(postResponse.getBody());

		ResponseEntity<Employee> getResponse = restTemplate.exchange(buildURL("/v1/bfs/employees/" + employeeId),
				HttpMethod.GET, entity, Employee.class);

		assertNotNull(getResponse.getBody());
		verifyResponseFields(getResponse.getBody());
	}

	private void verifyResponseFields(Employee result) {
		assertEquals(FIRST_NAME, result.getFirstName());
		assertEquals(LAST_NAME, result.getLastName());
		assertEquals(DATE_OF_BIRTH_STR, result.getDateOfBirth());

		assertNotNull(result.getAddress());
		assertEquals(LINE1, result.getAddress().getLine1());
		assertEquals(LINE2, result.getAddress().getLine2());
		assertEquals(CITY, result.getAddress().getCity());
		assertEquals(STATE, result.getAddress().getState());
		assertEquals(COUNTRY, result.getAddress().getCountry());
		assertEquals(ZIP_CODE, result.getAddress().getZipCode());
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

	private String buildURL(String uri) {
		return "http://localhost:" + port + uri;
	}
}