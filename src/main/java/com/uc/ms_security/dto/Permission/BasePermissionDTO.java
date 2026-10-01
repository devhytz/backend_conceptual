package com.uc.ms_security.dto.Permission;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BasePermissionDTO {

    @NotBlank(message = "La URL es obligatoria")
    private String url;

    @NotBlank(message = "El método es obligatorio")
    @Size(max = 10, message = "El método no puede exceder 10 caracteres")
    private String method;

    @NotBlank(message = "El modelo es obligatorio")
    @Size(max = 100, message = "El modelo no puede exceder 100 caracteres")
    private String model;
}
