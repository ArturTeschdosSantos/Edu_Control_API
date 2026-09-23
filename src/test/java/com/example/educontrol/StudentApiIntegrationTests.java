package com.example.educontrol;

import java.util.UUID;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.educontrol.repositories.StudentRepository;
import com.example.educontrol.student.Period;
import com.example.educontrol.student.Student;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class StudentApiIntegrationTests {
    private static final String VALID_STUDENT = """
            {"name":"Ana","registration":"001","classroom":"1A","turn":"MORNING"}
            """;

    @Autowired MockMvc mvc;
    @Autowired StudentRepository students;

    @BeforeEach
    void resetDatabase() {
        students.deleteAll();
    }

    @Test
    void putUpdatesEveryFieldAndPreservesThePathId() throws Exception {
        UUID id = createStudent(VALID_STUDENT);
        String replacement = """
                {"id":"%s","name":"Beatriz","registration":"002","classroom":"2B","turn":"AFTERNOON"}
                """.formatted(UUID.randomUUID());

        mvc.perform(put("/students/{id}", id).contentType(MediaType.APPLICATION_JSON).content(replacement))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Beatriz"))
                .andExpect(jsonPath("$.registration").value("002"))
                .andExpect(jsonPath("$.classroom").value("2B"))
                .andExpect(jsonPath("$.turn").value("AFTERNOON"));

        Student saved = students.findById(id).orElseThrow();
        assertThat(saved.getName()).isEqualTo("Beatriz");
        assertThat(saved.getRegistration()).isEqualTo("002");
        assertThat(saved.getClassroom()).isEqualTo("2B");
        assertThat(saved.getTurn()).isEqualTo(Period.AFTERNOON);
        assertThat(students.count()).isEqualTo(1);
    }

    @Test
    void putWithoutOptionalClassroomRemovesItsPreviousValue() throws Exception {
        UUID id = createStudent(VALID_STUDENT);

        mvc.perform(put("/students/{id}", id).contentType(MediaType.APPLICATION_JSON).content("""
                {"name":"Ana","registration":"001","turn":"AFTERNOON"}
                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.classroom").doesNotExist());

        assertThat(students.findById(id).orElseThrow().getClassroom()).isNull();
    }

    @ParameterizedTest
    @ValueSource(strings = {"GET", "PUT", "DELETE"})
    void nonexistentStudentReturnsStructuredNotFound(String method) throws Exception {
        mvc.perform(request(HttpMethod.valueOf(method), "/students/{id}", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON).content(VALID_STUDENT))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value(containsString("Aluno")));
        assertThat(students.count()).isZero();
    }

    @Test
    void postWithAnExistingIdCreatesANewStudentWithoutOverwritingTheOriginal() throws Exception {
        UUID originalId = createStudent(VALID_STUDENT);
        mvc.perform(post("/students").contentType(MediaType.APPLICATION_JSON).content("""
                {"id":"%s","name":"Beatriz","registration":"002","turn":"AFTERNOON"}
                """.formatted(originalId)))
                .andExpect(status().isCreated());

        assertThat(students.count()).isEqualTo(2);
        Student original = students.findById(originalId).orElseThrow();
        assertThat(original.getName()).isEqualTo("Ana");
        assertThat(original.getRegistration()).isEqualTo("001");
        assertThat(original.getClassroom()).isEqualTo("1A");
        assertThat(original.getTurn()).isEqualTo(Period.MORNING);
        assertThat(students.findAll()).anySatisfy(student -> {
            assertThat(student.getId()).isNotEqualTo(originalId);
            assertThat(student.getRegistration()).isEqualTo("002");
        });
    }

    @Test
    void duplicateRegistrationOnUpdateDoesNotCommitAnyChangedField() throws Exception {
        UUID firstId = createStudent(VALID_STUDENT);
        mvc.perform(post("/students").contentType(MediaType.APPLICATION_JSON).content("""
                {"name":"Beatriz","registration":"002","classroom":"2B","turn":"AFTERNOON"}
                """))
                .andExpect(status().isCreated());

        mvc.perform(put("/students/{id}", firstId).contentType(MediaType.APPLICATION_JSON).content("""
                {"name":"Alterado","registration":" 002 ","classroom":"3C","turn":"AFTERNOON"}
                """))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.detail").value(containsString("matricula")));

        Student original = students.findById(firstId).orElseThrow();
        assertThat(original.getName()).isEqualTo("Ana");
        assertThat(original.getRegistration()).isEqualTo("001");
        assertThat(original.getClassroom()).isEqualTo("1A");
        assertThat(original.getTurn()).isEqualTo(Period.MORNING);
        assertThat(students.count()).isEqualTo(2);
    }

    @Test
    void trimsRequiredTextAndDetectsDuplicateRegistrationAfterTrimming() throws Exception {
        UUID id = createStudent("""
                {"name":"  Ana  ","registration":" 001 ","turn":"MORNING"}
                """);
        Student saved = students.findById(id).orElseThrow();
        assertThat(saved.getName()).isEqualTo("Ana");
        assertThat(saved.getRegistration()).isEqualTo("001");

        mvc.perform(post("/students").contentType(MediaType.APPLICATION_JSON).content(VALID_STUDENT))
                .andExpect(status().isConflict());
        assertThat(students.count()).isEqualTo(1);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{}",
            "{\"registration\":\"001\",\"turn\":\"MORNING\"}",
            "{\"name\":\"   \",\"registration\":\"001\",\"turn\":\"MORNING\"}",
            "{\"name\":\"Ana\",\"turn\":\"MORNING\"}",
            "{\"name\":\"Ana\",\"registration\":\"   \",\"turn\":\"MORNING\"}",
            "{\"name\":\"Ana\",\"registration\":\"001\"}"
    })
    void missingOrBlankRequiredFieldsReturnUsefulBadRequest(String body) throws Exception {
        mvc.perform(post("/students").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value(containsString("obrigatorios")));
        assertThat(students.count()).isZero();
    }

    @Test
    void invalidUpdateLeavesPreviouslySavedStudentUnchanged() throws Exception {
        UUID id = createStudent(VALID_STUDENT);
        mvc.perform(put("/students/{id}", id).contentType(MediaType.APPLICATION_JSON).content("""
                {"name":"   ","registration":"002","turn":"AFTERNOON"}
                """))
                .andExpect(status().isBadRequest());
        Student saved = students.findById(id).orElseThrow();
        assertThat(saved.getName()).isEqualTo("Ana");
        assertThat(saved.getRegistration()).isEqualTo("001");
        assertThat(saved.getClassroom()).isEqualTo("1A");
        assertThat(saved.getTurn()).isEqualTo(Period.MORNING);
    }

    @ParameterizedTest
    @MethodSource("oversizedTextRequests")
    void textLongerThanDatabaseColumnsReturnsBadRequest(String method, String field) throws Exception {
        UUID id = "PUT".equals(method) ? createStudent(VALID_STUDENT) : null;
        String body = """
                {"name":"%s","registration":"%s","classroom":"%s","turn":"MORNING"}
                """.formatted(
                "name".equals(field) ? "A".repeat(256) : "Ana",
                "registration".equals(field) ? "1".repeat(256) : "001",
                "classroom".equals(field) ? "B".repeat(256) : "1A");

        mvc.perform(request(HttpMethod.valueOf(method), id == null ? "/students" : "/students/" + id)
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(400));

        if (id == null) {
            assertThat(students.count()).isZero();
        } else {
            Student saved = students.findById(id).orElseThrow();
            assertThat(saved.getName()).isEqualTo("Ana");
            assertThat(saved.getRegistration()).isEqualTo("001");
            assertThat(saved.getClassroom()).isEqualTo("1A");
        }
    }

    static Stream<Arguments> oversizedTextRequests() {
        return Stream.of("POST", "PUT").flatMap(method -> Stream.of("name", "registration", "classroom")
                .map(field -> Arguments.of(method, field)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"{", "null", "{\"name\":\"Ana\",\"registration\":\"001\",\"turn\":\"INVALID\"}"})
    void unreadableJsonReturnsStructuredBadRequest(String body) throws Exception {
        mvc.perform(post("/students").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").isNotEmpty());
        assertThat(students.count()).isZero();
    }

    @ParameterizedTest
    @ValueSource(strings = {"GET", "PUT", "DELETE"})
    void invalidUuidReturnsStructuredBadRequest(String method) throws Exception {
        mvc.perform(request(HttpMethod.valueOf(method), "/students/not-a-uuid")
                        .contentType(MediaType.APPLICATION_JSON).content(VALID_STUDENT))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").isNotEmpty());
    }

    private UUID createStudent(String body) throws Exception {
        mvc.perform(post("/students").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());
        return students.findAll().get(0).getId();
    }
}
