package com.uc.ms_security.dto.Session;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateSessionDTO extends BaseSessionDTO {

    private String code2FA;
}
