package com.tecsup.petclinic.services;

import com.tecsup.petclinic.dtos.VetDTO;
import java.util.List;

public interface VetService {
    List<VetDTO> findAll(Boolean active);
    VetDTO findById(Integer id);
    VetDTO create(VetDTO vet);
    VetDTO setActive(Integer id, boolean active);
    void delete(Integer id);
}
