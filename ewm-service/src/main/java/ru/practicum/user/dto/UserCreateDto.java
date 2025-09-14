package ru.practicum.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserCreateDto {

    @NotBlank(message = "Field: name. Error: must not be blank. Value: null")
    @Size(min = 2, max = 250)
    private String name;

    @NotBlank(message = "Field: email. Error: must not be blank. Value: null")
    @Email
    @Size(min = 6, max = 254)
    private String email;
}
