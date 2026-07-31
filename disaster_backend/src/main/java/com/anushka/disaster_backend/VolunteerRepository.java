package com.anushka.disaster_backend;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface VolunteerRepository extends MongoRepository<Volunteer, String> {
    Optional<Volunteer> findByUsernameIgnoreCase(String username);
    List<Volunteer> findByRoleIgnoreCase(String role);
}
