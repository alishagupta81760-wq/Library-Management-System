package com.library.library.management.system.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.library.library.management.system.entity.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    // Search by Name
    List<Student> findByNameContainingIgnoreCase(String name);

    // Search by Roll Number
    List<Student> findByRollNumberContainingIgnoreCase(String rollNumber);

    // Search by Email
    List<Student> findByEmailContainingIgnoreCase(String email);

    // Search by Name, Roll Number or Email
    List<Student> findByNameContainingIgnoreCaseOrRollNumberContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String name, String rollNumber, String email);

    // Check Duplicate Roll Number
    boolean existsByRollNumber(String rollNumber);

    // Check Duplicate Email
    boolean existsByEmail(String email);

    // Check Duplicate Roll Number while updating
    boolean existsByRollNumberAndIdNot(String rollNumber, Long id);

    // Check Duplicate Email while updating
    boolean existsByEmailAndIdNot(String email, Long id);
 // Search Students with Pagination
    Page<Student> findByNameContainingIgnoreCaseOrRollNumberContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String name, String rollNumber, String email, Pageable pageable);
}