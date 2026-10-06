package com.uc.ms_security.dto.User;

import com.uc.ms_security.dto.Session.SessionResponseDTO;
import lombok.Value;

import java.util.List;

@Value
public class UserSessionsResponseDTO {

    Long id;

    String name;

    String email;

    List<SessionResponseDTO> sessions;
}
