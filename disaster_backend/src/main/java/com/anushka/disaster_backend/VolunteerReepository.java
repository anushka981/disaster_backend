package com.anushka.disaster_backend;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface VolunteerReepository extends MongoRepository<Volunteer, String> {

    Volunteer findByUsername(String username);

    List<Volunteer> findByRole(String role);
}
