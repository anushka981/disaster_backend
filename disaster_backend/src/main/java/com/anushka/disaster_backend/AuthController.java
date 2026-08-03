package com.anushka.disaster_backend;

import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = {
        "http://127.0.0.1:5500",
        "http://localhost:5500"
})
public class AuthController {

    private final VolunteerRepository repo;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;

    public AuthController(VolunteerRepository repo, PasswordEncoder encoder, JwtService jwtService) {
        this.repo = repo;
        this.encoder = encoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/signup")
    public String signup(@Valid @RequestBody SignupRequest request) {

        if (repo.findByUsernameIgnoreCase(request.username()).isPresent()) {
            return "Username already exists!";
        }

        Volunteer volunteer = new Volunteer();
        volunteer.setUsername(request.username());
        volunteer.setPassword(encoder.encode(request.password()));
        volunteer.setRole("volunteer"); // everyone who signs up is a volunteer; coordinators are bootstrapped separately
        repo.save(volunteer);
        return "Signup Successful";
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody AuthRequest request) {

        Volunteer user = repo.findByUsernameIgnoreCase(request.username())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!encoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("Wrong password");
        }

        String token = jwtService.generateToken(user.getUsername());
        return new AuthResponse(token, user.getUsername(), user.getRole());
    }

    @GetMapping("/volunteers")
    public List<Volunteer> getVolunteers() {
        return repo.findByRoleIgnoreCase("volunteer");
    }
}

