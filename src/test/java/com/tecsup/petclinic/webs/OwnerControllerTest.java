package com.tecsup.petclinic.webs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tecsup.petclinic.dtos.OwnerDTO;
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
import jakarta.persistence.EntityManager;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("h2")
@Transactional
class OwnerControllerTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @Autowired JdbcTemplate jdbc;
    @Autowired EntityManager entityManager;

    @Test
    void testFindAllOwners() throws Exception {
        mvc.perform(get("/owners"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id", is(1)));
    }

    @Test
    void testFindOwnerOK() throws Exception {
        mvc.perform(get("/owners/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.lastName", is("Franklin")));
    }

    @Test
    void testFindOwnerKO() throws Exception {
        mvc.perform(get("/owners/666"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateOwner() throws Exception {
        OwnerDTO request = OwnerObjectCreator.owner(null, "Espiritu");
        mvc.perform(post("/owners").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", greaterThan(10)))
                .andExpect(jsonPath("$.firstName", is("Sebastian")))
                .andExpect(jsonPath("$.lastName", is("Espiritu")))
                .andExpect(jsonPath("$.address", is("Av. Lima 123")))
                .andExpect(jsonPath("$.city", is("Lima")))
                .andExpect(jsonPath("$.telephone", is("987654321")));
        entityManager.flush();
        assertEquals(1, jdbc.queryForObject("select count(*) from owners where last_name = ?", Integer.class, "Espiritu"));
    }

    @Test
    void testCreateOwner_BadRequest() throws Exception {
        OwnerDTO request = OwnerObjectCreator.owner(null, null);
        mvc.perform(post("/owners").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        entityManager.flush();
        assertEquals(10, jdbc.queryForObject("select count(*) from owners", Integer.class));
    }

    @Test
    void testFindByLastName() throws Exception {
        mvc.perform(get("/owners").param("lastName", "Davis"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].lastName", everyItem(is("Davis"))))
                .andExpect(jsonPath("$[*].id", contains(2, 4)));
    }

    @Test
    void testDeleteOwnerKO() throws Exception {
        mvc.perform(delete("/owners/1000"))
                .andExpect(status().isNotFound());
        entityManager.flush();
        assertEquals(10, jdbc.queryForObject("select count(*) from owners", Integer.class));
    }

    @Test
    void testUpdateOwner() throws Exception {
        OwnerDTO request = OwnerObjectCreator.owner(null, "Espiritu");
        String json = mvc.perform(post("/owners").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long id = mapper.readTree(json).get("id").asLong();
        request.setCity("Cusco");
        mvc.perform(put("/owners/{id}", id).contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.city", is("Cusco")));
        mvc.perform(get("/owners/{id}", id)).andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) id)))
                .andExpect(jsonPath("$.lastName", is("Espiritu")))
                .andExpect(jsonPath("$.city", is("Cusco")));
        entityManager.flush();
        assertEquals("Cusco", jdbc.queryForObject("select city from owners where id = ?", String.class, id));
    }

    @Test
    void testDeleteOwner() throws Exception {
        OwnerDTO request = OwnerObjectCreator.owner(null, "Espiritu");
        String json = mvc.perform(post("/owners").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long id = mapper.readTree(json).get("id").asLong();
        mvc.perform(delete("/owners/{id}", id)).andExpect(status().isOk());
        mvc.perform(get("/owners/{id}", id)).andExpect(status().isNotFound());
        entityManager.flush();
        assertEquals(0, jdbc.queryForObject("select count(*) from owners where id = ?", Integer.class, id));
    }
}
