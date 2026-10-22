package com.uc.ms_security.mapper;

import com.uc.ms_security.dto.User.CreateUserDTO;
import com.uc.ms_security.dto.User.UpdateUserDTO;
import com.uc.ms_security.dto.User.UserDetailResponseDTO;
import com.uc.ms_security.dto.User.UserResponseDTO;
import com.uc.ms_security.dto.User.UserSessionsResponseDTO;
import com.uc.ms_security.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final ProfileMapper profileMapper;
    private final SessionMapper sessionMapper;

    public User toEntity(
            CreateUserDTO dto,
            String encodedPassword) {
        User user = new User();

        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(encodedPassword);

        return user;
    }

    public void updateBasicData(UpdateUserDTO dto, User user) {
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
    }

    public UserResponseDTO toResponseDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public UserDetailResponseDTO toDetailResponseDTO(User user) {
        return new UserDetailResponseDTO(
            user.getId(),
            user.getName(),
            user.getEmail(),
            profileMapper.toResponseDTO(user.getProfile())
        );
    }

    public UserSessionsResponseDTO toSessionsResponseDTO(User user) {
        return new UserSessionsResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                sessionMapper.toResponseDTOList(user.getSessions())
        );
    }

    public List<UserResponseDTO> toResponseDTOList(List<User> users) {
        return users.stream()
                .map(this::toResponseDTO)
                .toList();
    }
}