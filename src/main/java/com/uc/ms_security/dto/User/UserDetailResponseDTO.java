package com.uc.ms_security.dto.User;

import com.uc.ms_security.dto.Profile.ProfileResponseDTO;
import lombok.Value;

@Value
public class UserDetailResponseDTO {

    Long id;

    String name;

    String email;

    ProfileResponseDTO profile;
}