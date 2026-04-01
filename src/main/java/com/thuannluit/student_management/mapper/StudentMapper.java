package com.thuannluit.student_management.mapper;

import com.thuannluit.student_management.dto.StudentDTO;
import com.thuannluit.student_management.entity.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface StudentMapper {

    StudentDTO toStudentDto(Student student);

    @Mapping(target = "studentCode", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Student toEntity(StudentDTO studentDto);

    void updateStudentFromDto(StudentDTO dto, @MappingTarget Student student);
}
