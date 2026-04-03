package com.thuannluit.student_management.service.impl;

import com.thuannluit.student_management.dto.StudentDTO;
import com.thuannluit.student_management.entity.Student;
import com.thuannluit.student_management.exception.ResourceNotFoundException;
import com.thuannluit.student_management.mapper.StudentMapper;
import com.thuannluit.student_management.repository.StudentRepository;
import com.thuannluit.student_management.service.StudentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentServiceImpl implements StudentService {

    @Autowired
    StudentRepository studentRepository;
    @Autowired
    StudentMapper studentMapper;

    @Override
    @Transactional
    public StudentDTO createStudent(StudentDTO studentDTO) {
        log.info("Attempting to create a new student with email: {}", studentDTO.email());

        Student student = studentMapper.toEntity(studentDTO);
        Student saved = studentRepository.save(student);

        log.info("Student created successfully with ID: {}", saved.getStudentCode());
        return studentMapper.toStudentDto(saved);
    }

    @Override
    public StudentDTO getStudentById(String id) {
        log.info("Fetching student with ID: {}", id);

        Integer studentId = parseId(id);

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> {
                    log.warn("Student not found for ID: {}", studentId);
                    return new ResourceNotFoundException("student.not.found");
                });

        log.debug("Successfully fetched student with ID: {}", studentId);
        return studentMapper.toStudentDto(student);
    }

    @Override
    public List<StudentDTO> getAllStudents() {
        log.info("Fetching all students from the database");

        List<StudentDTO> students = studentRepository.findAll().stream()
                .map(studentMapper::toStudentDto)
                .toList();

        log.info("Successfully fetched {} students", students.size());
        return students;
    }

    @Override
    @Transactional
    public StudentDTO updateStudent(String id, StudentDTO studentDTO) {
        log.info("Attempting to update student with ID: {}", id);

        Integer studentId = parseId(id);

        Student existingStudent = studentRepository.findById(studentId)
                .orElseThrow(() -> {
                    log.warn("Failed to update. Student not found for ID: {}", studentId);
                    return new ResourceNotFoundException("student.not.found");
                });

        studentMapper.updateStudentFromDto(studentDTO, existingStudent);
        Student updated = studentRepository.save(existingStudent);

        log.info("Student with ID: {} updated successfully", studentId);
        return studentMapper.toStudentDto(updated);
    }

    @Override
    @Transactional
    public void deleteStudent(String id) {
        log.info("Attempting to delete student with ID: {}", id);

        Integer studentId = parseId(id);

        if (!studentRepository.existsById(studentId)) {
            log.warn("Failed to delete. Student not found for ID: {}", studentId);
            throw new ResourceNotFoundException("student.not.found");
        }

        studentRepository.deleteById(studentId);
        log.info("Student with ID: {} deleted successfully", studentId);
    }

    private Integer parseId(String id) {
        log.debug("Parsing student ID string to integer: {}", id);
        try {
            return Integer.valueOf(id);
        } catch (NumberFormatException ex) {
            log.error("Failed to parse student ID: {}. It must be an integer.", id);
            throw new ResourceNotFoundException("student.invalid.id");
        }
    }
}
