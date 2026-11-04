package com.tecsup.petclinic.services;

import com.tecsup.petclinic.dtos.VetDTO;
import com.tecsup.petclinic.entities.Vet;
import com.tecsup.petclinic.exceptions.VetNotFoundException;
import com.tecsup.petclinic.repositories.VetRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class VetServiceImpl implements VetService {
    private final VetRepository repository;
    public VetServiceImpl(VetRepository repository) { this.repository = repository; }

    public List<VetDTO> findAll(Boolean active) {
        return (active == null ? repository.findAll(Sort.by("id")) :
                repository.findByActiveOrderByIdAsc(active)).stream().map(this::toDto).toList();
    }
    private Vet requireVet(Integer id) {
        return repository.findById(id).orElseThrow(() -> new VetNotFoundException(id));
    }
    public VetDTO findById(Integer id) { return toDto(requireVet(id)); }
    public VetDTO create(VetDTO dto) {
        Vet vet = new Vet();
        vet.setFirstName(dto.getFirstName());
        vet.setLastName(dto.getLastName());
        vet.setEmail(dto.getEmail());
        vet.setPhone(dto.getPhone());
        vet.setActive(dto.getActive() == null ? true : dto.getActive());
        return toDto(repository.save(vet));
    }
    public VetDTO setActive(Integer id, boolean active) {
        Vet vet = requireVet(id);
        vet.setActive(active);
        return toDto(repository.save(vet));
    }
    public void delete(Integer id) { repository.delete(requireVet(id)); }
    private VetDTO toDto(Vet vet) {
        VetDTO dto = new VetDTO();
        dto.setId(vet.getId());
        dto.setFirstName(vet.getFirstName());
        dto.setLastName(vet.getLastName());
        dto.setEmail(vet.getEmail());
        dto.setPhone(vet.getPhone());
        dto.setActive(vet.getActive());
        return dto;
    }
}
