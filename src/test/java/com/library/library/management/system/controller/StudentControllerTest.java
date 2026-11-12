package com.library.library.management.system.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import com.library.library.management.system.entity.Student;
import com.library.library.management.system.service.StudentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.validation.BeanPropertyBindingResult;

@ExtendWith(MockitoExtension.class)
class StudentControllerTest {

    @Mock
    private StudentService studentService;

    @InjectMocks
    private StudentController studentController;

    @Test
    void searchClampsPagingAndPreservesKeywordInModel() {
        when(studentService.searchStudentsPage("Riya", 0, 1))
                .thenReturn(new PageImpl<>(List.of()));
        ExtendedModelMap model = new ExtendedModelMap();

        String view = studentController.searchStudents("Riya", -4, 0, model);

        assertEquals("students", view);
        assertEquals("Riya", model.get("keyword"));
        assertEquals(1, model.get("pageSize"));
        verify(studentService).searchStudentsPage("Riya", 0, 1);
    }

    @Test
    void saveStudentRejectsDuplicateRollNumberAndEmail() {
        Student student = new Student();
        student.setRollNumber("BCA-12");
        student.setEmail("same@example.com");
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(student, "student");
        when(studentService.isRollNumberExists("BCA-12")).thenReturn(true);
        when(studentService.isEmailExists("same@example.com")).thenReturn(true);

        String view = studentController.saveStudent(student, errors);

        assertEquals("add-student", view);
        assertEquals(1, errors.getFieldErrorCount("rollNumber"));
        assertEquals(1, errors.getFieldErrorCount("email"));
        verify(studentService, never()).saveStudent(student);
    }

    @Test
    void updateStudentIgnoresBlankOptionalDuplicateChecks() {
        Student student = new Student();
        student.setRollNumber(" ");
        student.setEmail("");
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(student, "student");

        String view = studentController.updateStudent(6L, student, errors);

        assertEquals("redirect:/students", view);
        assertEquals(6L, student.getId());
        verify(studentService, never()).isRollNumberExistsForOtherStudent(" ", 6L);
        verify(studentService, never()).isEmailExistsForOtherStudent("", 6L);
        verify(studentService).updateStudent(6L, student);
    }
}
