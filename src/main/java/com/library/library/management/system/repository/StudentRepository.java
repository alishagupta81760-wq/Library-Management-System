package com.library.library.management.system.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.library.library.management.system.entity.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    // Search Student by Name
    List<Student> findByNameContainingIgnoreCase(String name);

    // Search Student by Roll Number
    List<Student> findByRollNumberContainingIgnoreCase(String rollNumber);

    // Search Student by Email
    List<Student> findByEmailContainingIgnoreCase(String email);

}