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
import com.tecsup.petclinic.services.OwnerService;
import com.tecsup.petclinic.exceptions.OwnerNotFoundException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import java.util.List;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("h2")
class OwnerControllerMockitoTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @MockitoBean OwnerService service;

    @Test
    void testFindAllOwners() throws Exception {
        when(service.findAll()).thenReturn(List.of(OwnerObjectCreator.owner(1L, "Franklin")));
        mvc.perform(get("/owners"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id", is(1)));
        verify(service).findAll();
        verifyNoMoreInteractions(service);
    }

    @Test
    void testFindOwnerOK() throws Exception {
        when(service.findById(1L)).thenReturn(OwnerObjectCreator.owner(1L, "Franklin"));
        mvc.perform(get("/owners/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.lastName", is("Franklin")));
        verify(service).findById(1L);
        verifyNoMoreInteractions(service);
    }

    @Test
    void testFindOwnerKO() throws Exception {
        when(service.findById(666L)).thenThrow(new OwnerNotFoundException(666L));
        mvc.perform(get("/owners/666"))
                .andExpect(status().isNotFound());
        verify(service).findById(666L);
        verifyNoMoreInteractions(service);
    }

    @Test
    void testCreateOwner() throws Exception {
        OwnerDTO request = OwnerObjectCreator.owner(null, "Espiritu");
        when(service.create(request)).thenReturn(OwnerObjectCreator.owner(11L, "Espiritu"));
        mvc.perform(post("/owners").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", greaterThan(10)))
                .andExpect(jsonPath("$.firstName", is("Sebastian")))
                .andExpect(jsonPath("$.lastName", is("Espiritu")))
                .andExpect(jsonPath("$.address", is("Av. Lima 123")))
                .andExpect(jsonPath("$.city", is("Lima")))
                .andExpect(jsonPath("$.telephone", is("987654321")));
        verify(service).create(request);
        verifyNoMoreInteractions(service);
    }

    @Test
    void testCreateOwner_BadRequest() throws Exception {
        OwnerDTO request = OwnerObjectCreator.owner(null, null);
        mvc.perform(post("/owners").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }

    @Test
    void testFindByLastName() throws Exception {
        when(service.findByLastName("Davis")).thenReturn(List.of(OwnerObjectCreator.owner(2L, "Davis"), OwnerObjectCreator.owner(4L, "Davis")));
        mvc.perform(get("/owners").param("lastName", "Davis"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].lastName", everyItem(is("Davis"))))
                .andExpect(jsonPath("$[*].id", contains(2, 4)));
        verify(service).findByLastName("Davis");
        verifyNoMoreInteractions(service);
    }

    @Test
    void testDeleteOwnerKO() throws Exception {
        doThrow(new OwnerNotFoundException(1000L)).when(service).delete(1000L);
        mvc.perform(delete("/owners/1000"))
                .andExpect(status().isNotFound());
        verify(service).delete(1000L);
        verifyNoMoreInteractions(service);
    }

    @Test
    void testUpdateOwner() throws Exception {
        OwnerDTO request = OwnerObjectCreator.owner(null, "Espiritu");
        when(service.create(request)).thenReturn(OwnerObjectCreator.owner(11L, "Espiritu"));
        String json = mvc.perform(post("/owners").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long id = mapper.readTree(json).get("id").asLong();
        request.setCity("Cusco");
        OwnerDTO changed = OwnerObjectCreator.owner(id, "Espiritu");
        changed.setCity("Cusco");
        when(service.update(id, request)).thenReturn(changed);
        when(service.findById(id)).thenReturn(changed);
        mvc.perform(put("/owners/{id}", id).contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.city", is("Cusco")));
        mvc.perform(get("/owners/{id}", id)).andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) id)))
                .andExpect(jsonPath("$.lastName", is("Espiritu")))
                .andExpect(jsonPath("$.city", is("Cusco")));
        verify(service).update(id, request);
        verify(service).findById(id);
        verify(service).create(OwnerObjectCreator.owner(null, "Espiritu"));
        verifyNoMoreInteractions(service);
    }

    @Test
    void testDeleteOwner() throws Exception {
        OwnerDTO request = OwnerObjectCreator.owner(null, "Espiritu");
        when(service.create(request)).thenReturn(OwnerObjectCreator.owner(11L, "Espiritu"));
        String json = mvc.perform(post("/owners").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long id = mapper.readTree(json).get("id").asLong();
        doNothing().when(service).delete(id);
        when(service.findById(id)).thenThrow(new OwnerNotFoundException(id));
        mvc.perform(delete("/owners/{id}", id)).andExpect(status().isOk());
        mvc.perform(get("/owners/{id}", id)).andExpect(status().isNotFound());
        verify(service).delete(id);
        verify(service).findById(id);
        verify(service).create(OwnerObjectCreator.owner(null, "Espiritu"));
        verifyNoMoreInteractions(service);
    }
}
