package com.egomaa.booknetwork.service;

import com.egomaa.booknetwork.dto.RegistrationRequest;
import com.egomaa.booknetwork.entity.Role;
import com.egomaa.booknetwork.entity.Token;
import com.egomaa.booknetwork.entity.User;
import com.egomaa.booknetwork.repository.RoleRepository;
import com.egomaa.booknetwork.repository.TokenRepository;
import com.egomaa.booknetwork.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Value("${bsn.app.activationLink}")
    private String activationLink;

    public void register(RegistrationRequest request) {

        if (userRepository.findByUsername(request.getEmail()).isPresent()){
            throw new RuntimeException("Email already in use");
        }

        List<Role> roles = new ArrayList<>();
        Role userRole = roleRepository.findByName("USER").orElseThrow(() -> new RuntimeException("User not found"));
        roles.add(userRole);

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(roles)
                .accountLocked(false)
                .enabled(false)
                .build();

        userRepository.save(user);
        sendValidationEmail(user);
    }

    public void sendValidationEmail(User user) {
        String activationToken = saveActivationToken(user);

        // Prepare email variables
        Map<String, Object> emailVariables = new HashMap<>();
        emailVariables.put("fullName", user.getFullName());
        emailVariables.put("activationCode", activationToken);
        emailVariables.put("activationLink",activationLink);

        emailService.sendEmail(user.getEmail(),
                "Activate Your Account",
                "activation-email",
                emailVariables);
    }

    public String saveActivationToken(User user) {
        String activationToken = generateActivationToken(6);   // Ex: 768407
        Token buildToken = Token.builder()
                .token(activationToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();
        tokenRepository.save(buildToken);
        return activationToken;
    }

    private String generateActivationToken(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder token = new StringBuilder();
        String digits = "0123456789";  // Only numeric characters

        for (int i = 0; i < length; i++) {
            token.append(digits.charAt(random.nextInt(digits.length())));
        }
        return token.toString();
    }


}
