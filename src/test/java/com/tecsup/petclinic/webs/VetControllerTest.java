package com.tecsup.petclinic.webs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tecsup.petclinic.dtos.VetDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.core.JdbcTemplate;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("h2")
@Transactional
class VetControllerTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @Autowired JdbcTemplate jdbc;
    @Autowired jakarta.persistence.EntityManager entityManager;

    @Test
    void testFindAllVets() throws Exception {
        mvc.perform(get("/vets"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(6)))
                .andExpect(jsonPath("$[0].id", is(1)));
    }

    @Test
    void testFindVetOK() throws Exception {
        mvc.perform(get("/vets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.lastName", is("Carter")));
    }

    @Test
    void testFindVetKO() throws Exception {
        mvc.perform(get("/vets/666"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateVet() throws Exception {
        VetDTO request = VetObjectCreator.vet(0, true); request.setId(null);
        mvc.perform(post("/vets").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", greaterThan(6)))
                .andExpect(jsonPath("$.firstName", is("James")))
                .andExpect(jsonPath("$.lastName", is("Carter")))
                .andExpect(jsonPath("$.email", is("james.carter@petclinic.com")))
                .andExpect(jsonPath("$.phone", is("6085551234")))
                .andExpect(jsonPath("$.active", is(true)));
        assertEquals(7, jdbc.queryForObject("select count(*) from vets", Integer.class));
        assertEquals(2, jdbc.queryForObject("select count(*) from vets where email = ?", Integer.class, request.getEmail()));
    }

    @Test
    void testDeactivateVet() throws Exception {
        mvc.perform(put("/vets/1/deactivate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.active", is(false)));
        entityManager.flush();
        assertFalse(jdbc.queryForObject("select active from vets where id = 1", Boolean.class));
        mvc.perform(get("/vets/1")).andExpect(jsonPath("$.active", is(false)));
    }

    @Test
    void testReactivateVet() throws Exception {
        mvc.perform(put("/vets/6/reactivate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(6)))
                .andExpect(jsonPath("$.active", is(true)));
        entityManager.flush();
        assertTrue(jdbc.queryForObject("select active from vets where id = 6", Boolean.class));
        mvc.perform(get("/vets/6")).andExpect(jsonPath("$.active", is(true)));
    }

    @Test
    void testFindActiveVets() throws Exception {
        mvc.perform(get("/vets").param("active", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$[*].active", everyItem(is(true))));
    }

    @Test
    void testFindInactiveVets() throws Exception {
        mvc.perform(get("/vets").param("active", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(6)))
                .andExpect(jsonPath("$[*].active", everyItem(is(false))));
    }

    @Test
    void testDeleteVetKO() throws Exception {
        mvc.perform(delete("/vets/1000"))
                .andExpect(status().isNotFound());
        assertEquals(6, jdbc.queryForObject("select count(*) from vets", Integer.class));
    }
}
