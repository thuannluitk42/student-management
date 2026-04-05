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
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.MediaType;
import org.springframework.restdocs.ManualRestDocumentation;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
class StudentControllerDocsTest {

    private MockMvc mockMvc;
    private StudentService studentService;
    private MessageService messageService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        studentService = mock(StudentService.class);
        messageService = mock(MessageService.class);
        when(messageService.get(anyString())).thenAnswer(invocation -> invocation.getArgument(0));

        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        StudentController controller = new StudentController();
        org.springframework.test.util.ReflectionTestUtils.setField(controller, "service", studentService);

        GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();
        org.springframework.test.util.ReflectionTestUtils.setField(exceptionHandler, "messageService", messageService);

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(exceptionHandler)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    @Test
    void createStudent_shouldGenerateDocs() throws Exception {
        StudentDTO dto = new StudentDTO("John", "Doe", "john.doe@example.com", LocalDate.of(2000, 1, 1));
        given(studentService.createStudent(any(StudentDTO.class))).willReturn(dto);

        mockMvc.perform(post("/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andDo(document("students/create",
                        requestFields(
                                fieldWithPath("firstName").description("First name"),
                                fieldWithPath("lastName").description("Last name"),
                                fieldWithPath("email").description("Email address"),
                                fieldWithPath("dateOfBirth").description("Date of birth (ISO format)")
                        ),
                        responseFields(
                                fieldWithPath("firstName").description("First name"),
                                fieldWithPath("lastName").description("Last name"),
                                fieldWithPath("email").description("Email address"),
                                fieldWithPath("dateOfBirth").description("Date of birth (ISO format)")
                        )
                ));
    }

    @Test
    void getAllStudents_shouldGenerateDocs() throws Exception {
        StudentDTO dto = new StudentDTO("John", "Doe", "john.doe@example.com", LocalDate.of(2000, 1, 1));
        given(studentService.getAllStudents()).willReturn(List.of(dto));

        mockMvc.perform(get("/v1/students")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("students/get-all",
                        responseFields(
                                fieldWithPath("[].firstName").description("First name"),
                                fieldWithPath("[].lastName").description("Last name"),
                                fieldWithPath("[].email").description("Email address"),
                                fieldWithPath("[].dateOfBirth").description("Date of birth (ISO format)")
                        )
                ));
    }

    @Test
    void getStudentById_shouldGenerateDocs() throws Exception {
        StudentDTO dto = new StudentDTO("John", "Doe", "john.doe@example.com", LocalDate.of(2000, 1, 1));
        given(studentService.getStudentById("1")).willReturn(dto);

        mockMvc.perform(get("/v1/students/{id}", "1"))
                .andExpect(status().isOk())
                .andDo(document("students/get-by-id",
                        pathParameters(
                                parameterWithName("id").description("Student ID")
                        ),
                        responseFields(
                                fieldWithPath("firstName").description("First name"),
                                fieldWithPath("lastName").description("Last name"),
                                fieldWithPath("email").description("Email address"),
                                fieldWithPath("dateOfBirth").description("Date of birth (ISO format)")
                        )
                ));
    }

    @Test
    void getStudentById_notFound_shouldGenerateErrorDocs() throws Exception {
        given(studentService.getStudentById("999"))
                .willThrow(new ResourceNotFoundException("resource.not.found"));

        mockMvc.perform(get("/v1/students/{id}", "999"))
                .andExpect(status().isNotFound())
                .andDo(document("students/get-by-id-404",
                        pathParameters(
                                parameterWithName("id").description("Student ID")
                        ),
                        responseFields(
                                fieldWithPath("errorCode").description("Error code"),
                                fieldWithPath("message").description("Error message"),
                                fieldWithPath("timestamp").description("Timestamp when error occurred")
                        )
                ));
    }

    @Test
    void updateStudent_shouldGenerateDocs() throws Exception {
        StudentDTO dto = new StudentDTO("Jane", "Doe", "jane.doe@example.com", LocalDate.of(2001, 5, 10));
        given(studentService.updateStudent(eq("1"), any(StudentDTO.class))).willReturn(dto);

        mockMvc.perform(put("/v1/students/{id}", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andDo(document("students/update",
                        pathParameters(
                                parameterWithName("id").description("Student ID")
                        ),
                        requestFields(
                                fieldWithPath("firstName").description("First name"),
                                fieldWithPath("lastName").description("Last name"),
                                fieldWithPath("email").description("Email address"),
                                fieldWithPath("dateOfBirth").description("Date of birth (ISO format)")
                        ),
                        responseFields(
                                fieldWithPath("firstName").description("First name"),
                                fieldWithPath("lastName").description("Last name"),
                                fieldWithPath("email").description("Email address"),
                                fieldWithPath("dateOfBirth").description("Date of birth (ISO format)")
                        )
                ));
    }

    @Test
    void updateStudent_notFound_shouldGenerateErrorDocs() throws Exception {
        StudentDTO dto = new StudentDTO("Jane", "Doe", "jane.doe@example.com", LocalDate.of(2001, 5, 10));
        given(studentService.updateStudent(eq("999"), any(StudentDTO.class)))
                .willThrow(new ResourceNotFoundException("resource.not.found"));

        mockMvc.perform(put("/v1/students/{id}", "999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andDo(document("students/update-404",
                        pathParameters(
                                parameterWithName("id").description("Student ID")
                        ),
                        responseFields(
                                fieldWithPath("errorCode").description("Error code"),
                                fieldWithPath("message").description("Error message"),
                                fieldWithPath("timestamp").description("Timestamp when error occurred")
                        )
                ));
    }

    @Test
    void deleteStudent_shouldGenerateDocs() throws Exception {
        doNothing().when(studentService).deleteStudent("1");

        mockMvc.perform(delete("/v1/students/{id}", "1"))
                .andExpect(status().isOk())
                .andDo(document("students/delete",
                        pathParameters(
                                parameterWithName("id").description("Student ID")
                        )
                ));
    }

    @Test
    void deleteStudent_notFound_shouldGenerateErrorDocs() throws Exception {
        doThrow(new ResourceNotFoundException("resource.not.found"))
                .when(studentService).deleteStudent("999");

        mockMvc.perform(delete("/v1/students/{id}", "999"))
                .andExpect(status().isNotFound())
                .andDo(document("students/delete-404",
                        pathParameters(
                                parameterWithName("id").description("Student ID")
                        ),
                        responseFields(
                                fieldWithPath("errorCode").description("Error code"),
                                fieldWithPath("message").description("Error message"),
                                fieldWithPath("timestamp").description("Timestamp when error occurred")
                        )
                ));
    }
}