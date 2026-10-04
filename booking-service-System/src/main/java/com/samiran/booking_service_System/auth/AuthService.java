package com.samiran.booking_service_System.auth;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    //signup - method
    @Transactional
    public SignupResponse signUp(SignupRequest signupRequest){
        String email = signupRequest.getEmail().trim().toLowerCase();
        //sanity check
        if(userRepository.existsByEmail(email)){
            throw new EmailAlreadyExistsException("Email is already Registered: "+ email);
        }
        //now building
        //new-user object
        // Building new-user object
        User newUser = new User();

        newUser.setFullName(signupRequest.getFullName());
        newUser.setEmail(signupRequest.getEmail());
        newUser.setPasswordHash(passwordEncoder.encode(signupRequest.getPassword()));
    //save the object
        User saved = userRepository.save(newUser);
        return new SignupResponse(saved.getId(),saved.getFullName(),saved.getEmail());
    }
}
