package com.example.educontrol.services;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.educontrol.dto.StudentResponseDTO;
import com.example.educontrol.exceptions.ResourceNotFoundException;
import com.example.educontrol.repositories.StudentRepository;
import com.example.educontrol.student.Student;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public StudentResponseDTO createStudent(Student student) {
        Student savedStudent = studentRepository.save(student);
        return convertToDTO(savedStudent);
    }

    public List<StudentResponseDTO> getAllStudents() {
        return studentRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public StudentResponseDTO getStudentById(UUID id) {
        Student student = findStudentById(id);
        return convertToDTO(student);
    }

    public StudentResponseDTO getStudentByRegistration(String registration) {
        Student student = studentRepository.findByRegistration(registration)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Estudante não encontrado com matrícula: " + registration));
        return convertToDTO(student);
    }

    public StudentResponseDTO updateStudent(UUID id, Student studentDetails) {
        Student student = findStudentById(id);
        
        if (studentDetails.getName() != null) {
            student.setName(studentDetails.getName());
        }
        if (studentDetails.getClassroom() != null) {
            student.setClassroom(studentDetails.getClassroom());
        }
        if (studentDetails.getTurn() != null) {
            student.setTurn(studentDetails.getTurn());
        }

        Student updatedStudent = studentRepository.save(student);
        return convertToDTO(updatedStudent);
    }

    public void deleteStudent(UUID id) {
        Student student = findStudentById(id);
        studentRepository.delete(student);
    }

    protected Student findStudentById(UUID id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estudante não encontrado com ID: " + id));
    }

    protected StudentResponseDTO convertToDTO(Student student) {
        return StudentResponseDTO.builder()
                .id(student.getId())
                .name(student.getName())
                .registration(student.getRegistration())
                .classroom(student.getClassroom())
                .turn(student.getTurn())
                .build();
    }
}
