package com.shopping_mall_api.repository;

import com.shopping_mall_api.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    Integer id(Integer id);
}