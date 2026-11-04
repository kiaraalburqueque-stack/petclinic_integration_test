package com.tecsup.petclinic.dtos;

import lombok.Data;

@Data
public class VetDTO {
    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Boolean active = true;
}
