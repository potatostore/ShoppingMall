package com.shopping_mall_api.Repository;

import com.shopping_mall_api.Entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
}