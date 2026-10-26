package com.project.financemanager.controller;

import com.project.financemanager.dto.AuthDTO;
import com.project.financemanager.dto.ProfileDTO;
import com.project.financemanager.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
    @GetMapping("/login")
    public ResponseEntity<Map<String,Object>> login(@RequestBody AuthDTO authDTO){
        try{
            if(!profileService.isAccountActive(authDTO.getEmail())){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message","Account is  not activate.Please activate your account first."));
            }
            Map<String,Object> response = profileService.authenticateAndGenerateToken(authDTO);
            return ResponseEntity.ok(response);

        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("Message",e.getMessage()));

        }

    }
}
