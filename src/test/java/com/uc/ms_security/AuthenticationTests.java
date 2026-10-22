package com.uc.ms_security;

import com.uc.ms_security.config.JwtConfig;
import com.uc.ms_security.dto.AuthResponseDTO;
import com.uc.ms_security.dto.LoginRequestDTO;
import com.uc.ms_security.entity.User;
import com.uc.ms_security.exception.ApplicationException;
import com.uc.ms_security.exception.ErrorCase;
import com.uc.ms_security.repository.UserRepository;
import com.uc.ms_security.service.AuthService;
import com.uc.ms_security.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthenticationTests {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(4);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final JwtService jwtService = mock(JwtService.class);
    private final AuthService authService = new AuthService(
            userRepository,
            passwordEncoder,
            jwtService
    );

    @Test
    void loginWithValidCredentialsReturnsToken() {
        User user = user();
        user.setPassword(passwordEncoder.encode("ValidPassword123!"));
        LoginRequestDTO request = loginRequest("ana@example.com", "ValidPassword123!");
        AuthResponseDTO response = new AuthResponseDTO(
                "signed-token",
                "Bearer",
                Instant.now().plusSeconds(900)
        );
        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn(response);

        assertEquals(response, authService.login(request));
        verify(jwtService).generateToken(user);
    }

    @Test
    void loginWithWrongPasswordReturnsGenericUnauthorizedError() {
        User user = user();
        user.setPassword(passwordEncoder.encode("ValidPassword123!"));
        LoginRequestDTO request = loginRequest("ana@example.com", "WrongPassword123!");
        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(user));

        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> authService.login(request)
        );

        assertEquals(ErrorCase.UNAUTHORIZED, exception.getErrorCase());
        assertEquals("Credenciales incorrectas", exception.getMessage());
        verify(jwtService, never()).generateToken(user);
    }

    @Test
    void loginWithUnknownEmailReturnsSameGenericUnauthorizedError() {
        LoginRequestDTO request = loginRequest("missing@example.com", "AnyPassword123!");
        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.empty());

        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> authService.login(request)
        );

        assertEquals(ErrorCase.UNAUTHORIZED, exception.getErrorCase());
        assertEquals("Credenciales incorrectas", exception.getMessage());
        verify(jwtService, never()).generateToken(
                org.mockito.ArgumentMatchers.any(User.class)
        );
    }

    @Test
    void generatedJwtHasExpectedClaimsAndCanBeValidated() {
        JwtConfig jwtConfig = new JwtConfig();
        SecretKey secretKey = jwtConfig.jwtSecretKey(
                "test-signing-secret-with-at-least-32-bytes"
        );
        JwtEncoder jwtEncoder = jwtConfig.jwtEncoder(secretKey);
        JwtDecoder jwtDecoder = jwtConfig.jwtDecoder(secretKey);
        JwtService service = new JwtService(jwtEncoder);

        AuthResponseDTO response = service.generateToken(user());
        Jwt decoded = jwtDecoder.decode(response.accessToken());

        assertEquals("Bearer", response.tokenType());
        assertEquals("ms-security", decoded.getClaimAsString("iss"));
        assertEquals("42", decoded.getSubject());
        assertEquals("ana@example.com", decoded.getClaimAsString("email"));
        assertNotNull(decoded.getId());
        assertTrue(decoded.getExpiresAt().isAfter(Instant.now()));
        assertEquals(decoded.getExpiresAt(), response.expiresAt());
        assertTrue(decoded.getTokenValue().contains("."));
    }

    @Test
    void jwtDecoderRejectsModifiedToken() {
        JwtConfig jwtConfig = new JwtConfig();
        SecretKey secretKey = jwtConfig.jwtSecretKey(
                "test-signing-secret-with-at-least-32-bytes"
        );
        JwtService service = new JwtService(jwtConfig.jwtEncoder(secretKey));
        JwtDecoder decoder = jwtConfig.jwtDecoder(secretKey);
        String token = service.generateToken(user()).accessToken();
        String modifiedToken = token.substring(0, token.length() - 1)
                + (token.endsWith("a") ? "b" : "a");

        assertThrows(JwtException.class, () -> decoder.decode(modifiedToken));
    }

    @Test
    void jwtSecretMustBeAtLeast32Bytes() {
        assertThrows(
                IllegalStateException.class,
                () -> new JwtConfig().jwtSecretKey("too-short")
        );
    }

    private User user() {
        User user = new User();
        user.setId(42L);
        user.setName("Ana");
        user.setEmail("ana@example.com");
        return user;
    }

    private LoginRequestDTO loginRequest(String email, String password) {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail(email);
        request.setPassword(password);
        return request;
    }
}
