package com.tecsup.petclinic.services;

import com.tecsup.petclinic.dtos.OwnerDTO;
import java.util.List;

public interface OwnerService {
    List<OwnerDTO> findAll();
    List<OwnerDTO> findByLastName(String lastName);
    OwnerDTO findById(Long id);
    OwnerDTO create(OwnerDTO owner);
    OwnerDTO update(Long id, OwnerDTO owner);
    void delete(Long id);
}
