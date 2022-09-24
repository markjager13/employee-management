package com.mj.ema.employee;

import com.mj.ema.employee.exception.BadRequestException;
import com.mj.ema.employee.exception.EmployeeNotFoundException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    // Note: don't want to test the real employee repository while testing employee service
    // we know it works, so we just want to mock its implementation inside of employee service test
    // unit test is fast, we don't need to bring up database, etc.
    // we've done that work once, so we can just mock it

    @Mock
    private EmployeeRepository employeeRepository;
    private EmployeeService underTest;

    @BeforeEach
    void setUp() {
        underTest = new EmployeeService(employeeRepository);
    }

    @Test
    void CanGetAllEmployees() {
        // when
        underTest.getAllEmployees();
        // then
        verify(employeeRepository).findAll();
    }

    @Test
    void canAddEmployee() {
        // given
        Employee employee = new Employee(
                "new employee",
                "newEmployee@fakeemail.com",
                Gender.MALE
        );
        // when
        underTest.addEmployee(employee);
        // then
        ArgumentCaptor<Employee> employeeArgumentCaptor = ArgumentCaptor.forClass(Employee.class);
        verify(employeeRepository).save(employeeArgumentCaptor.capture());
        Employee capturedEmployee = employeeArgumentCaptor.getValue();
        assertThat(capturedEmployee).isEqualTo(employee);
    }

    @Test
    void willThrowWhenEmailIsTaken() {
        // given
        Employee employee = new Employee(
                "new employee",
                "newEmployee@fakeemail.com",
                Gender.MALE
        );

        given(employeeRepository.selectExistsEmail(anyString()))
                .willReturn(true);

        // when
        // then
        assertThatThrownBy(() -> underTest.addEmployee(employee))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Email " + employee.getEmail() + " taken");
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void canDeleteEmployee() {
        // given
        long id = 10;
        given(employeeRepository.existsById(id))
                .willReturn(true);
        // when
        underTest.deleteEmployee(id);

        // then
        verify(employeeRepository).deleteById(id);
    }

    @Test
    void willThrowWhenDeleteEmployeeNotFound() {
        // given
        long id = 10;
        given(employeeRepository.existsById(id))
                .willReturn(false);
        // when
        // then
        assertThatThrownBy(() -> underTest.deleteEmployee(id))
                .isInstanceOf(EmployeeNotFoundException.class)
                .hasMessageContaining("Employee with id " + id + " does not exists");

        verify(employeeRepository, never()).deleteById(any());
    }

}