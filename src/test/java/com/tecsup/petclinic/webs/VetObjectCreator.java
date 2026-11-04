package com.tecsup.petclinic.webs;

import com.tecsup.petclinic.dtos.VetDTO;
import java.util.List;
import java.util.stream.IntStream;

final class VetObjectCreator {
    private VetObjectCreator() {}
    static VetDTO vet(int id, boolean active) {
        VetDTO vet = new VetDTO();
        vet.setId(id);
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vet.setEmail("james.carter@petclinic.com");
        vet.setPhone("6085551234");
        vet.setActive(active);
        return vet;
    }
    static List<VetDTO> activeVets() {
        return IntStream.rangeClosed(1, 5).mapToObj(id -> vet(id, true)).toList();
    }
}
