package com.paypal.bfs.test.employeeserv.impl.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *	Represents an employee in the database.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "employee")
public class EmployeeEntity {
	@Id
	@GeneratedValue(
	    strategy= GenerationType.AUTO,
	    generator="native"
	)
	@GenericGenerator(
	    name = "native",
	    strategy = "native"
	)
	private Integer id;
	@Column(name = "first_name")
	private String firstName;
	@Column(name = "last_name")
	private String lastName;
	@Column(name = "dob")
	private LocalDate dateOfBirth;
	
	// Currently the API supports only one address for an employee. 
	// The simplest option would have been to add the address fields as columns directly in the employee table. 
	// But it seems logical that an employee might require multiple addresses in the future. 
	// The DB structure 1 employee -> many addresses has been structured with this consideration. 
	@OneToMany(cascade = CascadeType.ALL,orphanRemoval = true)
	private List<AddressEntity> addresses = new ArrayList<>();
}
