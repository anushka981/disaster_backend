package com.anushka.disaster_backend;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface DisasterRepository extends MongoRepository<DisasterReport, String> {
}