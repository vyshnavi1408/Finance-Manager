package com.project.financemanager.controller;

import com.project.financemanager.dto.ProfileDTO;
import com.project.financemanager.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1.0")
public class ProfileController {
    private final ProfileService profileService;
    //@RequestBody here because the incoming request JSON, it is converted to profileDto obj.
    @PostMapping("/register")
    public ResponseEntity<ProfileDTO> registerProfile(@RequestBody ProfileDTO profileDTO) {
        ProfileDTO registeredProfile = profileService.registerProfile(profileDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredProfile);
        //for a successful registration 201 created is better than 200ok.

    }
    @GetMapping("/activate")
    public ResponseEntity<String> activateProfile(@RequestParam String Token) {
        boolean isActivated = profileService.activateProfile(Token);
        if (isActivated) {
            return ResponseEntity.ok("Activated successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Activation Token not found or already in use.");
        }
    }
}
