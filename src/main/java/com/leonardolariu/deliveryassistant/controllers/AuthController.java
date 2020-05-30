package com.leonardolariu.deliveryassistant.controllers;

import com.leonardolariu.deliveryassistant.models.ERole;
import com.leonardolariu.deliveryassistant.models.Role;
import com.leonardolariu.deliveryassistant.models.User;
import com.leonardolariu.deliveryassistant.payload.errors.BadRequestError;
import com.leonardolariu.deliveryassistant.payload.requests.SigninRequest;
import com.leonardolariu.deliveryassistant.payload.requests.SignupRequest;
import com.leonardolariu.deliveryassistant.payload.responses.JwtResponse;
import com.leonardolariu.deliveryassistant.payload.responses.MessageResponse;
import com.leonardolariu.deliveryassistant.repositories.RoleRepository;
import com.leonardolariu.deliveryassistant.repositories.UserRepository;
import com.leonardolariu.deliveryassistant.security.jwt.JwtUtils;
import com.leonardolariu.deliveryassistant.security.services.UserDetailsImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;



    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody SigninRequest signinRequest, BindingResult result) {
        log.info("POST request to authenticate user: {}", signinRequest.getUsername());

        ResponseEntity<?> responseEntity = checkValidation(result);
        if (responseEntity != null) return responseEntity;

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(signinRequest.getUsername(), signinRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getUsername(),
                roles));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest, BindingResult result) {
        log.info("POST request to register user: {}", signupRequest.getUsername());

        ResponseEntity<?> responseEntity = checkValidation(result);
        if (responseEntity != null) return responseEntity;

        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new BadRequestError("Username is already taken."));
        }

        // Create new user's account
        User user = new User(signupRequest.getUsername(),
                encoder.encode(signupRequest.getPassword()));

        Set<String> strRoles = signupRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);

                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully."));
    }



    private ResponseEntity<?> checkValidation(BindingResult result) {
        if (result.hasErrors()) {
            List<String> validationErrors = new ArrayList<>();
            result.getAllErrors().forEach(e -> {
                log.error(e.getDefaultMessage());
                validationErrors.add(e.getDefaultMessage());
            });

            if(!validationErrors.isEmpty()) {
                return ResponseEntity
                        .badRequest()
                        .body(new BadRequestError(validationErrors.get(0)));
            }
        }

        return null;
    }
}
