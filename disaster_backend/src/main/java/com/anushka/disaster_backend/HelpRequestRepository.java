package com.anushka.disaster_backend;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface HelpRequestRepository extends MongoRepository<HelpRequest, String> {
}