package com.anushka.disaster_backend;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DisasterRepository extends MongoRepository<DisasterReport, String> {
    List<DisasterReport> findByVolunteer(String volunteer);
    List<DisasterReport> findByLocationContainingIgnoreCase(String location);
    List<DisasterReport> findByDisasterTypeContainingIgnoreCase(String disasterType);
}
