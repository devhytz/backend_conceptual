package com.uc.ms_security;

import com.uc.ms_security.dto.User.CreateUserDTO;
import com.uc.ms_security.dto.User.UpdateUserDTO;
import com.uc.ms_security.entity.User;
import com.uc.ms_security.mapper.ProfileMapper;
import com.uc.ms_security.mapper.SessionMapper;
import com.uc.ms_security.mapper.UserMapper;
import com.uc.ms_security.repository.UserRepository;
import com.uc.ms_security.service.UserService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserPasswordProtectionTests {

    private final PasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder(4);

    private final UserRepository userRepository = mock(UserRepository.class);

    private final UserService userService = new UserService(
            userRepository,
            new UserMapper(new ProfileMapper(), new SessionMapper()),
            passwordEncoder
    );

    @Test
    void createStoresEncodedPasswordAndResponseOmitsIt() {
        CreateUserDTO dto = validCreateUser();
        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = userService.create(dto);

        var savedUserCaptor = org.mockito.ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(savedUserCaptor.capture());
        User savedUser = savedUserCaptor.getValue();

        assertNotEquals(dto.getPassword(), savedUser.getPassword());
        assertTrue(passwordEncoder.matches(dto.getPassword(), savedUser.getPassword()));
        assertEquals(dto.getName(), response.getName());
        assertEquals(dto.getEmail(), response.getEmail());
    }

    @Test
    void updateWithoutPasswordPreservesExistingHash() {
        User user = new User();
        user.setId(1L);
        user.setName("Ana");
        user.setEmail("ana@example.com");
        user.setPassword(passwordEncoder.encode("OldPassword123!"));
        String existingHash = user.getPassword();

        UpdateUserDTO dto = new UpdateUserDTO();
        dto.setName("Ana Updated");
        dto.setEmail("ana.updated@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailAndIdNot(dto.getEmail(), 1L))
                .thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);

        userService.update(1L, dto);

        assertEquals(existingHash, user.getPassword());
    }

    @Test
    void updateWithPasswordStoresNewEncodedPassword() {
        User user = new User();
        user.setId(1L);
        user.setName("Ana");
        user.setEmail("ana@example.com");
        user.setPassword(passwordEncoder.encode("OldPassword123!"));

        UpdateUserDTO dto = new UpdateUserDTO();
        dto.setName("Ana");
        dto.setEmail("ana@example.com");
        dto.setPassword("NewPassword123!");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailAndIdNot(dto.getEmail(), 1L))
                .thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);

        userService.update(1L, dto);

        assertTrue(passwordEncoder.matches(dto.getPassword(), user.getPassword()));
        assertNotEquals(dto.getPassword(), user.getPassword());
    }

    @Test
    void createPasswordRequiresAllConfiguredCharacterClasses() {
        Validator validator = Validation.buildDefaultValidatorFactory()
                .getValidator();
        CreateUserDTO dto = validCreateUser();
        dto.setPassword("Abcdef1!");

        assertFalse(hasPasswordViolation(validator.validate(dto)));

        dto.setPassword("password123!");

        assertTrue(hasPasswordViolation(validator.validate(dto)));
    }

    @Test
    void createPasswordCannotExceed72Characters() {
        Validator validator = Validation.buildDefaultValidatorFactory()
                .getValidator();
        CreateUserDTO dto = validCreateUser();
        dto.setPassword("A" + "a".repeat(69) + "1!");

        assertFalse(hasPasswordViolation(validator.validate(dto)));

        dto.setPassword("A" + "a".repeat(70) + "1!");
        assertTrue(hasPasswordViolation(validator.validate(dto)));
    }

    @Test
    void updatePasswordMayBeOmitted() {
        Validator validator = Validation.buildDefaultValidatorFactory()
                .getValidator();
        UpdateUserDTO dto = new UpdateUserDTO();
        dto.setName("Ana");
        dto.setEmail("ana@example.com");

        assertTrue(validator.validate(dto).isEmpty());
    }

    private CreateUserDTO validCreateUser() {
        CreateUserDTO dto = new CreateUserDTO();
        dto.setName("Ana");
        dto.setEmail("ana@example.com");
        dto.setPassword("ValidPassword123!");
        return dto;
    }

    private boolean hasPasswordViolation(
            Set<ConstraintViolation<CreateUserDTO>> violations) {
        return violations.stream()
                .anyMatch(violation -> violation.getPropertyPath()
                        .toString()
                        .equals("password"));
    }
}
