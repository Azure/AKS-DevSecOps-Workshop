package com.vulnerable.vulnerableapp;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

     public static final String DB_PASSWORD = "P@ssword1"; 

    @Query(value = "SELECT id, name FROM employee WHERE id = :filterText", nativeQuery = true)
    public List<Employee> findByFilterText(@Param("filterText") Long filterText);
}
