package com.shopping_mall_api.Controller;

import com.shopping_mall_api.Entity.Employee;
import com.shopping_mall_api.Repository.EmployeeRepository;
import com.shopping_mall_api.TableNames;
import org.springframework.http.HttpStatus;
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
    public Employee postEmployee(@RequestBody Employee employee){
        return employeeRepository.save(employee);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Employee> putEmployee(@PathVariable Integer id, @RequestBody Employee employee) {
        boolean exist = employeeRepository.existsById(id);

        employeeRepository.save(employee);

        return (exist) ?
                new ResponseEntity<Employee>(employee, HttpStatus.OK) :
                new ResponseEntity<Employee>(employee, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public void deleteEmployee(@PathVariable Integer id){
        employeeRepository.deleteById(id);
    }
}
