package com.library.library.management.system.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.library.library.management.system.entity.Student;
import com.library.library.management.system.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    @Test
    void updateStudentChangesProfileFieldsAndSavesExistingRecord() {
        Student existing = new Student();
        Student changes = new Student();
        changes.setName("Asha Sharma");
        changes.setRollNumber("BCA-22");
        changes.setEmail("asha@example.com");
        changes.setPhone("9876543210");
        changes.setCourse("BCA");
        when(studentRepository.findById(3L)).thenReturn(Optional.of(existing));
        when(studentRepository.save(existing)).thenReturn(existing);

        Student result = studentService.updateStudent(3L, changes);

        assertSame(existing, result);
        assertEquals("Asha Sharma", existing.getName());
        assertEquals("BCA-22", existing.getRollNumber());
        assertEquals("asha@example.com", existing.getEmail());
        assertEquals("9876543210", existing.getPhone());
        assertEquals("BCA", existing.getCourse());
        verify(studentRepository).save(existing);
    }

    @Test
    void updateStudentReportsMissingRecord() {
        when(studentRepository.findById(9L)).thenReturn(Optional.empty());

        RuntimeException error = assertThrows(RuntimeException.class,
                () -> studentService.updateStudent(9L, new Student()));

        assertEquals("Student not found with ID: 9", error.getMessage());
    }

    @Test
    void searchStudentsPageTrimsKeywordAndSearchesAllThreeFields() {
        when(studentRepository
                .findByNameContainingIgnoreCaseOrRollNumberContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(java.util.List.of()));

        studentService.searchStudentsPage("  bca-22 ", 1, 10);

        verify(studentRepository)
                .findByNameContainingIgnoreCaseOrRollNumberContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        "bca-22", "bca-22", "bca-22", PageRequest.of(1, 10,
                                Sort.by("id").descending()));
    }

    @Test
    void blankSearchUsesPaginatedFindAll() {
        when(studentRepository.findAll(any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(new PageImpl<>(java.util.List.of()));

        studentService.searchStudentsPage("  ", 0, 20);

        verify(studentRepository).findAll(PageRequest.of(0, 20,
                Sort.by("id").descending()));
    }
}
