package com.tecsup.petclinic.webs;

import com.tecsup.petclinic.dtos.OwnerDTO;

final class OwnerObjectCreator {
    private OwnerObjectCreator() {}
    static OwnerDTO owner(Long id, String lastName) {
        OwnerDTO owner = new OwnerDTO();
        owner.setId(id);
        owner.setFirstName("Sebastian");
        owner.setLastName(lastName);
        owner.setAddress("Av. Lima 123");
        owner.setCity("Lima");
        owner.setTelephone("987654321");
        return owner;
    }
}
