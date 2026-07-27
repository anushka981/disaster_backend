package com.anushka.disaster_backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.List;

@RestController
@CrossOrigin(origins = {
        "http://127.0.0.1:5500",
        "http://localhost:5500"
})
public class AuthController {

    @Autowired
    private VolunteerReepository repo;
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();


    @PostMapping("/signup")

    public String signup(@RequestBody Volunteer volunteer) {

        if (repo.findByUsername(volunteer.getUsername()) != null) {
            return "Username already exists!";
        }

        volunteer.setPassword(encoder.encode(volunteer.getPassword()));
        repo.save(volunteer);
        return "Signup Successful";
    }

    @PostMapping("/login")
    public String login(@RequestBody Volunteer volunteer) {

        Volunteer user = repo.findByUsername(volunteer.getUsername());

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
        return repo.findByRole("volunteer");
    }
}