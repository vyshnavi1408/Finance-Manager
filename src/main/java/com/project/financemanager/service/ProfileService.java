package com.project.financemanager.service;

import com.project.financemanager.Utility.JWTutil;
import com.project.financemanager.dto.AuthDTO;
import com.project.financemanager.dto.ProfileDTO;
import com.project.financemanager.entity.ProfileEntity;
import com.project.financemanager.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {
    //without final, we'll get a NullPointerException.
    private final ProfileRepository profileRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final AppUserDetailsService appUserDetailsService;

    //this method accepts the DTO object containing data (possibly from the controller)
    public ProfileDTO registerProfile(ProfileDTO profileDTO) {
        ProfileEntity newProfile = toEntity(profileDTO);
        newProfile.setActivationToken((UUID.randomUUID().toString()));
        newProfile = profileRepository.save(newProfile);

        String activationLink = "http://localhost:8080/api/v1.0/activate?token=" + newProfile.getActivationToken();
        String subject="Activate your Finance Manager account";
        String body = "Click on the following link to activate your account: "+ activationLink;
        emailService.sendEmail(newProfile.getEmail(), subject, body);
        return toDTO(newProfile);

    }

    //.builder() creates a new entity object, by copying the data from DTO and puts it in entity
    public ProfileEntity toEntity(ProfileDTO profileDTO) {
        return ProfileEntity.builder()
                .id(profileDTO.getId())
                .fullName(profileDTO.getFullName())
                .email(profileDTO.getEmail())
                .password(passwordEncoder.encode(profileDTO.getPassword()))
                .profileImageUrl(profileDTO.getProfileImageUrl())
                .createdAt(profileDTO.getCreatedAt())
                .updatedAt(profileDTO.getUpdatedAt())
                .build();
    }

    public ProfileDTO toDTO(ProfileEntity profileEntity) {
        return ProfileDTO.builder()
                .id(profileEntity.getId())
                .fullName(profileEntity.getFullName())
                .email(profileEntity.getEmail())
                .profileImageUrl(profileEntity.getProfileImageUrl())
                .createdAt(profileEntity.getCreatedAt())
                .updatedAt(profileEntity.getUpdatedAt())
                .build();

    }

    public boolean activateProfile(String activationToken) {
        return profileRepository.findByActivationToken(activationToken)
                .map(profileEntity -> {
                    profileEntity.setIsActive(true);
                    profileRepository.save(profileEntity);
                    return true;
                })
                .orElse(false);
    }
    //To check if the account is active
    public boolean isAccountActive(String email){
        return profileRepository.findByEmail(email)
                .map(ProfileEntity::getIsActive)
                .orElse(false);
    }
    //tells us who is making this request right now.
    public ProfileEntity getCurrentProfile(){
        //After login spring security remembers the logged-in user for the whole request.
        //it keeps that in SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return profileRepository.findByEmail(authentication.getName())
                .orElseThrow(()->new UsernameNotFoundException("Profile not found with the email: "+authentication.getName()));
    }

    //it returns a safe sharable copy of someone's profile or our profile.

    public ProfileDTO getPublicProfile(String email){
        ProfileEntity currentUser = null;
        if(email == null){
            currentUser = getCurrentProfile();
        }else{
            currentUser = profileRepository.findByEmail(email)
                    .orElseThrow(()->new UsernameNotFoundException("Profile not found with email: "+email));
        }
        return toDTO(currentUser);

    }
    public Map<String, Object> authenticateAndGenerateToken(AuthDTO authDTO) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authDTO.getEmail(), authDTO.getPassword()));
        String token = JWTutil.generateToken();
        return Map.of(
                "token", token,
                "user", getPublicProfile(authDTO.getEmail())
        );
    }
    //This method needs to return two things, a token(String) and a profileDT0.

}

