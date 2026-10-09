package com.example.educontrol;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.educontrol.repositories.CafeteriaRepository;
import com.example.educontrol.repositories.StudentRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ApiIntegrationTests {
    @Autowired MockMvc mvc;
    @Autowired StudentRepository students;
    @Autowired CafeteriaRepository cafeterias;

    @BeforeEach
    void resetDatabase() {
        students.deleteAll();
        cafeterias.deleteAll();
    }

    @Test
    void healthIsAvailable() throws Exception {
        mvc.perform(get("/health")).andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void studentLifecycle() throws Exception {
        String body = """
                {"name":"Ana","registration":"001","classroom":"1A","turn":"MORNING"}
                """;
        mvc.perform(post("/students").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.id").isNotEmpty());
        UUID id = students.findAll().get(0).getId();
        mvc.perform(get("/students")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].registration").value("001"));
        mvc.perform(get("/students/{id}", id)).andExpect(status().isOk())
                .andExpect(jsonPath("$.turn").value("MORNING"));
        mvc.perform(put("/students/{id}", id).contentType(MediaType.APPLICATION_JSON)
                .content(body.replace("MORNING", "AFTERNOON")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.turn").value("AFTERNOON"));
        mvc.perform(delete("/students/{id}", id)).andExpect(status().isNoContent());
        mvc.perform(get("/students/{id}", id)).andExpect(status().isNotFound());
    }

    @Test
    void invalidAndDuplicateStudentsAreRejected() throws Exception {
        mvc.perform(post("/students").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
        String body = """
                {"name":"Ana","registration":"001","turn":"MORNING"}
                """;
        mvc.perform(post("/students").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());
        mvc.perform(post("/students").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict());
        mvc.perform(post("/students").contentType(MediaType.APPLICATION_JSON)
                .content(body.replace("MORNING", "INVALID"))).andExpect(status().isBadRequest());
        mvc.perform(get("/students/not-a-uuid")).andExpect(status().isBadRequest());
    }

    @Test
    void cafeteriaLifecycle() throws Exception {
        String body = """
                {"name":"Central","location":"Bloco A","openingTime":"07:00","closingTime":"18:00","active":true}
                """;
        mvc.perform(post("/cafeteria").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.id").isNotEmpty());
        UUID id = cafeterias.findAll().get(0).getId();
        mvc.perform(get("/cafeteria")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Central"));
        mvc.perform(get("/cafeteria/{id}", id)).andExpect(status().isOk());
        mvc.perform(put("/cafeteria/{id}", id).contentType(MediaType.APPLICATION_JSON)
                .content(body.replace("true", "false"))).andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));
        mvc.perform(delete("/cafeteria/{id}", id)).andExpect(status().isNoContent());
        mvc.perform(get("/cafeteria/{id}", id)).andExpect(status().isNotFound());
    }

    @Test
    void invalidCafeteriasAreRejected() throws Exception {
        mvc.perform(post("/cafeteria").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/cafeteria").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Central\",\"openingTime\":\"25:00\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void postCannotOverwriteAnExistingStudent() throws Exception {
        String body = """
                {"name":"Ana","registration":"001","turn":"MORNING"}
                """;
        mvc.perform(post("/students").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());
        UUID id = students.findAll().get(0).getId();
        mvc.perform(post("/students").contentType(MediaType.APPLICATION_JSON)
                .content(body.replace("{", "{\"id\":\"" + id + "\",").replace("001", "002")))
                .andExpect(status().isCreated());
        mvc.perform(get("/students/{id}", id)).andExpect(status().isOk())
                .andExpect(jsonPath("$.registration").value("001"));
    }
}
