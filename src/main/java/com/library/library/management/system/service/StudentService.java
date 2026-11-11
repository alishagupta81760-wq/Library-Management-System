package com.library.library.management.system.service;

import java.util.List;

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

    // Search Student by Name
    public List<Student> searchStudents(String keyword) {

        if (keyword == null || keyword.trim().isEmpty()) {
            return studentRepository.findAll();
        }

        return studentRepository.findByNameContainingIgnoreCase(keyword);
    }

    // Count Total Students
    public long countStudents() {
        return studentRepository.count();
    }
}