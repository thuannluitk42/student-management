package com.thuannluit.student_management.service.impl;

import com.thuannluit.student_management.dto.StudentDTO;
import com.thuannluit.student_management.entity.Student;
import com.thuannluit.student_management.exception.ResourceNotFoundException;
import com.thuannluit.student_management.mapper.StudentMapper;
import com.thuannluit.student_management.repository.StudentRepository;
import com.thuannluit.student_management.service.StudentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    @Autowired
    StudentRepository studentRepository;
    @Autowired
    StudentMapper studentMapper;

    @Override
    @Transactional
    public StudentDTO createStudent(StudentDTO studentDTO) {

        Student student = studentMapper.toEntity(studentDTO);
        Student saved = studentRepository.save(student);

        return studentMapper.toStudentDto(saved);
    }

    @Override
    public StudentDTO getStudentById(String id) {

        Integer studentId = parseId(id);

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("student.not.found"));

        return studentMapper.toStudentDto(student);
    }

    @Override
    public List<StudentDTO> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(studentMapper::toStudentDto)
                .toList();
    }

    @Override
    @Transactional
    public StudentDTO updateStudent(String id, StudentDTO studentDTO) {

        Integer studentId = parseId(id);

        Student existingStudent = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("student.not.found"));

        studentMapper.updateStudentFromDto(studentDTO, existingStudent);

        Student updated = studentRepository.save(existingStudent);

        return studentMapper.toStudentDto(updated);
    }

    @Override
    @Transactional
    public void deleteStudent(String id) {

        Integer studentId = parseId(id);

        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("student.not.found");
        }

        studentRepository.deleteById(studentId);
    }

    // =========================
    // HELPER
    // =========================
    private Integer parseId(String id) {
        try {
            return Integer.valueOf(id);
        } catch (NumberFormatException ex) {
            throw new ResourceNotFoundException("student.invalid.id");
        }
    }
}