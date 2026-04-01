package com.thuannluit.student_management.service;

import com.thuannluit.student_management.dto.StudentDTO;

import java.util.List;
import java.util.UUID;

public interface StudentService {
    StudentDTO createStudent(StudentDTO studentDTO);
    StudentDTO getStudentById(String id);
    List<StudentDTO> getAllStudents();
    StudentDTO updateStudent(String id, StudentDTO studentDTO);
    void deleteStudent(String id);
}
