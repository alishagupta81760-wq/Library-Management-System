package com.library.library.management.system.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.library.library.management.system.entity.Student;
import com.library.library.management.system.repository.StudentRepository;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    // Constructor Injection
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    // Add Student
    public Student saveStudent(Student student) {
        return studentRepository.save(student);
    }

    // Get All Students
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    // Get Students with Pagination
    public Page<Student> getStudentsPage(int page, int size) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("id").descending()
        );

        return studentRepository.findAll(pageable);
    }

    // Get Student By ID
    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Student not found with ID: " + id));
    }

    // Update Student
    public Student updateStudent(Long id, Student updatedStudent) {

        Student existingStudent = getStudentById(id);

        existingStudent.setName(updatedStudent.getName());
        existingStudent.setRollNumber(updatedStudent.getRollNumber());
        existingStudent.setEmail(updatedStudent.getEmail());
        existingStudent.setPhone(updatedStudent.getPhone());
        existingStudent.setCourse(updatedStudent.getCourse());

        return studentRepository.save(existingStudent);
    }

    // Delete Student
    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }

    // Search Student by Name, Roll Number or Email
    public List<Student> searchStudents(String keyword) {

        if (keyword == null || keyword.trim().isEmpty()) {
            return studentRepository.findAll();
        }

        String searchKeyword = keyword.trim();

        return studentRepository
                .findByNameContainingIgnoreCaseOrRollNumberContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        searchKeyword,
                        searchKeyword,
                        searchKeyword
                );
    }

    // Search Students with Pagination
    public Page<Student> searchStudentsPage(String keyword, int page, int size) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("id").descending()
        );

        if (keyword == null || keyword.trim().isEmpty()) {
            return studentRepository.findAll(pageable);
        }

        String searchKeyword = keyword.trim();

        return studentRepository
                .findByNameContainingIgnoreCaseOrRollNumberContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        searchKeyword,
                        searchKeyword,
                        searchKeyword,
                        pageable
                );
    }

    // Count Total Students
    public long countStudents() {
        return studentRepository.count();
    }

    // Check Duplicate Roll Number while adding
    public boolean isRollNumberExists(String rollNumber) {
        return studentRepository.existsByRollNumber(rollNumber);
    }

    // Check Duplicate Email while adding
    public boolean isEmailExists(String email) {
        return studentRepository.existsByEmail(email);
    }

    // Check Duplicate Roll Number while editing
    public boolean isRollNumberExistsForOtherStudent(String rollNumber, Long id) {
        return studentRepository.existsByRollNumberAndIdNot(rollNumber, id);
    }

    // Check Duplicate Email while editing
    public boolean isEmailExistsForOtherStudent(String email, Long id) {
        return studentRepository.existsByEmailAndIdNot(email, id);
    }
}