package com.anushka.disaster_backend;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * On startup, creates a single "coordinator" account from environment
 * variables if BOOTSTRAP_COORDINATOR_USERNAME / BOOTSTRAP_COORDINATOR_PASSWORD
 * are set and no such user already exists. Everyone who signs up through
 * /signup always gets the "volunteer" role — this is the only way to get
 * a coordinator into the system.
 */
@Component
public class CoordinatorBootstrapRunner implements CommandLineRunner {

    private final VolunteerRepository repo;
    private final PasswordEncoder encoder;
    private final String bootstrapUsername;
    private final String bootstrapPassword;

    public CoordinatorBootstrapRunner(VolunteerRepository repo, PasswordEncoder encoder,
                                      @Value("${app.bootstrap.coordinator.username}") String bootstrapUsername,
                                      @Value("${app.bootstrap.coordinator.password}") String bootstrapPassword) {
        this.repo = repo;
        this.encoder = encoder;
        this.bootstrapUsername = bootstrapUsername;
        this.bootstrapPassword = bootstrapPassword;
    }

    @Override
    public void run(String... args) {
        if (bootstrapUsername == null || bootstrapUsername.isBlank()
                || bootstrapPassword == null || bootstrapPassword.isBlank()) {
            return;
        }
        if (repo.findByUsernameIgnoreCase(bootstrapUsername).isPresent()) {
            return;
        }
        Volunteer coordinator = new Volunteer();
        coordinator.setUsername(bootstrapUsername);
        coordinator.setPassword(encoder.encode(bootstrapPassword));
        coordinator.setRole("coordinator");
        repo.save(coordinator);
        System.out.println("Bootstrap coordinator account created: " + bootstrapUsername);
    }
}
