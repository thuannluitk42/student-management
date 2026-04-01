package com.thuannluit.student_management;

import org.springframework.boot.SpringApplication;

public class TestStudentManagementApplication {

	public static void main(String[] args) {
		SpringApplication.from(StudentManagementApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
