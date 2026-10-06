package com.uc.ms_security.controller;

import com.uc.ms_security.dto.Profile.CreateProfileDTO;
import com.uc.ms_security.dto.Profile.UpdateProfileDTO;
import com.uc.ms_security.dto.Profile.ProfileResponseDTO;
import com.uc.ms_security.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping("/{userId}/profiles")
    @ResponseStatus(HttpStatus.CREATED)
    public ProfileResponseDTO create(
            @PathVariable Long userId,
            @Valid @RequestBody CreateProfileDTO dto) {
        return profileService.create(userId, dto);
    }

    @GetMapping("/{userId}/profiles")
    public ProfileResponseDTO findByUserId(@PathVariable Long userId) {
        return profileService.findByUserId(userId);
    }

    @PutMapping("/{userId}/profiles")
    public ProfileResponseDTO update(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateProfileDTO dto) {
        return profileService.update(userId, dto);
    }

    @DeleteMapping("/{userId}/profiles")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long userId) {
        profileService.delete(userId);
    }
}
