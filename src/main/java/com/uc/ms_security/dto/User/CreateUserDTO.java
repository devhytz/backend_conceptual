package com.uc.ms_security.dto.User;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateUserDTO extends BaseUserDTO {

    @NotBlank(
            message = "La contraseña es obligatoria"
    )
    @Size(
            min = 8,
            message = "La contraseña debe tener mínimo 8 caracteres"
    )
    private String password;
}