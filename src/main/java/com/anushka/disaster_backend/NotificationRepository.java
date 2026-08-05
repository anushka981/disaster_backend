package com.anushka.disaster_backend;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NotificationRepository extends MongoRepository<Notification, String> {

    List<Notification> findByReceiverOrderByCreatedAtDesc(String receiver);

    List<Notification> findByReceiverAndReadFalse(String receiver);

    List<Notification> findByRoleOrderByCreatedAtDesc(String role);

    List<Notification> findByRoleAndReadFalse(String role);
}