package com.tecsup.petclinic.webs;

import com.tecsup.petclinic.dtos.VetDTO;
import com.tecsup.petclinic.services.VetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/vets")
public class VetController {
    private final VetService service;
    public VetController(VetService service) { this.service = service; }
    @GetMapping
    public List<VetDTO> findAll(@RequestParam(required = false) Boolean active) {
        return service.findAll(active);
    }
    @GetMapping("/{id}")
    public VetDTO findById(@PathVariable Integer id) { return service.findById(id); }
    @PostMapping
    public ResponseEntity<VetDTO> create(@RequestBody VetDTO vet) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(vet));
    }
    @PutMapping("/{id}/deactivate")
    public VetDTO deactivate(@PathVariable Integer id) { return service.setActive(id, false); }
    @PutMapping("/{id}/reactivate")
    public VetDTO reactivate(@PathVariable Integer id) { return service.setActive(id, true); }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}
