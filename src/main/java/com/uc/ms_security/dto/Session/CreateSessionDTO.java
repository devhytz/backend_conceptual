package com.uc.ms_security.dto.Session;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateSessionDTO extends BaseSessionDTO {

    private String code2FA;
}
