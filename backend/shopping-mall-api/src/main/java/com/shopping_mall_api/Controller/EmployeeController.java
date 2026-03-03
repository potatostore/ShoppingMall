package com.shopping_mall_api.Controller;

import com.shopping_mall_api.Entity.Employee;
import com.shopping_mall_api.Repository.EmployeeRepository;
import com.shopping_mall_api.TableNames;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/" + TableNames.employeeTableName)
public class EmployeeController {
    private final EmployeeRepository employeeRepository;

    EmployeeController(EmployeeRepository employeeRepository){
        this.employeeRepository = employeeRepository;
    }

    @GetMapping
    public List<Employee> getEmployee(){
        return employeeRepository.findAll();
    }

    @GetMapping("/{id}")
    public Employee getEmployee(@PathVariable Integer id){
        return employeeRepository.findById(id).orElse(null);
    }

    @PostMapping
    public ResponseEntity<Employee> postEmployee(@RequestBody Employee employee){

    }

    @PutMapping
    public ResponseEntity<Employee> putEmployee(@PathVariable Integer id, @RequestBody Employee employee) {

    }

    @DeleteMapping("/{id}")
    public void deletemapping(@PathVariable Integer id){

    }
}
