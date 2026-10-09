package com.example.educontrol.Controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.educontrol.dto.ApiResponseDTO;
import com.example.educontrol.dto.StudentResponseDTO;
import com.example.educontrol.services.StudentService;
import com.example.educontrol.student.Student;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponseDTO<StudentResponseDTO>> createStudent(@RequestBody Student student) {
        StudentResponseDTO studentDTO = studentService.createStudent(student);
        ApiResponseDTO<StudentResponseDTO> response = ApiResponseDTO.<StudentResponseDTO>builder()
                .success(true)
                .message("Estudante criado com sucesso")
                .data(studentDTO)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<StudentResponseDTO>>> getAllStudents() {
        List<StudentResponseDTO> students = studentService.getAllStudents();
        ApiResponseDTO<List<StudentResponseDTO>> response = ApiResponseDTO.<List<StudentResponseDTO>>builder()
                .success(true)
                .message("Estudantes recuperados com sucesso")
                .data(students)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<StudentResponseDTO>> getStudentById(@PathVariable UUID id) {
        StudentResponseDTO student = studentService.getStudentById(id);
        ApiResponseDTO<StudentResponseDTO> response = ApiResponseDTO.<StudentResponseDTO>builder()
                .success(true)
                .message("Estudante recuperado com sucesso")
                .data(student)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/registration/{registration}")
    public ResponseEntity<ApiResponseDTO<StudentResponseDTO>> getStudentByRegistration(
            @PathVariable String registration) {
        StudentResponseDTO student = studentService.getStudentByRegistration(registration);
        ApiResponseDTO<StudentResponseDTO> response = ApiResponseDTO.<StudentResponseDTO>builder()
                .success(true)
                .message("Estudante recuperado com sucesso")
                .data(student)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<StudentResponseDTO>> updateStudent(@PathVariable UUID id,
            @RequestBody Student studentDetails) {
        StudentResponseDTO updatedStudent = studentService.updateStudent(id, studentDetails);
        ApiResponseDTO<StudentResponseDTO> response = ApiResponseDTO.<StudentResponseDTO>builder()
                .success(true)
                .message("Estudante atualizado com sucesso")
                .data(updatedStudent)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteStudent(@PathVariable UUID id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }
}
