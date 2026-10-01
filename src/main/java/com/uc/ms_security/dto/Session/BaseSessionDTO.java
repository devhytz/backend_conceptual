package com.uc.ms_security.dto.Session;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class BaseSessionDTO {

    @NotBlank(message = "El token es obligatorio")
    private String token;

    @NotNull(message = "La fecha de expiración es obligatoria")
    private LocalDateTime expiration;
}
