package ru.practicum.dto.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserCreateDto {

    @NotBlank(message = "Field: name. Error: must not be blank. Value: null")
    @Size(max = 250)
    private String name;

    @NotBlank(message = "Field: email. Error: must not be blank. Value: null")
    @Email
    @Size(max = 254)
    private String email;
}
