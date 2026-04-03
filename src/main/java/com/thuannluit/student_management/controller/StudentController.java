package com.thuannluit.student_management.controller;

import com.thuannluit.student_management.dto.StudentDTO;
import com.thuannluit.student_management.entity.Student;
import com.thuannluit.student_management.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Student", description = "Student management APIs")
@RestController
@RequestMapping("/v1/students")
public class StudentController {

    @Autowired
    StudentService service;

    @Operation(
            summary = "Create student",
            description = "Create a new student (ADMIN only)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Student created successfully",
                    content = @Content(schema = @Schema(implementation = StudentDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "403", description = "Forbidden - requires ADMIN role")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StudentDTO> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Student data",
                    required = true,
                    content = @Content(schema = @Schema(implementation = StudentDTO.class))
            )
            @Valid @RequestBody StudentDTO dto) {

        StudentDTO saved = service.createStudent(dto);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Get all students",
            description = "Retrieve all students"
    )
    @ApiResponse(responseCode = "200", description = "Success",
            content = @Content(schema = @Schema(implementation = StudentDTO.class)))
    @GetMapping
    public List<StudentDTO> getAll() {
        return service.getAllStudents();
    }

    @Operation(
            summary = "Get student by ID",
            description = "Retrieve a student by ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = @Content(schema = @Schema(implementation = StudentDTO.class))),
            @ApiResponse(responseCode = "404", description = "Student not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<StudentDTO> getById(
            @Parameter(description = "Student ID", example = "123")
            @PathVariable String id) {

        StudentDTO std = service.getStudentById(id);
        return new ResponseEntity<>(std, HttpStatus.OK);
    }

    @Operation(
            summary = "Update student",
            description = "Update student information (ADMIN only)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated successfully",
                    content = @Content(schema = @Schema(implementation = StudentDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "403", description = "Forbidden - requires ADMIN role"),
            @ApiResponse(responseCode = "404", description = "Student not found")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StudentDTO> update(
            @Parameter(description = "Student ID", example = "123")
            @PathVariable String id,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated student data",
                    required = true,
                    content = @Content(schema = @Schema(implementation = StudentDTO.class))
            )
            @Valid @RequestBody StudentDTO student) {

        StudentDTO std = service.updateStudent(id, student);
        return new ResponseEntity<>(std, HttpStatus.OK);
    }

    @Operation(
            summary = "Delete student",
            description = "Delete a student (ADMIN only)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden - requires ADMIN role"),
            @ApiResponse(responseCode = "404", description = "Student not found")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Student ID", example = "123")
            @PathVariable String id) {

        service.deleteStudent(id);
        return ResponseEntity.ok().build();
    }
}
