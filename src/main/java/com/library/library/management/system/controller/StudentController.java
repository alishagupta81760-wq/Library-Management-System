package com.library.library.management.system.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.library.library.management.system.entity.Student;
import com.library.library.management.system.service.StudentService;

@Controller
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;

    // Constructor Injection
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // Display All Students
    @GetMapping
    public String getAllStudents(Model model) {

        List<Student> students = studentService.getAllStudents();

        model.addAttribute("students", students);

        return "students";
    }

    // Show Add Student Form
    @GetMapping("/add")
    public String showAddStudentForm(Model model) {

        model.addAttribute("student", new Student());

        return "add-student";
    }

    // Save Student
    @PostMapping("/save")
    public String saveStudent(@ModelAttribute Student student) {

        studentService.saveStudent(student);

        return "redirect:/students";
    }

    // Show Edit Student Form
    @GetMapping("/edit/{id}")
    public String showEditStudentForm(
            @PathVariable Long id,
            Model model) {

        Student student = studentService.getStudentById(id);

        model.addAttribute("student", student);

        return "edit-student";
    }

    // Update Student
    @PostMapping("/update/{id}")
    public String updateStudent(
            @PathVariable Long id,
            @ModelAttribute Student student) {

        studentService.updateStudent(id, student);

        return "redirect:/students";
    }

    // Delete Student
    @GetMapping("/delete/{id}")
    public String deleteStudent(@PathVariable Long id) {

        studentService.deleteStudent(id);

        return "redirect:/students";
    }

    // Search Student
    @GetMapping("/search")
    public String searchStudents(
            @RequestParam(required = false) String keyword,
            Model model) {

        List<Student> students = studentService.searchStudents(keyword);

        model.addAttribute("students", students);
        model.addAttribute("keyword", keyword);

        return "students";
    }
}