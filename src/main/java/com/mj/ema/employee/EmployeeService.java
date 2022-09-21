package com.mj.ema.employee;

import com.mj.ema.employee.exception.BadRequestException;
import com.mj.ema.employee.exception.EmployeeNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@AllArgsConstructor
@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public void addEmployee(Employee employee) {
        Boolean existsEmail = employeeRepository
                .selectExistsEmail(employee.getEmail());
        if (existsEmail) {
            throw new BadRequestException(
                    "Email " + employee.getEmail() + " taken");
        }

        employeeRepository.save(employee);
    }

    public void deleteEmployee(Long employeeId) {
        if(!employeeRepository.existsById(employeeId)) {
            throw new EmployeeNotFoundException(
                    "Employee with id " + employeeId + " does not exists");
        }
        employeeRepository.deleteById(employeeId);
    }
}
