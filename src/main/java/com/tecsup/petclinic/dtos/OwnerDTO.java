package com.tecsup.petclinic.dtos;

import lombok.Data;

@Data
public class OwnerDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String address;
    private String city;
    private String telephone;
}
