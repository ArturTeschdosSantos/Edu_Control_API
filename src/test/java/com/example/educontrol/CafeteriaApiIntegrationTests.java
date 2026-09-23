package com.example.educontrol;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.educontrol.cafeteria.Cafeteria;
import com.example.educontrol.repositories.CafeteriaRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CafeteriaApiIntegrationTests {
    private static final String VALID_CAFETERIA = """
            {"name":"Central","location":"Bloco A","openingTime":"07:00","closingTime":"18:00","active":true}
            """;

    @Autowired
    MockMvc mvc;

    @Autowired
    CafeteriaRepository cafeterias;

    @BeforeEach
    void resetDatabase() {
        cafeterias.deleteAll();
    }

    @Test
    void listsAnEmptyDatabase() throws Exception {
        mvc.perform(get("/cafeteria"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void createsReadsUpdatesEveryFieldAndDeletesPersistedCafeteria() throws Exception {
        UUID id = createCafeteria();

        mvc.perform(get("/cafeteria"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(id.toString()));
        mvc.perform(get("/cafeteria/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Central"))
                .andExpect(jsonPath("$.location").value("Bloco A"))
                .andExpect(jsonPath("$.openingTime").value("07:00"))
                .andExpect(jsonPath("$.closingTime").value("18:00"))
                .andExpect(jsonPath("$.active").value(true));

        mvc.perform(put("/cafeteria/{id}", id).contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"id":"%s","name":"  Noturno  ","location":"Bloco B",
                         "openingTime":"22:00:00","closingTime":"02:30:00","active":false}
                        """.formatted(UUID.randomUUID())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Noturno"));

        Cafeteria updated = cafeterias.findById(id).orElseThrow();
        assertThat(updated.getName()).isEqualTo("Noturno");
        assertThat(updated.getLocation()).isEqualTo("Bloco B");
        assertThat(updated.getOpeningTime()).isEqualTo("22:00:00");
        assertThat(updated.getClosingTime()).isEqualTo("02:30:00");
        assertThat(updated.isActive()).isFalse();
        assertThat(cafeterias.count()).isEqualTo(1);

        mvc.perform(delete("/cafeteria/{id}", id)).andExpect(status().isNoContent());
        assertThat(cafeterias.existsById(id)).isFalse();
        mvc.perform(get("/cafeteria/{id}", id)).andExpect(status().isNotFound());
        mvc.perform(delete("/cafeteria/{id}", id)).andExpect(status().isNotFound());
    }

    @Test
    void allowsOptionalFieldsAndNormalizesName() throws Exception {
        mvc.perform(post("/cafeteria").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"  Central  \"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Central"))
                .andExpect(jsonPath("$.active").value(false));

        Cafeteria saved = cafeterias.findAll().get(0);
        assertThat(saved.getName()).isEqualTo("Central");
        assertThat(saved.getLocation()).isNull();
        assertThat(saved.getOpeningTime()).isNull();
        assertThat(saved.getClosingTime()).isNull();
        assertThat(saved.isActive()).isFalse();
    }

    @Test
    void updateCanClearOptionalFields() throws Exception {
        UUID id = createCafeteria();
        mvc.perform(put("/cafeteria/{id}", id).contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Central\",\"location\":null,\"openingTime\":null,\"closingTime\":null}"))
                .andExpect(status().isOk());

        Cafeteria updated = cafeterias.findById(id).orElseThrow();
        assertThat(updated.getLocation()).isNull();
        assertThat(updated.getOpeningTime()).isNull();
        assertThat(updated.getClosingTime()).isNull();
        assertThat(updated.isActive()).isFalse();
    }

    @Test
    void postWithAnExistingIdCreatesANewRecordWithoutOverwriting() throws Exception {
        UUID existingId = createCafeteria();
        mvc.perform(post("/cafeteria").contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":\"%s\",\"name\":\"Anexo\"}".formatted(existingId)))
                .andExpect(status().isCreated());

        assertThat(cafeterias.count()).isEqualTo(2);
        assertThat(cafeterias.findById(existingId).orElseThrow().getName()).isEqualTo("Central");
        Cafeteria created = cafeterias.findAll().stream()
                .filter(cafeteria -> cafeteria.getName().equals("Anexo"))
                .findFirst().orElseThrow();
        assertThat(created.getId()).isNotEqualTo(existingId);
    }

    @Test
    void missingRecordsReturnNotFoundWithoutCreatingRecords() throws Exception {
        UUID missingId = UUID.randomUUID();
        mvc.perform(get("/cafeteria/{id}", missingId)).andExpect(status().isNotFound());
        mvc.perform(put("/cafeteria/{id}", missingId).contentType(MediaType.APPLICATION_JSON)
                .content(VALID_CAFETERIA)).andExpect(status().isNotFound());
        mvc.perform(delete("/cafeteria/{id}", missingId)).andExpect(status().isNotFound());
        assertThat(cafeterias.count()).isZero();
    }

    @Test
    void malformedIdsReturnBadRequest() throws Exception {
        mvc.perform(get("/cafeteria/not-a-uuid")).andExpect(status().isBadRequest());
        mvc.perform(put("/cafeteria/not-a-uuid").contentType(MediaType.APPLICATION_JSON)
                .content(VALID_CAFETERIA)).andExpect(status().isBadRequest());
        mvc.perform(delete("/cafeteria/not-a-uuid")).andExpect(status().isBadRequest());
        assertThat(cafeterias.count()).isZero();
    }

    @ParameterizedTest
    @ValueSource(strings = {"{}", "{\"name\":null}", "{\"name\":\"\"}", "{\"name\":\"   \"}"})
    void requiresANonBlankName(String body) throws Exception {
        mvc.perform(post("/cafeteria").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
        assertThat(cafeterias.count()).isZero();

        UUID id = createCafeteria();
        mvc.perform(put("/cafeteria/{id}", id).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
        assertThat(cafeterias.findById(id).orElseThrow().getName()).isEqualTo("Central");
    }

    @ParameterizedTest
    @ValueSource(strings = {"name", "location"})
    void rejectsOversizedTextBeforePersistingAndPreservesExistingRecord(String field) throws Exception {
        String body = field.equals("name")
                ? "{\"name\":\"%s\"}".formatted("a".repeat(256))
                : "{\"name\":\"Changed\",\"location\":\"%s\"}".formatted("a".repeat(256));

        mvc.perform(post("/cafeteria").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
        assertThat(cafeterias.count()).isZero();

        UUID id = createCafeteria();
        mvc.perform(put("/cafeteria/{id}", id).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
        Cafeteria unchanged = cafeterias.findById(id).orElseThrow();
        assertThat(unchanged.getName()).isEqualTo("Central");
        assertThat(unchanged.getLocation()).isEqualTo("Bloco A");
    }

    @Test
    void allowsTextAtDatabaseLengthBoundary() throws Exception {
        String text = "a".repeat(255);
        mvc.perform(post("/cafeteria").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"%s\",\"location\":\"%s\"}".formatted(text, text)))
                .andExpect(status().isCreated());
        Cafeteria saved = cafeterias.findAll().get(0);
        assertThat(saved.getName()).isEqualTo(text);
        assertThat(saved.getLocation()).isEqualTo(text);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "25:00", "12:60", "7:00", "noon", "12:00Z", "24:00"})
    void rejectsInvalidOpeningAndClosingTimes(String time) throws Exception {
        for (String field : new String[] {"openingTime", "closingTime"}) {
            mvc.perform(post("/cafeteria").contentType(MediaType.APPLICATION_JSON)
                    .content("{\"name\":\"Central\",\"%s\":\"%s\"}".formatted(field, time)))
                    .andExpect(status().isBadRequest());
        }
        assertThat(cafeterias.count()).isZero();
    }

    @ParameterizedTest
    @ValueSource(strings = {"00:00", "23:59", "07:30:15", "07:30:15.123456789"})
    void preservesSupportedLocalTimeFormats(String time) throws Exception {
        mvc.perform(post("/cafeteria").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Central\",\"openingTime\":\"%s\",\"closingTime\":\"%s\"}"
                        .formatted(time, time)))
                .andExpect(status().isCreated());

        Cafeteria saved = cafeterias.findAll().get(0);
        assertThat(saved.getOpeningTime()).isEqualTo(time);
        assertThat(saved.getClosingTime()).isEqualTo(time);
    }

    @ParameterizedTest
    @ValueSource(strings = {"openingTime", "closingTime"})
    void invalidTimeUpdateDoesNotPartiallyChangeExistingRecord(String field) throws Exception {
        UUID id = createCafeteria();
        mvc.perform(put("/cafeteria/{id}", id).contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Changed\",\"location\":\"Changed\",\"%s\":\"25:00\"}".formatted(field)))
                .andExpect(status().isBadRequest());

        Cafeteria unchanged = cafeterias.findById(id).orElseThrow();
        assertThat(unchanged.getName()).isEqualTo("Central");
        assertThat(unchanged.getLocation()).isEqualTo("Bloco A");
        assertThat(unchanged.getOpeningTime()).isEqualTo("07:00");
        assertThat(unchanged.getClosingTime()).isEqualTo("18:00");
        assertThat(unchanged.isActive()).isTrue();
    }

    private UUID createCafeteria() throws Exception {
        mvc.perform(post("/cafeteria").contentType(MediaType.APPLICATION_JSON).content(VALID_CAFETERIA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty());
        return cafeterias.findAll().get(0).getId();
    }
}
