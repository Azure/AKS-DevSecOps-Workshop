package com.vulnerable.vulnerableapp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class VulnerableappApplicationTests {

	@Autowired
	private EmployeeRepository employeeRepository;

	
	//test insert employee 
	@Test
	void insertTest() {
		Employee employee = new Employee();
		employee.setId(10001L);
		employee.setName("Jane doe");
		employee.setDesignation("CSA");
		employee.setExpertise("Java");
		employeeRepository.save(employee);
	}

}
