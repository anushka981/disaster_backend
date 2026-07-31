package com.anushka.disaster_backend;

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

    public AuthController(VolunteerRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    @PostMapping("/signup")
    public String signup(@RequestBody Volunteer volunteer) {

        if (repo.findByUsernameIgnoreCase(volunteer.getUsername()).isPresent()) {
            return "Username already exists!";
        }

        volunteer.setPassword(encoder.encode(volunteer.getPassword()));
        repo.save(volunteer);
        return "Signup Successful";
    }

    @PostMapping("/login")
    public String login(@RequestBody Volunteer volunteer) {

        Volunteer user = repo.findByUsernameIgnoreCase(volunteer.getUsername())
                .orElse(null);

        if (user == null) {
            return "User Not Found";
        }

        if (!encoder.matches(volunteer.getPassword(), user.getPassword())) {
            return "Wrong Password";
        }

        return user.getRole();
    }

    @GetMapping("/volunteers")
    public List<Volunteer> getVolunteers() {
        return repo.findByRoleIgnoreCase("volunteer");
    }
}
