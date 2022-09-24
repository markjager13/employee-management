package com.mj.ema.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.mj.ema.employee.Employee;
import com.mj.ema.employee.EmployeeRepository;
import com.mj.ema.employee.Gender;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.StringUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestPropertySource(
        locations = "classpath:application-it.properties"
)

@AutoConfigureMockMvc
public class EmployeeIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmployeeRepository employeeRepository;

    private final Faker faker = new Faker();


    @Test
    void canRegisterNewEmployee() throws Exception {
        // given
        String name = String.format(
                "%s %s",
                faker.name().firstName(),
                faker.name().lastName()
        );

        Employee employee = new Employee(
                name,
                String.format("%s@fakeemail.com",
                        StringUtils.trimAllWhitespace(name.trim().toLowerCase())),
                Gender.FEMALE
        );
        // when
        ResultActions resultActions = mockMvc
                .perform(post("/api/v1/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employee)));
        // then
        resultActions.andExpect(status().isOk());
        List<Employee> employees = employeeRepository.findAll();
        assertThat(employees)
                .usingElementComparatorIgnoringFields("id")
                .contains(employee);

    }

    @Test
    void canDeleteEmployee() throws Exception {
        // given
        String name = String.format(
                "%s %s",
                faker.name().firstName(),
                faker.name().lastName()
        );

        String email = String.format("%s@fakeemail.com",
                StringUtils.trimAllWhitespace(name.trim().toLowerCase()));

        Employee employee = new Employee(
                name,
                email,
                Gender.FEMALE
        );

        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee)))
                .andExpect(status().isOk());

        MvcResult getEmployeesResult = mockMvc.perform(get("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String contentAsString = getEmployeesResult
                .getResponse()
                .getContentAsString();

        List<Employee> employees = objectMapper.readValue(
                contentAsString,
                new TypeReference<>() {
                }
        );

        long id = employees.stream()
                .filter(e -> e.getEmail().equals(employee.getEmail()))
                .map(Employee::getId)
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException(
                                "employee with email: " + email + " not found"));

        // when
        ResultActions resultActions = mockMvc
                .perform(delete("/api/v1/employees/" + id));

        // then
        resultActions.andExpect(status().isOk());
        boolean exists = employeeRepository.existsById(id);
        assertThat(exists).isFalse();
    }

}
