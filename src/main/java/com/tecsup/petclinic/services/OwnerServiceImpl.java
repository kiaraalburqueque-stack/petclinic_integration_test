package com.tecsup.petclinic.services;

import com.tecsup.petclinic.dtos.OwnerDTO;
import com.tecsup.petclinic.entities.Owner;
import com.tecsup.petclinic.exceptions.OwnerNotFoundException;
import com.tecsup.petclinic.mapper.OwnerMapper;
import com.tecsup.petclinic.repositories.OwnerRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class OwnerServiceImpl implements OwnerService {
    private final OwnerRepository repository;
    private final OwnerMapper mapper;
    public OwnerServiceImpl(OwnerRepository repository, OwnerMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }
    public List<OwnerDTO> findAll() {
        return repository.findAll(Sort.by("id")).stream().map(mapper::mapToDto).toList();
    }
    public List<OwnerDTO> findByLastName(String lastName) {
        return repository.findByLastNameOrderByIdAsc(lastName).stream().map(mapper::mapToDto).toList();
    }
    private Owner requireOwner(Long id) {
        return repository.findById(id).orElseThrow(() -> new OwnerNotFoundException(id));
    }
    public OwnerDTO findById(Long id) { return mapper.mapToDto(requireOwner(id)); }
    public OwnerDTO create(OwnerDTO dto) {
        Owner owner = mapper.mapToEntity(dto);
        owner.setId(null);
        return mapper.mapToDto(repository.save(owner));
    }
    public OwnerDTO update(Long id, OwnerDTO dto) {
        Owner owner = requireOwner(id);
        owner.setFirstName(dto.getFirstName());
        owner.setLastName(dto.getLastName());
        owner.setAddress(dto.getAddress());
        owner.setCity(dto.getCity());
        owner.setTelephone(dto.getTelephone());
        return mapper.mapToDto(repository.save(owner));
    }
    public void delete(Long id) { repository.delete(requireOwner(id)); }
}
