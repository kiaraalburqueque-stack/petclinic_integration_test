package com.tecsup.petclinic.webs;

import com.tecsup.petclinic.dtos.OwnerDTO;
import com.tecsup.petclinic.services.OwnerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/owners")
public class OwnerController {
    private final OwnerService service;
    public OwnerController(OwnerService service) { this.service = service; }
    @GetMapping
    public List<OwnerDTO> findAll(@RequestParam(required = false) String lastName) {
        return lastName == null ? service.findAll() : service.findByLastName(lastName);
    }
    @GetMapping("/{id}")
    public OwnerDTO findById(@PathVariable Long id) { return service.findById(id); }
    private void validate(OwnerDTO owner) {
        if (owner.getLastName() == null || owner.getLastName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "lastName is required");
        }
    }
    @PostMapping
    public ResponseEntity<OwnerDTO> create(@RequestBody OwnerDTO owner) {
        validate(owner);
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(owner));
    }
    @PutMapping("/{id}")
    public OwnerDTO update(@PathVariable Long id, @RequestBody OwnerDTO owner) {
        validate(owner);
        return service.update(id, owner);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}
