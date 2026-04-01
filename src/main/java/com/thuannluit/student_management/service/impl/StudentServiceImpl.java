package com.thuannluit.student_management.service.impl;

import com.thuannluit.student_management.dto.StudentDTO;
import com.thuannluit.student_management.entity.Student;
import com.thuannluit.student_management.exception.ResourceNotFoundException;
import com.thuannluit.student_management.mapper.StudentMapper;
import com.thuannluit.student_management.repository.StudentRepository;
import com.thuannluit.student_management.service.StudentService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
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
        return studentRepository.findById(Integer.valueOf(id))
                .map(studentMapper::toStudentDto)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
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
        Student existingStudent = studentRepository.findById(Integer.valueOf(id))
                .orElseThrow(() -> new ResourceNotFoundException("Cannot update. Student not found with id: " + id));
        studentMapper.updateStudentFromDto(studentDTO, existingStudent);
        Student updatedStudent = studentRepository.save(existingStudent);
        return studentMapper.toStudentDto(updatedStudent);
    }

    @Override
    @Transactional
    public void deleteStudent(String id) {
        if (!studentRepository.existsById(Integer.valueOf(id))) {
            throw new ResourceNotFoundException("Cannot delete. Student not found with id: " + id);
        }
        studentRepository.deleteById(Integer.valueOf(id));
    }
}
