package com.example.educontrol.Controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.educontrol.repositories.StudentRepository;
import com.example.educontrol.student.Student;

@RestController
@RequestMapping("/students")
public class StudentController {
    private final StudentRepository repository;

    public StudentController(StudentRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Student> getAllStudents() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Student getStudent(@PathVariable UUID id) {
        return repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluno nao encontrado"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Student createStudent(@RequestBody Student student) {
        validate(student);
        student.setId(null);
        return repository.saveAndFlush(student);
    }

    @PutMapping("/{id}")
    public Student updateStudent(@PathVariable UUID id, @RequestBody Student student) {
        Student existing = getStudent(id);
        validate(student);
        existing.setName(student.getName());
        existing.setRegistration(student.getRegistration());
        existing.setClassroom(student.getClassroom());
        existing.setTurn(student.getTurn());
        return repository.saveAndFlush(existing);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStudent(@PathVariable UUID id) {
        repository.delete(getStudent(id));
    }

    private void validate(Student student) {
        if (student.getName() != null) {
            student.setName(student.getName().trim());
        }
        if (student.getRegistration() != null) {
            student.setRegistration(student.getRegistration().trim());
        }
        if (student.getName() == null || student.getName().isBlank()
                || student.getRegistration() == null || student.getRegistration().isBlank()
                || student.getTurn() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Nome, matricula e turno sao obrigatorios");
        }
        validateLength(student.getName(), "Nome");
        validateLength(student.getRegistration(), "Matricula");
        validateLength(student.getClassroom(), "Turma");
    }

    private void validateLength(String value, String field) {
        if (value != null && value.length() > 255) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    field + " deve ter no maximo 255 caracteres");
        }
    }
}
