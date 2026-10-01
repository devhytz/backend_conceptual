package com.uc.ms_security.service;

import com.uc.ms_security.dto.Profile.CreateProfileDTO;
import com.uc.ms_security.dto.Profile.UpdateProfileDTO;
import com.uc.ms_security.dto.Profile.ProfileResponseDTO;
import com.uc.ms_security.entity.Profile;
import com.uc.ms_security.exception.ApplicationException;
import com.uc.ms_security.exception.ErrorCase;
import com.uc.ms_security.mapper.ProfileMapper;
import com.uc.ms_security.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;

    private final ProfileMapper profileMapper;

    public ProfileResponseDTO create(CreateProfileDTO dto) {
        Profile profile = profileMapper.toEntity(dto);
        Profile savedProfile = profileRepository.save(profile);
        return profileMapper.toResponseDTO(savedProfile);
    }

    public List<ProfileResponseDTO> findAll() {
        List<Profile> profiles = profileRepository.findAll();
        return profileMapper.toResponseDTOList(profiles);
    }

    private Profile findProfile(Long id) {
        return profileRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(
                        ErrorCase.NOT_FOUND,
                        "Perfil no encontrado con id: " + id
                ));
    }

    public ProfileResponseDTO findById(Long id) {
        Profile profile = findProfile(id);
        return profileMapper.toResponseDTO(profile);
    }

    public ProfileResponseDTO update(Long id, UpdateProfileDTO dto) {
        Profile profile = findProfile(id);
        profileMapper.updateEntity(dto, profile);
        Profile updatedProfile = profileRepository.save(profile);
        return profileMapper.toResponseDTO(updatedProfile);
    }

    public void delete(Long id) {
        Profile profile = findProfile(id);
        profileRepository.delete(profile);
    }
}
