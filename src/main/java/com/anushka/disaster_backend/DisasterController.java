package com.anushka.disaster_backend;

import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.validation.annotation.Validated;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@RestController
@Validated
public class DisasterController {

    private static final Set<String> REPORT_STATUSES =
            Set.of("PENDING", "ASSIGNED", "IN_PROGRESS", "RESOLVED", "CLOSED");

    private final DisasterRepository reports;
    private final HelpRequestRepository helpRequests;
    private final VolunteerRepository volunteers;
    private final FileStorageService files;
    private final NotificationRepository notifications;


    public DisasterController(
            DisasterRepository reports,
            HelpRequestRepository helpRequests,
            VolunteerRepository volunteers,
            FileStorageService files,
            NotificationRepository notifications) {

        this.reports = reports;
        this.helpRequests = helpRequests;
        this.volunteers = volunteers;
        this.files = files;
        this.notifications = notifications;
    }


    @GetMapping("/hello")
    public String sayHello() {
        return "Backend is working";
    }


    @PostMapping("/report")
    public ResponseEntity<DisasterReport> submitReport(
            @RequestParam @NotBlank String disasterType,
            @RequestParam @NotBlank String location,
            @RequestParam @NotBlank String description,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(value = "image", required = false) MultipartFile image
    ) throws IOException {


        DisasterReport report = new DisasterReport();

        report.setDisasterType(disasterType.trim());
        report.setLocation(location.trim());
        report.setDescription(description.trim());
        report.setLatitude(latitude);
        report.setLongitude(longitude);

        report.setStatus("PENDING");
        report.setVolunteer("");

        report.setImageUrl(image == null ? null : files.store(image));

        report.setCreatedAt(Instant.now());
        report.setUpdatedAt(Instant.now());


        DisasterReport savedReport = reports.save(report);


        Notification notification = new Notification();

        // 🔧 FIX: "ADMIN" hardcoded value hataya, ab role-based broadcast hai
        notification.setReceiver(null);
        notification.setRole("COORDINATOR");
        notification.setType("NEW_REPORT");
        notification.setMessage(
                "New disaster report received: " + disasterType
        );
        notification.setCreatedAt(Instant.now());
        notifications.save(notification);


        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedReport);
    }



    @GetMapping("/reports")
    public List<DisasterReport> getAllReports() {
        return reports.findAllByOrderByCreatedAtDesc();
    }



    @GetMapping("/report/{id}")
    public ResponseEntity<DisasterReport> getReport(
            @PathVariable String id) {

        return reports.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }



    @GetMapping("/reports/assigned/{name}")
    public List<DisasterReport> getAssignedReports(
            @PathVariable String name) {

        return reports.findByVolunteer(name);
    }



    @GetMapping("/report/search/location")
    public List<DisasterReport> searchByLocation(
            @RequestParam @NotBlank String location) {

        return reports.findByLocationContainingIgnoreCase(
                location.trim()
        );
    }



    @GetMapping("/report/search/type")
    public List<DisasterReport> searchByType(
            @RequestParam @NotBlank String disasterType) {

        return reports.findByDisasterTypeContainingIgnoreCase(
                disasterType.trim()
        );
    }




    @PutMapping("/report/{id}/status/{status}")
    public ResponseEntity<DisasterReport> updateStatus(
            @PathVariable String id,
            @PathVariable String status,
            Authentication authentication) {


        String normalized =
                status.trim().toUpperCase(Locale.ROOT);


        if (!REPORT_STATUSES.contains(normalized)) {
            throw new IllegalArgumentException(
                    "Unsupported report status"
            );
        }


        DisasterReport report =
                reports.findById(id).orElse(null);


        if(report == null)
            return ResponseEntity.notFound().build();



        if(authentication != null){

            boolean isCoordinator =
                    authentication.getAuthorities()
                            .stream()
                            .anyMatch(a ->
                                    a.getAuthority()
                                            .equals("ROLE_COORDINATOR"));


            boolean isAssignedVolunteer =
                    authentication.getName()
                            .equalsIgnoreCase(
                                    report.getVolunteer()
                            );


            if(!isCoordinator && !isAssignedVolunteer){

                throw new AccessDeniedException(
                        "Not allowed"
                );

            }

        }



        if(normalized.equals("IN_PROGRESS")){

            report.setStartedAt(
                    Instant.now()
            );
        }



        if(normalized.equals("RESOLVED")){

            report.setResolvedAt(
                    Instant.now()
            );


            if(report.getStartedAt()!=null){

                long minutes =
                        java.time.Duration
                                .between(
                                        report.getStartedAt(),
                                        Instant.now()
                                )
                                .toMinutes();


                report.setTimeTaken(
                        minutes + " minutes"
                );
            }
        }



        report.setStatus(normalized);
        report.setUpdatedAt(Instant.now());


        DisasterReport updated =
                reports.save(report);



        Notification notification =
                new Notification();


        notification.setType(
                "STATUS_UPDATE"
        );


        notification.setMessage(
                "Report status changed to "
                        + normalized
        );


        notification.setCreatedAt(
                Instant.now()
        );


        if(report.getVolunteer()!=null &&
                !report.getVolunteer().isBlank()){

            notification.setReceiver(
                    report.getVolunteer()
            );

            notification.setRole(
                    "VOLUNTEER"
            );

            notifications.save(notification);
        }



        Notification adminNotification =
                new Notification();


        // 🔧 FIX: "ADMIN" hardcoded value hataya, ab role-based broadcast hai
        adminNotification.setReceiver(null);
        adminNotification.setRole("COORDINATOR");

        adminNotification.setType(
                "STATUS_UPDATE"
        );

        adminNotification.setMessage(
                "Report updated: "
                        + normalized
        );

        adminNotification.setCreatedAt(
                Instant.now()
        );


        notifications.save(adminNotification);



        return ResponseEntity.ok(updated);
    }




    @PutMapping("/report/{id}/volunteer/{username}")
    public ResponseEntity<DisasterReport> assignVolunteer(
            @PathVariable String id,
            @PathVariable String username) {


        Volunteer volunteer =
                volunteers.findByUsernameIgnoreCase(username)
                        .orElse(null);


        if(volunteer == null ||
                !"VOLUNTEER".equalsIgnoreCase(
                        volunteer.getRole())){

            throw new IllegalArgumentException(
                    "Volunteer does not exist"
            );
        }



        return reports.findById(id)
                .map(report -> {


                    report.setVolunteer(
                            volunteer.getUsername()
                    );

                    report.setStatus(
                            "ASSIGNED"
                    );


                    report.setAssignedAt(
                            Instant.now()
                    );


                    report.setUpdatedAt(
                            Instant.now()
                    );


                    DisasterReport saved =
                            reports.save(report);



                    Notification notification =
                            new Notification();


                    notification.setReceiver(
                            volunteer.getUsername()
                    );

                    notification.setRole(
                            "VOLUNTEER"
                    );

                    notification.setType(
                            "ASSIGNED"
                    );

                    notification.setMessage(
                            "New disaster report assigned to you"
                    );

                    notification.setCreatedAt(
                            Instant.now()
                    );


                    notifications.save(notification);



                    return ResponseEntity.ok(saved);


                })
                .orElseGet(() ->
                        ResponseEntity.notFound().build()
                );
    }




    // 🔧 FIX: ab role-aware hai — coordinator ko role-based notifications milte hain,
    // volunteer ko unke apne username wale (receiver-based)
    @GetMapping("/notifications/{username}")
    public List<Notification> getNotifications(
            @PathVariable String username,
            Authentication authentication){

        boolean isCoordinator = authentication != null &&
                authentication.getAuthorities()
                        .stream()
                        .anyMatch(a ->
                                a.getAuthority().equals("ROLE_COORDINATOR"));

        if (isCoordinator) {
            return notifications.findByRoleOrderByCreatedAtDesc("COORDINATOR");
        }

        return notifications
                .findByReceiverOrderByCreatedAtDesc(
                        username
                );
    }



    @DeleteMapping("/report/{id}/delete")
    public ResponseEntity<Void> deleteReport(
            @PathVariable String id) {

        if(!reports.existsById(id))
            return ResponseEntity.notFound().build();

        reports.deleteById(id);

        return ResponseEntity.noContent().build();
    }



    @PostMapping("/help")
    public ResponseEntity<HelpRequest> submitHelpRequest(
            @RequestBody HelpRequest helpRequest){


        if(blank(helpRequest.getHelpType()) ||
                blank(helpRequest.getLocation()) ||
                blank(helpRequest.getDescription())){

            throw new IllegalArgumentException(
                    "Required fields missing"
            );
        }


        helpRequest.setStatus("OPEN");
        helpRequest.setCreatedAt(
                Instant.now()
        );


        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        helpRequests.save(helpRequest)
                );
    }



    @GetMapping("/help")
    public List<HelpRequest> getAllHelpRequests(){

        return helpRequests.findAll();
    }



    @DeleteMapping("/help/{id}/delete")
    public ResponseEntity<Void> deleteHelpRequest(
            @PathVariable String id){

        if(!helpRequests.existsById(id))
            return ResponseEntity.notFound().build();


        helpRequests.deleteById(id);

        return ResponseEntity.noContent().build();
    }



    private boolean blank(String value){
        return value == null || value.isBlank();
    }
}