package com.Test.student.controller;

import com.Test.student.repo.StudentRepository;
import com.Test.student.entity.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Arrays;

@RestController
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentRepository studentRepository;

    @PostMapping
    public ResponseEntity<?> createStudent(@Valid @RequestBody Student student, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }

        // Validations
        if (student.getDob().isAfter(LocalDate.now().minusYears(15)) ||
                student.getDob().isBefore(LocalDate.now().minusYears(20))) {
            return ResponseEntity.badRequest().body("DOB should be between 15 and 20 years ago");
        }

        if (student.getMarks1() != null && (student.getMarks1() < 0 || student.getMarks1() > 100)) {
            return ResponseEntity.badRequest().body("Marks 1 should be between 0 and 100");
        }

        if (student.getMarks2() != null && (student.getMarks2() < 0 || student.getMarks2() > 100)) {
            return ResponseEntity.badRequest().body("Marks 2 should be between 0 and 100");
        }

        if (student.getMarks3() != null && (student.getMarks3() < 0 || student.getMarks3() > 100)) {
            return ResponseEntity.badRequest().body("Marks 3 should be between 0 and 100");
        }
        if (!Arrays.asList("M", "F").contains(student.getGender())) {
            return ResponseEntity.badRequest().body("Valid values for gender are M or F");
        }

        if (!Arrays.asList("A", "B", "C").contains(student.getSection())) {
            return ResponseEntity.badRequest().body("Valid values for section are A, B and C");
        }



        // Total, Average and Result
        int total = 0;
        int numSubjects = 0;
        if (student.getMarks1() != null && student.getMarks1() >= 35) {
            total += student.getMarks1();
            numSubjects++;
        }
        if (student.getMarks2() != null && student.getMarks2() >= 35) {
            total += student.getMarks2();
            numSubjects++;
        }
        if (student.getMarks3() != null && student.getMarks3() >= 35) {
            total += student.getMarks3();
            numSubjects++;
        }
        student.setTotal(total);
        student.setAverage(numSubjects > 0 ? (float) total / numSubjects : 0);
        student.setResult(numSubjects == 3 ? "PASS" : "FAIL");

        studentRepository.save(student);

        return ResponseEntity.ok(student);

    }
    
    // Endpoint to update marks of a student by ID
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateStudentMarks(@PathVariable Long id, @RequestBody Student student) {

        // Validate the student object
        if (student.getMarks1() == null || student.getMarks2() == null || student.getMarks3() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Marks 1, 2 and 3 are mandatory.");
        }
        if (student.getMarks1() < 0 || student.getMarks1() > 100) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Marks 1 should be between 0 and 100.");
        }
        if (student.getMarks2() < 0 || student.getMarks2() > 100) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Marks 2 should be between 0 and 100.");
        }
        if (student.getMarks3() < 0 || student.getMarks3() > 100) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Marks 3 should be between 0 and 100.");
        }

        // Find the student object by ID
        Optional<Student> optionalStudent = studentRepository.findById(id);
        if (!optionalStudent.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student not found.");
        }
        Student foundStudent = optionalStudent.get();

        // Update the marks and calculated fields
        foundStudent.setMarks1(student.getMarks1());
        foundStudent.setMarks2(student.getMarks2());
        foundStudent.setMarks3(student.getMarks3());
        int totalMarks = foundStudent.getMarks1() + foundStudent.getMarks2() + foundStudent.getMarks3();
        Float averageMarks =(float) (totalMarks / 3.0);
        String result = "PASS";
        if (foundStudent.getMarks1() < 35 || foundStudent.getMarks2() < 35 || foundStudent.getMarks3() < 35) {
            result = "FAIL";
        }
        foundStudent.setTotal(totalMarks);
        foundStudent.setAverage(averageMarks);
        foundStudent.setResult(result);

        // Save the updated student object
        Student updatedStudent = studentRepository.save(foundStudent);

        // Return the updated student object
        return ResponseEntity.status(HttpStatus.OK).body(updatedStudent);
    }
}
