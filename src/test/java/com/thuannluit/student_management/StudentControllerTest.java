package com.thuannluit.student_management;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thuannluit.student_management.controller.StudentController;
import com.thuannluit.student_management.dto.StudentDTO;
import com.thuannluit.student_management.exception.GlobalExceptionHandler;
import com.thuannluit.student_management.exception.ResourceNotFoundException;
import com.thuannluit.student_management.service.MessageService;
import com.thuannluit.student_management.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class StudentControllerTest {

    private MockMvc mockMvc;

    private StudentService studentService;

    private MessageService messageService;

    private ObjectMapper objectMapper;

    private StudentDTO sampleDTO;

    @BeforeEach
    void setUp() {
        studentService = mock(StudentService.class);
        messageService = mock(MessageService.class);
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        when(messageService.get(anyString())).thenAnswer(invocation -> invocation.getArgument(0));

        StudentController controller = new StudentController();
        org.springframework.test.util.ReflectionTestUtils.setField(controller, "service", studentService);

        GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();
        org.springframework.test.util.ReflectionTestUtils.setField(exceptionHandler, "messageService", messageService);

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(exceptionHandler)
                .build();

        sampleDTO = new StudentDTO(
                "John",
                "Doe",
                "john.doe@example.com",
                LocalDate.of(2000, 1, 1)
        );
    }

    @Test
    void createStudent_shouldReturn201() throws Exception {
        given(studentService.createStudent(any(StudentDTO.class))).willReturn(sampleDTO);

        mockMvc.perform(post("/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.dateOfBirth").value("2000-01-01"));
    }

    @Test
    void getAllStudents_shouldReturnList() throws Exception {
        given(studentService.getAllStudents()).willReturn(List.of(sampleDTO));

        mockMvc.perform(get("/v1/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].email").value("john.doe@example.com"));
    }

    @Test
    void getAllStudents_whenEmpty_shouldReturnEmptyList() throws Exception {
        given(studentService.getAllStudents()).willReturn(List.of());

        mockMvc.perform(get("/v1/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getStudentById_shouldReturn200() throws Exception {
        given(studentService.getStudentById("1")).willReturn(sampleDTO);

        mockMvc.perform(get("/v1/students/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    void getStudentById_notFound_shouldReturn404() throws Exception {
        given(studentService.getStudentById("999"))
                .willThrow(new ResourceNotFoundException("resource.not.found"));

        mockMvc.perform(get("/v1/students/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("resource.not.found"));
    }

    @Test
    void updateStudent_shouldReturn200() throws Exception {
        StudentDTO updated = new StudentDTO(
                "Jane", "Doe", "jane.doe@example.com", LocalDate.of(2001, 5, 10)
        );
        given(studentService.updateStudent(eq("1"), any(StudentDTO.class))).willReturn(updated);

        mockMvc.perform(put("/v1/students/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.email").value("jane.doe@example.com"));
    }

    @Test
    void updateStudent_notFound_shouldReturn404() throws Exception {
        given(studentService.updateStudent(eq("999"), any(StudentDTO.class)))
                .willThrow(new ResourceNotFoundException("resource.not.found"));

        mockMvc.perform(put("/v1/students/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("resource.not.found"));
    }

    @Test
    void deleteStudent_shouldReturn200() throws Exception {
        doNothing().when(studentService).deleteStudent("1");

        mockMvc.perform(delete("/v1/students/1"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteStudent_notFound_shouldReturn404() throws Exception {
        doThrow(new ResourceNotFoundException("resource.not.found"))
                .when(studentService).deleteStudent("999");

        mockMvc.perform(delete("/v1/students/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("resource.not.found"));
    }
}
