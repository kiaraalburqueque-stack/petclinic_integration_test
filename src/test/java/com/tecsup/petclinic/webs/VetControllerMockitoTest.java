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
import com.tecsup.petclinic.services.VetService;
import com.tecsup.petclinic.exceptions.VetNotFoundException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import java.util.List;
import java.util.stream.Stream;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("h2")
class VetControllerMockitoTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @MockitoBean VetService service;

    @Test
    void testFindAllVets() throws Exception {
        when(service.findAll(null)).thenReturn(Stream.concat(VetObjectCreator.activeVets().stream(), Stream.of(VetObjectCreator.vet(6, false))).toList());
        mvc.perform(get("/vets"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(6)))
                .andExpect(jsonPath("$[0].id", is(1)));
        verify(service).findAll(null);
        verifyNoMoreInteractions(service);
    }

    @Test
    void testFindVetOK() throws Exception {
        when(service.findById(1)).thenReturn(VetObjectCreator.vet(1, true));
        mvc.perform(get("/vets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.lastName", is("Carter")));
        verify(service).findById(1);
        verifyNoMoreInteractions(service);
    }

    @Test
    void testFindVetKO() throws Exception {
        when(service.findById(666)).thenThrow(new VetNotFoundException(666));
        mvc.perform(get("/vets/666"))
                .andExpect(status().isNotFound());
        verify(service).findById(666);
        verifyNoMoreInteractions(service);
    }

    @Test
    void testCreateVet() throws Exception {
        VetDTO request = VetObjectCreator.vet(0, true); request.setId(null);
        VetDTO saved = VetObjectCreator.vet(7, true);
        when(service.create(request)).thenReturn(saved);
        mvc.perform(post("/vets").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", greaterThan(6)))
                .andExpect(jsonPath("$.firstName", is("James")))
                .andExpect(jsonPath("$.lastName", is("Carter")))
                .andExpect(jsonPath("$.email", is("james.carter@petclinic.com")))
                .andExpect(jsonPath("$.phone", is("6085551234")))
                .andExpect(jsonPath("$.active", is(true)));
        verify(service).create(request);
        verifyNoMoreInteractions(service);
    }

    @Test
    void testDeactivateVet() throws Exception {
        when(service.setActive(1, false)).thenReturn(VetObjectCreator.vet(1, false));
        mvc.perform(put("/vets/1/deactivate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.active", is(false)));
        verify(service).setActive(1, false);
        verifyNoMoreInteractions(service);
    }

    @Test
    void testReactivateVet() throws Exception {
        when(service.setActive(6, true)).thenReturn(VetObjectCreator.vet(6, true));
        mvc.perform(put("/vets/6/reactivate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(6)))
                .andExpect(jsonPath("$.active", is(true)));
        verify(service).setActive(6, true);
        verifyNoMoreInteractions(service);
    }

    @Test
    void testFindActiveVets() throws Exception {
        when(service.findAll(true)).thenReturn(VetObjectCreator.activeVets());
        mvc.perform(get("/vets").param("active", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$[*].active", everyItem(is(true))));
        verify(service).findAll(true);
        verifyNoMoreInteractions(service);
    }

    @Test
    void testFindInactiveVets() throws Exception {
        when(service.findAll(false)).thenReturn(List.of(VetObjectCreator.vet(6, false)));
        mvc.perform(get("/vets").param("active", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(6)))
                .andExpect(jsonPath("$[*].active", everyItem(is(false))));
        verify(service).findAll(false);
        verifyNoMoreInteractions(service);
    }

    @Test
    void testDeleteVetKO() throws Exception {
        doThrow(new VetNotFoundException(1000)).when(service).delete(1000);
        mvc.perform(delete("/vets/1000"))
                .andExpect(status().isNotFound());
        verify(service).delete(1000);
        verifyNoMoreInteractions(service);
    }
}
